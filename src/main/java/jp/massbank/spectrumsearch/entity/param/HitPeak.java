package jp.massbank.spectrumsearch.entity.param;

public class HitPeak {

	private String qMz;
	private double qVal;
	private String hitMz;
	private double hitVal;

	public String getqMz() {
		return qMz;
	}

	public void setqMz(String qMz) {
		this.qMz = qMz;
	}

	public double getqVal() {
		return qVal;
	}

	public void setqVal(double qVal) {
		this.qVal = qVal;
	}

	public String getHitMz() {
		return hitMz;
	}

	public void setHitMz(String hitMz) {
		this.hitMz = hitMz;
	}

	public double getHitVal() {
		return hitVal;
	}

	public void setHitVal(double hitVal) {
		this.hitVal = hitVal;
	}
	
}
