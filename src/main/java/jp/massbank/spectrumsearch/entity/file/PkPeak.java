package jp.massbank.spectrumsearch.entity.file;

import java.util.List;
import java.util.Map;

public class PkPeak {

	private int no;
	private List<Map<String, String>> peakMap;

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public List<Map<String, String>> getPeakMap() {
		return peakMap;
	}

	public void setPeakMap(List<Map<String, String>> peakMap) {
		this.peakMap = peakMap;
	}
	
}
