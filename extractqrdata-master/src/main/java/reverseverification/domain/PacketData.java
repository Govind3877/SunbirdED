package reverseverification.domain;

/**
 * fileName    	:: PacketData.java
 * @author 	   	:: ravi
 * description 	:: JSON object model class for mapping packet reletad data to send for RV
 * @version     :: RVTool-3.0.0
 * 
 * 
 */


import java.util.List;

public class PacketData {
	private String packetName;
	private List<String> dialCodeList;
	private List<FileExecutionData> fileExecutionData;
	private List<String> unreadableQRPageList;
	private PacketAttributes packetAttributes;

	public String getPacketName() {
		return packetName;
	}

	public void setPacketName(String packetName) {
		this.packetName = packetName;
	}

	public List<String> getDialCodeList() {
		return dialCodeList;
	}

	public void setDialCodeList(List<String> dialCodeList) {
		this.dialCodeList = dialCodeList;
	}

	public List<FileExecutionData> getFileExecutionData() {
		return fileExecutionData;
	}

	public void setFileExecutionData(List<FileExecutionData> fileExecutionData) {
		this.fileExecutionData = fileExecutionData;
	}

	public List<String> getUnreadableQRPageList() {
		return unreadableQRPageList;
	}

	public void setUnreadableQRPageList(List<String> unreadableQRPageList) {
		this.unreadableQRPageList = unreadableQRPageList;
	}

	public PacketAttributes getPacketAttributes() {
		return packetAttributes;
	}

	public void setPacketAttributes(PacketAttributes packetAttributes) {
		this.packetAttributes = packetAttributes;
	}

}
