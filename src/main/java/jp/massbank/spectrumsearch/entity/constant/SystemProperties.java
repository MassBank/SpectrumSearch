package jp.massbank.spectrumsearch.entity.constant;

public class SystemProperties {
	
	private static SystemProperties INSTANCE = new SystemProperties();
	
	private static final String PATH_NAME = "C:/Apps/Documents/Projects/proj-massbank/massbankrecord";
	private static final String DATABASE_NAME = "massbankdb";
	
	private SystemProperties() {
		
	}

	public static SystemProperties getInstance() {
		return INSTANCE;
	}
	
	public String getFolderPath() {
		return PATH_NAME;
	}
	
	public String getDatabasePath() {
		return PATH_NAME + "/" + DATABASE_NAME;
	}
	
	public String getDatabaseName() {
		return DATABASE_NAME;
	}
	
}
