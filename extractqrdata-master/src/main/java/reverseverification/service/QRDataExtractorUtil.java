package reverseverification.service;

/**
 * fileName    	:: QRDataExtractorUtil.java
 * @author 	   	:: ravi
 * description 	:: This is utility class for getting QR data from books
 * @version     :: RVTool-3.0.0
 * 
 * 
 */

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reverseverification.domain.PacketData;

public class QRDataExtractorUtil {
	private static final Logger log = LoggerFactory.getLogger(QRDataExtractorUtil.class);

	// extracts QR data from books
	public static List<PacketData> getQRDataFromBooks(File booksDir) {
		List<PacketData> packetData = null;
		try {
			// starting QR Extraction service
			packetData = new QRExtractionService().runQRExtractionTask(booksDir);

		} catch (Exception exception) {
			log.error("Failed to start QR Extraction task " + exception);
		}
		return packetData;
	}

}
