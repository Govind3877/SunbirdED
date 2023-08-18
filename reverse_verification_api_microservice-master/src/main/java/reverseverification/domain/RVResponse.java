package reverseverification.domain;

/**
 * fileName    	:: RVResponse.java
 * @author 	   	:: ravi
 * description 	:: This is domain class for rv response
 * @version     :: RVTool-API-3.0.0
 * 
 * 
 */
public class RVResponse {
	private String errmsg;
	private Response response;

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}
