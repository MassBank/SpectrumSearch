package jp.massbank.spectrumsearch.db.entity;

public class MsType {
	
	public static final String TABLE = "MS_TYPE";
	public static final class Columns {
		public static final String MS_TYPE_ID = "MS_TYPE_ID";
		public static final String MS_TYPE_NAME = "MS_TYPE_NAME";
	}
	
	private int id;
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
