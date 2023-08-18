package reverseverification.domain;

/**
 * JSON object model class
 * for mapping Additional Used dials
 * and packet names
 * @author ravi
 *
 */
import java.util.List;

public class AdditionalUsedFrom {
	
	private String packetName;
	private List<String> additionalUsedCodes;
	
	public String getPacketName() {
		return packetName;
	}
	public void setPacketName(String packetName) {
		this.packetName = packetName;
	}
	public List<String> getAdditionalUsedCodes() {
		return additionalUsedCodes;
	}
	public void setAdditionalUsedCodes(List<String> additionalUsedCodes) {
		this.additionalUsedCodes = additionalUsedCodes;
	}
	
	

}
