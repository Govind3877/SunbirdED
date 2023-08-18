package reverseverification.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVPrinter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;

import reverseverification.domain.FileExecutionData;
import reverseverification.domain.PacketAttributes;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


/**
 * fileName    	:: QRExtractionTask.java
 *
 * @author :: ravi
 * description 	:: This is runnable class which is assigned to threads
 * @version :: RVTool-3.0.0
 */


class QRExtractionTask implements Runnable {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private String packetName;
    private FileExecutionData fileExecutionData;
    private CSVPrinter validDatacsvPrinter, invalidDatacsvPrinter;
    private List<String> QRDataList;
    private Map<String, String> packetStatusMap;
    private Map<String, String> qrCodeMap;
    private Map<String, String> duplicateQRCodeMap;
    private PacketAttributes packetAttributes;
    private List<String> unreadableQRList;

    public QRExtractionTask(String packetName, FileExecutionData fileExecutionData, CSVPrinter validDatacsvPrinter,
                            CSVPrinter invalidDatacsvPrinter, List<String> QRDataList, Map<String, String> packetStatusMap,
                            Map<String, String> qrCodeMap, Map<String, String> duplicateQRCodeMap, List<String> unreadableQRList,
                            PacketAttributes packetAttributes) {
        this.packetName = packetName;
        this.fileExecutionData = fileExecutionData;
        this.validDatacsvPrinter = validDatacsvPrinter;
        this.invalidDatacsvPrinter = invalidDatacsvPrinter;
        this.QRDataList = QRDataList;
        this.packetStatusMap = packetStatusMap;
        this.qrCodeMap = qrCodeMap;
        this.duplicateQRCodeMap = duplicateQRCodeMap;
        this.unreadableQRList = unreadableQRList;
        this.packetAttributes = packetAttributes;
    }

    public void run() {
        String readText, dialCode;
        int externalQRCount = 0;
        int detectedQRCount = 0;
        int dpi = ConfigurationService.getConfiguration().getDpi();
        long extractionStartTime = System.currentTimeMillis();
        try (PDDocument pdDocument = PDDocument.load(fileExecutionData.getPdfFile());) {
            PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);

            String pdfFileName = fileExecutionData.getPdfFile().getName().replace(".pdf", "");

            log.info("Book Name: " + packetName + " PDF Name: " + fileExecutionData.getPdfFile().getName() + " Pages: "
                    + pdDocument.getNumberOfPages());
            for (int page = 0; page < pdDocument.getNumberOfPages(); page++) {
                String imageName = pdfFileName + "_" + (page + 1);

                //rerading each QR image for issues - START
/*
				com.google.zxing.Reader qrreader = new QRCodeReader();
                StringBuffer QRCodeErrors=new StringBuffer("QR Code Structure Errors :");
                try {
					PDResources pdResources = page.getResources();
					for (COSName c : pdResources.getXObjectNames()) {
						PDXObject o = pdResources.getXObject(c);
						if (o instanceof org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject) {
							BufferedImage renderedImage = ((org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject) o).getImage();
							if (renderedImage.getHeight() < 30) {
								continue;
							} else if (renderedImage.getHeight() > 100) {
								continue;
							}

							com.google.zxing.LuminanceSource source = new com.google.zxing.client.j2se.BufferedImageLuminanceSource(renderedImage);
							com.google.zxing.BinaryBitmap bitmap = new com.google.zxing.BinaryBitmap(new com.google.zxing.common.HybridBinarizer(source));

							try {
								Result result = qrreader.decode(bitmap);

								if (result != null && result.getText() != null && result.getText().contains("https://diksha.gov.in/dial")) {
									// Code to check Mirror Image - START

									try {
										Decoder decoder = new Decoder();
										DecoderResult decoderResult;
										DetectorResult detectorResult = new Detector(bitmap.getBlackMatrix()).detect();
										decoderResult = decoder.decode(detectorResult.getBits());

										if (decoderResult.getOther() instanceof QRCodeDecoderMetaData) {
											QRCodeDecoderMetaData imageMetaData = (QRCodeDecoderMetaData) decoderResult.getOther();
											System.out.println(decoderResult.getText());
											System.out.println("Is Mirrored:: " + imageMetaData.isMirrored()); //returns true for a mirrored image
                                            QRCodeErrors.append(decoderResult.getText() + "Is Mirrored:: "  + "Book Name: " + packetName + " PDF Name: " + fileExecutionData.getPdfFile().getName() + " ");
										}
									} catch (Exception ex1) {
										//do nothing
									}
									/* Code to check Mirror Image - END

									String strReadText = result.getText();
									int iLastFolderSepindex = strReadText.lastIndexOf("/");
									String strQRCode = strReadText.substring(iLastFolderSepindex + 1);
									System.out.println("QRCode:: " + strQRCode);
									iCounter++;
									File file = new File(sourceDir + File.separator + "QRImages" + File.separator + strQRCode + "_" + iCounter + ".png");

									// Code to Check Missing Border - START
									iImageWidth = renderedImage.getWidth();
									iImageHeight = renderedImage.getHeight();

									//top border -> x=0towidth-1 y=0
									iSumRed = 0;
									iSumGreen = 0;
									iSumBlue = 0;
									for (int x = 0; x < iImageWidth; x++) {
										Color pixelColor = new Color(renderedImage.getRGB(x, 0));
										iSumRed = iSumRed + pixelColor.getRed();
										iSumGreen = iSumGreen + pixelColor.getGreen();
										iSumBlue = iSumBlue + pixelColor.getBlue();
									}
									if ((iSumRed / iImageWidth) > iBorderColorLimit || (iSumGreen / iImageWidth) > iBorderColorLimit || (iSumBlue / iImageWidth > iBorderColorLimit)) {
										System.out.println("Top Border Missing");
                                        QRCodeErrors.append("Top Border Missing");
									}

									//right border -> x=iImageWidth-1  y=0toheight-1
									iSumRed = 0;
									iSumGreen = 0;
									iSumBlue = 0;
									for (int y = 0; y < iImageHeight; y++) {
										Color pixelColor = new Color(renderedImage.getRGB(iImageWidth - 1, y));
										iSumRed = iSumRed + pixelColor.getRed();
										iSumGreen = iSumGreen + pixelColor.getGreen();
										iSumBlue = iSumBlue + pixelColor.getBlue();
									}
									if ((iSumRed / iImageHeight) > iBorderColorLimit || (iSumGreen / iImageHeight) > iBorderColorLimit || (iSumBlue / iImageHeight > iBorderColorLimit)) {
										System.out.println("Right Border Missing");
                                        QRCodeErrors.append("Right Border Missing");
									}


									//bottom border -> x=0towidth-1 y=iImageHeight-1
									iSumRed = 0;
									iSumGreen = 0;
									iSumBlue = 0;
									for (int x = 0; x < iImageWidth; x++) {
										Color pixelColor = new Color(renderedImage.getRGB(x, iImageHeight - 1));
										iSumRed = iSumRed + pixelColor.getRed();
										iSumGreen = iSumGreen + pixelColor.getGreen();
										iSumBlue = iSumBlue + pixelColor.getBlue();
									}
									if ((iSumRed / iImageWidth) > iBorderColorLimit || (iSumGreen / iImageWidth) > iBorderColorLimit || (iSumBlue / iImageWidth > iBorderColorLimit)) {
										System.out.println("Bottom Border Missing");
                                        QRCodeErrors.append("Bottom Border Missing");
									}


									//left border -> x=0 y=0toheight-1
									iSumRed = 0;
									iSumGreen = 0;
									iSumBlue = 0;
									for (int y = 0; y < iImageHeight; y++) {
										Color pixelColor = new Color(renderedImage.getRGB(0, y));
										iSumRed = iSumRed + pixelColor.getRed();
										iSumGreen = iSumGreen + pixelColor.getGreen();
										iSumBlue = iSumBlue + pixelColor.getBlue();
									}
									if ((iSumRed / iImageHeight) > iBorderColorLimit || (iSumGreen / iImageHeight) > iBorderColorLimit || (iSumBlue / iImageHeight > iBorderColorLimit)) {
										System.out.println("Left Border Missing");
                                        QRCodeErrors.append("Left Border Missing");
									}

									//Code to Check Missing Border - END

								}
							} catch (Exception re) {
								//do nothing
							}

						}
					}
				}catch (Exception rex) {
					//do nothing
				}
*/
				// readfing each QR image for issues - END




                // get Binary Bitmap
                BinaryBitmap binaryBitmap = QRDataExtractorJobs.getBinaryBitmap(page, dpi, pdfRenderer);

                Result[] results = null;
                try {
                    // extracting QR data for each image
                    results = QRDataExtractorJobs.extractQRContent(binaryBitmap);
                } catch (NotFoundException e) {
                  //  log.info("No readable QR codes are found in PDF:" + pdfFileName + " Page:" + (page + 1) + " [Book:"
                    //        + packetName + "]");
                }
                int detectedQRNumber = 0;
                try {
                    // detecting no. of QR codes for each image
                    detectedQRNumber = QRDataExtractorJobs.detectQRCode(binaryBitmap);
                } catch (NotFoundException e) {
                   // log.info("No QR codes are detected in PDF:" + pdfFileName + " Page:" + (page + 1) + " [Book:"
                      //      + packetName + "]");
                }


                binaryBitmap = null;
                detectedQRCount += detectedQRNumber;
                if ((detectedQRNumber > 0 && results == null)
                        || (results != null && detectedQRNumber > results.length)) {
                    unreadableQRList.add(imageName);
                    log.info("Unreadable QR code(s) are detected in PDF:" + pdfFileName + " Page:" + (page + 1) + " [Book:"
                            + packetName + "]" + " detected QR count:" + detectedQRNumber + " readable QR count: "
                            + results.length);

                }
                if (results != null) {
                    for (int iCount = 0; iCount < results.length; iCount++) {
                        readText = results[iCount].getText();
                        log.info("QR data in PDF:" + pdfFileName + " Page:" + (page + 1) + " [Book:"
                                + packetName + "]: " + readText);
                        if (readText != null
                                && readText.contains(ConfigurationService.getConfiguration().getDikshacode())) {
                            dialCode = readText.substring(readText.lastIndexOf("/") + 1);
                            QRDataList.add(dialCode);
                            if (qrCodeMap.get(dialCode) != null) {
                                String temp = duplicateQRCodeMap.get(dialCode);
                                duplicateQRCodeMap.put(dialCode,
                                        (temp == null) ? qrCodeMap.get(dialCode) + "," + imageName
                                                : temp + "," + imageName);
                                if (!QRExtractionService.isDuplicateFound)
                                    QRExtractionService.isDuplicateFound = true;
                                packetAttributes.setDuplicateFound(true);
                            } else {
                                qrCodeMap.put(dialCode, imageName);
                            }
                            writetoCSV(readText, imageName, true); // writing data for SB QR images
                        } else if (readText != null) {
                            writetoCSV(readText, imageName, false); // writing data for Non-SB QR images
                            externalQRCount++;
                        }
                    }
                }

            }

        } catch (Exception exception) {
            log.error("Error in QR extraction task for book: " + packetName, exception);
        }

        long extractionEndTime = System.currentTimeMillis();
        fileExecutionData.setExtractionTime(extractionEndTime - extractionStartTime);
        packetAttributes.setDetectedQRCount(detectedQRCount + packetAttributes.getDetectedQRCount());
        packetAttributes.setExternalQRCount(externalQRCount + packetAttributes.getExternalQRCount());
        packetStatusMap.put(fileExecutionData.getPdfFile().getName(), "true"); // marking true if PDF has finished
        // running

        Set<String> packetStatusSet = new HashSet<>(packetStatusMap.values());
        // checking if packet has finished running
        if (packetStatusSet.size() == 1 && packetStatusSet.stream().findFirst().get().equals("true")) {
            System.out.println("Payload generated for book: " + packetName);
            log.info("Payload generated for book: " + packetName);

        }
    }

    synchronized private void writetoCSV(String readText, String imageName, boolean isSBCode) {
        try {
            if (isSBCode) {
                validDatacsvPrinter.printRecord(readText, readText.substring(readText.lastIndexOf("/") + 1),
                        imageName.substring(0, imageName.lastIndexOf("_")),
                        imageName.substring(imageName.lastIndexOf("_") + 1, imageName.length()));
            } else {
                invalidDatacsvPrinter.printRecord(readText, imageName.substring(0, imageName.lastIndexOf("_")),
                        imageName.substring(imageName.lastIndexOf("_") + 1, imageName.length()));
            }

        } catch (IOException exception) {
            log.info("Error writing QR data for PDF_page-" + imageName + " in book: " + packetName + " to CSV file: "
                    + exception);
        }
    }
}
