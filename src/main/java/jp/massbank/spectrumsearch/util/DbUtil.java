package jp.massbank.spectrumsearch.util;

import jp.massbank.spectrumsearch.db.accessor.InstrumentAccessor;

public class DbUtil {

	public static void createSchemaIfNotExist() {
		InstrumentAccessor instrumentAccessor = new InstrumentAccessor();
		instrumentAccessor.createTable();
	}
	
}
