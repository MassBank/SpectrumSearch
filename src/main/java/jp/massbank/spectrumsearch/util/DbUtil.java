package jp.massbank.spectrumsearch.util;

import java.sql.SQLException;

import jp.massbank.spectrumsearch.db.accessor.DbAccessor;
import jp.massbank.spectrumsearch.db.accessor.InstrumentAccessor;
import jp.massbank.spectrumsearch.db.accessor.MassSpectrometryAccessor;
import jp.massbank.spectrumsearch.db.accessor.PeakAccessor;
import jp.massbank.spectrumsearch.db.accessor.RecordAccessor;
import jp.massbank.spectrumsearch.db.accessor.SpectrumAccessor;

import org.apache.log4j.Logger;

public class DbUtil {
	
	private static final Logger LOGGER = Logger.getLogger(DbUtil.class);

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
			
			DbAccessor.closeConnection();
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
}
