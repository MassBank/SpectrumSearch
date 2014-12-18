package jp.massbank.spectrumsearch.entity.type;


public enum IonModeType {
	
	POSITIVE	(1, "POS"),
	NEGATIVE	(-1, "NEG"),
	BOTH		(0, "BOTH");
	
	private String key;
	private int value;
	
	private IonModeType(int val, String key) {
		this.value = val;
		this.key = key;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public static int parseInt(String val) {
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException e) {
			for (IonModeType type : IonModeType.values()) {
				if (type.name().equalsIgnoreCase(val)) {
					return type.getValue();
				}
			}
		}
		return BOTH.getValue();
	}

}
