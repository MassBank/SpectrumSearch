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
 * SearchPage �N���X
 *
 * ver 1.0.20 2011.12.16
 *
 ******************************************************************************/

import java.applet.AppletContext;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import massbank.GetConfig;
import massbank.GetInstInfo;
import massbank.MassBankCommon;

/**
 * SearchPage �N���X
 */
@SuppressWarnings("serial")
public class SearchPage extends JApplet {

	public static String baseUrl = "";

	private GetInstInfo instInfo = null;
	
	private static int PRECURSOR = -1;
	private static float TOLERANCE = 0.3f;
	public static int CUTOFF_THRESHOLD = 5;
	private static final int LEFT_PANEL_WIDTH = 430;

	private static final int TAB_ORDER_DB = 0;
	private static final int TAB_ORDER_FILE = 1;
	private static final int TAB_RESULT_DB = 0;
	private static final int TAB_VIEW_COMPARE = 0;
	private static final int TAB_VIEW_PACKAGE = 1;

	public static final String COL_LABEL_NAME = "Name";
	public static final String COL_LABEL_SCORE = "Score";
	public static final String COL_LABEL_HIT = "Hit";
	public static final String COL_LABEL_ID = "ID";
	public static final String COL_LABEL_ION = "Ion";
	public static final String COL_LABEL_CONTRIBUTOR = "Contributor";
	public static final String COL_LABEL_NO = "No.";
	public static final String COL_LABEL_ORDER = "Order";
	public static final String COL_LABEL_TYPE = "Type";
	public static final String COL_LABEL_MATCH = "Match";
	public static final String COL_LABEL_DISABLE = "Disable";
	public static final String COL_LABEL_PEAK = "Peak";
	public static final String COL_LABEL_PRECURSOR = "Precursor";

	private UserFileData[] userDataList = null;

	public static final String TABLE_QUERY_FILE = "QueryFile";
	public static final String TABLE_QUERY_DB = "QueryDb";
	public static final String TABLE_RESULT = "Result";
	
	private TableSorter fileSorter = null;						// �N�G���[�t�@�C���e�[�u�����f��
	private TableSorter querySorter = null; 					// �N�G���[DB�e�[�u�����f��
	private TableSorter resultSorter = null;					// �������ʃe�[�u�����f��
	
	private JTable queryFileTable = null;						// �N�G���[���[�U�t�@�C���e�[�u��
	private JTable queryDbTable = null;						// �N�G���[DB�e�[�u��
	private JTable resultTable = null;							// �������ʃe�[�u��
	
	private PeakPanel queryPlot = new PeakPanel(false);		// �N�G���[�X�y�N�g���p�l��
	private PeakPanel resultPlot = new PeakPanel(false);		// �������ʃX�y�N�g���p�l��
	private PeakPanel compPlot = new PeakPanel(true);			// ��r�p�X�y�N�g���p�l��

	private JTabbedPane queryTabPane = new JTabbedPane();		// �N�G���[�^�u�y�C��
	private JTabbedPane resultTabPane = new JTabbedPane();		// �������ʃ^�u�y�C��
	private JTabbedPane viewTabPane = new JTabbedPane();		// �X�y�N�g���\���^�u�y�C��

	private JScrollPane queryFilePane = null;					// �N�G���[�t�@�C���y�C��
	private JScrollPane resultPane = null;						// �N�G���[DB�y�C��
	private JScrollPane queryDbPane = null;					// �������ʃy�C��
	
	private JButton btnName = new JButton("Search Name");
	private JButton btnAll = new JButton("All");

	private String saveSearchName = "";

	private JButton etcPropertyButton = new JButton("Search Parameter Setting");
	
	private boolean isRecActu;			// �X�y�N�g�������t���O(�����X�y�N�g��)
	private boolean isRecInteg;			// �X�y�N�g�������t���O(�����X�y�N�g��)
	private boolean isDispSelected;		// Package View�\���t���O(�I�����R�[�h)
	private boolean isDispRelated;		// Package View�\���t���O(�֘A�X�y�N�g��)
	
	private JRadioButton tolUnit1 = new JRadioButton("unit", true);
	private JRadioButton tolUnit2 = new JRadioButton("ppm");

	private Map<String, List<String>> instGroup;							// ���u��ʃO���[�v�}�b�v
	private LinkedHashMap<String, JCheckBox> instCheck;					// ���u��ʃ`�F�b�N�{�b�N�X�i�[�p
	private HashMap<String, Boolean> isInstCheck;							// ���u��ʃ`�F�b�N�{�b�N�X�l�i�[�p
	private LinkedHashMap<String, JCheckBox> msCheck;						// MS��ʃ`�F�b�N�{�b�N�X�i�[�p
	private HashMap<String, Boolean> isMsCheck;							// MS��ʃ`�F�b�N�{�b�N�X�l�i�[�p
	private LinkedHashMap<String, JRadioButton> ionRadio;					// �C�I����ʃ��W�I�{�^���i�[�p
	private HashMap<String, Boolean> isIonRadio;							// �C�I����ʃ��W�I�{�^���l�i�[�p
	
	private boolean isSubWindow = false;

	private JLabel hitLabel = new JLabel(" ");

	private ArrayList<String[]> nameList = new ArrayList<String[]>();

	private ArrayList nameListAll = new ArrayList();

	private String[] siteList;

	public static String[] siteNameList;

	private JPanel parentPanel2 = null;

	private PackageViewPanel pkgView = null;					// PackageView�R���|�[�l���g
	
	private MassBankCommon mbcommon = new MassBankCommon();
	private final Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);

	private static int seaqId = 0;
	private static int seaqCompound = 0;

	public static AppletContext context = null;				// �A�v���b�g�R���e�L�X�g
	public static int initAppletWidth = 0;					// �A�v���b�g������ʃT�C�Y(��)
	public static int initAppletHight = 0;					// �A�v���b�g������ʃT�C�Y(����)
	
	public static final int MAX_DISPLAY_NUM = 30;				// Package View�ő�\���\����
	
	private CookieManager cm;							// Cookie Manager
//	private final String COOKIE_PRE = "PRE";			// Cookie���L�[�iPRECURSOR�j
	private final String COOKIE_TOL = "TOL";			// Cookie���L�[�iTOLERANCE�j
	private final String COOKIE_CUTOFF = "CUTOFF"; 	// Cookie���L�[�iCOOKIE_CUTOFF�j
	private final String COOKIE_INST = "INST";			// Cookie���L�[�iINSTRUMENT�j
	private final String COOKIE_MS = "MS";				// Cookie���L�[�iMS�j
	private final String COOKIE_ION = "ION";			// Cookie���L�[�iION�j

	private final JRadioButton dispSelected = new JRadioButton("selected", true);
	private final JRadioButton dispRelated = new JRadioButton("related");
	private final JLabel lbl2 = new JLabel("Package View display mode  : ");

	public ProgressDialog dlg;
	public String[] ps = null;
	public String param = "";

	/**
	 * ���C���v���O����
	 */
	public void init() {
		
		// �A�v���b�g�R���e�L�X�g�擾
		context = getAppletContext();
		
		// �A�v���b�g������ʃT�C�Y�擾
		initAppletWidth = getWidth();
		initAppletHight = getHeight();

		// ���ݒ�t�@�C������A�g�T�C�g��URL���擾
		String confPath = getCodeBase().toString();
		confPath = confPath.replaceAll("/jsp", "");
		GetConfig conf = new GetConfig(confPath);
		siteNameList = conf.getSiteName();
		baseUrl = conf.getServerUrl();
		
		// Cookie��񃆁[�e�B���e�B������
		cm = new CookieManager(this, "SerchApplet", 30, conf.isCookie());
		
		// Precursor m/z��񏉊���
		initPreInfo();
		
		// Tolerance��񏉊���
		initTolInfo();
		
		// Cutoff Threshold��񏉊���
		initCutoffInfo();
		
		// ���u��ʏ�񏉊���
		instInfo = new GetInstInfo(confPath);
		initInstInfo();
		
		// MS��ʏ�񏉊���
		initMsInfo();
		
		// �C�I����ʏ�񏉊���
		initIonInfo();

		
		// �E�C���h�E����
		createWindow();

		// �������_�C�A���O
		this.dlg = new ProgressDialog(getFrame());

		// ���[�U�[�t�@�C���Ǎ���
		if (getParameter("file") != null) {
			loadFile(getParameter("file"));
		}
		// ����ʂ���̃N�G���ǉ�
		else if (getParameter("num") != null) {
			DefaultTableModel dm = (DefaultTableModel) querySorter.getTableModel();
			dm.setRowCount(0);

			int num = Integer.parseInt(getParameter("num"));
			for (int i = 0; i < num; i++) {
				String pnum = Integer.toString(i + 1);
				String id = getParameter("qid" + pnum);
				String name = getParameter("name" + pnum);
				String site = getParameter("site" + pnum);
				String[] idNameSite = new String[] { id, name, site };
				nameList.add(idNameSite);

				site = siteNameList[Integer.parseInt(site)];
				String[] idNameSite2 = new String[] { id, name, site, String.valueOf(i + 1) };
				dm.addRow(idNameSite2);
			}
		}
	}
	
	/**
	 * ���x�����W�y�у}�X�����W�ݒ�
	 */
	public void setAllPlotAreaRange() {
		queryPlot.setIntensityRange(PeakPanel.INTENSITY_MAX);
		compPlot.setIntensityRange(PeakPanel.INTENSITY_MAX);
		resultPlot.setIntensityRange(PeakPanel.INTENSITY_MAX);
		PeakData qPeak = queryPlot.getPeaks(0);
		PeakData rPeak = resultPlot.getPeaks(0);
		if (qPeak == null && rPeak == null)
			return;
		double qMax = 0d;
		double rMax = 0d;
		if (qPeak != null)
			qMax = qPeak.compMaxMzPrecusor(queryPlot.getPrecursor());
		if (rPeak != null)
			rMax = rPeak.compMaxMzPrecusor(resultPlot.getPrecursor());
		if (qMax > rMax) {
			queryPlot.setPeaks(null, -1);
			setAllPlotAreaRange(queryPlot);
		} else {
			resultPlot.setPeaks(null, -1);
			setAllPlotAreaRange(resultPlot);
		}
	}

	/**
	 * ���x�����W�y�у}�X�����W�ݒ�
	 * @param panel PeakPanel
	 */
	public void setAllPlotAreaRange(PeakPanel panel) {
		if (panel == queryPlot) {
			compPlot.setMass(queryPlot.getMassStart(),
					queryPlot.getMassRange(), queryPlot.getIntensityRange());
			resultPlot.setMass(queryPlot.getMassStart(), queryPlot.getMassRange(),
					queryPlot.getIntensityRange());
		}
		else if (panel == compPlot) {
			queryPlot.setMass(compPlot.getMassStart(), compPlot.getMassRange(),
					compPlot.getIntensityRange());
			resultPlot.setMass(compPlot.getMassStart(), compPlot.getMassRange(),
					compPlot.getIntensityRange());
		}
		else if (panel == resultPlot) {
			queryPlot.setMass(resultPlot.getMassStart(), resultPlot.getMassRange(),
					resultPlot.getIntensityRange());
			compPlot.setMass(resultPlot.getMassStart(), resultPlot.getMassRange(),
					resultPlot.getIntensityRange());
		}
	}

	/**
	 * �ő勭�x�擾
	 * �}�X�����W���s�[�N���ł̍ő勭�x�擾
	 * @param start
	 * @param end
	 */
	public int getMaxIntensity(double start, double end) {
		PeakData qPaek = queryPlot.getPeaks(0);
		PeakData dPeak = resultPlot.getPeaks(0);
		int qm = 0;
		int dm = 0;
		if (qPaek != null)
			qm = qPaek.getMaxIntensity(start, end);
		if (dPeak != null)
			dm = dPeak.getMaxIntensity(start, end);
		return Math.max(qm, dm);
	}

	/**
	 * �E�C���h�E����
	 */
	private void createWindow() {
		
		// �c�[���`�b�v�}�l�[�W���[�ݒ�
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(50);
		ttm.setDismissDelay(8000);
		
		// Search�p�l��
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		Border border = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
				new EmptyBorder(1, 1, 1, 1));
		mainPanel.setBorder(border);
		
		// *********************************************************************
		// User File Query�^�u
		// *********************************************************************
		DefaultTableModel fileDm = new DefaultTableModel();
		fileSorter = new TableSorter(fileDm, TABLE_QUERY_FILE);
		queryFileTable = new JTable(fileSorter) {
			@Override
			public boolean isCellEditable(int row, int column) {
//				super.isCellEditable(row, column);
				// �I�[�o�[���C�h�ŃZ���ҏW��s�Ƃ���
				return false;
			}
		};
		queryFileTable.addMouseListener(new TblMouseListener());
		fileSorter.setTableHeader(queryFileTable.getTableHeader());
		queryFileTable.setRowSelectionAllowed(true);
		queryFileTable.setColumnSelectionAllowed(false);
		queryFileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		String[] col = { COL_LABEL_NO, COL_LABEL_NAME, COL_LABEL_ID };
		((DefaultTableModel) fileSorter.getTableModel()).setColumnIdentifiers(col);
		(queryFileTable.getColumn(queryFileTable.getColumnName(0)))
				.setPreferredWidth(44);
		(queryFileTable.getColumn(queryFileTable.getColumnName(1)))
				.setPreferredWidth(LEFT_PANEL_WIDTH - 44);
		(queryFileTable.getColumn(queryFileTable.getColumnName(2)))
				.setPreferredWidth(70);
		
		ListSelectionModel lm = queryFileTable.getSelectionModel();
		lm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lm.addListSelectionListener(new LmFileListener());
		queryFilePane = new JScrollPane(queryFileTable);
		queryFilePane.addMouseListener(new PaneMouseListener());
		queryFilePane.setPreferredSize(new Dimension(300, 300));
		
		
		// *********************************************************************
		// Result�^�u
		// *********************************************************************
		DefaultTableModel resultDm = new DefaultTableModel();
		resultSorter = new TableSorter(resultDm, TABLE_RESULT);
		resultTable = new JTable(resultSorter) {
			@Override
			public String getToolTipText(MouseEvent me) {
//				super.getToolTipText(me);
				// �I�[�o�[���C�h�Ńc�[���`�b�v�̕������Ԃ�
				Point pt = me.getPoint();
				int row = rowAtPoint(pt);
				if (row < 0) {
					return null;
				} else {
					int nameCol = getColumnModel().getColumnIndex(COL_LABEL_NAME);
					return " " + getValueAt(row, nameCol) + " ";
				}
			}
			@Override
			public boolean isCellEditable(int row, int column) {
//				super.isCellEditable(row, column);
				// �I�[�o�[���C�h�ŃZ���ҏW��s�Ƃ���
				return false;
			}
		};
		resultTable.addMouseListener(new TblMouseListener());
		resultSorter.setTableHeader(resultTable.getTableHeader());
		
		JPanel dbPanel = new JPanel();
		dbPanel.setLayout(new BorderLayout());
		resultPane = new JScrollPane(resultTable);
		resultPane.addMouseListener(new PaneMouseListener());
		
		resultTable.setRowSelectionAllowed(true);
		resultTable.setColumnSelectionAllowed(false);
		resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		String[] col2 = { COL_LABEL_NAME, COL_LABEL_SCORE, COL_LABEL_HIT,
				COL_LABEL_ID, COL_LABEL_ION, COL_LABEL_CONTRIBUTOR, COL_LABEL_NO };

		resultDm.setColumnIdentifiers(col2);
		(resultTable.getColumn(resultTable.getColumnName(0)))
				.setPreferredWidth(LEFT_PANEL_WIDTH - 180);
		(resultTable.getColumn(resultTable.getColumnName(1))).setPreferredWidth(70);
		(resultTable.getColumn(resultTable.getColumnName(2))).setPreferredWidth(20);
		(resultTable.getColumn(resultTable.getColumnName(3))).setPreferredWidth(70);
		(resultTable.getColumn(resultTable.getColumnName(4))).setPreferredWidth(20);
		(resultTable.getColumn(resultTable.getColumnName(5))).setPreferredWidth(70);
		(resultTable.getColumn(resultTable.getColumnName(6))).setPreferredWidth(50);

		ListSelectionModel lm2 = resultTable.getSelectionModel();
		lm2.addListSelectionListener(new LmResultListener());
		
		resultPane.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, 200));
		dbPanel.add(resultPane, BorderLayout.CENTER);
		
		
		// *********************************************************************
		// DB Query�^�u
		// *********************************************************************
		DefaultTableModel dbDm = new DefaultTableModel();
		querySorter = new TableSorter(dbDm, TABLE_QUERY_DB);
		queryDbTable = new JTable(querySorter) {
			@Override
			public boolean isCellEditable(int row, int column) {
//				super.isCellEditable(row, column);
				// �I�[�o�[���C�h�ŃZ���ҏW��s�Ƃ���
				return false;
			}
		};
		queryDbTable.addMouseListener(new TblMouseListener());
		querySorter.setTableHeader(queryDbTable.getTableHeader());
		queryDbPane = new JScrollPane(queryDbTable);
		queryDbPane.addMouseListener(new PaneMouseListener());
		
		int h = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		queryDbPane.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, h));
		queryDbTable.setRowSelectionAllowed(true);
		queryDbTable.setColumnSelectionAllowed(false);
		queryDbTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		String[] col3 = { COL_LABEL_ID, COL_LABEL_NAME, COL_LABEL_CONTRIBUTOR, COL_LABEL_NO };
		DefaultTableModel model = (DefaultTableModel) querySorter.getTableModel();
		model.setColumnIdentifiers(col3);

		// �񕝃Z�b�g
		queryDbTable.getColumn(queryDbTable.getColumnName(0))
				.setPreferredWidth(70);
		queryDbTable.getColumn(queryDbTable.getColumnName(1))
				.setPreferredWidth(LEFT_PANEL_WIDTH - 70);
		queryDbTable.getColumn(queryDbTable.getColumnName(2))
				.setPreferredWidth(70);
		queryDbTable.getColumn(queryDbTable.getColumnName(3))
				.setPreferredWidth(50);

		ListSelectionModel lm3 = queryDbTable.getSelectionModel();
		lm3.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lm3.addListSelectionListener(new LmQueryDbListener());
		
		// �{�^���p�l��
		JPanel btnPanel = new JPanel();
		btnName.addActionListener(new BtnSearchNameListener());
		btnAll.addActionListener(new BtnAllListener());
		btnPanel.add(btnName);
		btnPanel.add(btnAll);
		
		parentPanel2 = new JPanel();
		parentPanel2.setLayout(new BoxLayout(parentPanel2, BoxLayout.PAGE_AXIS));
		parentPanel2.add(btnPanel);
		parentPanel2.add(queryDbPane);
		
		// �I�v�V�����p�l��
		JPanel dispModePanel = new JPanel();
		isDispSelected = dispSelected.isSelected();
		isDispRelated = dispRelated.isSelected();
		if (isDispSelected) {
			resultTable.getSelectionModel().setSelectionMode(
					ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		}
		else if (isDispRelated) {
			resultTable.getSelectionModel().setSelectionMode(
					ListSelectionModel.SINGLE_SELECTION);
		}
		Object[] retRadio = new Object[]{dispSelected, dispRelated};
		for (int i=0; i<retRadio.length; i++) {
			((JRadioButton)retRadio[i]).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (isDispSelected != dispSelected.isSelected()
							|| isDispRelated != dispRelated.isSelected()) {
						
						isDispSelected = dispSelected.isSelected();
						isDispRelated = dispRelated.isSelected();
						
						// ���ʃ��R�[�h�I����Ԃ�����
						resultTable.clearSelection();
						resultPlot.clear();
						compPlot.setPeaks(null, 1);
						resultPlot.setPeaks(null, 0);
						setAllPlotAreaRange();
						pkgView.initResultRecInfo();
						
						if (isDispSelected) {
							resultTable.getSelectionModel().setSelectionMode(
									ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
						}
						else if (isDispRelated) {
							resultTable.getSelectionModel().setSelectionMode(
									ListSelectionModel.SINGLE_SELECTION);
						}
					}
				}
			});
		}
		ButtonGroup disGroup = new ButtonGroup();
		disGroup.add(dispSelected);
		disGroup.add(dispRelated);
		dispModePanel.add(lbl2);
		dispModePanel.add(dispSelected);
		dispModePanel.add(dispRelated);
		
		
		JPanel paramPanel = new JPanel();
		paramPanel.add(etcPropertyButton);
		etcPropertyButton.setMargin(new Insets(0, 10, 0, 10));
		etcPropertyButton.addActionListener(new ActionListener() {
			private ParameterSetWindow ps = null;
			public void actionPerformed(ActionEvent e) {
				// �q��ʂ��J���Ă��Ȃ���ΐ���
				if (!isSubWindow) {
					ps = new ParameterSetWindow();
				} else {
					ps.requestFocus();
				}
			}
		});
		
		JPanel optionPanel = new JPanel();
		optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
		optionPanel.add(dispModePanel);
		optionPanel.add(paramPanel);
		
		// PackageView�����y�сA������
		pkgView = new PackageViewPanel();
		pkgView.initAllRecInfo();
		
		queryTabPane.addTab("DB", parentPanel2);
		queryTabPane.setToolTipTextAt(TAB_ORDER_DB, "Query from DB.");
		queryTabPane.addTab("File", queryFilePane);
		queryTabPane.setToolTipTextAt(TAB_ORDER_FILE, "Query from user file.");
		queryTabPane.setSelectedIndex(TAB_ORDER_DB);
		queryTabPane.setFocusable(false);
		queryTabPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {

				// �v���b�g�y�C��������
				queryPlot.clear();
				compPlot.clear();
				resultPlot.clear();
				queryPlot.setPeaks(null, 0);
				compPlot.setPeaks(null, 1);
				resultPlot.setPeaks(null, 0);

				// PackageView������
				pkgView.initAllRecInfo();
				
				// DB Hit�^�u�֘A������
				if (resultTabPane.getTabCount() > 0) {
					resultTabPane.setSelectedIndex(0);
				}
				DefaultTableModel dataModel = (DefaultTableModel) resultSorter
						.getTableModel();
				dataModel.setRowCount(0);
				hitLabel.setText(" ");

				// DB�^�u�AUser File�^�u�̑I���ς݃��R�[�h���f����
				queryTabPane.update(queryTabPane.getGraphics());
				if (queryTabPane.getSelectedIndex() == TAB_ORDER_DB) {
					parentPanel2.update(parentPanel2.getGraphics());
					updateSelectQueryTable(queryDbTable);
				} else if (queryTabPane.getSelectedIndex() == TAB_ORDER_FILE) {
					queryFilePane.update(queryFilePane.getGraphics());
					updateSelectQueryTable(queryFileTable);
				}
			}
		});
		
		
		// ���C�A�E�g		
		JPanel queryPanel = new JPanel();
		queryPanel.setLayout(new BorderLayout());
		queryPanel.add(queryTabPane, BorderLayout.CENTER);
		queryPanel.add(optionPanel, BorderLayout.SOUTH);
		queryPanel.setMinimumSize(new Dimension(0, 170));

		JPanel jtp2Panel = new JPanel();
		jtp2Panel.setLayout(new BorderLayout());
		jtp2Panel.add(dbPanel, BorderLayout.CENTER);
		jtp2Panel.add(hitLabel, BorderLayout.SOUTH);
		jtp2Panel.setMinimumSize(new Dimension(0, 70));
		Color colorGreen = new Color(0, 128, 0);
		hitLabel.setForeground(colorGreen);

		resultTabPane.addTab("Result", jtp2Panel);
		resultTabPane.setToolTipTextAt(TAB_RESULT_DB, "Result of DB hit.");
		resultTabPane.setFocusable(false);
		
		queryPlot.setMinimumSize(new Dimension(0, 100));
		compPlot.setMinimumSize(new Dimension(0, 120));
		resultPlot.setMinimumSize(new Dimension(0, 100));
		int height = initAppletHight / 3;
		JSplitPane jsp_cmp2db = new JSplitPane(JSplitPane.VERTICAL_SPLIT, compPlot, resultPlot);
		JSplitPane jsp_qry2cmp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, queryPlot,
				jsp_cmp2db);
		jsp_cmp2db.setDividerLocation(height);
		jsp_qry2cmp.setDividerLocation(height - 25);
		jsp_qry2cmp.setMinimumSize(new Dimension(190, 0));
		
		viewTabPane.addTab("Compare View", jsp_qry2cmp);
		viewTabPane.addTab("Package View", pkgView);
		viewTabPane.setToolTipTextAt(TAB_VIEW_COMPARE, "Comparison of query and result spectrum.");
		viewTabPane.setToolTipTextAt(TAB_VIEW_PACKAGE, "Package comparison of query and result spectrum.");
		viewTabPane.setSelectedIndex(TAB_VIEW_COMPARE);
		viewTabPane.setFocusable(false);
		
		JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, queryPanel,
				resultTabPane);
		jsp.setDividerLocation(310);
		jsp.setMinimumSize(new Dimension(180, 0));
		jsp.setOneTouchExpandable(true);
		
		JSplitPane jsp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jsp,
				viewTabPane);
		int divideSize = (int)(initAppletWidth * 0.4);
		divideSize = (divideSize >= 180) ? divideSize : 180;
		jsp2.setDividerLocation(divideSize);
		jsp2.setOneTouchExpandable(true);
		
		mainPanel.add(jsp2, BorderLayout.CENTER);
		add(mainPanel);

		queryPlot.setSearchPage(this);
		compPlot.setSearchPage(this);
		resultPlot.setSearchPage(this);
	}

	/**
	 * �t�@�C���ǂݍ��ݏ���
	 * @param fileName �t�@�C����
	 */
	private void loadFile(String fileName) {
		seaqCompound = 0;
		seaqId = 0;
		String reqUrl = baseUrl + "jsp/SearchPage.jsp?file=" + fileName;
		ArrayList<String> lineList = new ArrayList<String>();
		
		try {
			
			URL url = new URL(reqUrl);
			URLConnection con = url.openConnection();

			// ���X�|���X�擾
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = "";
			while ((line = in.readLine()) != null) {
				lineList.add(line);
			}
			in.close();
			
			// 1�s���ǂݍ��߂Ȃ������ꍇ
			if (lineList.size() == 0) {
				// ERROR�F�t�@�C��������܂���
				JOptionPane.showMessageDialog(null, "No file.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		catch (MalformedURLException mue) {			// URL��������
			mue.printStackTrace();
			// ERROR�F�T�[�o�[�G���[
			JOptionPane.showMessageDialog(null, "Server error.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch (IOException ie) {					// ���o�͗�O
			ie.printStackTrace();
			// ERROR�F�T�[�o�[�G���[
			JOptionPane.showMessageDialog(null, "Server error.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		DefaultTableModel dataModel = (DefaultTableModel) fileSorter.getTableModel();
		dataModel.setRowCount(0);

		Object[] row;
		String line = "";
		String peaksLine = "";
		UserFileData usrData = null;
		Vector<UserFileData>tmpUserDataList = new Vector<UserFileData>();
		int dataNum = 0;
		try {
			for (int i=0; i<lineList.size(); i++) {
				
				line = lineList.get(i);
				
				// �R�����g�s�ǂݔ�΂�
				if (line.trim().startsWith("//")) {
					continue;
				}
				
				// ���R�[�h���擾����
				if (line.trim().indexOf(":") == -1 && line.trim().length() != 0) {
					if (usrData == null) {
						usrData = new UserFileData();
					}
					if (line.lastIndexOf(";") != -1) {
						peaksLine += line.trim();
					}
					else {
						peaksLine += line.trim() + ";";
					}
				}
				else if (line.trim().startsWith("Name:")) {
					if (usrData == null) {
						usrData = new UserFileData();
					}
					usrData.setName(line.substring(5).trim());
				}
				else if (line.trim().startsWith("ID:")) {
					if (usrData == null) {
						usrData = new UserFileData();
					}
					usrData.setId(line.substring(3).trim());
				}
				
				// ���R�[�h���ǉ�����
				if (line.trim().length() == 0 || i == lineList.size()-1) {
					
					if (usrData != null) {
						
						dataNum++;
						
						// === ID ===
						if (usrData.getId().equals("")) {
							usrData.setId(createId());
						}
						
						// === �������� ===
						if (usrData.getName().equals("")) {
							usrData.setName(createName());
						}
						
						if (peaksLine.length() != 0) {
							
							// �s�[�N�����H(m/z������m/z�Ƌ��x�̑g�ݍ��킹)
							double max = 0d;
							ArrayList<String> peakList = new ArrayList<String>(Arrays.asList(peaksLine.split(";")));
							for (int j = 0; j < peakList.size(); j++) {
								peakList.set(j, peakList.get(j).replaceAll("^ +", ""));
								peakList.set(j, peakList.get(j).replaceAll(" +", "\t"));
								
								// �ő勭�x�ێ�
								if (max < Double.parseDouble(peakList.get(j).split("\t")[1])) {
									max = Double.parseDouble(peakList.get(j).split("\t")[1]);
								}
							}
							Collections.sort(peakList, new PeakComparator());
							
							// �����I�ɋ��x�𑊑΋��x�ɕϊ�
							for (int j = 0; j < peakList.size(); j++) {
								
								// m/z�ޔ�
								String tmpMz = peakList.get(j).split("\t")[0];
								
								// ���̋��x
								String beforeVal = peakList.get(j).split("\t")[1];
								
								// ���΋��x
								long tmpVal = Math.round(Double.parseDouble(beforeVal) / max * 999d);
								if (tmpVal > 999) { 
									tmpVal = 999;
								}
								if (tmpVal < 1) {
									tmpVal = 1;
								}
								String afterVal = String.valueOf(tmpVal);
								
								peakList.set(j, tmpMz + "\t" + afterVal);
							}
							usrData.setPeaks((String[])peakList.toArray(new String[peakList.size()]));
							
						}
						
						// ���[�U�f�[�^���ǉ�
						tmpUserDataList.add(usrData);
						
						// �e�[�u�����ǉ�
						row = new Object[3];
						row[0] = String.valueOf(dataNum);
						row[1] = usrData.getName();
						row[2] = usrData.getId();
						dataModel.addRow(row);
					}
					
					usrData = null;
					peaksLine = "";
				}
			}
		}
		catch (Exception e) {
			System.out.println("Illegal file format.");
			e.printStackTrace();
			// WARNING�F�t�@�C���t�H�[�}�b�g���s���ł�
			JOptionPane.showMessageDialog(null, "Illegal file format.", "Warning",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		userDataList = (UserFileData[])tmpUserDataList.toArray(new UserFileData[tmpUserDataList.size()]);
		queryTabPane.setSelectedIndex(TAB_ORDER_FILE);
	}

	/**
	 * ID����
	 * ID�������������ԋp����
	 * @return ��������
	 */
	private String createId() {
		String tmpId = "US";
		
		synchronized (this) {
			if (seaqId < Integer.MAX_VALUE) {
				seaqId++;
			} else {
				seaqId = 0;
			}
		}
		DecimalFormat df = new DecimalFormat("000000");
		return tmpId + df.format(seaqId);
	}
	
	/**
	 * ������������
	 * ���������������������ԋp����
	 * @return ��������
	 */
	private String createName() {
		String tmpName = "Compound_";

		synchronized (this) {
			if (seaqCompound < Integer.MAX_VALUE) {
				seaqCompound++;
			} else {
				seaqCompound = 0;
			}
		}
		DecimalFormat df = new DecimalFormat("000000");
		return tmpName + df.format(seaqCompound);
	}

	/**
	 * DB����
	 * �N�G���[�Ƀq�b�g����s�[�N�̃X�y�N�g����DB���猟������B
	 * @param ps �s�[�N���
	 * @param precursor �v���J�[�T�[
	 * @param queryName �N�G���[��������
	 * @param queryKey �N�G���[���R�[�h�L�[
	 */
	private void searchDb(String[] ps, String precursor, String queryName, String queryKey) {
		queryPlot.clear();
		compPlot.clear();
		resultPlot.clear();
		queryPlot.setPeaks(null, 0);
		compPlot.setPeaks(null, 1);
		resultPlot.setPeaks(null, 0);
		DefaultTableModel dataModel = (DefaultTableModel)resultSorter.getTableModel();
		dataModel.setRowCount(0);
		hitLabel.setText("");

		if (queryTabPane.getSelectedIndex() == TAB_ORDER_DB) {
			queryPlot.setSpectrumInfo(queryName, queryKey, precursor, PeakPanel.SP_TYPE_QUERY, false);	
		} else if (queryTabPane.getSelectedIndex() == TAB_ORDER_FILE) {
			queryPlot.setSpectrumInfo(queryName, queryKey, precursor, PeakPanel.SP_TYPE_QUERY, true);
		}
		
		// �N�G�����X�y�N�g���̃s�[�N���Ȃ��ꍇ
		if (ps.length == 0 || (ps.length == 1 && ps[0].split("\t")[0].equals("0") && ps[0].split("\t")[1].equals("0"))) {
			queryPlot.setNoPeak(true);
			hitLabel.setText(" 0 Hit.    ("
					+ ((PRECURSOR < 1) ? "" : "Precursor : " + PRECURSOR + ", ")
					+ "Tolerance : "
					+ TOLERANCE
					+ " "
					+ ((tolUnit1.isSelected()) ? tolUnit1.getText() : tolUnit2.getText()) + ", Cutoff threshold : "
					+ CUTOFF_THRESHOLD + ")");
			// �}�E�X�J�[�\�����f�t�H���g�J�[�\����
			this.setCursor(Cursor.getDefaultCursor());
			return;
		}

		// POST�f�[�^���쐬
		StringBuffer post = new StringBuffer();
		if (isRecInteg)
			post.append( "INTEG=true&" );
		else if (isRecActu)
			post.append( "INTEG=false&" );
		if (PRECURSOR > 0) {
			post.append( "PRE=" + PRECURSOR + "&");
		}
		post.append( "CUTOFF=" + CUTOFF_THRESHOLD + "&" );
		post.append( "TOLERANCE=" + TOLERANCE + "&" );
		if (tolUnit2.isSelected())
			post.append( "TOLUNIT=ppm&" );
		else
			post.append( "TOLUNIT=unit&" );
		post.append( "INST=" );
		StringBuffer instTmp = new StringBuffer();
		boolean isInstAll = true;
		for (Iterator i=isInstCheck.keySet().iterator(); i.hasNext(); ) {
			String key = (String)i.next();
			
			if ( (isInstCheck.get(key)) ) {
				if (instTmp.length() > 0) {
					instTmp.append( "," );
				}
				instTmp.append( key );
			} else {
				isInstAll = false;
			}
		}
		if (isInstAll) {
			if (instTmp.length() > 0) {
				instTmp.append( "," );
			}
			instTmp.append( "ALL" );
		}
		post.append( instTmp.toString() + "&" );
		
		post.append( "MS=" );
		StringBuffer msTmp = new StringBuffer();
		boolean isMsAll = true;
		for (Iterator i=isMsCheck.keySet().iterator(); i.hasNext(); ) {
			String key = (String)i.next();
			
			if ( (isMsCheck.get(key)) ) {
				if (msTmp.length() > 0) {
					msTmp.append( "," );
				}
				msTmp.append( key );
			} else {
				isMsAll = false;
			}
		}
		if (isMsAll) {
			if (msTmp.length() > 0) {
				msTmp.append( "," );
			}
			msTmp.append( "ALL" );
		}
		post.append( msTmp.toString() + "&" );
		
		
		if (isIonRadio.get("Posi")) {
			post.append( "ION=1&" );
		} else if (isIonRadio.get("Nega")) {
			post.append( "ION=-1&" );
		} else {
			post.append( "ION=0&");
		}
		
		post.append( "VAL=" );
		for (int i = 0; i < ps.length; i++) {
			post.append( ps[i].replace("\t", ",") + "@" );
		}

		// ��ʑ���𖳌�����
		setOperationEnbled(false);

		// �������_�C�A���O�\������
		dlg.setVisible(true);

		this.param = post.toString();
		this.ps = ps;
		SwingWorker worker = new SwingWorker() {
			private ArrayList<String> result = null;

			public Object construct() {
				// �T�[�u���b�g�Ăяo��-�}���`�X���b�h��CGI���N��
				String cgiType = MassBankCommon.CGI_TBL[MassBankCommon.CGI_TBL_NUM_TYPE][MassBankCommon.CGI_TBL_TYPE_SEARCH];
				result = mbcommon.execMultiDispatcher(baseUrl, cgiType, SearchPage.this.param);
				return null;
			}

			public void finished() {
				// ��ʑ��얳������������
				setOperationEnbled(true);

				// �������_�C�A���O���\���ɂ���
				dlg.setVisible(false);

				int total = 0;
				if (result != null && result.size() > 0) {
					total = result.size();
					DefaultTableModel dataModel = (DefaultTableModel)resultSorter.getTableModel();

					// �������ʂ�DBTable�ɃZ�b�g
					siteList = new String[total];
					for (int i = 0; i < total; i++) {
						String line = (String) result.get(i);
						String[] item = line.split("\t");
						String id = item[0];
						String name = item[1];

						// Score, Hit
						String score = "";
						String hit = "";
						String hitScore = item[2];
						int pos = hitScore.indexOf(".");
						if (pos > 0) {
							score = "0" + hitScore.substring(pos);
							hit = hitScore.substring(0, pos);
						} else {
							score = "0";
							hit = hitScore;
						}
						Double dblScore = Double.parseDouble(score);
						Integer ihit = Integer.parseInt(hit);

						// Ion
						int iIon = Integer.parseInt(item[3]);
						String ion = "";
						if (iIon > 0) {
							ion = "P";
						} else if (iIon < 0) {
							ion = "N";
						} else {
							ion = "-";
						}

						// SiteName
						String siteName = siteNameList[Integer.parseInt(item[4])];
						siteList[i] = item[4];

						// Name, Score, Hit, ID, Ion, SiteName, No.
						Object[] rowData = { name, dblScore, ihit, id, ion, siteName, (i + 1) };
						dataModel.addRow(rowData);
					}
				}

				PeakData peak = new PeakData(SearchPage.this.ps);
				queryPlot.setPeaks(peak, 0);
				compPlot.setPeaks(peak, 0);
				resultTabPane.setSelectedIndex(0);
				setAllPlotAreaRange(queryPlot);
				SearchPage.this.setCursor(Cursor.getDefaultCursor());
				hitLabel.setText(" "
						+ total
						+ " Hit.    ("
						+ ((PRECURSOR < 1) ? "" : "Precursor : " + PRECURSOR + ", ")
						+ "Tolerance : "
						+ TOLERANCE
						+ " "
						+ ((tolUnit1.isSelected()) ? tolUnit1.getText()
								: tolUnit2.getText()) + ", Cutoff threshold : "
						+ CUTOFF_THRESHOLD + ")");
				hitLabel.setToolTipText(" "
						+ total
						+ " Hit.    ("
						+ ((PRECURSOR < 1) ? "" : "Precursor : " + PRECURSOR + ", ")
						+ "Tolerance : "
						+ TOLERANCE
						+ " "
						+ ((tolUnit1.isSelected()) ? tolUnit1.getText()
								: tolUnit2.getText()) + ", Cutoff threshold : "
						+ CUTOFF_THRESHOLD + ")");
			}
		};
		worker.start();
	}
	
	/**
	 * �N�G���[�̑I����Ԃ��X�V
	 */
	private void updateSelectQueryTable(JTable tbl) {
		
		// �}�E�X�J�[�\���������v��
		this.setCursor(waitCursor);
		
		int selRow = tbl.getSelectedRow();
		if (selRow >= 0) {
			tbl.clearSelection();
			Color defColor = tbl.getSelectionBackground();
			tbl.setRowSelectionInterval(selRow, selRow);
			tbl.setSelectionBackground(Color.PINK);
			tbl.update(tbl.getGraphics());
			tbl.setSelectionBackground(defColor);
		}
		// �}�E�X�J�[�\�����f�t�H���g�J�[�\����
		this.setCursor(Cursor.getDefaultCursor());
	}

	/**
	 * �X�y�N�g���擾
	 * DB����X�y�N�g�����擾����
	 * @param searchName 
	 */
	private void getSpectrumForQuery(String searchName) {
		
		String param = "";
		if (!searchName.equals("")) {
			String wc = "&wc=";
			boolean wcStart = false;
			boolean wcEnd = false;
			if (searchName.substring(0, 1).equals("*")) {
				wcStart = true;
			}
			if (searchName.substring(searchName.length() - 1).equals("*")) {
				wcEnd = true;
			}

			if (wcStart) {
				if (wcEnd) {
					wc += "both";
				} else {
					wc += "start";
				}
			} else {
				if (wcEnd) {
					wc += "end";
				} else {
					wc = "";
				}
			}
			searchName = searchName.replace("*", "");
			param = "name=" + searchName + wc;
		}

		// �T�[�u���b�g�Ăяo��-�}���`�X���b�h��CGI���N��
		String cgiType = MassBankCommon.CGI_TBL[MassBankCommon.CGI_TBL_NUM_TYPE][MassBankCommon.CGI_TBL_TYPE_GNAME];
		ArrayList<String> result = mbcommon.execMultiDispatcher(baseUrl, cgiType, param);
		DefaultTableModel dataModel = (DefaultTableModel) querySorter.getTableModel();
		dataModel.setRowCount(0);
		if (result == null || result.size() == 0) {
			return;
		}

		// �\�[�g
		Collections.sort(result);

		nameList.clear();
		for (int i = 0; i < result.size(); i++) {
			String nameId = (String) result.get(i);
			String[] cutNameId = nameId.split("\t");
			String name = cutNameId[0];

			String id = cutNameId[1];
			String site = cutNameId[2];

			String[] cutIdNameSite = new String[] { id, name, site };
			nameList.add(cutIdNameSite);

			site = siteNameList[Integer.parseInt(site)];
			String[] idNameSite2 = new String[] { id, name, site, String.valueOf(i + 1) };

			// �擾�l���e�[�u���ɃZ�b�g
			dataModel.addRow(idNameSite2);
		}
	}
	
	/**
	 * ���R�[�h�y�[�W�\��
	 * @param selectIndex �I���s�C���f�b�N�X
	 */
	private void showRecordPage(JTable eventTbl) {
		int selRows[] = eventTbl.getSelectedRows();
		int idCol = eventTbl.getColumnModel().getColumnIndex(COL_LABEL_ID);
		int siteCol = eventTbl.getColumnModel().getColumnIndex(COL_LABEL_CONTRIBUTOR);
		
		// �I�����ꂽ�s�̒l(id)���擾
		String id = (String)eventTbl.getValueAt(selRows[0], idCol);
		String siteName = (String)eventTbl.getValueAt(selRows[0], siteCol);
		String site = "0";
		for (int i=0; i<siteNameList.length; i++) {
			if (siteName.equals(siteNameList[i])) {
				site = Integer.toString(i);
				break;
			}
		}

		// CGI�Ăяo��
		String typeName = MassBankCommon.CGI_TBL[MassBankCommon.CGI_TBL_NUM_TYPE][MassBankCommon.CGI_TBL_TYPE_DISP];
		String reqUrl = baseUrl + "jsp/" + MassBankCommon.DISPATCHER_NAME
				+ "?type=" + typeName + "&id=" + id + "&site=" + site;
		try {
			context.showDocument(new URL(reqUrl), "_blank");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * ���R�[�h���X�g�p�|�b�v�A�b�v�\��
	 * @param e �}�E�X�C�x���g
	 */
	private void recListPopup(MouseEvent e) {
		JTable tbl = null;
		JScrollPane pane = null;
		try {
			tbl = (JTable)e.getSource();
		}
		catch (ClassCastException cce) {
			pane = (JScrollPane)e.getSource();
			if (pane.equals(queryDbPane)) {
				tbl = queryDbTable;
			}
			else if (pane.equals(resultPane)) {
				tbl = resultTable;
			}
			if (pane.equals(queryFilePane)) {
				tbl = queryFileTable;
			}
		}
		int rowCnt = tbl.getSelectedRows().length;
		
		JMenuItem item1 = new JMenuItem("Show Record");
		item1.addActionListener(new PopupShowRecordListener(tbl));
		JMenuItem item2 = new JMenuItem("Multiple Display");
		item2.addActionListener(new PopupMultipleDisplayListener(tbl));
		
		// ���ݒ�
		if (tbl.equals(queryFileTable)) {
			item1.setEnabled(false);
			item2.setEnabled(false);
		}
		else if (rowCnt == 0) {
			item1.setEnabled(false);
			item2.setEnabled(false);
		}
		else if (rowCnt == 1) {
			item1.setEnabled(true);
			item2.setEnabled(false);
		}
		else if (rowCnt > 1) {
			item1.setEnabled(false);
			item2.setEnabled(true);
		}
		
		// �|�b�v�A�b�v���j���[�\��
		JPopupMenu popup = new JPopupMenu();
		popup.add(item1);
		if (tbl.equals(resultTable)) {
			popup.add(item2);
		}
		popup.show(e.getComponent(), e.getX(), e.getY());
	}
	
	/**
	 * Precursor m/z��񏉊���
	 */
	private void initPreInfo() {
//		// Cookie���p���X�g��Cookie����Precursor��Ԃ��擾
//		ArrayList<String> valueList = cm.getCookie(COOKIE_PRE);
//		
//		// Cookie�����݂���ꍇ
//		if (valueList.size() != 0) {
//			try {
//				PRECURSOR = Integer.valueOf(valueList.get(0));
//			} catch (Exception e) {
//				// PRECURSOR�̓f�t�H���g�l���g�p
//			}
//		} else {
			PRECURSOR = -1;
//			valueList.add(String.valueOf(PRECURSOR));
//			cm.setCookie(COOKIE_PRE, valueList);
//		}
	}
	
	/**
	 * Tolerance��񏉊���
	 */
	private void initTolInfo() {
		// Cookie���p���X�g��Cookie����Tolerance��Ԃ��擾
		ArrayList<String> valueList = cm.getCookie(COOKIE_TOL);
		
		// Cookie�����݂���ꍇ
		if (valueList.size() != 0) {
			try {
				TOLERANCE = Float.valueOf(valueList.get(0));
			} catch (Exception e) {
				// TOLERANCE�̓f�t�H���g�l���g�p
			}
			
			if (valueList.contains(tolUnit2.getText())) {
				tolUnit1.setSelected(false);
				tolUnit2.setSelected(true);
			} else {
				tolUnit1.setSelected(true);
				tolUnit2.setSelected(false);
			}
		} else {
			TOLERANCE = 0.3f;
			valueList.add(String.valueOf(TOLERANCE));
			if (tolUnit1.isSelected()) {
				valueList.add(tolUnit1.getText());	
			}
			else {
				valueList.add(tolUnit2.getText());
			}
			cm.setCookie(COOKIE_TOL, valueList);
		}
	}
	
	/**
	 * Cutoff Threshold��񏉊���
	 */
	private void initCutoffInfo() {
		// Cookie���p���X�g��Cookie����Cutoff Threshold��Ԃ��擾
		ArrayList<String> valueList = cm.getCookie(COOKIE_CUTOFF);
		
		// Cookie�����݂���ꍇ
		if (valueList.size() != 0) {
			try {
				CUTOFF_THRESHOLD = Integer.valueOf(valueList.get(0));
			} catch (Exception e) {
				// CUTOFF_THRESHOLD�̓f�t�H���g�l���g�p
			}
		} else {
			CUTOFF_THRESHOLD = 5;
			valueList.add(String.valueOf(CUTOFF_THRESHOLD));
			cm.setCookie(COOKIE_CUTOFF, valueList);
		}
	}
	
	/**
	 * ���u��ʏ�񏉊���
	 * �f�[�^�x�[�X����S���u��ʏ����擾���đΉ�����`�F�b�N�{�b�N�X�𐶐�
	 * �`�F�b�N�{�b�N�X�̑I����Ԃ�[Cookie���]��[�f�t�H���g]�̗D�揇�őI����Ԃ�����������
	 * �`�F�b�N�{�b�N�X��1���I������Ȃ��ꍇ�͋����I�ɑS�đI��������Ԃŏ���������
	 */
	private void initInstInfo() {
		instCheck = new LinkedHashMap<String, JCheckBox>();
		isInstCheck = new HashMap<String, Boolean>();
		instGroup = instInfo.getTypeGroup();
		
		// Cookie���p���X�g��Cookie���瑕�u��ʂ̑I����Ԃ��擾
		ArrayList<String> valueGetList = cm.getCookie(COOKIE_INST);
		ArrayList<String> valueSetList = new ArrayList<String>();
		
		boolean checked = false;
		
		for (Iterator i=instGroup.keySet().iterator(); i.hasNext(); ) {
			String key = (String)i.next();
			
			List<String> list = instGroup.get(key);
			for ( int j = 0; j < list.size(); j++ ) {
				String val = list.get(j);
			
				JCheckBox chkBox;
				
				// Cookie�����݂���ꍇ
				if (valueGetList.size() != 0) {
					if (valueGetList.contains(val)) {
						chkBox = new JCheckBox(val, true);
						checked = true;
					} else {
						chkBox = new JCheckBox(val, false);
					}
				} else {
					if ( isDefaultInst(val) ) {	// �f�t�H���g���u��ʂ̏ꍇ 
						chkBox = new JCheckBox(val, true);
						checked = true;
						valueSetList.add(val);
					} else {
						chkBox = new JCheckBox(val, false);
					}
				}
				
				instCheck.put(val, chkBox);
				isInstCheck.put(val, chkBox.isSelected());
			}
		}
		
		// ���u��ʂ��f�[�^�x�[�X�ɓo�^����Ă��Ȃ��ꍇ
		if (instCheck.size() == 0 && isInstCheck.size() == 0) {
			JOptionPane.showMessageDialog(null,
					"Instrument Type is not registered in the database.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// �����܂ł̏����ő��u��ʂ�1���I������Ă��Ȃ��ꍇ�͋����I�ɑS�đI������
		if ( !checked ) {
			for (Iterator i=instCheck.keySet().iterator(); i.hasNext();) {
				String key = (String)i.next();
				
				((JCheckBox)instCheck.get(key)).setSelected(true);
				isInstCheck.put(key, true);
				valueSetList.add(key);
			}
		}
		
		// ����ǂݍ��ݎ���Cookie��񂪑��݂��Ȃ��ꍇ��Cookie�ɐݒ�
		if (valueGetList.size() == 0) {
			cm.setCookie(COOKIE_INST, valueSetList);
		}
	}
	
	/**
	 * MS��ʏ�񏉊���
	 * �f�[�^�x�[�X����SMS��ʏ����擾���đΉ�����`�F�b�N�{�b�N�X�𐶐�
	 * �`�F�b�N�{�b�N�X�̑I����Ԃ�[Cookie���]��[�f�t�H���g]�̗D�揇�őI����Ԃ�����������
	 * �`�F�b�N�{�b�N�X��1���I������Ȃ��ꍇ�͋����I�ɑS�đI��������Ԃŏ���������
	 */
	private void initMsInfo() {
		msCheck = new LinkedHashMap<String, JCheckBox>();
		isMsCheck = new HashMap<String, Boolean>();
		
		// Cookie���p���X�g��Cookie����MS��ʂ̑I����Ԃ��擾
		ArrayList<String> valueGetList = cm.getCookie(COOKIE_MS);
		ArrayList<String> valueSetList = new ArrayList<String>();
		
		boolean checked = false;
		
		List<String> list = Arrays.asList(instInfo.getMsAll());
		for ( int j=0; j<list.size(); j++ ) {
			String val = list.get(j);
		
			JCheckBox chkBox;
			
			// Cookie�����݂���ꍇ
			if (valueGetList.size() != 0) {
				if (valueGetList.contains(val)) {
					chkBox = new JCheckBox(val, true);
					checked = true;
				} else {
					chkBox = new JCheckBox(val, false);
				}
			} else {
				chkBox = new JCheckBox(val, true);
				checked = true;
				valueSetList.add(val);
			}
			
			msCheck.put(val, chkBox);
			isMsCheck.put(val, chkBox.isSelected());
		}
		
		// MS��ʂ��f�[�^�x�[�X�ɓo�^����Ă��Ȃ��ꍇ
		if (msCheck.size() == 0 && isMsCheck.size() == 0) {
			JOptionPane.showMessageDialog(null,
					"MS Type is not registered in the database.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// �����܂ł̏�����MS��ʂ�1���I������Ă��Ȃ��ꍇ�͋����I�ɑS�đI������
		if ( !checked ) {
			for (Iterator i=msCheck.keySet().iterator(); i.hasNext();) {
				String key = (String)i.next();
				((JCheckBox)msCheck.get(key)).setSelected(true);
				isMsCheck.put(key, true);
				valueSetList.add(key);
			}
		}
		
		// ����ǂݍ��ݎ���Cookie��񂪑��݂��Ȃ��ꍇ��Cookie�ɐݒ�
		if (valueGetList.size() == 0) {
			cm.setCookie(COOKIE_MS, valueSetList);
		}
	}
	
	/**
	 * �C�I����ʏ�񏉊���
	 */
	private void initIonInfo() {
		final String keyPosi = "Posi";
		final String keyNega = "Nega";
		final String keyBoth = "Both";
		
		ionRadio = new LinkedHashMap<String, JRadioButton>();
		isIonRadio = new HashMap<String, Boolean>();
		
		// Cookie���p���X�g��Cookie����C�I����ʂ̑I����Ԃ��擾
		ArrayList<String> valueList = cm.getCookie(COOKIE_ION);
		
		JRadioButton ionPosi = new JRadioButton("Positive");
		JRadioButton ionNega = new JRadioButton("Negative");
		JRadioButton ionBoth = new JRadioButton("Both");
		
		// Cookie�����݂���ꍇ
		if (valueList.size() != 0) {
			ionPosi.setSelected(valueList.contains(keyPosi));
			ionNega.setSelected(valueList.contains(keyNega));
			ionBoth.setSelected(valueList.contains(keyBoth));
		}
		else {
			ionPosi.setSelected(true);
			ionNega.setSelected(false);
			ionBoth.setSelected(false);
			valueList.add(keyPosi);
			cm.setCookie(COOKIE_ION, valueList);
		}
		
		ionRadio.put(keyPosi, ionPosi);
		ionRadio.put(keyNega, ionNega);
		ionRadio.put(keyBoth, ionBoth);
		isIonRadio.put(keyPosi, ionPosi.isSelected());
		isIonRadio.put(keyNega, ionNega.isSelected());
		isIonRadio.put(keyBoth, ionBoth.isSelected());
	}

	/**
	 * �f�t�H���g���u��ʃ`�F�b�N
	 * ���u��ʂ�"ESI"�A"APPI"�A"MALDI"���܂ނ��̂��f�t�H���g���u��ʂƂ���
	 * @param inst ���u���
	 */
	private boolean isDefaultInst(String inst) {
		
		if ( inst.indexOf("ESI") != -1 ||
			 inst.indexOf("APPI") != -1 ||
			 inst.indexOf("MALDI") != -1 ) {
			
			return true;
		}
		return false;
	}
	
	/**
	 * �A�v���b�g�̃t���[�����擾
	 */
	protected Frame getFrame() {
		for (Container p = getParent(); p != null; p = p.getParent()) {
			if (p instanceof Frame) return (Frame)p;
		}
		return null;
	}

	/**
	 * ��ʑ���L���E�����ݒ�
	 */
	private void setOperationEnbled(boolean value) {
		queryFileTable.setEnabled(value);
		queryDbTable.setEnabled(value);
		etcPropertyButton.setEnabled(value);
		btnName.setEnabled(value);
		btnAll.setEnabled(value);
		dispSelected.setEnabled(value);
		dispRelated.setEnabled(value);
		queryTabPane.setEnabled(value);
		resultTabPane.setEnabled(value);
		viewTabPane.setEnabled(value);
		lbl2.setEnabled(value);
	}

	/**
	 * ParameterSetWindow�N���X
	 */
	class ParameterSetWindow extends JFrame {
		
		private final int LABEL_SIZE_L = 0;
		private final int LABEL_SIZE_M = 1;
		private final int LABEL_SIZE_S = 2;
		private final JTextField preField;
		private final JTextField tolField;
		private final JTextField cutoffField;
		private boolean isTolUnit1 = tolUnit1.isSelected();
		private boolean isTolUnit2 = tolUnit2.isSelected();
		
		/**
		 * �R���X�g���N�^
		 */
		public ParameterSetWindow() {
			
			// �E�B���h�E�T�C�Y�Œ�
			setResizable(false);
			
			// �L�[���X�i�[�o�^�p�R���|�[�l���g���X�g
			ArrayList<Component> keyListenerList = new ArrayList<Component>();
			keyListenerList.add(this);
			
			// ���C���R���e�i�[�擾
			Container container= getContentPane();
			initMainContainer(container);
			
			JPanel delimPanel;
			JPanel labelPanel;
			JPanel itemPanel;
			
			
			// Tolerance
			labelPanel = newLabelPanel("Tolerance of m/z", " Tolerance of m/z. ", LABEL_SIZE_L, 2);
			
			JPanel tolPanel = new JPanel();
			tolPanel.setLayout(new BoxLayout(tolPanel, BoxLayout.X_AXIS));
			tolField = new JTextField(String.valueOf(TOLERANCE), 5);
			tolField.setHorizontalAlignment(JTextField.RIGHT);
			keyListenerList.add(tolField);
			keyListenerList.add(tolUnit1);
			keyListenerList.add(tolUnit2);
			ButtonGroup tolGroup = new ButtonGroup();
			tolGroup.add(tolUnit1);
			tolGroup.add(tolUnit2);
			tolPanel.add(tolUnit1);
			tolPanel.add(tolUnit2);
			
			itemPanel = new JPanel();
			initItemPanel(itemPanel);
			itemPanel.add(wrappTextPanel(tolField), itemPanelGBC(0d, 0d, 0, 0, 1, 1));
			itemPanel.add(tolPanel, itemPanelGBC(0d, 0d, 1, 0, 1, 1));
			itemPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 2, 0, GridBagConstraints.REMAINDER, 1));
			
			delimPanel = new JPanel();
			initDelimPanel(delimPanel, true);
			delimPanel.add(labelPanel, delimPanelGBC(0d, 0d, 0, 0, 1, 1));
			delimPanel.add(itemPanel, delimPanelGBC(0d, 0d, 1, 0, 1, 1));
			delimPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 2, 0, GridBagConstraints.REMAINDER, 1));
			
			container.add(delimPanel, mainContainerGBC(0, 0, 1, 1));
			
			
			
			// Cutoff Thresholds
			labelPanel = newLabelPanel("Cutoff Threshold", " Cutoff threshold of intensities. ", LABEL_SIZE_L, 2);
			
			cutoffField = new JTextField(String.valueOf(CUTOFF_THRESHOLD), 5);
			cutoffField.setHorizontalAlignment(JTextField.RIGHT);
			keyListenerList.add(cutoffField);
			
			itemPanel = new JPanel();
			initItemPanel(itemPanel);
			itemPanel.add(wrappTextPanel(cutoffField), itemPanelGBC(0d, 0d, 0, 0, 1, 1));
			itemPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 1, 0, GridBagConstraints.REMAINDER, 1));
			
			delimPanel = new JPanel();
			initDelimPanel(delimPanel, true);
			delimPanel.add(labelPanel, delimPanelGBC(0d, 0d, 0, 0, 1, 1));
			delimPanel.add(itemPanel, delimPanelGBC(0d, 0d, 1, 0, 1, 1));
			delimPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 2, 0, GridBagConstraints.REMAINDER, 1));
			
			container.add(delimPanel, mainContainerGBC(0, 1, 1, 1));
			
			
			
			// Instrument Type
			labelPanel = newLabelPanel("Instrument Type", " Instrument type. ", LABEL_SIZE_L, 2);
			
			final JCheckBox chkBoxInstAll = new JCheckBox("All");
			chkBoxInstAll.setSelected(isInstAll());
			final JCheckBox chkBoxInstDefault = new JCheckBox("Default");
			chkBoxInstDefault.setSelected(isInstDefault());
			keyListenerList.add(chkBoxInstAll);
			keyListenerList.add(chkBoxInstDefault);

			itemPanel = new JPanel();
			initItemPanel(itemPanel);
			itemPanel.add(chkBoxInstAll, itemPanelGBC(0d, 0d, 0, 0, 1, 1));
			itemPanel.add(new JPanel(), itemPanelGBC(0.1d, 0d, 1, 0, 1, 1));
			itemPanel.add(chkBoxInstDefault, itemPanelGBC(0d, 0d, 2, 0, 1, 1));
			itemPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 3, 0, GridBagConstraints.REMAINDER, 0));
			
			delimPanel = new JPanel();
			initDelimPanel(delimPanel, true);
			delimPanel.add(labelPanel, delimPanelGBC(0d, 0d, 0, 0, 1, 1));
			delimPanel.add(itemPanel, delimPanelGBC(0d, 0d, 1, 0, 1, 1));
			
			JPanel instPanel = new JPanel();
			initItemPanel(instPanel);
			
			int keyNum = 0;
			boolean isSep = false;
			for (Iterator i=instGroup.keySet().iterator(); i.hasNext();) {
				String key = (String)i.next();
				
				itemPanel = new JPanel();
				initItemPanel(itemPanel);
				
				
				// �����ޒP�ʂ̃R���|�[�l���g�ǉ�����
				List<String> list = instGroup.get(key);
				int valNum = 0;
				for ( int j = 0; j < list.size(); j++ ) {
					String val = list.get(j);
					
					// �Z�p���[�^�}��
					if (keyNum != 0 && valNum == 0) {
						itemPanel.add(new JSeparator(), itemPanelGBC(0d, 0d, 0, valNum, 3, 1));
						valNum += 1;
						isSep = true;
					}
					
					JCheckBox chkBox = (JCheckBox)instCheck.get(val);
					keyListenerList.add(chkBox);
					itemPanel.add(chkBox, itemPanelGBC(0d, 0d, 1, valNum, 1, 1));
					valNum += 1;
				}
				
				
				// �啪�ޒP�ʂ̃R���|�[�l���g�ǉ�����
				if (valNum > 0) {
					labelPanel = newLabelPanel(key, null, LABEL_SIZE_S, 2);
					if (isSep) {
						itemPanel.add(labelPanel, itemPanelGBC(0d, 0d, 0, 1, 1, 1));
					} else {
						itemPanel.add(labelPanel, itemPanelGBC(0d, 0d, 0, 0, 1, 1));
					}
					itemPanel.add(new JPanel(), itemPanelGBC(1d, 1d, 2, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER));
					instPanel.add(itemPanel, itemPanelGBC(0d, 0d, 0, keyNum, 1, 1));
					keyNum += 1;
					isSep = false;
				}
			}
			instPanel.add(new JPanel(), itemPanelGBC(1d, 1d, 0, keyNum, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER));
			JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scroll.setPreferredSize(new Dimension(240, 250));
			scroll.getVerticalScrollBar().setUnitIncrement(60);
			scroll.setViewportView(instPanel);
			
			delimPanel.add(scroll, delimPanelGBC(0d, 0d, 1, 1, 1, 1));
			delimPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 2, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER));
			
			container.add(delimPanel, mainContainerGBC(0, 2, 1, 1));
			
			
			
			// MS Type
			labelPanel = newLabelPanel("MS Type", " MS type. ", LABEL_SIZE_L, 2);
			
			JPanel msPanel = new JPanel();
			msPanel.setLayout(new BoxLayout(msPanel, BoxLayout.X_AXIS));
			
			final JCheckBox chkBoxMsAll = new JCheckBox("All");
			chkBoxMsAll.setSelected(isMsAll());
			keyListenerList.add(chkBoxMsAll);
			msPanel.add(chkBoxMsAll);
			
			for (Iterator i=msCheck.keySet().iterator(); i.hasNext();) {
				String key = (String)i.next();
				
				JCheckBox chkBox = (JCheckBox)msCheck.get(key);
				keyListenerList.add(chkBox);
				msPanel.add(chkBox);
				msPanel.add(new JLabel(" "));
			}
			
			itemPanel = new JPanel();
			initItemPanel(itemPanel);
			itemPanel.add(msPanel, itemPanelGBC(0d, 0d, 0, 0, 1, 1));
			itemPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 1, 0, GridBagConstraints.REMAINDER, 1));

			delimPanel = new JPanel();
			initDelimPanel(delimPanel, true);
			delimPanel.add(labelPanel, delimPanelGBC(0d, 0d, 0, 0, 1, 1));
			delimPanel.add(itemPanel, delimPanelGBC(0d, 0d, 1, 0, 1, 1));
			delimPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 2, 0, GridBagConstraints.REMAINDER, 1));
			
			container.add(delimPanel, mainContainerGBC(0, 3, 1, 1));
			
			
			
			// Ion Mode
			labelPanel = newLabelPanel("Ion Mode", " Ion mode. ", LABEL_SIZE_L, 2);
			
			JPanel ionPanel = new JPanel();
			ionPanel.setLayout(new BoxLayout(ionPanel, BoxLayout.X_AXIS));
			
			ButtonGroup ionGroup = new ButtonGroup();
			for (Iterator i=ionRadio.keySet().iterator(); i.hasNext();) {
				String key = (String)i.next();
				
				JRadioButton rdoBtn = (JRadioButton)ionRadio.get(key);
				keyListenerList.add(rdoBtn);
				ionGroup.add(rdoBtn);
				ionPanel.add(rdoBtn);
			}
			
			itemPanel = new JPanel();
			initItemPanel(itemPanel);
			itemPanel.add(ionPanel, itemPanelGBC(0d, 0d, 0, 0, 1, 1));
			itemPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 1, 0, GridBagConstraints.REMAINDER, 1));

			delimPanel = new JPanel();
			initDelimPanel(delimPanel, true);
			delimPanel.add(labelPanel, delimPanelGBC(0d, 0d, 0, 0, 1, 1));
			delimPanel.add(itemPanel, delimPanelGBC(0d, 0d, 1, 0, 1, 1));
			delimPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 2, 0, GridBagConstraints.REMAINDER, 1));
			
			container.add(delimPanel, mainContainerGBC(0, 4, 1, 1));
			
			
			
			// Precursor m/z
			labelPanel = newLabelPanel("Precursor m/z", " Precursor m/z. ", LABEL_SIZE_L, 2);
			
			preField = new JTextField(((PRECURSOR < 0) ? "" : String.valueOf(PRECURSOR)), 5);
			preField.setHorizontalAlignment(JTextField.RIGHT);
			keyListenerList.add(preField);
			
			itemPanel = new JPanel();
			initItemPanel(itemPanel);
			itemPanel.add(wrappTextPanel(preField), itemPanelGBC(0d, 0d, 0, 0, 1, 1));
			itemPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 2, 0, GridBagConstraints.REMAINDER, 1));
			
			delimPanel = new JPanel();
			initDelimPanel(delimPanel, true);
			delimPanel.add(labelPanel, delimPanelGBC(0d, 0d, 0, 0, 1, 1));
			delimPanel.add(itemPanel, delimPanelGBC(0d, 0d, 1, 0, 1, 1));
			delimPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 2, 0, GridBagConstraints.REMAINDER, 1));
			
			container.add(delimPanel, mainContainerGBC(0, 5, 1, 1));
			
			
			
			// �{�^��
			final JButton okButton = new JButton("OK");
			keyListenerList.add(okButton);
			final JButton cancelButton = new JButton("Cancel");
			keyListenerList.add(cancelButton);
			JPanel btnPanel = new JPanel();
			btnPanel.add(okButton);
			btnPanel.add(cancelButton);
			
			container.add(btnPanel, mainContainerGBC(0, 6, 1, 1));
			
			
			// ���u���All�`�F�b�N���X�i�[
			chkBoxInstAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (Iterator i=instCheck.keySet().iterator(); i.hasNext(); ) {
						String key = (String)i.next();
						if (chkBoxInstAll.isSelected()) {
							((JCheckBox)instCheck.get(key)).setSelected(true);
						} else {
							((JCheckBox)instCheck.get(key)).setSelected(false);
						}
					}
					chkBoxInstDefault.setSelected(isInstDefault());
				}
			});
			
			// ���u���Default�`�F�b�N���X�i�[
			chkBoxInstDefault.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (Iterator i=instCheck.keySet().iterator(); i.hasNext(); ) {
						String key = (String)i.next();
						if (chkBoxInstDefault.isSelected()) {
							// All�`�F�b�N���͂����ăf�t�H���g�I��
							chkBoxInstAll.setSelected(false);
							if ( isDefaultInst(key) ) {
								((JCheckBox)instCheck.get(key)).setSelected(true);
							} else {
								((JCheckBox)instCheck.get(key)).setSelected(false);	
							}
						} else {
							((JCheckBox)instCheck.get(key)).setSelected(false);
						}
					}
					chkBoxInstAll.setSelected(isInstAll());
				}
			});
			
			// ���u��ʃ`�F�b�N���X�i�[
			for (Iterator i=instCheck.keySet().iterator(); i.hasNext();) {
				String key = (String)i.next();
				((JCheckBox)instCheck.get(key)).addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						chkBoxInstAll.setSelected(isInstAll());
						chkBoxInstDefault.setSelected(isInstDefault());
					}
				});
			}
			
			// MS���All�`�F�b�N���X�i�[
			chkBoxMsAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (Iterator i=msCheck.keySet().iterator(); i.hasNext(); ) {
						String key = (String)i.next();
						if (chkBoxMsAll.isSelected()) {
							((JCheckBox)msCheck.get(key)).setSelected(true);
						} else {
							((JCheckBox)msCheck.get(key)).setSelected(false);
						}
					}
				}
			});
			
			// MS��ʃ`�F�b�N���X�i�[
			for (Iterator i=msCheck.keySet().iterator(); i.hasNext();) {
				String key = (String)i.next();
				((JCheckBox)msCheck.get(key)).addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						chkBoxMsAll.setSelected(isMsAll());
					}
				});
			}
			
			// OK�{�^�����X�i�[
			okButton.addActionListener(new ActionListener() {
				private final Color defColor = okButton.getBackground();
				private void startProc() {
					// �{�^���̐F��ύX
					okButton.setBackground(Color.PINK);
					okButton.update(okButton.getGraphics());
					// �}�E�X�J�[�\���������v��
					ParameterSetWindow.this.setCursor(waitCursor);
				}
				private void endProc() {
					// �}�E�X�J�[�\�����f�t�H���g�J�[�\����
					if (!ParameterSetWindow.this.getCursor().equals(Cursor.getDefaultCursor())) {
						ParameterSetWindow.this.setCursor(Cursor.getDefaultCursor());
					}
					// �{�^���̐F��߂�
					okButton.setBackground(defColor);
				}
				public void actionPerformed(ActionEvent e) {
					
					startProc();
					
					// ���̓`�F�b�N
					try {
						tolField.setText(tolField.getText().trim());
						float num = Float.parseFloat(tolField.getText());
						if (num < 0) {
							JOptionPane.showMessageDialog(null,
									"[Tolerance]  Value must be an positive numerical value.",
									"Warning",
									JOptionPane.WARNING_MESSAGE);
							endProc();
							return;
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null,
								"[Tolerance]  Value must be an numerical value.",
								"Warning",
								JOptionPane.WARNING_MESSAGE);
						endProc();
						return;
					}
					try {
						cutoffField.setText(cutoffField.getText().trim());
						int num = Integer.parseInt(cutoffField.getText());
						if (num < 0) {
							JOptionPane.showMessageDialog(null,
									"[Cutoff Threshold]  Value must be an positive integer.",
									"Warning",
									JOptionPane.WARNING_MESSAGE);
							endProc();
							return;
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null,
								"[Cutoff Threshold]  Value must be an integer.",
								"Warning",
								JOptionPane.WARNING_MESSAGE);
						endProc();
						return;
					}
					if (instCheck.size() == 0) {
						JOptionPane.showMessageDialog(null,
								"[Instrument Type]  Instrument type is not registered in the database.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						endProc();
						return;							
					}
					if (!isInstCheck()) {
						JOptionPane.showMessageDialog(null,
								"[Instrument Type]  Select one or more checkbox.",
								"Warning",
								JOptionPane.WARNING_MESSAGE);
						endProc();
						return;	
					}
					if (msCheck.size() == 0) {
						JOptionPane.showMessageDialog(null,
								"[MS Type]  MS type is not registered in the database.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						endProc();
						return;							
					}
					if (!isMsCheck()) {
						JOptionPane.showMessageDialog(null,
								"[MS Type]  Select one or more checkbox.",
								"Warning",
								JOptionPane.WARNING_MESSAGE);
						endProc();
						return;	
					}
					try {
						preField.setText(preField.getText().trim());
						if (!preField.getText().equals("")) {
							int num = Integer.parseInt(preField.getText());
							if (num < 1) {
								JOptionPane.showMessageDialog(null,
										"[Precursor m/z]  Value must be an integer of 1 or more.",
										"Warning",
										JOptionPane.WARNING_MESSAGE);
								endProc();
								return;								
							}
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null,
								"[Precursor m/z]  Value must be an integer of 1 or more.",
								"Warning",
								JOptionPane.WARNING_MESSAGE);
						endProc();
						return;
					}
					
					
					// �����ݒ�ɕύX���������ꍇ
					if (isPreChange()
							|| isTolChange()
							|| isCutoffChange()
							|| isInstChange()
							|| isMsChange()
							|| isIonChange()) {
						
						preChange(true);
						tolChange(true);
						cutoffChange(true);
						instChange(true);
						msChange(true);
						ionChange(true);

						// �N�G���[�̑I����Ԃ̍X�V����
						resultPlot.setSpectrumInfo("", "", "", "", false);
						switch (queryTabPane.getSelectedIndex()) {
						case TAB_ORDER_DB :						// DB�^�u�I����
							updateSelectQueryTable(queryDbTable);
							break;
						case TAB_ORDER_FILE :						// FILE�^�u�I����
							updateSelectQueryTable(queryFileTable);
							break;
						}
					}
					
					endProc();

					dispose();
					isSubWindow = false;
				}
			});
			
			// Cancel�{�^�����X�i�[
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// �ݒ����߂�
					preChange(false);
					tolChange(false);
					cutoffChange(false);
					instChange(false);
					msChange(false);
					ionChange(false);
					
					dispose();
					isSubWindow = false;
				}
			});

			// �L�[���X�i�[�ǉ�
			for (int i = 0; i < keyListenerList.size(); i++) {
				keyListenerList.get(i).addKeyListener(new KeyAdapter() {
					public void keyReleased(KeyEvent e) {
						// Esc�L�[�����[�X
						if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
							// �ݒ����߂�
							preChange(false);
							tolChange(false);
							cutoffChange(false);
							instChange(false);
							msChange(false);
							ionChange(false);
							
							dispose();
							isSubWindow = false;
						}
					}
				});
			}
			setTitle("Search Parameter Setting");
			pack();
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			setLocation((int)(d.getWidth() / 2 - getWidth() / 2),
					(int)(d.getHeight() / 2 - getHeight() / 2));
			setVisible(true);
			
			// �E�B���h�E���X�i�[�ǉ�
			addWindowListener(new WindowAdapter() {
				public void windowOpened(WindowEvent e) {
					isSubWindow = true;
				}
				public void windowClosing(WindowEvent e) {
					cancelButton.doClick();
				}
			});
		}
		
		/**
		 * Tolerance�l�ύX�m�F
		 * Tolerance�l���ύX����Ă����ꍇ��true��ԋp����B
		 * @return �ύX�t���O
		 */
		private boolean isTolChange() {
			
			if (Float.parseFloat(tolField.getText()) != TOLERANCE) {
				return true;
			}
			else if (isTolUnit1 != tolUnit1.isSelected()
					|| isTolUnit2 != tolUnit2.isSelected()) {
				return true;
			}
			return false;
		}
		
		/**
		 * Tolerance�l�ύX
		 * Tolerance�l��ύX�����ꍇ�Ɋm��������̓L�����Z������B
		 * @param isChange �ύX�t���O
		 */
		private void tolChange(boolean isChange) {
			
			if (isChange) {
				
				// Cookie���p���X�g
				ArrayList<String> valueList = new ArrayList<String>();
				
				TOLERANCE = Float.parseFloat(tolField.getText());
				valueList.add(String.valueOf(TOLERANCE));
				
				isTolUnit1 = tolUnit1.isSelected();
				isTolUnit2 = tolUnit2.isSelected();
				if (tolUnit2.isSelected()) {
					valueList.add(tolUnit2.getText());
				}
				else {
					valueList.add(tolUnit1.getText());
				}
				
				// Tolerance�l��Cookie�ɐݒ�
				cm.setCookie(COOKIE_TOL, valueList);
			}
			else {
				tolUnit1.setSelected(isTolUnit1);
				tolUnit2.setSelected(isTolUnit2);
			}
		}
		
		/**
		 * Cutoff Threshold�l�ύX�m�F
		 * Cutoff Threshold�l���ύX����Ă����ꍇ��true��ԋp����B
		 * @return �ύX�t���O
		 */
		private boolean isCutoffChange() {
			
			if (Integer.parseInt(cutoffField.getText()) != CUTOFF_THRESHOLD) {
				return true;
			}
			return false;
		}
		
		/**
		 * Cutoff Threshold�l�ύX
		 * Cutoff Threshold�l��ύX�����ꍇ�Ɋm��������̓L�����Z������B
		 * @param isChange �ύX�t���O
		 */
		private void cutoffChange(boolean isChange) {
			
			if (isChange) {
				
				// Cookie���p���X�g
				ArrayList<String> valueList = new ArrayList<String>();
				
				CUTOFF_THRESHOLD = Integer.parseInt(cutoffField.getText());
				valueList.add(String.valueOf(CUTOFF_THRESHOLD));
				
				// Cutoff Threshold�l��Cookie�ɐݒ�
				cm.setCookie(COOKIE_CUTOFF, valueList);
			}
		}
		
		/**
		 * ���u��ʃ`�F�b�N�{�b�N�X�l�`�F�b�N�iAll�j
		 * ���u��ʂ�All�I����Ԃ���ԋp����B
		 * @return All�I���t���O
		 */
		private boolean isInstAll() {
			
			if (instCheck.size() == 0) {
				return false;
			}
			for (Iterator j=instCheck.keySet().iterator(); j.hasNext(); ) {
				String key = (String)j.next();
				
				if ( !((JCheckBox)instCheck.get(key)).isSelected() ) {
					return false;
				}
			}
			return true;
		}

		/**
		 * ���u��ʃ`�F�b�N�{�b�N�X�l�`�F�b�N�iDefault�j
		 * ���u��ʂ�Default�I����Ԃ���ԋp����B 
		 * @return Default�I���t���O
		 */
		private boolean isInstDefault() {
			
			if (instCheck.size() == 0) {
				return false;
			}
			for (Iterator j=instCheck.keySet().iterator(); j.hasNext(); ) {
				String key = (String)j.next();
				if ( isDefaultInst(key) ) {	// �f�t�H���g���u��ʂ̏ꍇ
					if ( !((JCheckBox)instCheck.get(key)).isSelected() ) {
						return false;
					}
				} else {
					if ( ((JCheckBox)instCheck.get(key)).isSelected() ) {
						return false;
					}
				}
			}
			return true;
		}
		
		/**
		 * ���u��ʑI���`�F�b�N
		 * ���u��ʂ�1�ł��I������Ă����ꍇ��true��ԋp����B
		 * @return �I���ς݃t���O
		 */
		private boolean isInstCheck() {
			
			if (instCheck.size() == 0) {
				return false;
			}
			for (Iterator j=instCheck.keySet().iterator(); j.hasNext(); ) {
				String key = (String)j.next();
				if ( ((JCheckBox)instCheck.get(key)).isSelected() ) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * ���u��ʑI��l�ύX�m�F
		 * ���u��ʃ`�F�b�N�{�b�N�X�̒l��1�ł��ύX����Ă����ꍇ��true��ԋp����B
		 * @return �ύX�t���O
		 */
		private boolean isInstChange() {
			
			if (isInstCheck.size() == 0) {
				return false;
			}
			for (Iterator i=isInstCheck.keySet().iterator(); i.hasNext(); ) {
				String key = (String)i.next();
				boolean before = (boolean)isInstCheck.get(key);
				boolean after = ((JCheckBox)instCheck.get(key)).isSelected();
				if (before != after) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * ���u��ʑI��l�ύX
		 * �`�F�b�N�{�b�N�X��I��ύX�����ꍇ�Ɋm��������̓L�����Z������B
		 * @param isChange �ύX�t���O
		 */
		private void instChange(boolean isChange) {
			
			// Cookie���p���X�g
			ArrayList<String> valueList = new ArrayList<String>();		
			
			if (isInstCheck.size() == 0) {
				return;
			}
			for (Iterator i=isInstCheck.keySet().iterator(); i.hasNext(); ) {
				String key = (String)i.next();
				boolean before = (boolean)isInstCheck.get(key);
				boolean after = ((JCheckBox)instCheck.get(key)).isSelected();
				if (before != after) {
					if (isChange) {
						isInstCheck.put(key, after);
					}
					else {
						((JCheckBox)instCheck.get(key)).setSelected(before);
					}
				}
				if ( ((JCheckBox)instCheck.get(key)).isSelected() ) {
					valueList.add(key);
				}
			}
			// ���u��ʑI����Ԃ�Cookie�ɐݒ�
			if (isChange) {
				cm.setCookie(COOKIE_INST, valueList);
			}
		}
		
		/**
		 * MS��ʃ`�F�b�N�{�b�N�X�l�`�F�b�N�iAll�j
		 * MS��ʂ�All�I����Ԃ���ԋp����B
		 * @return All�I���t���O
		 */
		private boolean isMsAll() {
			
			if (msCheck.size() == 0) {
				return false;
			}
			for (Iterator j=msCheck.keySet().iterator(); j.hasNext(); ) {
				String key = (String)j.next();
				if ( !((JCheckBox)msCheck.get(key)).isSelected() ) {
					return false;
				}
			}
			return true;
		}
		
		/**
		 * MS��ʑI���`�F�b�N
		 * MS��ʂ�1�ł��I������Ă����ꍇ��true��ԋp����B
		 * @return �I���ς݃t���O
		 */
		private boolean isMsCheck() {
			
			if (msCheck.size() == 0) {
				return false;
			}
			for (Iterator j=msCheck.keySet().iterator(); j.hasNext(); ) {
				String key = (String)j.next();
				if ( ((JCheckBox)msCheck.get(key)).isSelected() ) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * MS��ʑI��l�ύX�m�F
		 * MS��ʃ`�F�b�N�{�b�N�X�̒l��1�ł��ύX����Ă����ꍇ��true��ԋp����B
		 * @return �ύX�t���O
		 */
		private boolean isMsChange() {
			
			if (isMsCheck.size() == 0) {
				return false;
			}
			for (Iterator i=isMsCheck.keySet().iterator(); i.hasNext(); ) {
				String key = (String)i.next();
				boolean before = (boolean)isMsCheck.get(key);
				boolean after = ((JCheckBox)msCheck.get(key)).isSelected();
				if (before != after) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * MS��ʑI��l�ύX
		 * �`�F�b�N�{�b�N�X��I��ύX�����ꍇ�Ɋm��������̓L�����Z������B
		 * @param isChange �ύX�t���O
		 */
		private void msChange(boolean isChange) {
			
			// Cookie���p���X�g
			ArrayList<String> valueList = new ArrayList<String>();		
			
			if (isMsCheck.size() == 0) {
				return;
			}
			for (Iterator i=isMsCheck.keySet().iterator(); i.hasNext(); ) {
				String key = (String)i.next();
				boolean before = (boolean)isMsCheck.get(key);
				boolean after = ((JCheckBox)msCheck.get(key)).isSelected();
				if (before != after) {
					if (isChange) {
						isMsCheck.put(key, after);
					}
					else {
						((JCheckBox)msCheck.get(key)).setSelected(before);
					}
				}
				if ( ((JCheckBox)msCheck.get(key)).isSelected() ) {
					valueList.add(key);
				}
			}
			// MS��ʑI����Ԃ�Cookie�ɐݒ�
			if (isChange) {
				cm.setCookie(COOKIE_MS, valueList);
			}
		}
		
		/**
		 * �C�I����ʑI��l�ύX�m�F
		 * �C�I����ʃ��W�I�{�^���̒l���ύX����Ă����ꍇ��true��ԋp����B
		 * @return �ύX�t���O
		 */
		private boolean isIonChange() {
			
			for (Iterator i=isIonRadio.keySet().iterator(); i.hasNext(); ) {
				String key = (String)i.next();
				
				boolean before = (boolean)isIonRadio.get(key);
				boolean after = ((JRadioButton)ionRadio.get(key)).isSelected();
				
				if (before != after) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * �C�I����ʑI��l�ύX
		 * ���W�I�{�^����I��ύX�����ꍇ�Ɋm��������̓L�����Z������B
		 * @param isChange �ύX�t���O
		 */
		private void ionChange(boolean isChange) {
			
			// Cookie���p���X�g
			ArrayList<String> valueList = new ArrayList<String>();		
			
			for (Iterator i=isIonRadio.keySet().iterator(); i.hasNext(); ) {
				String key = (String)i.next();
				
				boolean before = (boolean)isIonRadio.get(key);
				boolean after = ((JRadioButton)ionRadio.get(key)).isSelected();
				
				if (before != after) {
					if (isChange) {
						isIonRadio.put(key, after);
					}
					else {
						((JRadioButton)ionRadio.get(key)).setSelected(before);
					}
				}
				if ( ((JRadioButton)ionRadio.get(key)).isSelected() ) {
					valueList.add(key);
				}
			}
			// �C�I����ʑI����Ԃ�Cookie�ɐݒ�
			if (isChange) {
				cm.setCookie(COOKIE_ION, valueList);
			}
		}
		
		/**
		 * Precursor m/z�l�ύX�m�F
		 * Precursor m/z�l���ύX����Ă����ꍇ��true��ԋp����B
		 * @return �ύX�t���O
		 */
		private boolean isPreChange() {
			
			if (preField.getText().equals("")) {
				if (PRECURSOR != -1) {
					return true;
				}
			}
			else if (Integer.parseInt(preField.getText()) != PRECURSOR) {
				return true;
			}
			return false;
		}
		
		/**
		 * Precursor m/z�l�ύX
		 * Precursor m/z�l��ύX�����ꍇ�Ɋm��������̓L�����Z������B
		 * @param isChange �ύX�t���O
		 */
		private void preChange(boolean isChange) {
			
			if (isChange) {
				
//				// Cookie���p���X�g
//				ArrayList<String> valueList = new ArrayList<String>();
				
				if (preField.getText().equals("")) {
					PRECURSOR = -1;
				}
				else {
					PRECURSOR = Integer.parseInt(preField.getText());
				}
//				valueList.add(String.valueOf(PRECURSOR));
				
//				// Precursor m/z�l��Cookie�ɐݒ�
//				cm.setCookie(COOKIE_PRE, valueList);
			}
		}
		
		/**
		 * ���C���R���e�i�[�̏�����
		 * @param c �R���e�i�[
		 */
		private void initMainContainer(Container c) {
			c.setLayout(new GridBagLayout());
		}

		/**
		 * ���C���R���e�i�[�p���C�A�E�g����
		 * @param x ���������Z���ʒu
		 * @param y ���������Z���ʒu
		 * @param w ����������1�s�̃Z����
		 * @param h ����������1�s�̃Z����
		 * @return ���C�A�E�g����
		 */
		private GridBagConstraints mainContainerGBC(int x, int y, int w, int h) {
			
			GridBagConstraints gbc = new GridBagConstraints();
			
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.weightx = 1.0d;
			gbc.weighty = 1.0d;
			gbc.insets = new Insets(15, 15, 0, 15);
			
			gbc.gridx = x;
			gbc.gridy = y;
			gbc.gridwidth = w;
			gbc.gridheight = h;
			
			return gbc;
		}
		
		/**
		 * ������؂�p�̃p�l���̏�����
		 * 
		 * @param p
		 * @param isBorder
		 * @return ������؂�p�p�l��
		 */
		private void initDelimPanel(JPanel p, boolean isBorder) {
			
			p.setLayout(new GridBagLayout());
			
			if (isBorder) {
				Border border = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
						new EmptyBorder(3, 3, 3, 3));
				p.setBorder(border);
			}
		}
		
		/**
		 * ������؂�p�̃p�l���̃��C�A�E�g����
		 * 
		 * @parma wx ���������g��{��
		 * @parma wy ���������g��{��
		 * @param x ���������Z���ʒu
		 * @param y ���������Z���ʒu
		 * @param w ����������1�s�̃Z����
		 * @param h ����������1�s�̃Z����
		 * @return ���C�A�E�g����
		 */
		private GridBagConstraints delimPanelGBC(double wx, double wy, int x, int y, int w, int h) {
			
			GridBagConstraints gbc = new GridBagConstraints();
			
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.weightx = wx;
			gbc.weighty = wy;
			gbc.insets = new Insets(2, 2, 2, 2);
			
			gbc.gridx = x;
			gbc.gridy = y;
			gbc.gridwidth = w;
			gbc.gridheight = h;
			
			return gbc;
		}
		
		/**
		 * ���x���p�l�������i���C�A�E�g�����p�j
		 * 
		 * JLabel�𒼐ڃR���|�[�l���g��add����ƈʒu�̔��������ł��Ȃ����߁A
		 * ���������s��JPanel�Ń��b�s���O���ĕԋp����B
		 * �܂��A���x���T�C�Y�̓���̂��߂Ɏg�p���邱�Ƃ𐄏�����B
		 * 
		 * @param label ���x��������iHTML�^�O���ɕ\�����镶����j
		 * @param tooltip �c�[���`�b�v������iHTML�^�O���ɕ\�����镶����j
		 * @param size ���x���T�C�Y
		 * @param labelIndent ���x�����C���f���g���i���p�X�y�[�X�̐��j
		 * @return ���x�������b�s���O�����p�l��
		 */
		private JPanel newLabelPanel(String label, String tooltip, int size, int labelIndent) {
			// ���x������
			for (int i=0; i<labelIndent; i++) {
				label = " " + label;
			}
			JLabel l = new JLabel(label);
			
			switch (size) {
				case LABEL_SIZE_L:
					l.setPreferredSize(new Dimension(110, 20));
					l.setMinimumSize(new Dimension(110, 20));				
					break;
				case LABEL_SIZE_M:
					l.setPreferredSize(new Dimension(85, 20));
					l.setMinimumSize(new Dimension(85, 20));				
					break;
				case LABEL_SIZE_S:
					l.setPreferredSize(new Dimension(45, 20));
					l.setMinimumSize(new Dimension(45, 20));				
					break;				
				default:
					break;
			}
			
			if (tooltip != null) {
				l.setToolTipText(tooltip);
			}
			
			
			// �p�l���Z�b�g
			JPanel p = new JPanel();
			p.setLayout(new GridBagLayout());
			
			GridBagConstraints gbc = new GridBagConstraints();
			
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.weightx = 0.0d;
			gbc.weighty = 0.0d;
			gbc.insets = new Insets(2, 2, 2, 2);
			
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			
			p.add(l, gbc);
			
			return p;
		}
		
		/**
		 * �e�L�X�g�t�B�[���h���b�s���O�i���C�A�E�g�����p�j
		 * 
		 * JTextField��add����ꍇ�Ɉʒu�̔��������s�������ꍇ�Ɏg�p����B
		 * 
		 * @return �e�L�X�g�t�B�[���h�����b�s���O�����p�l��
		 */
		private JPanel wrappTextPanel(JTextField t) {
			
			// �p�l���Z�b�g
			JPanel p = new JPanel();
			p.setLayout(new GridBagLayout());
			
			GridBagConstraints gbc = new GridBagConstraints();
			
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.weightx = 0.0d;
			gbc.weighty = 0.0d;
			gbc.insets = new Insets(2, 2, 2, 2);
			
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			
			p.add(t, gbc);
			
			return p;
		}
		
		/**
		 * �A�C�e���p�l���̏�����
		 * @return �A�C�e���p�l��
		 */
		private void initItemPanel(JPanel p) {
			p.setLayout(new GridBagLayout());
		}
		
		/**
		 * �A�C�e���p�l���̃��C�A�E�g����
		 * @parma wx ���������g��{��
		 * @parma wy ���������g��{��
		 * @param x ���������Z���ʒu
		 * @param y ���������Z���ʒu
		 * @param w ����������1�s�̃Z����
		 * @param h ����������1�s�̃Z����
		 * @return ���C�A�E�g����
		 */
		private GridBagConstraints itemPanelGBC(double wx, double wy, int x, int y, int w, int h) {
			
			GridBagConstraints gbc = new GridBagConstraints();
			
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.weightx = wx;
			gbc.weighty = wy;
			gbc.insets = new Insets(2, 2, 2, 2);
			
			gbc.gridx = x;
			gbc.gridy = y;
			gbc.gridwidth = w;
			gbc.gridheight = h;
			
			return gbc;
		}	
	}
	
	/**
	 * File�^�u�̃e�[�u�����X�g���f�����X�i�[�N���X
	 * SerarchPage�̃C���i�[�N���X�B
	 */
	class LmFileListener implements ListSelectionListener {
		
		/**
		 * �o�����[�`�F���W�C�x���g
		 * @see javax.swing.event.ListSelectionListener#valueChanged(java.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent le) {
			
			if (le.getValueIsAdjusting()) {
				return;
			}
			
			final int selRow = queryFileTable.getSelectedRow();
			if (selRow < 0) {
				// CompareView������
				queryPlot.clear();
				compPlot.clear();
				resultPlot.clear();
				queryPlot.setPeaks(null, 0);
				compPlot.setPeaks(null, 1);
				resultPlot.setPeaks(null, 0);

				// PackageView������
				pkgView.initAllRecInfo();
				
				DefaultTableModel dm = (DefaultTableModel)resultSorter.getTableModel();
				dm.setRowCount(0);
				hitLabel.setText(" ");
				return;
			}
			
			// �}�E�X�J�[�\���������v��
			SearchPage.this.setCursor(waitCursor);

			// PackageView�\���t�@�C���f�[�^�ݒ�
			int noCol = queryFileTable.getColumnModel().getColumnIndex(COL_LABEL_NO);
			int nameCol = queryFileTable.getColumnModel().getColumnIndex(COL_LABEL_NAME);
			int idCol = queryFileTable.getColumnModel().getColumnIndex(COL_LABEL_ID);
			
			// �s�[�N���
			String[] peaks = userDataList[selRow].getPeaks();
				
			PackageRecData recData = new PackageRecData();
			
			// == �N�G���[���R�[�h�t���O ===
			recData.setQueryRecord(true);
			
			// === �������R�[�h�t���O ===
			recData.setIntegRecord(false);
			
			// === ID ===
			recData.setId((String)queryFileTable.getValueAt(selRow, idCol));
			
			// === �X�R�A ===
			recData.setScore(" -");
			
			// === �T�C�g ===
			recData.setSite("");
			
			// === �������� ===
			recData.setName((String)queryFileTable.getValueAt(selRow, nameCol));
			
			// === �v���J�[�T�[ ===
			recData.setPrecursor("");
			
			// === �s�[�N�� ===
			int num = peaks.length;
			if (num == 1) {
				if (peaks[0].split("\t")[0].equals("0") && peaks[0].split("\t")[1].equals("0")) {
					num = 0;
				}
			}
			recData.setPeakNum( num );
			
			for (int i=0; i < recData.getPeakNum(); i++ ) {
				// === m/z ===
				recData.setMz( i, peaks[i].split("\t")[0] );
				
				// === ���x ===
				recData.setIntensity(i, peaks[i].split("\t")[1] );
			}
			
			// === �s�[�N�F ===
			recData.setPeakColorType(PackageRecData.COLOR_TYPE_BLACK);
			
			// ���R�[�h���ǉ�
			pkgView.addQueryRecInfo(recData);
			pkgView.addRecInfoAfter(true, recData.getId(), PackageSpecData.SORT_KEY_NONE);
			
			
			// DB����
			String name = (String)queryFileTable.getValueAt(selRow, nameCol);
			String key = String.valueOf(queryFileTable.getValueAt(selRow, noCol));
			searchDb(userDataList[selRow].getPeaks(), "", name, key);
		}
	}
	
	/**
	 * Result�^�u�̃e�[�u�����X�g���f�����X�i�[�N���X
	 * SerarchPage�̃C���i�[�N���X�B
	 */
	class LmResultListener implements ListSelectionListener {
		
		/**
		 * �o�����[�`�F���W�C�x���g
		 * @see javax.swing.event.ListSelectionListener#valueChanged(java.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent le) {
			
			if (le.getValueIsAdjusting()) {
				return;
			}
			
			int[] selRows = resultTable.getSelectedRows();
			if (selRows.length < 1) {
				// CompareView������
				resultPlot.clear();
				compPlot.setPeaks(null, 1);
				resultPlot.setPeaks(null, 0);
				setAllPlotAreaRange();
				
				// PackageView������
				pkgView.initResultRecInfo();
				return;
			}
			
			// �}�E�X�J�[�\���������v��
			SearchPage.this.setCursor(waitCursor);
			
			int idCol = resultTable.getColumnModel().getColumnIndex(COL_LABEL_ID);
			int nameCol = resultTable.getColumnModel().getColumnIndex(COL_LABEL_NAME);
			int siteNameCol = resultTable.getColumnModel().getColumnIndex(COL_LABEL_CONTRIBUTOR);
			int scoreCol = resultTable.getColumnModel().getColumnIndex(COL_LABEL_SCORE);
			
			String typeName = MassBankCommon.CGI_TBL[MassBankCommon.CGI_TBL_NUM_TYPE][MassBankCommon.CGI_TBL_TYPE_GSDATA];
			String id;
			String name;
			String relation;
			int ion;
			String score;
			String siteName;
			String site = "0";
			PackageRecData recData = null;

			if (isIonRadio.get("Posi")) {
				ion = 1;
			} else if (isIonRadio.get("Nega")) {
				ion = -1;
			} else {
				ion = 0;
			}
			
			if (isDispSelected) {
				
				// Compare View�p�f�[�^�N���X������
				if (selRows.length > 1) {
					// 2���ȏ�I�����͕\���ł��Ȃ�
					resultPlot.clear();
					compPlot.setPeaks(null, 1);
					resultPlot.setPeaks(null, 0);
					setAllPlotAreaRange();
				}
				if (selRows.length > MAX_DISPLAY_NUM) {
					JOptionPane.showMessageDialog(
							null,
							"Cannot display more than " + MAX_DISPLAY_NUM + " spectra in Package View.",
							"Warning",
							JOptionPane.WARNING_MESSAGE);
					SearchPage.this.setCursor(Cursor.getDefaultCursor());
					return;
				}
				
				// PackageView�\���f�[�^�ݒ�
				boolean recChangeFlag = true;
				PeakData peak = null;
				for (int i=0; i<selRows.length; i++) {
					
					id = (String)resultTable.getValueAt(selRows[i], idCol);
					name = (String)resultTable.getValueAt(selRows[i], nameCol);
					relation = "false";
					score = String.valueOf(resultTable.getValueAt(selRows[i], scoreCol));
					siteName = (String)resultTable.getValueAt(selRows[i], siteNameCol);
					for (int j=0; j<siteNameList.length; j++) {
						if (siteName.equals(siteNameList[j])) {
							site = Integer.toString(j);
							break;
						}
					}
					
					String reqUrl = baseUrl + "jsp/"
							+ MassBankCommon.DISPATCHER_NAME + "?type="
							+ typeName + "&id=" + id + "&site=" + site + "&relation=" + relation + "&ion=" + ion;
					
					String line = "";
					String findStr;
					try {
						URL url = new URL( reqUrl );
						URLConnection con = url.openConnection();
						
						// ���X�|���X�擾
						BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()) );
						
						// ���X�|���X�i�[
						String result;
						while ( (result = in.readLine()) != null ) {
							if ( !result.equals("") ) {		// �X�y�[�X�s��ǂݔ�΂�
								line = result;
								break;
							}
						}
						in.close();
					}
					catch (IOException iex) {
						iex.printStackTrace();
						SearchPage.this.setCursor(Cursor.getDefaultCursor());
					}
					
						
					recData = new PackageRecData();
					
					// === �������� ===
					findStr = "name=";
					recData.setName(line, findStr);
					
					// === �v���J�[�T�[ ===
					findStr = "precursor=";
					recData.setPrecursor(line, findStr);
					
					// === ID ===
					findStr = "id=";
					recData.setId(line, findStr);
					
					// === ���U���g���R�[�h�t���O ===
					recData.setResultRecord(true);
					
					// === �������R�[�h�t���O ===
					recData.setIntegRecord(recData.getName());
					
					// === �X�R�A ===
					recData.setScore(score);
					
					// === �T�C�g ===
					recData.setSite(site);
					
					// ���R�[�h���ȍ~�̕�������폜���A�s�[�N���(m/z�A���x)�݂̂��c��
					// �s�[�N���݂̂�؂�o���B
					if (line.indexOf("::") > 0) {
						line = line.substring(0, line.indexOf("::"));
					}
					String[] tmpPeak = line.split("\t\t");
					
					// === �s�[�N�� ===
					int num = tmpPeak.length;
					if (num == 1) {
						if (tmpPeak[0].split("\t")[0].equals("0") && tmpPeak[0].split("\t")[1].equals("0")) {
							num = 0;
						}
					}
					recData.setPeakNum( num );
					
					for (int j = 0; j < recData.getPeakNum(); j++ ) {
						// === m/z ===
						recData.setMz( j, tmpPeak[j].split("\t")[0] );
						
						// === ���x ===
						recData.setIntensity(j, tmpPeak[j].split("\t")[1] );
					}
					
					// === �s�[�N�F ===
					recData.setPeakColorType(PackageRecData.COLOR_TYPE_BLACK);
					
					// ���R�[�h���ǉ�
					pkgView.addResultRecInfo(recData, recChangeFlag);
					recChangeFlag = false;
					
					// Compare View�p�f�[�^�N���X����
					if (selRows.length == 1) {
						peak = new PeakData(tmpPeak);
						resultPlot.clear();
						resultPlot.setPeaks(peak, 0);
						resultPlot.setSpectrumInfo(name, id, recData.getPrecursor(), PeakPanel.SP_TYPE_RESULT, false);
						compPlot.setPeaks(peak, 1);
						setAllPlotAreaRange();
						compPlot.setTolerance(String.valueOf(TOLERANCE), tolUnit1.isSelected());
					}
				}
				
				pkgView.setTolerance(String.valueOf(TOLERANCE), tolUnit1.isSelected());
				pkgView.addRecInfoAfter(
						false,
						(String)resultTable.getValueAt(resultTable.getSelectionModel().getLeadSelectionIndex(),
						idCol),
						PackageSpecData.SORT_KEY_SCORE);
			}
			else if (isDispRelated) {
				id = (String)resultTable.getValueAt(selRows[0], idCol);
				name = (String)resultTable.getValueAt(selRows[0], nameCol);
				relation = "true";
				siteName = (String)resultTable.getValueAt(selRows[0], siteNameCol);
				for (int i = 0; i < siteNameList.length; i++) {
					if (siteName.equals(siteNameList[i])) {
						site = Integer.toString(i);
						break;
					}
				}
				String reqUrl = baseUrl + "jsp/"
						+ MassBankCommon.DISPATCHER_NAME + "?type="
						+ typeName + "&id=" + id + "&site=" + site + "&relation=" + relation + "&ion=" + ion;
				String precursor = "";
				PeakData peak = null;
				try {
					URL url = new URL(reqUrl);
					URLConnection con = url.openConnection();
					String line = "";
					String findStr;
					boolean recChangeFlag = true;
					BufferedReader in = new BufferedReader(
							new InputStreamReader(con.getInputStream()));
					
					// PackageView�\���f�[�^�ݒ�
					while ((line = in.readLine()) != null) {
						if (line.equals("")) {		// �X�y�[�X�s��ǂݔ�΂�
							continue;
						}
						
						recData = new PackageRecData();
						
						// === �������� ===
						findStr = "name=";
						recData.setName(line, findStr);
						
						// === �v���J�[�T�[ ===
						findStr = "precursor=";
						recData.setPrecursor(line, findStr);
						
						// === ID ===
						findStr = "id=";
						recData.setId(line, findStr);
						
						// === �������R�[�h�t���O ===
						recData.setIntegRecord(recData.getName());
						
						// === �X�R�A ===
						recData.setScore("");
						
						// === �T�C�g ===
						recData.setSite(site);
						
						// ���R�[�h���ȍ~�̕�������폜���A�s�[�N���(m/z�A���x)�݂̂��c��
						// �s�[�N���݂̂�؂�o���B
						if (line.indexOf("::") > 0) {
							line = line.substring(0, line.indexOf("::"));
						}
						String[] tmpPeak = line.split("\t\t");
						
						// === �s�[�N�� ===
						int num = tmpPeak.length;
						if (num == 1) {
							if (tmpPeak[0].split("\t")[0].equals("0") && tmpPeak[0].split("\t")[1].equals("0")) {
								num = 0;
							}
						}
						recData.setPeakNum( num );
						
						for (int j = 0; j < recData.getPeakNum(); j++ ) {
							// === m/z ===
							recData.setMz( j, tmpPeak[j].split("\t")[0] );
							
							// === ���x ===
							recData.setIntensity(j, tmpPeak[j].split("\t")[1] );
						}
						
						// === �s�[�N�F ===
						recData.setPeakColorType(PackageRecData.COLOR_TYPE_BLACK);
						
						// ���R�[�h���ǉ�
						pkgView.addResultRecInfo(recData, recChangeFlag);
						recChangeFlag = false;
						
						// Compare View�p�f�[�^�N���X����
						if (id.equals(recData.getId())) {
							peak = new PeakData(tmpPeak);
							precursor = recData.getPrecursor();
						}
					}
					in.close();
				}
				catch (Exception ex) {
					ex.printStackTrace();
					SearchPage.this.setCursor(Cursor.getDefaultCursor());
				}
				
				pkgView.setTolerance(String.valueOf(TOLERANCE), tolUnit1.isSelected());
				pkgView.addRecInfoAfter(false, id, PackageSpecData.SORT_KEY_NAME);
				
				resultPlot.clear();
				resultPlot.setPeaks(peak, 0);
				resultPlot.setSpectrumInfo(name, id, precursor, PeakPanel.SP_TYPE_RESULT, false);
				compPlot.setPeaks(peak, 1);

				setAllPlotAreaRange();
				compPlot.setTolerance(String.valueOf(TOLERANCE), tolUnit1.isSelected());
			}

			// �\�����摜�̃t�@�C�������擾����
			id = recData.getId();
			site = recData.getSite();
			String temp = recData.getName();
			String[] items = temp.split(";");
			name = URLEncoder.encode(items[0]);
			String getUrl = baseUrl + "jsp/GetCompoudInfo.jsp?name=" + name + "&site=" + site + "&id=" + id;
			String gifMFileName = "";
			String gifSFileName = "";
			String formula = "";
			String emass = "";
			try {
				URL url = new URL(getUrl);
				URLConnection con = url.openConnection();
				BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()) );
				String line = "";
				while ( (line = in.readLine()) != null ) {
					if ( line.indexOf("GIF:") >= 0 ) {
						gifMFileName = line.replace("GIF:", "");
					}
					else if ( line.indexOf("GIF_SMALL:") >= 0 ) {
						gifSFileName = line.replace("GIF_SMALL:", "");
					}
					else if ( line.indexOf("FORMULA:") >= 0 ) {
						formula = line.replace("FORMULA:", "");
					}
					else if ( line.indexOf("EXACT_MASS:") >= 0 ) {
						emass = line.replace("EXACT_MASS:", "");
					}
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
			resultPlot.loadStructGif(gifMFileName, gifSFileName);
			resultPlot.setCompoundInfo(formula, emass);

			SearchPage.this.setCursor(Cursor.getDefaultCursor());
		}
	}
	
	/**
	 * �N�G��DB�^�u�̃e�[�u�����X�g���f�����X�i�[�N���X
	 * SerarchPage�̃C���i�[�N���X�B
	 */
	class LmQueryDbListener implements ListSelectionListener {
		
		/**
		 * �o�����[�`�F���W�C�x���g
		 * @see javax.swing.event.ListSelectionListener#valueChanged(java.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent le) {
			
			if (le.getValueIsAdjusting()) {
				return;
			}
			
			final int selRow = queryDbTable.getSelectedRow();
			if (selRow < 0) {
				// CompareView������
				queryPlot.clear();
				compPlot.clear();
				resultPlot.clear();
				queryPlot.setPeaks(null, 0);
				compPlot.setPeaks(null, 1);
				resultPlot.setPeaks(null, 0);

				// PackageView������
				pkgView.initAllRecInfo();
				
				DefaultTableModel dm = (DefaultTableModel)resultSorter.getTableModel();
				dm.setRowCount(0);
				hitLabel.setText(" ");
				return;
			}
			
			// �}�E�X�J�[�\���������v��
			SearchPage.this.setCursor(waitCursor);
			
			int idCol = queryDbTable.getColumnModel().getColumnIndex(COL_LABEL_ID);
			int nameCol = queryDbTable.getColumnModel().getColumnIndex(COL_LABEL_NAME);
			
			// nameList����̃��R�[�h���擾�p�C���f�b�N�X����
			int nameListIndex = -1;
			if (!querySorter.isSorting()) {
				// �\�[�g�����̏ꍇ
				nameListIndex = selRow;
			} else {
				// �\�[�g�L��̏ꍇ
				String tmpId = (String) queryDbTable.getValueAt(selRow, idCol);
				for (int i = 0; i < nameList.size(); i++) {
					if (nameList.get(i)[0].equals(tmpId)) {
						nameListIndex = i;
						break;
					}
				}
			}
			String idName[] = (String[]) nameList.get(nameListIndex);
			String id = idName[0];
			String site = idName[2];
			
			
			// PackageView�\���N�G���[�f�[�^�ݒ�
			PackageRecData recData = new PackageRecData();
			
			// == �N�G���[���R�[�h�t���O ===
			recData.setQueryRecord(true);
			
			// === ID ===
			recData.setId(id);
			
			// === �X�R�A ===
			recData.setScore(" -");
			
			// === �T�C�g ===
			recData.setSite(site);
			
			String typeName = MassBankCommon.CGI_TBL[MassBankCommon.CGI_TBL_NUM_TYPE][MassBankCommon.CGI_TBL_TYPE_GSDATA];
			String reqUrl = baseUrl + "jsp/" + MassBankCommon.DISPATCHER_NAME
					+ "?type=" + typeName + "&id=" + id + "&site=" + site + "&relation=false";
			
			String line = "";
			String findStr;
			try {
				URL url = new URL( reqUrl );
				URLConnection con = url.openConnection();
				
				// ���X�|���X�擾
				BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()) );
				
				// ���X�|���X�i�[
				String result;
				while ( (result = in.readLine()) != null ) {
					// �X�y�[�X�s��ǂݔ�΂�
					if ( !result.equals("") ) {
						line = result;
						break;
					}
				}
				in.close();
			}
			catch (IOException iex) {
				iex.printStackTrace();
				SearchPage.this.setCursor(Cursor.getDefaultCursor());
			}
			
			// === �������� ===
			findStr = "name=";
			recData.setName(line, findStr);
			
			// === �������R�[�h�t���O ===
			recData.setIntegRecord(recData.getName());
			
			// === �v���J�[�T�[ ===
			findStr = "precursor=";
			recData.setPrecursor(line, findStr);
			
			// ���R�[�h���ȍ~�̕�������폜���A�s�[�N���(m/z�A���x)�݂̂��c��
			// �s�[�N���݂̂�؂�o���B
			if (line.indexOf("::") > 0) {
				line = line.substring(0, line.indexOf("::"));
			}
			String[] tmpPeak = line.split("\t\t");
			
			// === �s�[�N�� ===
			int num = tmpPeak.length;
			if (num == 1) {
				if (tmpPeak[0].split("\t")[0].equals("0") && tmpPeak[0].split("\t")[1].equals("0")) {
					num = 0;
				}
			}
			recData.setPeakNum( num );
			
			for (int i = 0; i < recData.getPeakNum(); i++ ) {
				// === m/z ===
				recData.setMz( i, tmpPeak[i].split("\t")[0] );
				
				// === ���x ===
				recData.setIntensity(i, tmpPeak[i].split("\t")[1] );
			}
			
			// === �s�[�N�F ===
			recData.setPeakColorType(PackageRecData.COLOR_TYPE_BLACK);
			
			// ���R�[�h���ǉ�
			pkgView.addQueryRecInfo(recData);
			pkgView.addRecInfoAfter(true, id, PackageSpecData.SORT_KEY_NONE);
			
			
			// DB����
			String name = (String)queryDbTable.getValueAt(selRow, nameCol);
			String key = (String)queryDbTable.getValueAt(selRow, idCol);
			searchDb(tmpPeak, recData.getPrecursor(), name, key);
		}
	}

	/**
	 * �e�[�u���}�E�X���X�i�[�N���X
	 * SerarchPage�̃C���i�[�N���X�B
	 */
	class TblMouseListener extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			
			// ���N���b�N�̏ꍇ
			if (SwingUtilities.isLeftMouseButton(e)) {

				JTable tbl = (JTable)e.getSource();
				
				if (e.getClickCount() == 2 && !tbl.equals(queryFileTable)) {
					showRecordPage(tbl);
				}
				else if (e.getClickCount() == 1) {
					
					if (e.isShiftDown() || e.isControlDown()) {
						return;
					}
					
					int selRow[] = tbl.getSelectedRows();
					int idCol = tbl.getColumnModel().getColumnIndex(COL_LABEL_ID);
					
					// Package View �e�[�u���đI������
					String id = (String)tbl.getValueAt(selRow[0], idCol);
					if (tbl.equals(resultTable)) {
						pkgView.setTblSelection(false, id);
					}
					else {
						pkgView.setTblSelection(true, id);
					}
				}
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			
			// �E�����[�X�̏ꍇ
			if (SwingUtilities.isRightMouseButton(e)) {
				
				recListPopup(e);
			}
		}
	}
	
	/**
	 * �y�C���}�E�X���X�i�[
	 * SearchPage�̃C���i�[�N���X
	 */
	class PaneMouseListener extends MouseAdapter {
		
		@Override
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			
			// �E�����[�X�̏ꍇ
			if (SwingUtilities.isRightMouseButton(e)) {
				recListPopup(e);
			}
		}
	}
	
	/**
	 * Search Name�{�^�����X�i�[�N���X
	 * SerarchPage�̃C���i�[�N���X�B
	 */
	class BtnSearchNameListener implements ActionListener {
		
		/**
		 * �A�N�V�����C�x���g
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {

			String inputStr = (String)JOptionPane.showInputDialog(null,
					"Please input the Name.", "Search Name",
					JOptionPane.PLAIN_MESSAGE, null, null, saveSearchName);

			// �L�����Z���{�^���A�~�{�^���AEsc�L�[������
			if (inputStr == null) {
				return;
			}

			String searchName = inputStr.trim();
			// �{�^���̐F��ύX
			JButton btn = btnName;
			Color defColor = btn.getBackground();
			btn.setBackground(Color.PINK);
			btn.update(btn.getGraphics());

			// �}�E�X�J�[�\���������v��
			SearchPage.this.setCursor(waitCursor);
			
			// �v���b�g�y�C��������
			queryPlot.clear();
			compPlot.clear();
			resultPlot.clear();
			queryPlot.setPeaks(null, 0);
			compPlot.setPeaks(null, 1);
			resultPlot.setPeaks(null, 0);

			// PackageView������
			pkgView.initAllRecInfo();
			
			// DB Hit�^�u�֘A������
			if (resultTabPane.getTabCount() > 0) {
				resultTabPane.setSelectedIndex(0);
			}
			DefaultTableModel dm1 = (DefaultTableModel) resultSorter.getTableModel();
			dm1.setRowCount(0);
			hitLabel.setText(" ");

			if (searchName.equals("")) {
				// DB�^�u�֘A������
				DefaultTableModel dataModel = (DefaultTableModel) querySorter.getTableModel();
				dataModel.setRowCount(0);
				SearchPage.this.setCursor(Cursor.getDefaultCursor());
				btn.setBackground(defColor);
				return;
			}

			saveSearchName = searchName;

			// �X�y�N�g���擾
			getSpectrumForQuery(searchName);

			// �}�E�X�J�[�\�����f�t�H���g�J�[�\����
			SearchPage.this.setCursor(Cursor.getDefaultCursor());

			// �{�^���̐F��߂�
			btn.setBackground(defColor);
		}
	}

	/**
	 * All�{�^�����X�i�[�N���X
	 * SerarchPage�̃C���i�[�N���X�B
	 */
	class BtnAllListener implements ActionListener {
		
		/**
		 * �A�N�V�����C�x���g
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			// �{�^���̐F��ύX
			JButton btn = btnAll;
			Color defColor = btn.getBackground();
			btn.setBackground(Color.PINK);
			btn.update(btn.getGraphics());

			// �}�E�X�J�[�\���������v��
			SearchPage.this.setCursor(waitCursor);

			// �������̏ꍇ
			if (nameListAll.size() == 0) {
				// �X�y�N�g���擾
				getSpectrumForQuery("");
				nameListAll = new ArrayList(nameList);
			}
			// �������̏ꍇ
			else {
				// �v���b�g�y�C��������
				queryPlot.clear();
				compPlot.clear();
				resultPlot.clear();
				queryPlot.setPeaks(null, 0);
				compPlot.setPeaks(null, 1);
				resultPlot.setPeaks(null, 0);

				// PackageView������
				pkgView.initAllRecInfo();
				
				DefaultTableModel dm = (DefaultTableModel)resultSorter.getTableModel();
				dm.setRowCount(0);
				hitLabel.setText(" ");
				nameList = new ArrayList(nameListAll);
				try {
					DefaultTableModel dataModel = (DefaultTableModel) querySorter.getTableModel();
					queryDbTable.clearSelection();
					dataModel.setRowCount(0);
					for (int i = 0; i < nameListAll.size(); i++) {
						String[] item = (String[]) nameListAll.get(i);
						String id = item[0];
						String name = item[1];
						String site = siteNameList[Integer.parseInt(item[2])];
						String[] idNameSite = new String[] { id, name, site, String.valueOf(i + 1) };
						dataModel.addRow(idNameSite);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			// �}�E�X�J�[�\�����f�t�H���g�J�[�\����
			SearchPage.this.setCursor(Cursor.getDefaultCursor());

			// �{�^���̐F��߂�
			btn.setBackground(defColor);
		}
	}

	/**
	 * �|�b�v�A�b�v���j���[Show Record���X�i�[�N���X
	 * SerarchPage�̃C���i�[�N���X�B
	 */
	class PopupShowRecordListener implements ActionListener {
		
		private JTable eventTbl;	// �C�x���g�����e�[�u��
		
		/**
		 * �R���X�g���N�^
		 * @param eventTbl �C�x���g�����������e�[�u��
		 */
		public PopupShowRecordListener(JTable eventTbl) {
			this.eventTbl = eventTbl;
		}
		
		/**
		 * �A�N�V�����C�x���g
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			showRecordPage(eventTbl);
		}
	}

	/**
	 * �|�b�v�A�b�v���j���[Multiple Display���X�i�[�N���X
	 * SerarchPage�̃C���i�[�N���X�B
	 */
	class PopupMultipleDisplayListener implements ActionListener {
		
		private JTable eventTbl;	// �C�x���g�����e�[�u��
		
		/**
		 * �R���X�g���N�^
		 * @param eventTbl �C�x���g�����������e�[�u��
		 */
		public PopupMultipleDisplayListener(JTable eventTbl) {
			this.eventTbl = eventTbl;
		}
		
		/**
		 * �A�N�V�����C�x���g
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {

			// �I�����ꂽ�s�̃C���f�b�N�X���擾
			int selRows[] = eventTbl.getSelectedRows();

			// CGI�Ăяo��
			try {
				String reqUrl = baseUrl + "jsp/Display.jsp";
				String param = "";

				int idCol = eventTbl.getColumnModel().getColumnIndex(SearchPage.COL_LABEL_ID);
				int nameCol = eventTbl.getColumnModel().getColumnIndex(SearchPage.COL_LABEL_NAME);
				int ionCol = eventTbl.getColumnModel().getColumnIndex(SearchPage.COL_LABEL_ION);
				int siteCol = eventTbl.getColumnModel().getColumnIndex(SearchPage.COL_LABEL_CONTRIBUTOR);
				for (int i = 0; i < selRows.length; i++) {
					int row = selRows[i];
					String name = (String)eventTbl.getValueAt(row, nameCol);
					String id = (String)eventTbl.getValueAt(row, idCol);
					String formula = "";
					String mass = "";
					String ion = (String)eventTbl.getValueAt(row, ionCol);
					name = URLEncoder.encode(name);
					String siteName = (String)eventTbl.getValueAt(row, siteCol);
					String site = "0";
					for (int j = 0; j < siteNameList.length; j++) {
						if (siteName.equals(siteNameList[j])) {
							site = Integer.toString(j);
							break;
						}
					}
					param += "id=" + name + "\t" + id + "\t" + formula + "\t" + mass + "\t"	+ ion + "\t" + site + "&";
				}
				param = param.substring(0, param.length() - 1);

				URL url = new URL(reqUrl);
				URLConnection con = url.openConnection();
				con.setDoOutput(true);
				PrintStream out = new PrintStream(con.getOutputStream());
				out.print(param);
				out.close();
				String line;
				String filename = "";
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				while ((line = in.readLine()) != null) {
					filename += line;
				}
				in.close();

				reqUrl += "?type=Multiple Display&" + "name=" + filename;
				context.showDocument(new URL(reqUrl), "_blank");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * �s�[�N�R���p���[�^
	 * SearchPage�̃C���i�[�N���X�B
	 * m/z�̏����\�[�g���s���B
	 */
	class PeakComparator implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			String mz1 = String.valueOf(o1).split("\t")[0];
			String mz2 = String.valueOf(o2).split("\t")[0];
			return Double.valueOf(mz1).compareTo(Double.valueOf(mz2));
		}
	}
}
