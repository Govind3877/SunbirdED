package reverseverification.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reverseverification.domain.FileExecutionData;
import reverseverification.domain.PacketAttributes;
import reverseverification.domain.PacketData;

/**
 * fileName    	:: QRExtractionService.java
 * @author 	   	:: ravi
 * description 	:: This is service class to start QRExtraction Task for each PDF
 * @version     :: RVTool-3.0.0
 * 
 * 
 */
class QRExtractionService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	protected static boolean isDuplicateFound = false;
	protected List<PacketData> runQRExtractionTask(File booksDir) {
		List<PacketData> packetDataList = new ArrayList<>();
		List<CSVPrinter> validDatacsvPrinters = new ArrayList<>();
		List<CSVPrinter> invalidDatacsvPrinters = new ArrayList<>();

		Map<String, Map<String, String>> allDuplicateQRCodeMap = new HashMap<>();
		try {
			File[] pdfDirs = booksDir.listFiles();

			if (null != pdfDirs && pdfDirs.length != 0) {
				new File(booksDir.getAbsolutePath() + "/Payloads/").mkdir();
				ExecutorService executorService = Executors
						.newFixedThreadPool(ConfigurationService.getConfiguration().getThreadpoolsize());
				for (File pdfDir : pdfDirs) {
					if (pdfDir.isDirectory()) {// running for each packet
						log.info("Book Name: " + pdfDir.getName());
						File[] pdfFiles = pdfDir.listFiles();
						if (pdfFiles.length != 0) {
							// checking files count
							int pdfCount = (int) Arrays.stream(pdfDir.listFiles())
									.filter(file -> getFileExtension(file).toLowerCase().equals("pdf")).count();
							if (pdfCount != 0) {
								System.out.println("Preparing payload for book: " + pdfDir.getName());
								Map<String, String> packetStatusMap = new HashMap<>();
								Map<String, String> qrCodeMap = new HashMap<>();
								Map<String, String> duplicateQRCodeMap = new HashMap<>();
								allDuplicateQRCodeMap.put(pdfDir.getName(), duplicateQRCodeMap);

								List<String> QRDataList = new ArrayList<>();
								LinkedList<FileExecutionData> fileExecutionDataList = new LinkedList<>();
								List<String> unreadableQRList = new ArrayList<>();
								PacketAttributes packetAttributes = new PacketAttributes();
								// creating packet data for each packet
								PacketData packetData = new PacketData();
								packetData.setPacketName(pdfDir.getName());
								packetData.setDialCodeList(QRDataList);
								packetData.setFileExecutionData(fileExecutionDataList);
								packetData.setUnreadableQRPageList(unreadableQRList);
								packetData.setPacketAttributes(packetAttributes);

								// CSV file for SB QR codes
								CSVPrinter validDatacsvPrinter = new CSVPrinter(
										Files.newBufferedWriter(Paths.get(pdfDir.getParent() + "/Payloads/"
												+ pdfDir.getName() + "-SB_Data.csv")),
										CSVFormat.DEFAULT.withHeader("QR Contents", "QR Codes", "PDF Name", "Page Number"));
								validDatacsvPrinters.add(validDatacsvPrinter);
								// CSV file for non-SB QR codes
								CSVPrinter invalidDatacsvPrinter = new CSVPrinter(
										Files.newBufferedWriter(Paths.get(pdfDir.getParent() + "/Payloads/"
												+ pdfDir.getName() + "-Non_SB_Data.csv")),
										CSVFormat.DEFAULT.withHeader("QR Contents", "PDF Name","Page Number"));
								invalidDatacsvPrinters.add(invalidDatacsvPrinter);

								// starting QR extraction task for each PDF
								for (File pdf : pdfFiles) {
									if (getFileExtension(pdf).toLowerCase().equals("pdf")) {
										FileExecutionData fileExecutionData = new FileExecutionData();
										fileExecutionData.setPdfFile(pdf);
										fileExecutionDataList.add(fileExecutionData);
										packetStatusMap.put(pdf.getName(), "false");
										QRExtractionTask qrExtractionTask = new QRExtractionTask(pdfDir.getName(),
												fileExecutionData, validDatacsvPrinter, invalidDatacsvPrinter,
												QRDataList, packetStatusMap, qrCodeMap, duplicateQRCodeMap,
												unreadableQRList, packetAttributes);
										executorService.execute(qrExtractionTask);
									} else
										log.warn(pdf.getName() + " is not a PDF file");
								}
								packetDataList.add(packetData);

							} else
								log.warn(pdfDir + " directory does not contain PDF files");
						} else
							log.warn(pdfDir + " is empty directory");
					} // packet ends
					else {
						log.warn(pdfDir + " is not a directory");
					}
				}
				// shutting down executors service
				executorService.shutdown();
				while (!executorService.isTerminated()) {
				} // waiting for all threads to complete

				
				// writing duplicate QR details
				if (isDuplicateFound) {
					System.out.println(
							"Duplicate QR Codes are found, please check 'Duplicate_Dial_Codes.csv' in Payloads folder");
					log.info(
							"Duplicate QR Codes are found, please check 'Duplicate_Dial_Codes.csv' in Payloads directory");
					try (CSVPrinter duplicateDataCSVPrinter = new CSVPrinter(
							Files.newBufferedWriter(
									Paths.get(booksDir.getAbsolutePath() + "/Payloads/" + "Duplicate_Dial_Codes.csv")),
							CSVFormat.DEFAULT.withHeader("Book Name", "Dial Code", "PDF Name", "Page Number"));) {
						for (Map.Entry<String, Map<String, String>> duplicateEntry : allDuplicateQRCodeMap.entrySet()) {
							for (Map.Entry<String, String> dialCodeEntry : duplicateEntry.getValue().entrySet()) {
								String[] pageNumbers = dialCodeEntry.getValue().split(",");
								for (String pageNumber : pageNumbers) {
									duplicateDataCSVPrinter.printRecord(duplicateEntry.getKey(), dialCodeEntry.getKey(),
											pageNumber.substring(0, pageNumber.lastIndexOf("_")),
											pageNumber.substring(pageNumber.lastIndexOf("_") + 1, pageNumber.length()));
								}
							}
						}
						duplicateDataCSVPrinter.flush();
					} catch (Exception e) {
						log.error("Error generating Duplicate_Dial_Codes.csv", e);
						System.out.println("Duplicate_Dial_Codes.csv could not be generated!");
					}
				}

			} else {
				System.out.println(booksDir.getName() + " is empty or invalid folder");
				log.warn(booksDir.getName() + " is empty or invalid directory");
			}
		} catch (Exception exception) {
			log.error("QR extraction has failed due to: ", exception);
		} finally {
			try {
				// closing CSV resources
				for (CSVPrinter validDatacsvPrinter : validDatacsvPrinters)
					validDatacsvPrinter.flush();

				for (CSVPrinter invalidDatacsvPrinter : invalidDatacsvPrinters)
					invalidDatacsvPrinter.flush();

				for (CSVPrinter validDatacsvPrinter : validDatacsvPrinters)
					validDatacsvPrinter.close();

				for (CSVPrinter invalidDatacsvPrinter : invalidDatacsvPrinters)
					invalidDatacsvPrinter.close();

			} catch (Exception exception) {
				log.error("Error in closing CSV  streams: " + exception);
			}
		}

		return packetDataList;

	}

	// getting file extension
	private static String getFileExtension(File file) {
		String fileName = file.getName();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		else
			return "";
	}

}
