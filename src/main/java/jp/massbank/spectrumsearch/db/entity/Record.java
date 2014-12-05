package jp.massbank.spectrumsearch.db.entity;

public class Record {
	
	public static final String TABLE = "RECORD";
	public static final class Columns {
		public static final String RECORD_ID = "RECORD_ID";
		public static final String RECORD_TITLE = "RECORD_TITLE";
	}

	private String id;
	private String title;

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
	
}