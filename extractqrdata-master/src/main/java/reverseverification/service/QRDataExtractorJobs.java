package reverseverification.service;

/**
 * fileName    	:: QRDataExtractorJobs.java
 * @author 	   	:: ravi
 * description 	:: This class has methods to get binary bitmap of pdf pages, detect & read qrcode
 * @version     :: RVTool-3.0.0
 * 
 * 
 */

import java.io.IOException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import com.google.zxing.multi.qrcode.detector.MultiDetector;

//import reverseverification.service.QRCodeMultiReaderCustom;

class QRDataExtractorJobs {
	
	private static Logger log = null;

	private static Map<DecodeHintType, Object> HINTS = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
	static {
		HINTS.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		HINTS.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.allOf(BarcodeFormat.class));
	}

	// QR Extraction from images
	protected static Result[] extractQRContent(BinaryBitmap bitmap) throws NotFoundException {
		return new QRCodeMultiReader().decodeMultiple(bitmap, HINTS);
	}

	// getting binary bitmap of an image
	protected static BinaryBitmap getBinaryBitmap(int page, int dpi, PDFRenderer pdfRenderer) {
		
		ch.qos.logback.classic.Logger  root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		   root.setLevel(ch.qos.logback.classic.Level.INFO);
		   log = root.getLoggerContext().getLogger(QRDataExtractorJobs.class);
		
		BinaryBitmap binaryBitmap = null;
		try {
			binaryBitmap = new BinaryBitmap(new HybridBinarizer(
					new BufferedImageLuminanceSource(pdfRenderer.renderImageWithDPI(page, dpi, ImageType.RGB))));
		} catch (IOException e) {
			log.error("Error in getting binary bit map from page: " + page);
		}
		return binaryBitmap;
	}

	// QR detection method
	protected static int detectQRCode(BinaryBitmap binaryBitmap) throws NotFoundException {
		int QRCounter = 0;
		MultiDetector detector = new MultiDetector(binaryBitmap.getBlackMatrix());
		DetectorResult[] detectorResult = detector.detectMulti(HINTS);

		if (detectorResult != null)
			QRCounter = detectorResult.length;

		return QRCounter;
	}

}