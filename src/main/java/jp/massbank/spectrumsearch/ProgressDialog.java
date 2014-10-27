package jp.massbank.spectrumsearch;
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
 * 処理中メッセージ表示ダイアログ
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
		// ウインドウ装飾なし
		setUndecorated(true);

		// パネルセット
		JPanel panel = new JPanel();
		panel.setBackground(new Color(0x0000FF));	//パネル背景色
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		// ラベルセット
		JLabel label = new JLabel();
		label.setFont(new Font("Dialog", (Font.ITALIC|Font.BOLD), 22));
		label.setForeground(Color.white);	//フォントの色
		String text = "Searching...";
		if ( !msg.equals("") ) {
			text = msg;
		}
		label.setText(text);
		panel.add(label);

		// プログレスバーセット
		UIManager.put("ProgressBar.repaintInterval", new Integer(20));
		UIManager.put("ProgressBar.cycleTime", new Integer(1000));
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);	// 不確定モード
		panel.add(progressBar);
		add(panel);
		pack();

		// 画面中央表示
		setLocationRelativeTo(null);
	}
}
