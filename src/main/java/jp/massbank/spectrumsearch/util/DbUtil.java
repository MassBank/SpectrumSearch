package jp.massbank.spectrumsearch.util;

import java.sql.SQLException;

import jp.massbank.spectrumsearch.accessor.DbAccessor;
import jp.massbank.spectrumsearch.accessor.InstrumentAccessor;
import jp.massbank.spectrumsearch.accessor.MassSpectrometryAccessor;
import jp.massbank.spectrumsearch.accessor.MsTypeAccessor;
import jp.massbank.spectrumsearch.accessor.PeakAccessor;
import jp.massbank.spectrumsearch.accessor.RecordAccessor;
import jp.massbank.spectrumsearch.accessor.SpectrumAccessor;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class DbUtil {
	
	private static final Logger LOGGER = Logger.getLogger(DbUtil.class);
	
	public static String concat(String data) {
		String[] args = data.split("||");
		return StringUtils.join(args);
	}
	
	public static String lpad(String str, int size, String padChar) {
		return StringUtils.leftPad(str, size, padChar);
	}
	
	public static String castDouble(Double val) {
		return String.valueOf(val);
	}
	
	public static String castInteger(Integer val) {
		return String.valueOf(val);
	}

	public static void createSchemaIfNotExist() {
		try {
			DbAccessor.createConnection();
			
			RecordAccessor recordAccessor = new RecordAccessor();
			recordAccessor.dropTable();
			recordAccessor.createTable();
			
			InstrumentAccessor instrumentAccessor = new InstrumentAccessor();
			instrumentAccessor.dropTable();
			instrumentAccessor.createTable();
			
			MassSpectrometryAccessor massSpectrometryAccessor = new MassSpectrometryAccessor();
			massSpectrometryAccessor.dropTable();
			massSpectrometryAccessor.createTable();
			
			PeakAccessor peakAccessor = new PeakAccessor();
			peakAccessor.dropTable();
			peakAccessor.createTable();
			
			SpectrumAccessor spectrumAccessor = new SpectrumAccessor();
			spectrumAccessor.dropTable();
			spectrumAccessor.createTable();
			
			MsTypeAccessor msTypeAccessor = new MsTypeAccessor();
			msTypeAccessor.dropTable();
			msTypeAccessor.createTable();
			
			DbAccessor.closeConnection();
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
}
