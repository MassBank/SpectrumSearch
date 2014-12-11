package jp.massbank.spectrumsearch.file.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class MassBankRecord {
	
	// Record Specific Information
	private String id;
	private String title;
	private String date;
	private List<String> authors;
	private String license;
	private List<String> copyrights;
	private List<String> publications;
	private List<String> comments;
	// Information of Chemical Compound Analyzed
	private List<String> chNames;
	private String chCompoundClass;
	private String chFormula;
	private Float chExtractMass;
	private String chSmiles;
	private String chIupac;
	private Map<String, String> chLinkMap;
	// Information of Biological Sample
	private String spScientificName;
	private String spName;
	private String spLineage;
	private Map<String, String> spLinkMap;
	private String spSample;
	// Analytical Methods and Conditions
	private AcInstrument acInstrument;
	private Map<String, String> acMassSpectrometryMap;
	private Map<String, String> acChromatographyMap;
	// Description of mass spectral data
	private Map<String, String> msFocusedIonMap;
	private Map<String, String> msDataProcessingMap;
	// Peak Information
	private List<Map<String, String>> pkAnnotationMap;
	private PkPeak pkPeak;
	
	public boolean isAvailable() {
		return StringUtils.isNotBlank(id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<String> getAuthors() {
		if (authors == null) {
			authors = new ArrayList<String>();
		}
		return authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public List<String> getCopyrights() {
		if (copyrights == null) {
			copyrights = new ArrayList<String>();
		}
		return copyrights;
	}

	public void setCopyrights(List<String> copyrights) {
		this.copyrights = copyrights;
	}

	public List<String> getPublications() {
		if (publications == null) {
			publications = new ArrayList<String>();
		}
		return publications;
	}

	public void setPublications(List<String> publications) {
		this.publications = publications;
	}

	public List<String> getComments() {
		if (comments == null) {
			comments = new ArrayList<String>();
		}
		return comments;
	}

	public void setComments(List<String> comments) {
		this.comments = comments;
	}

	public List<String> getChNames() {
		if (chNames == null) {
			chNames = new ArrayList<String>();
		}
		return chNames;
	}

	public void setChNames(List<String> ChemicalNames) {
		this.chNames = ChemicalNames;
	}

	public String getChCompoundClass() {
		return chCompoundClass;
	}

	public void setChCompoundClass(String chemicalCompoundClass) {
		this.chCompoundClass = chemicalCompoundClass;
	}

	public String getChFormula() {
		return chFormula;
	}

	public void setChFormula(String formula) {
		this.chFormula = formula;
	}

	public Float getChExtractMass() {
		return chExtractMass;
	}

	public void setChExtractMass(Float extractMass) {
		this.chExtractMass = extractMass;
	}

	public String getChSmiles() {
		return chSmiles;
	}

	public void setChSmiles(String smiles) {
		this.chSmiles = smiles;
	}

	public String getChIupac() {
		return chIupac;
	}

	public void setChIupac(String iupac) {
		this.chIupac = iupac;
	}

	public Map<String, String> getChLinkMap() {
		if (chLinkMap == null) {
			chLinkMap = new LinkedHashMap<String, String>();
		}
		return chLinkMap;
	}

	public void setChLinkMap(Map<String, String> linkMap) {
		this.chLinkMap = linkMap;
	}

	public String getSpScientificName() {
		return spScientificName;
	}

	public void setSpScientificName(String spScientificName) {
		this.spScientificName = spScientificName;
	}

	public String getSpName() {
		return spName;
	}

	public void setSpName(String spName) {
		this.spName = spName;
	}

	public String getSpLineage() {
		return spLineage;
	}

	public void setSpLineage(String spLineage) {
		this.spLineage = spLineage;
	}

	public Map<String, String> getSpLinkMap() {
		if (spLinkMap == null) {
			spLinkMap = new HashMap<String, String>();
		}
		return spLinkMap;
	}

	public void setSpLinkMap(Map<String, String> spLinkMap) {
		this.spLinkMap = spLinkMap;
	}

	public String getSpSample() {
		return spSample;
	}

	public void setSpSample(String spSample) {
		this.spSample = spSample;
	}

	public AcInstrument getAcInstrument() {
		if (acInstrument ==  null) {
			acInstrument = new AcInstrument();
		}
		return acInstrument;
	}

	public void setAcInstrument(AcInstrument instrument) {
		this.acInstrument = instrument;
	}

	public Map<String, String> getAcMassSpectrometryMap() {
		if (acMassSpectrometryMap == null) {
			acMassSpectrometryMap = new LinkedHashMap<String, String>();
		}
		return acMassSpectrometryMap;
	}

	public void setAcMassSpectrometryMap(Map<String, String> massSpectrometryMap) {
		this.acMassSpectrometryMap = massSpectrometryMap;
	}

	public Map<String, String> getAcChromatographyMap() {
		if (acChromatographyMap == null) {
			acChromatographyMap = new LinkedHashMap<String, String>();
		}
		return acChromatographyMap;
	}

	public void setAcChromatographyMap(Map<String, String> chromatographyMap) {
		this.acChromatographyMap = chromatographyMap;
	}

	public Map<String, String> getMsFocusedIonMap() {
		if (msFocusedIonMap == null) {
			msFocusedIonMap = new LinkedHashMap<String, String>();
		}
		return msFocusedIonMap;
	}

	public void setMsFocusedIonMap(Map<String, String> focusedIonMap) {
		this.msFocusedIonMap = focusedIonMap;
	}

	public Map<String, String> getMsDataProcessingMap() {
		if (msDataProcessingMap == null) {
			msDataProcessingMap = new LinkedHashMap<String, String>();
		}
		return msDataProcessingMap;
	}

	public void setMsDataProcessingMap(Map<String, String> dataProcessingMap) {
		this.msDataProcessingMap = dataProcessingMap;
	}

	public List<Map<String, String>> getPkAnnotationMap() {
		if (pkAnnotationMap == null) {
			pkAnnotationMap =  new ArrayList<Map<String,String>>();
		}
		return pkAnnotationMap;
	}

	public void setPkAnnotationMap(List<Map<String, String>> annotationMap) {
		this.pkAnnotationMap = annotationMap;
	}

	public PkPeak getPkPeak() {
		if (pkPeak == null) {
			pkPeak = new PkPeak();
		}
		return pkPeak;
	}

	public void setPkPeak(PkPeak peak) {
		this.pkPeak = peak;
	}

}
