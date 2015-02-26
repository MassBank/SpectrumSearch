package jp.massbank.spectrumsearch.entity.db;

public class Precursor {

	public static final String TABLE = "PRECURSOR";
	public static final class Columns {
		public static final String PRECURSOR_ID = "PRECURSOR_ID";
		public static final String PRECURSOR_MZ = "PRECURSOR_MZ";
		public static final String COMPOUND_ID = "COMPOUND_ID";
	}

	private int precursorId;
	private double precursorMz;
	private String compoundId;

	public int getPrecursorId() {
		return precursorId;
	}

	public void setPrecursorId(int precursorId) {
		this.precursorId = precursorId;
	}

	public double getPrecursorMz() {
		return precursorMz;
	}

	public void setPrecursorMz(double precursorMz) {
		this.precursorMz = precursorMz;
	}

	public String getCompoundId() {
		return compoundId;
	}

	public void setCompoundId(String compoundId) {
		this.compoundId = compoundId;
	}
	
}
