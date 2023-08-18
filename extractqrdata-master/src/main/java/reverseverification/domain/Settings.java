package reverseverification.domain;

/**
 * fileName    	:: Settings.java
 * @author 	   	:: ravi
 * description 	:: JSON mapping file for settings.java
 * @version     :: RVTool-3.0.0
 * 
 * 
 */
public class Settings {

	private String booksDir;
	private String token;
	private String isReverseVerificationRequired;
	private String maxBookLimit;

	public String getBooksDir() {
		return booksDir;
	}

	public void setBooksDir(String booksDir) {
		this.booksDir = booksDir;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getIsReverseVerificationRequired() {
		return isReverseVerificationRequired;
	}

	public void setIsReverseVerificationRequired(String isReverseVerificationRequired) {
		this.isReverseVerificationRequired = isReverseVerificationRequired;
	}

	public String getMaxBookLimit() {
		return maxBookLimit;
	}

	public void setMaxBookLimit(String maxBookLimit) {
		this.maxBookLimit = maxBookLimit;
	}

	

}
