package jp.massbank.spectrumsearch.entity.type;

public enum ToleranceUnit {
	
	PPM ("ppm"),
	UNIT ("unit");
	
	private String label;
	
	private ToleranceUnit(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}

}
