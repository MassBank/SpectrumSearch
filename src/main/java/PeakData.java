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
 * �s�[�N���f�[�^ �N���X
 *
 * ver 1.0.1 2009.12.15
 *
 ******************************************************************************/

/**
 * �s�[�N���f�[�^ �N���X
 * �X�y�N�g���P�ʂ�Peak����ێ�����f�[�^�N���X
 */
public class PeakData {
	
	/** �s�[�N�� */
	private int peakNum = 0;
	
	/** m/z */
	private double[] mz;

	/** ���x */
	private int[] intensity;

	/** �s�[�N�I���t���O */
	private boolean[] selectPeakFlag;

	/**
	 * �R���X�g���N�^
	 * @param data m/z�Ƌ��x�̃^�u��؂蕶������i�[�����z��
	 */
	public PeakData(String[] data) {
		clear();
		
		peakNum = data.length;
		if (data.length == 1) {
			if (data[0].split("\t")[0].equals("0") && data[0].split("\t")[1].equals("0")) {
				peakNum = 0;
			}
		}
		mz = new double[peakNum];
		intensity = new int[peakNum];
		selectPeakFlag = new boolean[peakNum];
		String[] words;
		for (int i=0; i<peakNum; i++) {
			words = data[i].split("\t");
			mz[i] = Double.parseDouble(words[0]);
			intensity[i] = Integer.parseInt(words[1]);
		}
	}

	/**
	 * ������
	 */
	public void clear() {
		mz = null;
		intensity = null;
		selectPeakFlag = null;
	}
	
	/**
	 * �ő勭�x�擾
	 * @param start �}�X�����W(m/z)�J�n�l
	 * @param end �}�X�����W(m/z)�I���l
	 * @return ���R�[�h���̎w�肳�ꂽ�}�X�����W(m/z)�̊Ԃōő�̋��x
	 */
	public int getMaxIntensity(double start, double end) {
		int max = 0;
		for (int i = 0; i < peakNum; i++) {
			if (mz[i] > end)
				break;

			if (start <= mz[i]) {
				max = Math.max(max, intensity[i]);
			}
		}

		return max;
	}

	/**
	 * m/z�擾
	 * @param index �C���f�b�N�X
	 * @return m/z
	 */
	public double getMz(int index) {
		if (index < 0 || index >= peakNum) {
			return -1.0d;
		}
		return mz[index];
	}

	/**
	 * �ő�m/z�ƃv���J�[�T�[�̔�r
	 * @param �v���J�[�T�[
	 * @return �ő�m/z�ƃv���J�[�T�[�̑傫����
	 */
	public double compMaxMzPrecusor(String precursor) {
		double mzMax;
		if (mz.length == 0) {
			mzMax = 0d;
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
	 * ���x�擾
	 * @param index �C���f�b�N�X
	 * @return ���x
	 */
	public int getIntensity(int index) {
		if (index < 0 || index >= peakNum) {
			return -1;
		}
		return intensity[index];
	}

	/**
	 * �C���f�b�N�X�擾
	 * @param mz m/z
	 * @return �C���f�b�N�X
	 */
	public int getIndex(double mz) {
		int i;
		for (i = 0; i < peakNum; i++) {
			if (this.mz[i] >= mz)
				break;
		}
		return i;
	}
	
	/**
	 * �s�[�N�I����Ԏ擾
	 * @param index �C���f�b�N�X
	 * @return �I����ԁitrue�F�I����, false�F���I���j
	 */
	public boolean isSelectPeakFlag(int index) {
		return selectPeakFlag[index];
	}

	/**
	 * �s�[�N�I����Ԑݒ�
	 * @param index �C���f�b�N�X
	 * @param flag �I����ԁitrue�F�I����, false�F���I���j
	 */
	public void setSelectPeakFlag(int index, boolean flag) {
		this.selectPeakFlag[index] = flag;
	}

	/**
	 * �s�[�N�I����ԏ�����
	 */
	public void initSelectPeakFlag() {
		this.selectPeakFlag = new boolean[peakNum];
	}
	/**
	 * �I����ԃs�[�N���擾 
	 * @return int �I����ԃs�[�N��
	 */
	public int getSelectPeakNum() {
		int num = 0;
		for (int i = 0; i < peakNum; i++) {
			if (selectPeakFlag[i]) {
				num++;
			}
		}
		return num;
	}

	/**
	 * �s�[�N���擾
	 * @return �s�[�N��
	 */
	public int getPeakNum() {
		return peakNum;
	}
}
