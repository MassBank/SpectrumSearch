package jp.massbank.spectrumsearch.Logic;

import jp.massbank.spectrumsearch.entity.constant.SystemProperties;
import jp.massbank.spectrumsearch.logic.MassBankRecordLogic;

import org.junit.Test;

public class FileRecordLogicTest {
	
	@Test
	public void testSync() {
		MassBankRecordLogic logic = new MassBankRecordLogic();
		logic.syncFilesRecordsByFolderPath(SystemProperties.getInstance().getDirPath());
	}
	
//	@Test
//	public void testQuery() throws SQLException {
//		DbAccessor.createConnection();
////		List<Map<Integer, Object>> result = DbAccessor.execQuery( "select R.RECORD_ID from RECORD R inner join SPECTRUM S on R.RECORD_ID = S.RECORD_ID" );
////		List<Map<Integer, Object>> result = DbAccessor.execQuery( "select concat(lpad(CASTDOUBLE(S.PRECURSOR_MZ), 10, ' ') || S.RECORD_ID) from SPECTRUM S" );
//		List<Map<Integer, Object>> result = DbAccessor.execQuery( "select RECORD_ID, max(RELATIVE_INTENSITY) from PEAK group by RECORD_ID" );
////		List<Map<Integer, Object>> result = DbAccessor.execQuery( "select * from PEAK where RELATIVE_INTENSITY >= 5 and (MZ between 59.699990 and 60.300010) group by RECORD_ID" );
////		List<Map<Integer, Object>> result = DbAccessor.execQuery( "select max(concat(lpad(CAST(RELATIVE_INTENSITY AS CHAR(3)), 3, ' ') || ' ' || PEAK_ID || ' ' || castdouble(MZ))) from PEAK" );
////		List<Map<Integer, Object>> result = DbAccessor.execQuery( "select R.RECORD_ID from RECORD R" );
//		result.size();
//		DbAccessor.closeConnection();
//	}
	
//	@Test
//	public void testLoop() {
//		System.out.println("start");
//		for (int i = 1; i != 2; ++i) {
//			System.out.println(i);
//		}
//		System.out.println("end");
//	}
	
//	@Test
//	public void testSearchQuery() throws SQLException {
//		DbAccessor.createConnection();
//		String keyword = "d%p_s";
//		RecordAccessor recordAccessor = new RecordAccessor();
//		List<Record> result = new ArrayList<Record>();
//		result = recordAccessor.getRecordsByName(keyword);
//		System.out.println(result.size());
//		DbAccessor.closeConnection();
//	}

}
