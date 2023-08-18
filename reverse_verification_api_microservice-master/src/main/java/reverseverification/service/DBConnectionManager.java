package reverseverification.service;

/**
 * fileName    	:: DBConnectionManager.java
 * @author 	   	:: ravi
 * description 	:: This class is used to create DB connection
 * @version     :: RVTool-API-3.0.0
 * 
 * 
 */
//import java.nio.file.Path;
//import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DBConnectionManager {
	private static final Logger log = LoggerFactory.getLogger(DBConnectionManager.class);

	protected static Connection getConnection() {
		Connection dbConnection = null;
		try {
			String host = ConfigurationService.getConfiguration().getDBUrl();
			String user = ConfigurationService.getConfiguration().getDBUser();
			String pass = ConfigurationService.getConfiguration().getDBPwd();
			String schema = ConfigurationService.getConfiguration().getDBSchema();
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			log.info(host + "/" + schema + "?useUnicode=yes&characterEncoding=UTF-8"+user+"::"+pass);
			dbConnection = DriverManager
					.getConnection(host + "/" + schema + "?useUnicode=yes&characterEncoding=UTF-8", user, pass);
			dbConnection.prepareStatement("USE " + schema).executeQuery();
		} catch (Exception e) {
			log.error("Error creating database connection", e);
		}
		return dbConnection;
	}
}
