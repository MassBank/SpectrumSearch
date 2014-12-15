/*
 * Copyright (C) 2008 JST-BIRD MassBank
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package massbank;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.massbank.spectrumsearch.accessor.DbAccessor;
import jp.massbank.spectrumsearch.accessor.InstrumentAccessor;
import jp.massbank.spectrumsearch.accessor.MsTypeAccessor;
import jp.massbank.spectrumsearch.entity.db.Instrument;
import jp.massbank.spectrumsearch.entity.db.MsType;

import org.apache.log4j.Logger;

/**
 * INSTRUMENT情報とMS情報を取得するクラス
 * */
public class GetInstInfo {
  static final Logger LOGGER = Logger.getLogger(GetInstInfo.class);

  @Deprecated
	ArrayList<String>[] instNo   = null;
  @Deprecated
	ArrayList<String>[] instType = null;
  @Deprecated
	ArrayList<String>[] instName = null;
  @Deprecated
    ArrayList<String>[] msType = null;

  List<MsType> msList = null;
  List<Instrument> instList = null;
  
	//	private int index = 0;

	/**
	 * レコードフォーマットバージョン2の
	 * INSTRUMENT情報とMS情報を取得するコンストラクタ
	 * @param baseUrl ベースURL
	 * @throws SQLException 
	 */
	public GetInstInfo() throws SQLException {
		DbAccessor.createConnection();
		
		InstrumentAccessor instrumentAccessor = new InstrumentAccessor();
		instList = instrumentAccessor.getAllInstruments();
		
		MsTypeAccessor msTypeAccessor = new MsTypeAccessor();
		msList = msTypeAccessor.getAllMsTypes();
//		msList = massSpectrometryAccessor.getDistinctValuesBySubTag("MS_TYPE");
		
		DbAccessor.closeConnection();
	}
	
//	public GetInstInfo( String baseUrl ) throws SQLException {
//		String urlParam = "ver=2";
////		getInformation(baseUrl, urlParam);
//		getInformationNew();
//	}

	/**
	 * コンストラクタ
	 * レコードフォーマットバージョンとPeakSearchAdvancedフラグを指定して
	 * INSTRUMENT情報とMS情報を取得するコンストラクタ
	 * @param baseUrl ベースURL
	 * @param formatVer MassBankレコードフォーマットバージョン
	 * @param isPeakAdv PeakSearchAdvancedフラグ
	 */
	// TODO remove this. this seems not used.
//	public GetInstInfo( String baseUrl, int formatVer, boolean isPeakAdv ) {
//		String urlParam = "ver=" + formatVer;
//		if ( isPeakAdv ) {
//			urlParam += "&padv=1";
//		}
//		getInformation(baseUrl, urlParam);
//	}
	
	/**
	 * 装置種別、MS種別情報取得
	 * @param baseUrl ベースURL
	 * @param urlParam CGI実行時のパラメータ
	 */
	private void getInformation( String baseUrl, String urlParam ) {
//		GetConfig conf = new GetConfig(baseUrl);
      String[] urlList = {"http://massbank.jp/"};
//      String[] urlList = conf.getSiteUrl();

      String serverUrl = "http://massbank.jp/";
//      String serverUrl = conf.getServerUrl();
      
//		MassBankCommon mbcommon = new MassBankCommon();
		String typeName = MassBankCommon.CGI_TBL[MassBankCommon.CGI_TBL_NUM_TYPE][MassBankCommon.CGI_TBL_TYPE_INST];
//	    LOGGER.info(serverUrl);
        ArrayList<String> resultAll = new ArrayList<String>();
//        ArrayList<String> resultAll = mbcommon.execDispatcher( serverUrl, typeName, urlParam, true, null );
		
		instNo = new ArrayList[urlList.length];
		instType = new ArrayList[urlList.length];
		instName = new ArrayList[urlList.length];
		msType = new ArrayList[urlList.length];
		for ( int i = 0; i < urlList.length; i++ ) {
			instNo[i]   = new ArrayList<String>();
			instType[i] = new ArrayList<String>();
			instName[i] = new ArrayList<String>();
			msType[i] = new ArrayList<String>();
		}

		boolean isInst = true;
		boolean isMs = false;
		int prevSiteNo = 0;
		for ( int i = 0; i < resultAll.size(); i++ ) {
			String line = resultAll.get(i).trim();
			if ( line.equals("") ) { continue; }
			String[] item = line.split("\t");
			int siteNo = Integer.parseInt( item[item.length-1] );
			if ( prevSiteNo != siteNo) {
				prevSiteNo = siteNo;
				isInst = true; isMs = false;
			}
			if ( line.startsWith("INSTRUMENT_INFORMATION") ) { isInst = true; isMs = false; continue; }
			if ( line.startsWith("MS_INFORMATION") ) { isInst = false; isMs = true; continue; }
			if ( isInst ) {
				instNo[siteNo].add( item[0] );
				instType[siteNo].add( item[1] );
				instName[siteNo].add( item[2] );
			}
			else if ( isMs ) {
				msType[siteNo].add( item[0] );
			}
		}
	}
	private void getInformation() throws SQLException {
//	     instNo = new ArrayList[1];
//	        instType = new ArrayList[urlList.length];
//	        instName = new ArrayList[urlList.length];
//	        msType = new ArrayList[urlList.length];
//	        for ( int i = 0; i < urlList.length; i++ ) {
//	            instNo[i]   = new ArrayList<String>();
//	            instType[i] = new ArrayList<String>();
//	            instName[i] = new ArrayList<String>();
//	            msType[i] = new ArrayList<String>();
//	        }
	  
	}
	/**
	 * サイトインデックスをセット
	 */ 
//	public void setIndex(int index) {
//		this.index = index;
//	}

	/**
	 * INSTRUMENT_TYPE_NOを取得
	 */ 
//	public String[] getNo() {
//		return (String[])this.instNo[this.index].toArray( new String[0] );
//	}

	/**
	 * INSTRUMENT_NAMEを取得
	 */
//	public String[] getName() {
//		return (String[])this.instName[this.index].toArray( new String[0] );
//	}

	/**
	 * INSTRUMENT_TYPEを取得
	 */
//	public String[] getType() {
//		return (String[])this.instType[this.index].toArray( new String[0] );
//	}

	/**
	 * INSTRUMENT_TYPEを取得（重複なしで全サイト分を取得）
	 */
	private String[] getTypeAll() {
		ArrayList<String> instTypeList = new ArrayList<String>();
		for (Instrument oneInst : instList ) {
				String type = oneInst.getType();
				if ( !instTypeList.contains(type) ) {
					instTypeList.add( type );
				}
		}
//        for ( int i = 0; i < this.instType.length; i++ ) {
//          for ( int j = 0; j < instType[i].size(); j++ ) {
//              String type = instType[i].get(j);
//              if ( !instTypeList.contains(type) ) {
//                  instTypeList.add( type );
//              }
//          }
//      }
		// 名前順でソート
		Collections.sort( instTypeList );
		return instTypeList.toArray( new String[0] );
	}

	/**
	 * INSTRUMENT_TYPEのグループ情報を取得
	 */
	public Map<String, List<String>> getTypeGroup() {
		final String[] baseGroup = { "ESI", "EI", "Others" };

		String[] instTypes = getTypeAll();
		int num = baseGroup.length;
		List<String>[] listInstType = new ArrayList[num];
		for ( int i = 0; i < num; i++ ) {
			listInstType[i] = new ArrayList<String>();
		}
		for ( int j = 0; j < instTypes.length; j++ ) {
			String val = instTypes[j];
			boolean isFound = false;
			for ( int i = 0; i < num; i++ ) {
				if ( val.indexOf(baseGroup[i]) >= 0 ) {
					listInstType[i].add(val);
					isFound = true;
					break;
				}
			}
			if ( !isFound ) {
				listInstType[num - 1].add(val);
			}
		}

		Map<String, List<String>> group = new TreeMap<String, List<String>>();
		for ( int i = 0; i < num; i++ ) {
			if ( listInstType[i].size() > 0 ) {
				group.put( baseGroup[i], listInstType[i] );
			}
		}
		return group;
	}
	
	/**
	 * MS_TYPEを取得
	 */
//	public String[] getMsType() {
//		return (String[])this.msType[this.index].toArray( new String[0] );
//	}

	/**
	 * MS_TYPEを取得（重複なしで全サイト分を取得）
	 */
	public String[] getMsAll() {
		List<String> msTypeList = new ArrayList<String>();
		for ( MsType msType : msList ) {
//				String type = msType[i].get(j);
				if ( !msTypeList.contains(msType) ) {
					msTypeList.add( msType.getName() );
				}
		}
//        for ( int i = 0; i < this.msType.length; i++ ) {
//          for ( int j = 0; j < msType[i].size(); j++ ) {
//              String type = msType[i].get(j);
//              if ( !msTypeList.contains(type) ) {
//                  msTypeList.add( type );
//              }
//          }
//      }
		
		// 名前順でソート
		Collections.sort( msTypeList );
		return msTypeList.toArray( new String[0] );
	}
}
