package jp.massbank.spectrumsearch.logic;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.massbank.spectrumsearch.db.accessor.DbAccessor;
import jp.massbank.spectrumsearch.db.accessor.InstrumentAccessor;
import jp.massbank.spectrumsearch.db.accessor.MassSpectrometryAccessor;
import jp.massbank.spectrumsearch.db.accessor.PeakAccessor;
import jp.massbank.spectrumsearch.db.accessor.RecordAccessor;
import jp.massbank.spectrumsearch.db.entity.Instrument;
import jp.massbank.spectrumsearch.db.entity.MassSpectrometry;
import jp.massbank.spectrumsearch.db.entity.Peak;
import jp.massbank.spectrumsearch.db.entity.Record;
import jp.massbank.spectrumsearch.entity.constant.Constant;
import jp.massbank.spectrumsearch.entity.type.MassBankRecordLine;
import jp.massbank.spectrumsearch.file.entity.MassBankRecord;
import jp.massbank.spectrumsearch.util.DbUtil;
import jp.massbank.spectrumsearch.util.MassBankRecordReader;

import org.apache.log4j.Logger;

public class MassBankRecordLogic {
	
	private static final Logger LOGGER = Logger.getLogger(MassBankRecordLogic.class);
	private RecordAccessor recordAccessor;
	private InstrumentAccessor instrumentAccessor;
	private MassSpectrometryAccessor massSpectrometryAccessor;
	private PeakAccessor peakAccessor;
	
	public MassBankRecordLogic() {
		this.recordAccessor = new RecordAccessor();
		this.instrumentAccessor = new InstrumentAccessor();
		this.massSpectrometryAccessor = new MassSpectrometryAccessor();
		this.peakAccessor = new PeakAccessor();
	}
	
	public void syncFilesRecordsByFolderPath(String pathname) {
		DbUtil.createSchemaIfNotExist();
		
		try {
			DbAccessor.createConnection();
			this.recordAccessor.deleteAll();
			this.instrumentAccessor.deleteAll();
			this.massSpectrometryAccessor.deleteAll();
			this.peakAccessor.deleteAll();
			DbAccessor.closeConnection();
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		syncFolderInfo(pathname);
		
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
//		        	LOGGER.info("start sync folder -> " + item);
		        	
		            File[] internalFiles = item.listFiles();
		            for (int j = 0; j < 1; j++) {
//	            	for (int j = 0; j < internalFiles.length; j++) {
		            	File item2 = internalFiles[j];
		            	if (! item2.isHidden()) {
			                if (item2.isDirectory()) {
			                    String name = item2.getAbsolutePath();
			                    syncFolderInfo(name);
			                } else {
			                	// read the file content
			                	MassBankRecord massBankRecord = getFileRecordByFile(item2);
//			                	validateMassBankRecord(massBankRecord);
			                	// open connection
			                	try {
									DbAccessor.createConnection();
//									DbAccessor.setAutoCommit(false);
									// TODO implement rollback feature
									
									// save record
									Record record = new Record();
									record.setId(massBankRecord.getId());
									record.setTitle(massBankRecord.getTitle());
									this.recordAccessor.insertRecord(record);
									// save instrument
				                	Instrument oInstrument = this.instrumentAccessor.getInstrumentByType(massBankRecord.getAcInstrument().getType());
				                	if (oInstrument == null) {
				                		Instrument instrument = new Instrument();
				                		instrument.setType(massBankRecord.getAcInstrument().getType());
				                		instrument.setName(massBankRecord.getAcInstrument().getName());
				                		instrument.setRecordId(massBankRecord.getId());
				                		this.instrumentAccessor.insertInstrument(instrument);
				                	}
				                	// save mass spectrometry
				                	if (massBankRecord.getAcMassSpectrometryMap() != null) {
				                		for (Entry<String, String> entry : massBankRecord.getAcMassSpectrometryMap().entrySet()) {
				                			MassSpectrometry massSpectrometry = new MassSpectrometry();
				                			massSpectrometry.setType(entry.getKey());
				                			massSpectrometry.setValue(entry.getValue());
				                			massSpectrometry.setRecordId(massBankRecord.getId());
				                			massSpectrometryAccessor.insertMassSpectrometry(massSpectrometry);
				                		}
				                	}
				                	// save peak
				                	if (massBankRecord.getPkPeak().getPeakMap() != null) {
				                		for (Map<String, String> pValue : massBankRecord.getPkPeak().getPeakMap()) {
				                			Peak peak = new Peak();
				                			peak.setMz(Double.parseDouble(pValue.get("m/z")));
				                			peak.setIntensity(Double.parseDouble(pValue.get("int.")));
				                			peak.setRelativeIntensity(Integer.parseInt(pValue.get("rel.int.")));
				                			this.peakAccessor.insertPeak(peak);
				                		}
				                	}
				                	
									// commit or rollback all data
//									DbAccessor.commit();
									// close connection
								} catch (SQLException e) {
									LOGGER.error(e.getMessage(), e);
//									try {
//										DbAccessor.rollback();
//									} catch (SQLException e1) {
//										LOGGER.error(e.getMessage(), e);
//									}
								} finally {
									try {
										DbAccessor.closeConnection();
									} catch (SQLException e) {
										LOGGER.error(e.getMessage(), e);
									}
								}
			                }
		            	}
		            }
//	            	LOGGER.info("end sync folder -> " + item);
		        } else {
		        	LOGGER.info(item);
		        }
			}
		}
		LOGGER.info("time duration to read files : " + (System.currentTimeMillis() - s)/1000 + "s");
	}
	
	private void validateMassBankRecord(MassBankRecord massBankRecord) {
		if (massBankRecord != null) {
		if (massBankRecord.getPkPeak() != null && massBankRecord.getPkPeak().getPeakMap() != null) {
			for (Map<String, String> map : massBankRecord.getPkPeak().getPeakMap()) {
				if (map.keySet().size() > 3) {
					LOGGER.error("invalid peak data : " + massBankRecord.getId());
				}
			}
		} else {
			LOGGER.error("no peak data : " + massBankRecord.getId());
		}
		}
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
//					LOGGER.warn("no read : (" + file.getName() + ") :" + strLine);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
//		LOGGER.info("read a file: (" + file.getName() + ") - lines : (" + lineCount + ") " + (System.currentTimeMillis() - s) + "ms");
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
