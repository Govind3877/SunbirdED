package reverseverification.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reverseverification.domain.TenantData;
import reverseverification.domain.TenantInfo;

public class ReportService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public TenantData getRVBooks(String tenant) {
		TenantData rvBooks = new TenantData();

		try (Connection dbConnection = DBConnectionManager.getConnection();) {
			ArrayList<TenantInfo> rvBookList = new ArrayList<>();
			ResultSet result;

			PreparedStatement preparedStatement = dbConnection.prepareStatement(
					"SELECT CREATED_FOR,COUNT(1) AS COUNT FROM QR_PACKET WHERE REVERSE_VERIFIED_FLG='Y' AND PKT_NAME NOT LIKE '%POOL%' GROUP BY CREATED_FOR");
			result = preparedStatement.executeQuery();

			if (!result.isBeforeFirst()) {
				rvBooks.setErrmsg("No reverse verified book is found");
				log.info("No reverse verified book is found: " + tenant);
			} else {
				while (result.next()) {
					TenantInfo tenantInfo = new TenantInfo();
					tenantInfo.setCreatedFor(result.getString("CREATED_FOR"));
					tenantInfo.setCount(result.getLong("COUNT"));
					rvBookList.add(tenantInfo);
				}
				rvBooks.setResult(rvBookList);
				log.info("RV book list is successfully accessed for tenant: " + tenant);
			}

			preparedStatement = dbConnection.prepareStatement(
					"SELECT COUNT(1) AS COUNT FROM QR_PACKET WHERE REVERSE_VERIFIED_FLG='Y' AND PKT_NAME NOT LIKE '%POOL%'");
			result = preparedStatement.executeQuery();
			result.next();
			rvBooks.setTotalCount(result.getLong("COUNT"));

		} catch (SQLException e) {
			log.error("Error in fetching RV books for tenant:" + tenant, e);
			rvBooks.setErrmsg("Error fetching reverse verified books");
		}
		return rvBooks;

	}

	public TenantData getUsedQRCodes(String tenant) {
		TenantData usedQRCodes = new TenantData();

		try (Connection dbConnection = DBConnectionManager.getConnection();) {
			ArrayList<TenantInfo> rvBookList = new ArrayList<>();
			ResultSet result;

			PreparedStatement preparedStatement = dbConnection.prepareStatement(
					"SELECT CREATED_FOR, SUM(USED_COUNT) AS USED_COUNT  FROM QR_PACKET GROUP BY CREATED_FOR");
			result = preparedStatement.executeQuery();

			if (!result.isBeforeFirst()) {
				usedQRCodes.setErrmsg("No used QR code is found");
				log.info("No used QR code is found: " + tenant);
			} else {
				while (result.next()) {
					TenantInfo tenantInfo = new TenantInfo();
					tenantInfo.setCreatedFor(result.getString("CREATED_FOR"));
					tenantInfo.setCount(result.getLong("USED_COUNT"));
					rvBookList.add(tenantInfo);
				}
				usedQRCodes.setResult(rvBookList);
				log.info("Used QR code list is successfully accessed for tenant: " + tenant);
			}

			preparedStatement = dbConnection.prepareStatement("SELECT SUM(USED_COUNT) AS USED_COUNT  FROM QR_PACKET");
			result = preparedStatement.executeQuery();
			result.next();
			usedQRCodes.setTotalCount(result.getLong("USED_COUNT"));

		} catch (SQLException e) {
			log.error("Error in fetching used QR codes for tenant:" + tenant, e);
			usedQRCodes.setErrmsg("Error fetching used QR codes");
		}
		return usedQRCodes;

	}

}
