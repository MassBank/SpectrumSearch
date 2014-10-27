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
 * Cookie���Ǘ� �N���X
 *
 * ver 1.0.5 2011.12.16
 *
 ******************************************************************************/

import java.applet.Applet;
import java.util.ArrayList;

import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import netscape.javascript.JSUtil;

/**
 * Cookie���Ǘ� �N���X
 * 
 * Cookie���͎��̂悤�Ɏ擾�ł���
 * [Cookie��]=[�l]; [���̑��L�[]=[���̑��̒l];�c
 * [Cookie��]�ɂ͓K���Ȗ��́A[�l]��[[�L�[]=[�l],[�L�[]=[�l],�c;]�̂悤�Ɋi�[
 * ����Ă��邱�Ƃ�O��Ƃ��Ă���
 * 
 * Cookie���̗�F
 *   SearchApplet=INST=CI-MS,FI-MS;ION=Positive;
 */
public class CookieManager {

	private boolean isCookie = false;		// �N�b�L�[�L���t���O
	private JSObject win;					// Javascript��window�I�u�W�F�N�g
    private JSObject doc;					// Javascript��document�I�u�W�F�N�g
	private String cookieName = "Applet";	// �Ώ�Cookie���i�f�t�H���gApplet�j
	private int expDate = 30;				// �L�����������i�f�t�H���g30���j
	
	/**
	 * �f�t�H���g�R���X�g���N�^
	 * @deprecated �g�p�s��
	 */
	private CookieManager() {
	}
	
	/**
	 * �R���X�g���N�^
	 * �Ώ�Cookie���ƗL�������������w�肷��R���X�g���N�^
	 * Javascript��Window�I�u�W�F�N�g���擾�ł��Ȃ��ꍇ��
	 * ���̃N���X���g�p�s�Ƃ���t���O��ݒ肷��
	 * �u���E�U�E�B���h�E�I�u�W�F�N�g���擾�ł��Ȃ��ꍇ�͗�O���o�͂���
	 * @param applet �A�v���b�g
	 * @param name �Ώ�Cookie��
	 * @param expDate �L����������
	 * @param isCookie Cookie�L���t���O
	 */
	public CookieManager(Applet applet, String name, int expDate, boolean isCookie) {
		try {
			this.win = JSObject.getWindow(applet);
			this.doc = (JSObject)win.getMember("document");
			
			if (!name.trim().equals("")) {
				this.cookieName = name;
			}
			this.expDate = expDate;
			this.isCookie = isCookie;
			
			// Cookie��������Cookie���S�폜
			if ( !this.isCookie ) {
				updateCookie("");
			}
		}
		catch (JSException e) {
			System.out.println("browser window object doesn't exist.");
			System.out.println(JSUtil.getStackTrace(e));
			return;
		}
	}
	
	/**
	 * Cookie���ݒ�
	 * �ΏۂƂȂ�Cookie���ɃL�[�ƒl�̃Z�b�g��ݒ肷��
	 * ���ɃL�[�ƒl�̃Z�b�g��Cookie���ɑ��݂���ꍇ�͒u������
	 * �T�C�Y0�̒l���X�g�������Ɏ󂯎�����ꍇ�̓L�[�ɑΉ�����Cookie���̂ݍ폜�����
	 * @param key �L�[
	 * @param valueList �l�̃��X�g
	 * @return ����
	 */
	public boolean setCookie(String key, ArrayList<String> valueList) {
		
		// Cookie������������Javascript�I�u�W�F�N�g���擾�ł��Ă��Ȃ��ꍇ
		if ( !isCookie || win == null || doc == null ) {
			return false;
		}
		
		String param = "";
		String values = getCookie();
		if (values.trim().length() != 0) {
			String[] data = values.split(";");
			for (int i=0; i<data.length; i++) {
				// ����Cookie������ꍇ��key�ɊY�����Ȃ����݂̂������p��
				if (!data[i].split("=")[0].trim().equals(key)) {
					param += data[i] + ";";
				}
			}
		}
		
		if (valueList.size() != 0) {
			param += key + "=";
			for (int i=0; i<valueList.size(); i++) {
				param += valueList.get(i);
				if (i+1 < valueList.size()) {
					param += ",";
				}
			}
			param += ";";
		}
		
		// Cookie���X�V
		return updateCookie(param);
	}
	
	/**
	 * Cookie���X�V
	 * Cookie�ɕۑ�����p�����[�^���Ȃ��ꍇ��Cookie��ێ����Ȃ��i���ɂ���ꍇ�͍폜�j
	 * @param param Cookie�ɕۑ�����p�����[�^
	 * @return ����
	 */
	private boolean updateCookie(String param) {
		try {
			// ���݂܂ł̌o�ߎ��Ԏ擾�i�~���b�j
			JSObject date = (JSObject)win.eval("new Date()");
			Double time = Double.parseDouble(String.valueOf(date.call("getTime", null)));
			
			// �L���������O���j�b�W�W�����ŎZ�o
			if ( !param.equals("") ) {
				time += ((double)expDate * 24d * 60d * 60d * 1000d);
			} else {
				time -= (double)expDate;
			}
			try {
				time = Double.parseDouble(String.valueOf(date.call("setTime", new Object[]{time})));
			}
			catch (Exception e) {
				time = Double.parseDouble(String.valueOf(date.eval("setTime(" + String.valueOf(time) + ")")));
			}
			String gmtTime = String.valueOf(date.call("toGMTString", null));
			
			// Cookie�ݒ���ݒ菈��
			String paramVal = cookieName + "=" + win.call("escape", new Object[]{param});
			String timeVal = "expires=" + gmtTime;
			String cookieVal = paramVal + "; " + timeVal;
			doc.setMember("cookie", cookieVal);
		}
		catch (JSException jse) {
			System.out.println("Unsupported javascript was used.");
			System.out.println(JSUtil.getStackTrace(jse));
			return false;
		}
		return true;
	}
	
	/**
	 * Cookie���擾
	 * �ΏۂƂȂ�Cookie����S�Ď擾����
	 * @return Cookie���
	 */
	private String getCookie() {
		
		String values = "";	// �Ώ�Cookie���
		try {
			String tmpAllCookie = (String)doc.getMember("cookie");
			if (tmpAllCookie == null) {
				try {
					tmpAllCookie = (String)doc.eval("cookie");
				}
				catch (JSException jse) {				
				}
				if (tmpAllCookie == null) {
					return values;
				}
			}
			String[] allCookie = tmpAllCookie.split(";");
			String[] tmp;
			for (int i=0; i<allCookie.length; i++) {
				tmp = allCookie[i].split("=");
				// �Y������Cookie���̎擾
				if (tmp[0].trim().equals(cookieName)) {
					if (tmp.length == 2) {
						try {
							values = String.valueOf(win.eval("unescape('" +  tmp[1].trim() +"')"));
						}
						catch (JSException e) {
							values = String.valueOf(win.call("unescape", new Object[]{tmp[1].trim()}));
						}
					}
					break;
				}
			}
		}
		catch (JSException e) {
			System.out.println(JSUtil.getStackTrace(e));
			values = "";
		}
		
		return values;
	}
	
	/**
	 * Cookie���擾�i�L�[�w��j
	 * �ΏۂƂȂ�Cookie��񂩂�L�[�ɊY������l�݂̂��擾����
	 * @param key �擾������Cookie���̃L�[
	 * @return Cookie���
	 */
	public ArrayList<String> getCookie(String key) {
		
		ArrayList<String> valueList = new ArrayList<String>();
		
		// Cookie������������Javascript�I�u�W�F�N�g���擾�ł��Ă��Ȃ��ꍇ
		if ( !isCookie || win == null || doc == null ) {
			return valueList;
		}
		
		// �Ώ�Cookie���擾
		String values = getCookie();
		
		
		// �L�[�ɑ΂���l�̎擾����
		String val = "";
		if (values.trim().length() != 0) {
			String[] data = values.split(";");
			String[] item;
			for (int i=0; i<data.length; i++) {
				item = data[i].split("=");
				if (item[0].trim().equals(key)) {
					if (item.length == 2) {
						val = item[1].trim();
					}
					break;
				}
			}
		}
		String[] tmp = val.split(",");
		for (int i=0; i<tmp.length; i++) {
			if (!tmp[i].equals("")) {
				valueList.add(tmp[i]);
			}
		}
		
		return valueList;
	}
}
