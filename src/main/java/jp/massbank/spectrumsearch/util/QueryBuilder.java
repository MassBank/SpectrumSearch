package jp.massbank.spectrumsearch.util;


public class QueryBuilder {
	
	public static String getDropTable(String table) {
		return "DROP TABLE " + table;
	}
	
	public static String getDeleteAll(String table) {
		return "DELETE FROM " + table;
	}

}
