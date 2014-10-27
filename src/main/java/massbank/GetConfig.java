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
 * ���ݒ�t�@�C���̏����擾����N���X
 *
 * ver 1.0.7 2012.09.06
 *
 ******************************************************************************/
package massbank;

import javax.xml.parsers.*;
import org.w3c.dom.*;

public class GetConfig {
	public static final int MYSVR_INFO_NUM = 0;
	private Element m_root;

	/**
	 * �R���X�g���N�^
	 */ 
	public GetConfig( String baseUrl ) {
		String url =  baseUrl + "massbank.conf";
		try {
			// �h�L�������g�r���_�[�t�@�N�g���𐶐�
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();

			// �h�L�������g�r���_�[�𐶐�
			DocumentBuilder builder = dbfactory.newDocumentBuilder();

			// �p�[�X�����s����Document�I�u�W�F�N�g���擾
			Document doc = builder.parse( url );

			// ���[�g�v�f���擾
			m_root = doc.getDocumentElement();
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * �T�C�g�̃t�����̂��擾����
	 */ 
	public String[] getSiteLongName() {
		return  getSetting("LongName");
	}

	/**
	 * Browse�A�v���b�g�̓��샂�[�h���擾����
	 */ 
	public String[] getBrowseMode() {
		return getSetting("BrowseMode");
	}

	/**
	 * DB�����擾����
	 */ 
	public String[] getDbName() {
		return getSetting("DB");
	}

	/**
	 * �Z�J���_��DB�����擾����
	 */ 
	public String[] getSecondaryDBName() {
		return getSetting("SecondaryDB");
	}

	/**
	 * �T�[�o�[URL���擾����
	 */ 
	public String getServerUrl() {
		String url = this.getServerSetting("FrontServer");
		return url;
	}

	/**
	 * �T�C�gURL���擾����
	 */ 
	public String[] getSiteUrl() {
		return getSetting("URL");
	}
	
	/**
	 * �T�C�g���̂��擾����
	 */ 
	public String[] getSiteName() {
		return getSetting("Name");
	}

	/**
	 * �^�C���A�E�g�l�擾����
	 */ 
	public int getTimeout() {
		// �f�t�H���g
		int val = 120;
		String ret = getValByTagName( "Timeout" );
		if ( !ret.equals("") ) {
			val = Integer.parseInt(ret);
		}
		return val;
	}

	/**
	 * �g���[�X���O�o�͗L�����ǂ����𔻒肷��
	 */ 
	public boolean isTraceEnable() {
		// �f�t�H���g
		boolean val = false;
		String ret = getValByTagName( "TraceLog" );
		if ( ret.equals("true") ) {
			val = true;
		}
		return val;
	}

	/**
	 * �y�[�W�����N�\�����擾
	 */ 
	public String getDispLinkNum() {
		return getValByTagName( "LinkNum" );
	}
	
	/**
	 * �e�m�[�h�\�����擾����
	 */ 
	public String getDispNodeNum() {
		return getValByTagName( "NodeNum" );
	}
	
	/**
	 * Cookie�L���t���O
	 */ 
	public boolean isCookie() {
		// �f�t�H���g
		boolean val = false;
		String ret = getValByTagName( "Cookie" );
		if ( ret.equals("true") ) {
			val = true;
		}
		return val;
	}
	
	/**
	 * �T�[�o�Ď��̃|�[�����O�������擾����
	 */
	public int getPollInterval() {
		// �f�t�H���g30��
		int val = 30;
		String ret = getValByTagName( "PollingInterval" );
		if ( !ret.equals("") ) {
			val = Integer.parseInt(ret);
		}
		return val;
	}
	
	/**
	 * 
	 */
	private String[] getSetting( String tagName ) {
		String[] infoList = null;
		String tagName1;
		String tagName2;
		tagName1 = tagName2 = tagName;
		if ( tagName.equals("URL") ) {
			tagName1 = "MiddleServer";
		}
		String info1 = this.getServerSetting(tagName1);
		String[] info2 = this.getRelatedSetting(tagName2);
		if ( info2 == null ) {
			infoList = new String[1];
		}
		else {
			int len = info2.length;
			infoList = new String[len+1];
			for ( int i = 0; i < len; i++ ) {
				infoList[i+1] = info2[i];
			}
		}
		infoList[MYSVR_INFO_NUM] = info1;
		return infoList;
	}
	
	/**
	 * ���T�[�o�[�̐ݒ���擾����
	 */
	private String getServerSetting(String tagName) {
		String val = "";
		try {
			NodeList nodeList = m_root.getElementsByTagName( "MyServer" );
			if ( nodeList == null ) {
				return val;
			}
			Element child = (Element)nodeList.item(0);
			NodeList childNodeList = child.getElementsByTagName( tagName );
			Element child2 = (Element)childNodeList.item(0);
			if ( child2 != null ) {
				if ( tagName.equals("FrontServer") || tagName.equals("MiddleServer") ) {
					val = child2.getAttribute("URL");
				}
				else {
					Node node = child2.getFirstChild();
					if ( node != null ) {
						val = node.getNodeValue();
					}
				}
				if ( val == null ) {
					val = "";
				}
			}
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		return val;
	}

	/**
	 * �A�g�T�C�g�̐ݒ���擾����
	 */
	private String[] getRelatedSetting(String tagName) {
		String[] vals = null;
		try {
			NodeList nodeList = m_root.getElementsByTagName( "Related" );
			if ( nodeList == null ) {
				return null;
			}
			int len = nodeList.getLength();
			vals = new String[len];
			for ( int i = 0; i < len; i++ ) {
				Element child = (Element)nodeList.item(i);
				NodeList childNodeList = child.getElementsByTagName( tagName );
				Element child2 = (Element)childNodeList.item(0);
				vals[i] = "";
				if ( child2 == null ) {
					continue;
				}
				Node node = child2.getFirstChild();
				if ( node == null ) {
					continue;
				}
				String val = node.getNodeValue();
				if ( val != null ) {
					vals[i] = val;
				}
			}
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		return vals;
	}

	/**
	 * �w�肳�ꂽ�^�O�̒l���擾
	 */ 
	private String getValByTagName( String tagName ) {
		String val = "";
		try {
			NodeList nodeList = m_root.getElementsByTagName( tagName );
			Element child = (Element)nodeList.item(0);
			val = child.getFirstChild().getNodeValue();
		}
		catch ( Exception e ) {
			System.out.println("\"" + tagName + "\" tag doesn't exist in massbank.conf.");
		}
		return val;
	}
}
