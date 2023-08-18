package reverseverification.service;
/**
 * fileName    	:: StringUtils.java
 * @author 	   	:: ravi
 * description 	:: This is class is used to check if string is blank
 * @version     :: RVTool-3.0.0
 * 
 * 
 */
class StringUtils {

	protected static boolean isBlank(final CharSequence cs) {
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
