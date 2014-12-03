package jp.massbank.spectrumsearch.logic;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import jp.massbank.spectrumsearch.db.accessor.InstrumentAccessor;
import jp.massbank.spectrumsearch.entity.constant.Constant;
import jp.massbank.spectrumsearch.entity.type.MassBankRecordLine;
import jp.massbank.spectrumsearch.file.entity.MassBankRecord;
import jp.massbank.spectrumsearch.util.MassBankRecordReader;

import org.apache.log4j.Logger;

public class MassBankRecordLogic {
	
	private static final Logger LOGGER = Logger.getLogger(MassBankRecordLogic.class);
	private InstrumentAccessor instrumentAccessor;
	
	public MassBankRecordLogic() {
		this.instrumentAccessor = new InstrumentAccessor();
	}
	
	public void syncFilesRecordsByFolderPath(String pathname) {
//		DbUtil.createSchemaIfNotExist();
		
//		this.instrumentAccessor.deleteAll();
		
//		Instrument oInstrument = this.instrumentAccessor.getInstrumentByType("TYPE_A");
//    	if (oInstrument == null) {
//    		Instrument i = new Instrument();
//    		i.setType("TYPE_A");
//    		i.setName("NAME_A");
//    		this.instrumentAccessor.insertInstrument(i);
//    	}
		
		syncFolderInfo(pathname);
		
//		List<Instrument> instruments = instrumentAccessor.getAllInstruments();
//		for (Instrument instrument : instruments) {
//			System.out.println(instrument.getType());
//		}
	}
	
	private void syncFolderInfo(String pathname) {
		long s = System.currentTimeMillis();
		File f = new File(pathname);
		File[] listfiles = f.listFiles();
//		for (int i = 0; i < 3; i++) {
		for (int i = 0; i < listfiles.length; i++) {
			File item = listfiles[i];
			if (! item.isHidden()) {
		        if (item.isDirectory()) {
		        	LOGGER.info("start sync folder -> " + item);
		        	
		            File[] internalFiles = item.listFiles();
		            for (int j = 0; j < 1; j++) {
//	            	for (int j = 0; j < internalFiles.length; j++) {
		            	File item2 = internalFiles[j];
		            	if (! item2.isHidden()) {
			                if (item2.isDirectory()) {
			                    String name = item2.getAbsolutePath();
			                    syncFolderInfo(name);
			                } else {
			                	// open connection
			                	MassBankRecord massBankRecord = getFileRecordByFile(item2);
			                	// commit or rollback all data
			                	// close connection
//			                	Instrument oInstrument = this.instrumentAccessor.getInstrumentByType(recordData.getInstrumentType());
//			                	if (oInstrument == null) {
//			                		Instrument instrument = new Instrument();
//			                		instrument.setType(recordData.getInstrumentType());
//			                		instrument.setName(recordData.getInstrumentName());
//			                		this.instrumentAccessor.insertInstrument(instrument);
//			                	}
			                }
		            	}
		            }
	            	LOGGER.info("end sync folder -> " + item);
		        } else {
		        	LOGGER.info(item);
		        }
			}
		}
		LOGGER.info("time duration to read files : " + (System.currentTimeMillis() - s)/1000 + "s");
	}
	
	private MassBankRecord getFileRecordByFile(File file) {
		long s = System.currentTimeMillis();
		long lineCount = 0;
		
		MassBankRecord result = new MassBankRecord();
		try {
			FileInputStream fstream = (FileInputStream) getFileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream, Constant.ENCODING));
			
			MassBankRecordLine multipleLine = null;
			String headerLine = null;
			List<String> followingLines = null;
			
			String strLine;
			while ((strLine = br.readLine()) != null) {
				lineCount++;
				
				MassBankRecordLine recordLine = MassBankRecordReader.getRecordLine(strLine);
				if (recordLine != null) {
					if ((MassBankRecordLine.EOF != recordLine) || 
							((MassBankRecordLine.EOF == recordLine) && (multipleLine != null && followingLines != null))) {
						if (recordLine.isMultipleLine()) {
							multipleLine = recordLine;
							headerLine = strLine;
						} else if (recordLine.isFollowingLine()) {
							if (followingLines == null) {
								followingLines = new ArrayList<String>();
							}
							followingLines.add(strLine);
						} else {
							if (multipleLine != null) {
								readMultipleTypeLine(multipleLine, headerLine, followingLines, result);
								multipleLine = null;
								headerLine = null;
								followingLines = null;
							}
							readSingleTypeLine(recordLine, strLine, result);
						}
					}
				} else {
					LOGGER.warn("no read : (" + file.getName() + ") :" + strLine);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		LOGGER.info("read a file: (" + file.getName() + ") - lines : (" + lineCount + ") " + (System.currentTimeMillis() - s) + "ms");
		return result;
	}
	
	private void readMultipleTypeLine(MassBankRecordLine multipleLine, String headerValue, List<String> followingLines, MassBankRecord result) {
		switch (multipleLine) {
		case PK$ANNOTATION:
			result.setPkAnnotationMap(MassBankRecordReader.getValueAsMapList(headerValue, followingLines, multipleLine));
			break;
		case PK$PEAK:
			result.getPkPeak().setPeakMap(MassBankRecordReader.getValueAsMapList(headerValue, followingLines, multipleLine));
			break;
		default:
			break;
		}
	}
	
	private void readSingleTypeLine(MassBankRecordLine recordLine, String line, MassBankRecord result) {
		switch (recordLine) {
		case ACCESSION:
			result.setId(MassBankRecordReader.getValueAsString(line, recordLine));
			break;
		case RECORD_TITLE:
			result.setTitle(MassBankRecordReader.getValueAsString(line, recordLine));
			break;
		case DATE:
			result.setDate(MassBankRecordReader.getValueAsString(line, recordLine));
			break;
		case AUTHORS:
			result.setAuthors(MassBankRecordReader.getValueAsStringList(line, recordLine));
			break;
		case LICENSE:
			result.setLicense(MassBankRecordReader.getValueAsString(line, recordLine));
			break;
		case PUBLICATION:
			result.getPublications().add(MassBankRecordReader.getValueAsString(line, recordLine));
			break;
		case COPYRIGHT:
			result.getCopyrights().add(MassBankRecordReader.getValueAsString(line, recordLine));
			break;
		case COMMENT:
			result.getComments().add(MassBankRecordReader.getValueAsString(line, recordLine));
			break;
		case CH$NAME:
			result.getChNames().add(MassBankRecordReader.getValueAsString(line, recordLine));
			break;
		case CH$COMPOUND_CLASS:
			result.setChCompoundClass(MassBankRecordReader.getValueAsString(line, recordLine));
			break;
		case CH$FORMULA:
			result.setChFormula(MassBankRecordReader.getValueAsString(line, recordLine));
			break;
		case CH$EXACT_MASS:
			result.setChExtractMass(MassBankRecordReader.getValueAsFloat(line, recordLine));
			break;
		case CH$SMILES:
			result.setChSmiles(MassBankRecordReader.getValueAsString(line, recordLine));
			break;
		case CH$IUPAC:
			result.setChIupac(MassBankRecordReader.getValueAsString(line, recordLine));
			break;
		case CH$LINK:
			result.getChLinkMap().putAll(MassBankRecordReader.getValueAsMapEntry(line, recordLine));
			break;
		case SP$SCIENTIFIC_NAME:
			result.setSpScientificName(MassBankRecordReader.getValueAsString(line, recordLine));
			break;
		case SP$NAME:
			result.setSpName(MassBankRecordReader.getValueAsString(line, recordLine));
			break;
		case SP$LINEAGE:
			result.setSpLineage(MassBankRecordReader.getValueAsString(line, recordLine));
			break;
		case SP$LINK:
			result.getSpLinkMap().putAll(MassBankRecordReader.getValueAsMapEntry(line, recordLine));
			break;
		case SP$SAMPLE:
			result.setSpSample(MassBankRecordReader.getValueAsString(line, recordLine));
			break;
		case AC$INSTRUMENT:
			result.getAcInstrument().setName(MassBankRecordReader.getValueAsString(line, recordLine));
			break;
		case AC$INSTRUMENT_TYPE:
			result.getAcInstrument().setType(MassBankRecordReader.getValueAsString(line, recordLine));
			break;
		case AC$MASS_SPECTROMETRY:
			result.getAcMassSpectrometryMap().putAll(MassBankRecordReader.getValueAsMapEntry(line, recordLine));
			break;
		case MS$FOCUSED_ION:
			result.getMsFocusedIonMap().putAll(MassBankRecordReader.getValueAsMapEntry(line, recordLine));
			break;
		case MS$DATA_PROCESSING:
			result.getMsDataProcessingMap().putAll(MassBankRecordReader.getValueAsMapEntry(line, recordLine));
			break;
		case PK$NUM_PEAK:
			result.getPkPeak().setNo(MassBankRecordReader.getValueAsInteger(line, recordLine));
			break;
		default:
			break;
		}
	}
	
//	private FileRecord getFileRecordByFile(File file) {
//		FileRecord result = new FileRecord();
//		
//		List<String> contents = getFileContentsWithSpecialCharactor(file, Constant.Seperator.DOLLAR);
//		for (String line : contents) {
//			if (FileRecordReader.isInstrumentNameLine(line)) {
//				result.getInstrument().setName(FileRecordReader.getInstrumentNameValue(line));
//			} else if (FileRecordReader.isInstrumentTypeLine(line)) {
//				result.getInstrument().setType(FileRecordReader.getInstrumentTypeValue(line));
//			}
//		}
//		return result;
//	}
	
	private List<String> getFileContentsWithSpecialCharactor(File file, String oChar) {
		List<String> contents = new ArrayList<String>();
		try {
			FileInputStream fstream = (FileInputStream) getFileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream, Constant.ENCODING));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				if (strLine.contains(oChar)) {
					contents.add(strLine);
				}
			}
			fstream.close();
		} catch (UnsupportedEncodingException e) {
//			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
//			LOGGER.error(e.getMessage(), e);
		}
		return contents;
	}
	
	private InputStream getFileInputStream(File file) {
		if (file == null) {
			return new ByteArrayInputStream(new byte[0]);
		}
		try {
			return new FileInputStream(file);
		} catch (IOException e) {
			throw new RuntimeException("error : " + file);
		}
	}

}
