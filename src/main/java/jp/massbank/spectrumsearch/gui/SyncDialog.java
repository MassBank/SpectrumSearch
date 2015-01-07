package jp.massbank.spectrumsearch.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;

import jp.massbank.spectrumsearch.SearchPage;
import jp.massbank.spectrumsearch.entity.constant.SystemProperties;
import jp.massbank.spectrumsearch.logic.MassBankRecordLogic;
import jp.massbank.spectrumsearch.util.MassBankDirSyncThread;

import org.apache.log4j.Logger;

public class SyncDialog extends JDialog {

	private static final long serialVersionUID = 3762607965692279780L;
	private static final Logger LOGGER = Logger.getLogger(SyncDialog.class);

	private static final String TITLE = "Synchronize MassBank Records";
	private static final int ONE_SECOND = 1000;
	
	private SearchPage parent;
	private JTextField txtDirPath;
	private JFileChooser fileChooser;
	private JProgressBar progressBar;
	private MassBankDirSyncThread mbDirSyncThread;
	private Timer timer;
	
	public SyncDialog(SearchPage parent){
		super(parent, TITLE, true);
		this.parent = parent;
	}
	
	public void showDialog() {
		if (parent != null) {
			Dimension parentSize = parent.getSize();
			Point p = parent.getLocation();
			setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
			setSize(new Dimension(1000, 100));
		}
		
		initDirTextField();
		initDirChooser();
		initProgressBar();
		initTimer();
		getContentPane().add(getPanel());
		
	    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    setResizable(false);
	    pack(); 
	    setVisible(true);
	}
	
	private void initDirTextField() {
		txtDirPath = new JTextField((new File(SystemProperties.getInstance().getDirPath())).getAbsolutePath());
	}
	
	private void initDirChooser() {
		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(SystemProperties.getInstance().getDirPath() + "/."));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false); // disable the "All files" option.
	}

	private void initProgressBar() {
		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		progressBar.setIndeterminate(true);
		progressBar.setString("counting files to synchronous...");
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				LOGGER.info("start count files");
				MassBankRecordLogic mbRecordLogic = new MassBankRecordLogic();
				int total = mbRecordLogic.getTotalFileCountInFolder(getSyncDirPath());
				LOGGER.info("end count files");
				
				progressBar.setIndeterminate(false);
				progressBar.setMaximum(total);
				progressBar.setString(total + " files to synchronous");
			}

		}).start();
	}

	private void initTimer() {
        timer = new Timer(ONE_SECOND, new ActionListener() {
        	
			@Override
			public void actionPerformed(ActionEvent e) {
				progressBar.setString(mbDirSyncThread.getCount() + "/" + progressBar.getMaximum() + " files finished. (" + (mbDirSyncThread.getCount() * 100/progressBar.getMaximum()) + "%)");
				progressBar.setValue(mbDirSyncThread.getCount());
			}
			
	    });
	}
	
	private JPanel getPanel() {
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		panel.setLayout(layout);
		
		JButton btnCancel = getBtnCancel();
		JButton btnSync = getBtnSync();
		JButton btnDirChooser = getBtnDirChooser();
		JLabel label = new JLabel("Please select a massbank record directory to synchronize.");
		
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(label, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addGroup(layout.createSequentialGroup()
				.addComponent(txtDirPath, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(btnDirChooser, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
			.addComponent(progressBar)
			.addGroup(layout.createSequentialGroup()
				.addComponent(btnSync, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(btnCancel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addComponent(label)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(txtDirPath)
				.addComponent(btnDirChooser))
			.addComponent(progressBar)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(btnSync)
				.addComponent(btnCancel))
		);
		
		return panel;
	}
	
	private JButton getBtnCancel() {
		JButton btn = new JButton("Cancel"); 
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mbDirSyncThread.stop();
				timer.stop();
				setVisible(false);
				dispose();
			}
		});
		return btn;
	}
	
	private JButton getBtnSync() {
		final JButton btn = new JButton("Start Sync"); 
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btn.setEnabled(false);

				new Thread(new Runnable() {

					@Override
					public void run() {
						
						timer.start();

						LOGGER.info("start sync files");
						mbDirSyncThread = new MassBankDirSyncThread(getSyncDirPath());
						mbDirSyncThread.start();
						LOGGER.info("end sync files");
						
					}

				}).start();
			}
		});
		return btn;
	}
	
	private JButton getBtnDirChooser() {
		JButton btn = new JButton("Change"); 
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnValue = fileChooser.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		          File selectedFile = fileChooser.getSelectedFile();
		          txtDirPath.setText(selectedFile.getAbsolutePath());
		        }
			}
		});
		return btn;
	}
	
	private String getSyncDirPath() {
		String dirPath = txtDirPath.getText().trim();
		if (!SystemProperties.getInstance().getDirPath().equals(dirPath)) {
			SystemProperties.updateParam(SystemProperties.Key.DIR_PATH, dirPath);
			SystemProperties.loadParams();
		}
		return dirPath;
	}
	
}
