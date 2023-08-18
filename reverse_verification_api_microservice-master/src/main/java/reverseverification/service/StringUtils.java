package reverseverification.service;

/**
 * fileName    	:: StringUtils.java
 * @author 	   	:: ravi
 * description 	:: This class is used to check if string is blank
 * @version     :: RVTool-API-3.0.0
 * 
 * 
 */
public class StringUtils {

	public static boolean isBlank(final CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(cs.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
}
