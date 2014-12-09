package jp.massbank.spectrumsearch.db.entity;

public class Instrument {
	
	public static final String TABLE = "INSTRUMENT";
	public static final class Columns {
		public static final String INSTRUMENT_ID = "INSTRUMENT_ID";
		public static final String INSTRUMENT_TYPE = "INSTRUMENT_TYPE";
		public static final String INSTRUMENT_NAME = "INSTRUMENT_NAME";
	}
	
	private int id;
	private String type;
	private String name;
  
  	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Instrument [no=" + id + ", type=" + type + ", name=" + name + "]";
	}
  
}
