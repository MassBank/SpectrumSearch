/*******************************************************************************
 *
 * Copyright (C) 2010 JST-BIRD MassBank
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
 * ���������b�Z�[�W�\���_�C�A���O
 *
 * ver 1.0.0 2010.11.10
 *
 ******************************************************************************/

import javax.swing.*;
import java.awt.*;

public class ProgressDialog extends JDialog {

	public ProgressDialog(Frame parent){
		super(parent, false);
		setDialog("");
	}

	public ProgressDialog(Frame parent, String msg){
		super(parent, false);
		setDialog(msg);
	}

	public void setDialog(String msg) {
		// �E�C���h�E�����Ȃ�
		setUndecorated(true);

		// �p�l���Z�b�g
		JPanel panel = new JPanel();
		panel.setBackground(new Color(0x0000FF));	//�p�l���w�i�F
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		// ���x���Z�b�g
		JLabel label = new JLabel();
		label.setFont(new Font("Dialog", (Font.ITALIC|Font.BOLD), 22));
		label.setForeground(Color.white);	//�t�H���g�̐F
		String text = "Searching...";
		if ( !msg.equals("") ) {
			text = msg;
		}
		label.setText(text);
		panel.add(label);

		// �v���O���X�o�[�Z�b�g
		UIManager.put("ProgressBar.repaintInterval", new Integer(20));
		UIManager.put("ProgressBar.cycleTime", new Integer(1000));
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);	// �s�m�胂�[�h
		panel.add(progressBar);
		add(panel);
		pack();

		// ��ʒ����\��
		setLocationRelativeTo(null);
	}
}
