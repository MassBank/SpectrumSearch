package jp.massbank.spectrumsearch.entity.db;

public class CompoundName {
	
	public static final String TABLE = "COMPOUND_NAME";
	public static final class Columns {
		public static final String COMPOUND_NAME_ID = "COMPOUND_NAME_ID";
		public static final String NAME = "NAME";
		public static final String COMPOUND_ID = "COMPOUND_ID";
	}
	
	private int compoundNameId;
	private String name;
	private String compoundId;

	public int getCompoundNameId() {
		return compoundNameId;
	}

	public void setCompoundNameId(int compoundNameId) {
		this.compoundNameId = compoundNameId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompoundId() {
		return compoundId;
	}

	public void setCompoundId(String compoundId) {
		this.compoundId = compoundId;
	}

}
