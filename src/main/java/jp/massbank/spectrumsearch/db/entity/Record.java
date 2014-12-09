package jp.massbank.spectrumsearch.db.entity;

public class Record {
	
	public static final String TABLE = "RECORD";
	public static final class Columns {
		public static final String RECORD_ID = "RECORD_ID";
		public static final String RECORD_TITLE = "RECORD_TITLE";
		public static final String INSTRUMENT_ID = "INSTRUMENT_ID";
	}

	private String id;
	private String title;
	private int instrumentId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(int instrumentId) {
		this.instrumentId = instrumentId;
	}
	
}
