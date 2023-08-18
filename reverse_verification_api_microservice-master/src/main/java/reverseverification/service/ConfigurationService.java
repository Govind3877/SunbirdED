package reverseverification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reverseverification.domain.Configuration;



/**
 * fileName    	:: ConfigurationService.java
 * @author 	   	:: ravi
 * description 	:: This class is used to get environment variables
 * @version     :: RVTool-3.0.0
 * 
 * 
 */
class ConfigurationService {
	private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);
	private static Configuration configParam;

	// getting environment variables
	protected static Configuration getConfiguration() {
		try {
			if (configParam == null) {	
				configParam = new Configuration();
				configParam.setBaseUrl(System.getenv("sb_base_url"));
				configParam.setContentReadAPI(System.getenv("sb_content_read_api"));
				configParam.setOrgReadAPI(System.getenv("sb_org_read_api"));
				configParam.setDialCodeSearchAPI(System.getenv("sb_dialCode_search_api"));
				configParam.setAPIToken(System.getenv("sb_api_token"));

				configParam.setDBUrl(System.getenv("qr_db_url"));
				configParam.setDBUser(System.getenv("qr_db_user"));
				configParam.setDBPwd(System.getenv("qr_db_pwd"));
				configParam.setDBSchema(System.getenv("qr_db_schema"));
			}
		} catch (Exception e) {
			log.error("Error reading environement variables", e);
		}
		return configParam;
	}

	}
