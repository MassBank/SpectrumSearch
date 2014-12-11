package jp.massbank.spectrumsearch.logic.search;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.massbank.spectrumsearch.db.accessor.DbAccessor;
import jp.massbank.spectrumsearch.entity.constant.Constant;
import jp.massbank.spectrumsearch.entity.param.HitPeak;
import jp.massbank.spectrumsearch.entity.param.ResScore;
import jp.massbank.spectrumsearch.entity.param.SearchQueryParam;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class SearchLogic {
	
	private static final Logger LOGGER = Logger.getLogger(SearchLogic.class);
	
	private List<String> queryMz;
	private List<Double> queryVal;
	private Map<String, List<HitPeak>> mapHitPeak;
	private Map<String, Integer> mapMzCnt;
	private List<ResScore> vecScore;
	private double m_fLen;
	private double m_fSum;
	private int m_iCnt;
	private boolean isQuick;
	private boolean isInteg;
	private boolean isAPI;
	
	public SearchLogic() {
		this.queryMz = new ArrayList<String>();
		this.queryVal = new ArrayList<Double>();
		this.mapHitPeak = new HashMap<String, List<HitPeak>>();
		this.mapMzCnt = new HashMap<String, Integer>();
		this.vecScore = new ArrayList<ResScore>();
	}

	public ArrayList<String> getSearchResult(SearchQueryParam param) {
		ArrayList<String> result = new ArrayList<String>();
		try {
			DbAccessor.createConnection();
			setQueryPeak(param);
			if ( !searchPeak(param) ) {
				return result;
			}
			setScore(param);
			result.addAll(outResult());
			DbAccessor.closeConnection();
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
			double fMz = Float.parseFloat(sMz);
			double fVal = Float.parseFloat(pVals[1]);
			
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
		String sql;
		
		//----------------------------------------------------------------
		// precursor m/zでの絞り込みに使用する検索条件を用意しておく
		//----------------------------------------------------------------
		String sqlw1 = StringUtils.EMPTY;
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
		
		// 検索対象ALLの場合
		boolean isFilter = false;
		List<String> vecTargetId = new ArrayList<String>();
		if ( StringUtils.isBlank(param.getInstType()) || !param.getInstType().endsWith("ALL") ) {
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
				
//			 	for ( long l = 0; l < lNumRows; l++ ) {
//					MYSQL_ROW fields = mysql_fetch_row( resMySql );
//					vecTargetId.add(fields[0]);
//				}
//				// 結果セット解放
//				mysql_free_result( resMySql );
			}
		//● 検索対象ALL以外の場合
		} else {
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
			sql = "select INSTRUMENT_ID from INSTRUMENT where INSTRUMENT_TYPE in(" + instTypeQueryParam + ")";
			
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
			
//		 	for ( long l = 0; l < lNumRows; l++ ) {
//				MYSQL_ROW fields = mysql_fetch_row( resMySql );
//				instNo += fields[0];
//				if ( l < lNumRows - 1 ) {
//					instNo += ",";
//				}
//			}
//			// 結果セット解放
//			mysql_free_result( resMySql );

			if ( param.getIon() == 0 ) {
				sql = "select R.RECORD_ID from RECORD R, SPECTRUM S where R.RECORD_ID = S.RECORD_ID and R.INSTRUMENT_ID in(" + instNo + ")";
			} else {
				sql = "select R.RECORD_ID from RECORD R, SPECTRUM S where R.RECORD_ID = S.RECORD_ID and S.ION_MODE = ";
				sql += param.getIon();
				sql += " and R.INSTRUMENT_ID in(";
				sql += instNo;
				sql += ")";
			}
			// precursor m/z絞り込み条件セット
			if ( isPre ) {
				sql += sqlw1;
			}
			if ( isMsType ) {
				sql += sqlw2;
			}

			sql += " order by RECORD_ID";
			
			List<Map<Integer, Object>> result2 = dbExecuteSql( sql );
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
			
//		 	for ( long l = 0; l < lNumRows; l++ ) {
//				MYSQL_ROW fields = mysql_fetch_row( resMySql );
//				vecTargetId.push_back(fields[0]);
//			}
//			// 結果セット解放
//			mysql_free_result( resMySql );
		}
		
		//---------------------------------------------------
		// ピーク値取得
		//---------------------------------------------------
		double fMin;
		double fMax;
		String sqlw;
		for ( int i = 0; i < queryMz.size(); i++ ) {
			String strMz = queryMz.get(i);
			double fMz = Float.parseFloat( strMz );
			double fVal = queryVal.get(i);

			float fTolerance = param.getTolerance();
			if ( param.getTolUnit().equals("unit") ) {
				fMin = fMz - fTolerance;
				fMax = fMz + fTolerance;
			} else {
				fMin = fMz * (1 - fTolerance / 1000000);
				fMax = fMz * (1 + fTolerance / 1000000);
			}
			fMin -= 0.00001;
			fMax += 0.00001;

			if ( isInteg ) {
				sql = "select SPECTRUM_NO, max(RELATIVE), MZ from PARENT_PEAK where ";
				sqlw = String.format("RELATIVE >= %d and (MZ between %.6f and %.6f) group by SPECTRUM_NO",
						param.getCutoff(), fMin, fMax);
			} else {
				sql = "select max(concat(lpad(castinteger(RELATIVE_INTENSITY), 3, ' ') || ' ' || RECORD_ID || ' ' || castdouble(MZ))) from PEAK where ";
				sqlw = String.format("RELATIVE_INTENSITY >= %d and (MZ between %.6f and %.6f) group by RECORD_ID",
						param.getCutoff(), fMin, fMax);
			}
			sql += sqlw;
			
			List<Map<Integer, Object>> result = dbExecuteSql( sql );
			result.size();

			int prevAryNum = 0;
			for (Map<Integer, Object> rowResult : result) {
//		 	for ( long l = 0; l < lNumRows; l++ ) {
//				MYSQL_ROW fields = mysql_fetch_row( resMySql );

				String[] vacVal = String.valueOf(rowResult.get(1)).trim().split(" ");
//				String[] vacVal = fields[0].split(" ");
				String strId = vacVal[1];

				if ( isFilter ) {
					boolean isFound = false;
					for ( int j = prevAryNum; j < vecTargetId.size(); j++ ) {
						if ( strId.equals(vecTargetId.get(j)) ) {
							isFound = true;
							prevAryNum = j + 1;
							break;
						}
					}
					if ( !isFound ) {
						continue;
					}
				}

				double fHitVal = Float.parseFloat( vacVal[0] );
				String strHitMz = vacVal[2];
				double fHitMz = Float.parseFloat( strHitMz );

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
					mapMzCnt.put(key, mapMzCnt.get(key)+1);
				} else { 
					mapMzCnt.put(key, 1);
				}
				
			}
//			// 結果セット解放
//			mysql_free_result( resMySql );
		}
		return true;
	}
	
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
				sql = String.format("select MZ, RELATIVE_INTENSITY from %s where RECORD_ID = '%s' and RELATIVE_INTENSITY >= %d", tblName, strId, param.getCutoff());
			}
			
			
			List<Map<Integer, Object>> result = dbExecuteSql( sql);
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
			resScore.setRecordId(strId);
			resScore.setScore(iHitNum + dblScore);
			vecScore.add(resScore);
			
		}

	}
	
	private List<String> outResult() {
		List<String> result = new ArrayList<String>();
		String sql = StringUtils.EMPTY;
		for (ResScore resScore : vecScore) {
			StringBuilder sb = new StringBuilder();

			if ( isQuick || isAPI ) {
				sql = String.format("select S.TITLE, S.ION_MODE, R.FORMULA, R.EXACT_MASS from SPECTRUM S, RECORD R where S.RECORD_ID = R.RECORD_ID and S.RECORD_ID='%s'", resScore.getRecordId());
			} else if ( isInteg ) {
				sql = String.format("select TITLE, ION_MODE, RECORD_ID from SPECTRUM where SPECTRUM_ID=%s", resScore.getRecordId());
			} else {
				sql = String.format("select TITLE, ION_MODE from SPECTRUM where RECORD_ID='%s'", resScore.getRecordId());
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
					strId = resScore.getRecordId();
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
			}
			result.add(sb.toString());
		}
		return result;
	}
	
	private String removeLastChar(String str) {
		if (StringUtils.isNotBlank(str)) {
			return str.substring(0, str.length() - 1);
		}
		return StringUtils.EMPTY;
	}

	private List<Map<Integer,Object>> dbExecuteSql(String sql) {
		return DbAccessor.execQuery(sql);
	}

}