package reverseverification.domain;

/**
 * fileName    	:: Configuration.java
 * @author 	   	:: ravi
 * description 	:: JSON object model class for mapping Configuration.json
 * @version     :: RVTool-3.0.0
 * 
 * 
 */

public class Configuration {

	private int dpi;
	private int threadpoolsize;
	private String dikshacode;
	private String requesturl;
	private boolean openReportWindow;

	public int getDpi() {
		return dpi;
	}

	public void setDpi(int dpi) {
		this.dpi = dpi;
	}

	public int getThreadpoolsize() {
		return threadpoolsize;
	}

	public void setThreadpoolsize(int threadpoolsize) {
		this.threadpoolsize = threadpoolsize;
	}

	public String getDikshacode() {
		return dikshacode;
	}

	public void setDikshacode(String dikshacode) {
		this.dikshacode = dikshacode;
	}

	public String getRequesturl() {
		return requesturl;
	}

	public void setRequesturl(String requesturl) {
		this.requesturl = requesturl;
	}

	public boolean isOpenReportWindow() {
		return openReportWindow;
	}

	public void setOpenReportWindow(boolean openReportWindow) {
		this.openReportWindow = openReportWindow;
	}
	
	

}
