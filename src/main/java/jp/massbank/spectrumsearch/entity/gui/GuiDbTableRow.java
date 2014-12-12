package jp.massbank.spectrumsearch.entity.gui;

public class GuiDbTableRow implements Comparable<GuiDbTableRow> {
	
	private String index;		// tbl.column : No.
	private String recordId;	// tbl.column : ID
	private String recordTitle; // tbl.column : Name
	private String contributor; // tbl.column : Contributor

	public GuiDbTableRow(String index, String recordId, String recordTitle, String contributor) {
		super();
		this.index = index;
		this.recordId = recordId;
		this.recordTitle = recordTitle;
		this.contributor = contributor;
	}
	
	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getRecordTitle() {
		return recordTitle;
	}

	public void setRecordTitle(String recordTitle) {
		this.recordTitle = recordTitle;
	}

	public String getContributor() {
		return contributor;
	}

	public void setContributor(String contributor) {
		this.contributor = contributor;
	}

	@Override
	public int compareTo(GuiDbTableRow o) {
		int index = Integer.parseInt(((GuiDbTableRow) o).getIndex());
		// ascending order
		return Integer.parseInt(this.index) - index;
	}

}
