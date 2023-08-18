package reverseverification.domain;

/**
 * fileName    	:: QRPacket.java
 * @author 	   	:: ravi
 * description 	:: This domain class for mapping packet details
 * @version     :: RVTool-API-3.0.0
 * 
 * 
 */
import java.sql.Date;

public class QRPacket {
	String packetName;
	String bookName;
	String medium;
	String grade;
	String subject;
	String createdFor;
	String po;
	String astPO;

	String packetSentTo;
	String packetSentFlag;
	String createdTime;
	String reverseVerificationFlag;
	String rvFileName;
	Date packetSentOn;
	Date reverseVerifiedOn;

	String issuedDials;
	String usedDials;
	String unusedDials;
	String additionalDials;
	String duplicateDials;
	String externalDials;
	String additionalUsedFrom;

	String apiRequest;
	String mac;
	String systemSerial;
	String contentType;
	String channel;

	int issuedDialCount;
	int usedDialCount;
	int unusedDialCount;
	int additionalUsedDialCount;
	int duplicateDialCount;
	int externalDialCount;
	int noOfTimeVerified;

	public String getPacketName() {
		return packetName;
	}

	public void setPacketName(String packetName) {
		this.packetName = packetName;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getMedium() {
		return medium;
	}

	public void setMedium(String medium) {
		this.medium = medium;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getCreatedFor() {
		return createdFor;
	}

	public void setCreatedFor(String createdFor) {
		this.createdFor = createdFor;
	}

	public String getPo() {
		return po;
	}

	public void setPo(String po) {
		this.po = po;
	}

	public String getAstPO() {
		return astPO;
	}

	public void setAstPO(String astPO) {
		this.astPO = astPO;
	}

	public String getPacketSentTo() {
		return packetSentTo;
	}

	public void setPacketSentTo(String packetSentTo) {
		this.packetSentTo = packetSentTo;
	}

	public String getPacketSentFlag() {
		return packetSentFlag;
	}

	public void setPacketSentFlag(String packetSentFlag) {
		this.packetSentFlag = packetSentFlag;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public String getReverseVerificationFlag() {
		return reverseVerificationFlag;
	}

	public void setReverseVerificationFlag(String reverseVerificationFlag) {
		this.reverseVerificationFlag = reverseVerificationFlag;
	}
	
	public String getRvFileName() {
		return rvFileName;
	}

	public void setRvFileName(String rvFileName) {
		this.rvFileName = rvFileName;
	}

	public Date getPacketSentOn() {
		return packetSentOn;
	}

	public void setPacketSentOn(Date packetSentOn) {
		this.packetSentOn = packetSentOn;
	}

	public Date getReverseVerifiedOn() {
		return reverseVerifiedOn;
	}

	public void setReverseVerifiedOn(Date reverseVerifiedOn) {
		this.reverseVerifiedOn = reverseVerifiedOn;
	}

	public String getIssuedDials() {
		return issuedDials;
	}

	public void setIssuedDials(String issuedDials) {
		this.issuedDials = issuedDials;
	}

	public String getUsedDials() {
		return usedDials;
	}

	public void setUsedDials(String usedDials) {
		this.usedDials = usedDials;
	}

	public String getUnusedDials() {
		return unusedDials;
	}

	public void setUnusedDials(String unusedDials) {
		this.unusedDials = unusedDials;
	}

	public String getAdditionalDials() {
		return additionalDials;
	}

	public void setAdditionalDials(String additionalDials) {
		this.additionalDials = additionalDials;
	}

	public String getDuplicateDials() {
		return duplicateDials;
	}

	public void setDuplicateDials(String duplicateDials) {
		this.duplicateDials = duplicateDials;
	}

	public String getExternalDials() {
		return externalDials;
	}

	public void setExternalDials(String externalDials) {
		this.externalDials = externalDials;
	}

	public String getAdditionalUsedFrom() {
		return additionalUsedFrom;
	}

	public void setAdditionalUsedFrom(String additionalUsedFrom) {
		this.additionalUsedFrom = additionalUsedFrom;
	}

	public String getApiRequest() {
		return apiRequest;
	}

	public void setApiRequest(String apiRequest) {
		this.apiRequest = apiRequest;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getSystemSerial() {
		return systemSerial;
	}

	public void setSystemSerial(String systemSerial) {
		this.systemSerial = systemSerial;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public int getIssuedDialCount() {
		return issuedDialCount;
	}

	public void setIssuedDialCount(int issuedDialCount) {
		this.issuedDialCount = issuedDialCount;
	}

	public int getUsedDialCount() {
		return usedDialCount;
	}

	public void setUsedDialCount(int usedDialCount) {
		this.usedDialCount = usedDialCount;
	}

	public int getUnusedDialCount() {
		return unusedDialCount;
	}

	public void setUnusedDialCount(int unusedDialCount) {
		this.unusedDialCount = unusedDialCount;
	}

	public int getAdditionalUsedDialCount() {
		return additionalUsedDialCount;
	}

	public void setAdditionalUsedDialCount(int additionalUsedDialCount) {
		this.additionalUsedDialCount = additionalUsedDialCount;
	}

	public int getDuplicateDialCount() {
		return duplicateDialCount;
	}

	public void setDuplicateDialCount(int duplicateDialCount) {
		this.duplicateDialCount = duplicateDialCount;
	}

	public int getExternalDialCount() {
		return externalDialCount;
	}

	public void setExternalDialCount(int externalDialCount) {
		this.externalDialCount = externalDialCount;
	}

	public int getNoOfTimeVerified() {
		return noOfTimeVerified;
	}

	public void setNoOfTimeVerified(int noOfTimeVerified) {
		this.noOfTimeVerified = noOfTimeVerified;
	}

}