package jp.massbank.spectrumsearch.db.entity;

public class Spectrum {
	
	public static final String TABLE = "SPECTRUM";
	public static final class Columns {
		public static final String SPECTRUM_ID = "SPECTRUM_ID"; 	// auto generate value
		public static final String TITLE = "TITLE";					// RECORD_TITLE
		public static final String ION_MODE = "ION_MODE";			// AC$MASS_SPECTROMETRY: ION_MODE
		public static final String PRECURSOR_MZ = "PRECURSOR_MZ";	// MS$FOCUSED_ION: PRECURSOR_M/Z
		public static final String RECORD_ID = "RECORD_ID";			// ACCESSION
	}

	private int id;
	private String title;
	private int ionMode;
	private float precursorMz;
	private String recordId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIonMode() {
		return ionMode;
	}

	public void setIonMode(int ionMode) {
		this.ionMode = ionMode;
	}

	public float getPrecursorMz() {
		return precursorMz;
	}

	public void setPrecursorMz(float precursorMz) {
		this.precursorMz = precursorMz;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
	
}
