package jp.massbank.spectrumsearch.entity.gui;

public class GuiResultTableRow implements Comparable<GuiResultTableRow> {
	
	private int index;		// tbl.column : No.
	private String recordId;	// tbl.column : ID
	private String recordTitle; // tbl.column : Name
	private String contributor; // tbl.column : Contributor
	private double score; 		// tbl.column : Score
	private int hit; 		// tbl.column : Hit
	private String ionMode; 	// tbl.column : Ion

	public GuiResultTableRow(int index, String recordId, String recordTitle,
			String contributor, double score, int hit, String ionMode) {
		super();
		this.index = index;
		this.recordId = recordId;
		this.recordTitle = recordTitle;
		this.contributor = contributor;
		this.score = score;
		this.hit = hit;
		this.ionMode = ionMode;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
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

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public int getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}

	public String getIonMode() {
		return ionMode;
	}

	public void setIonMode(String ionMode) {
		this.ionMode = ionMode;
	}
	
	@Override
	public int compareTo(GuiResultTableRow o) {
		int index = ((GuiResultTableRow) o).getIndex();
		// ascending order
		return this.index - index;
	}
}
