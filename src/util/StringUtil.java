package util;

public class StringUtil {

	public static boolean isValidate(String str) {
		if(str == null || str.trim().equals("")) {
			return false;
		}
		return true;
	}
	
}
