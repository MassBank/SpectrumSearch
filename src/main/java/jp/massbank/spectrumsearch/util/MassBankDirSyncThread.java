package jp.massbank.spectrumsearch.util;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.massbank.spectrumsearch.accessor.DbAccessor;
import jp.massbank.spectrumsearch.entity.db.Instrument;
import jp.massbank.spectrumsearch.entity.db.MsType;
import jp.massbank.spectrumsearch.logic.MassBankRecordLogic;

import org.apache.log4j.Logger;

public class MassBankDirSyncThread implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(MassBankDirSyncThread.class);
	
	private boolean stop;
	private boolean finished;
	private String path;
	private List<Instrument> instruments;
	private List<MsType> msTypes;
	private int count = 0;
	
	private MassBankRecordLogic mbRecordLogic;
	
	public MassBankDirSyncThread(String path) {
		this.path = path;
		this.instruments = new ArrayList<Instrument>();
		this.msTypes = new ArrayList<MsType>();
		this.mbRecordLogic = new MassBankRecordLogic();
	}
	
	@Override
	public void run() {
		mbRecordLogic.upgradeAndResetDatabase();
		
		try {
			// open connection
			DbAccessor.createConnection();
			syncDir(this.path);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				// close connection
				DbAccessor.closeConnection();
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		
	}
	
	public void start() {
		this.run();
	}
	
	public void stop() {
        this.stop = true;
    }
	
	public int getCount() {
		return this.count;
	}
	
	public boolean isFinished() {
		return this.finished;
	}
	
	private void syncDir(String pathname) {
		File f = new File(pathname);
		File[] listfiles = f.listFiles();
		for (int i = 0; i < listfiles.length; i++) {
			if (!stop) {
				File item = listfiles[i];
				if (! item.isHidden()) {
					if (item.isDirectory()) {
						File[] internalFiles = item.listFiles();
						for (int j = 0; j < internalFiles.length; j++) {
							if (!stop) {
								File item2 = internalFiles[j];
								if (! item2.isHidden()) {
									if (item2.isDirectory()) {
										String name = item2.getAbsolutePath();
										syncDir(name);
									} else {
										mbRecordLogic.mergeMassBankRecordIntoDb(item2, instruments, msTypes);
										count++;
									}
								}
							}
						}
					}
				}
			}
		}
		finished = true;
	}

}
