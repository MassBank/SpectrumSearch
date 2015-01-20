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

import jp.massbank.spectrumsearch.accessor.DbAccessor;
import jp.massbank.spectrumsearch.accessor.InstrumentAccessor;
import jp.massbank.spectrumsearch.accessor.MassSpectrometryAccessor;
import jp.massbank.spectrumsearch.accessor.MsTypeAccessor;
import jp.massbank.spectrumsearch.accessor.PeakAccessor;
import jp.massbank.spectrumsearch.accessor.RecordAccessor;
import jp.massbank.spectrumsearch.accessor.SpectrumAccessor;
import jp.massbank.spectrumsearch.entity.constant.Constant;
import jp.massbank.spectrumsearch.entity.constant.SystemProperties;
import jp.massbank.spectrumsearch.entity.db.Instrument;
import jp.massbank.spectrumsearch.entity.db.MassSpectrometry;
import jp.massbank.spectrumsearch.entity.db.MsType;
import jp.massbank.spectrumsearch.entity.db.Peak;
import jp.massbank.spectrumsearch.entity.db.Record;
import jp.massbank.spectrumsearch.entity.db.Spectrum;
import jp.massbank.spectrumsearch.entity.file.MassBankRecord;
import jp.massbank.spectrumsearch.entity.type.IonMode;
import jp.massbank.spectrumsearch.entity.type.MassBankRecordLine;
import jp.massbank.spectrumsearch.util.MassBankRecordReader;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class MassBankRecordLogic {
	
	private static final Logger LOGGER = Logger.getLogger(MassBankRecordLogic.class);
	private RecordAccessor recordAccessor;
	private InstrumentAccessor instrumentAccessor;
	private MassSpectrometryAccessor massSpectrometryAccessor;
	private PeakAccessor peakAccessor;
	private SpectrumAccessor spectrumAccessor;
	private MsTypeAccessor msTypeAccessor;
	
	public MassBankRecordLogic() {
		this.recordAccessor = new RecordAccessor();
		this.instrumentAccessor = new InstrumentAccessor();
		this.massSpectrometryAccessor = new MassSpectrometryAccessor();
		this.peakAccessor = new PeakAccessor();
		this.spectrumAccessor = new SpectrumAccessor();
		this.msTypeAccessor = new MsTypeAccessor();
	}
	
	public void upgradeAndResetDatabase() {
		try {
			DbAccessor.createConnection();
			dropTableIndexes();
			syncDatabaseSchema();
			clearTableData();
			createTableIndexes();
			DbAccessor.closeConnection();
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public void syncDatabaseSchema() {
		recordAccessor.dropTable();
		recordAccessor.createTable();
		
		instrumentAccessor.dropTable();
		instrumentAccessor.createTable();
		
		massSpectrometryAccessor.dropTable();
		massSpectrometryAccessor.createTable();
		
		peakAccessor.dropTable();
		peakAccessor.createTable();
		
		spectrumAccessor.dropTable();
		spectrumAccessor.createTable();
		
		msTypeAccessor.dropTable();
		msTypeAccessor.createTable();
	}
	
	public void clearTableData() {
		this.recordAccessor.deleteAll();
		this.instrumentAccessor.deleteAll();
		this.massSpectrometryAccessor.deleteAll();
		this.peakAccessor.deleteAll();
		this.spectrumAccessor.deleteAll();
		this.msTypeAccessor.deleteAll();
	}
	
	public void dropTableIndexes() {
		DbAccessor.execUpdate("DROP INDEX IDX_PEAK_RECORD");
		DbAccessor.execUpdate("DROP INDEX IDX_PEAK_MZ");
		DbAccessor.execUpdate("DROP INDEX IDX_PEAK_RELATIVE_INTENSITY");
		DbAccessor.execUpdate("DROP INDEX IDX_SPECTRUM_RECORD");
	}
	
	public void createTableIndexes() {
		DbAccessor.execUpdate("CREATE INDEX IDX_PEAK_RECORD ON PEAK (RECORD_ID)");
		DbAccessor.execUpdate("CREATE INDEX IDX_PEAK_MZ ON PEAK (MZ)");
		DbAccessor.execUpdate("CREATE INDEX IDX_PEAK_RELATIVE_INTENSITY ON PEAK (RELATIVE_INTENSITY)");
		DbAccessor.execUpdate("CREATE INDEX IDX_SPECTRUM_RECORD ON SPECTRUM (RECORD_ID)");
	}
	
	public void syncFilesRecordsByFolderPath(String pathname) {
		try {
			// open connection
			DbAccessor.createConnection();
			syncFolderInfo(pathname);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				// close connection
				DbAccessor.closeConnection();
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		
	}
	
	public int getTotalFileCountInFolder(String pathname) {
		File f = new File(pathname);
		return getFilesCount(f);
	}
	
	public static int getFilesCount(File file) {
		int count = 0;
		if (! file.getName().equals(SystemProperties.getInstance().getDatabaseName())) {
			File[] files = file.listFiles();
			for (File f : files) {
				if (! f.isHidden()) {
					if (f.isDirectory()) {
						count += getFilesCount(f);
					} else {
						count++;
					}
				}
			}
		}
		return count;
	}
	
	public void mergeMassBankRecordIntoDb(File mbFile, List<Instrument> instruments, List<MsType> msTypes) {
		// read the file content
		long s = System.currentTimeMillis();
    	MassBankRecord massBankRecord = getFileRecordByFile(mbFile);
    	
    	if (massBankRecord.isAvailable()) {
    		
    		try {
    			
    			// INSTRUMENT
    			Instrument oInstrument = null;
    			for (Instrument instrument : instruments) {
    				if (instrument.getType().equals(massBankRecord.getAcInstrument().getType())) {
    					oInstrument = instrument;
    				}
    			}
    			// merge INSTRUMENT if not exist
    			if (oInstrument == null) {
    				Instrument instrument = new Instrument();
    				instrument.setType(massBankRecord.getAcInstrument().getType());
    				this.instrumentAccessor.insert(instrument);
    				
    				oInstrument = this.instrumentAccessor.getInstrument(massBankRecord.getAcInstrument().getType());
    				instruments.add(oInstrument);
    			}
    			
    			// MS_TYPE
    			String strMsType = massBankRecord.getAcMassSpectrometryMap().get("MS_TYPE");
    			
    			MsType oMsType = null;
    			for (MsType msType : msTypes) {
    				if (msType.getName().equals(strMsType)) {
    					oMsType = msType;
    				}
    			}
    			// merge MS_TYPE if not exist
    			if (oMsType == null) {
    				MsType msType = new MsType();
    				msType.setName(strMsType);
    				this.msTypeAccessor.insert(msType);
    				
    				oMsType = this.msTypeAccessor.getMsTypeByName(strMsType);
    				msTypes.add(oMsType);
    			}
    			
    			DbAccessor.setAutoCommit(false);
    			
    			// RECORD
    			Record record = new Record();
    			record.setId(massBankRecord.getId());
    			record.setTitle(massBankRecord.getTitle());
    			record.setMsType(strMsType);
    			record.setFormula(massBankRecord.getChFormula());
    			record.setExactMass(massBankRecord.getChExtractMass());
    			record.setInstrumentId(oInstrument.getId());
    			this.recordAccessor.addBatchInsert(record);
    			
    			/*// MASS_SPECTROMETRY
    			if (massBankRecord.getAcMassSpectrometryMap() != null) {
    				List<MassSpectrometry> massSpectrometries = new ArrayList<MassSpectrometry>();
    				for (Entry<String, String> entry : massBankRecord.getAcMassSpectrometryMap().entrySet()) {
    					MassSpectrometry massSpectrometry = new MassSpectrometry();
    					massSpectrometry.setType(entry.getKey());
    					massSpectrometry.setValue(entry.getValue());
    					massSpectrometry.setRecordId(massBankRecord.getId());
    				}
    				if (massSpectrometries.size() > 0) {
//    					massSpectrometryAccessor.addBatchInsert(massSpectrometries);
    					massSpectrometryAccessor.executeBatchInsert(massSpectrometries);
    				}
    			}*/
    			
    			// PEAK
    			if (massBankRecord.getPkPeak().getPeakMap() != null) {
    				List<Peak> peaks = new ArrayList<Peak>();
    				for (Map<String, String> pValue : massBankRecord.getPkPeak().getPeakMap()) {
    					Peak peak = new Peak();
    					peak.setMz(Double.parseDouble(pValue.get("m/z")));
    					peak.setIntensity(Double.parseDouble(pValue.get("int.")));
    					peak.setRelativeIntensity(Integer.parseInt(pValue.get("rel.int.")));
    					peak.setRecordId(massBankRecord.getId());
    					peaks.add(peak);
    				}
    				if (peaks.size() > 0) {
    					this.peakAccessor.executeBatchInsert(peaks);
//    					this.peakAccessor.addBatchInsert(peaks);
    				}
    			}
    			
    			try {
    				// SPECTRUM
    				String strPrecursorMz = massBankRecord.getMsFocusedIonMap().get("PRECURSOR_M/Z");
    				String strIonMode = massBankRecord.getAcMassSpectrometryMap().get("ION_MODE");
	    			if (StringUtils.isNotBlank(strPrecursorMz) && StringUtils.isNotBlank(strIonMode)) {
	    				Spectrum spectrum = new Spectrum();
	    				spectrum.setTitle(massBankRecord.getTitle());
	    				spectrum.setPrecursorMz(Float.parseFloat(strPrecursorMz));
	    				spectrum.setIonMode(IonMode.parseInt(strIonMode));
	    				spectrum.setRecordId(massBankRecord.getId());
	    				this.spectrumAccessor.addBatchInsert(spectrum);
	    			} else {
	    				LOGGER.warn("No Spectrum Info.: " + massBankRecord.getId());
	    			}
    			} catch (NumberFormatException e) {
    				LOGGER.error(e.getMessage(), e);
    			}
    			
    			LOGGER.debug("before executeBatch (" + mbFile.getName() + ") - " + (System.currentTimeMillis() - s) + "ms");
    			DbAccessor.executeBatchAndCloseStatment();
    			LOGGER.debug("after executeBatch (" + mbFile.getName() + ") - " + (System.currentTimeMillis() - s) + "ms");
    			// commit
    			DbAccessor.commit();
    			DbAccessor.setAutoCommit(true);
    			LOGGER.debug("after commit (" + mbFile.getName() + ") - " + (System.currentTimeMillis() - s) + "ms");
    		} catch (SQLException e) {
    			LOGGER.error("error in file:" + mbFile.getPath());
    			LOGGER.error(e.getMessage(), e);
				try {
					DbAccessor.rollback();
				} catch (SQLException e1) {
					LOGGER.error(e.getMessage(), e);
				}
    		} finally {
    			
    		}
    	}
	}
	
	private void syncFolderInfo(String pathname) {
		long s = System.currentTimeMillis();

		List<Instrument> instruments = new ArrayList<Instrument>();
		List<MsType> msTypes = new ArrayList<MsType>();
		
		File f = new File(pathname);
		File[] listfiles = f.listFiles();
		for (int i = 0; i < listfiles.length; i++) {
			File item = listfiles[i];
			if (! item.isHidden()) {
		        if (item.isDirectory()) {
		        	String name = item.getAbsolutePath();
		        	syncFolderInfo(name);
//		            File[] internalFiles = item.listFiles();
////		            for (int j = 0; j < 1; j++) {
//	            	for (int j = 0; j < internalFiles.length; j++) {
//		            	File item2 = internalFiles[j];
//		            	if (! item2.isHidden()) {
//			                if (item2.isDirectory()) {
//			                    String name = item2.getAbsolutePath();
//			                    syncFolderInfo(name);
//			                } else {
//			                	// read the file content
//			                	mergeMassBankRecordIntoDb(item2, instruments, msTypes);
//			                }
//		            	}
//		            }
		        } else {
		        	// read the file content
		        	long s1 = System.currentTimeMillis();
                	mergeMassBankRecordIntoDb(item, instruments, msTypes);
                	LOGGER.debug("merge massbank record : " + item.getName() + "(" + (System.currentTimeMillis() - s1) + ")");
		        }
			}
		}
		
		LOGGER.debug("time duration to read files : " + (System.currentTimeMillis() - s)/1000 + "s");
	}
	
//	private void syncFolderInfo(String pathname) {
//		long s = System.currentTimeMillis();
//		
//		int fileCount = getTotalFileCountInFolder(pathname);
//		int count = 0;
//		
//		List<Instrument> instruments = new ArrayList<Instrument>();
//		List<MsType> msTypes = new ArrayList<MsType>();
//		
//		File f = new File(pathname);
//		File[] listfiles = f.listFiles();
//		for (int i = 0; i < listfiles.length; i++) {
//			File item = listfiles[i];
//			if (! item.isHidden()) {
//				if (item.isDirectory()) {
////		        	LOGGER.debug("start sync folder -> " + item);
//					
//					File[] internalFiles = item.listFiles();
//					for (int j = 0; j < 1; j++) {
////	            	for (int j = 0; j < internalFiles.length; j++) {
//						File item2 = internalFiles[j];
//						if (! item2.isHidden()) {
//							if (item2.isDirectory()) {
//								String name = item2.getAbsolutePath();
//								syncFolderInfo(name);
//							} else {
//								// read the file content
//								long s1 = System.currentTimeMillis();
//								MassBankRecord massBankRecord = getFileRecordByFile(item2);
//								long s2 = System.currentTimeMillis();
//								
//								if (massBankRecord.isAvailable()) {
//									
////			                		long maxMemory = Runtime.getRuntime().maxMemory();
////			            			LOGGER.debug(
////			            					"file: " + item2.getPath() +
////			            					"\nAvai. processors (cores): " + Runtime.getRuntime().availableProcessors() +
////			            					", Free mem (bytes): " + Runtime.getRuntime().freeMemory() +
////			            					", Max. mem (bytes): " + (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory) +
////			            					", Tot.mem avai. to JVM (bytes): " + Runtime.getRuntime().totalMemory()
////			            					);
//									
//									try {
//										
//										// INSTRUMENT
//										Instrument oInstrument = null;
//										for (Instrument instrument : instruments) {
//											if (instrument.getType().equals(massBankRecord.getAcInstrument().getType())) {
//												oInstrument = instrument;
//											}
//										}
//										
//										if (oInstrument == null) {
//											Instrument instrument = new Instrument();
//											instrument.setType(massBankRecord.getAcInstrument().getType());
//											this.instrumentAccessor.insert(instrument);
//											
//											oInstrument = this.instrumentAccessor.getInstrument(massBankRecord.getAcInstrument().getType());
//											
//											instruments.add(oInstrument);
//										}
//										
//										// MS_TYPE
//										String strMsType = massBankRecord.getAcMassSpectrometryMap().get("MS_TYPE");
//										
//										MsType oMsType = null;
//										for (MsType msType : msTypes) {
//											if (msType.getName().equals(strMsType)) {
//												oMsType = msType;
//											}
//										}
//										
//										if (oMsType == null) {
//											MsType msType = new MsType();
//											msType.setName(strMsType);
//											this.msTypeAccessor.insert(msType);
//											
//											oMsType = this.msTypeAccessor.getMsTypeByName(strMsType);
//											msTypes.add(oMsType);
//										}
//										
//										DbAccessor.setAutoCommit(false);
//										
//										// RECORD
//										Record record = new Record();
//										record.setId(massBankRecord.getId());
//										record.setTitle(massBankRecord.getTitle());
//										record.setMsType(strMsType);
//										record.setFormula(massBankRecord.getChFormula());
//										record.setExactMass(massBankRecord.getChExtractMass());
//										record.setInstrumentId(oInstrument.getId());
//										this.recordAccessor.addBatchInsert(record);
//										
//										// MASS_SPECTROMETRY
//										if (massBankRecord.getAcMassSpectrometryMap() != null) {
//											List<MassSpectrometry> massSpectrometries = new ArrayList<MassSpectrometry>();
//											for (Entry<String, String> entry : massBankRecord.getAcMassSpectrometryMap().entrySet()) {
//												MassSpectrometry massSpectrometry = new MassSpectrometry();
//												massSpectrometry.setType(entry.getKey());
//												massSpectrometry.setValue(entry.getValue());
//												massSpectrometry.setRecordId(massBankRecord.getId());
//											}
//											if (massSpectrometries.size() > 0) {
//												massSpectrometryAccessor.addBatchInsert(massSpectrometries);
//											}
//										}
//										
//										// PEAK
//										if (massBankRecord.getPkPeak().getPeakMap() != null) {
//											List<Peak> peaks = new ArrayList<Peak>();
//											for (Map<String, String> pValue : massBankRecord.getPkPeak().getPeakMap()) {
//												Peak peak = new Peak();
//												peak.setMz(Double.parseDouble(pValue.get("m/z")));
//												peak.setIntensity(Double.parseDouble(pValue.get("int.")));
//												peak.setRelativeIntensity(Integer.parseInt(pValue.get("rel.int.")));
//												peak.setRecordId(massBankRecord.getId());
//												peaks.add(peak);
//											}
//											if (peaks.size() > 0) {
//												this.peakAccessor.addBatchInsert(peaks);
//											}
//										}
//										
//										// SPECTRUM
//										String strPrecursorMz = massBankRecord.getMsFocusedIonMap().get("PRECURSOR_M/Z");
//										String strIonMode = massBankRecord.getAcMassSpectrometryMap().get("ION_MODE");
//										if (StringUtils.isNotBlank(strPrecursorMz) && StringUtils.isNotBlank(strIonMode)) {
//											Spectrum spectrum = new Spectrum();
//											spectrum.setTitle(massBankRecord.getTitle());
//											try {
//												spectrum.setPrecursorMz(Float.parseFloat(strPrecursorMz));
//											} catch (NumberFormatException e) {
//												LOGGER.error(e.getMessage(), e);
//											}
//											spectrum.setIonMode(IonMode.parseInt(strIonMode));
//											spectrum.setRecordId(massBankRecord.getId());
//											this.spectrumAccessor.addBatchInsert(spectrum);
//										} else {
//											LOGGER.warn("No Spectrum Info.: " + massBankRecord.getId());
//										}
//										
//										DbAccessor.executeBatch();
//										// commit
//										DbAccessor.commit();
//										
//									} catch (SQLException e) {
//										LOGGER.error("error in file:" + item2.getPath());
//										LOGGER.error(e.getMessage(), e);
//										try {
//											DbAccessor.rollback();
//										} catch (SQLException e1) {
//											LOGGER.error(e.getMessage(), e);
//										}
//									}
//									count++;
//									LOGGER.debug("progress... " + count + "/" + fileCount + " (" + (System.currentTimeMillis() - s1) + "ms / " + (s2-s1) + "ms)");
//								}
//							}
//						}
//					}
////	            	LOGGER.debug("end sync folder -> " + item);
//				} else {
//					LOGGER.debug(item);
//				}
//			}
//		}
//		
//		DbAccessor.execUpdate("CREATE INDEX IDX_RECORD_PEAK ON PEAK (RECORD_ID)");
//		
//		LOGGER.debug("time duration to read files : " + (System.currentTimeMillis() - s)/1000 + "s");
//	}
	
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
	
	public MassBankRecord getFileRecordByFile(File file) {
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
		LOGGER.debug("read file: (" + file.getName() + ") - " + lineCount + " lines, " + (System.currentTimeMillis() - s) + "ms");
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
