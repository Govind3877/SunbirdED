package reverseverification.service;

/**
 * fileName    	:: TokenService.java
 * @author 	   	:: ravi
 * description 	:: This class is used for getting and generating tokens
 * @version     :: RVTool-API-3.0.0
 * 
 * 
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reverseverification.domain.Token;

public class TokenService {	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	public boolean isTenantExists(Connection connection, String tenant) throws SQLException {
		System.out.println("Before Prepared Statement");
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT STATE FROM QR_TOKEN WHERE STATE=?;");
		System.out.println("After Prepared Statement");
		preparedStatement.setString(1, tenant);
		return preparedStatement.executeQuery().next();
	}

	public Token generateToken(String tenant) {
		Token token = new Token();
		int result = -1;
		String randomString = null;
		try (Connection dbConnection = DBConnectionManager.getConnection();) {
			if (isTenantExists(dbConnection, tenant)) {
				log.info("Token could not be generated because " + tenant + " alreay exists");
				token.setErrmsg("Tenant already exists");
				token.setStatus("Failure");
				return token;
			}
			System.out.println("Tenant Does not Exists: Creating new token");
			char[] chars = "abcdefghijklmnopqrstuvwxyz123456789".toCharArray();
			StringBuilder stringBuilder = new StringBuilder(10);
			Random random = new Random();
			for (int i = 0; i < 10; i++) {
				char c = chars[random.nextInt(chars.length)];
				stringBuilder.append(c);
			}
			randomString = stringBuilder.toString();
			// insertion query
			PreparedStatement preparedStatement = dbConnection
					.prepareStatement("INSERT INTO QR_TOKEN (STATE,TOKEN) VALUES (?, ?);");
			preparedStatement.setString(1, tenant);
			preparedStatement.setString(2, randomString);
			result = preparedStatement.executeUpdate();

		} catch (Exception e) {
			log.error("Error in generating token for tenant: " + tenant, e);
			token.setErrmsg("Error in generating token");
			token.setStatus("Failure");

		}
		if (result == 1) {
			log.info("Token generated for tenant: " + tenant);
			token.setStatus("Sucess");
			token.setToken(randomString);
		} else {
			log.error("Error in generating token for tenant: " + tenant);
			token.setErrmsg("Error in generating token");
			token.setStatus("Failure");
		}

		return token;

	}

	public Token getToken(String tenant) {			
		Token token = new Token();
		try (Connection dbConnection = DBConnectionManager.getConnection();) {
			ResultSet result;
			PreparedStatement preparedStatement = dbConnection
					.prepareStatement("SELECT TOKEN FROM QR_TOKEN WHERE STATE=?;");
			preparedStatement.setString(1, tenant);
			result = preparedStatement.executeQuery();
			if (!result.isBeforeFirst()) {
				log.info("Token unavailable for: " + tenant);
				token.setErrmsg("Token unavailable for: " + tenant);
				token.setStatus("failure");
			} else {
				while (result.next()) {
					token.setToken(result.getString("TOKEN"));
				}
				token.setStatus("Success");
				log.info("Token for tenant " + tenant + " is accessed");
			}
		} catch (Exception e) {
			log.error("Error in fetching token for tenant: " + tenant, e);
		}
		return token;
	}

	protected static boolean isValidToken(Connection dbConnnection, String token) throws SQLException {
		PreparedStatement preparedStatement = dbConnnection
				.prepareStatement("SELECT STATE FROM QR_TOKEN WHERE TOKEN = ?");
		preparedStatement.setString(1, token);

		return preparedStatement.executeQuery().next();
	}
}
