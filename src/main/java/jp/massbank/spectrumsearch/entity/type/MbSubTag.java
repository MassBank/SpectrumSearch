package jp.massbank.spectrumsearch.entity.type;

public enum MbSubTag {

	MS_TYPE ("MS_TYPE");
	
	private String subTag;
	
	private MbSubTag(String subTag) {
		this.subTag = subTag;
	}
	
	public String getValue() {
		return this.subTag;
	}
	
}
