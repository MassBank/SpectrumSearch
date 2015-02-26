package jp.massbank.spectrumsearch.logic.search;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.massbank.spectrumsearch.accessor.CompoundAccessor;
import jp.massbank.spectrumsearch.accessor.DbAccessor;
import jp.massbank.spectrumsearch.accessor.InstrumentAccessor;
import jp.massbank.spectrumsearch.accessor.MsTypeAccessor;
import jp.massbank.spectrumsearch.entity.constant.Constant;
import jp.massbank.spectrumsearch.entity.db.Compound;
import jp.massbank.spectrumsearch.entity.db.Instrument;
import jp.massbank.spectrumsearch.entity.db.MsType;
import jp.massbank.spectrumsearch.entity.db.Peak;
import jp.massbank.spectrumsearch.entity.param.HitPeak;
import jp.massbank.spectrumsearch.entity.param.ResScore;
import jp.massbank.spectrumsearch.entity.param.SearchQueryParam;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class SearchLogic {
	
	private static final Logger LOGGER = Logger.getLogger(SearchLogic.class);
	
	private List<String> queryMz;	// list of PK$PEAK : m/z
	private List<Double> queryVal;	// list of PK$PEAK : rel.int.
	private Map<String, List<HitPeak>> mapHitPeak;
	private Map<String, Integer> mapMzCnt;
	private List<ResScore> vecScore;
	private double m_fLen;
	private double m_fSum;
	private int m_iCnt;
	private boolean isQuick;
	private boolean isInteg;
	private boolean isAPI;
	
	private CompoundAccessor compoundAccessor;
	private InstrumentAccessor instrumentAccessor;
	private MsTypeAccessor msTypeAccessor;
	
	public SearchLogic() {
		this.queryMz = new ArrayList<String>();
		this.queryVal = new ArrayList<Double>();
		this.mapHitPeak = new HashMap<String, List<HitPeak>>();
		this.mapMzCnt = new HashMap<String, Integer>();
		this.vecScore = new ArrayList<ResScore>();
		
		this.compoundAccessor = new CompoundAccessor();
		this.instrumentAccessor = new InstrumentAccessor();
		this.msTypeAccessor = new MsTypeAccessor();
	}

	public ArrayList<String> getSearchResult(SearchQueryParam param) {
		ArrayList<String> result = new ArrayList<String>();
		try {
			LOGGER.debug("start search result");
			long s1 = System.currentTimeMillis();
			DbAccessor.createConnection();
			long s2 = System.currentTimeMillis();
			LOGGER.debug("open connection : " + (s2-s1) + " ms");
			setQueryPeak(param);
			long s3 = System.currentTimeMillis();
			LOGGER.debug("setQueryPeak : " + (s3-s2) + " ms");
			if (!searchPeak(param)) {
				return result;
			}
			long s4 = System.currentTimeMillis();
			LOGGER.debug("searchPeak : " + (s4-s3) + " ms");
			setScore(param);
			long s5 = System.currentTimeMillis();
			LOGGER.debug("setScore : " + (s5-s4) + " ms");
			result.addAll(outResult());
			long s6 = System.currentTimeMillis();
			LOGGER.debug("outResult : " + (s6-s5) + " ms");
			DbAccessor.closeConnection();
			long s7 = System.currentTimeMillis();
			LOGGER.debug("close connection : " + (s7-s6) + " ms");
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return result;
	}
	
	private void setQueryPeak(SearchQueryParam param) {
		String[] peaks = param.getPeak().split("@");
		for (String peak : peaks) {
			String[] pVals = peak.split(",");
			String sMz = pVals[0];
			double fMz = Float.parseFloat(sMz); 		// PK$PEAK : m/z
			double fVal = Float.parseFloat(pVals[1]); 	// PK$PEAK : rel.int.
			
			if ( fVal < 1 ) {
				fVal = 1;
			} else if ( fVal > 999 ) {
				fVal = 999;
			}
			
			if ( fVal < param.getCutoff() ) {
				continue;
			}
			
			if ( param.getWeight() == Constant.PARAM_WEIGHT_LINEAR ) {
				fVal *= fMz / 10;
			} else if ( param.getWeight() == Constant.PARAM_WEIGHT_SQUARE ) {
				fVal *= fMz * fMz / 100;
			}
			
			if ( param.getNorm() == Constant.PARAM_NORM_LOG ) {
				fVal = Math.log(fVal);
			} else if ( param.getNorm() == Constant.PARAM_NORM_SQRT ) {
				fVal = Math.sqrt(fVal);
			}
			
			if ( fVal > 0 ) {
				queryMz.add( sMz );
				queryVal.add( fVal );
				m_fLen += fVal * fVal;
				m_fSum += fVal;
				m_iCnt++;
			}
			
		}
		
		if ( m_iCnt - 1 < param.getThreshold() ) {
			param.setThreshold(m_iCnt - 1);
		}
	}
	
	private boolean searchPeak(SearchQueryParam param) {
		String sql = StringUtils.EMPTY;
		
		//----------------------------------------------------------------
		// precursor m/zでの絞り込みに使用する検索条件を用意しておく
		//----------------------------------------------------------------
		/*String sqlw1 = StringUtils.EMPTY;
		boolean isPre = false;
		if ( param.getPrecursor() > 0 ) {
			isPre = true;
			int pre1 = param.getPrecursor() - 1;
			int pre2 = param.getPrecursor() + 1;
			sqlw1 = String.format(" and (S.PRECURSOR_MZ is not null and S.PRECURSOR_MZ between %d and %d)", pre1, pre2);
		}
		
		//----------------------------------------------------------------
		// MS TYPEでの絞り込みに使用する検索条件を用意しておく
		//----------------------------------------------------------------
		String sqlw2 = StringUtils.EMPTY;
		boolean isMsType = false;
		if ( StringUtils.isNotBlank(param.getMstype()) && param.getMstype().endsWith("ALL") ) {
			// MS_TYPEカラム有無チェック
			isMsType = true;
			String ms = param.getMstype().replaceAll(",", "','");
			sqlw2 = String.format(" and R.MS_TYPE in ('%s')", ms);
		}
		
		// 検索対象ALLの場合*/
		boolean isFilter = false;
		List<String> vecTargetId = new ArrayList<String>();
		/*if ( StringUtils.isBlank(param.getInstType()) || !param.getInstType().endsWith("ALL") ) {
			if ( param.getIon() != 0 ) {
				sql = "select R.RECORD_ID from RECORD R, SPECTRUM S where R.RECORD_ID = S.RECORD_ID and S.ION_MODE = ";
				sql += param.getIon();
				// precursor m/z絞り込み条件セット
				if ( isPre ) {
					sql += sqlw1;
				}
				if ( isMsType ) {
					sql += sqlw2;
				}
				sql += " order by R.RECORD_ID";
				
				List<Map<Integer, Object>> result = dbExecuteSql( sql );
				long lNumRows = result.size();

				//** 検索対象のIDがないので終了
				if ( lNumRows == 0 ) {
					return false;
				}

				isFilter = true;

				//--------------------------------------------------------
				// 検索対象のIDを格納
				//--------------------------------------------------------
				for (Map<Integer, Object> rowResult : result) {
					vecTargetId.add(String.valueOf(rowResult.get(1)));
				}
				
			}
		}*/
		// 検索対象ALL以外の場合
		/*else {
			//------------------------------------------------------------
			// (1) 検索対象のINSTRUMENT_TYPEが存在するかチェック
			//------------------------------------------------------------		
			String[] vecInstType = param.getInstType().split(",");
			StringBuilder sbInstType = new StringBuilder();
			for (String instType : vecInstType ) {
				if ( ! instType.equals("ALL") ) {
					sbInstType.append("'");
					sbInstType.append(instType);
					sbInstType.append("',");
				}
			}
			String instTypeQueryParam = sbInstType.toString();
			instTypeQueryParam = removeLastChar(instTypeQueryParam);
			sql = "select INSTRUMENT_ID from INSTRUMENT where INSTRUMENT_TYPE in (" + instTypeQueryParam + ")";
			
			List<Map<Integer, Object>> result = dbExecuteSql( sql );
			long lNumRows = result.size();
			//** 検索対象のINSTRUMENT TYPEがないので終了
			if ( lNumRows == 0 ) {
				return false;
			}
			
			//------------------------------------------------------------
			// (2) 検索対象のINSTRUMENT_IDのレコードが存在するかチェック
			//------------------------------------------------------------
			String instNo = StringUtils.EMPTY;
			for (Map<Integer, Object> rowResult : result) {
				instNo += String.valueOf(rowResult.get(1)).concat(",");
			}
			instNo = removeLastChar(instNo);
			
			if (param.getIon() == 0) {
				sql = "select R.RECORD_ID from RECORD R, SPECTRUM S where R.RECORD_ID = S.RECORD_ID and R.INSTRUMENT_ID in (" + instNo + ")";
			} else {
				sql = "select R.RECORD_ID from RECORD R, SPECTRUM S where R.RECORD_ID = S.RECORD_ID and S.ION_MODE = ";
				sql += param.getIon();
				sql += " and R.INSTRUMENT_ID in (";
				sql += instNo;
				sql += ")";
			}
			
			// precursor m/z絞り込み条件セット
			if (isPre) {
				sql += sqlw1;
			}
			if (isMsType) {
				sql += sqlw2;
			}

			sql += " order by R.RECORD_ID";
			
			List<Map<Integer, Object>> result2 = dbExecuteSql(sql);
			lNumRows = result2.size();

			//** 検索対象のIDがないので終了
			if ( lNumRows == 0 ) {
				return false;
			}
			
			isFilter = true;
			//------------------------------------------------------------
			// (3) 検索対象のIDを格納
			//------------------------------------------------------------
			for (Map<Integer, Object> rowResult : result2) {
				vecTargetId.add(String.valueOf(rowResult.get(1)));
			}
		}*/
		
		Integer precursor1 = null;
		Integer precursor2 = null;
		if (param.getPrecursor() > 0) {
			precursor1 = param.getPrecursor() - 1;
			precursor2 = param.getPrecursor() + 1;
		}
		List<String> instrumentTypes = Arrays.asList(param.getInstType().split(","));
		List<String> msTypes = Arrays.asList(param.getMstype().split(","));
		List<Integer> instrumentIds = new ArrayList<Integer>();
		List<Integer> msTypeIds = new ArrayList<Integer>();
		if (!instrumentTypes.contains("ALL")) {
			List<Instrument> oInstruments = this.instrumentAccessor.getInstrumentsByTypes(instrumentTypes);
			for (Instrument oInstrument : oInstruments) {
				instrumentIds.add(oInstrument.getId());
			}
		}
		if (!msTypes.contains("ALL")) {
			List<MsType> oMsTypes = this.msTypeAccessor.getMsTypesByNames(msTypes);
			for (MsType oMsType : oMsTypes) {
				msTypeIds.add(oMsType.getId());
			}
		}
		List<Compound> oCompoundList = this.compoundAccessor.getCompoundList(precursor1, precursor2, 
				param.getIon(), instrumentIds, msTypeIds);
		for (Compound compound : oCompoundList) {
			vecTargetId.add(compound.getId());
		}
		
		//---------------------------------------------------
		// ピーク値取得
		//---------------------------------------------------
		double fMin;
		double fMax;
		String sqlw = StringUtils.EMPTY;
		
		for (int i = 0; i < queryMz.size(); i++) {
			String strMz = queryMz.get(i);
			double fMz = Float.parseFloat(strMz);
			double fVal = queryVal.get(i);
			
			float fTolerance = param.getTolerance();
			if (param.getTolUnit().equals("unit")) {
				fMin = fMz - fTolerance;
				fMax = fMz + fTolerance;
			} else {
				fMin = fMz * (1 - fTolerance / 1000000);
				fMax = fMz * (1 + fTolerance / 1000000);
			}
			fMin -= 0.00001;
			fMax += 0.00001;
			
			if (isInteg) {
				sql = "select SPECTRUM_NO, max(RELATIVE), MZ from PARENT_PEAK where ";
				sqlw = String.format("RELATIVE >= %d and (MZ between %.6f and %.6f) group by SPECTRUM_NO",
						param.getCutoff(), fMin, fMax);
			} else {
				sql = "SELECT max(concat(lpad(castinteger(" + Peak.Columns.RELATIVE_INTENSITY + "), 3, ' ') || ' ' || " + Peak.Columns.COMPOUND_ID + " || ' ' || castdouble(" + Peak.Columns.MZ + "))) FROM " + Peak.TABLE + " WHERE ";
				sqlw = String.format(Peak.Columns.RELATIVE_INTENSITY + " >= %d AND (" + Peak.Columns.MZ + " BETWEEN %.6f and %.6f) GROUP BY " + Peak.Columns.COMPOUND_ID, param.getCutoff(), fMin, fMax);
			}
			sql += sqlw;
			List<Map<Integer, Object>> result = dbExecuteSql(sql);
			
			for (Map<Integer, Object> rowResult : result) {
				String[] vacVal = String.valueOf(rowResult.get(1)).trim().split(" ");
				String strId = vacVal[1];
				
				if (isFilter) {
					boolean isFound = vecTargetId.contains(strId);
					if (!isFound) {
						continue;
					}
				}
				
				double fHitVal = Float.parseFloat(vacVal[0]);
				String strHitMz = vacVal[2];
				double fHitMz = Float.parseFloat(strHitMz);
				
				if ( param.getWeight() == Constant.PARAM_WEIGHT_LINEAR ) {
					fHitVal *= fHitVal / 10;
				} else if ( param.getWeight() == Constant.PARAM_WEIGHT_SQUARE ) {
					fHitVal *= fHitMz * fHitMz / 100;
				}
				if ( param.getNorm() == Constant.PARAM_NORM_LOG ) {
					fHitVal = Math.log(fHitVal);
				} else if ( param.getNorm() == Constant.PARAM_NORM_SQRT ) {
					fHitVal = Math.sqrt(fHitVal);
				}
				
				// クエリとヒットしたピークのm/z, rel.int.を格納 
				HitPeak pHitPeak = new HitPeak();
				pHitPeak.setqMz(strMz);
				pHitPeak.setqVal(fVal);
				pHitPeak.setHitMz(strHitMz);
				pHitPeak.setHitVal(fHitVal);
				
				if (!mapHitPeak.containsKey(strId)) {
					mapHitPeak.put(strId, new ArrayList<HitPeak>());
				}
				mapHitPeak.get(strId).add(pHitPeak);
				
				String key = String.format("%s %s", strId, strHitMz);
				if (mapMzCnt.containsKey(key)) {
					mapMzCnt.put(key, mapMzCnt.get(key) + 1);
				} else { 
					mapMzCnt.put(key, 1);
				}
			}
			
		}
		return true;
	}
	
	
//	private boolean searchPeak(SearchQueryParam param) {
//		String sql;
//		
//		//----------------------------------------------------------------
//		// precursor m/zでの絞り込みに使用する検索条件を用意しておく
//		//----------------------------------------------------------------
//		String sqlw1 = StringUtils.EMPTY;
//		boolean isPre = false;
//		if ( param.getPrecursor() > 0 ) {
//			isPre = true;
//			int pre1 = param.getPrecursor() - 1;
//			int pre2 = param.getPrecursor() + 1;
//			sqlw1 = String.format(" and (S.PRECURSOR_MZ is not null and S.PRECURSOR_MZ between %d and %d)", pre1, pre2);
//		}
//		
//		//----------------------------------------------------------------
//		// MS TYPEでの絞り込みに使用する検索条件を用意しておく
//		//----------------------------------------------------------------
//		String sqlw2 = StringUtils.EMPTY;
//		boolean isMsType = false;
//		if ( StringUtils.isNotBlank(param.getMstype()) && param.getMstype().endsWith("ALL") ) {
//			// MS_TYPEカラム有無チェック
//			isMsType = true;
//			String ms = param.getMstype().replaceAll(",", "','");
//			sqlw2 = String.format(" and R.MS_TYPE in ('%s')", ms);
//		}
//		
//		// 検索対象ALLの場合
//		boolean isFilter = false;
//		List<String> vecTargetId = new ArrayList<String>();
//		if ( StringUtils.isBlank(param.getInstType()) || !param.getInstType().endsWith("ALL") ) {
//			if ( param.getIon() != 0 ) {
//				sql = "select R.RECORD_ID from RECORD R, SPECTRUM S where R.RECORD_ID = S.RECORD_ID and S.ION_MODE = ";
//				sql += param.getIon();
//				// precursor m/z絞り込み条件セット
//				if ( isPre ) {
//					sql += sqlw1;
//				}
//				if ( isMsType ) {
//					sql += sqlw2;
//				}
//				sql += " order by R.RECORD_ID";
//				
//				List<Map<Integer, Object>> result = dbExecuteSql( sql );
//				long lNumRows = result.size();
//
//				//** 検索対象のIDがないので終了
//				if ( lNumRows == 0 ) {
//					return false;
//				}
//
//				isFilter = true;
//
//				//--------------------------------------------------------
//				// 検索対象のIDを格納
//				//--------------------------------------------------------
//				for (Map<Integer, Object> rowResult : result) {
//					vecTargetId.add(String.valueOf(rowResult.get(1)));
//				}
//				
////			 	for ( long l = 0; l < lNumRows; l++ ) {
////					MYSQL_ROW fields = mysql_fetch_row( resMySql );
////					vecTargetId.add(fields[0]);
////				}
////				// 結果セット解放
////				mysql_free_result( resMySql );
//			}
//		//● 検索対象ALL以外の場合
//		} else {
//			//------------------------------------------------------------
//			// (1) 検索対象のINSTRUMENT_TYPEが存在するかチェック
//			//------------------------------------------------------------		
//			String[] vecInstType = param.getInstType().split(",");
//			StringBuilder sbInstType = new StringBuilder();
//			for (String instType : vecInstType ) {
//				if ( ! instType.equals("ALL") ) {
//					sbInstType.append("'");
//					sbInstType.append(instType);
//					sbInstType.append("',");
//				}
//			}
//			String instTypeQueryParam = sbInstType.toString();
//			instTypeQueryParam = removeLastChar(instTypeQueryParam);
//			sql = "select INSTRUMENT_ID from INSTRUMENT where INSTRUMENT_TYPE in(" + instTypeQueryParam + ")";
//			
//			List<Map<Integer, Object>> result = dbExecuteSql( sql );
//			long lNumRows = result.size();
//
//			//** 検索対象のINSTRUMENT TYPEがないので終了
//			if ( lNumRows == 0 ) {
//				return false;
//			}
//
//			//------------------------------------------------------------
//			// (2) 検索対象のINSTRUMENT_IDのレコードが存在するかチェック
//			//------------------------------------------------------------
//			String instNo = StringUtils.EMPTY;
//			for (Map<Integer, Object> rowResult : result) {
//				instNo += String.valueOf(rowResult.get(1)).concat(",");
//			}
//			instNo = removeLastChar(instNo);
//			
////		 	for ( long l = 0; l < lNumRows; l++ ) {
////				MYSQL_ROW fields = mysql_fetch_row( resMySql );
////				instNo += fields[0];
////				if ( l < lNumRows - 1 ) {
////					instNo += ",";
////				}
////			}
////			// 結果セット解放
////			mysql_free_result( resMySql );
//
//			if (param.getIon() == 0) {
//				sql = "select R.RECORD_ID from RECORD R, SPECTRUM S where R.RECORD_ID = S.RECORD_ID and R.INSTRUMENT_ID in (" + instNo + ")";
//			} else {
//				sql = "select R.RECORD_ID from RECORD R, SPECTRUM S where R.RECORD_ID = S.RECORD_ID and S.ION_MODE = ";
//				sql += param.getIon();
//				sql += " and R.INSTRUMENT_ID in (";
//				sql += instNo;
//				sql += ")";
//			}
//			// precursor m/z絞り込み条件セット
//			if (isPre) {
//				sql += sqlw1;
//			}
//			if (isMsType) {
//				sql += sqlw2;
//			}
//
//			sql += " order by R.RECORD_ID";
//			
////			List<Map<Integer, Object>> result2 = new ArrayList<Map<Integer,Object>>();
////			boolean loop = true;
////			int startIndex = 0;
////			int limit = 1000;
////			while (loop) {
////				List<Map<Integer, Object>> oResult = dbExecuteSql(sql + " OFFSET " + startIndex + " ROWS FETCH NEXT " + limit + " ROWS ONLY");
////				result2.addAll(oResult);
////				if (oResult.size() == limit) {
////					startIndex += limit;
////				} else {
////					// oResult.size() < limit
////					loop = false;
////				}
////			}
//			
//			List<Map<Integer, Object>> result2 = dbExecuteSql(sql);
//			lNumRows = result2.size();
//
//			//** 検索対象のIDがないので終了
//			if ( lNumRows == 0 ) {
//				return false;
//			}
//
//			isFilter = true;
//			//------------------------------------------------------------
//			// (3) 検索対象のIDを格納
//			//------------------------------------------------------------
//			for (Map<Integer, Object> rowResult : result2) {
//				vecTargetId.add(String.valueOf(rowResult.get(1)));
//			}
//			
////		 	for ( long l = 0; l < lNumRows; l++ ) {
////				MYSQL_ROW fields = mysql_fetch_row( resMySql );
////				vecTargetId.push_back(fields[0]);
////			}
////			// 結果セット解放
////			mysql_free_result( resMySql );
//		}
//		
//		//---------------------------------------------------
//		// ピーク値取得
//		//---------------------------------------------------
//		double fMin;
//		double fMax;
//		String sqlw = StringUtils.EMPTY;
//		List<String> sqls = new ArrayList<String>();
//		
//		for (int i = 0; i < queryMz.size(); i++) {
//			String strMz = queryMz.get(i);
//			double fMz = Float.parseFloat(strMz);
//			double fVal = queryVal.get(i);
//
//			float fTolerance = param.getTolerance();
//			if (param.getTolUnit().equals("unit")) {
//				fMin = fMz - fTolerance;
//				fMax = fMz + fTolerance;
//			} else {
//				fMin = fMz * (1 - fTolerance / 1000000);
//				fMax = fMz * (1 + fTolerance / 1000000);
//			}
//			fMin -= 0.00001;
//			fMax += 0.00001;
//
//			if (isInteg) {
//				sql = "select SPECTRUM_NO, max(RELATIVE), MZ from PARENT_PEAK where ";
//				sqlw = String.format("RELATIVE >= %d and (MZ between %.6f and %.6f) group by SPECTRUM_NO",
//						param.getCutoff(), fMin, fMax);
//			} else {
////				sql = String.format("select P1.RELATIVE_INTENSITY, P1.MZ, P1.RECORD_ID from PEAK P1 "
////						+ "RIGHT OUTER JOIN "
////						+ "(select max(P.RELATIVE_INTENSITY) MAX_RELATIVE_INTENSITY, P.RECORD_ID from PEAK P where P.RELATIVE_INTENSITY >= %d and (P.MZ between %.6f and %.6f) group by P.RECORD_ID) P2 "
////						+ "on P1.RELATIVE_INTENSITY = P2.MAX_RELATIVE_INTENSITY and P1.RECORD_ID = P2.RECORD_ID", param.getCutoff(), fMin, fMax);
////				sql = "select strmax(castinteger(RELATIVE_INTENSITY) || ' ' || castdouble(MZ)), RECORD_ID from PEAK where ";
////				sqlw = String.format("RELATIVE_INTENSITY >= %d and (MZ between %.6f and %.6f) group by RECORD_ID", param.getCutoff(), fMin, fMax);
//				
////				sql = "select max(concat(castinteger(RELATIVE_INTENSITY) || ' ' || castdouble(MZ))), RECORD_ID, " + strMz + ", " + fVal + " from PEAK where ";
////				sqlw = String.format("RELATIVE_INTENSITY >= %d and (MZ between %.6f and %.6f) group by RECORD_ID", param.getCutoff(), fMin, fMax);
//				
////				sql = "select max(concat(castinteger(RELATIVE_INTENSITY) || ' ' || castdouble(MZ))), RECORD_ID, " + strMz + ", " + fVal + " from PEAK where ";
////				sqlw = String.format("RELATIVE_INTENSITY >= %d and (MZ between %.6f and %.6f) group by RECORD_ID", param.getCutoff(), fMin, fMax);
//				
////				sql = String.format("SELECT RELATIVE_INTENSITY, MZ, RECORD_ID, " + strMz + ", " + fVal + " from PEAK WHERE RELATIVE_INTENSITY IN (SELECT MAX(RELATIVE_INTENSITY) FROM PEAK WHERE RELATIVE_INTENSITY >= %d and (MZ between %.6f and %.6f) group by RECORD_ID)", param.getCutoff(), fMin, fMax);
////				sqlw = "";
//				
//				sql = "select max(concat(castinteger(RELATIVE_INTENSITY) || ' ' || castdouble(MZ) || ' ' || RECORD_ID || ' ' || '" + String.valueOf(strMz) + "' || ' ' || '" + String.valueOf(fVal) + "')) from PEAK where ";
//				sqlw = String.format("RELATIVE_INTENSITY >= %d and (MZ between %.6f and %.6f) group by RECORD_ID", param.getCutoff(), fMin, fMax);
//				
//			}
//			sql += sqlw;
//			sqls.add(sql);
//		}
//		
//		long s = System.currentTimeMillis();
//		int limit = 1;
//		for (int i = 0; i < sqls.size(); i = i + limit) {
//			int max = Math.min(sqls.size(), i + limit);
//			String subSql = StringUtils.join(sqls.subList(i, max), " UNION ALL ");
//			List<Map<Integer, Object>> result = dbExecuteSql(subSql);
//			
//			LOGGER.debug("counting peak; dbExecuteSql :" + (System.currentTimeMillis() - s) + "ms");
//			List<QueryResultHitPeak> qrHitPeaks = new ArrayList<QueryResultHitPeak>();
//			for (Map<Integer, Object> rowResult : result) {
//				QueryResultHitPeak qrHitPeak = new QueryResultHitPeak();
//				String[] vacVal = String.valueOf(rowResult.get(1)).trim().split(" ");
//				
//				qrHitPeak.setRecordId(vacVal[2]);
//				qrHitPeak.setHitRelInt(Float.parseFloat( vacVal[0] ));
//				qrHitPeak.setHitMz(vacVal[1]);
//				qrHitPeak.setMz(vacVal[3]);
//				qrHitPeak.setValue(Float.parseFloat(vacVal[4]));
//				
////				qrHitPeak.setRecordId(String.valueOf(rowResult.get(2)).trim());
////				qrHitPeak.setHitRelInt(Float.parseFloat(vacVal[0]));
////				qrHitPeak.setHitMz(vacVal[1]);
////				qrHitPeak.setMz(String.valueOf(rowResult.get(3)));
////				qrHitPeak.setValue(Float.parseFloat(String.valueOf(rowResult.get(4))));
//				
////				qrHitPeak.setRecordId(String.valueOf(rowResult.get(3)).trim());
////				qrHitPeak.setHitRelInt(Float.parseFloat(String.valueOf(rowResult.get(1))));
////				qrHitPeak.setHitMz(String.valueOf(rowResult.get(2)));
////				qrHitPeak.setMz(String.valueOf(rowResult.get(4)));
////				qrHitPeak.setValue(Float.parseFloat(String.valueOf(rowResult.get(5))));
//				
//				qrHitPeaks.add(qrHitPeak);
//			}
//			
//			LOGGER.debug("counting peak; read max :" + (System.currentTimeMillis() - s) + "ms");
//
//			int prevAryNum = 0;
//			for (QueryResultHitPeak grHitPeak : qrHitPeaks) {
//				String recordId = grHitPeak.getRecordId();
//
//				if ( isFilter ) {
//					boolean isFound = false;
//					for ( int j = prevAryNum; j < vecTargetId.size(); j++ ) {
//						if ( recordId.equals(vecTargetId.get(j)) ) {
//							isFound = true;
//							prevAryNum = j + 1;
//							break;
//						}
//					}
//					if ( !isFound ) {
//						continue;
//					}
//				}
//
//				double fHitVal = grHitPeak.getHitRelInt();
//				String strHitMz = grHitPeak.getHitMzString();
//				double fHitMz = Float.parseFloat( strHitMz );
//
//				if ( param.getWeight() == Constant.PARAM_WEIGHT_LINEAR ) {
//					fHitVal *= fHitVal / 10;
//				} else if ( param.getWeight() == Constant.PARAM_WEIGHT_SQUARE ) {
//					fHitVal *= fHitMz * fHitMz / 100;
//				}
//				if ( param.getNorm() == Constant.PARAM_NORM_LOG ) {
//					fHitVal = Math.log(fHitVal);
//				} else if ( param.getNorm() == Constant.PARAM_NORM_SQRT ) {
//					fHitVal = Math.sqrt(fHitVal);
//				}
//
//				// クエリとヒットしたピークのm/z, rel.int.を格納 
//				HitPeak pHitPeak = new HitPeak();
//				pHitPeak.setqMz(grHitPeak.getMzString());
//				pHitPeak.setqVal(grHitPeak.getValue());
//				pHitPeak.setHitMz(strHitMz);
//				pHitPeak.setHitVal(fHitVal);
//				
//				if (!mapHitPeak.containsKey(recordId)) {
//					mapHitPeak.put(recordId, new ArrayList<HitPeak>());
//				}
//				mapHitPeak.get(recordId).add(pHitPeak); 
//
//				String key = String.format("%s %s", recordId, strHitMz);
//				if (mapMzCnt.containsKey(key)) {
//					mapMzCnt.put(key, mapMzCnt.get(key) + 1);
//				} else {
//					mapMzCnt.put(key, 1);
//				}
//				
//			}
//		}
//		LOGGER.debug("counting peak:" + (System.currentTimeMillis() - s) + "ms");
//		
//		/*int index = 0;
//		StringBuilder sb = new StringBuilder();
//		for (String oSql : sqls) {
//			
//			if (StringUtils.isNotBlank(sb.toString())) {
//				sb.append(" UNION ALL ");
//			}
//			sb.append(oSql);
//			
//			if (index > 0 && (index % 100 == 0 || (index + 1) == sqls.size())) {
//				List<Map<Integer, Object>> result = dbExecuteSql(sb.toString());
//				
//				List<QueryResultHitPeak> qrHitPeaks = new ArrayList<QueryResultHitPeak>();
//				for (Map<Integer, Object> rowResult : result) {
//					QueryResultHitPeak qrHitPeak = new QueryResultHitPeak();
//					String[] vacVal = String.valueOf(rowResult.get(1)).trim().split(" ");
//					qrHitPeak.setRecordId(String.valueOf(rowResult.get(2)).trim());
//					qrHitPeak.setHitRelInt(Float.parseFloat( vacVal[0] ));
//					qrHitPeak.setHitMz(vacVal[1]);
//					qrHitPeak.setMz(String.valueOf(rowResult.get(3)));
//					qrHitPeak.setValue(Float.parseFloat(String.valueOf(rowResult.get(4))));
//					qrHitPeaks.add(qrHitPeak);
//				}
//	
//				int prevAryNum = 0;
//				for (QueryResultHitPeak grHitPeak : qrHitPeaks) {
//					String recordId = grHitPeak.getRecordId();
//	
//					if ( isFilter ) {
//						boolean isFound = false;
//						for ( int j = prevAryNum; j < vecTargetId.size(); j++ ) {
//							if ( recordId.equals(vecTargetId.get(j)) ) {
//								isFound = true;
//								prevAryNum = j + 1;
//								break;
//							}
//						}
//						if ( !isFound ) {
//							continue;
//						}
//					}
//	
//					double fHitVal = grHitPeak.getHitRelInt();
//					String strHitMz = grHitPeak.getHitMzString();
//					double fHitMz = Float.parseFloat( strHitMz );
//	
//					if ( param.getWeight() == Constant.PARAM_WEIGHT_LINEAR ) {
//						fHitVal *= fHitVal / 10;
//					} else if ( param.getWeight() == Constant.PARAM_WEIGHT_SQUARE ) {
//						fHitVal *= fHitMz * fHitMz / 100;
//					}
//					if ( param.getNorm() == Constant.PARAM_NORM_LOG ) {
//						fHitVal = Math.log(fHitVal);
//					} else if ( param.getNorm() == Constant.PARAM_NORM_SQRT ) {
//						fHitVal = Math.sqrt(fHitVal);
//					}
//	
//					// クエリとヒットしたピークのm/z, rel.int.を格納 
//					HitPeak pHitPeak = new HitPeak();
//					pHitPeak.setqMz(grHitPeak.getMzString());
//					pHitPeak.setqVal(grHitPeak.getValue());
//					pHitPeak.setHitMz(strHitMz);
//					pHitPeak.setHitVal(fHitVal);
//					
//					if (!mapHitPeak.containsKey(recordId)) {
//						mapHitPeak.put(recordId, new ArrayList<HitPeak>());
//					}
//					mapHitPeak.get(recordId).add(pHitPeak); 
//	
//					String key = String.format("%s %s", recordId, strHitMz);
//					if (mapMzCnt.containsKey(key)) {
//						mapMzCnt.put(key, mapMzCnt.get(key) + 1);
//					} else { 
//						mapMzCnt.put(key, 1);
//					}
//					
//				}
//	//			// 結果セット解放
//	//			mysql_free_result( resMySql );
//				sb = new StringBuilder();
//			}
//			index++;
//		}*/
//		return true;
//	}
	
	private void setScore(SearchQueryParam param) {
		String sql = StringUtils.EMPTY;
		List<HitPeak> vecHitPeak = new ArrayList<HitPeak>();

		String tblName = "PEAK";
//		if ( existHeapTable("PEAK_HEAP") ) {
//			tblName = "PEAK_HEAP";
//		}

		for (Entry<String, List<HitPeak>> pEntry : mapHitPeak.entrySet()) {
			String strId = pEntry.getKey();
			
			// 同一IDのヒットピークを取り出す
			vecHitPeak = pEntry.getValue();
			
			// ヒットピーク数がスレシホールド以下の場合は除外
			int iHitNum = vecHitPeak.size();
			if ( iHitNum <= param.getThreshold() ) {
				continue;
			}
			
			double fSum = 0;
			double fLen = 0;
			int iCnt = 0;
			
			// ヒットしたスペクトルのピークをDBより取得
			if ( isInteg ) {
				sql = String.format("select MZ, RELATIVE from PARENT_PEAK where SPECTRUM_NO = %s and RELATIVE >= %d", strId, param.getCutoff());
			} else {
				sql = String.format("SELECT " + Peak.Columns.MZ + ", " + Peak.Columns.RELATIVE_INTENSITY + " FROM %s WHERE " + Peak.Columns.COMPOUND_ID + " = '%s' AND " + Peak.Columns.RELATIVE_INTENSITY + " >= %d", tblName, strId, param.getCutoff());
			}
			
			
			List<Map<Integer, Object>> result = dbExecuteSql(sql);
			long lNumRows = result.size();
			
			for (Map<Integer, Object> rowResult : result) {
				String strMz = String.valueOf(rowResult.get(1));
				String strRelInt = String.valueOf(rowResult.get(2));;
				double fMz = Double.parseDouble(strMz);
				double fVal = Double.parseDouble(strRelInt);
				
				if ( param.getWeight() == Constant.PARAM_WEIGHT_LINEAR ) {
					fVal *= fMz / 10;
				} else if ( param.getWeight() == Constant.PARAM_WEIGHT_SQUARE ) {
					fVal *= fMz * fMz / 100;
				}
				if ( param.getNorm() == Constant.PARAM_NORM_LOG ) {
					fVal = Math.log(fVal);
				} else if ( param.getNorm() == Constant.PARAM_NORM_SQRT ) {
					fVal = Math.sqrt(fVal);
				}
				
				String key = StringUtils.EMPTY;
				key = String.format("%s %s", strId, strMz);
				Integer iMul = mapMzCnt.get(key);
				if ( iMul == null || iMul == 0 ) {
					iMul = 1;
				}
				fLen += fVal * fVal * iMul;
				fSum += fVal * iMul;
				iCnt += iMul;
			}
			
			// スコアセット
			double dblScore = 0;
			if ( param.getColType().equals("COSINE") ) {
				double fCos = 0;
				for (HitPeak pHitPeak : vecHitPeak) {
					fCos += (double)(pHitPeak.getqVal() * pHitPeak.getHitVal());
				}
				if ( m_fLen * fLen == 0 ) {
					dblScore = 0;
				} else {
					dblScore = fCos / Math.sqrt(m_fLen * fLen);
				}
			}
			if ( dblScore >= 0.9999 ) {
				// doubleで扱えるのは15桁までのため、小数部は12桁とする
				dblScore = 0.999999999999;
			} else if ( dblScore < 0 ) {
				dblScore = 0;
			}
			
			ResScore resScore = new ResScore();
			resScore.setCompoundId(strId);
			resScore.setScore(iHitNum + dblScore);
			vecScore.add(resScore);
			
		}

	}
	
	private List<String> outResult() {
		List<String> result = new ArrayList<String>();
//		String sql = StringUtils.EMPTY;
		for (ResScore resScore : vecScore) {
			StringBuilder sb = new StringBuilder();
			
			Compound compound = this.compoundAccessor.getCompoundById(resScore.getCompoundId());
			if (compound != null) {
				sb.append(String.format("%s\t%s\t%.12f\t%s", compound.getId(), compound.getTitle(), resScore.getScore(), compound.getIonMode()));
				sb.append( "\n" );
			}
			

			/*if ( isQuick || isAPI ) {
				sql = String.format("select S.TITLE, S.ION_MODE, R.FORMULA, R.EXACT_MASS from SPECTRUM S, RECORD R where S.RECORD_ID = R.RECORD_ID and S.RECORD_ID='%s'", resScore.getCompoundId());
			} else if ( isInteg ) {
				sql = String.format("select TITLE, ION_MODE, RECORD_ID from SPECTRUM where SPECTRUM_ID=%s", resScore.getCompoundId());
			} else {
				sql = String.format("select TITLE, ION_MODE from COMPOUND where COMPOUND_ID='%s'", resScore.getCompoundId());
//				sql = String.format("select TITLE, ION_MODE from SPECTRUM where RECORD_ID='%s'", resScore.getRecordId());
			}

			List<Map<Integer,Object>> dbResult = dbExecuteSql( sql );
			long lNumRows = dbResult.size();
			if ( lNumRows > 0 ) {
				Map<Integer,Object> resultRow = dbResult.get(0);
				String strName = String.valueOf(resultRow.get(1));
				String strIon = String.valueOf(resultRow.get(2));
				String strId = StringUtils.EMPTY;
				if ( isInteg ) {
					strId = String.valueOf(resultRow.get(3));
				} else {
					strId = resScore.getCompoundId();
				}

				sb.append(String.format("%s\t%s\t%.12f\t%s", strId, strName, resScore.getScore(), strIon));
				if ( isQuick || isAPI ) {
					String formula = String.valueOf(resultRow.get(3));
					sb.append(String.format("\t%s", formula));
				}
				if ( isAPI ) {
					String emass = String.valueOf(resultRow.get(4));
					sb.append(String.format("\t%s", emass));
				}
				sb.append( "\n" );
			}*/
			String line = sb.toString();
			if (StringUtils.isNotBlank(line)) {
				result.add(line);
			}
		}
		return result;
	}
	
	/*private String removeLastChar(String str) {
		if (StringUtils.isNotBlank(str)) {
			return str.substring(0, str.length() - 1);
		}
		return StringUtils.EMPTY;
	}*/

	private List<Map<Integer,Object>> dbExecuteSql(String sql) {
		return DbAccessor.execResultQuery(sql);
	}

}