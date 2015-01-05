package jp.massbank.spectrumsearch.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

public class SyncProgressMonitor extends JFrame implements ActionListener {

	private static final long serialVersionUID = 9148785909467066915L;

	private static ProgressMonitor pbar;
	private static int counter = 0;
	
	public SyncProgressMonitor() {
		setSize(250, 100);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    pbar = new ProgressMonitor(null, "Monitoring Progress", "Initializing . . .", 0, 100);
	 	// Fire a timer every once in a while to update the progress.
	    Timer timer = new Timer(500, this);
	    timer.start();
	    setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		SwingUtilities.invokeLater(new Update());
	}
	
	class Update implements Runnable {
		public void run() {
			if (pbar.isCanceled()) {
				pbar.close();
				System.exit(1);
			}
			pbar.setProgress(counter);
			pbar.setNote("Operation is " + counter + "% complete");
			counter += 2;
		}
	}

}
