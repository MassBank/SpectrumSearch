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
 * ���R�[�h���i�[ �N���X
 *
 * ver 1.0.4 2010.09.16
 *
 ******************************************************************************/

import java.awt.Color;

/**
 * ���R�[�h���i�[ �N���X
 * 
 * �X�y�N�g���ꊇ�\���p���R�[�h���f�[�^�N���X
 * ���R�[�h�P�ʂ�Peak����ێ�����f�[�^�N���X
 */
public class PackageRecData {
	
	/** �s�[�N�`��F���(��) */
	public static final int COLOR_TYPE_BLACK = 0;
	
	/** �s�[�N�`��F���(��) */
	public static final int COLOR_TYPE_RED = 1;
	
	/** �s�[�N�`��F���(�}�[���^) */
	public static final int COLOR_TYPE_MAGENTA = 2;
	
	/** �N�G���[���R�[�h�t���O */
	private boolean queryRecord = false;
	
	/** ���U���g���R�[�h�t���O */
	private boolean resultRecord = false;
	
	/** �������R�[�h�t���O */
	private boolean integRecord = false;
	
	/** ID */
	private String id;
	
	/** �������� */
	private String name;
	
	/** �T�C�g */
	private String site;
	
	/** �s�[�N��(m/z�A���x�̐�) */
	private int peakNum;
	
	/** m/z */
	private double[] mz;
	
	/** ���x */
	private int[] intensity;
	
	/** �s�[�N�I���t���O */
	private boolean[] selectPeak;
	
	/** �v���J�[�T�[ */
	private String precursor;
	
	/** �s�[�N�`��F��� */
	private int[] peakColorType;
	
	/** ��\���t���O */
	private boolean disable = false;
	
	/** �X�R�A */
	private String score = " -";
	
	/**
	 * �f�t�H���g�R���X�g���N�^
	 */
	public PackageRecData() {
	}
	
	/**
	 * ID�擾
	 * @return ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * ID�ݒ�
	 * @param id ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * ID�ݒ�
	 * @param s ID���܂܂�Ă��镶����
	 * @param findStr ID��T�����߂̕�����
	 */
	public void setId(String s, String findStr) {
		int pos = s.indexOf(findStr);
		int posNext = 0;
		if ( pos >= 0 ) { 
			posNext = s.indexOf( "\t", pos );
			this.id = s.substring( pos + findStr.length(), posNext );
		}
		else {
			this.id = "";
		}
	}

	/**
	 * �s�[�N�`��F��ʎ擾
	 * @param index �C���f�b�N�X
	 * @return �s�[�N�`��F
	 */
	public Color getPeakColor(int index) {
		Color peakColor = Color.BLACK;
		if (peakColorType[index] == COLOR_TYPE_RED) {
			peakColor = Color.RED;
		}
		else if (peakColorType[index] == COLOR_TYPE_MAGENTA) {
			peakColor = Color.MAGENTA;
		}
		return peakColor;
	}
	
	/**
	 * �s�[�N�`��F��ʎ擾
	 * @param index �C���f�b�N�X
	 * @return �s�[�N�`��F���
	 */
	public int getPeakColorType(int index) {
		return peakColorType[index];
	}

	/**
	 * �s�[�N�`��F��ʐݒ�
	 * @param index �C���f�b�N�X
	 * @param peakColorType �s�[�N�`��F���
	 */
	public void setPeakColorType(int index, int peakColorType) {
		this.peakColorType[index] = peakColorType;
	}
	
	/**
	 * �s�[�N�`��F��ʐݒ�(�ꊇ)
	 * @param index �C���f�b�N�X
	 * @param peakColorType �s�[�N�`��F���
	 */
	public void setPeakColorType(int peakColorType) {
		for (int i=0; i<peakNum; i++) { 
			this.peakColorType[i] = peakColorType;
		}
	}
	
	/**
	 * �q�b�g�s�[�N���擾
	 * @return �q�b�g�s�[�N��
	 */
	public String getHitPeakNum() {
		if (!queryRecord) {
			return " -";
		}
		int hitPeakNum = 0;
		for (int i=0; i<peakNum; i++) {
			if (peakColorType[i] == COLOR_TYPE_RED) {
				hitPeakNum++;
			}
		}
		return String.valueOf(hitPeakNum);
	}
	
	/**
	 * �}�b�`�s�[�N���擾
	 * @return �}�b�`�s�[�N��
	 */
	public String getMatchPeakNum() {
		if (queryRecord) {
			return " -";
		}
		int matchPeakNum = 0;
		for (int i=0; i<peakNum; i++) {
			if (peakColorType[i] != COLOR_TYPE_BLACK) {
				matchPeakNum++;
			}
		}
		return String.valueOf(matchPeakNum);
	}
	
	/**
	 * ���x�擾
	 * @param mz m/z
	 * @return ���x
	 */
	public int getIntensity(String mz) {
		// �󂯎����m/z�̋��x��ԋp����
		// ���R�[�h����m/z�������ŋ��x���قȂ�s�[�N���������݂���ꍇ�́A
		// ��ԍŏ��Ɍ�������m/z�ɑΉ����鋭�x��ԋp
		int index = -1;
		for (int i=0; i<this.mz.length; i++) {
			if (String.valueOf(this.mz[i]).equals(mz)) {
				index = i;
				break;
			}
		}
		return intensity[index];
	}
	
	/**
	 * ���x�擾
	 * @param index �C���f�b�N�X
	 * @return ���x
	 */
	public int getIntensity(int index) {
		return intensity[index];
	}

	/**
	 * ���x�ݒ�
	 * @param index �C���f�b�N�X
	 * @param intensity ���x
	 */
	public void setIntensity(int index, String intensity) {
		this.intensity[index] = Integer.parseInt(intensity);
	}

	/**
	 * �ő勭�x�擾
	 * @param start �}�X�����W(m/z)�J�n�l
	 * @param end �}�X�����W(m/z)�I���l
	 * @return ���R�[�h���̎w�肳�ꂽ�}�X�����W(m/z)�̊Ԃōő�̋��x
	 */
	public int getMaxIntensity(double start, double end) {
		int max = 0;
		for (int i=0; i<this.peakNum; i++) {
			if (this.mz[i] > end) {
				break;
			}
			if (start <= this.mz[i]) {
				max = Math.max(max, this.intensity[i]);
			}
		}
		return max;
	}
	
	/**
	 * m/z���݊m�F
	 * @param mz m/z
	 * @return ����(m/z�����݁Ftrue�Am/z�����݂��Ȃ��Ffalse)
	 */
	public boolean checkMz(String mz) {
		// �󂯎����m/z�����R�[�h���ɑ��݂��邩���m�F
		for (int i=0; i<this.mz.length; i++) {
			if (String.valueOf(this.mz[i]).equals(mz)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * m/z�擾
	 * @param index �C���f�b�N�X
	 * @return m/z
	 */
	public double getMz(int index) {
		return mz[index];
	}

	/**
	 * m/z�ݒ�
	 * @param index �C���f�b�N�X
	 * @param mz m/z
	 */
	public void setMz(int index, String mz) {
		this.mz[index] = Double.parseDouble(mz);
	}
	
	/**
	 * �ő�m/z�ƃv���J�[�T�[�̔�r
	 * @return �ő�m/z�ƃv���J�[�T�[�̑傫����
	 */
	public double compMaxMzPrecusor() {
		double mzMax;
		if (mz == null || mz.length == 0) {
			mzMax = 0f;
		}
		else {
			mzMax = mz[mz.length-1];
		}
		try {
			Double.parseDouble(precursor);
		} catch (Exception e) {
			return mzMax;
		}
		
		return Math.max(mzMax, Double.parseDouble(precursor));
	}
	
	/**
	 * �s�[�N�I���t���O�擾
	 * @param index �C���f�b�N�X
	 * @return �s�[�N�I���t���O
	 */
	public boolean isSelectPeak(int index) {
		return selectPeak[index];
	}

	/**
	 * �s�[�N�I���t���O�擾
	 * @param mz m/z
	 * @return �s�[�N�I���t���O
	 */
	public boolean isSelectPeak(String mz) {
		
		int index = -1;
		for (int i=0; i<peakNum; i++) {
			if (this.mz[i] == Double.parseDouble(mz)) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			return this.selectPeak[index];
		}
		else {
			return false;
		}
	}	
	
	/**
	 * �s�[�N�I���t���O�ݒ�
	 * @param index �C���f�b�N�X
	 * @param selectPeak �s�[�N�I���t���O
	 */
	public void setSelectPeak(int index, boolean selectPeak) {
		this.selectPeak[index] = selectPeak;
	}
	
	/**
	 * �s�[�N�I���t���O�ύX
	 * @param mz m/z
	 * @param status �s�[�N�I���t���O
	 */
	public void setSelectPeak(String mz, boolean status) {
		int index = -1;
		for (int i=0; i<peakNum; i++) {
			if (this.mz[i] == Double.parseDouble(mz)) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			this.selectPeak[index] = status;
		}
	}
	
	/**
	 * �s�[�N�I���t���O������
	 */
	public void initSelectPeak() {
		this.selectPeak = new boolean[peakNum];
	}
	
	/**
	 * ���������̎擾
	 * @return ��������
	 */
	public String getName() {
		return name;
	}

	/**
	 * ���������̐ݒ�
	 * @param name ��������
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * ���������̐ݒ�
	 * @param s �����������܂܂�Ă��镶����
	 * @param findStr ����������T�����߂̕�����
	 */
	public void setName(String s, String findStr) {
		int pos = s.indexOf(findStr);
		int posNext = 0;
		if ( pos >= 0 ) { 
			posNext = s.indexOf( "\t", pos );
			this.name = s.substring( pos + findStr.length(), posNext );
		}
		else {
			this.name = "";
		}
	}

	/**
	 * �T�C�g�擾
	 * @return �T�C�g
	 */
	public String getSite() {
		return site;
	}

	/**
	 * �T�C�g�ݒ�
	 * @param site �T�C�g
	 */
	public void setSite(String site) {
		this.site = site;
	}
	
	/**
	 * �v���J�[�T�[�擾
	 * @return �v���J�[�T�[
	 */
	public String getPrecursor() {
		return precursor;
	}

	/**
	 * �v���J�[�T�[�ݒ�
	 * @param precursor �v���J�[�T�[
	 */
	public void setPrecursor(String precursor) {
		this.precursor = precursor;
	}
	
	/**
	 * �v���J�[�T�[�ݒ�
	 * @param s �v���J�[�T�[���܂܂�Ă��镶����
	 * @param findStr �v���J�[�T�[��T�����߂̕�����
	 */
	public void setPrecursor(String s, String findStr) {
		int pos = s.indexOf(findStr);
		int posNext = 0;
		if ( pos >= 0 ) { 
			posNext = s.indexOf( "\t", pos );
			// IT-MS�Ή�
			String[] precursors = s.substring( pos + findStr.length(), posNext ).split("/");
			this.precursor = precursors[precursors.length - 1];
		}
		else {
			this.precursor = "";
		}
	}

	/**
	 * �s�[�N���擾
	 * @return �s�[�N��
	 */
	public int getPeakNum() {
		return peakNum;
	}

	/**
	 * �s�[�N���ݒ�
	 * ������m/z�A���x�A�s�[�N�I���t���O�̔z���������
	 * @param peakNum �s�[�N��
	 */
	public void setPeakNum(int peakNum) {
		this.peakNum = peakNum;
		this.mz = new double[peakNum];
		this.intensity = new int[peakNum];
		this.selectPeak = new boolean[peakNum];
		this.peakColorType = new int[peakNum];
	}
	
	/**
	 * �w��m/z�C���f�b�N�X�擾
	 * �w�肳�ꂽm/z�ȏ��m/z���i�[����Ă���C���f�b�N�X���擾����
	 * @param target m/z�w��l
	 * @return �C���f�b�N�X
	 */
	public int getIndex(double target) {
		int index;
		for (index=0; index<this.peakNum; index++) {
			if (this.mz[index] >= target) {
				break;
			}
		}
		return index;
	}

	/**
	 * �N�G���[���R�[�h�t���O�擾
	 * @return �N�G���[���R�[�h�t���O(true�F�N�G���[�Afalse�F�N�G���[�ȊO)
	 */
	public boolean isQueryRecord() {
		return queryRecord;
	}

	/**
	 * �N�G���[���R�[�h�t���O�ݒ�
	 * @param queryRecord �N�G���[���R�[�h�t���O(true�F�N�G���[�Afalse�F�N�G���[�ȊO)
	 */
	public void setQueryRecord(boolean queryRecord) {
		this.queryRecord = queryRecord;
	}

	/**
	 * ���U���g���R�[�h�t���O�擾
	 * @return ���U���g���R�[�h�t���O(true�F���U���g�Afalse�F���U���g�ȊO)
	 */
	public boolean isResultRecord() {
		return resultRecord;
	}

	/**
	 * ���U���g���R�[�h�t���O�ݒ�
	 * @param resultRecord ���U���g���R�[�h�t���O(true�F���U���g�Afalse�F���U���g�ȊO)
	 */
	public void setResultRecord(boolean resultRecord) {
		this.resultRecord = resultRecord;
	}
	
	/**
	 * �������R�[�h�t���O�擾
	 * @return �������R�[�h�t���O(true�F�������R�[�h�Afalse�F���[���R�[�h)
	 */
	public boolean isIntegRecord() {
		return integRecord;
	}

	/**
	 * �������R�[�h�t���O�ݒ�
	 * @param integRecord �������R�[�h�t���O�itrue�F�������R�[�h�Afalse�F�������R�[�h�ł͂Ȃ��j
	 */
	public void setIntegRecord(boolean integRecord) {
		this.integRecord = integRecord;
	}
	
	/**
	 * �������R�[�h�t���O�ݒ�
	 * @param title ���R�[�h�^�C�g��
	 */
	public void setIntegRecord(String title) {
		// RECORD_TITLE��"MERGED"���܂܂�Ă����ꍇ�ɁA�����X�y�N�g���Ɣ��f����
		if ( title.indexOf("MERGED") != -1 ) {
			this.integRecord = true;
		}
	}
	
	/**
	 * ��\���t���O�擾
	 * @return ��\���t���O(true�F��\���Afalse�F�\��)
	 */
	public boolean isDisable() {
		return disable;
	}

	/**
	 * ��\���t���O�ݒ�
	 * @param disable ��\���t���O(true�F��\���Afalse�F�\��)
	 */
	public void setDisable(boolean disable) {
		this.disable = disable;
	}

	/**
	 * �X�R�A�擾
	 * @return �X�R�A
	 */
	public String getScore() {
		return score;
	}

	/**
	 * �X�R�A�ݒ�
	 * @param score �X�R�A
	 */
	public void setScore(String score) {
		if (score.length() == 0) {
			return;
		}
		this.score = score;
	}
}
