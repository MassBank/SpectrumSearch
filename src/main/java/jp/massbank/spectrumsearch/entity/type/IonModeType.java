package jp.massbank.spectrumsearch.entity.type;

public enum IonModeType {
	
	POSITIVE (1),
	NEGATIVE (-1),
	OTHER (0);
	
	private int value;
	
	private IonModeType(int val) {
		this.value = val;
	}
	
	public int getValue() {
		return this.value;
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
		return OTHER.getValue();
	}

}
