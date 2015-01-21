package jp.massbank.spectrumsearch.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ProgressDialog extends AbstractDialog {

	private static final long serialVersionUID = -964096635641256745L;
	private static boolean isHidden;
	
	public ProgressDialog(Frame parent) {
		super(parent, ModalityType.DOCUMENT_MODAL);
		setDialogContent();
	}
	
	public void showDialog() {
		
		setLocation();
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				setVisible(true);
				// time consuming algorithm.
				if (ProgressDialog.isHidden) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							setVisible(false);
						}
					});
				}
			}

		}).start();
	}
	

	public void hideDialog() {
		setVisible(false);
		ProgressDialog.isHidden = true;
	}
	
	private void setDialogContent() {
		// ウインドウ装飾なし
		setUndecorated(true);
		getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		
		// パネルセット
		JPanel panel = new JPanel();
		panel.setBackground(new Color(0x0000FF));	//パネル背景色
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		// ラベルセット
		JLabel label = new JLabel();
		label.setFont(new Font("Dialog", (Font.ITALIC|Font.BOLD), 22));
		label.setForeground(Color.white);
		label.setText("search...");
		panel.add(label);
		
		// プログレスバーセット
		UIManager.put("ProgressBar.repaintInterval", Integer.valueOf(20));
		UIManager.put("ProgressBar.cycleTime", Integer.valueOf(1000));
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);	// 不確定モード
		panel.add(progressBar);
		
		add(panel);
		pack();
		setResizable(false);
		
		setLocationRelativeTo(getOwner());
	}

}
