package jp.massbank.spectrumsearch.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

public class CommonUtil {
	
	private static final Logger LOGGER = Logger.getLogger(CommonUtil.class);
	
	public static Float parseFloat(String str) {
		try {
			return Float.parseFloat(str);
		} catch (NumberFormatException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static Integer parseInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static void openBrowser(String reqUrl) {
		LOGGER.info("open browser with " + reqUrl);
		try {
			Desktop.getDesktop().browse(new URI(reqUrl));
		} catch (IOException | URISyntaxException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
	}
	
	public static int getRowByValue(TableModel model, Object value) {
		for (int i = model.getRowCount() - 1; i >= 0; --i) {
			for (int j = model.getColumnCount() - 1; j >= 0; --j) {
				if (model.getValueAt(i, j).equals(value)) {
					// what if value is not unique?
					return i;
				}
			}
		}
		return 0;
	}
	
	public static Integer getRowByValue(TableModel model, int columnIndex, Object value) {
		for (int i = model.getRowCount() - 1; i >= 0; --i) {
			if (model.getValueAt(i, columnIndex).equals(value)) {
				return i;
			}
		}
		return null;
	}

}
