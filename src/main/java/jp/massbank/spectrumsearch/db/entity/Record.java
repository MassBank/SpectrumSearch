package jp.massbank.spectrumsearch.db.entity;

public class Record {
	
	public static final String TABLE = "RECORD";
	public static final class Columns {
		public static final String RECORD_ID = "RECORD_ID";
		public static final String RECORD_TITLE = "RECORD_TITLE";
		public static final String MS_TYPE = "MS_TYPE";
		public static final String FORMULA = "FORMULA";
		public static final String EXACT_MASS = "EXACT_MASS";
		public static final String INSTRUMENT_ID = "INSTRUMENT_ID";
	}

	private String id;
	private String title;
	private String msType;
	private String formula;
	private double exactMass;
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

	public String getMsType() {
		return msType;
	}

	public void setMsType(String msType) {
		this.msType = msType;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public double getExactMass() {
		return exactMass;
	}

	public void setExactMass(double exactMass) {
		this.exactMass = exactMass;
	}

	public int getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(int instrumentId) {
		this.instrumentId = instrumentId;
	}
	
}
