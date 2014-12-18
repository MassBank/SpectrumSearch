package jp.massbank.spectrumsearch.util;

import org.apache.log4j.Logger;

public class CommonUtil {
	
	private static final Logger LOGGER = Logger.getLogger(CommonUtil.class);
	
	public static Float parseFloat(String str) {
		try {
			return Float.parseFloat(str);
		} catch (NumberFormatException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static Integer parseInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

}
