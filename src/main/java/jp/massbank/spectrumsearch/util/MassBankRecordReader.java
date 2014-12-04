package jp.massbank.spectrumsearch.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.massbank.spectrumsearch.entity.type.MassBankRecordLine;

import org.apache.log4j.Logger;

public class MassBankRecordReader {
	
	private static final Logger LOGGER = Logger.getLogger(MassBankRecordReader.class);
	
	private static final String EMPTY = "";
	private static final String DATE_PATTERN = "yyyy-MM-dd";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);
	
	public static MassBankRecordLine getRecordLine(String line) {
		for (MassBankRecordLine recordLine : MassBankRecordLine.values()) {
			if (line.startsWith(recordLine.getKey().concat(":")) || line.startsWith(recordLine.getKey().concat(";"))) {
				return recordLine;
			}
		}
		return null;
	}
	
	public static String getValueAsString(String line, MassBankRecordLine recordLine) {
		return line.replace(recordLine.getKey().concat(":"), EMPTY).replace(recordLine.getKey().concat(";"), EMPTY).trim();
	}
	
	public static List<String> getValueAsStringList(String line, MassBankRecordLine recordLine) {
		String commaSeperatedString = getValueAsString(line, recordLine);
		List<String> result = new ArrayList<>();
		for (String val : commaSeperatedString.split(",")) {
			result.add(val.trim());
		}
		return result;
	}
	
	public static Date getValueAsDate(String line, MassBankRecordLine recordLine) {
		try {
			String strFormat = getValueAsString(line, recordLine);
			return DATE_FORMAT.parse(strFormat);
		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static Float getValueAsFloat(String line, MassBankRecordLine recordLine) {
		return Float.valueOf(getValueAsString(line, recordLine));
	}
	
	public static Integer getValueAsInteger(String line, MassBankRecordLine recordLine) {
		return Integer.valueOf(getValueAsString(line, recordLine));
	}
	
	public static Map<String, String> getValueAsMapEntry(String line, MassBankRecordLine recordLine) {
		String value = getValueAsString(line, recordLine);
		String[] arr = value.split(" ");
		Map<String,String> result = new HashMap<String, String>();
		result.put(arr[0], arr[1]);
		return result;
	}
	
	public static List<Map<String,String>> getValueAsMapList(String headerLine, List<String> followingLines, MassBankRecordLine recordLine) {
		List<Map<String, String>> result = new ArrayList<Map<String,String>>();
		String headerValue = getValueAsString(headerLine, recordLine);
		String[] headerValues = headerValue.split(" ");
		if (followingLines != null) {
			for (String followingLine : followingLines) {
				if (followingLine != null) {
					String followingValue = getValueAsString(followingLine, MassBankRecordLine.FOLLOWING_LINE);
					String[] followingValues = followingValue.split(" ");
					Map<String, String> map = new LinkedHashMap<String, String>();
					if (headerValues.length == followingValues.length) {
						for (int i = 0; i < headerValues.length; i++) {
							map.put(headerValues[i], followingValues[i]);
						}
					} else {
//						LOGGER.warn("there is a problem in multiple line value. (" + headerLine + ")");
					}
					result.add(map);
				} else {
//					LOGGER.warn("there is a problem in following line value. (" + followingLine + ")");
				}
			}
		} else {
//			LOGGER.warn("there is a problem in following lines of multiple line. (" + headerValue + ")");
		}
		return result;
	}
	
//	public static boolean isAccessionLine(String line) {
//		return isLine(line, MassBankRecordLine.ACCESSION);
//	}
//	
//	public static String getAccessionValue(String line) {
//		return getLineValue(line, MassBankRecordLine.ACCESSION);
//	}
//	
//	public static boolean isTitleLine(String line) {
//		return isLine(line, MassBankRecordLine.RECORD_TITLE);
//	}
//	
//	public static String getTitleValue(String line) {
//		return getLineValue(line, MassBankRecordLine.RECORD_TITLE);
//	}
//	
//	public static boolean isDateLine(String line) {
//		return isLine(line, MassBankRecordLine.DATE);
//	}
//	
//	public static String getDateValue(String line) {
//		return getLineValue(line, MassBankRecordLine.DATE);
//	}
//	
//	public static boolean isAuthorsLine(String line) {
//		return isLine(line, MassBankRecordLine.AUTHORS);
//	}
//	
//	public static String getAuthorsValue(String line) {
//		return getLineValue(line, MassBankRecordLine.AUTHORS);
//	}
//	
//	public static boolean isLicenseLine(String line) {
//		return isLine(line, MassBankRecordLine.LICENSE);
//	}
//	
//	public static String getLicenseValue(String line) {
//		return getLineValue(line, MassBankRecordLine.LICENSE);
//	}
//	
//	public static boolean isCopyrightLine(String line) {
//		return isLine(line, MassBankRecordLine.COPYRIGHT);
//	}
//	
//	public static String getCopyrightValue(String line) {
//		return getLineValue(line, MassBankRecordLine.COPYRIGHT);
//	}
//	
//	public static boolean isCommentLine(String line) {
//		return isLine(line, MassBankRecordLine.COMMENT);
//	}
//	
//	public static String getCommentValue(String line) {
//		return getLineValue(line, MassBankRecordLine.COMMENT);
//	}
//	
//	public static boolean isChemicalNameLine(String line) {
//		return isLine(line, MassBankRecordLine.CH$NAME);
//	}
//	
//	public static String getChemicalNameValue(String line) {
//		return getLineValue(line, MassBankRecordLine.CH$NAME);
//	}
//	
//	public static boolean isChemicalCompoundClassLine(String line) {
//		return isLine(line, MassBankRecordLine.CH$COMPOUND_CLASS);
//	}
//	
//	public static String getChemicalCompoundClassValue(String line) {
//		return getLineValue(line, MassBankRecordLine.CH$COMPOUND_CLASS);
//	}
//	
//	public static boolean isChemicalFormulaLine(String line) {
//		return isLine(line, MassBankRecordLine.CH$FORMULA);
//	}
//	
//	public static String getChemicalFormulaValue(String line) {
//		return getLineValue(line, MassBankRecordLine.CH$FORMULA);
//	}
//	
//	public static boolean isChemicalExactMassLine(String line) {
//		return isLine(line, MassBankRecordLine.CH$EXACT_MASS);
//	}
//	
//	public static String getChemicalExactMassValue(String line) {
//		return getLineValue(line, MassBankRecordLine.CH$EXACT_MASS);
//	}
//	
//	public static boolean isChemicalSmilesLine(String line) {
//		return isLine(line, MassBankRecordLine.CH$SMILES);
//	}
//	
//	public static String getChemicalSmilesValue(String line) {
//		return getLineValue(line, MassBankRecordLine.CH$SMILES);
//	}
//	
//	public static boolean isChemicalIupacLine(String line) {
//		return isLine(line, MassBankRecordLine.CH$IUPAC);
//	}
//	
//	public static String getChemicalIupacValue(String line) {
//		return getLineValue(line, MassBankRecordLine.CH$IUPAC);
//	}
//	
//	public static boolean isChemicalLinkLine(String line) {
//		return isLine(line, MassBankRecordLine.CH$LINK);
//	}
//	
//	public static String getChemicalLinkValue(String line) {
//		return getLineValue(line, MassBankRecordLine.CH$LINK);
//	}
//	
//	public static boolean isInstrumentNameLine(String line) {
//		return isLine(line, MassBankRecordLine.AC$INSTRUMENT);
//	}
//	
//	public static String getInstrumentNameValue(String line) {
//		return getLineValue(line, MassBankRecordLine.AC$INSTRUMENT);
//	}
//	
//	public static boolean isInstrumentTypeLine(String line) {
//		return isLine(line, MassBankRecordLine.AC$INSTRUMENT_TYPE);
//	}
//	
//	public static String getInstrumentTypeValue(String line) {
//		return getLineValue(line, MassBankRecordLine.AC$INSTRUMENT_TYPE);
//	}
//	
//	public static boolean isMassSpectrometryLine(String line) {
//		return isLine(line, MassBankRecordLine.AC$MASS_SPECTROMETRY);
//	}
//	
//	public static String getMassSpectrometryValue(String line) {
//		return getLineValue(line, MassBankRecordLine.AC$MASS_SPECTROMETRY);
//	}
//	
//	public static boolean isFocusedIonLine(String line) {
//		return isLine(line, MassBankRecordLine.MS$FOCUSED_ION);
//	}
//	
//	public static String getFocusedIonValue(String line) {
//		return getLineValue(line, MassBankRecordLine.MS$FOCUSED_ION);
//	}
//	
//	public static boolean isDataProcessingLine(String line) {
//		return isLine(line, MassBankRecordLine.MS$DATA_PROCESSING);
//	}
//	
//	public static String getDataProcessingValue(String line) {
//		return getLineValue(line, MassBankRecordLine.MS$DATA_PROCESSING);
//	}
//	
//	public static boolean isAnnotationLine(String line) {
//		return isLine(line, MassBankRecordLine.PK$ANNOTATION);
//	}
//	
//	public static String getAnnotationValue(String line) {
//		return getLineValue(line, MassBankRecordLine.PK$ANNOTATION);
//	}
//	
//	public static boolean isPeakCountLine(String line) {
//		return isLine(line, MassBankRecordLine.PK$NUM_PEAK);
//	}
//	
//	public static String getPeakCountValue(String line) {
//		return getLineValue(line, MassBankRecordLine.PK$NUM_PEAK);
//	}
//	
//	public static boolean isPeakLine(String line) {
//		return isLine(line, MassBankRecordLine.PK$PEAK);
//	}
//	
//	public static String getPeakValue(String line) {
//		return getLineValue(line, MassBankRecordLine.PK$PEAK);
//	}
	
	// private
	
//	private static boolean isLine(String line, MassBankRecordLine recordLine) {
//		return line.startsWith(recordLine.getKey());
//	}
//	
//	private static String getLineValue(String line, MassBankRecordLine recordLine) {
//		return line.replace(recordLine.getKey().concat(":"), EMPTY).trim();
//	}

}
