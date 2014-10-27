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
 * �������ʃ��R�[�h���ꊇ�Ǘ��N���X
 * �����Ƀq�b�g�����X�y�N�g���̏��iResultRecord�N���X�j���ꊇ�ŕێ�����f�[�^�N���X
 * Result�y�[�W�ɕ\������S�Ă̏���ێ�����N���X
 *
 * ver 1.0.3 2008.12.17
 *
 ******************************************************************************/
package massbank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ResultList {
	
	/** �\�[�g�A�N�V�����i�����j */
	public static final int SORT_ACTION_ASC = 1;			// �f�t�H���g
	
	/** �\�[�g�A�N�V�����i�~���j */
	public static final int SORT_ACTION_DESC = -1;
	
	/** �\�[�g�L�[�i���������j */
	public static final String SORT_KEY_NAME = "name";	// �f�t�H���g
	
	/** �\�[�g�L�[�i�g�����j */
	public static final String SORT_KEY_FORMULA = "formula";
	
	/** �\�[�g�L�[�i�������ʁj */
	public static final String SORT_KEY_EMASS = "emass";
	
	/** �\�[�g�L�[�iID�j */
	public static final String SORT_KEY_ID = "id";
	
	/** �\������ő�y�[�W�����N�� */
	private final int DISP_LINK_NUM;
	
	/** 1�y�[�W�ӂ�̕\���e�m�[�h�� */
	private final int DISP_NODE_NUM;
	
	/** ���R�[�h���X�g */
	private ArrayList<ResultRecord> list = new ArrayList<ResultRecord>();
	
	/**
	 * �R���X�g���N�^�i�ݒ�t�@�C����񖢎g�p�j
	 * @deprecated replaced by {@link #ResultList(GetConfig conf)}
	 */
	public ResultList() {
		this.DISP_LINK_NUM = 10;							// �f�t�H���g
		this.DISP_NODE_NUM = 25;							// �f�t�H���g
	}
	
	/**
	 * �R���X�g���N�^�i�ݒ�t�@�C�����g�p�j
	 * @param conf �ݒ�t�@�C�����I�u�W�F�N�g
	 */
	public ResultList(GetConfig conf) {
		// �y�[�W�����N�\����
		int linkNum = 10;									// �f�t�H���g
		try {
			if ( Integer.parseInt(conf.getDispLinkNum()) > 0 ) {
				linkNum =  Integer.parseInt(conf.getDispLinkNum());
			}
		}
		catch (NumberFormatException e) {
		}
		
		// �e�m�[�h�\����
		int nodeNum = 25;									// �f�t�H���g
		try {
			if ( Integer.parseInt(conf.getDispNodeNum()) > 0 ) {
				nodeNum =  Integer.parseInt(conf.getDispNodeNum());
			}
		}
		catch (NumberFormatException e) {
		}
		
		this.DISP_LINK_NUM = linkNum;
		this.DISP_NODE_NUM = nodeNum;
	}

	/**
	 * ���R�[�h���X�g�擾
	 * @return ���R�[�h���X�g
	 */
	public ArrayList<ResultRecord> getList() {
		return list;
	}

	/**
	 * ���R�[�h�擾
	 * @param index �C���f�b�N�X
	 * @return ���ʏ��ꊇ�Ǘ����X�g
	 */
	public ResultRecord getRecord(int index) {
		return list.get(index);
	}
	
	/**
	 * ���R�[�h�ǉ�
	 * @param record ���ʏ�񃌃R�[�h
	 */
	public void addRecord(ResultRecord record) {
		this.list.add(record);
	}
	
	/**
	 * ���R�[�h���X�g�\�[�g(�\�[�g�L�[�w��)
	 * @param sortKey �\�[�g�L�[
	 * @param sortAction �\�[�g�A�N�V����
	 */
	public void sortList(String sortKey, int sortAction) {
		Collections.sort(list, new ListComparator(sortKey, sortAction));
	}
	
	/**
	 * ���R�[�h���擾
	 * @return ���R�[�h��
	 */
	public int getResultNum() {
		return list.size();
	}
	
	/**
	 * 1�y�[�W�ɕ\�����郌�R�[�h�C���f�b�N�X�擾
	 * @param pageNo �\������y�[�W
	 * @return �\���J�n�ƏI���̃C���f�b�N�X���i�[�����z��
	 */
	public int[] getDispRecordIndex(int pageNo) {
		int[] index = new int[]{-1, -1};
		HashMap<Integer, String> pNodeMap = new HashMap<Integer, String>();
		int pageCount = 1;				// �y�[�W���J�E���^
		int pNodeCount = 0;				// 1�y�[�W�ӂ�̐e�m�[�h�J�E���^
		int startIndex = -1;			// 1�y�[�W�ɕ\�����郌�R�[�h�̊J�n�C���f�b�N�X
		int endIndex = -1;				// 1�y�[�W�ɕ\�����郌�R�[�h�̏I���C���f�b�N�X
		boolean isNextPage = false;	// ���y�[�W�t���O
		for (int i=0; i<list.size(); i++) {
			
			// �J�n�C���f�b�N�X�ݒ�
			if (pageNo == pageCount && startIndex == -1) {
				startIndex = i;
			}
			
			if ( (i+1) == list.size() ) {
				// ���݂̃��R�[�h���ŏI���R�[�h�̏ꍇ
				if ( !pNodeMap.containsKey(list.get(i).getNodeGroup()) ) {
					pNodeMap.put(list.get(i).getNodeGroup(), list.get(i).getName());
					pNodeCount++;
				}
				isNextPage = true;
			}
			else if ( pNodeCount < this.DISP_NODE_NUM ) {
				// �e�m�[�h�����\���e�m�[�h���ɒB���Ă��Ȃ��ꍇ
				if ( !pNodeMap.containsKey(list.get(i).getNodeGroup()) ) {
					pNodeMap.put(list.get(i).getNodeGroup(), list.get(i).getName());
					pNodeCount++;
				}
				if ( pNodeCount == this.DISP_NODE_NUM ) {
					if ( !pNodeMap.containsKey(list.get(i+1).getNodeGroup()) ) {
						isNextPage = true;
					}
				}
			}
			else {
				// �e�m�[�h�����\���e�m�[�h�ɒB�����ꍇ
				if ( !pNodeMap.containsKey(list.get(i+1).getNodeGroup()) ) {
					isNextPage = true;
				}
			}
			
			// ���y�[�W�ڍs����
			if ( isNextPage ) {
				
				// �I���C���f�b�N�X�ݒ�
				if (pageCount == pageNo) {
					endIndex = i;
					break;
				}
				pNodeCount = 0;
				isNextPage = false;
				pageCount++;
			}
		}
		if (startIndex != -1 && endIndex != -1) {
			index = new int[]{startIndex, endIndex};
		}
		return index;
	}
	
	/**
	 * ���y�[�W���擾
	 * @return ���y�[�W��
	 */
	public int getTotalPageNum() {
		int num = (int)Math.ceil(((double)getCompoundNum() / this.DISP_NODE_NUM));
		return num;
	}

	/**
	 * ���������擾
	 * @return ��������
	 */
	public int getCompoundNum() {
		HashMap<Integer, String> nodeCount = new HashMap<Integer, String>();
		for (int i=0; i<list.size(); i++) {
			// �e�m�[�h���ێ�
			if (!nodeCount.containsKey(list.get(i).getNodeGroup())) {
				nodeCount.put(list.get(i).getNodeGroup(), list.get(i).getName());
			}
		}
		return nodeCount.size();
	}
	
	
	/**
	 * �\������y�[�W�����N�擾
	 * @param totalPage ���y�[�W��
	 * @param pageNo ���݂̃y�[�W
	 * @return �\���J�n�ƏI���̃y�[�W���i�[�����z��
	 */
	public int[] getDispPageIndex(int totalPage, int pageNo) {
		int[] index = new int[2];
		// ��ɍő�y�[�W�����N����\������悤�ɂ���
		index[0] = Math.max(1, pageNo - (int)Math.floor(this.DISP_LINK_NUM / 2.0));
		index[1] = Math.min(totalPage, pageNo + (int)Math.ceil(this.DISP_LINK_NUM / 2.0) - 1);
		
		// �\���y�[�W�̏I�����ő�y�[�W�����N����菬�����ꍇ
		if (index[1] < this.DISP_LINK_NUM) {
			index[1] = Math.min(totalPage, this.DISP_LINK_NUM);
		}
		// �\���y�[�W�����ő�y�[�W�����N����菬�����ꍇ
		if ((index[1] - index[0] + 1) < this.DISP_LINK_NUM) {
			index[0] = Math.max(1, (index[1] - this.DISP_LINK_NUM + 1));
		}
		return index;
	}
	
	/**
	 * 1�y�[�W�ɕ\������e�m�[�h���Ƃ̎q�m�[�h���擾
	 * @param dispIndex �\���J�n�ƏI���̃C���f�b�N�X���i�[�����z��
	 * @return 1�y�[�W�ɕ\������e�m�[�h���̎q�m�[�h���}�b�v
	 */
	public HashMap<Integer, Integer> getDispParentNodeMap(int startIndex, int endIndex) {
		HashMap<Integer, Integer> nodeMap = new HashMap<Integer, Integer>();
		int nodeCnt = 0;
		for (int i=startIndex; i<list.size(); i++) {
			
			// �e�m�[�h���ێ�
			if (!nodeMap.containsKey(list.get(i).getNodeGroup())) {
				nodeCnt = 1;
				nodeMap.put(Integer.valueOf(list.get(i).getNodeGroup()), Integer.valueOf(nodeCnt));
			}
			else {
				nodeCnt = (int)nodeMap.get(list.get(i).getNodeGroup());
				nodeCnt++;
				nodeMap.put(list.get(i).getNodeGroup(), nodeCnt);
			}
			
			if ( i == endIndex ) {
				break;
			}
		}
		return nodeMap;
	}
	
	/**
	 * ���R�[�h���X�g�\�[�g�p�R���p���[�^
	 * ResultList�̃C���i�[�N���X�B
	 * ResultRecord���i�[�������X�g�̃\�[�g���s���B
	 */
	class ListComparator implements Comparator<Object> {
		
		/** �\�[�g�L�[ */
		private String sortKey;
		
		/** �\�[�g�A�N�V���� */
		private int sortAction;
		
		/**
		 * �R���X�g���N�^
		 * @param sortKey �\�[�g�L�[
		 */
		public ListComparator(String sortKey, int sortAction) {
			this.sortKey = sortKey;
			this.sortAction = sortAction;
		}
		
		public int compare(Object o1, Object o2){
			ResultRecord e1 =(ResultRecord)o1;
			ResultRecord e2 =(ResultRecord)o2;
			
			int ret = 0;
			
			// �\�[�g����
			if (sortKey == SORT_KEY_NAME) {				// Name�\�[�g
				if (!e1.getSortName().equals(e2.getSortName())) {
					switch (sortAction) {
						case SORT_ACTION_ASC:
							ret = (e1.getSortName()).compareTo(e2.getSortName());
							break;
						case SORT_ACTION_DESC:
							ret = (e2.getSortName()).compareTo(e1.getSortName());
							break;
					}
				}
				else {
					switch (sortAction) {
						case SORT_ACTION_ASC:
							ret = (e1.getSortAddition()).compareTo(e2.getSortAddition());
							break;
						case SORT_ACTION_DESC:
							ret = (e2.getSortAddition()).compareTo(e1.getSortAddition());
							break;
					}
				}
			}
			else if (sortKey == SORT_KEY_FORMULA) {		// Formula�\�[�g
				if (!e1.getSortFormula().equals(e2.getSortFormula())) {
					switch (sortAction) {
						case SORT_ACTION_ASC:
							ret = (e1.getSortFormula()).compareTo(e2.getSortFormula());
							break;
						case SORT_ACTION_DESC:
							ret = (e2.getSortFormula()).compareTo(e1.getSortFormula());
							break;
					}
				}
				else {
					if (!e1.getSortName().equals(e2.getSortName())) {
						ret = (e1.getSortName()).compareTo(e2.getSortName());
					}
					else {
						ret = (e1.getSortAddition()).compareTo(e2.getSortAddition());
					}
				}
			}
			else if (sortKey == SORT_KEY_EMASS) {		// ExactMass�\�[�g
				if (e1.getSortEmass() != e2.getSortEmass()) {
					switch (sortAction) {
						case SORT_ACTION_ASC:
							ret = (Float.valueOf(e1.getSortEmass())).compareTo(Float.valueOf(e2.getSortEmass()));
							break;
						case SORT_ACTION_DESC:
							ret = (Float.valueOf(e2.getSortEmass())).compareTo(Float.valueOf(e1.getSortEmass()));
							break;
					}
				}
				else {
					if (!e1.getSortName().equals(e2.getSortName())) {
						ret = (e1.getSortName()).compareTo(e2.getSortName());
					}
					else {
						ret = (e1.getSortAddition()).compareTo(e2.getSortAddition());
					}
				}
			}
			else if (sortKey == SORT_KEY_ID) {			// ID�\�[�g
				if (!e1.getSortName().equals(e2.getSortName())) {
					ret = (e1.getSortName()).compareTo(e2.getSortName());
				}
				else {
					switch (sortAction) {
						case SORT_ACTION_ASC:
							ret = e1.getId().compareTo(e2.getId());
							break;
						case SORT_ACTION_DESC:
							ret = e2.getId().compareTo(e1.getId());
							break;
					}
				}
			}
			
			return ret;
		}
	}
}
