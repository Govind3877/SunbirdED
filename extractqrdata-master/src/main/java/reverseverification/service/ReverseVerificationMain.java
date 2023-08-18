package reverseverification.service;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reverseverification.domain.PacketData;
import reverseverification.domain.RVResponse;
import reverseverification.domain.Response;
import reverseverification.domain.Settings;

/**
 * fileName :: ReverseVerificationMain.java
 * 
 * @author :: ravi description :: This is main class which should be run to
 *         start RVTool
 * @version :: RVTool-3.0.0
 * 
 * 
 */

public class ReverseVerificationMain {
	private static final Logger log = LoggerFactory.getLogger(ReverseVerificationMain.class);

	public static void main(String[] args) {
		System.out.println("*****Reverse Verification Tool has started running at:[" + LocalDateTime.now() + "]*****");
		log.info("Reverse Verification Tool has started running at: " + LocalDateTime.now());

		Workbook workbook = null;
		FileOutputStream fileOutputStream = null;
		boolean isAnyRVFailure = false;

		try {
			if (args.length == 5) {
				ConfigurationService.setConfiguration(args);
			} else {
				log.error(args.length + " configuration params are specified but it requires 5");
				System.out.println(args.length + " configuration params are specified but it requires 5");
				RVTool.exit("missing_value_config");
			}
		} catch (Exception e) {
			log.error("Error receiving configuration params", e);
			RVTool.exit("invalid_value");
		}

		try {
			Settings settings = ConfigurationService.getSettings();
			if (settings != null) {
				List<String> folderNameList = new ArrayList<>();
				File folderName = new File(settings.getBooksDir());
				System.out.println("FolderName===========>>>" + folderName.listFiles());
				int bookMaxLimit = Integer.parseInt(settings.getMaxBookLimit().trim());
				System.out.println("bookMaxLimit=========>>>" + bookMaxLimit);
				if (folderName.listFiles().length > bookMaxLimit) {
					System.out.println("You have more than " + bookMaxLimit
							+ " books for reverse verification, maximum limit is set to " + settings.getMaxBookLimit());
					log.error(folderName.listFiles().length + " books are more than max limit " + bookMaxLimit);
					RVTool.exit("book_count_mismatch");
				}
				Arrays.stream(folderName.listFiles())
						.filter(folder -> folder.isDirectory() && !folder.getName().equals("Payloads"))
						.forEach(folder -> {
							folderNameList.add(folder.getName()/* .toUpperCase() */);
						});
				

				if (ConfigurationService.findPacketNames(folderNameList)) {
					// generating payloads
					List<PacketData> packetDataList = QRDataExtractorUtil.getQRDataFromBooks(folderName);

					if (!packetDataList.isEmpty()) {
						boolean isReverseVerificationRequired = settings.getIsReverseVerificationRequired().equals("y")
								? true
								: false;
						File rvReportsDir = null;
						if (isReverseVerificationRequired) {
							System.out.println("Reverse verifying generated payloads...");
							if (!new File("./RVReports/").exists()) {
								new File("./RVReports/").mkdir(); // creating RVReports folder
							}
							if (!(new File("./RVReports/" + LocalDate.now()).exists())) {
								new File("./RVReports/" + LocalDate.now()).mkdir(); // creating a folder with current
																					// data and time under RVReports
																					// folder
							}

							String rvResultFile = "./RVReports/" + LocalDate.now() + "/RVReport_" + LocalDateTime.now()
									.withNano(0).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss")) + ".xlsx";
							String[] rvReportColumns = { "Book Name", "Used Count", "Used QR", "Unused Count",
									"Unused QR", "Duplicate Count", "Duplicate QR", "Additional Count", "Additional QR",
									"Additional Used From" };
							workbook = new XSSFWorkbook();
							Sheet detailSheet = workbook.createSheet("Details");
							Sheet summarySheet = workbook.createSheet("Summary");
							summarySheet.setColumnWidth(0, 8000);
							summarySheet.setColumnWidth(1, 9000);

							Font statusGreenFont = workbook.createFont();
							statusGreenFont.setColor(IndexedColors.GREEN.getIndex());

							Font statusRedFont = workbook.createFont();
							statusRedFont.setColor(IndexedColors.RED.getIndex());

							Font boldFont = workbook.createFont();
							boldFont.setBold(true);

							CellStyle statusPassCellStyle = workbook.createCellStyle();
							statusPassCellStyle.setFont(statusGreenFont);
							statusPassCellStyle.setAlignment(HorizontalAlignment.CENTER);

							CellStyle statusFailCellStyle = workbook.createCellStyle();
							statusFailCellStyle.setFont(statusRedFont);
							statusFailCellStyle.setAlignment(HorizontalAlignment.CENTER);

							CellStyle boldCellStyle = workbook.createCellStyle();
							boldCellStyle.setAlignment(HorizontalAlignment.CENTER);
							boldCellStyle.setFont(boldFont);

							CellStyle alignmentStyle = workbook.createCellStyle();
							alignmentStyle.setAlignment(HorizontalAlignment.CENTER);
							alignmentStyle.setVerticalAlignment(VerticalAlignment.TOP);
							alignmentStyle.setWrapText(true);

							Row headerRow = detailSheet.createRow(0);
							for (int i = 0; i < rvReportColumns.length; i++) {
								Cell cell = headerRow.createCell(i);
								cell.setCellValue(rvReportColumns[i]);
								cell.setCellStyle(boldCellStyle);
							}
							int detailsRowNum = 1;
							int summaryRowNum = 0;
							Row summaryRow;
							ReverseVerificationService reverseVerificationService = new ReverseVerificationService();
							for (PacketData packetData : packetDataList) {// running for each packet
								LinkedList<String> pdfCheckSumList = new LinkedList<>();
								LinkedList<String> fileExecutionDataList = new LinkedList<>();

								// generating checksum, file size
								packetData.getFileExecutionData().forEach(fileExecutionData -> {
									fileExecutionDataList.add(fileExecutionData.getPdfFile().getName() + ":"
											+ fileExecutionData.getPdfFile().length() + ":"
											+ fileExecutionData.getExtractionTime());
									pdfCheckSumList.add(SecurityAttributeService
											.getPDFCheckSum(fileExecutionData.getPdfFile().getAbsolutePath()));
								});

								// reverse verifying each packet
								RVResponse rvResponse = reverseVerificationService.reverseVerify(
										ConfigurationService.getConfiguration().getRequesturl(),
										settings.getToken().trim(), packetData.getPacketName(),
										packetData.getDialCodeList(), fileExecutionDataList, pdfCheckSumList,
										new File(rvResultFile).getName());
								if (null != rvResponse) {
									if (null == rvResponse.getErrmsg()) {
										Response response = rvResponse.getResponse();
										if (null != response) {
											rvReportsDir = new File(rvResultFile).getParentFile();
											// writing on RV report excel
											Row detailsRow = detailSheet.createRow(detailsRowNum++);

											int detailsColNum = 0;

											// writing details sheet for each packet
											detailsRow.createCell(detailsColNum++)
													.setCellValue(response.getPacketName());
											detailsRow.createCell(detailsColNum++)
													.setCellValue(response.getUsedCount());
											detailsRow.createCell(detailsColNum++)
													.setCellValue(response.getUsedDials().toString());
											detailsRow.createCell(detailsColNum++)
													.setCellValue(response.getUnusedCount());
											detailsRow.createCell(detailsColNum++)
													.setCellValue(response.getUnusedDials().toString());
											detailsRow.createCell(detailsColNum++).setCellValue(response.getDupCount());
											detailsRow.createCell(detailsColNum++)
													.setCellValue(response.getDupDials().toString());
											detailsRow.createCell(detailsColNum++)
													.setCellValue(response.getAdditionalCount());
											detailsRow.createCell(detailsColNum++)
													.setCellValue(response.getAdditionalDials().toString());
											detailsRow.createCell(detailsColNum++)
													.setCellValue(response.getAdditionalUsedFrom());

											// writing summary sheet for each packet
											List<String> failureReasonList = validateReverseVerificationReport(
													packetData.getPacketAttributes().isDuplicateFound(),
													response.getAdditionalCount(), packetData.getDialCodeList().size(),
													response.getDialCodeCount(),
													packetData.getPacketAttributes().getDetectedQRCount(),
													packetData.getPacketAttributes().getExternalQRCount());

											summaryRow = summarySheet.createRow(summaryRowNum++);
											Cell packetNameCell = summaryRow.createCell(0);
											packetNameCell.setCellValue("Book");
											packetNameCell.setCellStyle(boldCellStyle);

											packetNameCell = summaryRow.createCell(1);
											packetNameCell.setCellValue(response.getPacketName());
											packetNameCell.setCellStyle(boldCellStyle);

											summaryRow = summarySheet.createRow(summaryRowNum++);
											Cell cell = summaryRow.createCell(0);
											cell.setCellValue("Date");
											cell.setCellStyle(alignmentStyle);

											cell = summaryRow.createCell(1);
											cell.setCellValue(LocalDate.now().toString());
											cell.setCellStyle(alignmentStyle);

											summaryRow = summarySheet.createRow(summaryRowNum++);
											cell = summaryRow.createCell(0);
											cell.setCellValue("Status");
											cell.setCellStyle(alignmentStyle);

											if (failureReasonList.isEmpty()) {
												cell = summaryRow.createCell(1);
												cell.setCellValue("Signed-off");
												cell.setCellStyle(statusPassCellStyle);
											} else {
												cell = summaryRow.createCell(1);
												cell.setCellValue("Not Signed-off");
												cell.setCellStyle(statusFailCellStyle);
											}

											summaryRow = summarySheet.createRow(summaryRowNum++);
											cell = summaryRow.createCell(0);
											cell.setCellValue("No. of QR Codes Issued");
											cell.setCellStyle(alignmentStyle);

											cell = summaryRow.createCell(1, CellType.NUMERIC);
											cell.setCellValue(response.getDialCodeCount());
											cell.setCellStyle(alignmentStyle);

											summaryRow = summarySheet.createRow(summaryRowNum++);
											cell = summaryRow.createCell(0);
											cell.setCellValue("No. of QR Codes Detected in Scanning");
											cell.setCellStyle(alignmentStyle);

											cell = summaryRow.createCell(1, CellType.NUMERIC);
											cell.setCellValue(packetData.getPacketAttributes().getDetectedQRCount());
											cell.setCellStyle(alignmentStyle);

											summaryRow = summarySheet.createRow(summaryRowNum++);
											cell = summaryRow.createCell(0);
											cell.setCellValue("No. of SB QR Codes Read in PDF Book");
											cell.setCellStyle(alignmentStyle);

											cell = summaryRow.createCell(1, CellType.NUMERIC);
											cell.setCellValue(packetData.getDialCodeList().size());
											cell.setCellStyle(alignmentStyle);

											summaryRow = summarySheet.createRow(summaryRowNum++);
											cell = summaryRow.createCell(0);
											cell.setCellValue("No. of Additional SB QR Codes");
											cell.setCellStyle(alignmentStyle);

											cell = summaryRow.createCell(1, CellType.NUMERIC);
											cell.setCellValue(response.getAdditionalCount());
											cell.setCellStyle(alignmentStyle);

											summaryRow = summarySheet.createRow(summaryRowNum++);
											cell = summaryRow.createCell(0);
											cell.setCellValue("Duplicate Found");
											cell.setCellStyle(alignmentStyle);

											cell = summaryRow.createCell(1);
											cell.setCellValue(
													packetData.getPacketAttributes().isDuplicateFound() ? "Yes" : "No");
											cell.setCellStyle(packetData.getPacketAttributes().isDuplicateFound()
													? statusFailCellStyle
													: alignmentStyle);

											summaryRow = summarySheet.createRow(summaryRowNum++);
											cell = summaryRow.createCell(0);
											cell.setCellValue("No. of Non-SB QR Codes");
											cell.setCellStyle(alignmentStyle);

											cell = summaryRow.createCell(1, CellType.NUMERIC);
											cell.setCellValue(packetData.getPacketAttributes().getExternalQRCount());
											cell.setCellStyle(alignmentStyle);

											summaryRow = summarySheet.createRow(summaryRowNum++);
											cell = summaryRow.createCell(0);
											cell.setCellValue("Reason of Failure");
											cell.setCellStyle(alignmentStyle);

											cell = summaryRow.createCell(1);
											cell.setCellValue(
													failureReasonList.isEmpty() ? "NA" : failureReasonList.toString());
											cell.setCellStyle(alignmentStyle);

											if (!packetData.getUnreadableQRPageList().isEmpty()) {
												log.info("Unreadable page number list [book: "
														+ packetData.getPacketName() + "]: "
														+ packetData.getUnreadableQRPageList());
												summaryRow = summarySheet.createRow(summaryRowNum++);
												cell = summaryRow.createCell(0);
												cell.setCellValue("Unreadable QR Codes [PDF-Page No.]");
												cell.setCellStyle(alignmentStyle);
												StringBuilder unreadableQRDetails = new StringBuilder();
												packetData.getUnreadableQRPageList().forEach(page -> {
													unreadableQRDetails.append(page.substring(0, page.lastIndexOf("_"))
															+ "-"
															+ page.substring(page.lastIndexOf("_") + 1, page.length()));
													unreadableQRDetails.append(",");
												});
												cell = summaryRow.createCell(1);
												cell.setCellValue(unreadableQRDetails.substring(0,
														unreadableQRDetails.length() - 1));
												cell.setCellStyle(alignmentStyle);
											}
											summaryRow = summarySheet.createRow(summaryRowNum++);

										}
									} else {
										isAnyRVFailure = true;
										System.out.println(rvResponse.getErrmsg());
										log.error(rvResponse.getErrmsg());
									}
								} else {
									isAnyRVFailure = true;
									System.out.println("Could not verify book: " + packetData.getPacketName());
									log.info("Could not verify book: " + packetData.getPacketName());
								}

							} // packet ends
							fileOutputStream = new FileOutputStream(rvResultFile);
							workbook.write(fileOutputStream);
						}

						// opening output folder if reverse verified
						if (isReverseVerificationRequired && null != rvReportsDir
								&& ConfigurationService.getConfiguration().isOpenReportWindow()) {
							try {
								Desktop.getDesktop().open(rvReportsDir);
							} catch (IOException e) {
								System.out.println("Could not open  RV report folder, please find the file at: "
										+ rvReportsDir.getAbsolutePath());
								log.error("Could not open RV report folder", e);
							}
						}

					} else {
						System.out.println("Error in receving books data");
						log.error("Error in receving books data");
						RVTool.exit("books_dir_error");
					}
				}

			}
		} catch (Exception exception) {
			log.error("Error in reverse verification", exception);
			RVTool.exit("unexpected_error_main");
		} finally {
			try {
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
				if (workbook != null) {
					workbook.close();
				}
			} catch (IOException e) {
				log.error("Error closing excel resource", e);
			}
		}
		System.out.println("*****Reverse Verification Tool has finished running at:[" + LocalDateTime.now() + "]*****");
		log.info("Reverse Verification Tool has finished running at: " + LocalDateTime.now());
		if (isAnyRVFailure)
			RVTool.exit("api_response_error");

	}

	// Finding if reverse verification successes or fails
	public static List<String> validateReverseVerificationReport(Boolean isDuplicateFound, long additionalQRCount,
			int SBQRCount, long sentQRCount, int detectedQRCount, int externalQRCount) {
		List<String> failureReasonList = new ArrayList<String>();
		if (isDuplicateFound) {
			failureReasonList.add("Duplicate QR codes found");
		}
		if (additionalQRCount > 0) {
			failureReasonList.add("Additional QR codes found");
		}
		if ((SBQRCount - additionalQRCount) < sentQRCount) {
			failureReasonList.add("Not all QR codes are used");
		}
		if ((SBQRCount - additionalQRCount) > sentQRCount) {
			failureReasonList.add("More QR codes are used");
		}
		if ((detectedQRCount - externalQRCount) != SBQRCount) {
			failureReasonList.add("Unreadable QR codes are found");
		}
		return failureReasonList;

	}
}
