package jp.massbank.spectrumsearch.entity.type;

public enum MassBankRecordLine {
	
	// Record Specific Information
	ACCESSION			("ACCESSION", MassBankRecordLineType.SINGLE),
	RECORD_TITLE		("RECORD_TITLE", MassBankRecordLineType.SINGLE),
	DATE				("DATE", MassBankRecordLineType.SINGLE),
	AUTHORS				("AUTHORS", MassBankRecordLineType.SINGLE),
	LICENSE				("LICENSE", MassBankRecordLineType.SINGLE),
	COPYRIGHT			("COPYRIGHT", MassBankRecordLineType.SINGLE),
	PUBLICATION			("PUBLICATION", MassBankRecordLineType.SINGLE),
	COMMENT				("COMMENT", MassBankRecordLineType.SINGLE),
	// Information of Chemical Compound Analyzed
	CH$NAME				("CH$NAME", MassBankRecordLineType.SINGLE),
	CH$COMPOUND_CLASS	("CH$COMPOUND_CLASS", MassBankRecordLineType.SINGLE),
	CH$FORMULA			("CH$FORMULA", MassBankRecordLineType.SINGLE),
	CH$EXACT_MASS		("CH$EXACT_MASS", MassBankRecordLineType.SINGLE),
	CH$SMILES			("CH$SMILES", MassBankRecordLineType.SINGLE),
	CH$IUPAC			("CH$IUPAC", MassBankRecordLineType.SINGLE),
	CH$LINK				("CH$LINK", MassBankRecordLineType.SINGLE),
	// Information of Biological Sample
	SP$SCIENTIFIC_NAME	("SP$SCIENTIFIC_NAME", MassBankRecordLineType.SINGLE),
	SP$NAME				("SP$NAME", MassBankRecordLineType.SINGLE),
	SP$LINEAGE			("SP$LINEAGE", MassBankRecordLineType.SINGLE),
	SP$LINK				("SP$LINK", MassBankRecordLineType.SINGLE),
	SP$SAMPLE			("SP$SAMPLE", MassBankRecordLineType.SINGLE),
	// Analytical Methods and Conditions
	AC$INSTRUMENT		("AC$INSTRUMENT", MassBankRecordLineType.SINGLE),
	AC$INSTRUMENT_TYPE	("AC$INSTRUMENT_TYPE", MassBankRecordLineType.SINGLE),
	// TODO more fields
	AC$MASS_SPECTROMETRY("AC$MASS_SPECTROMETRY", MassBankRecordLineType.SINGLE),
	AC$CHROMATOGRAPHY	("AC$CHROMATOGRAPHY", MassBankRecordLineType.SINGLE),
	// Description of mass spectral data
	MS$FOCUSED_ION		("MS$FOCUSED_ION", MassBankRecordLineType.SINGLE),
	MS$DATA_PROCESSING	("MS$DATA_PROCESSING", MassBankRecordLineType.SINGLE),
	// Peak Information
	PK$ANNOTATION		("PK$ANNOTATION", MassBankRecordLineType.MULTIPLE),
	PK$NUM_PEAK			("PK$NUM_PEAK", MassBankRecordLineType.SINGLE),
	PK$PEAK				("PK$PEAK", MassBankRecordLineType.MULTIPLE),
	// OTHERs
//	FOLLOWING_LINE		(" ", MassBankRecordLineType.FOLLOWING),
	FOLLOWING_LINE		("  ", MassBankRecordLineType.FOLLOWING),
	EOF					("//", MassBankRecordLineType.EOF);
	
	private String key;
	private MassBankRecordLineType lineType;
	
	private MassBankRecordLine(String key) {
		this.key = key;
	}

	private MassBankRecordLine(String key, MassBankRecordLineType lineType) {
		this.key = key;
		this.lineType = lineType;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public boolean isSingleLine() {
		return MassBankRecordLineType.SINGLE == this.lineType;
	}
	
	public boolean isMultipleLine() {
		return MassBankRecordLineType.MULTIPLE == this.lineType;
	}
	
	public boolean isFollowingLine() {
		return MassBankRecordLineType.FOLLOWING == this.lineType;
	}
	
	public static MassBankRecordLine getValue(String key) {
		for (MassBankRecordLine k : MassBankRecordLine.values()) {
			if (k.getKey().equals(key)) {
				return k;
			}
		}
		return null;
	}

}
