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
 * UserFile�f�[�^ �N���X
 *
 * ver 1.0.0 2008.12.05
 *
 ******************************************************************************/

/**
 * UserFile�f�[�^ �N���X
 * 
 * UserFile�ǂݍ��ݏ����i�[
 */
public class UserFileData {

	/** ID */
	private String id = "";
	
	/** �s�[�N��� */
	private String[] peaks = new String[]{"0\t0"};

	/** �������� */
	private String name = "";

	/**
	 * �R���X�g���N�^
	 */
	public UserFileData() {
	}

	/**
	 * �s�[�N���ݒ�
	 * @param ps �s�[�N���
	 */
	public void setPeaks(String[] ps) {
		if (ps == null) {
			return;
		}
		peaks = ps;
	}

	/**
	 * �s�[�N���擾
	 * @return �s�[�N���
	 */
	public String[] getPeaks() {
		return peaks;
	}

	/**
	 * ���������ݒ�
	 * @param name ��������
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * ���������擾
	 * @return ��������
	 */
	public String getName() {
		return name;
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
}
