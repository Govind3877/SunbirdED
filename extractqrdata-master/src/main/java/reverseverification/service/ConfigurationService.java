package reverseverification.service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reverseverification.domain.Configuration;
import reverseverification.domain.Settings;

/**
 * fileName :: ConfigurationService.java
 * 
 * @author :: ravi description :: This class is used to get configuration and
 *         settings from JSON files
 * @version :: RVTool-3.0.0
 * 
 * 
 */
class ConfigurationService {
	private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);
	private static Configuration configParam;
	
	protected static Configuration getConfiguration() {
		return configParam;
	}

	protected static void setConfiguration(String config[]) {
		if (configParam == null) {
			configParam = new Configuration();
			configParam.setDpi(Integer.parseInt(config[0]));
			configParam.setThreadpoolsize(Integer.parseInt(config[1]));
			configParam.setDikshacode(config[2]);
			configParam.setRequesturl(config[3]);
			configParam.setOpenReportWindow(config[4].equals("true")?true:false);
		}
	}

	// used to receive data from packets.json
	protected static boolean findPacketNames(List<String> folderNameList) {
		List<String> packetNameList = null;
		try {
			packetNameList = Arrays.asList(new ObjectMapper().readValue(new File("./packets.json"), String[].class));
		} catch (JsonParseException jsonParseException) {
			log.error("Error in parsing packets.json file", jsonParseException);
			RVTool.exit("json_parse_ex");
		} catch (JsonMappingException jsonMappingException) {
			log.error("Error in mapping packets.json file", jsonMappingException);
			System.out.println("Incorrect syntax in packet.json file");
			RVTool.exit("json_map_ex");
		} catch (IOException ioException) {
			log.error("IO error while parsing packets.json file", ioException);
			RVTool.exit("io_ex");
		}
		Collections.sort(packetNameList);
		Collections.sort(folderNameList);
		if (packetNameList.containsAll(folderNameList)) {
			return true;
		} else {
			folderNameList.removeAll(packetNameList);
			System.out.println(folderNameList + " book(s) are not found in packets.json, please specify");
			log.info(folderNameList + " book(s) are not found");
			RVTool.exit("missing_value_packets");
			return false;
		}

	}

	// used to receive data from settings.json
	protected static Settings getSettings() {
		Settings settingParam = null;
		try {
			settingParam = new ObjectMapper().readValue(new File("./settings.json"), Settings.class);
		} catch (JsonParseException jsonParseException) {
			log.error("Error in parsing settings.json file", jsonParseException);
			System.out.println("Error in settings.json file, for more details please see logs" + "\n"
					+ "Please correct and try again!");
			RVTool.exit("json_parse_ex");
		} catch (JsonMappingException jsonMappingException) {
			log.error("Error in mapping settings.json file", jsonMappingException);
			System.out.println("Error in settings.json file, for more details please see logs" + "\n"
					+ "Please correct and try again!");
			RVTool.exit("json_map_ex");
		} catch (IOException ioException) {
			log.error("I/O error in parsing settings.json file", ioException);
			System.out.println("Error in settings.json file, for more details please see logs" + "\n"
					+ "Please correct and try again!");
			RVTool.exit("io_ex");
		}
		if (settingParam != null && isSettingsInputOk(settingParam)) {
			return settingParam;
		}
		return null;
	}

	// validating settings.json
	private static boolean isSettingsInputOk(Settings settings) {
		if (StringUtils.isBlank(settings.getBooksDir())) {
			System.out.println("Please specify booksdir path and run the tool again!");
			log.warn("Please specify booksdir path and run the tool again!");
			RVTool.exit("missing_value_settings");
			return false;
		}

		if (StringUtils.isBlank(settings.getMaxBookLimit())) {
			System.out.println("Please specify maxBookLimit and run the tool again!");
			log.warn("Please specify maxBookLimit and run the tool again!");
			RVTool.exit("missing_value_settings");
			return false;
		}

		if (!settings.getMaxBookLimit().trim().matches("-?\\d+")) {
			System.out.println("maxBookLimit must be a number");
			log.warn("maxBookLimit must be a number");
			RVTool.exit("invalid_value");
			return false;
		}

		if (StringUtils.isBlank(settings.getIsReverseVerificationRequired())
				|| !(settings.getIsReverseVerificationRequired().equals("y")
						|| settings.getIsReverseVerificationRequired().equals("n"))) {
			System.out.println("Please specify valid entry (y or n) for reverse verification and run the tool again!");
			log.warn("Please specify valid entry (y or n) for reverse verification and run the tool again!");
			RVTool.exit("invalid_value");
			return false;
		}
		if (settings.getIsReverseVerificationRequired().equals("y")) {
			if (StringUtils.isBlank(settings.getToken())) {
				System.out.println("Please specify valid token for reverse verification and run the tool again!");
				log.warn("Please specify valid token for reverse verification and run the tool again!");
				RVTool.exit("missing_value_settings");
				return false;
			}
		}

		return true;
	}
}
