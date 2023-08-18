package reverseverification.domain;

/**
 * fileName    	:: Request.java
 * @author 	   	:: ravi
 * description 	:: JSON object model class request to send for rv
 * @version     :: RVTool-3.0.0
 * 
 * 
 */
import java.util.LinkedList;
import java.util.List;

public class Request {
	private String packetName;
	private List<String> dialList;
	private LinkedList<String> fileExecutionData;
	private LinkedList<String> pdfCheckSum;
	private String rvFileName;
	
	public String getPacketName() {
		return packetName;
	}

	public void setPacketName(String packetName) {
		this.packetName = packetName;
	}

	public List<String> getDialList() {
		return dialList;
	}

	public void setDialList(List<String> dialList) {
		this.dialList = dialList;
	}

	public LinkedList<String> getFileExecutionData() {
		return fileExecutionData;
	}

	public void setFileExecutionData(LinkedList<String> fileExecutionData) {
		this.fileExecutionData = fileExecutionData;
	}

	public LinkedList<String> getPdfCheckSum() {
		return pdfCheckSum;
	}

	public void setPdfCheckSum(LinkedList<String> pdfCheckSum) {
		this.pdfCheckSum = pdfCheckSum;
	}

	public String getRvFileName() {
		return rvFileName;
	}

	public void setRvFileName(String rvFileName) {
		this.rvFileName = rvFileName;
	}

	
}
