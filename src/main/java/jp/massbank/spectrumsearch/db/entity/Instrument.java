package jp.massbank.spectrumsearch.db.entity;

public class Instrument {
	
	public static final String TABLE = "INSTRUMENT";
	public static final class Columns {
		public static final String INSTRUMENT_NO = "INSTRUMENT_NO";
		public static final String INSTRUMENT_TYPE = "INSTRUMENT_TYPE";
		public static final String INSTRUMENT_NAME = "INSTRUMENT_NAME";
	}
	
	private int no;
	private String type;
	private String name;
  
  	public int getNo() {
		return no;
	}
	
	public void setNo(int no) {
		this.no = no;
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
		return "Instrument [no=" + no + ", type=" + type + ", name=" + name + "]";
	}
  
}
