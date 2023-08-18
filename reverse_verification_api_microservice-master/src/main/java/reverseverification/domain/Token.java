package reverseverification.domain;

/**
 * fileName    	:: Token.java
 * @author 	   	:: ravi
 * description 	:: This is domain class for token
 * @version     :: RVTool-API-3.0.0
 * 
 * 
 */
public class Token {
	String errmsg;
	String status;
	String token;

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
