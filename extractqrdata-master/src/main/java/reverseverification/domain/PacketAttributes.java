package reverseverification.domain;

/**
 * fileName    	:: PacketAttributes.java
 * @author 	   	:: ravi
 * description 	:: This is class is used to track duplicate dial, detected and external QR count in each packet
 * @version     :: RVTool-3.0.0
 * 
 * 
 */

public class PacketAttributes {

	private boolean isDuplicateFound;
	private int detectedQRCount;
	private int externalQRCount;

	public boolean isDuplicateFound() {
		return isDuplicateFound;
	}

	public void setDuplicateFound(boolean isDuplicateFound) {
		this.isDuplicateFound = isDuplicateFound;
	}

	public int getDetectedQRCount() {
		return detectedQRCount;
	}

	public void setDetectedQRCount(int detectedQRCount) {
		this.detectedQRCount = detectedQRCount;
	}

	public int getExternalQRCount() {
		return externalQRCount;
	}

	public void setExternalQRCount(int externalQRCount) {
		this.externalQRCount = externalQRCount;
	}

}
