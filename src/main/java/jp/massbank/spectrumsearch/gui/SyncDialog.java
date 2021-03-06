package jp.massbank.spectrumsearch.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;

import jp.massbank.spectrumsearch.entity.constant.SystemProperties;
import jp.massbank.spectrumsearch.logic.MassBankRecordLogic;
import jp.massbank.spectrumsearch.util.MassBankDirSyncThread;

import org.apache.log4j.Logger;

public class SyncDialog extends AbstractDialog {

	private static final long serialVersionUID = 3762607965692279780L;
	private static final Logger LOGGER = Logger.getLogger(SyncDialog.class);

	private static final String TITLE = "Synchronize MassBank Records";
	private static final int ONE_SECOND = 1000;
	
	private JTextField txtDirPath;
	private JFileChooser fileChooser;
	private JProgressBar progressBar;
	private MassBankDirSyncThread mbDirSyncThread;
	private Timer timer;
	
	public SyncDialog(Frame parent){
		super(parent, TITLE);
	}
	
	public void showDialog() {
		setSize(new Dimension(1000, 100));
		setLocation();
		initDirTextField();
		initDirChooser();
		initProgressBar();
		initTimer();
		getContentPane().add(getPanel());
		
	    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    setResizable(false);
	    pack(); 
	    setVisible(true);
	    
	    this.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosed(WindowEvent e) {
	        	if (mbDirSyncThread != null) {
					mbDirSyncThread.stop();
				}
				timer.stop();
	        }
	    });
	}
	
	private void initDirTextField() {
		txtDirPath = new JTextField((new File(SystemProperties.getInstance().getDirPath())).getAbsolutePath());
		txtDirPath.setColumns(30);
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
		runSyncDirFileCount();
	}

	private void initTimer() {
        timer = new Timer(ONE_SECOND, new ActionListener() {
        	
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((mbDirSyncThread.getCount() == progressBar.getMaximum()) || mbDirSyncThread.isStopped()) {
					progressBar.setString("file synchronization completed.");
					progressBar.setValue(progressBar.getMaximum());
				} else {
					progressBar.setString(mbDirSyncThread.getCount() + "/" + progressBar.getMaximum() + " files finished. (" + (mbDirSyncThread.getCount() * 100/progressBar.getMaximum()) + "%)");
					progressBar.setValue(mbDirSyncThread.getCount());
				}
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
		JLabel lblPath = new JLabel("Path:");
		JLabel lblBlank = new JLabel("");
		
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(label, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addGroup(layout.createSequentialGroup()
				.addComponent(lblPath, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(txtDirPath, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(btnDirChooser, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
			.addComponent(progressBar)
			.addGroup(layout.createSequentialGroup()
				.addComponent(lblBlank, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(btnSync, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(btnCancel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(lblBlank, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				)
		);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addComponent(label)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(lblPath)
				.addComponent(txtDirPath)
				.addComponent(btnDirChooser))
			.addComponent(progressBar)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(lblBlank)
				.addComponent(btnSync)
				.addComponent(btnCancel)
				.addComponent(lblBlank)
				)
		);
		
		return panel;
	}
	
	private JButton getBtnCancel() {
		JButton btn = new JButton("Close"); 
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (mbDirSyncThread != null) {
					mbDirSyncThread.stop();
				}
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
		          runSyncDirFileCount();
		        }
			}
		});
		return btn;
	}
	
	private void runSyncDirFileCount() {
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
	
	private String getSyncDirPath() {
		String dirPath = txtDirPath.getText().trim();
		if (!SystemProperties.getInstance().getDirPath().equals(dirPath)) {
			SystemProperties.updateParam(SystemProperties.Key.DIR_PATH, dirPath);
			SystemProperties.loadParams();
		}
		return dirPath;
	}
	
}
