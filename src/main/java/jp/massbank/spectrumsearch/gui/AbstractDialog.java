package jp.massbank.spectrumsearch.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;

import javax.swing.JDialog;

public abstract class AbstractDialog extends JDialog {

	private static final long serialVersionUID = 7470967027631297850L;
	
	public AbstractDialog(Frame parent, String title) {
		super(parent, title, true);
	}
	
	public AbstractDialog(Frame parent, ModalityType documentModal) {
		super(parent, documentModal);
	}
	
	protected void setLocation() {
		if (getOwner() != null) {
			Dimension parentSize = getOwner().getSize();
			Point p = getOwner().getLocation();
			setLocation(p.x + (parentSize.width - getWidth()) / 2, p.y + (parentSize.height - getHeight()) / 2);
		}
	}


}
