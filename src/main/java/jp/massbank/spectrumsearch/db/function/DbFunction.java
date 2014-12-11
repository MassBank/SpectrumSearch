package jp.massbank.spectrumsearch.db.function;

import org.apache.commons.lang3.StringUtils;

public class DbFunction {

	public static String concat(String data) {
		String[] args = data.split("||");
		return StringUtils.join(args);
	}
	
	public static String lpad(String str, int size, String padChar) {
		return StringUtils.leftPad(str, size, padChar);
	}
	
	public static String castDouble(Double val) {
		return String.valueOf(val);
	}
	
	public static String castInteger(Integer val) {
		return String.valueOf(val);
	}
}
