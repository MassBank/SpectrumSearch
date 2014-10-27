/*******************************************************************************
 *
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
 *******************************************************************************
 *
 * INSTRUMENT����MS�����擾����N���X
 *
 * ver 1.0.10 2011.07.22
 *
 ******************************************************************************/
package massbank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GetInstInfo {
	ArrayList<String>[] instNo   = null;
	ArrayList<String>[] instType = null;
	ArrayList<String>[] instName = null;
	ArrayList<String>[] msType = null;
	private int index = 0;

	/**
	 * �R���X�g���N�^
	 * ���R�[�h�t�H�[�}�b�g�o�[�W����2��
	 * INSTRUMENT����MS�����擾����R���X�g���N�^
	 * @param baseUrl �x�[�XURL
	 */
	public GetInstInfo( String baseUrl ) {
		String urlParam = "ver=2";
		getInformation(baseUrl, urlParam);
	}

	/**
	 * �R���X�g���N�^
	 * ���R�[�h�t�H�[�}�b�g�o�[�W������PeakSearchAdvanced�t���O���w�肵��
	 * INSTRUMENT����MS�����擾����R���X�g���N�^
	 * @param baseUrl �x�[�XURL
	 * @param formatVer MassBank���R�[�h�t�H�[�}�b�g�o�[�W����
	 * @param isPeakAdv PeakSearchAdvanced�t���O
	 */
	public GetInstInfo( String baseUrl, int formatVer, boolean isPeakAdv ) {
		String urlParam = "ver=" + formatVer;
		if ( isPeakAdv ) {
			urlParam += "&padv=1";
		}
		getInformation(baseUrl, urlParam);
	}
	
	/**
	 * ���u��ʁAMS��ʏ��擾
	 * @param baseUrl �x�[�XURL
	 * @param urlParam CGI���s���̃p�����[�^
	 */
	private void getInformation( String baseUrl, String urlParam ) {
		GetConfig conf = new GetConfig(baseUrl);
		String[] urlList = conf.getSiteUrl();

		String serverUrl = conf.getServerUrl();
		MassBankCommon mbcommon = new MassBankCommon();
		String typeName = MassBankCommon.CGI_TBL[MassBankCommon.CGI_TBL_NUM_TYPE][MassBankCommon.CGI_TBL_TYPE_INST];
		ArrayList<String> resultAll = mbcommon.execDispatcher( serverUrl, typeName, urlParam, true, null );
		
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
	
	/**
	 * �T�C�g�C���f�b�N�X���Z�b�g
	 */ 
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * INSTRUMENT_TYPE_NO���擾
	 */ 
	public String[] getNo() {
		return (String[])this.instNo[this.index].toArray( new String[0] );
	}

	/**
	 * INSTRUMENT_NAME���擾
	 */
	public String[] getName() {
		return (String[])this.instName[this.index].toArray( new String[0] );
	}

	/**
	 * INSTRUMENT_TYPE���擾
	 */
	public String[] getType() {
		return (String[])this.instType[this.index].toArray( new String[0] );
	}

	/**
	 * INSTRUMENT_TYPE���擾�i�d���Ȃ��őS�T�C�g�����擾�j
	 */
	public String[] getTypeAll() {
		ArrayList<String> instTypeList = new ArrayList<String>();
		for ( int i = 0; i < this.instType.length; i++ ) {
			for ( int j = 0; j < instType[i].size(); j++ ) {
				String type = instType[i].get(j);
				if ( !instTypeList.contains(type) ) {
					instTypeList.add( type );
				}
			}
		}
		// ���O���Ń\�[�g
		Collections.sort( instTypeList );
		return (String[])instTypeList.toArray( new String[0] );
	}

	/**
	 * INSTRUMENT_TYPE�̃O���[�v�����擾
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
	 * MS_TYPE���擾
	 */
	public String[] getMsType() {
		return (String[])this.msType[this.index].toArray( new String[0] );
	}

	/**
	 * MS_TYPE���擾�i�d���Ȃ��őS�T�C�g�����擾�j
	 */
	public String[] getMsAll() {
		ArrayList<String> msTypeList = new ArrayList<String>();
		for ( int i = 0; i < this.msType.length; i++ ) {
			for ( int j = 0; j < msType[i].size(); j++ ) {
				String type = msType[i].get(j);
				if ( !msTypeList.contains(type) ) {
					msTypeList.add( type );
				}
			}
		}
		// ���O���Ń\�[�g
		Collections.sort( msTypeList );
		return (String[])msTypeList.toArray( new String[0] );
	}
}
