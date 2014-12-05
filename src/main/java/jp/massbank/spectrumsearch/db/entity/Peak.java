package jp.massbank.spectrumsearch.db.entity;

public class Peak {
	
	public static final String TABLE = "PEAK";
	public static final class Columns {
		public static final String PEAK_ID = "PEAK_ID";							 	// auto generate value
		public static final String MZ = "MZ";										// PK$PEAK -> m/z
		public static final String INTENSITY = "INTENSITY";							// PK$PEAK -> int.
		public static final String RELATIVE_INTENSITY = "RELATIVE_INTENSITY";		// PK$PEAK -> rel.int.
		public static final String RECORD_ID = "RECORD_ID";							// PK$PEAK -> ACCESSION
	}
	
	private int id;
	private double mz;
	private double intensity;
	private int relativeIntensity;
	private String recordId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getMz() {
		return mz;
	}

	public void setMz(double mz) {
		this.mz = mz;
	}

	public double getIntensity() {
		return intensity;
	}

	public void setIntensity(double intensity) {
		this.intensity = intensity;
	}

	public int getRelativeIntensity() {
		return relativeIntensity;
	}

	public void setRelativeIntensity(int relativeIntensity) {
		this.relativeIntensity = relativeIntensity;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

}