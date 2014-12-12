package jp.massbank.spectrumsearch.entity.xml;

public class Site {
	
	private String name;
	private String longName;
	private String[] prefixes;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public boolean hasPrefix() {
		return prefixes != null && prefixes.length > 0;
	}
	
	public String[] getPrefixes() {
		return prefixes;
	}

	public void setPrefixes(String[] prefixes) {
		this.prefixes = prefixes;
	}

}
