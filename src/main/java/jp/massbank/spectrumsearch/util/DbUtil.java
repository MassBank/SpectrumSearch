package jp.massbank.spectrumsearch.util;

import org.apache.commons.lang3.StringUtils;

public class DbUtil {
	
	public static String concat(String data) {
		return StringUtils.join(data.split("||"));
	}
	
	public static String lpad(String str, int size, String padChar) {
		return StringUtils.leftPad(str, size, padChar);
	}
	
	public static String castDouble(Double val) {
		return String.valueOf(val);
	}
	
	public static boolean regexplike(String val, String pattern) {
		return val.matches(pattern);
	}
	
	public static String castInteger(Integer val) {
		return String.valueOf(val);
	}

}
