package reverseverification.service;

/**
 * fileName    	:: RVService.java
 * @author 	   	:: ravi
 * description 	:: This class is used to reverse verify books
 * @version     :: RVTool-API-3.0.0
 * 
 * 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import reverseverification.domain.QRPacket;
import reverseverification.domain.RVResponse;
import reverseverification.domain.Response;

public class RVService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public RVResponse verifyPacketWithDials(String token, String request, String packetName, List<String> usedDialList,
			String mac, String systemSerial, List<String> checksumList, List<String> fileList, String rvFileName) {
		log.info("Reverse verifying packet: " + packetName + " from MAC: " + mac + " and Serial: " + systemSerial
				+ " with token: " + token);

		RVResponse rvResponse = new RVResponse();
		JSONObject responseObj = new JSONObject();
		try (Connection dbConnection = DBConnectionManager.getConnection();) {
			DBService dbService = new DBService();

			if (!TokenService.isValidToken(dbConnection, token)) {
				rvResponse.setErrmsg("Invalid token");
				responseObj.put("errmsg", "Invalid token");
				log.warn("Packet: " + packetName + " could not be verified due to invalid token: " + token);
				return rvResponse;
			}

			// getting packet details from QR_PACKET
			QRPacket qrPacket = null;
			int packetCount = 0;
			if (packetName.startsWith("DO")) {
				qrPacket = getQrCodeFromDoId(packetName);
				log.info("qrPacket1::"+qrPacket);
				System.out.println("qrPacket::"+qrPacket);
				
				log.info("qrPacket1.getContentType::"+qrPacket.getContentType());
				System.out.println("qrPacket.getContentType::"+qrPacket.getContentType());
				
				if (!qrPacket.getContentType().equalsIgnoreCase("TextBook")) {
					rvResponse.setErrmsg("please check your packet name:" + packetName.toLowerCase() + " & try again");
					responseObj.put("errmsg",
							"please check your packet name:" + packetName.toLowerCase() + " & try again");
					log.error(packetName + " is not a text book or not found");
					return rvResponse;
				}
				String createdFor = getChannelName(qrPacket.getChannel());
				if (createdFor != null) {
					qrPacket.setCreatedFor(createdFor);
				} else {
					rvResponse.setErrmsg("Could not find channel name for: " + qrPacket.getChannel());
					log.error("Could not find channel name for: " + qrPacket.getChannel());
					return rvResponse;
				}
				if ((packetCount = dbService.getPacketCount(dbConnection, packetName)) < 1) {
					dbService.insertPacket(dbConnection, qrPacket.getPacketName(), qrPacket.getIssuedDials(),
							qrPacket.getIssuedDialCount(), qrPacket.getBookName(), qrPacket.getSubject(),
							qrPacket.getMedium(), qrPacket.getGrade(), qrPacket.getPo(), qrPacket.getAstPO(),
							qrPacket.getCreatedFor(), qrPacket.getCreatedTime());
				}
				if (packetCount == 1) {
					dbService.updatePacket(dbConnection, qrPacket.getPacketName(), qrPacket.getIssuedDials(),
							qrPacket.getIssuedDialCount(), qrPacket.getBookName(), qrPacket.getSubject(),
							qrPacket.getMedium(), qrPacket.getGrade(), qrPacket.getPo(), qrPacket.getAstPO(),
							qrPacket.getCreatedFor(), qrPacket.getCreatedTime());
				}
			} else {

				if ((qrPacket = dbService.getPacketDetails(dbConnection, packetName)) == null) {
					rvResponse.setErrmsg("please check your packet name::" + packetName + " & try again");
					responseObj.put("errmsg", "please check your packet name::" + packetName + " & try again");
					log.info(packetName + " is not available");
					return rvResponse;
				}
			}

			String retrievedDials = "";
			String fileData = "";
			String fileChecksums = "";

			for (String dial : usedDialList) {
				retrievedDials = retrievedDials + dial + "|";
			}
			for (String file : fileList) {
				fileData = fileData + file + "|";
			}

			for (String checksum : checksumList) {
				fileChecksums = fileChecksums + checksum + "|";
			}

			List<String> packetDialList = Arrays.asList(qrPacket.getIssuedDials().split("\\|"));

			Collections.sort(packetDialList);
			Collections.sort(usedDialList);

			String unusedDials = "";
			int unusedDialCounter = 0;
			String additionalDials = "";
			int additionalDialCounter = 0;
			String duplicateDials = "";
			int duplicateDialCounter = 0;

			if (!packetDialList.equals(usedDialList)) {
				List<String> packetExtraDialList = new ArrayList<>(
						CollectionUtils.subtract(packetDialList, usedDialList));
				List<String> usedExtraDialList = new ArrayList<>(
						CollectionUtils.subtract(usedDialList, packetDialList));

				if (packetExtraDialList != null && packetExtraDialList.size() > 0) {
					for (int iCounter = 0; iCounter < packetExtraDialList.size(); iCounter++) {
						unusedDials = unusedDials + packetExtraDialList.get(iCounter) + "|";
						unusedDialCounter++;
					}
					log.info("Packet: " + packetName + " has " + unusedDialCounter + " unused dials: [" + unusedDials
							+ "]");
				}

				if (usedExtraDialList != null && usedExtraDialList.size() > 0) {
					for (int iCounter = 0; iCounter < usedExtraDialList.size(); iCounter++) {
						String dialCode = usedExtraDialList.get(iCounter);
						if (packetDialList.contains(dialCode)) {
							duplicateDials = duplicateDials + dialCode + "|";
							duplicateDialCounter++;
						} else {
							if (additionalDials.contains(dialCode)) {
								duplicateDials = duplicateDials + dialCode + "|";
								duplicateDialCounter++;
							}
							additionalDials = additionalDials + dialCode + "|";
							additionalDialCounter++;
						}
					}
					log.info("Packet: " + packetName + " has" + duplicateDialCounter + " duplicate dials: ["
							+ duplicateDials + "]");
					log.info("Packet: " + packetName + " has" + additionalDialCounter + " additional dials: ["
							+ additionalDials + "]");
				}
			}

			Response qrModel = new Response();
			qrModel.setPacketName(packetName);
			qrModel.setDialCodeCount(qrPacket.getIssuedDialCount());
			qrModel.setUsedCount(usedDialList.size());
			qrModel.setUsedDials(retrievedDials.split("\\|"));
			qrModel.setUnusedCount(unusedDialCounter);
			qrModel.setUnusedDials(unusedDials.split("\\|"));
			qrModel.setAdditionalCount(additionalDialCounter);
			qrModel.setAdditionalDials(additionalDials.split("\\|"));
			qrModel.setDupCount(duplicateDialCounter);
			qrModel.setDupDials(duplicateDials.split("\\|"));

			String additionalUsedFromData = "";
			if (!StringUtils.isBlank(additionalDials)) {
				if (packetName.startsWith("DO")) {
					Set<String> foundDials = new HashSet<>();
					Set<Object> diffChannelDialList = Arrays.stream(additionalDials.split("\\|"))
							.collect(Collectors.toSet());
					HashMap<String, String> bookQrMap = getAdditionalUsedFrom(additionalDials, qrPacket.getChannel());
					for (String id : bookQrMap.keySet()) {
						additionalUsedFromData = additionalUsedFromData + id + "=" + bookQrMap.get(id) + ";";
						for (String bookDial : bookQrMap.get(id).split(",")) {
							foundDials.add(bookDial);
						}
					}
					diffChannelDialList.removeAll(foundDials);

					if (diffChannelDialList.size() > 0) {
						additionalUsedFromData = additionalUsedFromData + "Does not belong to this tenant="
								+ diffChannelDialList + ";";
					}

					//
				} else {
					List<String> additionalUsedPktList = new ArrayList<String>();
					Map<String, String> additionalUsedPacketMap = new HashMap<String, String>();
					for (String dialCode : additionalDials.split("\\|")) {
						List<String> additionalUsedPacketList = dbService.getPktNameByDialCode(dbConnection, dialCode);
						if (null != additionalUsedPacketList && additionalUsedPacketList.size() > 0) {
							for (String pktName : additionalUsedPacketList) {
								if (additionalUsedPktList.contains(pktName)) {
									String temp = additionalUsedPacketMap.get(pktName);
									additionalUsedPacketMap.put(pktName, temp + dialCode + ",");
								} else {
									additionalUsedPktList.add(pktName);
									additionalUsedPacketMap.put(pktName, dialCode + ",");
								}
							}
						}
					}
					if (null != additionalUsedPktList && additionalUsedPktList.size() > 0) {
						for (String pktName : additionalUsedPktList) {
							additionalUsedFromData = additionalUsedFromData + pktName + "="
									+ additionalUsedPacketMap.get(pktName) + ";";
						}
					}
				}
				log.info("Packet: " + packetName + " has additional used from data: [" + additionalUsedFromData + "]");
			}
			qrModel.setAdditionalUsedFrom(additionalUsedFromData);

			if (packetName.startsWith("DO") && packetCount == 1) {
				qrPacket = dbService.getPacketDetails(dbConnection, packetName);

			}
			// Make QR_Packet History entry
			dbService.makePacketHistoryEntry(dbConnection, qrPacket);

			dbService.updateReverseVerificationDetails(dbConnection, packetName, usedDialList.size(), retrievedDials,
					unusedDialCounter, unusedDials, additionalDialCounter, additionalDials, additionalUsedFromData,
					duplicateDialCounter, duplicateDials, 0, "", request, mac, systemSerial,
					qrPacket.getNoOfTimeVerified() + 1, rvFileName);

			// packet file entry
			for (int i = 0; i < fileList.size(); i++) {
				String fileName = fileList.get(i).substring(0, fileList.get(i).indexOf(":"));
				int fileSize = Integer.parseInt(
						fileList.get(i).substring(fileList.get(i).indexOf(":") + 1, fileList.get(i).lastIndexOf(":")));
				int executionTime = Integer.parseInt(fileList.get(i).substring(fileList.get(i).lastIndexOf(":") + 1));
				dbService.MakeFileEntry(dbConnection, packetName, qrPacket.getNoOfTimeVerified() + 1, fileName,
						fileSize, checksumList.get(i), executionTime);
			}

			ObjectMapper mapper = new ObjectMapper();
			String response = mapper.writeValueAsString(qrModel);
			rvResponse.setResponse(qrModel);
			responseObj.put("response", new JSONParser().parse(response));

		} catch (Exception e) {
			rvResponse.setErrmsg(e.getMessage());
			responseObj.put("errmsg", e.getMessage());
			log.error("Error occurred during reverse verification", e);
		}
		return rvResponse;
	}

	private QRPacket getQrCodeFromDoId(String packetName) {
		QRPacket qrPacket = new QRPacket();
		String url = ConfigurationService.getConfiguration().getBaseUrl()+ "/"
				+ ConfigurationService.getConfiguration().getContentReadAPI() + "/" + packetName.toLowerCase();
		
		log.info("ConfigurationService.getConfiguration().getBaseUrl()::"+ConfigurationService.getConfiguration().getBaseUrl());
		log.info("ConfigurationService.getConfiguration().getContentReadAPI()::"+ConfigurationService.getConfiguration().getContentReadAPI());
		log.info("packetName.toLowerCase()::"+packetName.toLowerCase());
		
		System.out.println("ConfigurationService.getConfiguration().getBaseUrl()::"+ConfigurationService.getConfiguration().getBaseUrl());
		System.out.println("ConfigurationService.getConfiguration().getContentReadAPI()::"+ConfigurationService.getConfiguration().getContentReadAPI());
		
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet getRequest = new HttpGet(url);
			getRequest.addHeader("Content-Type", "application/json");
			getRequest.addHeader("Authorization", "Bearer " + ConfigurationService.getConfiguration().getAPIToken());
			HttpResponse httpResponse = httpClient.execute(getRequest);

			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader((httpResponse.getEntity().getContent())));
			String dialcodes = "";
			String output;
			ArrayList<String> reservedDialcodes = new ArrayList<>();
			while ((output = bufferedReader.readLine()) != null) {
				Gson gson = new Gson();
				JsonElement jsonElement = gson.fromJson(output, JsonElement.class);
				JsonObject jsonObj = jsonElement.getAsJsonObject();

				if (httpResponse.getStatusLine().getStatusCode() != 200) {
					log.error("Content read API returned status code: " + jsonObj.get("responseCode").getAsString());
					qrPacket.setContentType(jsonObj.get("responseCode").getAsString());
					return qrPacket;
				}

				JsonObject dialCodes = jsonObj.get("result").getAsJsonObject().get("content").getAsJsonObject()
						.get("reservedDialcodes").getAsJsonObject();
				Set<Map.Entry<String, JsonElement>> entries = dialCodes.entrySet();// will return members of your object

				for (Map.Entry<String, JsonElement> entry : entries)
					reservedDialcodes.add(entry.getKey());

				for (String dialcode : reservedDialcodes)
					dialcodes += dialcode + "|";
				log.info("Fetched dial codes from read API for packet:" + packetName + " are: " + dialcodes);

				qrPacket.setPacketName(packetName);
				qrPacket.setIssuedDials(dialcodes);
				qrPacket.setIssuedDialCount(reservedDialcodes.size());
				
				System.out.println("SYSO::"+jsonObj.get("result").getAsJsonObject().get("content").getAsJsonObject());
				
				qrPacket.setBookName(jsonObj.get("result").getAsJsonObject().get("content").getAsJsonObject()
						.get("name").getAsString());				
				//qrPacket.setMedium(jsonObj.get("result").getAsJsonObject().get("content").getAsJsonObject().get("medium").getAsString());
				
				
				JsonArray arrJson = jsonObj.get("result").getAsJsonObject().get("content").getAsJsonObject()
						.get("gradeLevel").getAsJsonArray();
				String gradeLevelArray=null; 
				for(int i = 0; i < arrJson.size(); i++) {
				if(i==0) {
						gradeLevelArray = arrJson.get(i).toString();
					}else {
						gradeLevelArray=gradeLevelArray+"|"+arrJson.get(i).toString();
					}
				}
				
				qrPacket.setGrade(gradeLevelArray);
				
				
				arrJson = jsonObj.get("result").getAsJsonObject().get("content").getAsJsonObject()
						.get("medium").getAsJsonArray();
				String mediumArray=null; 
				for(int i = 0; i < arrJson.size(); i++) {
				if(i==0) {
						mediumArray = arrJson.get(i).toString();
					}else {
						mediumArray=mediumArray+"|"+arrJson.get(i).toString();
					}
				}
				
				qrPacket.setMedium(mediumArray);
				
				arrJson = jsonObj.get("result").getAsJsonObject().get("content").getAsJsonObject()
						.get("subject").getAsJsonArray();
				String subjectArray=null; 
				for(int i = 0; i < arrJson.size(); i++) {
				if(i==0) {
						subjectArray = arrJson.get(i).toString();
					}else {
						subjectArray=subjectArray+"|"+arrJson.get(i).toString();
					}
				}
				
				qrPacket.setSubject(subjectArray);
				
				/*qrPacket.setGrade(jsonObj.get("result").getAsJsonObject().get("content").getAsJsonObject()
						.get("gradeLevel").getAsJsonArray().getAsString());
				qrPacket.setSubject(jsonObj.get("result").getAsJsonObject().get("content").getAsJsonObject()
						.get("subject").getAsString());*/
				qrPacket.setContentType(jsonObj.get("result").getAsJsonObject().get("content").getAsJsonObject()
						.get("contentType").getAsString());
				String dateInString = jsonObj.get("result").getAsJsonObject().get("content").getAsJsonObject()
						.get("createdOn").getAsString();
				qrPacket.setChannel(jsonObj.get("result").getAsJsonObject().get("content").getAsJsonObject()
						.get("channel").getAsString());
				dateInString = dateInString.substring(0, dateInString.indexOf("."));
				String s1 = dateInString.substring(0, dateInString.indexOf("T"));
				String s2 = dateInString.substring(dateInString.indexOf("T") + 1);
				qrPacket.setCreatedTime(s1 + " " + s2);

			}
		} catch (Exception e) {
			log.error("Error in getting content detail from read API", e);
		}

		return qrPacket;

	}

	private HashMap<String, String> getAdditionalUsedFrom(String additionalDials, String Channel) {
		String url = ConfigurationService.getConfiguration().getBaseUrl()+"/"
				+ ConfigurationService.getConfiguration().getDialCodeSearchAPI();

		HashMap<String, String> bookDialMap = new HashMap<>();
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Content-Type", "application/json");
		httpPost.addHeader("X-Channel-ID", Channel);
		httpPost.addHeader("Authorization", "Bearer " + ConfigurationService.getConfiguration().getAPIToken());

		JsonObject requestJSON = new JsonObject();
		JsonObject request = new JsonObject();
		JsonObject search = new JsonObject();

		JsonArray reservedDialCode = new JsonArray();
		for (String additionalDial : additionalDials.split("\\|"))
			reservedDialCode.add(additionalDial);

		log.info("reserved dials for verification---" + reservedDialCode);
		search.add("identifier", reservedDialCode);

		request.add("search", search);
		requestJSON.add("request", request);

		StringEntity stringEntity = new StringEntity(requestJSON.toString(), "UTF8");
		stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		httpPost.setEntity(stringEntity);
		try {
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() != 200) {
				log.error("Dial code search API returned status code: " + httpResponse.getStatusLine().getStatusCode());
			}
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader((httpResponse.getEntity().getContent())));
			String output;
			while ((output = bufferedReader.readLine()) != null) {
				Gson gson = new Gson();
				JsonElement jsonElement = gson.fromJson(output, JsonElement.class);
				JsonObject jsonObj = jsonElement.getAsJsonObject();

				if (jsonObj.get("result").getAsJsonObject().get("count").getAsInt() > 0) {
					JsonArray dialJSONArray = jsonObj.get("result").getAsJsonObject().get("dialcodes").getAsJsonArray();
					String bookId = null;
					String dialcode = null;
					for (JsonElement content : dialJSONArray) {
						bookId = content.getAsJsonObject().get("batchcode").getAsString();
						dialcode = content.getAsJsonObject().get("identifier").getAsString();
						if (bookDialMap.containsKey(bookId)) {
							bookDialMap.put(bookId, bookDialMap.get(bookId) + "," + dialcode);
						} else {
							bookDialMap.put(bookId, dialcode);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("Error in fetching additonal used from data", e);
		}
		return bookDialMap;

	}

	private String getChannelName(String orgId) {
		String url = ConfigurationService.getConfiguration().getBaseUrl()+"/"
				+ ConfigurationService.getConfiguration().getOrgReadAPI();
		String channelName = null;
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost httpPost = new HttpPost(url);

			httpPost.addHeader("Content-Type", "application/json");
			httpPost.addHeader("Authorization", "Bearer " + ConfigurationService.getConfiguration().getAPIToken());

			JsonObject requestJSON = new JsonObject();
			JsonObject request = new JsonObject();

			request.add("organisationId", new Gson().fromJson(orgId, JsonElement.class));
			requestJSON.add("request", request);

			StringEntity stringEntity = new StringEntity(requestJSON.toString(), "UTF8");
			stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			httpPost.setEntity(stringEntity);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() != 200) {
				log.error("Org read API returned status code: " + httpResponse.getStatusLine().getStatusCode());
				return channelName;
			}
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader((httpResponse.getEntity().getContent())));
			String output;
			while ((output = bufferedReader.readLine()) != null) {
				Gson gson = new Gson();
				JsonElement jsonElement = gson.fromJson(output, JsonElement.class);
				JsonObject jsonObj = jsonElement.getAsJsonObject();
				channelName = jsonObj.get("result").getAsJsonObject().get("response").getAsJsonObject().get("channel")
						.getAsString().toString();
			}

		} catch (Exception e) {
			log.error("Error in fetching channel for packet: " + orgId, e);
		}
		return channelName;
	}

}