package jp.massbank.spectrumsearch.entity.param;

public class QueryResultHitPeak {
	
	private String recordId;
	private double hitRelInt;
	private String hitMz;
	private String mz;
	private double value;

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public double getHitRelInt() {
		return hitRelInt;
	}

	public void setHitRelInt(double hitRelInt) {
		this.hitRelInt = hitRelInt;
	}

	public double getHitMz() {
		return Float.parseFloat(hitMz);
	}
	
	public String getHitMzString() {
		return hitMz;
	}
	
	public void setHitMz(String hitMz) {
		this.hitMz = hitMz;
	}

	public double getMz() {
		return Float.parseFloat(mz);
	}
	
	public String getMzString() {
		return mz;
	}

	public void setMz(String mz) {
		this.mz = mz;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

}
