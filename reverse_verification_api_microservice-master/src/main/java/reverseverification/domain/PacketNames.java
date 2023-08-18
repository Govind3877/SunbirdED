package reverseverification.domain;

/**
 * fileName    	:: PacketNames.java
 * @author 	   	:: ravi
 * description 	:: This is domain class for mapping packet list
 * @version     :: RVTool-API-3.0.0
 * 
 * 
 */
import java.util.ArrayList;

public class PacketNames {
	private String errmsg;
	private ArrayList<String> packetNameList;

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public ArrayList<String> getPacketNameList() {
		return packetNameList;
	}

	public void setPacketNameList(ArrayList<String> packetNameList) {
		this.packetNameList = packetNameList;
	}

}
