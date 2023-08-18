package reverseverification.service;

/**
 * fileName    	:: DBService.java
 * @author 	   	:: ravi
 * description 	:: This class is used for db services
 * @version     :: RVTool-API-3.0.0
 * 
 * 
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reverseverification.domain.QRPacket;

class DBService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	protected void insertPacket(Connection dbConnection, String packetName, String issuedDials, int issuedDialCount,
			String bookName, String subject, String medium, String grade, String po, String astPO, String createdFor,
			String createdTime) throws SQLException {
		PreparedStatement preparedStatement = dbConnection.prepareStatement(
				"INSERT INTO QR_PACKET(PKT_NAME, DIAL_LIST, COUNT, BOOK_NAME, SUBJECT, MEDIUM, GRADE, PO, AST_PO, CREATED_FOR, R_CRE_TIME, PKT_SENT_FLG) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'N')");
		preparedStatement.setString(1, packetName);
		preparedStatement.setString(2, issuedDials);
		preparedStatement.setInt(3, issuedDialCount);
		preparedStatement.setString(4, bookName);
		preparedStatement.setString(5, subject);
		preparedStatement.setString(6, medium);
		preparedStatement.setString(7, grade);
		preparedStatement.setString(8, po);
		preparedStatement.setString(9, astPO);
		preparedStatement.setString(10, createdFor);
		preparedStatement.setString(11, createdTime);
		int result = preparedStatement.executeUpdate();

		log.info("Packet: " + packetName + " is inserted with result:  " + result);
	}

	protected void makePacketHistoryEntry(Connection dbConnection, QRPacket qrPacket) throws SQLException {
		PreparedStatement preparedStatement = dbConnection.prepareStatement("INSERT INTO QR_PACKET_HISTORY "
				+ "(PKT_NAME, DIAL_LIST, COUNT, CREATED_FOR, BOOK_NAME, MEDIUM, GRADE, SUBJECT, PO, AST_PO, "
				+ "R_CRE_TIME, PKT_SENT_ON, PKT_SENT_TO, PKT_SENT_FLG, REVERSE_VERIFIED_FLG, USED_COUNT, USED_CODES, "
				+ "UNUSED_COUNT, UNUSED_CODES, ADDITIONAL_USED_COUNT, ADDITIONAL_USED_CODES, ADDITIONAL_USED_FROM, DUP_COUNT, "
				+ "DUP_CODES, EXT_COUNT, EXT_CODES, API_REQUEST, REVERSE_VERIFIED_ON, BACKED_ON,MAC_ADDRESS,SYSTEM_SERIAL,NO_OF_TIME_VERIFIED, VERIFIED_CSV_NAME ) "
				+ "VALUES (?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(),?,?,?,?)");
		preparedStatement.setString(1, qrPacket.getPacketName());
		preparedStatement.setString(2, qrPacket.getIssuedDials());
		preparedStatement.setInt(3, qrPacket.getIssuedDialCount());
		preparedStatement.setString(4, qrPacket.getCreatedFor());
		preparedStatement.setString(5, qrPacket.getBookName());
		preparedStatement.setString(6, qrPacket.getMedium());
		preparedStatement.setString(7, qrPacket.getGrade());
		preparedStatement.setString(8, qrPacket.getSubject());
		preparedStatement.setString(9, qrPacket.getPo());
		preparedStatement.setString(10, qrPacket.getAstPO());
		preparedStatement.setString(11, qrPacket.getCreatedTime());
		preparedStatement.setDate(12, qrPacket.getPacketSentOn());
		preparedStatement.setString(13, qrPacket.getPacketSentTo());
		preparedStatement.setString(14, qrPacket.getPacketSentFlag());
		preparedStatement.setString(15, qrPacket.getReverseVerificationFlag());
		preparedStatement.setInt(16, qrPacket.getUsedDialCount());
		preparedStatement.setString(17, qrPacket.getUsedDials());
		preparedStatement.setInt(18, qrPacket.getUnusedDialCount());
		preparedStatement.setString(19, qrPacket.getUnusedDials());
		preparedStatement.setInt(20, qrPacket.getAdditionalUsedDialCount());
		preparedStatement.setString(21, qrPacket.getAdditionalDials());
		preparedStatement.setString(22, qrPacket.getAdditionalUsedFrom());
		preparedStatement.setInt(23, qrPacket.getDuplicateDialCount());
		preparedStatement.setString(24, qrPacket.getDuplicateDials());
		preparedStatement.setInt(25, qrPacket.getExternalDialCount());
		preparedStatement.setString(26, qrPacket.getExternalDials());
		preparedStatement.setString(27, qrPacket.getApiRequest());
		preparedStatement.setDate(28, qrPacket.getReverseVerifiedOn());
		preparedStatement.setString(29, qrPacket.getMac());
		preparedStatement.setString(30, qrPacket.getSystemSerial());
		preparedStatement.setInt(31, qrPacket.getNoOfTimeVerified());
		preparedStatement.setString(32, qrPacket.getRvFileName());
		int result = preparedStatement.executeUpdate();
		log.info("Packet history entry is succusseful for packet: " + qrPacket.getPacketName() + " with result:  "
				+ result);

	}

	protected void MakeFileEntry(Connection dbConnection, String packetName, int noOfTimeverified, String fileName,
			int fileSize, String fileCheckSum, int fileExecutionTime) throws SQLException {
		PreparedStatement preparedStatement = dbConnection.prepareStatement("INSERT INTO PACKETFILE "
				+ "(PKT_NAME ,NO_OF_TIME_VERIFIED ,FILE_NAME ,FILE_SIZE,FILE_CHECKSUM,FILE_UPLOADED_ON,FILE_EXECUTION_TIME) "
				+ "values(?,?,?,?,?,now(),?)");
		preparedStatement.setString(1, packetName);
		preparedStatement.setInt(2, noOfTimeverified);
		preparedStatement.setString(3, fileName);
		preparedStatement.setInt(4, fileSize);
		preparedStatement.setString(5, fileCheckSum);
		preparedStatement.setInt(6, fileExecutionTime);
		int result = preparedStatement.executeUpdate();
		log.info("Packet file entry is successful for packet: " + packetName + " with result: " + result
				+ " for file name: " + fileName);

	}

	protected void updateReverseVerificationDetails(Connection dbConnection, String packetName, int usedDialCount,
			String retrievedDials, int unusedDialCounter, String unusedDials, int additionalDialCounter,
			String additionalDials, String additionalUsedFromData, int duplicateDialCounter, String duplicateDials,
			int externalCount, String externalCodes, String apiRequest, String mac, String systeSerial,
			int noOfTimeVerified, String rvFileName) throws SQLException {
		PreparedStatement preparedStatement = dbConnection.prepareStatement(
				"UPDATE QR_PACKET SET REVERSE_VERIFIED_FLG='Y', USED_COUNT=?, USED_CODES=?, UNUSED_COUNT=?, UNUSED_CODES=?, "
						+ "ADDITIONAL_USED_COUNT=?, ADDITIONAL_USED_CODES=?, ADDITIONAL_USED_FROM=?, DUP_COUNT=?, DUP_CODES=?, EXT_COUNT=?, "
						+ "EXT_CODES=?, API_REQUEST=?, REVERSE_VERIFIED_ON=NOW(),MAC_ADDRESS =?, SYSTEM_SERIAL = ?, NO_OF_TIME_VERIFIED =?, VERIFIED_CSV_NAME=? WHERE PKT_NAME=? ");
		preparedStatement.setInt(1, usedDialCount);
		preparedStatement.setString(2, retrievedDials);
		preparedStatement.setInt(3, unusedDialCounter);
		preparedStatement.setString(4, unusedDials);
		preparedStatement.setInt(5, additionalDialCounter);
		preparedStatement.setString(6, additionalDials);
		preparedStatement.setString(7, additionalUsedFromData);
		preparedStatement.setInt(8, duplicateDialCounter);
		preparedStatement.setString(9, duplicateDials);
		preparedStatement.setInt(10, externalCount);
		preparedStatement.setString(11, externalCodes);
		preparedStatement.setString(12, apiRequest);
		preparedStatement.setString(13, mac);
		preparedStatement.setString(14, systeSerial);
		preparedStatement.setInt(15, noOfTimeVerified);
		preparedStatement.setString(16, rvFileName);
		preparedStatement.setString(17, packetName);

		int result = preparedStatement.executeUpdate();
		log.info("Reverse verification update is succusseful for packet: " + packetName + " with result:  " + result);
	}

	protected void updatePacket(Connection dbConnection, String packetName, String dialList, int dialCodeCount,
			String bookName, String subject, String medium, String grade, String po, String astPO, String createdFor,
			String createdTime) throws SQLException {
		PreparedStatement preparedStatement = dbConnection
				.prepareStatement("UPDATE QR_PACKET SET DIAL_LIST=?, COUNT=?, BOOK_NAME=?, SUBJECT=?,"
						+ " MEDIUM=?, GRADE=?, PO=?, AST_PO=?, CREATED_FOR=?, R_CRE_TIME=? WHERE PKT_NAME=? ");

		preparedStatement.setString(1, dialList);
		preparedStatement.setInt(2, dialCodeCount);
		preparedStatement.setString(3, bookName);
		preparedStatement.setString(4, subject);
		preparedStatement.setString(5, medium);
		preparedStatement.setString(6, grade);
		preparedStatement.setString(7, po);
		preparedStatement.setString(8, astPO);
		preparedStatement.setString(9, createdFor);
		preparedStatement.setString(10, createdTime);
		preparedStatement.setString(11, packetName);
		int result = preparedStatement.executeUpdate();

		log.info("Packet: " + packetName + " is updated with result:  " + result);
	}

	protected QRPacket getPacketDetails(Connection dbConnection, String packetName) throws SQLException {
		PreparedStatement preparedStatement = dbConnection
				.prepareStatement("SELECT PKT_NAME, DIAL_LIST,  COUNT,CREATED_FOR, BOOK_NAME, MEDIUM, GRADE, "
						+ "SUBJECT, PO, AST_PO, R_CRE_TIME, PKT_SENT_ON, PKT_SENT_TO, PKT_SENT_FLG, "
						+ "REVERSE_VERIFIED_FLG, USED_COUNT, USED_CODES, UNUSED_COUNT, UNUSED_CODES, "
						+ "ADDITIONAL_USED_COUNT, ADDITIONAL_USED_CODES, REVERSE_VERIFIED_ON, ADDITIONAL_USED_FROM,"
						+ " DUP_COUNT, DUP_CODES, EXT_COUNT, EXT_CODES, API_REQUEST,VERIFIED_CSV_NAME,MAC_ADDRESS, SYSTEM_SERIAL, NO_OF_TIME_VERIFIED FROM QR_PACKET WHERE PKT_NAME=? ");

		preparedStatement.setString(1, packetName);
		ResultSet resultSet = preparedStatement.executeQuery();

		QRPacket qrPacket = new QRPacket();
		if (!resultSet.isBeforeFirst())
			return null;
		while (resultSet.next()) {
			qrPacket.setPacketName(resultSet.getString("PKT_NAME"));
			qrPacket.setIssuedDials(resultSet.getString("DIAL_LIST"));
			qrPacket.setIssuedDialCount(resultSet.getInt("COUNT"));
			qrPacket.setCreatedFor(resultSet.getString("CREATED_FOR"));
			qrPacket.setBookName(resultSet.getString("BOOK_NAME"));
			qrPacket.setMedium(resultSet.getString("MEDIUM"));
			qrPacket.setGrade(resultSet.getString("GRADE"));
			qrPacket.setSubject(resultSet.getString("SUBJECT"));
			qrPacket.setPo(resultSet.getString("PO"));
			qrPacket.setAstPO(resultSet.getString("AST_PO"));
			qrPacket.setCreatedTime(resultSet.getString("R_CRE_TIME"));
			qrPacket.setPacketSentOn(resultSet.getDate("PKT_SENT_ON"));
			qrPacket.setPacketSentTo(resultSet.getString("PKT_SENT_TO"));
			qrPacket.setPacketSentFlag(resultSet.getString("PKT_SENT_FLG"));
			qrPacket.setReverseVerificationFlag(resultSet.getString("REVERSE_VERIFIED_FLG"));
			qrPacket.setUsedDialCount(resultSet.getInt("USED_COUNT"));
			qrPacket.setUnusedDialCount(resultSet.getInt("UNUSED_COUNT"));
			qrPacket.setAdditionalUsedDialCount(resultSet.getInt("ADDITIONAL_USED_COUNT"));
			qrPacket.setUsedDials(resultSet.getString("USED_CODES"));
			qrPacket.setUnusedDials(resultSet.getString("UNUSED_CODES"));
			qrPacket.setAdditionalDials(resultSet.getString("ADDITIONAL_USED_CODES"));
			qrPacket.setReverseVerifiedOn(resultSet.getDate("REVERSE_VERIFIED_ON"));
			qrPacket.setAdditionalUsedFrom(resultSet.getString("ADDITIONAL_USED_FROM"));
			qrPacket.setDuplicateDialCount(resultSet.getInt("DUP_COUNT"));
			qrPacket.setDuplicateDials(resultSet.getString("DUP_CODES"));
			qrPacket.setExternalDialCount(resultSet.getInt("EXT_COUNT"));
			qrPacket.setExternalDials(resultSet.getString("EXT_CODES"));
			qrPacket.setApiRequest(resultSet.getString("API_REQUEST"));
			qrPacket.setRvFileName(resultSet.getString("VERIFIED_CSV_NAME"));
			qrPacket.setMac(resultSet.getString("MAC_ADDRESS"));
			qrPacket.setSystemSerial(resultSet.getString("SYSTEM_SERIAL"));
			qrPacket.setNoOfTimeVerified(resultSet.getInt("NO_OF_TIME_VERIFIED"));
		}

		return qrPacket;
	}

	protected List<String> getPktNameByDialCode(Connection dbConnection, String dialCode) throws SQLException {
		List<String> packetNames = new ArrayList<String>();
		PreparedStatement preparedStatement = dbConnection
				.prepareStatement("SELECT PKT_NAME FROM QR_PACKET WHERE LOCATE(?,DIAL_LIST) > 0");
		preparedStatement.setString(1, dialCode);
		ResultSet resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			packetNames.add(resultSet.getString("PKT_NAME"));
		}
		return packetNames;
	}

	protected int getPacketCount(Connection dbConnection, String packetName) throws SQLException {
		int packetCount = 0;
		final String query = "SELECT COUNT(*) FROM QR_PACKET WHERE PKT_NAME = ?;" + "";
		final PreparedStatement ps = dbConnection.prepareStatement(query);
		ps.setString(1, packetName);
		final ResultSet resultSet = ps.executeQuery();
		if (resultSet.next()) {
			packetCount = resultSet.getInt(1);
		}
		return packetCount;
	}
}