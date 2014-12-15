package jp.massbank.spectrumsearch.entity.db;

public class MassSpectrometry {
	
	public static final String TABLE = "MASS_SPECTROMETRY";
	public static final class Columns {
		public static final String MASS_SPECTROMETRY_ID = "MASS_SPECTROMETRY_ID";
		public static final String MASS_SPECTROMETRY_TYPE = "MASS_SPECTROMETRY_TYPE";
		public static final String MASS_SPECTROMETRY_VALUE = "MASS_SPECTROMETRY_VALUE";
		public static final String RECORD_ID = "RECORD_ID";
	}
	
	private int id;
	private String type;
	private String value;
	private String recordId;

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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

}
