package jp.massbank.spectrumsearch.Logic;

import java.sql.SQLException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.massbank.spectrumsearch.accessor.DbAccessor;
import jp.massbank.spectrumsearch.accessor.RecordAccessor;
import jp.massbank.spectrumsearch.entity.constant.SystemProperties;
import jp.massbank.spectrumsearch.entity.db.Record;
import jp.massbank.spectrumsearch.logic.MassBankRecordLogic;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class FileRecordLogicTest {
	
	@Test
	public void testSync() {
		MassBankRecordLogic logic = new MassBankRecordLogic();
		logic.upgradeAndResetDatabase();
		logic.syncFilesRecordsByFolderPath(SystemProperties.getInstance().getDirPath());
	}
	
//	@Test
	public void dbExecuteSql() throws SQLException {
		DbAccessor.createConnection();
		List<String> sqls = new ArrayList<String>();
		sqls.add("select max(concat(castinteger(RELATIVE_INTENSITY) || ' ' || castdouble(MZ))), RECORD_ID, 84.1, 47.57414337506346 from PEAK where RELATIVE_INTENSITY >= 5 and (MZ between 83.799988 and 84.400008) group by RECORD_ID");
		sqls.add("select max(concat(castinteger(RELATIVE_INTENSITY) || ' ' || castdouble(MZ))), RECORD_ID, 105.1, 25.744136822888727 from PEAK where RELATIVE_INTENSITY >= 5 and (MZ between 104.799988 and 105.400008) group by RECORD_ID");
		sqls.add("select max(concat(castinteger(RELATIVE_INTENSITY) || ' ' || castdouble(MZ))), RECORD_ID, 107.1, 26.234034771445366 from PEAK where RELATIVE_INTENSITY >= 5 and (MZ between 106.799988 and 107.400008) group by RECORD_ID");
		sqls.add("select max(concat(castinteger(RELATIVE_INTENSITY) || ' ' || castdouble(MZ))), RECORD_ID, 114.1, 116.91770667603842 from PEAK where RELATIVE_INTENSITY >= 5 and (MZ between 113.799988 and 114.400008) group by RECORD_ID");
		sqls.add("select max(concat(castinteger(RELATIVE_INTENSITY) || ' ' || castdouble(MZ))), RECORD_ID, 115.1, 30.452597186643825 from PEAK where RELATIVE_INTENSITY >= 5 and (MZ between 114.799988 and 115.400008) group by RECORD_ID");
		
		sqls.add("select max(concat(castinteger(RELATIVE_INTENSITY) || ' ' || castdouble(MZ))), RECORD_ID, 84.1, 47.57414337506346 from PEAK where RELATIVE_INTENSITY >= 5 and (MZ between 83.799988 and 84.400008) group by RECORD_ID "
				+ "UNION ALL select max(concat(castinteger(RELATIVE_INTENSITY) || ' ' || castdouble(MZ))), RECORD_ID, 105.1, 25.744136822888727 from PEAK where RELATIVE_INTENSITY >= 5 and (MZ between 104.799988 and 105.400008) group by RECORD_ID "
				+ "UNION ALL select max(concat(castinteger(RELATIVE_INTENSITY) || ' ' || castdouble(MZ))), RECORD_ID, 107.1, 26.234034771445366 from PEAK where RELATIVE_INTENSITY >= 5 and (MZ between 106.799988 and 107.400008) group by RECORD_ID "
				+ "UNION ALL select max(concat(castinteger(RELATIVE_INTENSITY) || ' ' || castdouble(MZ))), RECORD_ID, 114.1, 116.91770667603842 from PEAK where RELATIVE_INTENSITY >= 5 and (MZ between 113.799988 and 114.400008) group by RECORD_ID "
				+ "UNION ALL select max(concat(castinteger(RELATIVE_INTENSITY) || ' ' || castdouble(MZ))), RECORD_ID, 115.1, 30.452597186643825 from PEAK where RELATIVE_INTENSITY >= 5 and (MZ between 114.799988 and 115.400008) group by RECORD_ID");
		
		for (String sql : sqls) {
			long s1 = System.currentTimeMillis();
			List<Map<Integer, Object>> result = DbAccessor.execResultQuery(sql);
			System.out.println("--------------------------------------------------" + (System.currentTimeMillis() - s1) + "ms");
			for (Map<Integer, Object> item : result) {
				for (Entry<Integer, Object> entry : item.entrySet()) {
					System.out.print(entry.getValue());
					System.out.print(" ");
				}
				System.out.println(" ");
			}
		}
		DbAccessor.closeConnection();
	}
	
//	@Test
	public void testQuery2() throws SQLException {
		DbAccessor.createConnection();
		System.out.println("start...");
		long s = System.currentTimeMillis();
		List<String> sqls = new ArrayList<String>();
		String sql = "select max(RELATIVE_INTENSITY), RECORD_ID from PEAK group by RECORD_ID";
		List<Map<Integer, Object>> result = DbAccessor.execResultQuery(sql);
		for (Map<Integer, Object> item : result) {
			sqls.add("select RELATIVE_INTENSITY, MZ, RECORD_ID from PEAK where RELATIVE_INTENSITY = " + String.valueOf(item.get(1)) + " and RECORD_ID = '" + String.valueOf(item.get(2)) + "'");
		}
		StringBuilder sb = new StringBuilder();
		for (String oSql : sqls) {
			if (StringUtils.isNotBlank(sb.toString())) {
				sb.append(" UNION ALL ");
			}
			sb.append(oSql);
		}
		List<Map<Integer, Object>> result2 = DbAccessor.execResultQuery(sb.toString());
		System.out.println("end..." + (System.currentTimeMillis() - s) + "ms");
		DbAccessor.closeConnection();
	}
	
//	@Test
	public void testSort() {
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("21");
		list.add("11");
		list.add("5");
		list.add("3");
		list.add("65");
		Collections.sort(list);
		for (String s : list) {
			System.out.println(s);
		}
	}
	
//	@Test
	public void testQuery() throws SQLException {
		
//		runSelectSQL("select P1.RELATIVE_INTENSITY, P1.MZ, P1.RECORD_ID from PEAK P1 join (select max(P.RELATIVE_INTENSITY) MAX_RELATIVE_INTENSITY, P.RECORD_ID from PEAK P where P.RELATIVE_INTENSITY >= 5 and (P.MZ between 84.099963 and 84.100039) group by P.RECORD_ID) P2 on P1.RELATIVE_INTENSITY = P2.MAX_RELATIVE_INTENSITY and P1.RECORD_ID = P2.RECORD_ID");
		
		// http://www.vogella.com/tutorials/JavaRegularExpressions/article.html
		String sql = String.format("SELECT * FROM %s WHERE REGEXP_LIKE (%s, '%s')", Record.TABLE, Record.Columns.RECORD_TITLE, "^.*MS3.*$");
		runSelectSQL(sql);
		
//		runSelectSQL("select max(concat(castinteger(RELATIVE_INTENSITY) || ' ' || castdouble(MZ))), RECORD_ID, 84.1, 47.57414337506346 from PEAK where RELATIVE_INTENSITY >= 5 and (MZ between 84.099963 and 84.100039) group by RECORD_ID UNION ALL select max(concat(castinteger(RELATIVE_INTENSITY) || ' ' || castdouble(MZ))), RECORD_ID, 105.1, 25.744136822888727 from PEAK where RELATIVE_INTENSITY >= 5 and (MZ between 105.099957 and 105.100046) group by RECORD_ID");
//		runSelectSQL("select max(RELATIVE_INTENSITY) MAX_RELATIVE_INTENSITY, RECORD_ID from PEAK group by RECORD_ID");
//		runSelectSQL("select max(castinteger(RELATIVE_INTENSITY)) MAX_RELATIVE_INTENSITY, RECORD_ID from PEAK group by RECORD_ID");
//		runSelectSQL("select max(concat(castinteger(RELATIVE_INTENSITY) || ' ' || castdouble(MZ))) MAX_RELATIVE_INTENSITY, RECORD_ID from PEAK group by RECORD_ID");
//		runSelectSQL("select max(concat(castinteger(RELATIVE_INTENSITY) || ' ' || RECORD_ID || ' ' || castdouble(MZ))) from PEAK group by RECORD_ID");
//		runSelectSQL("select max(concat(lpad(castinteger(RELATIVE_INTENSITY), 3, ' ') || ' ' || RECORD_ID || ' ' || castdouble(MZ))) from PEAK group by RECORD_ID");
//		runSelectSQL("select max(concat(castinteger(RELATIVE_INTENSITY) || ' ' || RECORD_ID || ' ' || castdouble(MZ) || ' a b')) from PEAK group by RECORD_ID");
//		runSelectSQL("select P1.RELATIVE_INTENSITY, P1.MZ, P1.RECORD_ID from PEAK P1 join (select max(RELATIVE_INTENSITY) MAX_RELATIVE_INTENSITY, RECORD_ID from PEAK group by RECORD_ID) P2 on P1.RECORD_ID = P2.RECORD_ID and P1.RELATIVE_INTENSITY = P2.MAX_RELATIVE_INTENSITY");
		
//		DbAccessor.createConnection();
//		System.out.println("start...");
//		long s = System.currentTimeMillis();
//		List<Map<Integer, Object>> result = DbAccessor.execQuery( "select R.RECORD_ID from RECORD R inner join SPECTRUM S on R.RECORD_ID = S.RECORD_ID" );
//		List<Map<Integer, Object>> result = DbAccessor.execQuery( "select concat(lpad(CASTDOUBLE(S.PRECURSOR_MZ), 10, ' ') || S.RECORD_ID) from SPECTRUM S" );
//		List<Map<Integer, Object>> result = DbAccessor.execQuery( "select RECORD_ID, max(RELATIVE_INTENSITY) from PEAK group by RECORD_ID" );
//		List<Map<Integer, Object>> result = DbAccessor.execQuery( "select * from PEAK where RELATIVE_INTENSITY >= 5 and (MZ between 59.699990 and 60.300010) group by RECORD_ID" );
//		List<Map<Integer, Object>> result = DbAccessor.execQuery( "select P1.RELATIVE_INTENSITY, P1.MZ, P1.RECORD_ID from PEAK P1 inner join (select max(RELATIVE_INTENSITY) MAX_RELATIVE_INTENSITY, RECORD_ID from PEAK group by RECORD_ID) P2 on P1.RECORD_ID = P2.RECORD_ID and P1.RELATIVE_INTENSITY = P2.MAX_RELATIVE_INTENSITY" );
//		List<Map<Integer, Object>> result = DbAccessor.execQuery( "select max(RELATIVE_INTENSITY) MAX_RELATIVE_INTENSITY, RECORD_ID from PEAK group by RECORD_ID" );
//		List<Map<Integer, Object>> result = DbAccessor.execQuery( "select P1.RELATIVE_INTENSITY, P1.MZ, P1.RECORD_ID from PEAK P1" );
//		List<Map<Integer, Object>> result = DbAccessor.execQuery( "select max(concat(castinteger(RELATIVE_INTENSITY) || ' ' || castdouble(MZ))), RECORD_ID from PEAK group by RECORD_ID" );
//		List<Map<Integer, Object>> result = DbAccessor.execQuery( "select max(concat(lpad(castinteger(RELATIVE_INTENSITY), 3, ' ') || ' ' || RECORD_ID || ' ' || castdouble(MZ))) from PEAK group by RECORD_ID" );
//		List<Map<Integer, Object>> result = DbAccessor.execQuery( "select R.RECORD_ID from RECORD R" );
//		System.out.println("end..." + (System.currentTimeMillis() - s) + "ms");
//		for (int i = 0; i < 100; i++) {
//			Map<Integer, Object> item = result.get(i);
//			for (Entry<Integer, Object> entry : item.entrySet()) {
//				System.out.print(String.valueOf(entry.getValue()) + ", ");
//			}
//		}
//		DbAccessor.closeConnection();
	}
	
	private void runSelectSQL(String sql) throws SQLException {
		DbAccessor.createConnection();
		System.out.println("start... | " + sql);
		long s = System.currentTimeMillis();
		List<Map<Integer, Object>> result = DbAccessor.execResultQuery(sql);
		System.out.println("end..." + (System.currentTimeMillis() - s) + "ms");
		for (int i = 0; i < result.size(); i++) {
			Map<Integer, Object> item = result.get(i);
			for (Entry<Integer, Object> entry : item.entrySet()) {
				System.out.print(String.valueOf(entry.getValue()) + ", ");
			}
			System.out.println("");
		}
		DbAccessor.closeConnection();
	}
	
//	@Test
	public void testLoop() {
		System.out.println("start");
		for (int i = 1; i != 2; ++i) {
			System.out.println(i);
		}
		System.out.println("end");
	}
	
//	@Test
	public void testSearchQuery() throws SQLException {
		DbAccessor.createConnection();
		String keyword = "d%p_s";
		RecordAccessor recordAccessor = new RecordAccessor();
		List<Record> result = new ArrayList<Record>();
		result = recordAccessor.getRecordsByName(keyword);
		System.out.println(result.size());
		DbAccessor.closeConnection();
	}

//	@Test
	public void testParse() {
		System.out.println(("1,2").split(","));
	}
}
