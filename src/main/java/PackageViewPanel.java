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
 * �X�y�N�g���ꊇ�\�� �N���X
 *
 * ver 1.0.7 2011.08.10
 *
 ******************************************************************************/

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import massbank.MassBankCommon;

/**
 * �X�y�N�g���ꊇ�\�� �N���X
 */
@SuppressWarnings("serial")
public class PackageViewPanel extends JPanel {
	
	private final PackageSpecData specData;				// �X�y�N�g�����f�[�^�N���X(blank final�ϐ�)
	
	private final int MARGIN = 15;						// �]��
	private final int INTENSITY_RANGE_MAX = 1000;			// �ő勭�x
	private final int MASS_RANGE_MIN = 5;					// �ŏ��}�X�����W
	
	private int width = 0;									// ��ʕ�
	private int height = 0;								// ��ʍ�
	
	private int maxMoveXPoint = 0;							// x���ő���l
	private int maxMoveYPoint = 0;							// y���ő���l
	private int minMoveXPoint = 0;							// x���ŏ����l
	private int minMoveYPoint = 0;							// y���ŏ����l	
	private int moveXPoint = 0;							// x�����l
	private int moveYPoint = 0;							// y�����l
	private int tmpMoveXPoint = -1;						// ���݂�x�����l(�ޔ�p)
	private int tmpMoveYPoint = -1;						// ���݂�y�����l(�ޔ�p)

	private double massStart = 0;
	private double massRange = 0;
	private int massRangeMax = 0;
	private int intensityRange = 0;
	
	private Point cursorPoint = null;						// �}�E�X�J�[�\���|�C���g
	
	private boolean underDrag = false;					// �h���b�O���t���O
	private Point fromPos = null;							// �h���b�O�J�n�|�C���g
	private Point toPos = null;							// �h���b�O�I���|�C���g
	private boolean isInitRate = false;					// �����{���t���O(true:���g��Afalse:�g�咆)
	
	private Timer animationTimer = null;					// �g�又���p�^�C�}�[�I�u�W�F�N�g
	private Timer moveTimer = null;						// �O���t���p�^�C�}�[�I�u�W�F�N�g
	
	private GridBagConstraints gbc = null;					// ���C�A�E�g����I�u�W�F�N�g
	
	private JButton leftMostBtn = null;					// �X�y�N�g���ړ��{�^��(�ō�)
	private JButton leftBtn = null;						// �X�y�N�g���ړ��{�^��(��)
	private JButton rightBtn = null;						// �X�y�N�g���ړ��{�^��(�E)
	private JButton rightMostBtn = null;					// �X�y�N�g���ړ��{�^��(�ŉE)
	
	private JButton xAxisDown = null;						// x���O���t���{�^��(Down)
	private JButton xAxisUp = null;						// x���O���t���{�^��(Up)
	private JButton yAxisUp = null;						// y���O���t���{�^��(Up)
	private JButton yAxisDown = null;						// y���O���t���{�^��(Down)
	private JToggleButton mzDisp = null;					// show all m/z�{�^��
	private JToggleButton mzMatchDisp = null;				// show match m/z�{�^��
	private JToggleButton chgColor = null;					// change color�{�^��
	private JToggleButton flat = null;						// flat�{�^��
	private JButton topAngleBtn = null;					// �I�[�g�A���O���{�^��(top)
	private JButton sideAngleBtn = null;					// �I�[�g�A���O���{�^��(side)

	private JLabel statusKeyLbl = null;						// �X�e�[�^�X���x��(�L�[)
	private JLabel statusValLbl = null;						// �X�e�[�^�X���x��(�l)
	
	private final Color[] colorTable = new Color[]{
			new Color(51, 102, 153),	// #336699
			new Color(0, 102, 102),		// #006666
			new Color(0, 102, 0), 		// #006600
			new Color(0, 153, 0), 		// #009900
			new Color(102, 153, 0),		// #669900
			new Color(153, 153, 51), 	// #999933			
			new Color(204, 153, 0),		// #CC9900
			new Color(255, 153, 51),	// #FF9933
			new Color(255, 102, 0),		// #FF6600
			new Color(255, 102, 102),	// #FF6666
			new Color(204, 51, 102),	// #CC3366
			new Color(204, 51, 51),		// #CC3333
			new Color(153, 0, 0),		// #990000
			new Color(102, 0, 0),		// #660000
			new Color(51, 0, 51),		// #330033
			new Color(51, 0, 102),		// #330066
			new Color(51, 0, 153),		// #330099
			new Color(51, 51, 153),		// #333399
			new Color(0, 51, 153),		// #003399
			new Color(0, 102, 153)		// #006699
			};												// Color�e�[�u��(Web�Z�[�t�J���[Only)
	
	private int recNum = 0;								// ���R�[�h��
	private TableSorter recSorter = null;					// ���R�[�h���X�g�e�[�u�����f��
	private JTable recTable = null;						// ���R�[�h���X�g�e�[�u��
	private boolean initDispFlag = false;					// �����\���t���O
	
	public static final String QUERY_RECORD = "Query";
	public static final String RESULT_RECORD = "Result";
	public static final String INTEGRATE_RECORD = " / MERGED SPECTRUM";
	
	public static final String TABLE_RECORD_LIST = "RecordList";
	
	private float tolVal = 0.3f;			// Tolerance���͒l
	private boolean tolUnit = true;		// Tolerance�P�ʑI��l�itrue�Funit�Afalse�Fppm�j
	private int pressRowIndex = -1;		// �v���X���̍s�C���f�b�N�X
	private int releaseRowIndex = -1;		// �����[�X���̍s�C���f�b�N�X
	private int dragRowIndex = -1;			// �h���b�O���̍s�C���f�b�N�X
	private boolean isDragCancel = false;	// �h���b�O�L�����Z���t���O
	
	/**
	 * �R���X�g���N�^
	 */
	public PackageViewPanel() {
		
		specData = new PackageSpecData();
		
		// �e�����W������
		initRange(true);
		
		// �R���|�[�l���g�̔z�u
		initComponentLayout();
	}
	
	/**
	 * ���R�[�h��񏉊���(�S���R�[�h)
	 */
	public void initAllRecInfo() {
		specData.initAllData();
		recNum = specData.getRecNum();
		chgColor.setSelected(false);
		flat.setSelected(false);
		allBtnCtrl(false);
		setMassRangeMax();
		setTblData(false, "");
		initRange(true);
	}
	
	/**
	 * ���R�[�h��񏉊���(�������ʃ��R�[�h�̂�)
	 */
	public void initResultRecInfo() {
		specData.initResultData();
		recNum = specData.getRecNum();
		flat.setSelected(false);
		allBtnCtrl(true);
		setMassRangeMax();
		if (recNum > 0) {
			setTblData(true, specData.getRecInfo(0).getId());
		}
		initRange(true);
	}
	
	/**
	 * �N�G���[���R�[�h���ǉ�
	 * @param recData ���R�[�h���
	 */
	public void addQueryRecInfo(PackageRecData recData) {
		specData.initAllData();
		specData.addRecInfo(recData);
	}
	
	/**
	 * �������ʃ��R�[�h���ǉ�
	 * @param recData ���R�[�h���
	 * @param changeRecFlag �������ʃ��R�[�h���ύX�t���O
	 * @param id �I�����R�[�h��ID
	 */
	public void addResultRecInfo(PackageRecData recData, boolean changeRecFlag) {
		if (changeRecFlag) {
			specData.initResultData();
		}
		specData.addRecInfo(recData);
	}
	
	/**
	 * ���R�[�h���ǉ��㏈��
	 * ���R�[�h���ǉ���ɕK���ĂԁB
	 * @param isQuery �N�G���[���R�[�h���ǉ���t���O(true�F�N�G���[�ǉ���Afalse�F���ʒǉ���)
	 * @param selectedId �I�����R�[�hID
	 * @param sortKey �\�[�g�L�[�Ƃ���J������
	 */
	public void addRecInfoAfter(boolean isQuery, String selectedId, int sortKey) {
		recNum = specData.getRecNum();
		if (recNum == 0) {
			return;
		}
		if (isQuery) {
			allBtnCtrl(true);
		}
		else {
			specData.setMatchPeakInfo(tolVal, tolUnit);
			specData.sortRecInfo(sortKey);
			mzDisp.setSelected(false);
			mzMatchDisp.setSelected(true);
		}
		setMassRangeMax();
		setTblData(isQuery, selectedId);
		initRange(true);
	}
	
	/**
	 * ���R�[�h���X�g�e�[�u���̃��R�[�h�I����Ԑݒ�
	 * @param selectionQuery �N�G���[���R�[�h�t���O(true�F�N�G���[���R�[�h�Afalse�F�N�G���[���R�[�h�ȊO)
	 * @param selectionId �I�����R�[�hID
	 */
	public void setTblSelection(boolean selectionQuery, String selectionId) {
		int idCol = recTable.getColumnModel().getColumnIndex(SearchPage.COL_LABEL_ID);
		int typeCol = recTable.getColumnModel().getColumnIndex(SearchPage.COL_LABEL_TYPE);
		// ���ɑI������Ă���Ώ������Ȃ�
		String selectedQuery = String.valueOf(recTable.getValueAt(recTable.getSelectedRow(), typeCol));
		String selectedId = String.valueOf(recTable.getValueAt(recTable.getSelectedRow(), idCol));

		if ((selectionQuery == selectedQuery.equals(QUERY_RECORD)
				|| selectionQuery == selectedQuery.equals(QUERY_RECORD + INTEGRATE_RECORD))
				&& selectionId.equals(selectedId)) {
			return;
		}
		
		// �I������
		for (int i=0; i<recTable.getRowCount(); i++) {
			if ((selectionQuery == recTable.getValueAt(i, typeCol).equals(QUERY_RECORD)
					|| selectionQuery == recTable.getValueAt(i, typeCol).equals(QUERY_RECORD + INTEGRATE_RECORD))
					&& selectionId.equals(recTable.getValueAt(i, idCol))) {
				recTable.setRowSelectionInterval(i, i);
				break;
			}
		}
	}
	
	/**
	 * Tolerance���͒l�ݒ�
	 * @param val tolerance�l
	 * @param unit Tolerance�P�ʁitrue�Funit�Afalse�Fppm�j
	 */
	public void setTolerance(String val, boolean unit) {
		this.tolVal = Float.parseFloat(val);
		this.tolUnit = unit;
	}
	
	/**
	 * massRangeMax�ݒ�
	 * ���R�[�h���ɕύX���������ꍇ�ɌĂԂƁA
	 * ���݂̃��R�[�h���ň�ԑ傫��m/z�����X�y�N�g����m/z���\���ł���悤��
	 * �X�y�N�g���̃}�X�����W��ݒ肷��B
	 */
	private void setMassRangeMax() {
		massRangeMax = 0;
		PackageRecData recData = null;
		int tmpMassRange = 0;
		for (int i=0; i<recNum; i++) {
			recData = specData.getRecInfo(i);
			// m/z�̍ő�l��100�̈�(������2��)�Ő؂�グ���l�������W�Ƃ���
			tmpMassRange = new BigDecimal(
					String.valueOf(recData.compMaxMzPrecusor())).setScale(-2, BigDecimal.ROUND_UP).intValue();
			// m/z�̍ő�l��100�Ŋ���؂��ꍇ�̓����W��+100����
			if (recData.compMaxMzPrecusor() % 100d == 0d) {
				tmpMassRange += 100;
			}
			if (massRangeMax < tmpMassRange) {
				massRangeMax = tmpMassRange;
			}
		}
	}
	
	/**
	 * �e�����W������
	 * @param initAngle �A���O���������t���O�itrue�F�������Afalse�F�ێ��j
	 */
	private void initRange(boolean initAngle) {

		isInitRate = true;
		massRange = massRangeMax;
		massStart = 0;
		intensityRange = INTENSITY_RANGE_MAX;
		initDispFlag = initAngle;
		
		this.repaint();
	}
	
	/**
	 * �R���|�[�l���g�z�u
	 * �R���|�[�l���g�̃��C�A�E�g���w�肵�Ĕz�u����B
	 */
	private void initComponentLayout() {
		
		// �}�E�X�J�[�\���ݒ�
		PackageViewPanel.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		// ���C�A�E�g�w��
		setLayout(new BorderLayout());
		GridBagLayout gbl = new GridBagLayout();
		
		// �X�y�N�g���\���y�C��
		SpectrumPlotPane plotPane = new SpectrumPlotPane();
		plotPane.setMinimumSize(new Dimension(0, 180));
		plotPane.repaint();
		gbc = new GridBagConstraints();						// ���C�A�E�g���񏉊���
		gbc.fill = GridBagConstraints.BOTH;					// �����A�����T�C�Y�̕ύX������
		gbc.weightx = 1;									// �]���̐����X�y�[�X�𕪔z
		gbc.weighty = 1;									// �]���̐����X�y�[�X�𕪔z
		gbc.gridwidth = GridBagConstraints.REMAINDER;		// ��Ō�̃R���|�[�l���g�Ɏw��
		gbl.setConstraints(plotPane, gbc);
		
		// �{�^���y�C��
		ButtonPane btnPane = new ButtonPane();
		btnPane.setMinimumSize(new Dimension(0, 44));
		btnPane.setLayout( new FlowLayout(FlowLayout.LEFT, 0, 0) );
		gbc = new GridBagConstraints();						// ���C�A�E�g���񏉊���
		gbc.fill = GridBagConstraints.HORIZONTAL;			// �����T�C�Y�̕ύX�݂̂�����
		gbc.weightx = 1;									// �]���̐����X�y�[�X�𕪔z
		gbc.weighty = 0;									// �]���̐����X�y�[�X�𕪔z���Ȃ�
		gbc.gridwidth = GridBagConstraints.REMAINDER;		// ��Ō�̃R���|�[�l���g�Ɏw��
		gbl.setConstraints(btnPane, gbc);
		
		// �X�e�[�^�X���x��
		statusKeyLbl = new JLabel(" ");
		gbc = new GridBagConstraints();						// ���C�A�E�g���񏉊���
		gbc.fill = GridBagConstraints.HORIZONTAL;			// �����T�C�Y�̕ύX�݂̂�����
		gbc.weightx = 0;									// �]���̐����X�y�[�X�𕪔z���Ȃ�
		gbc.weighty = 0;									// �]���̐����X�y�[�X�𕪔z���Ȃ�
		gbc.gridheight = GridBagConstraints.REMAINDER;		// �s�Ō�̃R���|�[�l���g�Ɏw��
		gbc.insets = new Insets(4, 4, 4, 4);				// �O���p�f�B���O�w��
		gbl.setConstraints(statusKeyLbl, gbc);
		statusValLbl = new JLabel(" ");
		statusValLbl.setForeground(new Color(0, 139, 139));
		gbc = new GridBagConstraints();						// ���C�A�E�g���񏉊���
		gbc.fill = GridBagConstraints.HORIZONTAL;			// �����T�C�Y�̕ύX�݂̂�����
		gbc.weightx = 1;									// �]���̐����X�y�[�X�𕪔z
		gbc.weighty = 0;									// �]���̐����X�y�[�X�𕪔z���Ȃ�
		gbc.gridwidth = GridBagConstraints.REMAINDER;		// ��Ō�̃R���|�[�l���g�Ɏw��
		gbc.gridheight = GridBagConstraints.REMAINDER;		// �s�Ō�̃R���|�[�l���g�Ɏw��
		gbc.insets = new Insets(4, 4, 4, 4);				// �O���p�f�B���O�w��
		gbl.setConstraints(statusValLbl, gbc);
		
		// �\���p�l���ǉ�
		JPanel dispPanel = new JPanel();
		dispPanel.setLayout(gbl);
		dispPanel.add(plotPane);
		dispPanel.add(btnPane);
		dispPanel.add(statusKeyLbl);
		dispPanel.add(statusValLbl);	
		add(dispPanel);
		
		// ���R�[�h���X�g�y�C���ǉ�
		recTable = createRecListTable();
		JScrollPane recListPane = new JScrollPane(recTable);
		recListPane.addMouseListener(new PaneMouseListener());
		recListPane.setMinimumSize(new Dimension(0, 100));
		
		// PackageViewPanel�y�C���ǉ�
		JSplitPane pkgPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dispPanel, recListPane);
		pkgPane.setMinimumSize(new Dimension(350, 0));
		pkgPane.setDividerLocation((int)(SearchPage.initAppletHight * 0.7));
		pkgPane.setOneTouchExpandable(true);
		add(pkgPane, BorderLayout.CENTER);
	}
	
	/**
	 * ���R�[�h���X�g�e�[�u���쐬
	 * @return ���R�[�h���X�g�e�[�u��
	 */
	private JTable createRecListTable() {
		
		recSorter = new TableSorter(new DefaultTableModel(), specData);
		JTable t = new JTable(recSorter) {
			@Override
			public boolean isCellEditable(int row, int column) {
				super.isCellEditable(row, column);
				// �Z���ҏW��s�Ƃ���
				return false;
			}
			
			@Override
			public void setValueAt(Object value, int row, int col) {
				super.setValueAt(value, row, col);
				// �`�F�b�N�{�b�N�X���ҏW���ꂽ�ꍇ�ɍĕ`����s��
				if (recTable.getColumnName(col).equals(SearchPage.COL_LABEL_DISABLE)) {
					PackageRecData recData = specData.getRecInfo(row);
					recData.setDisable(Boolean.parseBoolean(String.valueOf(recTable.getValueAt(row, col))));
					specData.setMatchPeakInfo(tolVal, tolUnit);
					PackageViewPanel.this.repaint();
					
					int hitCol = recTable.getColumnModel().getColumnIndex(SearchPage.COL_LABEL_HIT);
					for (int i=0; i<recTable.getRowCount(); i++) {
						// �ҏW���ꂽ���R�[�h�ɂ����Hit�J�����̒l���A�b�v�f�[�g����
						setValueAt(specData.getRecInfo(i).getHitPeakNum(), i, hitCol);
					}
				}
			}
			
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				Graphics2D g2 = (Graphics2D)g;
				Rectangle2D area = new Rectangle2D.Float();
				
				// �h���b�O���Ƀh���b�O��h��Ԃ��\��
				if (!isSortStatus() && dragRowIndex >= 0) {
					
					g2.setPaint(Color.RED);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
					
					area.setRect(0, (dragRowIndex * getRowHeight()), getWidth(), getRowHeight());
					g2.fill(area);
				}
				
				// change color�̏ꍇ�̓I�[�_�[�J�����̍s��h��Ԃ��\��
				if (chgColor.isSelected()) {
					
					for (int i=0; i<recTable.getRowCount(); i++) {
						// ��\���̏ꍇ�͐F�Â����Ȃ�
						if (Boolean.parseBoolean(String.valueOf(getValueAt(i, getColumnModel().getColumnIndex(SearchPage.COL_LABEL_DISABLE))))) {
							continue;
						}
						g2.setPaint(getColor(i));
						g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
						
						area.setRect(getCellRect(i, getColumnModel().getColumnIndex(SearchPage.COL_LABEL_ORDER), false));
						g2.fill(area);
					}
				}
			}
		};
		recSorter.setTableHeader(t.getTableHeader());
		t.setMinimumSize(new Dimension(400, 400));
		t.setRowSelectionAllowed(true);
		t.setColumnSelectionAllowed(false);
		t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		t.addMouseListener(new TblMouseListener());						// ���X�i�[�ǉ�
		t.addMouseMotionListener(new TblMouseMotionListener());			// ���X�i�[�ǉ�
		t.addKeyListener(new TblKeyListener());							// �L�[���X�i�[�ǉ�
		t.setDefaultRenderer(Object.class, new TblRenderer());			// �I���W�i�������_���[
		ListSelectionModel lm = t.getSelectionModel();
		lm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lm.addListSelectionListener(new LmSelectionListener());

		
		// �J�����Z�b�g
		String[] columnLabel = {
				SearchPage.COL_LABEL_ORDER, SearchPage.COL_LABEL_TYPE, SearchPage.COL_LABEL_NAME,
				SearchPage.COL_LABEL_MATCH, SearchPage.COL_LABEL_ID, SearchPage.COL_LABEL_DISABLE, 
				SearchPage.COL_LABEL_CONTRIBUTOR, SearchPage.COL_LABEL_SCORE, SearchPage.COL_LABEL_HIT,
				SearchPage.COL_LABEL_PEAK, SearchPage.COL_LABEL_PRECURSOR };
		DefaultTableModel model = (DefaultTableModel)recSorter.getTableModel();
		model.setColumnIdentifiers(columnLabel);

		// �񕝃Z�b�g
		t.getColumn(t.getColumnName(0)).setPreferredWidth(36);
		t.getColumn(t.getColumnName(1)).setPreferredWidth(75);
		t.getColumn(t.getColumnName(2)).setPreferredWidth(360);
		t.getColumn(t.getColumnName(3)).setPreferredWidth(36);
		t.getColumn(t.getColumnName(4)).setPreferredWidth(70);
		t.getColumn(t.getColumnName(5)).setPreferredWidth(47);
		t.getColumn(t.getColumnName(6)).setPreferredWidth(70);
		t.getColumn(t.getColumnName(7)).setPreferredWidth(70);
		t.getColumn(t.getColumnName(8)).setPreferredWidth(20);
		t.getColumn(t.getColumnName(9)).setPreferredWidth(36);
		t.getColumn(t.getColumnName(10)).setPreferredWidth(58);

		return t;
	}
	
	/**
	 * ���R�[�h���X�g�e�[�u���f�[�^�ݒ�
	 * @param selectedQuery �N�G���[���R�[�h�I���t���O(true�F�N�G���[�ǉ���Afalse�F���ʒǉ���)
	 * @param id �I�����R�[�h��ID
	 */
	private void setTblData(boolean selectedQuery, String selectedId) {
		
		// �\�[�g����
		for (int i=0; i<recTable.getColumnCount(); i++) {
			recSorter.setSortingStatus(i, TableSorter.NOT_SORTED);
		}
		
		DefaultTableModel dataModel = (DefaultTableModel)recSorter.getTableModel();
		dataModel.setRowCount(0);
		
		if (recNum == 0) {
			return;
		}
		
		PackageRecData recData = null;
		Object[] recordData;
		int number = recNum;
		for (int i=0; i<recNum; i++) {
			recData = specData.getRecInfo(i);
			
			// ���R�[�h��񐶐�
			recordData = new Object[dataModel.getColumnCount()];
			recordData[0] = String.valueOf(number--);
			if (recData.isQueryRecord()) {
				recordData[1] = QUERY_RECORD;
			}
			else if (recData.isResultRecord()) {
				recordData[1] = RESULT_RECORD;
			}
			if (recData.isIntegRecord()) {
				recordData[1] = recordData[1] + INTEGRATE_RECORD;
			}
			recordData[2] = recData.getName();
			recordData[3] = recData.getMatchPeakNum();
			recordData[4] = recData.getId();
			recordData[5] = new Boolean(recData.isDisable());
			if (!recData.getSite().equals("")) {
				recordData[6] = SearchPage.siteNameList[Integer.parseInt(recData.getSite())];
			}
			else {
				recordData[6] = "";
			}
			recordData[7] = recData.getScore();
			recordData[8] = recData.getHitPeakNum();
			recordData[9] = String.valueOf(recData.getPeakNum());
			recordData[10] = recData.getPrecursor();
			
			// �e�[�u���ւ̃��R�[�h�ǉ�
			dataModel.addRow(recordData);
			
			if (selectedQuery == recData.isQueryRecord() && selectedId.equals(recData.getId())) {
				// �N�G���[DB�^�u�ADB Hit�^�u�ł̑I�����R�[�h�������I��
				recTable.setRowSelectionInterval(i, i);
			}
		}
	}
	
	/**
	 * ���R�[�h���X�g�e�[�u���\����Ԉꊇ�ݒ�
	 * @param disable �\�����(true�F�\���Afalse�F��\��)
	 */
	private void setTblDispStatus(boolean disable) {
    	if (recTable.getRowCount() > 0) {
    		int disableCol = recTable.getColumnModel().getColumnIndex(SearchPage.COL_LABEL_DISABLE);
			for (int i=0; i<recTable.getRowCount(); i++) {
				recTable.setValueAt(new Boolean(disable), i, disableCol);
			}
    	}
	}
	
	/**
	 * ���R�[�h���X�g�e�[�u���\�[�g��Ԏ擾
	 * ���R�[�h���X�g�e�[�u���̂����ꂩ�̃J�������\�[�g����Ă��邩���擾����B
	 * @return �\�[�g���(true�F�\�[�g���Afale�F�f�t�H���g)
	 */
	private boolean isSortStatus() {
    	if (recTable.getRowCount() > 0) {
			int sortStatus = TableSorter.NOT_SORTED;
			for (int i=0; i<recTable.getColumnCount(); i++) {
				sortStatus = recSorter.getSortingStatus(i);
				if (sortStatus != TableSorter.NOT_SORTED) {
					return true;
				}
			}
    	}
    	return false;
	}
	
	/**
	 * ���R�[�h���X�g�p�|�b�v�A�b�v�\��
	 * @param e �}�E�X�C�x���g
	 */
	private void recListPopup(MouseEvent e) {
		final int selRow = recTable.getSelectedRow();
		final int rowCnt = recTable.getRowCount();
		
		JMenuItem item1 = new JMenuItem("Show Record");
		item1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showRecordPage(selRow);
			}
		});
		int siteCol = recTable.getColumnModel().getColumnIndex(SearchPage.COL_LABEL_CONTRIBUTOR);
		if (selRow == -1 || String.valueOf(recTable.getValueAt(selRow, siteCol)).equals("")) {
			item1.setEnabled(false);
		}
		
		JSeparator sep = new JSeparator();

		JMenuItem item2 = new JMenuItem("All Disable");
		item2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setTblDispStatus(true);
			}
		});
		if (rowCnt == 0) {
			item2.setEnabled(false);
		}
		
		JMenuItem item3 = new JMenuItem("All Enable");
		item3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setTblDispStatus(false);
			}
		});
		if (rowCnt == 0) {
			item3.setEnabled(false);
		}
		
		// �|�b�v�A�b�v�\��
		JPopupMenu showRecPopup = new JPopupMenu();
		showRecPopup.add(item1);
		showRecPopup.add(sep);
		showRecPopup.add(item2);
		showRecPopup.add(item3);
		showRecPopup.show(e.getComponent(), e.getX(), e.getY());
	}
	
	/**
	 * m/z�̕\���p�t�H�[�}�b�g
	 * ��ʕ\���p��m/z�̌��������킹�ĕԋp����
	 * @param mass �t�H�[�}�b�g�Ώۂ�m/z
	 * @param isForce ������������t���O�itrue:0���߂Ɛ؎̂Ă��s���Afalse:�؎̂Ă̂ݍs���j
	 * @return �t�H�[�}�b�g���m/z
	 */
	private String formatMass(double mass, boolean isForce) {
		final int ZERO_DIGIT = 4;
		String massStr = String.valueOf(mass);
		if (isForce) {
			// �����I�ɑS�Ă̌��𓝈ꂷ��i0���߂Ɛ؎̂Ă��s���j
			if (massStr.indexOf(".") == -1) {
				massStr += ".0000";
			}
			else {
				if (massStr.indexOf(".") != -1) {
					String [] tmpMzStr = massStr.split("\\.");
					if (tmpMzStr[1].length() <= ZERO_DIGIT) {
						int addZeroCnt = ZERO_DIGIT - tmpMzStr[1].length();
						for (int j=0; j<addZeroCnt; j++) {
							massStr += "0";
						}
					}
					else {
						if (tmpMzStr[1].length() > ZERO_DIGIT) {
							massStr = tmpMzStr[0] + "." + tmpMzStr[1].substring(0, ZERO_DIGIT);
						}
					}
				}
			}
		}
		else {
			// ���𒴂���ꍇ�̂݌��𓝈ꂷ��i�؎̂Ă̂ݍs���j
			if (massStr.indexOf(".") != -1) {
				String [] tmpMzStr = massStr.split("\\.");
				if (tmpMzStr[1].length() > ZERO_DIGIT) {
					massStr = tmpMzStr[0] + "." + tmpMzStr[1].substring(0, ZERO_DIGIT);
				}
			}
		}
		return massStr;
	}
	
	/**
	 * Color�e�[�u������̐F�擾
	 * Color�e�[�u������F���擾���ĕԋp�B
	 * Color�e�[�u�����̐F���g���܂킷�B
	 * @param index �C���f�b�N�X
	 * @return Color�I�u�W�F�N�g
	 */
	private Color getColor(int index) {
		return colorTable[index % colorTable.length];
	}

	/**
	 * �S�{�^���L����������
	 * @param enable �L������
	 */
	private void allBtnCtrl(boolean enable) {
		
		if (enable) {
			// �X�y�N�g���ړ��{�^��
			if (!isInitRate) {
				leftMostBtn.setEnabled(true);
				leftBtn.setEnabled(true);
				rightBtn.setEnabled(true);
				rightMostBtn.setEnabled(true);
			}
			else {
				leftMostBtn.setEnabled(false);
				leftBtn.setEnabled(false);
				rightBtn.setEnabled(false);
				rightMostBtn.setEnabled(false);
			}
		}
		else {
			leftMostBtn.setEnabled(false);
			leftBtn.setEnabled(false);
			rightBtn.setEnabled(false);
			rightMostBtn.setEnabled(false);
			mzDisp.setSelected(false);
		}
		
		mzDisp.setEnabled(enable);
		mzMatchDisp.setEnabled(enable);
		if (recNum <= 1) {
			mzMatchDisp.setSelected(false);
		}
		chgColor.setEnabled(enable);
		xAxisUp.setEnabled(enable);
		xAxisDown.setEnabled(enable);
		yAxisUp.setEnabled(enable);
		yAxisDown.setEnabled(enable);
		flat.setEnabled(enable);
		topAngleBtn.setEnabled(enable);
		sideAngleBtn.setEnabled(enable);
	}
	
	/**
	 * ���R�[�h�y�[�W�\��
	 * @param selectIndex �I���s�C���f�b�N�X
	 */
	private void showRecordPage(int selectIndex) {
		String id = specData.getRecInfo(selectIndex).getId();
		String site = specData.getRecInfo(selectIndex).getSite();

		String typeName = MassBankCommon.CGI_TBL[MassBankCommon.CGI_TBL_NUM_TYPE][MassBankCommon.CGI_TBL_TYPE_DISP];
		String reqUrl = SearchPage.baseUrl + "jsp/" + MassBankCommon.DISPATCHER_NAME
				+ "?type=" + typeName + "&id=" + id + "&site=" + site;
		try {
			SearchPage.context.showDocument(new URL(reqUrl), "_blank");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * �X�y�N�g���\���y�C��
	 * PackageViewPanel�̃C���i�[�N���X
	 */
	class SpectrumPlotPane extends JPanel implements MouseListener, MouseMotionListener {
				
		private long lastClickedTime = 0;									// �Ō�ɃN���b�N��������
		
		private boolean isDrag = false;									// �h���b�O�C�x���g�L�������t���O
		private int[] dragArea = null;									// �h���b�O�͈͕ێ��p
		
		private final String LT = "LeftTop";								// �����\���萔
		private final String LB = "LeftBottom";							// ������\���萔
		private final String RT = "RightTop";								// �E���\���萔
		private final String RB = "RightBottom";							// �E����\���萔
		
		private HashMap<String, Point> prevGraphArea = 
			new HashMap<String, Point>(4);									// ��Ԏ�O�̃O���t�G���A�͈͕ێ��p
		
		private final Color gridColor1 = Color.LIGHT_GRAY;					// �O���b�h���C���F
		private final Color gridColor2 = new Color(235, 235, 235);		// �O���b�h�T�u�F
		private final Color graphColorBack = new Color(248, 248, 253);	// �O���t�w�ʐF
		private final Color graphColorSide = new Color(240, 240, 255);	// �O���t���ʐF
		private final Color graphColorBottom = new Color(250, 250, 250);	// �O���t��ʐF
		
		private final Color selectRecColor = new Color(153, 51, 255);		// ���R�[�h�I��F
		private final Color onCursorColor = Color.BLUE;					// �J�[�\����F
		
		private double xscale = 0d;											// x�������g�嗦
		private double yscale = 0d;											// y�������g�嗦
		
		private int marginTop = MARGIN;									// �㕔�]��
		private int marginRight = MARGIN;									// �E���]��
		
		private JPopupMenu contextPopup = null;							// �R���e�L�X�g�|�b�v�A�b�v���j���[
		private JPopupMenu selectPopup = null;				 				// �s�[�N�I���|�b�v�A�b�v���j���[
		
		private ArrayList<String> overCursorMz = null;						// �J�[�\����s�[�Nm/z���X�g
		
		
		/**
		 * �f�t�H���g�R���X�g���N�^
		 */
		public SpectrumPlotPane() {
			cursorPoint = new Point();
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		
		/**
		 * x���ڐ��蕝�v�Z
		 * @param range �}�X�����W
		 */
		private int stepCalc(int range) {
			
			if (range < 10) {
				return 1;
			}
			if (range < 20) {
				return 2;
			}
			if (range < 50) {
				return 5;
			}
			if (range < 100) {
				return 10;
			}
			if (range < 250) {
				return 25;
			}
			if (range < 500) {
				return 50;
			}
			return 100;
		}
		
		/**
		 * �O���t�̃A���O���Ɋւ���ݒ�
		 */
		private void setAngleProperty() {
			
			// �����\���A���O���ݒ�
			if (initDispFlag) {
				if (recNum <= 1 ) {
					maxMoveXPoint = 0;
					minMoveXPoint = 0;
					moveXPoint = 0;
					maxMoveYPoint = 0;
					minMoveYPoint = 0;
					moveYPoint = 0;
					return;
				}
				
				// x���������l
				if (width == 0) {
					moveXPoint = 0;
				}
				else {
					moveXPoint = (int)((width - (MARGIN * 2)) * 0.2) / (recNum - 1);
				}
				
				// y���������l
				if (height == 0) {
					moveYPoint = 0;
				}
				else {
					moveYPoint = (int)((height - (MARGIN * 2)) * 0.8) / (recNum - 1);
				}
				
				initDispFlag = false;
			}
			
			
			// x���ő�y�эŏ����l�̐ݒ�
			if (recNum > 1) {
				maxMoveXPoint = (int)((width - (MARGIN * 2)) * 0.8) / (recNum - 1);
				if (0 > maxMoveXPoint) {
					maxMoveXPoint = 0;
				}
			
				minMoveXPoint = (int)((width - (MARGIN * 2)) * 0.1) / (recNum - 1);
				if (0 > minMoveXPoint) {
					minMoveXPoint = 0;
				}
			}
			else {
				maxMoveXPoint = 0;
				minMoveXPoint = 0;
			}
				
			// x�����l�͈̔̓`�F�b�N
			if (moveXPoint > maxMoveXPoint) {
				moveXPoint = maxMoveXPoint;
			}
			else if (moveXPoint < minMoveXPoint) {
				if (moveTimer == null
						|| (!moveTimer.isRunning() && !flat.isSelected())) {
					moveXPoint = minMoveXPoint;
				}
			}

			// y���ő�y�эŏ����l�̐ݒ�
			if (recNum > 1) {
				maxMoveYPoint = (int)((height - (MARGIN * 2)) * 0.9) / (recNum - 1);
				if (0 > maxMoveYPoint) {
					maxMoveYPoint = 0;
				}
				
				minMoveYPoint = (int)((height - (MARGIN * 2)) * 0.2) / (recNum - 1);
				if (0 > minMoveYPoint) {
					minMoveYPoint = 0;
				}
			}
			else {
				maxMoveYPoint = 0;
				minMoveYPoint = 0;
			}
				
			// y�����l�͈̔̓`�F�b�N
			if (moveYPoint > maxMoveYPoint) {
				moveYPoint = maxMoveYPoint;
			}
			else if (moveYPoint < minMoveYPoint) {
				if (moveTimer == null
						|| (!moveTimer.isRunning() && !flat.isSelected())) {
					moveYPoint = minMoveYPoint;
				}
			}
		}
		
		/**
		 * �X�e�[�^�X���x��������ݒ�
		 */
		private void setStatusLabel() {
			if (specData.getSelectedPeakNum() != 0) {
				StringBuffer sb = new StringBuffer();
				Iterator<Double> ite = specData.getSelectedPeakList().iterator();
				while (ite.hasNext()) {
					sb.append(String.valueOf(ite.next()) + ",  ");
				}
				statusKeyLbl.setText("Selected m/z :");
				statusValLbl.setText(sb.toString().substring(0, sb.length()-3));
			}
			else {
				statusKeyLbl.setText(" ");
				statusValLbl.setText(" ");
			}
		}
		
		/**
		 * �y�C���g�R���|�[�l���g
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		public void paintComponent(Graphics g) {
			
			super.paintComponent(g);
			
			final FontUIResource font1 = new FontUIResource(
					g.getFont().getFamily(),g.getFont().getStyle(),14);	// �t�H���g1
			final FontUIResource font2 = new FontUIResource(
					g.getFont().getFamily(),g.getFont().getStyle(),9);	// �t�H���g2
			final FontUIResource font3 = new FontUIResource(
					g.getFont().getFamily(),g.getFont().getStyle(),12);	// �t�H���g3
			
			// ��ʃT�C�Y�Đݒ�
			width = getWidth();
			height = getHeight();
			
			// �O���t�̉��Ɋւ���ݒ�̍Đݒ�i��ʃ��T�C�Y�ɑΉ����邽�߂̏����j
			setAngleProperty();
			
			// �`��G���A�S�̂̔w�i�`��
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, height);
			
			// x�������g�嗦�̎Z�o
//			xscale = (width - 2.0f * MARGIN) / massRange;
			xscale = (width - (2.0d * MARGIN) - ((recNum-1) * moveXPoint)) / massRange;
			if (xscale < 0d) {
				xscale = 0d;
			}
			
			// y�������g�嗦�̎Z�o
//			float yscale = (height - (float)(MARGIN +marginTop) ) / intensityRange;
			yscale = (height - (double)(MARGIN + marginTop + (recNum-1) * moveYPoint) ) / intensityRange;
			if (yscale < 0d) {
				yscale = 0d;
			}
			
			int tmpMarginTop = 0;
			int tmpMarginRight = 0;
			
			int baseX = 0;			// x����y���̌�_��x���W
			int baseY = 0;			// x����y���̌�_��y���W
			int nextBaseX = 0;		// �����R�[�h��x����y���̌�_��x���W
			int nextBaseY = 0;		// �����R�[�h��x����y���̌�_��y���W
			
			int loopCount = ((recNum != 0) ? recNum : (recNum + 1));
			
			// �O���t�w�i�F��`��
			int[] sideXPlots = new int[4];		// �O���t���ʗpx��
			int[] sideYPlots = new int[4];		// �O���t���ʗpy��
			int[] backXPlots = new int[4];		// �O���t�w�ʗpx��
			int[] backYPlots = new int[4];		// �O���t�w�ʗpy��
			int[] bottomXPlots = new int[4];	// �O���t��ʗpx��
			int[] bottomYPlots = new int[4];	// �O���t��ʗpy��
			for (int i=0; i<loopCount; i++) {
				
				baseX = MARGIN + (loopCount-1-i) * moveXPoint;
				baseY = height - MARGIN - (loopCount-1-i) * moveYPoint;

				nextBaseX = baseX - moveXPoint;
				nextBaseY = baseY + moveYPoint;
				
				tmpMarginTop = marginTop + (i * moveYPoint);
				tmpMarginRight = marginRight + (i * moveXPoint);
				if (tmpMarginTop > baseY) {
					tmpMarginTop = baseY;
				}
				if (width - tmpMarginRight < baseX) {
					tmpMarginRight = (width - baseX > 0) ? (width - baseX) : 0;
				}
				
				if ( i == 0 ) {
					sideXPlots[0] = baseX;
					sideYPlots[0] = tmpMarginTop;
					sideXPlots[1] = baseX;
					sideYPlots[1] = baseY;
					
					backXPlots[0] = baseX;
					backYPlots[0] = tmpMarginTop;
					backXPlots[1] = width - tmpMarginRight;
					backYPlots[1] = tmpMarginTop;
					backXPlots[2] = width - tmpMarginRight;
					backYPlots[2] = baseY;
					backXPlots[3] = baseX;
					backYPlots[3] = baseY;
					
					bottomXPlots[0] = baseX;
					bottomYPlots[0] = baseY;
					bottomXPlots[1] = ((width - tmpMarginRight > baseX) ? (width - tmpMarginRight) : baseX);
					bottomYPlots[1] = baseY;
				}
				if ( i == (loopCount-1) ) {
					sideXPlots[2] = baseX;
					sideYPlots[2] = baseY;
					sideXPlots[3] = baseX;
					sideYPlots[3] = tmpMarginTop;
					
					bottomXPlots[2] = width - tmpMarginRight;
					bottomYPlots[2] = baseY;
					bottomXPlots[3] = baseX;
					bottomYPlots[3] = baseY;
				}
			}
			g.setColor(graphColorBack);
			g.fillPolygon(backXPlots, backYPlots, 4);		// �w��
			
			g.setColor(graphColorSide);
			g.fillPolygon(sideXPlots, sideYPlots, 4);		// ����
			
			g.setColor(graphColorBottom);
			g.fillPolygon(bottomXPlots, bottomYPlots, 4);	// ���
			
			
			
			// �O���t�O���b�h�`��
			g.setFont(font2);
			for (int i=0; i<loopCount; i++) {
 				g.setColor(Color.LIGHT_GRAY);
				
				baseX = MARGIN + (loopCount-1-i) * moveXPoint;
				baseY = height - MARGIN - (loopCount-1-i) * moveYPoint;
				
				nextBaseX = baseX - moveXPoint;
				nextBaseY = baseY + moveYPoint;
				
				tmpMarginTop = marginTop + (i * moveYPoint);
				tmpMarginRight = marginRight + (i * moveXPoint);
				if (tmpMarginTop > baseY) {
					tmpMarginTop = baseY;
				}
				if (width - tmpMarginRight < baseX) {
					tmpMarginRight = (width - baseX > 0) ? (width - baseX) : 0;
				}
				
				int step = stepCalc((int)massRange);
				int start = (step - (int)massStart % step) % step;
				
				// x��
				for (int j=start; j<(int)massRange; j+=step) {
					g.setColor(gridColor2);
					// x������ɃO���t�w��y���O���b�h�`��
					if ( i == 0 ) {
						g.drawLine(baseX + (int)(j * xscale),
								   baseY,
								   baseX + (int)(j * xscale),
								   tmpMarginTop);
					}
					
					// x������ɃO���t���z���O���b�h�`��
					if ( i != (loopCount-1) ) {
						g.drawLine(baseX + (int)(j * xscale),
								   baseY,
								   nextBaseX + (int)(j * xscale),
								   nextBaseY);
					}
					
					g.setColor(gridColor1);
					// �O���t�x���ڐ���`��
					g.drawLine(baseX + (int)(j * xscale),
							   baseY,
							   baseX + (int)(j * xscale),
							   baseY + 2);
					
					// �O���t�x���ւ�m/z������`��
					if (i == (loopCount-1)) {
						g.drawString(formatMass(j + massStart, true),
									 baseX + (int)(j * xscale) - 5,
									 height - 1);
					}
				}
				
				// y��
				for (int j=0; j<=intensityRange; j += intensityRange / 5) {
					g.setColor(gridColor2);
					// y������ɃO���t�w��x���O���b�h�`��
					if ( i == 0 ) {
						g.drawLine(baseX,
								   baseY - (int)(j * yscale),
								   width - tmpMarginRight,
								   baseY - (int)(j * yscale));
					}
					
					// y������ɃO���t����z���O���b�h�`��
					if ( i != (loopCount-1) ) {
						g.drawLine(baseX,
								   baseY - (int)(j * yscale),
								   nextBaseX,
								   nextBaseY - (int)(j * yscale));
					}
					
					g.setColor(gridColor1);
					// �O���t�y���ڐ���`��
					g.drawLine(baseX - 2,
							   baseY - (int)(j * yscale),
							   baseX,
							   baseY - (int)(j * yscale));
					
					// �O���t�y���ւ̋��x������`��
					if (i == (loopCount-1)) {
						g.drawString(String.valueOf(j),	
									 0,
									 baseY - (int)(j * yscale));
					}
				}
				
				
				// ��v�O���b�h�`��
				g.setColor(gridColor1);
				if ( i != (loopCount-1) ) {
					g.drawLine(baseX,
							   baseY,
							   nextBaseX,
							   nextBaseY);									// z��
				}
				if (recTable != null && i == recTable.getSelectedRow()) {
					g.setColor(selectRecColor.darker());
				}
				else {
					g.setColor(gridColor1);
				}
				g.drawLine(baseX, tmpMarginTop, baseX, baseY);				// y��
				g.drawLine(baseX, baseY, width - tmpMarginRight, baseY);	// x��
				
				
				// ��Ԏ�O�̃O���t�G���A�͈̔͂�ێ�
				if (i == (recNum-1)) {
					prevGraphArea.put(LB, new Point(baseX, baseY));
					prevGraphArea.put(LT, new Point(baseX, tmpMarginTop));
					prevGraphArea.put(RT, new Point(width - tmpMarginRight, tmpMarginTop));
					prevGraphArea.put(RB, new Point(width - tmpMarginRight, baseY));					
				}
				
				// �}�E�X�Ńh���b�O�����̈�����F�̗��̂ň͂�
				if (i == (recNum-1) && underDrag) {
					
					int xpos = Math.min(fromPos.x, toPos.x);
					int dragWidth = Math.abs(fromPos.x - toPos.x);
					
					
					// �h���b�O�͈͂𗧑̂ŕ`��
					g.setXORMode(Color.WHITE);
					g.setColor(Color.YELLOW);
					g.fillPolygon(
							new int[]{xpos+dragWidth, xpos+dragWidth, xpos, xpos},
							new int[]{tmpMarginTop, baseY, baseY, tmpMarginTop},
							4);	// ����
					g.setColor(new Color(255, 255, 90));
					g.fillPolygon(
							new int[]{xpos+dragWidth, xpos+dragWidth+((recNum-1)*moveXPoint), xpos+((recNum-1)*moveXPoint), xpos},
							new int[]{tmpMarginTop, tmpMarginTop-((recNum-1)*moveYPoint), tmpMarginTop-((recNum-1)*moveYPoint), tmpMarginTop},
							4);	// �V��
					g.setColor(new Color(225, 225, 0));
					g.fillPolygon(
							new int[]{xpos+dragWidth, xpos+dragWidth+((recNum-1)*moveXPoint), xpos+dragWidth+((recNum-1)*moveXPoint), xpos+dragWidth},
							new int[]{tmpMarginTop, tmpMarginTop-((recNum-1)*moveYPoint), baseY-((recNum-1)*moveYPoint), baseY},
							4);	// ����
					
					// �ʏ팩���Ȃ����̂̋��E����`��
					g.setPaintMode();
					g.setColor(new Color(200, 200, 0));
					g.drawLine(
							xpos+((recNum-1)*moveXPoint),
							tmpMarginTop-((recNum-1)*moveYPoint),
							xpos+((recNum-1)*moveXPoint),
							baseY-((recNum-1)*moveYPoint)
							);	// y��
					g.drawLine(
							xpos+((recNum-1)*moveXPoint), 
							baseY-((recNum-1)*moveYPoint),
							xpos, 
							baseY
							);	// z��
					g.drawLine(
							xpos+((recNum-1)*moveXPoint), 
							baseY-((recNum-1)*moveYPoint), 
							xpos+dragWidth+((recNum-1)*moveXPoint), 
							baseY-((recNum-1)*moveYPoint)
							);	// x��
					
					// ���̂̋��E����`��
					g.setColor(new Color(150, 150, 0));
					g.drawPolygon(
							new int[]{xpos + dragWidth,xpos+dragWidth, xpos, xpos},
							new int[]{tmpMarginTop, baseY, baseY, tmpMarginTop},
							4);	// ����
					g.drawPolygon(
							new int[]{xpos+dragWidth, xpos+dragWidth+((recNum-1)*moveXPoint), xpos+((recNum-1)*moveXPoint), xpos},
							new int[]{tmpMarginTop, tmpMarginTop-((recNum-1)*moveYPoint), tmpMarginTop-((recNum-1)*moveYPoint), tmpMarginTop},
							4);	// �V��
					g.drawPolygon(
							new int[]{xpos+dragWidth, xpos+dragWidth+((recNum-1)*moveXPoint), xpos+dragWidth+((recNum-1)*moveXPoint), xpos+dragWidth},
							new int[]{tmpMarginTop, tmpMarginTop-((recNum-1)*moveYPoint), baseY-((recNum-1)*moveYPoint), baseY},
							4);	// ����
					
					// �h���b�O�͈͂�ێ�
					dragArea = new int[]{xpos, xpos + dragWidth};
				}
			}
			
			
			PackageRecData recData = null;
			overCursorMz = new ArrayList<String>();
			
			// �J�[�\�����C���\������
			ArrayList<ArrayList<String>> cursorLineXInfo = new ArrayList<ArrayList<String>>();		// ���R�[�h���̃J�[�\�����C��x���W���
			for (int i=0; i<recNum; i++) {
				
				recData = specData.getRecInfo(i);
				
				baseX = MARGIN + (recNum-1-i) * moveXPoint;
				baseY = height - MARGIN - (recNum-1-i) * moveYPoint;

				tmpMarginTop = marginTop + (i * moveYPoint);
				tmpMarginRight = marginRight + (i * moveXPoint);
				if (tmpMarginTop > baseY) {
					tmpMarginTop = baseY;
				}
				if (width - tmpMarginRight < baseX) {
					tmpMarginRight = (width - baseX > 0) ? (width - baseX) : 0;
				}
				
				int step = stepCalc((int)massRange);
				int start = (step - (int)massStart % step) % step;
				
				// �s�[�N���Ȃ��ꍇ
				if ( recData.getPeakNum() == 0 ) {
					continue;
				}
				
				int end, its, x, y, w;
				double mz;
				start = recData.getIndex(massStart);
				end = recData.getIndex(massStart + massRange);
				for (int j=start; j<end; j++) {
					boolean isSelectedLine = false;
					mz = recData.getMz(j);
					its = recData.getIntensity(j);
					
					x = baseX + (int)((mz - massStart) * xscale) - (int)Math.floor(xscale / 8);
					y = baseY - (int)(its * yscale);
					w = (int)(xscale / 8);
					
					// �`��p�����[�^�i���j����
					if (w < 2) {
						w = 2;
					}
					else if (w < 3) {
						w = 3;
					}
					
					// y����荶���ɂ͕`�悵�Ȃ��悤�ɒ���
					if(baseX > x) {
						w = (w - (baseX - x) > 0) ? (w - (baseX - x)) : 1;
						x = baseX + 1;
					}
					
					// �I���σs�[�N�ł��邩�𔻒�
					if (specData.getSelectedPeakNum() != 0) {
						Iterator<Double> ite = specData.getSelectedPeakList().iterator();
						while (ite.hasNext()) {
							if (String.valueOf(mz).equals(String.valueOf(ite.next()))) {
								isSelectedLine = true;
								break;
							}
						}
						// �J�[�\�����C���`��
						if ( isSelectedLine ) {
							g.setColor(Color.CYAN.darker().darker());
							g.drawLine(
									x,
									baseY,
									x + (i * moveXPoint),
									baseY - (i * moveYPoint)
									);											// �J�[�\���s�[�N��艜
							g.drawLine(
									x,
									baseY,
									x - ((recNum - 1 - i) * moveXPoint),
									baseY + ((recNum - 1 - i) * moveYPoint)
									);											// �J�[�\���s�[�N����O
							g.drawLine(
									x - ((recNum - 1 - i) * moveXPoint),
									baseY + ((recNum - 1 - i) * moveYPoint),
									x - ((recNum - 1 - i) * moveXPoint) - 2,
									baseY + ((recNum - 1 - i) * moveYPoint) + 6
									);											// �J�[�\�����C���ڈ�
							g.drawLine(
									x - ((recNum - 1 - i) * moveXPoint),
									baseY + ((recNum - 1 - i) * moveYPoint),
									x - ((recNum - 1 - i) * moveXPoint) + 2,
									baseY + ((recNum - 1 - i) * moveYPoint) + 6
									);											// �J�[�\�����C���ڈ�
						}
					}
					
					
					// �g�又�����͏������Ȃ�
					if (animationTimer == null || !animationTimer.isRunning()) {
						
						// �J�[�\���̏ꏊ�iX/Y���W�j��Peak�̕`��G���A�Ɋ܂܂�Ă���ꍇ				
						if ((cursorPoint.x >= x 
								&& cursorPoint.x <= (x + w) 
								&& cursorPoint.y >= y
								&& cursorPoint.y <= baseY)) {
							
							ArrayList<String> cursorLineX = new ArrayList<String>();	// ���R�[�h���̃J�[�\�����C��x���W
							for (int k=0; k<recNum; k++) {
								
								if (k <= i) {
									cursorLineX.add(String.valueOf(x + ((i - k) * moveXPoint)));
								}
								else {
									cursorLineX.add(String.valueOf(x - ((k - i) * moveXPoint)));
								}
							}
							cursorLineXInfo.add(cursorLineX);
							
							// �J�[�\�����C���`��
							if ( !isSelectedLine ) {
								// �I���σs�[�N�łȂ��ꍇ�ɕ`��
								g.setColor(onCursorColor.darker());
								g.drawLine(
										x,
										baseY,
										x + (i * moveXPoint),
										baseY - (i * moveYPoint)
										);											// �J�[�\���s�[�N��艜
								g.drawLine(
										x,
										baseY,
										x - ((recNum - 1 - i) * moveXPoint),
										baseY + ((recNum - 1 - i) * moveYPoint)
										);											// �J�[�\���s�[�N����O
								g.drawLine(
										x - ((recNum - 1 - i) * moveXPoint),
										baseY + ((recNum - 1 - i) * moveYPoint),
										x - ((recNum - 1 - i) * moveXPoint) - 2,
										baseY + ((recNum - 1 - i) * moveYPoint) + 6
										);											// �J�[�\�����C���ڈ�
								g.drawLine(
										x - ((recNum - 1 - i) * moveXPoint),
										baseY + ((recNum - 1 - i) * moveYPoint),
										x - ((recNum - 1 - i) * moveXPoint) + 2,
										baseY + ((recNum - 1 - i) * moveYPoint) + 6
										);											// �J�[�\�����C���ڈ�
							}
							
							overCursorMz.add(String.valueOf(mz));
						}
					}
				}	// end for
			}	// end for
			
			
			// �s�[�N�`��
			for (int i=0; i<recNum; i++) {
				
				recData = specData.getRecInfo(i);
				
				if (recData.isDisable()) {
					continue;
				}
				
				baseX = MARGIN + (recNum-1-i) * moveXPoint;
				baseY = height - MARGIN - (recNum-1-i) * moveYPoint;

				nextBaseX = baseX - moveXPoint;
				nextBaseY = baseY + moveYPoint;
				
				tmpMarginTop = marginTop + (i * moveYPoint);
				tmpMarginRight = marginRight + (i * moveXPoint);
				if (tmpMarginTop > baseY) {
					tmpMarginTop = baseY;
				}
				if (width - tmpMarginRight < baseX) {
					tmpMarginRight = (width - baseX > 0) ? (width - baseX) : 0;
				}
				
				int step = stepCalc((int)massRange);
				int start = (step - (int)massStart % step) % step;
				
				// �s�[�N���Ȃ��ꍇ
				if ( recData.getPeakNum() == 0 ) {
					continue;
				}
				
				
				int end, its, x, y, w, h;
				double mz;
				start = recData.getIndex(massStart);
				end = recData.getIndex(massStart + massRange);
				for (int j=start; j<end; j++) {
					boolean isSelectedLine = false;
					boolean isOnLine = false;						// �}�E�X�J�[�\�����C����s�[�N�t���O
					boolean isDragArea = false;					// �h���b�O�͈͓��t���O
					
					mz = recData.getMz(j);
					its = recData.getIntensity(j);
					
//					x = MARGIN + (int) ((peak - massStart) * xscale) - (int) Math.floor(xscale / 8);
					x = baseX + (int)((mz - massStart) * xscale) - (int)Math.floor(xscale / 8);
					y = baseY - (int)(its * yscale);
					w = (int)(xscale / 8);
					h = (int)(its * yscale);
					
					// �`��p�����[�^�i�����A�ʒu�j����
					if (h == 0) {
						y -= 1;
						h = 1;
					}
					// �`��p�����[�^�i���j����
					if (w < 2) {
						w = 2;
					}
					else if (w < 3) {
						w = 3;
					}
					
					// y����荶���ɂ͕`�悵�Ȃ��悤�ɒ���
					if(baseX > x) {
						w = (w - (baseX - x) > 0) ? (w - (baseX - x)) : 1;
						x = baseX + 1;
					}
					
					
					boolean inPeakArea = false;
					
					// �g�又�����͏������Ȃ�
					if (animationTimer == null || !animationTimer.isRunning()) {
						// �}�E�X�J�[�\�����C���̏ꏊ�iX/Y���W�j��Peak�̕`��G���A�Ɋ܂܂�Ă��邩�𔻒�	
						for (int k=0; k<cursorLineXInfo.size(); k++) {
							
							if (Integer.parseInt(String.valueOf(cursorLineXInfo.get(k).get(i))) == x) {
								inPeakArea = true;
								break;
							}
						}
						
						// ����ɁA�G���A���̏ꍇ�̓J�[�\����Peak��m/z�Ɠ������𔻒�
						if (inPeakArea) {
							for (int k=0; k<overCursorMz.size(); k++) {
								if (Double.parseDouble(overCursorMz.get(k)) == mz) {
									isOnLine = true;
									break;
								}
							}
						}
					}
					
					// �I���σs�[�Nm/z�ł��邩�̔���
					if (specData.getSelectedPeakNum() != 0) {
						Iterator<Double> ite = specData.getSelectedPeakList().iterator();
						while (ite.hasNext()) {
							if (String.valueOf(mz).equals(String.valueOf(ite.next()))) {
								isSelectedLine = true;
								break;
							}
						}
					}
					
					// �h���b�O�͈͂̒���Peak���܂܂�Ă��邩�𔻒�
					if ( underDrag ) {
						if ( dragArea[0] + ((recNum-1-i) * moveXPoint) <= (x + w) 
								&& dragArea[1] + ((recNum-1-i) * moveXPoint) >= x) {
							
							isDragArea = true;
						}
					}
					
					
					// �s�[�N�`��
					g.setPaintMode();
					if (isSelectedLine) {
						g.setColor(Color.CYAN.darker());						// ���C���s�[�N�I�����̐F
					}
					else if (isOnLine) {
						g.setColor(onCursorColor);								// ���C����̐F
					}
					else if ( isDragArea ) {									// �h���b�O�͈͓��̐F
						if (!chgColor.isSelected() && recData.getPeakColor(j).equals(Color.RED)) {
							g.setColor(Color.MAGENTA);
						}
						else if (!chgColor.isSelected() && recData.getPeakColor(j).equals(Color.MAGENTA)) {
							g.setColor(Color.RED);
						}
						else {
							g.setColor(Color.BLUE);
						}
					}
					else if (chgColor.isSelected()) {
						g.setColor(getColor(i));								// �I�[�g�J���[�ύX���̐F
					}
					else if (y < tmpMarginTop) {
						g.setColor(Color.GRAY);									// �ʏ펞�̐F(�s�[�N���O���t���ɂ����܂��Ă��Ȃ��ꍇ)
					}
					else {
						g.setColor(recData.getPeakColor(j));					// �ʏ펞�̐F
					}
					// �s�[�N�̕`��Ńs�[�N�̋��x���������߂ɃO���t���ɂ����܂肫��Ȃ��ꍇ�́A
					// �O���t���ɕ`��ł��鋭�x�܂ŕ`�悷��B
					if (y >= tmpMarginTop) {
						g.fill3DRect(x, y, w, h, true);
					}
					else {
						g.fill3DRect(x, tmpMarginTop, w, baseY - tmpMarginTop, true);
					}
					
					
					// m/z�l��`��
					g.setFont(font2);
					boolean isMzDraw = false;
					if ( isSelectedLine ) {
						g.setColor(Color.CYAN.darker());
						if ( isOnLine ) {
							g.setFont(font1);
						}
						isMzDraw = true;
					}
					else if ( isOnLine ) {
						g.setFont(font1);
						g.setColor(onCursorColor);
						isMzDraw = true;
					}
					else if ( mzDisp.isSelected() ) {
						g.setFont(font2);
						if (!isDragArea) {
							if (its > intensityRange * 0.4 ) {
								g.setColor(Color.RED);
							}
							else if (!chgColor.isSelected()){
								g.setColor(Color.BLACK);
							}
						}
						isMzDraw = true;
					}
					else if (mzMatchDisp.isSelected()) {
						if (recData.getPeakColorType(j) != PackageRecData.COLOR_TYPE_BLACK) {
							if (!chgColor.isSelected() && !isDragArea) {
								g.setFont(font2);
								g.setColor(recData.getPeakColor(j));
							}
							isMzDraw = true;
						}
					}
					else if (its > intensityRange * 0.4) {
						g.setFont(font2);
						if (!chgColor.isSelected()) {
							g.setColor(Color.BLACK);
						}
						isMzDraw = true;
					}
					
					// �O���t���ɕ`��ł���ꍇ�̂ݕ`��
					if (isMzDraw && y >= tmpMarginTop) {
						g.drawString(formatMass(mz, false), x, y);
					}
					
					
					// �s�[�N�̋��x�\��
					if (isOnLine || isSelectedLine) {
						// ���x�̕`��ʒu���O���t��(���݂̍ő勭�x�ȉ�)�̏ꍇ
						if (y >= tmpMarginTop) {
							g.setColor(onCursorColor);
							if ( isSelectedLine ) {
								g.setColor(Color.CYAN.darker());
							}
							g.drawLine(
									baseX + 4, 
									y, 
									baseX - 4, 
									y);					// ���x�ڐ���
							g.setColor(Color.GRAY);
							g.setFont(font2);
							if (isOnLine) {
								g.setColor(Color.DARK_GRAY);
								g.setFont(font3);
							}
							g.drawString(String.valueOf(recData.getIntensity(j)),
									baseX + 5,
									y - 1);				// ���x
						}
					}
				}	// end for
				
				// �v���J�[�T�[m/z�ɎO�p�}�[�N�t��
				if ( recData != null
						&& !recData.getPrecursor().equals("") ) {
					
					double pre = Double.parseDouble(recData.getPrecursor());
					int preX = baseX + (int)((pre - massStart) * xscale) - (int)Math.floor(xscale / 8);

					// �v���J�[�T�[m/z���O���t���̏ꍇ�̂ݕ`��
					if ( preX >= baseX 
							&& preX <= width - tmpMarginRight ) {
						
						// �v���J�[�T�[�O�p�̕`��p�x���O���t�̉��ɂ��킹�ĕύX
						int maxGraphWidth = width - (MARGIN * 2);
						int nowGraphWidth = width - tmpMarginRight - baseX;
						int maxMovePreX = 7;
						int movePreX = 0;
						for (int j=1; j<maxMovePreX; j++) {
							if ((maxGraphWidth / maxMovePreX * j) > nowGraphWidth) {
								movePreX = maxMovePreX - j;
								break;
							}
						}
						
						int[] xp = { preX, (preX + 6 - movePreX), (preX - 6 - movePreX) };
						int[] yp = { baseY, (baseY + 5), (baseY + 5) };
						g.setColor( Color.RED );
						g.fillPolygon( xp, yp, 3 );
					}
				}
			}	// end for
		}
		

		/**
		 * �}�E�X�v���X�C�x���g
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent e) {
			
			if (animationTimer != null && animationTimer.isRunning()) {
				return;
			}
			else if (moveTimer != null && moveTimer.isRunning()) {
				return;
			}
			
			if (recNum == 0) {
				return;
			}
			
			// ���{�^���̏ꍇ
			if ( SwingUtilities.isLeftMouseButton(e) ) {
				
				// ��Ԏ�O�̃O���t�̍��[�A�E�[�ɂ����܂�͈͓��ł̃C�x���g�̏ꍇ
				if (e.getPoint().x >= prevGraphArea.get(LT).x
						&& e.getPoint().x <= prevGraphArea.get(RT).x) {
					
					isDrag = true;
				}
				else {
					isDrag = false;
				}
				
				// �h���b�O�C�x���g�L���̏ꍇ
				if ( isDrag ) {
					if(animationTimer != null && animationTimer.isRunning()) {
						return;
					}
					fromPos = toPos = e.getPoint();
				}
			}
			
			// �E�{�^���̏ꍇ
			if ( !SwingUtilities.isRightMouseButton(e) ) {
				// �\�[�g���j���[�\�����Ή�
				if (contextPopup != null && !contextPopup.isVisible()) {
					cursorPoint = e.getPoint();
					PackageViewPanel.this.repaint();
				}
				
			}
		}
		
		/**
		 * �}�E�X�h���b�O�C�x���g
		 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
		 */
		public void mouseDragged(MouseEvent e) {

			if (animationTimer != null && animationTimer.isRunning()) {
				return;
			}
			else if (moveTimer != null && moveTimer.isRunning()) {
				return;
			}
			
			if (recNum == 0) {
				return;
			}
			
			// ���{�^���̏ꍇ
			if ( SwingUtilities.isLeftMouseButton(e) ) {
				
				// �h���b�O�C�x���g�L���̏ꍇ
				if ( isDrag ) {

					cursorPoint = new Point();
					if(animationTimer != null && animationTimer.isRunning()) {
						return;
					}
					underDrag = true;
					toPos = e.getPoint();
					PackageViewPanel.this.repaint();
				}
			}
		}

		/**
		 * �}�E�X�����[�X�C�x���g
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent e) {
			
			if (animationTimer != null && animationTimer.isRunning()) {
				return;
			}
			else if (moveTimer != null && moveTimer.isRunning()) {
				return;
			}
			
			// ���{�^���̏ꍇ
			if ( SwingUtilities.isLeftMouseButton(e) ) {
				
				if (recNum == 0) {
					return;
				}
				
				// �h���b�O�C�x���g�L���̏ꍇ
				if ( isDrag ) {
				
					if (!underDrag || (animationTimer != null && animationTimer.isRunning())) {
						return;
					}
					underDrag = false;
					if ((fromPos != null) && (toPos != null)) {
						
						if (Math.min(fromPos.x, toPos.x) < 0) {
							massStart = Math.max(0, massStart - massRange / 3);
						}
						else if (Math.max(fromPos.x, toPos.x) > getWidth()) {
							massStart = Math.min(massRangeMax - massRange, massStart + massRange / 3);
						}
						else {
							if (specData != null) {
								// �}�E�X�J�[�\���ݒ�
								PackageViewPanel.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
								
								isInitRate = false;
								
								// �X�y�N�g���ړ��{�^���̎g�p�𐧌�
								leftMostBtn.setEnabled(true);
								leftBtn.setEnabled(true);
								rightBtn.setEnabled(true);
								rightMostBtn.setEnabled(true);
								
								animationTimer = new Timer(30,
												  new AnimationTimer(Math.abs(fromPos.x - toPos.x),
												  Math.min(fromPos.x, toPos.x)));
								animationTimer.start();
							}
							else {
								fromPos = toPos = null;
								PackageViewPanel.this.repaint();
							}
						}
					}
					// �g�又����̃}�E�X�J�[�\���|�W�V�����\��
					cursorPoint = e.getPoint();
				}
			}
			// �E�{�^���̏ꍇ
			else if ( SwingUtilities.isRightMouseButton(e) ) {
				
				if (!underDrag) {
					
					// �|�b�v�A�b�v���j���[�C���X�^���X����
					contextPopup = new JPopupMenu();
					
					JMenuItem item1 = null;
					item1 = new JMenuItem("Peak Search");
					item1.setActionCommand("search");
					item1.addActionListener(new ContextPopupListener());
					item1.setEnabled(false);
					contextPopup.add(item1);
					
					JMenuItem item2 = null;
					item2 = new JMenuItem("Select Reset");
					item2.setActionCommand("reset");
					item2.addActionListener(new ContextPopupListener());
					item2.setEnabled(false);
					contextPopup.add(item2);
					
					if (specData.getSelectedPeakNum() != 0) {
						item1.setEnabled(true);
						item2.setEnabled(true);
					}
				
					// �|�b�v�A�b�v���j���[�\��
					contextPopup.show( e.getComponent(), e.getX(), e.getY() );
				}
			}
		}

		/**
		 * �}�E�X�N���b�N�C�x���g
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent e) {
			
			if (animationTimer != null && animationTimer.isRunning()) {
				return;
			}
			else if (moveTimer != null && moveTimer.isRunning()) {
				return;
			}
			
			// ���{�^���̏ꍇ
			if ( SwingUtilities.isLeftMouseButton(e) ) {
				
				if (recNum == 0) {
					return;
				}
				
				// �N���b�N�Ԋu�Z�o
				long interSec = (e.getWhen() - lastClickedTime);
				lastClickedTime = e.getWhen();
				
				// �_�u���N���b�N�̏ꍇ�i�N���b�N�Ԋu280�~���b�ȓ��j
				if(interSec <= 280){
					
					// �g�又��
					fromPos = toPos = null;
					initRange(false);
					
					// �X�y�N�g���ړ��{�^���̎g�p�𐧌�
					leftMostBtn.setEnabled(false);
					leftBtn.setEnabled(false);
					rightBtn.setEnabled(false);
					rightMostBtn.setEnabled(false);
				}
				// �V���O���N���b�N�̏ꍇ�i�N���b�N�Ԋu281�~���b�ȏ�j
				else {
					
					Point clickPoint = e.getPoint();			// �N���b�N�|�C���g
					
					int tmpMarginTop = 0;
					int tmpMarginRight = 0;
					
					int baseX = 0;			// x����y���̌�_��x���W
					int baseY = 0;			// x����y���̌�_��y���W

					PackageRecData recData = null;
					
					TreeSet<Double> tmpClickMzList = new TreeSet<Double>();
					
					for (int i=0; i<recNum; i++) {
						
						recData = specData.getRecInfo(i);
						
						baseX = MARGIN + (recNum-1-i) * moveXPoint;
						baseY = height - MARGIN - (recNum-1-i) * moveYPoint;
						
						tmpMarginTop = marginTop + (i * moveYPoint);
						tmpMarginRight = marginRight + (i * moveXPoint);
						if (tmpMarginTop > baseY) {
							tmpMarginTop = baseY;
						}
						if (width - tmpMarginRight < baseX) {
							tmpMarginRight = (width - baseX > 0) ? (width - baseX) : 0;
						}
						
						int step = stepCalc((int)massRange);
						int start = (step - (int)massStart % step) % step;
						
						// �s�[�N���Ȃ��ꍇ
						if ( recData.getPeakNum() == 0 ) {
							continue;
						}
						
						int end, its, x, y, w;
						double mz;
						start = recData.getIndex(massStart);
						end = recData.getIndex(massStart + massRange);
						for (int j=start; j<end; j++) {
							mz = recData.getMz(j);
							its = recData.getIntensity(j);
							
							x = baseX + (int)((mz - massStart) * xscale) - (int)Math.floor(xscale / 8);
							y = baseY - (int)(its * yscale);
							w = (int)(xscale / 8);
							
							// �`��p�����[�^�i���j����
							if (w < 2) {
								w = 2;
							}
							else if (w < 3) {
								w = 3;
							}
							
							// y����荶���ɂ͕`�悵�Ȃ��悤�ɒ���
							if(baseX > x) {
								w = (w - (baseX - x) > 0) ? (w - (baseX - x)) : 1;
								x = baseX + 1;
							}
							
							// �N���b�N�����ꏊ���iX/Y���W�j��Peak�̕`��G���A�Ɋ܂܂�Ă���ꍇ
							if (clickPoint.x >= x 
									&& clickPoint.x <= (x + w) 
									&& clickPoint.y >= y
									&& clickPoint.y <= baseY) {
								
								tmpClickMzList.add(mz);
							}
						}	// end for
					}	// end for
					
					
					// �N���b�N�|�C���g��Peak��1�̏ꍇ
					if (tmpClickMzList.size() == 1) {
						
						String mz = String.valueOf(tmpClickMzList.iterator().next());
						
						if (!specData.containsSelectedPeak(mz)) {
							if (specData.getSelectedPeakNum() < MassBankCommon.PEAK_SEARCH_PARAM_NUM) {
								// �I���σs�[�N�̕ێ�
								specData.addSelectedPeakList(mz);
								// �I����Ԃ�ݒ�
								for (int i=0; i<recNum; i++) {
									recData = specData.getRecInfo(i);
									recData.setSelectPeak(mz, true);
								}
							}
							else {
								JOptionPane.showMessageDialog(
										SpectrumPlotPane.this,
										" m/z of " + MassBankCommon.PEAK_SEARCH_PARAM_NUM + " peak or more cannot be selected.&nbsp;",
										"Warning",
										JOptionPane.WARNING_MESSAGE);
							}
						}
						else {
							// �I���σs�[�N�̕ێ�����
							specData.removeSelectedPeakList(mz);
							// �I����Ԃ�����
							for (int i=0; i<recNum; i++) {
								recData = specData.getRecInfo(i);
								recData.setSelectPeak(mz, false);
							}
						}
						setStatusLabel();
						PackageViewPanel.this.repaint();
					}
					// �N���b�N�|�C���g��Peak��2�ȏ�̏ꍇ
					else if (tmpClickMzList.size() >= 2) {
						// �|�b�v�A�b�v���j���[�C���X�^���X����
						selectPopup = new JPopupMenu();
						JMenuItem item = null;
						
						Iterator<Double> ite = tmpClickMzList.iterator();
						while (ite.hasNext()) {
							String mz = String.valueOf(ite.next());
							item = new JMenuItem(mz);
							selectPopup.add(item);
							item.addActionListener(new SelectMZPopupListener(mz));
							
							if (specData.getSelectedPeakNum() >= MassBankCommon.PEAK_SEARCH_PARAM_NUM
									&& !specData.containsSelectedPeak(mz)) {
								// Peak�I�𐔂�MAX�̏ꍇ�A�I���ς�Peak�ȊO�͑I��s��ݒ�
								item.setEnabled(false);
							}
						}
						
						// �|�b�v�A�b�v���j���[�\��
						selectPopup.show( e.getComponent(), e.getX(), e.getY() );
					}
				}
			}
		}
		
		/**
		 * �}�E�X�G���^�[�C�x���g
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseEntered(MouseEvent e) {
		}
		
		/**
		 * �}�E�X�C�O�W�b�g�C�x���g
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseExited(MouseEvent e) {
		}
		
		/**
		 * �}�E�X���[�u�C�x���g
		 * @see java.awt.event.MouseMotionListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseMoved(MouseEvent e) {
			
			if (animationTimer != null && animationTimer.isRunning()) {
				return;
			}
			else if (moveTimer != null && moveTimer.isRunning()) {
				return;
			}
			
			if (recNum == 0) {
				return;
			}
			
			// �|�b�v�A�b�v���j���[���\������Ă��鎞�̓C�x���g���������Ȃ�
			if ((contextPopup == null || (contextPopup != null && !contextPopup.isVisible()))
					&& (selectPopup == null || (selectPopup != null && !selectPopup.isVisible()))) {
				
				// �}�E�X�J�[�\���|�C���g
				cursorPoint = e.getPoint();
				PackageViewPanel.this.repaint();
			}
		}
		
		/**
		 * �g�又�����A�j���[�V����������N���X
		 * PlotPane�̃C���i�[�N���X
		 */
		class AnimationTimer implements ActionListener {
			private final int LOOP = 15;
			private int loopCoef;
			private int toX;
			private int fromX;
			private double tmpMassStart;
			private double tmpMassRange;
			private int tmpIntensityRange;
			private int movex;

			/**
			 * �R���X�g���N�^
			 * @param from �h���b�O�J�n�ʒu
			 * @param to �h���b�O�I���ʒu
			 */
			public AnimationTimer(int from, int to) {
				loopCoef = 0;
				toX = to;
				fromX = from;
				movex = 0 + MARGIN;
				// �ړI�g�嗦���Z�o
//				float xs = (getWidth() - 2.0f * MARGIN) / massRange;
				double xs = (getWidth() - (2.0d * MARGIN) - ((recNum-1) * moveXPoint)) / massRange;
				tmpMassStart = massStart + ((toX - MARGIN) / xs);
				
				tmpMassRange = 10 * (fromX / (10 * xs));
				if (tmpMassRange < MASS_RANGE_MIN) {
					tmpMassRange = MASS_RANGE_MIN;
				}

				// Intensity�̃����W��ݒ�
				if (massRange <= massRangeMax) {
					int maxIntensity = 0;
					double start = Math.max(tmpMassStart, 0.0d);
					// �S�Ẵ��R�[�h�����狭�x�̍ő�l���Z�o
					PackageRecData recData = null;
					for ( int i=0; i<recNum; i++ ) {
						recData = specData.getRecInfo(i);
						if (maxIntensity < recData.getMaxIntensity(start, start + tmpMassRange)) {
							maxIntensity = recData.getMaxIntensity(start, start + tmpMassRange);
						}
					}
					// 50�P�ʂɕϊ����ăX�P�[��������
					tmpIntensityRange = (int)((1.0d + maxIntensity / 50.0d) * 50.0d);
					if(tmpIntensityRange > INTENSITY_RANGE_MAX) {
						tmpIntensityRange = INTENSITY_RANGE_MAX;
					}
				}
			}

			/**
			 * �A�N�V�����C�x���g
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				int xpos = (movex + toX) / 2;
				if (Math.abs(massStart - tmpMassStart) <= 2
						&& Math.abs(massRange - tmpMassRange) <= 2
						&& Math.abs(intensityRange - tmpIntensityRange) <= 2) {
					
					xpos = toX;
					
					massStart = tmpMassStart;
					massRange = tmpMassRange;
					intensityRange = tmpIntensityRange;
					animationTimer.stop();
					// �}�E�X�J�[�\���ݒ�
					PackageViewPanel.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
				else {
					
					loopCoef++;
					massStart = massStart
							+ (((tmpMassStart + massStart) / 2 - massStart)
									* loopCoef / LOOP);
					massRange = massRange
							+ (((tmpMassRange + massRange) / 2 - massRange)
									* loopCoef / LOOP);
					intensityRange = intensityRange
							+ ((tmpIntensityRange - intensityRange)
									* loopCoef / LOOP);

					if (loopCoef >= LOOP) {
						movex = xpos;
						loopCoef = 0;
					}
				}
				PackageViewPanel.this.repaint();
			}
		}
		
		/**
		 * �s�[�N�I���|�b�v�A�b�v���j���[���X�i�[�N���X
		 * PlotPane�̃C���i�[�N���X
		 */
		class SelectMZPopupListener implements ActionListener {
			
			private String mz = "";			// m/z
			
			/**
			 * �R���X�g���N�^
			 * @param mz m/z
			 */
			public SelectMZPopupListener(String mz) {
				this.mz = mz;
			}
			
			/**
			 * �A�N�V�����C�x���g
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				
				PackageRecData recData = null;
				
				if (!specData.containsSelectedPeak(mz)) {
					// �I���σs�[�N�̕ێ�
					specData.addSelectedPeakList(mz);
					// �I����Ԃ�ݒ�
					for (int i=0; i<recNum; i++) {
						recData = specData.getRecInfo(i);
						recData.setSelectPeak(mz, true);
					}
				}
				else {
					// �I���σs�[�N�̕ێ�����
					specData.removeSelectedPeakList(mz);
					// �I����Ԃ�����
					for (int i=0; i<recNum; i++) {
						recData = specData.getRecInfo(i);
						recData.setSelectPeak(mz, false);
					}
				}
				
				setStatusLabel();
				
				// PlotPane�J�[�\���|�C���g������
				cursorPoint = new Point();
				
				PackageViewPanel.this.repaint();
			}
		}
		
		/**
		 * �R���e�L�X�g�|�b�v�A�b�v���j���[���X�i�[�N���X
		 * PlotPane�̃C���i�[�N���X
		 */
		class ContextPopupListener implements ActionListener {
			
			/**
			 * �R���X�g���N�^
			 */
			public ContextPopupListener() {
			}

			/**
			 * �A�N�V�����C�x���g
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {

				String com = e.getActionCommand();
				
				if (com.equals("search")) {
					// URL�p�����[�^����
					StringBuffer urlParam = new StringBuffer();
					
					String typeName = MassBankCommon.CGI_TBL[MassBankCommon.CGI_TBL_NUM_TYPE][MassBankCommon.CGI_TBL_TYPE_PEAK];
					
					urlParam.append("?type=" + typeName);							// type�Fpeak
					urlParam.append("&num=" + specData.getSelectedPeakNum());		// num �F
					urlParam.append("&tol=0");										// tol �F0
					urlParam.append("&int=5");										// int �F5
					
					int index = 0;
					Iterator<Double> ite = specData.getSelectedPeakList().iterator();
					while (ite.hasNext()) {
						if (index != 0) {
							urlParam.append("&op"+ index +"=and");					// op  �Fand
						} else {
							urlParam.append("&op"+ index +"=or");					// op  �For
						}
						urlParam.append("&mz"+ index +"=" + String.valueOf(ite.next()));	// mz  �F
						index++;
					}
					urlParam.append("&sortKey=name&sortAction=1&pageNo=1&exec=&inst=all");
					
					// JSP�Ăяo��
					String reqUrl = SearchPage.baseUrl + "jsp/Result.jsp" + urlParam.toString();
					try {
						SearchPage.context.showDocument(new URL(reqUrl), "_blank");
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				else if (com.equals("reset")) {
					specData.clearSelectedPeakList();
					specData.initAllSelectedPeak();
					setStatusLabel();
					PackageViewPanel.this.repaint();
				}
			}
		}
	}
	
	/**
	 * �{�^���y�C��
	 * PackageViewPanel�̃C���i�[�N���X
	 */
	class ButtonPane extends JPanel implements MouseListener, MouseMotionListener {

		private boolean isInMZ = false;					// show all m/z�{�^���J�[�\���C���t���O
		private boolean isInMZMatch = false;				// show match m/z�{�^���J�[�\���C���t���O
		private boolean isInChgColor = false;				// change color�{�^���J�[�\���C���t���O
		private boolean isInFlat = false;					// flat�{�^���J�[�\���C���t���O
		private boolean isMZState = false;				// �C�x���g�Oshow all m/z�{�^�����
		private boolean isMZMatchState = false;			// �C�x���g�Oshow match m/z�{�^�����
		private boolean isChgColorState = false;			// �C�x���g�Ochange color�{�^�����
		private boolean isFlatState = false;				// �C�x���g�Oflat�{�^�����
		
		/**
		 * �R���X�g���N�^
		 */
		public ButtonPane() {
			
			// ���C�A�E�g�w��
			setLayout(new FlowLayout());
			
			leftMostBtn = new JButton("<<");
			leftMostBtn.setName("<<");
			leftMostBtn.addMouseListener(this);
			leftMostBtn.setMargin(new Insets(0, 0, 0, 0));
			
			leftBtn = new JButton(" < ");
			leftBtn.setName("<");
			leftBtn.addMouseListener(this);
			leftBtn.setMargin(new Insets(0, 0, 0, 0));

			rightBtn = new JButton(" > ");
			rightBtn.setName(">");
			rightBtn.addMouseListener(this);
			rightBtn.setMargin(new Insets(0, 0, 0, 0));

			rightMostBtn = new JButton(">>");
			rightMostBtn.setName(">>");
			rightMostBtn.addMouseListener(this);
			rightMostBtn.setMargin(new Insets(0, 0, 0, 0));

			if (!isInitRate) {
				leftMostBtn.setEnabled(true);
				leftBtn.setEnabled(true);
				rightBtn.setEnabled(true);
				rightMostBtn.setEnabled(true);
			}
			else {
				leftMostBtn.setEnabled(false);
				leftBtn.setEnabled(false);
				rightBtn.setEnabled(false);
				rightMostBtn.setEnabled(false);
			}
			
			mzDisp = new JToggleButton("show all m/z");
			mzDisp.setName("mz");
			mzDisp.addMouseListener(this);
			mzDisp.addMouseMotionListener(this);
			mzDisp.setMargin(new Insets(0, 0, 0, 0));
			mzDisp.setSelected(false);

			mzMatchDisp = new JToggleButton("show match m/z");
			mzMatchDisp.setName("match");
			mzMatchDisp.addMouseListener(this);
			mzMatchDisp.addMouseMotionListener(this);
			mzMatchDisp.setMargin(new Insets(0, 0, 0, 0));
			mzMatchDisp.setSelected(false);
			
			chgColor = new JToggleButton("change color");
			chgColor.setName("chgColor");
			chgColor.addMouseListener(this);
			chgColor.addMouseMotionListener(this);
			chgColor.setMargin(new Insets(0, 0, 0, 0));
			chgColor.setSelected(false);
			
			// ���C�A�E�g�����p�u�����N���x��
			JLabel blankLabel1 = new JLabel("   ");
			
			xAxisUp = new JButton("��");
			xAxisUp.setName("xup");
			xAxisUp.addMouseListener(this);
			xAxisUp.addMouseMotionListener(this);
			xAxisUp.setMargin(new Insets(0, 0, 0, 0));
			
			xAxisDown = new JButton("��");
			xAxisDown.setName("xdown");
			xAxisDown.addMouseListener(this);
			xAxisDown.addMouseMotionListener(this);
			xAxisDown.setMargin(new Insets(0, 0, 0, 0));
			
			yAxisUp = new JButton("��");
			yAxisUp.setName("yup");
			yAxisUp.addMouseListener(this);
			yAxisUp.addMouseMotionListener(this);
			yAxisUp.setMargin(new Insets(0, 0, 0, 0));
			
			yAxisDown = new JButton("��");
			yAxisDown.setName("ydown");
			yAxisDown.addMouseListener(this);
			yAxisDown.addMouseMotionListener(this);
			yAxisDown.setMargin(new Insets(0, 0, 0, 0));
			
			topAngleBtn = new JButton("top angle");
			topAngleBtn.setName("top");
			topAngleBtn.addMouseListener(this);
			topAngleBtn.setMargin(new Insets(0,0,0,0));
			
			sideAngleBtn = new JButton("side angle");
			sideAngleBtn.setName("side");
			sideAngleBtn.addMouseListener(this);
			sideAngleBtn.setMargin(new Insets(0,0,0,0));
			
			flat = new JToggleButton("flat");
			flat.setName("flat");
			flat.addMouseListener(this);
			flat.addMouseMotionListener(this);
			flat.setMargin(new Insets(0, 0, 0, 0));
			flat.setSelected(false);
			
			add(leftMostBtn);
			add(leftBtn);
			add(rightBtn);
			add(rightMostBtn);
			add(mzDisp);
			add(mzMatchDisp);
			add(chgColor);
			add(blankLabel1);
			add(xAxisUp);
			add(xAxisDown);
			add(yAxisUp);
			add(yAxisDown);
			add(topAngleBtn);
			add(sideAngleBtn);
			add(flat);
		}
		
		
		/**
		 * �}�E�X�N���b�N�C�x���g
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent e) {
			
			String btnName = e.getComponent().getName();
			
			if (!e.getComponent().isEnabled()) {
				return;
			}
			else if (animationTimer != null && animationTimer.isRunning()) {
				return;
			}
			
			// ���{�^���̏ꍇ
			if ( SwingUtilities.isLeftMouseButton(e) ) {
				
				if (btnName.equals("<<")) {
					massStart = Math.max(0, massStart - massRange);
					PackageViewPanel.this.repaint();
				}
				else if (btnName.equals("<")) {
					massStart = Math.max(0, massStart - massRange / 4);
					PackageViewPanel.this.repaint();
				}
				else if (btnName.equals(">")) {
					massStart = Math.min(massRangeMax - massRange, massStart + massRange / 4);
					PackageViewPanel.this.repaint();
				}
				else if (btnName.equals(">>")) {
					massStart = Math.min(massRangeMax - massRange, massStart + massRange);
					PackageViewPanel.this.repaint();
				}
				else if (btnName.equals("top")) {
					if (moveXPoint != minMoveXPoint || moveYPoint != maxMoveYPoint) {
						allBtnCtrl(false);
						// �}�E�X�J�[�\���ݒ�
						PackageViewPanel.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						moveTimer = new Timer(10,
								  new MoveAnimationTimer(btnName));
						moveTimer.start();
					}
				}
				else if (btnName.equals("side")) {
					if (moveXPoint != minMoveXPoint || moveYPoint != minMoveYPoint) {
						allBtnCtrl(false);
						// �}�E�X�J�[�\���ݒ�
						PackageViewPanel.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						moveTimer = new Timer(10,
								  new MoveAnimationTimer(btnName));
						moveTimer.start();
					}
				}
			}
		}

		/**
		 * �}�E�X�v���X�C�x���g
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent e) {
			
			String btnName = e.getComponent().getName();
			
			if (!e.getComponent().isEnabled()) {
				return;
			}
			else if (animationTimer != null && animationTimer.isRunning()) {
				// �{�^�������O��Ԃ�ێ�
				isMZState = mzDisp.isSelected();
				isMZMatchState = mzMatchDisp.isSelected();
				isChgColorState = chgColor.isSelected();
				isFlatState = flat.isSelected();
				return;
			}
			
			// PlotPane�J�[�\���|�C���g������
			cursorPoint = new Point();
			
			// ���{�^���̏ꍇ
			if ( SwingUtilities.isLeftMouseButton(e) ) {
				
				int time = 10;
				
				// �R���g���[���L�[�������ɉ�������Ă���ꍇ�́A�����x������
				if (e.isControlDown()) {
					time = 70;
				}
				
				if (btnName.equals("xup")
						|| btnName.equals("xdown")
						|| btnName.equals("yup")
						|| btnName.equals("ydown")) {
					
					moveTimer = new Timer(time,
							  new MoveAnimationTimer(btnName));
					moveTimer.start();
				}
			}
		}

		/**
		 * �}�E�X�����[�X�C�x���g
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent e) {
			
			String btnName = e.getComponent().getName();
			
			if (!e.getComponent().isEnabled()) {
				return;
			}
			else if (animationTimer != null && animationTimer.isRunning()) {
				// �{�^�������O��Ԃɖ߂�
				if (btnName.equals("mz")) {
					mzDisp.setSelected(isMZState);
				}
				else if (btnName.equals("match")) {
					mzMatchDisp.setSelected(isMZMatchState);
				}
				else if (btnName.equals("chgColor")) {
					chgColor.setSelected(isChgColorState);
				}
				else if (btnName.equals("flat")) {
					flat.setSelected(isFlatState);
				}
				return;
			}
			
			
			// ���{�^���̏ꍇ
			if ( SwingUtilities.isLeftMouseButton(e) ) {
				
				if (btnName.equals("mz")) {
					if (isInMZ) {
						mzMatchDisp.setSelected(false);
						PackageViewPanel.this.repaint();
					}
				}
				else if (btnName.equals("match")) {
					if (isInMZMatch) {
						mzDisp.setSelected(false);
						PackageViewPanel.this.repaint();
					}
				}
				else if (btnName.equals("chgColor")) {
					if (isInChgColor) {
						PackageViewPanel.this.repaint();
					}
				}
				else if (btnName.equals("flat")) {
					if (isInFlat) {
						
						// flat�{�^����Ԏ擾
						if (flat.isSelected()) {
							if (tmpMoveXPoint == -1 && tmpMoveYPoint == -1) {
								// ���݂�x���Ay�����l��ޔ�
								tmpMoveXPoint = moveXPoint;
								tmpMoveYPoint = moveYPoint;
							}
						}
						
						allBtnCtrl(false);
						// �}�E�X�J�[�\���ݒ�
						PackageViewPanel.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						moveTimer = new Timer(10,
								  new MoveAnimationTimer(btnName));
						moveTimer.start();
					}
				}
				else if (btnName.equals("xup")
						|| btnName.equals("xdown")
						|| btnName.equals("yup")
						|| btnName.equals("ydown")) {
					
					if (moveTimer != null && moveTimer.isRunning()) {
						moveTimer.stop();
					}
				}
			}
		}

		/**
		 * �}�E�X�G���^�[�C�x���g
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseEntered(MouseEvent e) {
			
			String btnName = e.getComponent().getName();
			
			if (!e.getComponent().isEnabled()) {
				return;
			}
			else if (animationTimer != null && animationTimer.isRunning()) {
				return;
			}
			
			if (btnName.equals("mz")) {
				isInMZ = true;
			}
			else if (btnName.equals("match")) {
				isInMZMatch = true;
			}
			else if (btnName.equals("chgColor")) {
				isInChgColor = true;
			}
			else if (btnName.equals("flat")) {
				isInFlat = true;
			}
		}

		/**
		 * �}�E�X�C�O�W�b�g�C�x���g
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseExited(MouseEvent e) {
			
			String btnName = e.getComponent().getName();
			
			if (!e.getComponent().isEnabled()) {
				return;
			}
			else if (animationTimer != null && animationTimer.isRunning()) {
				return;
			}
			
			if (btnName.equals("mz")) {
				isInMZ = false;
			}
			else if (btnName.equals("match")) {
				isInMZMatch = false;
			}
			else if (btnName.equals("chgColor")) {
				isInChgColor = false;
			}
			else if (btnName.equals("flat")) {
				isInFlat = false;
			}
		}

		/**
		 * �}�E�X�h���b�O�C�x���g
		 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
		 */
		public void mouseDragged(MouseEvent e) {
		}

		/**
		 * �}�E�X���[�u�C�x���g
		 * @see java.awt.event.MouseMotionListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseMoved(MouseEvent e) {
		}
		
		
		/**
		 * �O���t�����A�j���[�V����������N���X
		 * FunctionPain2�̃C���i�[�N���X
		 */
		class MoveAnimationTimer implements ActionListener {

			private String btnName = "";				// �{�^����
			
			private int moveXNum = 1;					// x���̈�x�ɉғ�����l
			private int moveYNum = 1;					// y���̈�x�ɉғ�����l
			
			/**
			 * �R���X�g���N�^
			 * @param btnName �{�^����
			 */
			public MoveAnimationTimer(String btnName) {
				this.btnName = btnName;
				moveXNum = (((int)maxMoveXPoint / 90) > 0) ? ((int)maxMoveXPoint / 90) : 1;
				moveYNum = (((int)maxMoveYPoint / 70) > 0) ? ((int)maxMoveYPoint / 70) : 1;
			}

			/**
			 * �A�N�V�����C�x���g
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				
				if (btnName.equals("xup")) {
					if (moveXPoint < maxMoveXPoint) {
						moveXPoint += moveXNum;
						moveXPoint = (moveXPoint > maxMoveXPoint) ? maxMoveXPoint : moveXPoint;
					}
					else {
						moveTimer.stop();
					}
				}
				else if (btnName.equals("xdown")) {
					if (moveXPoint > minMoveXPoint) {
						moveXPoint -= moveXNum;
						moveXPoint = (moveXPoint < minMoveXPoint) ? minMoveXPoint : moveXPoint;
					}
					else {
						moveTimer.stop();
					}
				}
				else if (btnName.equals("yup")) {
					if (moveYPoint < maxMoveYPoint) {
						moveYPoint += moveYNum;
						moveYPoint = (moveYPoint > maxMoveYPoint) ? maxMoveYPoint : moveYPoint;
					}
					else {
						moveTimer.stop();
					}
				}
				else if (btnName.equals("ydown")) {
					if (moveYPoint > minMoveYPoint) {
						moveYPoint -= moveYNum;
						moveYPoint = (moveYPoint < minMoveYPoint) ? minMoveYPoint : moveYPoint;
					}
					else {
						moveTimer.stop();
					}
				}
				else if (btnName.equals("top")) {
					
					boolean isMovedX = false;
					boolean isMovedY = false;
					if (moveXPoint > minMoveXPoint) {
						moveXPoint -= moveXNum;
						moveXPoint = (moveXPoint < minMoveXPoint) ? minMoveXPoint : moveXPoint;
					}
					else {
						isMovedX = true;
					}
					
					if (moveYPoint < maxMoveYPoint) {
						moveYPoint += moveYNum;
						moveYPoint = (moveYPoint > maxMoveYPoint) ? maxMoveYPoint : moveYPoint;
					}
					else {
						isMovedY = true;
					}
					
					if (isMovedX && isMovedY) {
						moveTimer.stop();
						// �}�E�X�J�[�\���ݒ�
						PackageViewPanel.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						allBtnCtrl(true);
					}
				}
				else if (btnName.equals("side")) {
					
					boolean isMovedX = false;
					boolean isMovedY = false;
					if (moveXPoint > minMoveXPoint) {
						moveXPoint -= moveXNum;
						moveXPoint = (moveXPoint < minMoveXPoint) ? minMoveXPoint : moveXPoint;
					}
					else {
						isMovedX = true;
					}
					
					if (moveYPoint > minMoveYPoint) {
						moveYPoint -= moveYNum;
						moveYPoint = (moveYPoint < minMoveYPoint) ? minMoveYPoint : moveYPoint;
					}
					else {
						isMovedY = true;
					}
					
					if (isMovedX && isMovedY) {
						moveTimer.stop();
						// �}�E�X�J�[�\���ݒ�
						PackageViewPanel.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						allBtnCtrl(true);
					}
				}
				else if (btnName.equals("flat")) {
					
					boolean isMovedX = false;
					boolean isMovedY = false;
					
					if (flat.isSelected()) {
						
						if (moveXPoint > 0) {
							moveXPoint -= moveXNum;
							moveXPoint = (moveXPoint < 0) ? 0 : moveXPoint;
						}
						else {
							isMovedX = true;
						}
						
						if (moveYPoint > 0) {
							moveYPoint -= moveYNum;
							moveYPoint = (moveYPoint < 0) ? 0 : moveYPoint;
						}
						else {
							isMovedY = true;
						}
						
						if (isMovedX && isMovedY) {
							moveTimer.stop();
							// �}�E�X�J�[�\���ݒ�
							PackageViewPanel.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
							allBtnCtrl(true);
							
							// �e�O���t���{�^���̐���
							xAxisDown.setEnabled(false);
							xAxisUp.setEnabled(false);
							yAxisDown.setEnabled(false);
							yAxisUp.setEnabled(false);
							topAngleBtn.setEnabled(false);
							sideAngleBtn.setEnabled(false);
						}
					}
					else {
						
						if (moveXPoint < tmpMoveXPoint) {
							moveXPoint += moveXNum;
							moveXPoint = (moveXPoint > tmpMoveXPoint) ? tmpMoveXPoint : moveXPoint;
						}
						else {
							isMovedX = true;
						}
						
						if (moveYPoint < tmpMoveYPoint) {
							moveYPoint += moveYNum;
							moveYPoint = (moveYPoint > tmpMoveYPoint) ? tmpMoveYPoint : moveYPoint;
						}
						else {
							isMovedY = true;
						}
						
						if (isMovedX && isMovedY) {
							moveTimer.stop();
							// �}�E�X�J�[�\���ݒ�
							PackageViewPanel.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
							allBtnCtrl(true);
							
							tmpMoveXPoint = -1;
							tmpMoveYPoint = -1;
						}
					}
				}
				PackageViewPanel.this.repaint();
			}
		}
	}

	/**
	 * �e�[�u�����X�g�Z���N�g���X�i�[�N���X
	 * PackageViewPanel�̃C���i�[�N���X
	 */
	class LmSelectionListener implements ListSelectionListener {

		/**
		 * �o�����[�`�F���W�C�x���g
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			
			// �I���s�����ɂ��邽�߃X�N���[���o�[�ʒu�ݒ�
			int selRow = recTable.getSelectedRow();
			int selCol = recTable.getSelectedColumn();
			Rectangle cellRect = recTable.getCellRect(selRow, selCol, false	);
			if(cellRect != null) {
				recTable.scrollRectToVisible( cellRect );
			}
			
			PackageViewPanel.this.repaint();
		}
	}
	
	/**
	 * �e�[�u���}�E�X���X�i�[�N���X
	 * PackageViewPanel�̃C���i�[�N���X
	 */
	class TblMouseListener extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			
			if (!SwingUtilities.isLeftMouseButton(e)) {
				return;
			}
			
			Point p = e.getPoint();
			int selRow = recTable.rowAtPoint(p);
			int selCol = recTable.columnAtPoint(p);
			
			if (selRow == -1 || selCol == -1) {
				return;
			}
			
			// �`�F�b�N�{�b�N�X�̑I������
			int disableCol = recTable.getColumnModel().getColumnIndex(SearchPage.COL_LABEL_DISABLE);
			if (selCol == disableCol) {
				recTable.setValueAt(
						!Boolean.parseBoolean(String.valueOf(recTable.getValueAt(selRow, disableCol))),
						selRow, 
						disableCol);
				return;
			}
			
			// ���R�[�h�y�[�W�\������
			if (e.getClickCount() == 2) {
				int siteCol = recTable.getColumnModel().getColumnIndex(SearchPage.COL_LABEL_CONTRIBUTOR);
				if (String.valueOf(recTable.getValueAt(selRow, siteCol)).equals("")) {
					return;
				}
				
				showRecordPage(selRow);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			
			if (SwingUtilities.isRightMouseButton(e)) {
				isDragCancel = true;
				dragRowIndex = -1;
				PackageViewPanel.this.setCursor(Cursor.getDefaultCursor());
				recTable.repaint();
				return;
			}
			if (!SwingUtilities.isLeftMouseButton(e)) {
				return;
			}
			
			isDragCancel = false;
			
			Point p = e.getPoint();
			pressRowIndex = recTable.rowAtPoint(p);
			PackageViewPanel.this.repaint();
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			
			// �E�����[�X�̏ꍇ
			if (SwingUtilities.isRightMouseButton(e)) {
				recListPopup(e);
			}
			
			if (!SwingUtilities.isLeftMouseButton(e)) {
				return;
			}
			if (isDragCancel) {
				return;
			}
			
			dragRowIndex = -1;
			
			Point p = e.getPoint();
		    releaseRowIndex = recTable.rowAtPoint(p);
			
			if (!isSortStatus() && pressRowIndex != releaseRowIndex && releaseRowIndex != -1) {
				// ���R�[�h�h���b�v(�ړ�)
				DefaultTableModel dataModel = (DefaultTableModel)recSorter.getTableModel();
				dataModel.moveRow(pressRowIndex, pressRowIndex, releaseRowIndex);
				recTable.setRowSelectionInterval(releaseRowIndex, releaseRowIndex);
				
				// �f�[�^�N���X�\�[�g
				specData.sortRecInfo(recTable);
			}
			recTable.repaint();
			setCursor(Cursor.getDefaultCursor());
		}
	}
	
	/**
	 * �e�[�u���}�E�X���[�V�������X�i�[�N���X
	 * PackageViewPanel�̃C���i�[�N���X
	 */
	class TblMouseMotionListener extends MouseMotionAdapter {
		
		@Override
		public void mouseDragged(MouseEvent e) {
			super.mouseDragged(e);
			
			if (!SwingUtilities.isLeftMouseButton(e)) {
				return;
			}
			if (isDragCancel) {
				recTable.setRowSelectionInterval(pressRowIndex, pressRowIndex);
				return;
			}
			
			Point p = e.getPoint();
			dragRowIndex = recTable.rowAtPoint(p);
			
			// �J�[�\���ύX
			Cursor cursor = Cursor.getDefaultCursor();
			if ( !isSortStatus()  && pressRowIndex != dragRowIndex) {
				try {
					cursor = Cursor.getSystemCustomCursor("MoveDrop.32x32");
				} catch (Exception ex) {
					// �h���b�v�p�V�X�e���J�[�\�������݂��Ȃ��ꍇ�A�J�[�\���ύX�Ȃ�
				}
			}
			else {
				try {
					cursor = Cursor.getSystemCustomCursor("MoveNoDrop.32x32");
				} catch (Exception ex) {
					// �h���b�v�s�p�V�X�e���J�[�\�������݂��Ȃ��ꍇ�A�J�[�\���ύX�Ȃ�
				}				
			}
			PackageViewPanel.this.setCursor(cursor);
			
			// �h���b�O���͑I���s���ړ����Ȃ�
			recTable.setRowSelectionInterval(pressRowIndex, pressRowIndex);
			recTable.repaint();
		}
	}
	
	/**
	 * �e�[�u���L�[���X�i�[�N���X
	 * PackageViewPanel�̃C���i�[�N���X
	 */
	class TblKeyListener extends KeyAdapter {
		
		@Override
		public void keyReleased(KeyEvent e) {
			super.keyReleased(e);
			
			// ESC�L�[�v���X�Ńh���b�O�C�x���g����
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				isDragCancel = true;
				dragRowIndex = -1;
				PackageViewPanel.this.setCursor(Cursor.getDefaultCursor());
				recTable.repaint();
			}
		}
	}
	
	/**
	 * �e�[�u�������_���[
	 * PackageViewPanel�̃C���i�[�N���X
	 * �I���W�i�������_���[�B
	 */
	class TblRenderer extends DefaultTableCellRenderer {
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			if(isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			}else{
				setForeground(table.getForeground());
				setBackground(table.getBackground());
			}
			
			// ��\�����R�[�h�̕����F�ύX
			int disableCol = recTable.getColumnModel().getColumnIndex(SearchPage.COL_LABEL_DISABLE);
			if (Boolean.parseBoolean(String.valueOf(recTable.getValueAt(row, disableCol)))) {
				if (isSelected) {
					setForeground(Color.GRAY);
				}
				else {
					setForeground(Color.LIGHT_GRAY);	
				}
			}
			
			if (value instanceof Boolean) {
				if (column == disableCol) {
					// �`�F�b�N�{�b�N�X�ԋp
					JCheckBox obj = new JCheckBox(null, null, ((Boolean)table
							.getValueAt(row, column)).booleanValue());
					if (isSelected) {
						obj.setBackground(table.getSelectionBackground());
					} else {
						obj.setBackground(table.getBackground());
					}
					obj.setHorizontalAlignment(CENTER);
					return obj;
				}
			}
			return this;
		}
	}
	
	/**
	 * �y�C���}�E�X���X�i�[
	 * PackageViewPanel�̃C���i�[�N���X
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
}
