package jp.massbank.spectrumsearch.entity.param;

import org.apache.commons.lang3.StringUtils;

import jp.massbank.spectrumsearch.entity.constant.Constant;
import jp.massbank.spectrumsearch.entity.constant.SystemProperties;

public class SearchQueryParam {

	private int start 		= 1;
	private int num 		= 0;
	private int floor 		= 0;
	private int celing 		= 1000;
	private int threshold 	= 3;
	private int cutoff 		= SystemProperties.getInstance().getDefaultCutoffThreshold();	// from system.properties
	private float tolerance	= SystemProperties.getInstance().getDefaultTolerance(); // from system.properties
	private String colType 	= "COSINE";
	private boolean weight 	= Constant.PARAM_WEIGHT_SQUARE;
	private boolean norm 	= Constant.PARAM_NORM_SQRT;
	private String tolUnit 	= "unit";
	private String val		= StringUtils.EMPTY;
	private String instType	= StringUtils.EMPTY;
	private int ion			= 0;
	private int precursor 	= 0;
	
	private String msType 	= StringUtils.EMPTY;
	private boolean isQuick = false;
	private boolean isInteg = false;
	private boolean isAPI 	= false;
	private String peak;

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		if (start > 0) {
			this.start = start;
		}
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		if (num > 0) {
			this.num = num;
		}
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public int getCeling() {
		return celing;
	}

	public void setCeling(int celing) {
		this.celing = celing;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public int getCutoff() {
		return cutoff;
	}

	public void setCutoff(int cutoff) {
		this.cutoff = cutoff;
	}

	public float getTolerance() {
		return tolerance;
	}

	public void setTolerance(float tolerance) {
		this.tolerance = tolerance;
	}

	public String getColType() {
		return colType;
	}

	public void setColType(String colType) {
		this.colType = colType;
	}

	public boolean getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		if ("LINEAR".equals(weight)) {
			this.weight = Constant.PARAM_WEIGHT_LINEAR;
		} else if ("SQUARE".equals(weight)) {
			this.weight = Constant.PARAM_WEIGHT_SQUARE;
		}
	}

	public boolean getNorm() {
		return norm;
	}

	public void setNorm(String norm) {
		if ("LOG".equals(norm)) {
			this.norm = Constant.PARAM_NORM_LOG;
		} else if ("SQRT".equals(norm)) {
			this.norm = Constant.PARAM_NORM_SQRT;
		}
	}

	public String getTolUnit() {
		return tolUnit;
	}

	public void setTolUnit(String tolUnit) {
		this.tolUnit = tolUnit;
	}

	public int getPrecursor() {
		return precursor;
	}

	public void setPrecursor(int precursor) {
		if (precursor > 0) {
			this.precursor = precursor;
		}
	}

	public String getMstype() {
		return msType;
	}

	public void setMsType(String msType) {
		this.msType = msType;
	}

	public boolean isQuick() {
		return isQuick;
	}

	public void setQuick(boolean isQuick) {
		this.isQuick = isQuick;
	}

	public boolean isInteg() {
		return isInteg;
	}

	public void setInteg(boolean isInteg) {
		this.isInteg = isInteg;
	}

	public boolean isAPI() {
		return isAPI;
	}

	public void setAPI(boolean isAPI) {
		this.isAPI = isAPI;
	}

	public String getInstType() {
		return instType;
	}

	public void setInstType(String instType) {
		this.instType = instType;
	}

	public int getIon() {
		return ion;
	}

	public void setIon(int ion) {
		this.ion = ion;
	}

	public String getPeak() {
		return peak;
	}

	public void setPeak(String peak) {
		this.peak = peak;
	}
	
}