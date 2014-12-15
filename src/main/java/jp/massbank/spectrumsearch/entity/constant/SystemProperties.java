package jp.massbank.spectrumsearch.entity.constant;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SystemProperties {
	
	private static final Logger LOGGER = Logger.getLogger(SystemProperties.class);
	
	private static SystemProperties INSTANCE = new SystemProperties();
	private static final String STS_PROPS_FILE_PATH;
	
	private static Properties props;
	
	static {
		URL classPath = SystemProperties.class.getClassLoader().getResource(Constant.SYS_PROPERTIES_FILE_NAME);
		STS_PROPS_FILE_PATH = classPath.getPath();
	}
	
	public static final class Key {
		public static final String DIR_PATH = "massbank.record.dir.path"; 
		public static final String DATABASE_NAME = "massbank.db.name"; 
		public static final String CUTOFF_THRESHOLD = "cutoff.threshold"; 
		public static final String TOLERANCE = "tolerance"; 
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
			props.load(new FileInputStream(STS_PROPS_FILE_PATH));
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public static void updateParam(String key, String value) {
		try {
			props.setProperty(key, value);
			OutputStream output = new FileOutputStream(STS_PROPS_FILE_PATH);
			props.store(output, null);
			output.close();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public String getDirPath() {
		return props.getProperty(Key.DIR_PATH);
	}
	
	public String getDatabasePath() {
		return getDirPath() + "/" + props.getProperty(Key.DATABASE_NAME);
	}
	
	public int getCutoffThreshold() {
		return Integer.parseInt(props.getProperty(Key.CUTOFF_THRESHOLD));
	}
	
	public float getTolerance() {
		return Float.parseFloat(props.getProperty(Key.TOLERANCE));
	}
	
}
