package reverseverification.domain;
/**
 * fileName    	:: Configuration.java
 * @author 	   	:: ravi
 * description 	:: This is domain class to map environment variables
 * @version     :: RVTool-3.0.0
 * 
 * 
 */
public class Configuration {

	private String sb_base_url = "";
	private String sb_content_read_api = "";
	private String sb_org_read_api = "";
	private String sb_dialCode_search_api = "";
	private String sb_api_token = "";

	private String qr_db_url = "";
	private String qr_db_user = "";
	private String qr_db_pwd = "";
	private String qr_db_schema = "";

	public String getBaseUrl() {
		return sb_base_url;
	}

	public void setBaseUrl(String sb_base_url) {
		this.sb_base_url = sb_base_url;
	}

	public String getContentReadAPI() {
		return sb_content_read_api;
	}

	public void setContentReadAPI(String sb_content_read_api) {
		this.sb_content_read_api = sb_content_read_api;
	}

	public String getOrgReadAPI() {
		return sb_org_read_api;
	}

	public void setOrgReadAPI(String sb_org_read_api) {
		this.sb_org_read_api = sb_org_read_api;
	}

	public String getDialCodeSearchAPI() {
		return sb_dialCode_search_api;
	}

	public void setDialCodeSearchAPI(String sb_dialCode_search_api) {
		this.sb_dialCode_search_api = sb_dialCode_search_api;
	}

	public String getAPIToken() {
		return sb_api_token;
	}

	public void setAPIToken(String sb_api_token) {
		this.sb_api_token = sb_api_token;
	}

	public String getDBUrl() {
		return qr_db_url;
	}

	public void setDBUrl(String qr_db_url) {
		this.qr_db_url = qr_db_url;
	}

	public String getDBUser() {
		return qr_db_user;
	}

	public void setDBUser(String qr_db_user) {
		this.qr_db_user = qr_db_user;
	}

	public String getDBPwd() {
		return qr_db_pwd;
	}

	public void setDBPwd(String qr_db_password) {
		this.qr_db_pwd = qr_db_password;
	}

	public String getDBSchema() {
		return qr_db_schema;
	}

	public void setDBSchema(String qr_db_schema) {
		this.qr_db_schema = qr_db_schema;
	}

}
