package reverseverification.service;

/**
 * fileName    	:: PacketService.java
 * @author 	   	:: ravi
 * description 	:: This class is used to fetch all the packets from db for given tenant
 * @version     :: RVTool-API-3.0.0
 * 
 * 
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reverseverification.domain.PacketNames;

public class PacketService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public PacketNames getPackets(String tenant) {
		PacketNames packetNames = new PacketNames();

		try (Connection dbConnection = DBConnectionManager.getConnection();) {
			ArrayList<String> packetNameList = new ArrayList<>();
			ResultSet result;
			String query = "SELECT PKT_NAME FROM QR_PACKET WHERE CREATED_FOR = ?;";

			PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
			preparedStatement.setString(1, tenant);
			result = preparedStatement.executeQuery();

			if (!result.isBeforeFirst()) {
				packetNames.setErrmsg("No packet is found");
				log.info("No packet is found for tenant: "+tenant);
			}
			else {
			while (result.next()) {
				packetNameList.add(result.getString("PKT_NAME"));
			}
			packetNames.setPacketNameList(packetNameList);
			log.info("Packet Name list is successfully accessed for tenant: "+tenant);
			}
		} catch (SQLException e) {
			log.error("Error in fetching packet names for tenant:"+ tenant, e);
		}
		return packetNames;

	}
}
