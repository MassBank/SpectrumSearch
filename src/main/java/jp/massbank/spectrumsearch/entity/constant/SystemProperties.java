package jp.massbank.spectrumsearch.entity.constant;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class SystemProperties {
	
	private static final Logger LOGGER = Logger.getLogger(SystemProperties.class);
	
	private static SystemProperties INSTANCE = new SystemProperties();
	private static final String SYS_PROPS_FILE_PATH;
	
	private static Properties props;
	
	static {
		URL classPath = SystemProperties.class.getClassLoader().getResource("config/" + Constant.SYS_PROPERTIES_FILE_NAME);
		SYS_PROPS_FILE_PATH = classPath.getPath();
		LOGGER.debug("system.properties file path : " + SYS_PROPS_FILE_PATH);
	}
	
	public static final class Key {
		public static final String DIR_PATH = "massbank.record.dir.path"; 
		public static final String DATABASE_NAME = "massbank.db.name"; 
		public static final String SYS_PARAM_DEFAULT_CUTOFF_THRESHOLD = "search.param.default.cutoff.threshold"; 
		public static final String SYS_PARAM_DEFAULT_TOLERANCE = "search.param.default.tolerance"; 
		public static final String SYS_PARAM_DEFAULT_TOLERANCE_UNIT = "search.param.default.tolerance.unit"; 
		public static final String SYS_PARAM_DEFAULT_INST_TYPE_LIST = "search.param.default.instance.type.list"; 
		public static final String SYS_PARAM_DEFAULT_MS_LIST = "search.param.default.ms.list"; 
		public static final String SYS_PARAM_DEFAULT_ION_LIST = "search.param.default.ion.list"; 
	}

	private SystemProperties() {
	}

	public static SystemProperties getInstance() {
		if (props == null) {
			loadParams();
		}
		return INSTANCE;
	}
	
	public static void loadParams() {
		props = new Properties();
		try {
			props.load(new FileInputStream(SYS_PROPS_FILE_PATH));
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	// setters
	public static void updateParam(String key, String value) {
		try {
			props.setProperty(key, value);
			OutputStream output = new FileOutputStream(SYS_PROPS_FILE_PATH);
			props.store(output, null);
			output.close();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public static void setDefaultCutoffThreshold(int value) {
		updateParam(Key.SYS_PARAM_DEFAULT_CUTOFF_THRESHOLD, String.valueOf(value));
	}
	
	public static void setDefaultTolerance(float value) {
		updateParam(Key.SYS_PARAM_DEFAULT_TOLERANCE, String.valueOf(value));
	}
	
	public static void setDefaultToleranceUnit(String unit) {
		updateParam(Key.SYS_PARAM_DEFAULT_TOLERANCE_UNIT, unit);
	}
	
	public static void setDefaultInstanceTypeList(List<String> valueSetList) {
		updateParam(Key.SYS_PARAM_DEFAULT_INST_TYPE_LIST, toValue(valueSetList));
	}
	
	public static void setDefaultMsList(List<String> valueSetList) {
		updateParam(Key.SYS_PARAM_DEFAULT_MS_LIST, toValue(valueSetList));
	}
	
	public static void setDefaultIonList(List<String> valueSetList) {
		updateParam(Key.SYS_PARAM_DEFAULT_ION_LIST, toValue(valueSetList));
	}
	
	// getters
	public String getDirPath() {
		return props.getProperty(Key.DIR_PATH);
	}
	
	public String getDatabasePath() {
		return getDirPath() + "/" + getDatabaseName();
	}
	
	public String getDatabaseName() {
		return props.getProperty(Key.DATABASE_NAME);
	}
	
	public int getDefaultCutoffThreshold() {
		return Integer.parseInt(props.getProperty(Key.SYS_PARAM_DEFAULT_CUTOFF_THRESHOLD));
	}
	
	public float getDefaultTolerance() {
		return Float.parseFloat(props.getProperty(Key.SYS_PARAM_DEFAULT_TOLERANCE));
	}
	
	public String getDefaultToleranceUnit() {
		return props.getProperty(Key.SYS_PARAM_DEFAULT_TOLERANCE_UNIT);
	}
	
	public String[] getDefaultInstanceTypeList() {
		return toArray(props.getProperty(Key.SYS_PARAM_DEFAULT_INST_TYPE_LIST));
	}
	
	public String[] getDefaultMsList() {
		return toArray(props.getProperty(Key.SYS_PARAM_DEFAULT_MS_LIST));
	}
	
	public String[] getDefaultIonList() {
		return toArray(props.getProperty(Key.SYS_PARAM_DEFAULT_ION_LIST));
	}
	
	// private
	private static String toValue(List<String> list) {
		StringBuilder sb = new StringBuilder();
		for (String item : list) {
			sb.append(item);
			sb.append(",");
		}
		String result = sb.toString();
		return result.substring(0, result.length() - 1);
	}
	
	private String[] toArray(String value) {
		if (StringUtils.isNotBlank(value)) {
			return value.split(",");
		}
		return new String[0];
	}
	
}
