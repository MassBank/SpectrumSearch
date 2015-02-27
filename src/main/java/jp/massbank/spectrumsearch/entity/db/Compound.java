package jp.massbank.spectrumsearch.entity.db;

public class Compound {
	
	public static final String TABLE = "COMPOUND";
	public static final class Columns {
		public static final String COMPOUND_ID = "COMPOUND_ID";
		public static final String TITLE = "TITLE";
		public static final String FORMULA = "FORMULA";
		public static final String EXACT_MASS = "EXACT_MASS";
		public static final String ION_MODE = "ION_MODE";
		public static final String INSTRUMENT_ID = "INSTRUMENT_ID";
		public static final String MS_TYPE_ID = "MS_TYPE_ID";
	}

	private String id;
	private String title;
	private String formula;
	private double exactMass;
	private int ionMode;
	private int instrumentId;
	private int msId;

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

	public int getIonMode() {
		return ionMode;
	}

	public void setIonMode(int ionMode) {
		this.ionMode = ionMode;
	}

	public int getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(int instrumentId) {
		this.instrumentId = instrumentId;
	}

	public int getMsId() {
		return msId;
	}

	public void setMsId(int msId) {
		this.msId = msId;
	}
	
}
