package reverseverification.domain;

/**
 * fileName    	:: Response.java
 * @author 	   	:: ravi
 * description 	:: This is domain class for rv response
 * @version     :: RVTool-API-3.0.0
 * 
 * 
 */
public class Response {

	private String packetName;
	private int dialCodeCount;
	private int usedCount;
	private String[] usedDials;
	private int unusedCount;
	private String[] unusedDials;
	private int additionalCount;
	private String[] additionalDials;
	private String additionalUsedFrom;
	private int dupCount;
	private String[] dupDials;

	public String getPacketName() {
		return packetName;
	}

	public void setPacketName(String packetName) {
		this.packetName = packetName;
	}

	public int getDialCodeCount() {
		return dialCodeCount;
	}

	public void setDialCodeCount(int dialCodeCount) {
		this.dialCodeCount = dialCodeCount;
	}

	public int getUsedCount() {
		return usedCount;
	}

	public void setUsedCount(int usedCount) {
		this.usedCount = usedCount;
	}

	public String[] getUsedDials() {
		return usedDials;
	}

	public void setUsedDials(String[] usedDials) {
		this.usedDials = usedDials;
	}

	public int getUnusedCount() {
		return unusedCount;
	}

	public void setUnusedCount(int unusedCount) {
		this.unusedCount = unusedCount;
	}

	public String[] getUnusedDials() {
		return unusedDials;
	}

	public void setUnusedDials(String[] unusedDials) {
		this.unusedDials = unusedDials;
	}

	public int getAdditionalCount() {
		return additionalCount;
	}

	public void setAdditionalCount(int additionalCount) {
		this.additionalCount = additionalCount;
	}

	public String[] getAdditionalDials() {
		return additionalDials;
	}

	public void setAdditionalDials(String[] additionalDials) {
		this.additionalDials = additionalDials;
	}

	public String getAdditionalUsedFrom() {
		return additionalUsedFrom;
	}

	public void setAdditionalUsedFrom(String additionalUsedFrom) {
		this.additionalUsedFrom = additionalUsedFrom;
	}

	public int getDupCount() {
		return dupCount;
	}

	public void setDupCount(int dupCount) {
		this.dupCount = dupCount;
	}

	public String[] getDupDials() {
		return dupDials;
	}

	public void setDupDials(String[] dupDials) {
		this.dupDials = dupDials;
	}

}
