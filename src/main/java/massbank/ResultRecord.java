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
 * �������ʃ��R�[�h���i�[�N���X
 * �����Ƀq�b�g����1�X�y�N�g���̏����i�[����f�[�^�N���X
 *   �񋟋@�\
 *     �E�m�[�h�O���[�v�擾
 *     �E�m�[�h�O���[�v�ݒ�
 *     �E�T�C�g�擾
 *     �E�T�C�g�ݒ�
 *     �E�������ʎ擾
 *     �E�������ʐݒ�
 *     �E�������ʁi�\�[�g�p�j�擾
 *     �E�������ʁi�\���p�j�擾
 *     �E�g�����擾
 *     �E�g�����ݒ�
 *     �E�g�����i�\�[�g�p�j�擾
 *     �EID�擾
 *     �EID�ݒ�
 *     �E�C�I���擾
 *     �E�C�I���ݒ�
 *     �E���R�[�h���擾
 *     �E���R�[�h���ݒ�
 *     �E���������擾
 *     �E���������i�\�[�g�p�j�擾
 *     �E�t�����擾
 *     �E�t�����i�\�[�g�p�j�擾
 *     �E�e�m�[�h�������N�i�\���p�j�擾
 *     �E�q�m�[�h�������N�i�\���p�j�擾
 *
 * ver 1.0.3 2008.12.05
 *
 ******************************************************************************/
package massbank;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ResultRecord {
	
	/** �m�[�h�O���[�v */
	private int nodeGroup = -1;
	
	/** ���R�[�h��� */
	private String info = "";			// �Z�~�R������؂背�R�[�h��񕶎���
	
	/** �������� */
	private String name = "";

	/** ���������i�\�[�g�p�j */
	private String sortName = "";
	
	/** �t����� */
	private String addition = "";
	
	/** �t�����i�\�[�g�p�j */
	private String sortAddition = "";
	
	/** ���R�[�hID */
	private String id = "";
	
	/** �C�I�� */
	private String ion = "";
	
	/** �g���� */
	private String formula = "";
	
	/** �g�����i�\�[�g�p�j */
	private String sortFormula = "";
	
	/** �������� */
	private String emass = "";
	
	/** �������ʁi�\�[�g�p�j */
	private float sortEmass = 0.0f;
	
	/** �������ʁi�\���p�j */
	private String dispEmass = "";
	
	/** �T�C�g */
	private String contributor = "";
	
	/** �e�m�[�h�������N�i�\���p�j */
	private String parentLink = "";
	
	/** �q�m�[�h�������N�i�\���p�j */
	private String childLink = "";
	
	/** ���q���t�H�[�}�b�g */
	private DecimalFormat numFormat = new DecimalFormat("000");
	
	/** �������ʏ����_�ȉ��t�H�[�}�b�g */
	private DecimalFormat backPeriodFormat = new DecimalFormat("0.00000");
	
	
	/**
	 * �R���X�g���N�^
	 */
	public ResultRecord() {
	}

	/**
	 * �m�[�h�O���[�v�擾
	 * @return �m�[�h�O���[�v
	 */
	public int getNodeGroup() {
		return nodeGroup;
	}

	/**
	 * �m�[�h�O���[�v�ݒ�
	 * @param nodeGroup �m�[�h�O���[�v
	 */
	public void setNodeGroup(int nodeGroup) {
		this.nodeGroup = nodeGroup;
	}
	
	/**
	 * �T�C�g�擾
	 * @return �T�C�g
	 */
	public String getContributor() {
		return contributor;
	}

	/**
	 * �T�C�g�ݒ�
	 * @param contributor �T�C�g
	 */
	public void setContributor(String contributor) {
		this.contributor = contributor;
	}

	/**
	 * �������ʎ擾
	 * @return ��������
	 */
	public String getEmass() {
		return emass;
	}
	
	/**
	 * �������ʐݒ�
	 * @param emass ��������
	 */
	public void setEmass(String emass) {
		
		// �������ʐݒ�
		this.emass = emass;
		
		// �������ʂ���\�[�g�p�������ʐݒ�
		if (!emass.equals("") && emass.length() != 0) {
		this.sortEmass = Float.parseFloat(emass);
		}
		
		// �������ʂ���\���p�������ʐݒ�
		StringBuffer dispEmass = new StringBuffer();
		if (!emass.equals("") && emass.length() != 0) {
			
			String forePeriod = "";	// �s���I�h�O
			String backPeriod = "";	// �s���I�h�ȍ~
			if (emass.indexOf(".") != -1) {
				forePeriod = emass.substring(0, emass.indexOf("."));
				backPeriod = emass.substring(emass.indexOf("."));
			}
			else {
				forePeriod = emass;
			}
			
			// ��������3���Ƀt�H�[�}�b�g�i���󔒋l�߁j
			for (int i=4; i>0; i--) {
				if (i == forePeriod.length()) {
					dispEmass.append(forePeriod);
					break;
				}
				else {
					dispEmass.append("&nbsp;&nbsp;");
				}
			}
			
			// �������������_�t��5���Ƀt�H�[�}�b�g�i�E0�l�߁j
			backPeriod = "0" + backPeriod;
			backPeriod = backPeriodFormat.format(Double.parseDouble(backPeriod));
			backPeriod = backPeriod.substring(backPeriod.indexOf("."));
			
			// �������{�������i�����_�t���j
			dispEmass.append(backPeriod);
		}
		this.dispEmass = dispEmass.toString();
	}
	
	/**
	 * �������ʁi�\�[�g�p�j�擾
	 * @return �������ʁi�\�[�g�p�j
	 */
	public float getSortEmass() {
		return sortEmass;
	}
	
	/**
	 * �������ʁi�\���p�j�擾
	 * @return �������ʁi�\���p�j
	 */
	public String getDispEmass() {
		return dispEmass;
	}
	
	/**
	 * �g�����擾
	 * @return �g����
	 */
	public String getFormula() {
		return formula;
	}

	/**
	 * �g�����ݒ�
	 * @param formula �g����
	 */
	public void setFormula(String formula) {
		
		// �g�����ݒ�
		this.formula = formula;
		
		// �g��������\�[�g�p�g�����ݒ�
		StringBuffer sortFormula = new StringBuffer();
		if (!formula.equals("") && formula.length() != 0) {
			
			int current = 0;									// �������C���f�b�N�X
			int next = 0;										// �������C���f�b�N�X
			char[] c = formula.toCharArray();					// �g���������z��
			String symbol = String.valueOf(c[current]);			// ���q�L��
			String num = "";									// ���q��
			
			for (current=0; current<c.length; current++) {
				next = current + 1;
				
				// ���̕������Ō�̕����A�܂��͑啶���p�����̏ꍇ
				if (next == c.length || Character.isUpperCase(c[next])) {
					sortFormula.append(symbol);
					if (!num.equals("")) {
						sortFormula.append(numFormat.format(Integer.parseInt(num)));
					}
					else {
						sortFormula.append(numFormat.format(1));
					}
					// ���̕��������݂���ꍇ
					if (next != c.length) {
						symbol = String.valueOf(c[next]);
						num = "";
					}
				}
				// ���̕������������p�����̏ꍇ
				else if (Character.isLowerCase(c[next])) {
					symbol += String.valueOf(c[next]);
				}
				// ���̕����������̏ꍇ
				else if (Character.isDigit(c[next])) {
					num += String.valueOf(c[next]);
				}
			}
		}
		this.sortFormula = sortFormula.toString();
	}

	/**
	 * �g�����i�\�[�g�p�j�擾
	 * @return �g�����i�\�[�g�p�j  
	 */
	public String getSortFormula() {
		return sortFormula;
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
	 * �C�I���擾
	 * @return �C�I��
	 */
	public String getIon() {
		return ion;
	}
	
	/**
	 * �C�I���ݒ�
	 * @param ion �C�I��
	 */
	public void setIon(String ion) {
		this.ion = ion;
	}
	
	/**
	 * ���R�[�h���擾
	 * @return ���R�[�h���
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * ���R�[�h���ݒ�
	 * @param info ���R�[�h���
	 */
	public void setInfo(String info) {
		// ���R�[�h���ݒ�
		this.info = info;
		
		String[] tmp = info.split(";");
		
		// ���������ݒ�
		this.name = tmp[0].trim();
		
		// �\�[�g�p���������ݒ�
		this.sortName = tmp[0].trim();
		
		// �t�����ݒ�
		StringBuffer addition = new StringBuffer();
		for (int i=0; i<tmp.length; i++) {
			if (i==0) {
				continue;
			}
			addition.append(tmp[i].trim());
			if (i != (tmp.length-1)) {
				addition.append("; ");
			}
		}
		this.addition = addition.toString();
		
		// �\�[�g�p�t�����ݒ�
		this.sortAddition = addition.toString();
		
		// �����N�p�e�m�[�h���ݒ�
		final int maxLinkStr = 50;
		if (tmp[0].trim().length() > maxLinkStr) {
			StringBuffer parentLink = new StringBuffer();
			parentLink.append(tmp[0].trim().substring(0, maxLinkStr));
			parentLink.append("...");
			this.parentLink = sanitize(parentLink.toString());
		}
		else {
			this.parentLink = sanitize(tmp[0].trim());
		}
		
		// �����N�p�q�m�[�h���ݒ�
		this.childLink = sanitize(addition.toString());
	}

	/**
	 * ���������擾
	 * @return ��������
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * ���������i�\�[�g�p�j�擾
	 * @return ���������i�\�[�g�p�j
	 */
	public String getSortName() {
		return sortName;
	}
	
	/**
	 * �t�����擾
	 * @return �t�����
	 */
	public String getAddition() {
		return addition;
	}
	
	/**
	 * �t�����i�\�[�g�p�j�擾
	 * @return �t�����i�\�[�g�p�j
	 */
	public String getSortAddition() {
		return sortAddition;
	}
	
	/**
	 * �e�m�[�h�������N�i�\���p�j�擾
	 * @return �e�m�[�h�������N�i�\���p�j
	 */
	public String getParentLink() {
		return parentLink;
	}
	
	/**
	 * �q�m�[�h�������N�i�\���p�j�擾
	 * @return �q�m�[�h�������N�i�\���p�j
	 */
	public String getChildLink() {
		return childLink;
	}

	/**
	 * �T�j�^�C�W���O����
	 * @param value �T�j�^�C�W���O���镶����
	 * @return �T�j�^�C�W���O����������
	 */
	private String sanitize(String value) {
		return value.replace("&", "&amp;")
					 .replace("<", "&lt;")
					 .replace(">", "&gt;")
					 .replace("\"", "&quot;");
		
	}
}
