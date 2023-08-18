package reverseverification.controller;
/**
 * fileName    	:: QRController.java
 * @author 	   	:: ravi
 * description 	:: Controller class
 * @version     :: RVTool-API-3.0.0
 * 
 * 
 */
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import reverseverification.domain.PacketNames;
import reverseverification.domain.TenantData;
import reverseverification.domain.RVResponse;
import reverseverification.domain.RequestParameters;
import reverseverification.domain.Token;
import reverseverification.service.ActivityLoggerService;
import reverseverification.service.PacketService;
import reverseverification.service.RVService;
import reverseverification.service.ReportService;
import reverseverification.service.StringUtils;
import reverseverification.service.TokenService;

@RestController
@EnableAutoConfiguration
@RequestMapping("/api/rv")
public class QRController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@PostMapping(value = "/verify")
	@ResponseBody
	public ResponseEntity<RVResponse> reverseVerify(InputStream inputStream, @RequestHeader HttpHeaders httpHeaders) {
		log.info("Reverse verification API controller is accessed at: " + LocalDateTime.now() + " from MAC: "
				+ httpHeaders.getFirst("mac") + " and Serial: " + httpHeaders.getFirst("serial")
				+ " with token: " + httpHeaders.getFirst("token"));

		RVResponse rvResponse = new RVResponse();		
		String token = null;
		String mac = null;
		String systemSerial = null;

		JSONObject requestObj = null;
		JSONObject inputObj = null;
		JSONObject errorObj = new JSONObject();

		try {
			token = httpHeaders.getFirst("token");
			if (token == null) {
				rvResponse.setErrmsg("Token not found");
				return ResponseEntity.status(200).body(rvResponse);
			}
		} catch (Exception e) {			
			errorObj.put("errmsg", "Token not found");
			rvResponse.setErrmsg("Token not found");
			log.error("Token error: ",e);
			return ResponseEntity.status(200).body(rvResponse);
		}

		try {
			mac = httpHeaders.getFirst("mac");
			if (mac == null) {
				rvResponse.setErrmsg("MAC address is not found");
				return ResponseEntity.status(200).body(rvResponse);
			}

			else if (!mac.contains("|")) {
				if (mac.length() != 17 || getCharOccurrence(mac, '-') != 5) {
					rvResponse.setErrmsg("Invalid MAC address");
					return ResponseEntity.status(200).body(rvResponse);
				}
			}

		} catch (Exception e) {
			rvResponse.setErrmsg("MAC address is not found");
			log.error("MAC error: ",e);
			return ResponseEntity.status(200).body(rvResponse);
		}
		
		try {
			systemSerial = httpHeaders.getFirst("serial");
			if (systemSerial == null) {
				rvResponse.setErrmsg("System serial-manufacturer is not found");
				return ResponseEntity.status(200).body(rvResponse);
			}
		} catch (Exception e) {
			rvResponse.setErrmsg("System serial-manufacturer is not found");
			log.error("System details error: ",e);
			return ResponseEntity.status(200).body(rvResponse);
		}

		String packetName, rvFileName;
		List<String> usedDialList = new ArrayList<String>();
		List<String> fileList = new ArrayList<String>();
		List<String> checksumList = new ArrayList<String>();
		StringBuilder inputBuilder = new StringBuilder();
		
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));) {
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				inputBuilder.append(line);
			}
			inputObj = (JSONObject) new JSONParser().parse(inputBuilder.toString());
			requestObj = (JSONObject) inputObj.get(RequestParameters.request.name());
			packetName = (String) requestObj.get(RequestParameters.packetName.name());
			rvFileName = (String) requestObj.get(RequestParameters.rvFileName.name());			
			if(StringUtils.isBlank(packetName)) {
				rvResponse.setErrmsg("Missing packet name");
				ActivityLoggerService.logActivity(token, inputObj.toJSONString(), errorObj.toJSONString());
				return ResponseEntity.status(200).body(rvResponse);
			}
			if(packetName.startsWith("do_")) {
				packetName = packetName.substring(0,getDoIdIndex(packetName));
			}
			packetName = packetName.toUpperCase();

			JSONArray dialCodeArray = (JSONArray) requestObj.get(RequestParameters.dialList.name());
			JSONArray fileDataArray = (JSONArray) requestObj.get(RequestParameters.fileExecutionData.name());
			JSONArray checksumArray = (JSONArray) requestObj.get(RequestParameters.pdfCheckSum.name());

			if (fileDataArray.size() != checksumArray.size()) {
				if (fileDataArray.size() > checksumArray.size()) {
					rvResponse.setErrmsg("Missing cheksum for " + (fileDataArray.size() - checksumArray.size()) + " file(s)");
				} else {
					rvResponse.setErrmsg("Checksum count is more than file count");
				}
				ActivityLoggerService.logActivity(token, inputObj.toJSONString(), errorObj.toJSONString());
				return ResponseEntity.status(200).body(rvResponse);
			}
			
			for (int i = 0; i < dialCodeArray.size(); i++) {
				usedDialList.add(dialCodeArray.get(i).toString());
			}

			for (int i = 0; i < fileDataArray.size(); i++) {
				fileList.add(fileDataArray.get(i).toString());
				checksumList.add(checksumArray.get(i).toString());
			}
		} catch (Exception e) {
			rvResponse.setErrmsg("Invalid input format");
			ActivityLoggerService.logActivity(token, inputObj.toJSONString(), errorObj.toJSONString());
			return ResponseEntity.status(200).body(rvResponse);
		}

		try {							
			rvResponse = new RVService().verifyPacketWithDials(token,requestObj.toString(), packetName, usedDialList, mac, systemSerial,
					checksumList, fileList, rvFileName);
			if (rvResponse.getErrmsg() != null) {
				log.info("Reverse verification for packet: " + packetName + " from MAC: "
						+ mac+ " and Serial: " + systemSerial+ " with token: " + token+" is unsuccessful with error: "+rvResponse.getErrmsg());
				return ResponseEntity.status(200).body(rvResponse);
			} else {				
				Gson gson = new Gson();
				String json = gson.toJson(rvResponse.getResponse());
				log.info("Reverse verification for packet: " + packetName + " from MAC: "
						+ mac+ " and Serial: " + systemSerial+ " with token: " + token+" is successful \n"+ "Response JSON:"+ json);				
				ActivityLoggerService.logActivity(token, inputObj.toJSONString(), json);
				return ResponseEntity.status(200).body(rvResponse);
			}

		} catch (Exception e) {
			log.error("Error in reverse verification: ",e);
			errorObj.put("errmsg", e.getMessage());
			rvResponse.setErrmsg(e.getMessage());
			ActivityLoggerService.logActivity(token, inputObj.toJSONString(), errorObj.toJSONString());
			return ResponseEntity.status(200).body(rvResponse);
		}

	}

	@GetMapping(value = "/packets/read")
	@ResponseBody
	public ResponseEntity<PacketNames> getPacketName(@RequestParam(name = "tenant", required = true) String tenant) {
		PacketNames packets = null;
		try {			
			packets = new PacketService().getPackets(tenant);
		} catch (Exception e) {
			log.error("Error in reading packet names for tenant: "+tenant,e);
		}

		return ResponseEntity.status(200).body(packets);
	}

	@GetMapping(value = "/token/read")
	@ResponseBody
	public ResponseEntity<Token> getToken(@RequestParam(name = "tenant", required = false) String tenant) {		
		Token token = null;
		try {
			token = new TokenService().getToken(tenant);
		} catch (Exception e) {
			log.error("Error in reading token for tenant: "+tenant, e);
		}
		return ResponseEntity.status(200).body(token);
	}

	@GetMapping(value = "/token/create")
	@ResponseBody
	public ResponseEntity<Token> generateToken(@RequestParam(name = "tenant", required = true) String tenant) {
		Token token = null;
		try {
			token = new TokenService().generateToken(tenant);

		} catch (Exception e) {
			log.error("Error in token generation for tenant: "+tenant, e);
		}
		return ResponseEntity.status(200).body(token);
	}
	
	@GetMapping(value = "/books/read")
	@ResponseBody
	public ResponseEntity<TenantData> getRVBooks(@RequestParam(name = "tenant", required = false) String tenant) {
		TenantData rvBooks = null;
		try {
			rvBooks = new ReportService().getRVBooks(tenant);

		} catch (Exception e) {
			log.error("Error in getting RV books for tenant: "+tenant, e);
		}
		return ResponseEntity.status(200).body(rvBooks);
	}
	
	@GetMapping(value = "/dialcode/read")
	@ResponseBody
	public ResponseEntity<TenantData> getUsedQRCodes(@RequestParam(name = "tenant", required = false) String tenant) {
		TenantData rvBooks = null;
		try {
			rvBooks = new ReportService().getUsedQRCodes(tenant);

		} catch (Exception e) {
			log.error("Error in getting RV books for tenant: "+tenant, e);
		}
		return ResponseEntity.status(200).body(rvBooks);
	}
	
	private int getCharOccurrence(String string, char c) {
		int counter = 0;
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == c) {
				counter++;
			}
		}
		return counter;
	}
	
	private int getDoIdIndex(final CharSequence cs) {		
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return 0;
		}
		for (int i = 3; i < strLen; i++) {			
			if(!Character.isDigit(cs.charAt(i))) {				
				return i;
			}
		}
		
		return 0;
	}
}
