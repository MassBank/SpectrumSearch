package jp.massbank.spectrumsearch.Logic;

import jp.massbank.spectrumsearch.entity.constant.SystemProperties;
import jp.massbank.spectrumsearch.logic.MassBankRecordLogic;

import org.junit.Test;

public class FileRecordLogicTest {
	
	@Test
	public void testSync() {
		MassBankRecordLogic logic = new MassBankRecordLogic();
		logic.syncFilesRecordsByFolderPath(SystemProperties.getInstance().getFolderPath());
	}

}
