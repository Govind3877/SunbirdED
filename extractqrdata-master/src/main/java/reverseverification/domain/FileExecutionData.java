package reverseverification.domain;

/**
 * fileName    	:: FileExecutionData.java
 * @author 	   	:: ravi
 * description 	:: JSON object model class to map individual file & it's extraction time
 * @version     :: RVTool-3.0.0
 * 
 * 
 */
import java.io.File;

public class FileExecutionData {

	private File pdfFile;
	private long extractionTime;

	public File getPdfFile() {
		return pdfFile;
	}

	public void setPdfFile(File pdfFile) {
		this.pdfFile = pdfFile;
	}

	public long getExtractionTime() {
		return extractionTime;
	}

	public void setExtractionTime(long extractionTime) {
		this.extractionTime = extractionTime;
	}

}
