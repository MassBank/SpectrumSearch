package jp.massbank.spectrumsearch.entity.constant;

public final class Constant {

	public static final String ENCODING = "UTF-8";
	
	// user file names
	public static final String SYS_PROPERTIES_FILE_NAME = "system.properties";
	public static final String SERVERS_CONFIG_FILE_NAME = "massbank.conf";

	// seperator
	public static class Seperator {
		public static final String DOLLAR = "$";
	}
	
	// massbank site external urls
	public static final class ExternalUrl {
		public static final String SHOW_RECORD_PAGE = ""; 
		public static final String SHOW_MULTIPLE_RECORD_PAGE = ""; 
		public static final String SHOW_PACKAGE_VIEW_RECORD_PAGE = ""; 
		public static final String SHOW_PACKAGE_VIEW_SEARCH_PAGE = ""; 
		public static final String SHOW_SEARCH_PAGE = ""; 
		public static final String SHOW_DISPATCH_PAGE = ""; 
	}
	
	// param defaults
	public static final boolean PARAM_WEIGHT_LINEAR = true;
	public static final boolean PARAM_WEIGHT_SQUARE = false;
	public static final boolean PARAM_NORM_LOG = true;
	public static final boolean PARAM_NORM_SQRT = false;
	
}
