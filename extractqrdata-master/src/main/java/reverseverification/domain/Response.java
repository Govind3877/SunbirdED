package reverseverification.domain;

/**
 * fileName    	:: Response.java
 * @author 	   	:: ravi
 * description 	:: JSON object model class to receive rv reponses
 * @version     :: RVTool-3.0.0
 * 
 * 
 */
import java.util.List;

public class Response {

	private String packetName;
	private List<String> usedDials;
	private List<String> unusedDials;
	private List<String> dupDials;
	private List<String> additionalDials;
	private String additionalUsedFrom;
	private long dupCount;
	private long additionalCount;
	private long unusedCount;
	private long usedCount;
	private int dialCodeCount;

	public String getPacketName() {
		return packetName;
	}

	public void setPacketName(String packetName) {
		this.packetName = packetName;
	}

	public List<String> getUsedDials() {
		return usedDials;
	}

	public void setUsedDials(List<String> usedDials) {
		this.usedDials = usedDials;
	}

	public List<String> getUnusedDials() {
		return unusedDials;
	}

	public void setUnusedDials(List<String> unusedDials) {
		this.unusedDials = unusedDials;
	}

	public List<String> getDupDials() {
		return dupDials;
	}

	public void setDupDials(List<String> dupDials) {
		this.dupDials = dupDials;
	}

	public List<String> getAdditionalDials() {
		return additionalDials;
	}

	public void setAdditionalDials(List<String> additionalDials) {
		this.additionalDials = additionalDials;
	}

	public String getAdditionalUsedFrom() {
		return additionalUsedFrom;
	}

	public void setAdditionalUsedFrom(String additionalUsedFrom) {
		this.additionalUsedFrom = additionalUsedFrom;
	}

	public long getDupCount() {
		return dupCount;
	}

	public void setDupCount(long dupCount) {
		this.dupCount = dupCount;
	}

	public long getAdditionalCount() {
		return additionalCount;
	}

	public void setAdditionalCount(long additionalCount) {
		this.additionalCount = additionalCount;
	}

	public long getUnusedCount() {
		return unusedCount;
	}

	public void setUnusedCount(long unusedCount) {
		this.unusedCount = unusedCount;
	}

	public long getUsedCount() {
		return usedCount;
	}

	public void setUsedCount(long usedCount) {
		this.usedCount = usedCount;
	}

	public int getDialCodeCount() {
		return dialCodeCount;
	}

	public void setDialCodeCount(int dialCodeCount) {
		this.dialCodeCount = dialCodeCount;
	}

}
