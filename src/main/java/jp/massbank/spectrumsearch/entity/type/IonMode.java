package jp.massbank.spectrumsearch.entity.type;


public enum IonMode {
	
	POSITIVE	(1, "POS", "Positive"),
	NEGATIVE	(-1, "NEG", "Negative"),
	BOTH		(0, "BOTH", "Both");
	
	private int value;
	private String key;
	private String label;
	
	private IonMode(int val, String key, String label) {
		this.value = val;
		this.key = key;
		this.label = label;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public static int parseInt(String val) {
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException e) {
			for (IonMode type : IonMode.values()) {
				if (type.name().equalsIgnoreCase(val)) {
					return type.getValue();
				}
			}
		}
		return BOTH.getValue();
	}

}
