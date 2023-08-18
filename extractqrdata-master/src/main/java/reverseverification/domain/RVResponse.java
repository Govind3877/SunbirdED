package reverseverification.domain;

/**
 * fileName    	:: RVResponse.java
 * @author 	   	:: ravi
 * description 	:: JSON to object mapping domain for RV API returned response
 * @version     :: RVTool-3.0.0
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
