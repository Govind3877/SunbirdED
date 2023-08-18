package reverseverification.service;

/**
 * fileName    	:: ActivityLoggerService.java
 * @author 	   	:: ravi
 * description 	:: This class is used for activity loggin in db
 * @version     :: RVTool-API-3.0.0
 * 
 * 
 */
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivityLoggerService {
	private static final Logger log = LoggerFactory.getLogger(ActivityLoggerService.class);

	public static void logActivity(String token, String apiRequest, String apiResponse) {		
		try(Connection connection = DBConnectionManager.getConnection();) {
			
			PreparedStatement ps = connection.prepareStatement(
					"INSERT INTO QR_API_LOG(TOKEN,API_REQUEST,API_RESPONSE,LOG_TIME) VALUES(?,?,?,NOW())");
			ps.setString(1, token);
			ps.setString(2, apiRequest);
			ps.setString(3, apiResponse);
			int result = ps.executeUpdate();
			log.info("Activity log result --> " + result);
			log.info("logged activity --> " + token + ":::" + apiRequest + ":::" + apiResponse);
		} catch (Exception e) {
			log.warn("Activity Logging Failed");
			log.warn("logged activity --> " + token + ":::" + apiRequest + ":::" + apiResponse);
			log.warn(e.getMessage());
		}
	}
}
