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
 * �X�y�N�g�����i�[ �N���X
 *
 * ver 1.0.4 2010.02.01
 *
 ******************************************************************************/

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

import javax.swing.JTable;

/**
 * �X�y�N�g�����i�[ �N���X
 * 
 * �X�y�N�g���ꊇ�\���p�f�[�^�N���X
 * ���R�[�h�����ꊇ�ŕێ�����f�[�^�N���X
 */
public class PackageSpecData {
	
	/** ���R�[�h�\�[�g�L�[(�\�[�g����) */
	public static final int SORT_KEY_NONE = -1;
	
	/** ���R�[�h�\�[�g�L�[(�X�R�A) */
	public static final int SORT_KEY_SCORE = 0;
	
	/** ���R�[�h�\�[�g�L�[(��������) */
	public static final int SORT_KEY_NAME = 1;
	
	/** ���R�[�h��� */
	private ArrayList<PackageRecData> recInfo = new ArrayList<PackageRecData>();
	
	/** ���R�[�h�� */
	private int recNum = 0;
	
	/** �I���ς݃s�[�Nm/z���X�g */
	private TreeSet<Double> selectedPeakList = new TreeSet<Double>();
	
	/**
	 * �f�t�H���g�R���X�g���N�^
	 */
	public PackageSpecData() {
	}
	
	/**
	 * ���R�[�h��񏉊���(�S���R�[�h)
	 */
	public void initAllData() {
		recInfo = new ArrayList<PackageRecData>();
		recNum = 0;
		selectedPeakList = new TreeSet<Double>();
	}
	
	/**
	 * ���R�[�h��񏉊���(�������ʃ��R�[�h�̂�)
	 */
	public void initResultData() {
		PackageRecData queryRecData = null;
		for (int i=0; i<recInfo.size(); i++) {
			// �N�G���[���R�[�h�ޔ�
			if(recInfo.get(i).isQueryRecord()) {
				queryRecData = recInfo.get(i);
				break;
			}
		}
		recInfo = new ArrayList<PackageRecData>();
		if (queryRecData != null) {
			queryRecData.setPeakColorType(PackageRecData.COLOR_TYPE_BLACK);
			recInfo.add(queryRecData);
		}
		recNum = recInfo.size();
		selectedPeakList = new TreeSet<Double>();
	}
	
	/**
	 * ���R�[�h���擾
	 * @return ���R�[�h��
	 */
	public int getRecNum() {
		return recNum;
	}

	/**
	 * ���R�[�h���擾
	 * @return ���R�[�h���
	 */
	public ArrayList<PackageRecData> getRecInfo() {
		return recInfo;
	}
	
	/**
	 * ���R�[�h���擾(�C���f�b�N�X�w��)
	 * @param index �C���f�b�N�X
	 * @return ���R�[�h�f�[�^
	 */
	public PackageRecData getRecInfo(int index) {
		return recInfo.get(index);
	}
	
	/**
	 * ���R�[�h���ǉ�
	 * @param recData ���R�[�h�f�[�^
	 */
	public void addRecInfo(PackageRecData recData) {
		this.recInfo.add(recData);
		this.recNum = recInfo.size();			// ���R�[�h���̐ݒ�������ɍs��
	}
	
	/**
	 * ���R�[�h���\�[�g(�\�[�g�L�[�w��)
	 * @param sortKey �\�[�g�L�[
	 */
	public void sortRecInfo(int sortKey) {
		Collections.sort(recInfo, new RecInfoComparator(sortKey));
	}
	
	/**
	 * ���R�[�h���\�[�g(�e�[�u����)
	 * @param t ���R�[�h���\�[�g�̌��ƂȂ�e�[�u��
	 */
	public void sortRecInfo(JTable t) {
		if(recNum == 0) {
			return;
		}
		
		int idColumn = t.getColumnModel().getColumnIndex(SearchPage.COL_LABEL_ID);
		int typeColumn = t.getColumnModel().getColumnIndex(SearchPage.COL_LABEL_TYPE);
		String idVal = "";
		String typeVal = "";
		
		// ���݂̃��R�[�h����ޔ�
		ArrayList tmpRecInfo = (ArrayList)recInfo.clone();
		recInfo.clear();
		
		// �w�肳�ꂽ�C���f�b�N�X���X�g���ŕ��ёւ�
		PackageRecData recData;
		for (int i=0; i<t.getRowCount(); i++) {
			idVal = String.valueOf(t.getValueAt(i, idColumn));
			typeVal = String.valueOf(t.getValueAt(i, typeColumn));
			for (int j=0; j<tmpRecInfo.size(); j++) {
				recData = (PackageRecData)tmpRecInfo.get(j);
				if ( idVal.equals(recData.getId()) ) {
					if ( (typeVal.indexOf(PackageViewPanel.INTEGRATE_RECORD) == -1 
							&& typeVal.equals(PackageViewPanel.QUERY_RECORD) == recData.isQueryRecord()) 
							|| (typeVal.indexOf(PackageViewPanel.INTEGRATE_RECORD) != -1
								&& typeVal.equals(PackageViewPanel.QUERY_RECORD + PackageViewPanel.INTEGRATE_RECORD) == recData.isQueryRecord() ))
					{
						recInfo.add(recData);
						tmpRecInfo.remove(j);
						break;
					}
				}
			}
		}
	}
	
	/**
	 * �S���R�[�h�����̃s�[�N�I���t���O������
	 */
	public void initAllSelectedPeak() {
		// �e���R�[�h�����̃s�[�N�I���t���O���������\�b�h�Ăяo��
		for (int i=0; i<recNum; i++) {
			recInfo.get(i).initSelectPeak();
		}
	}
	
	/**
	 * �I���ς݃s�[�N�̊m�F
	 * @param mz �m�F������m/z
	 * @return ����(�I���ρFtrue�A���I���Ffalse)
	 */
	public boolean containsSelectedPeak(String mz) {
		return selectedPeakList.contains(Double.valueOf(mz));
	}
	
	/**
	 * �I���ς݃s�[�N���X�g�̏�����
	 */
	public void clearSelectedPeakList() {
		selectedPeakList.clear();
	}
	
	/**
	 * �I���ς݃s�[�N���X�g�ւ̓o�^
	 * @param mz �I���ς݂Ƃ���m/z
	 */
	public void addSelectedPeakList(String mz) {
		selectedPeakList.add(Double.parseDouble(mz));
	}
	
	/**
	 * �I���ς݃s�[�N���X�g����̍폜
	 * @param mz �I����������m/z
	 */
	public void removeSelectedPeakList(String mz) {
		selectedPeakList.remove(Double.valueOf(mz));
	}
	
	/**
	 * �I���ς݃s�[�N���X�g�̎擾
	 * @return �I���ς݃s�[�N���X�g
	 */
	public TreeSet<Double> getSelectedPeakList() {
		return selectedPeakList;
	}
	
	/**
	 * �I���ς݃s�[�N���擾
	 * @retrun �I���σs�[�N��
	 */
	public int getSelectedPeakNum() {
		return selectedPeakList.size();
	}
	
	/**
	 * �}�b�`�s�[�N���ݒ�
	 * �N�G���[���R�[�h�̃s�[�N�ɑ΂��č��v�����s�[�N��F�Â����邽�߁A
	 * PackeageRecData�N���X�Ƀs�[�N�F����ݒ肷��B
	 *  �N�G���[���R�[�h�̃s�[�N�Ɋ��S��v�̏ꍇ
	 *   �E�N�G���[���R�[�h�̃s�[�N�F��
	 *   �E��r���R�[�h�̃s�[�N�F��
	 *  �N�G���[���R�[�h�̃s�[�N�ɔ͈͓���v�̏ꍇ
	 *   �E�N�G���[���R�[�h�̃s�[�N�F��
	 *   �E��r���R�[�h�̃s�[�N�F�}�[���^
	 * @param tolVal Tolerance���͒l
	 * @param tolUnit Tolerance�P��(true�Funit�Afalse�Fppm)
	 */
	public void setMatchPeakInfo(float tolVal, boolean tolUnit) {
		
		// �N�G���[���R�[�h�擾�ƃs�[�N�F������
		PackageRecData queryRecData = null;
		for (int i=0; i<recNum; i++) {
			if ( recInfo.get(i).isQueryRecord() ) {
				queryRecData = recInfo.get(i);
				queryRecData.setPeakColorType(PackageRecData.COLOR_TYPE_BLACK);
				break;
			}
		}
		
		// �N�G���[���R�[�h�Ƃ̔�r
		long qMz;
		long cMz;
		int qIts = 0;
		int cIts = 0;
		long minusRange;
		long plusRange;
		final int TO_INTEGER_VAL = 100000;	// �ۂߌ덷�������邽�ߐ���������̂Ɏg�p
		PackageRecData compRecData = null;
		for (int i=0; i<recNum; i++) {
			if ( !recInfo.get(i).isQueryRecord() ) {
				// ��r�p���R�[�h�擾�ƃs�[�N�F������
				compRecData = recInfo.get(i);
				compRecData.setPeakColorType(PackageRecData.COLOR_TYPE_BLACK);
				
				if (queryRecData == null) {
					continue;
				}
				// ��\�������R�[�h�͔�r�ΏۂƂ��Ȃ�
				if (queryRecData.isDisable() || compRecData.isDisable()) {
					continue;
				}
				
				for (int queryPeakI=0; queryPeakI<queryRecData.getPeakNum(); queryPeakI++) {
					qMz = (long)(queryRecData.getMz(queryPeakI) * TO_INTEGER_VAL);
					qIts = queryRecData.getIntensity(queryPeakI);
					
					if (qIts < SearchPage.CUTOFF_THRESHOLD) {
						continue;
					}
					
					// unit�̏ꍇ
					if (tolUnit) {
						minusRange = qMz - (int)(tolVal * TO_INTEGER_VAL);
						plusRange = qMz + (int)(tolVal * TO_INTEGER_VAL);
					}
					// ppm�̏ꍇ
					else {
						minusRange = (long)(qMz * (1 - tolVal / 1000000));
						plusRange = (long)(qMz * (1 + tolVal / 1000000));
					}
					
					for (int compPeakI=0; compPeakI<compRecData.getPeakNum(); compPeakI++) {
						cMz = (long)(compRecData.getMz(compPeakI) * TO_INTEGER_VAL);
						cIts = compRecData.getIntensity(compPeakI);
						
						if (cIts < SearchPage.CUTOFF_THRESHOLD) {
							continue;
						}
						
						if (minusRange <= cMz && cMz <= plusRange) {
							if (qMz == cMz) {
								compRecData.setPeakColorType(compPeakI, PackageRecData.COLOR_TYPE_RED);
							}
							else if(compRecData.getPeakColorType(compPeakI) != PackageRecData.COLOR_TYPE_RED) {
								compRecData.setPeakColorType(compPeakI, PackageRecData.COLOR_TYPE_MAGENTA);
							}
							queryRecData.setPeakColorType(queryPeakI, PackageRecData.COLOR_TYPE_RED);
						}
					}
				}
			}
		}
	}
	
	/**
	 * ���R�[�h���\�[�g�p�R���p���[�^
	 * PeckageSpecData�̃C���i�[�N���X�B
	 * PackageRecData���i�[�������X�g�̃\�[�g���s���B
	 * �\�[�g�������s�����A�ǂ̂悤�ȃ\�[�g���s�����ꍇ�ł�
	 * �K���Ō������N�G���[���R�[�h�A�������R�[�h�A�������R�[�h�̏��ɕ��ԁB
	 * �\�[�g�L�[���w�肳�ꂽ�ꍇ�͎������R�[�h�̏������ѕς��B
	 */
	class RecInfoComparator implements Comparator<Object> {
		
		/** �\�[�g�L�[ */
		private int sortKey = PackageSpecData.SORT_KEY_NONE;
		
		/**
		 * �R���X�g���N�^
		 * @param sortKey �\�[�g�L�[
		 */
		public RecInfoComparator(int sortKey) {
			this.sortKey = sortKey;
		}
		
		public int compare(Object o1, Object o2){
			PackageRecData e1 =(PackageRecData)o1;
			PackageRecData e2 =(PackageRecData)o2;
			
			int ret = 0;
			
			// �\�[�g�L�[�\�[�g����
			if (sortKey == PackageSpecData.SORT_KEY_NAME) {
				ret = (e2.getName()).compareTo(e1.getName());
			}
			else if (sortKey == PackageSpecData.SORT_KEY_SCORE) {
				// �X�R�A�ɂ��\�[�g�͕�������܂܂��ꍇ������̂ōl������
	    		boolean e1String = false;
	    		boolean e2String = false;
	    		try {
	    			Double.parseDouble(e1.getScore());
	    		} catch (NumberFormatException e) {
	    			e1String = true;
	    		}
	    		try {
	    			Double.parseDouble(e2.getScore());
	    		} catch (NumberFormatException e) {
	    			e2String = true;
	    		}
	    		if (e1String && e2String) {
	    			ret = o1.toString().compareTo(o2.toString());
	    		}
	    		else if (e1String && !e2String) {
	    			ret = 1;
	    		}
	    		else if (!e1String && e2String) {
	    			ret = -1;
	    		}
	    		else {
	    			ret = Double.valueOf((e1.getScore())).compareTo(Double.valueOf(e2.getScore()));
	    		}
			}
			else {
				// �\�[�g�L�[�w��Ȃ��̏ꍇ��ID�Ń\�[�g
				ret = (e1.getId()).compareTo(e2.getId());
			}
			
			// �Œ�\�[�g����
			if (e1.isQueryRecord() && !e2.isQueryRecord()) {
				ret = 1;
			}
			else if (e2.isQueryRecord() && !e1.isQueryRecord()) {
				ret = -1;
			}
			else if (e1.isIntegRecord() && !e2.isIntegRecord()) {
				ret = 1;
			}
			else if (e2.isIntegRecord() && !e1.isIntegRecord()) {
				ret = -1;
			}
			return ret;
		}
	}
}
