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
 * �s�[�N�p�l�� �N���X
 *
 * ver 1.0.10 2011.08.10
 *
 ******************************************************************************/

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import massbank.MassBankCommon;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * �s�[�N�p�l�� �N���X
 */
@SuppressWarnings("serial")
public class PeakPanel extends JPanel {

	public static final int INTENSITY_MAX = 1000;	// �ő勭�x

	private static final int MARGIN = 12;				// �}�[�W��
	
	private static final int MASS_RANGE_MIN = 5;		// �ŏ��}�X�����W

	private static int massRangeMax = 0;
	
	private PeakData peaks1 = null;
	private PeakData peaks2 = null;

	private double massStart = 0;
	private double massRange = 0;

	private int intensityRange = INTENSITY_MAX;

	private boolean head2tail = false;	// ��r�p�p�l���t���O

	private Point fromPos = null;			// �h���b�O�J�n�|�C���g
	private Point toPos = null;			// �h���b�O�I���|�C���g
	
	private double xscale = 0;
	
	private SearchPage searchPage = null;			// SearchPage�I�u�W�F�N�g

	private String tolVal = null;					// Tolerance���͒l
	private boolean tolUnit = true;				// Tolerance�P�ʑI��l�itrue�Funit�Afalse�Fppm�j
	
	private Point cursorPoint = null;				// �}�E�X�J�[�\���|�C���g
	
	private String typeLbl1 = " ";								// �X�y�N�g�����1������
	private String typeLbl2 = " ";								// �X�y�N�g�����2������
	
	public static final String SP_TYPE_QUERY = "Query";		// �X�y�N�g����ʁi�N�G���[�j
	public static final String SP_TYPE_COMPARE = "Compare";	// �X�y�N�g����ʁi��r�j
	public static final String SP_TYPE_RESULT = "Result";		// �X�y�N�g����ʁi���ʁj
	
	private static final String SP_TYPE_MERGED = "MERGED SPECTRUM";	// �X�y�N�g����ʁi�����j
	private int TYPE_LABEL_1 = 1;								// ���x��1
	private int TYPE_LABEL_2 = 2;								// ���x��2
	
	private JLabel nameLbl = null;					// �����������x��

	private String precursor = "";					// �v���J�[�T�[
	
	private boolean isNoPeak = false;

	private ArrayList<String> selectPeakList = null;

	private long lastClickedTime = 0;

	private JButton leftMostBtn = null;
	private JButton leftBtn = null;
	private JButton rightBtn = null;
	private JButton rightMostBtn = null;

	private JToggleButton mzDisp = null;
	private JToggleButton mzHitDisp = null;

	private static boolean isInitRate = false;	// �����{���t���O(true:���g��Afalse:�g�咆)

	public BufferedImage structImgM = null;
	public BufferedImage structImgS = null;
	public String formula = "";
	public String emass = "";

	/**
	 * �R���X�g���N�^
	 * @param isHead2Tail ��r�p�p�l���t���O�itrue�F��r�p�p�l���Afalse�F��r�p�p�l���ȊO�j
	 */
	public PeakPanel(boolean isHead2Tail) {
		selectPeakList = new ArrayList<String>();
		head2tail = isHead2Tail;
		
		if ( head2tail ) {
			typeLbl1 = SP_TYPE_COMPARE;
			typeLbl2 = " ";
		}
		
		GridBagConstraints gbc = null;						// ���C�A�E�g����I�u�W�F�N�g
		GridBagLayout gbl = new GridBagLayout();
		
		JPanel typePane1 = new TypePane(TYPE_LABEL_1, new Color(153 , 153, 153), 16);
		typePane1.setMinimumSize(new Dimension(22, 76));
		typePane1.setPreferredSize(new Dimension(22, 76));
		typePane1.setMaximumSize(new Dimension(22, 76));
		
		JPanel typePane2 = new TypePane(TYPE_LABEL_2, new Color(0 , 0, 255), 9);
		typePane2.setPreferredSize(new Dimension(22, 0));
		
		JPanel typePane = new JPanel();
		typePane.setLayout(new BoxLayout(typePane, BoxLayout.Y_AXIS));
		typePane.add(typePane1);
		typePane.add(typePane2);
		
		gbc = new GridBagConstraints();						// ���C�A�E�g���񏉊���
		gbc.fill = GridBagConstraints.VERTICAL;				// �����T�C�Y�̕ύX�݂̂�����
		gbc.weightx = 0;									// �]���̐����X�y�[�X�𕪔z���Ȃ�
		gbc.weighty = 1;									// �]���̐����X�y�[�X�𕪔z
		gbc.gridheight = GridBagConstraints.REMAINDER;		// �s�Ō�̃R���|�[�l���g�Ɏw��
		gbl.setConstraints(typePane, gbc);	
		
		
		PlotPane plotPane = new PlotPane();		
		
		gbc = new GridBagConstraints();						// ���C�A�E�g���񏉊���
		gbc.fill = GridBagConstraints.BOTH;					// �����A�����T�C�Y�̕ύX������
		gbc.weightx = 1;									// �]���̐����X�y�[�X�𕪔z
		gbc.weighty = 1;									// �]���̐����X�y�[�X�𕪔z
		gbc.gridwidth = GridBagConstraints.REMAINDER;		// ��Ō�̃R���|�[�l���g�Ɏw��
		gbl.setConstraints(plotPane, gbc);
		
		
		ButtonPane btnPane =new ButtonPane();	
		
		gbc = new GridBagConstraints();						// ���C�A�E�g���񏉊���
		gbc.fill = GridBagConstraints.BOTH;					// �����A�����T�C�Y�̕ύX������
		gbc.weightx = 1;									// �]���̐����X�y�[�X�𕪔z
		gbc.weighty = 0;									// �]���̐����X�y�[�X�𕪔z���Ȃ�
		gbc.gridwidth = GridBagConstraints.REMAINDER;		// ��Ō�̃R���|�[�l���g�Ɏw��
		gbc.gridheight = GridBagConstraints.REMAINDER;		// �s�Ō�̃R���|�[�l���g�Ɏw��
		gbl.setConstraints(btnPane, gbc);
		
		setLayout(gbl);
		add(typePane);
		add(plotPane);
		add(btnPane);
	}

	/**
	 * �X�y�N�g����ʃy�C���N���X
	 * PeakPanel�̃C���i�[�N���X
	 */
	class TypePane extends JPanel {
		
		private int lblNo = -1;
		private Color fontColor = new Color(0, 0, 0);
		private int fontSize = 1;
		
		/**
		 * �f�t�H���g�R���X�g���N�^
		 * @deprecated
		 */
		private TypePane() {
		}
		
		/**
		 * �R���X�g���N�^
		 * @param lbl ���x���ԍ�
		 * @param color �t�H���g�J���[
		 * @param size �t�H���g�T�C�Y
		 */
		public TypePane(int lbl, Color color, int size) {
			this.lblNo = lbl;
			this.fontColor = color;
			this.fontSize = size;
		}
		
		/**
		 * �y�C���g�R���|�[�l���g
		 * @param g
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
	    public void paintComponent(Graphics g) {

			// �X�y�N�g�����\������Ă���ꍇ�̂ݏ������s���i�s�[�N���Ȃ��ꍇ���\���j
			if ((!head2tail && peaks1 != null) 
					|| (head2tail && peaks2 != null)
					|| (!head2tail && peaks1 == null && isNoPeak) ) {
				
		        Graphics2D g2 = (Graphics2D)g;
		        
		        // �F�Z�b�g
		        g2.setPaint(fontColor);
		        
		        // �A���`�G�C���A�X
		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		        FontRenderContext frc = new FontRenderContext(null, true, true);
		        Font font;
		        Shape shape;
		        if (lblNo == TYPE_LABEL_1) {
		        	font = new Font("Arial", Font.ITALIC, fontSize);
		        	shape = new TextLayout(typeLbl1, font, frc).getOutline(null);
		        }
		        else {
		        	font = new Font("Arial", Font.ITALIC | Font.BOLD, fontSize);
		        	shape = new TextLayout(typeLbl2, font, frc).getOutline(null);
		        }
		        Rectangle2D b = shape.getBounds();
		        
		        // �`��ʒu�ϊ��i��]�j�I�u�W�F�N�g�擾
		        AffineTransform at1 = AffineTransform.getRotateInstance(Math.toRadians(-90), b.getX(), b.getY());
		        
		        // �`��ʒu�ϊ��i���s�ړ��j�I�u�W�F�N�g�擾
		        AffineTransform at2;
		        if (lblNo == TYPE_LABEL_1) {
		        	at2 = AffineTransform.getTranslateInstance(3, b.getWidth() + b.getHeight() + 5);
		        }
		        else {
		        	at2 = AffineTransform.getTranslateInstance(7, getHeight() + 1);
		        }
		        // �ϊ���K�p���ĕ`��
		        g2.fill(at2.createTransformedShape(at1.createTransformedShape(shape)));
	    	}
	    }
	}
	
	/**
	 * �X�y�N�g���\���y�C��
	 * PeakPanel�̃C���i�[�N���X
	 */
	class PlotPane extends JPanel implements MouseListener, MouseMotionListener {
		
		private JPopupMenu selectPopup = null;			// �s�[�N�I���|�b�v�A�b�v���j���[
		private JPopupMenu contextPopup = null;		// �R���e�L�X�g�|�b�v�A�b�v���j��

		private Timer timer = null;					// �g�又���p�^�C�}�[�I�u�W�F�N�g
		
		private boolean underDrag = false;			// �h���b�O���t���O
		
		private final int STATUS_NORAML = 0;			// �s�[�N�`��p�X�e�[�^�X�iNOMAL�j
		private final int STATUS_NEXT_LAST = 1;		// �s�[�N�`��p�X�e�[�^�X�iNEXTLAST�j
		private final int STATUS_CLOSED = 2;			// �s�[�N�`��p�X�e�[�^�X�iCLOSED�j
		
		private final Color onCursorColor = Color.blue;		// �J�[�\����F
		
		/**
		 * �R���X�g���N�^
		 */
		public PlotPane() {
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
		 * �y�C���g�R���|�[�l���g
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			int width = getWidth();
			int height = getHeight();
			xscale = (width - 2.0d * MARGIN) / massRange;
			double yscale = (height - 2.0d * MARGIN) / intensityRange;
			// �w�i�𔒂ɂ���
			g.setColor(Color.white);
			g.fillRect(0, 0, width, height);

			if ( !head2tail && peaks1 != null) {
				boolean isSizeM = false;
				// �\�����̉摜��\������
				if ( structImgM != null && height > structImgM.getHeight() ) {
					g.drawImage(structImgM, (width - structImgM.getWidth()), 0, null);
					isSizeM = true;
				}
				else if ( structImgS != null && height > structImgS.getHeight() ) {
					g.drawImage(structImgS, (width - structImgS.getWidth()), 5, null);
					isSizeM = false;
				}

				// FORMULA, EXACT MASS��\������
				if ( !formula.equals("") ) {
					String info = formula + " (" + emass + ")";
					int xPos = 0;
					int fontSize = 0;
					if ( isSizeM ) {
						xPos = width - info.length() * 7;
						fontSize = 12;
					}
					else {
						xPos = width - info.length() * 6;
						fontSize = 10;
					}
					g.setFont(new Font("SansSerif",Font.BOLD,fontSize));
					g.setColor(new Color(0x008000));
					g.drawString(info, xPos - 2, 12);
				}
			}

			g.setFont(g.getFont().deriveFont(9.0f));
			g.setColor(Color.lightGray);
			if (!head2tail) {
				// �ڐ����`��
				g.drawLine(MARGIN, MARGIN, MARGIN, height - MARGIN);
				g.drawLine(MARGIN, height - MARGIN, width - MARGIN, height
						- MARGIN);
				// x��
				int step = stepCalc((int)massRange);
				int start = (step - (int)massStart % step) % step;
				for (int i = start; i < (int)massRange; i += step) {
					g.drawLine(MARGIN + (int)(i * xscale), height - MARGIN,
							MARGIN + (int)(i * xscale), height - MARGIN + 2);
					g.drawString(formatMass(i + massStart, true), MARGIN
							+ (int)(i * xscale) - 5, height - 1);
				}
				// y��
				for (int i = 0; i <= intensityRange; i += intensityRange / 5) {
					g.drawLine(MARGIN - 2,
							height - MARGIN - (int)(i * yscale), MARGIN,
							height - MARGIN - (int)(i * yscale));
					g.drawString(String.valueOf(i), 0, height - MARGIN
							- (int)(i * yscale));
				}
			} else {
				// HEAD2TAIL
				// �ڐ����`��
				g.drawLine(MARGIN, MARGIN, MARGIN, height - MARGIN);
				g.drawLine(MARGIN, height / 2, width - MARGIN, height / 2);
				// x��
				int step = stepCalc((int)massRange);
				int start = (step - (int)massStart % step) % step;
				for (int i = start; i < (int)massRange; i += step) {
					g.drawLine(MARGIN + (int)(i * xscale), height / 2 + 1,
							MARGIN + (int)(i * xscale), height / 2 - 1);

					g.drawString(formatMass(i + massStart, true), MARGIN
							+ (int)(i * xscale) - 5, height - 1);
				}
				// y��
				for (int i = 0; i <= intensityRange; i += intensityRange / 5) {
					g.drawLine(MARGIN - 2, height / 2 - (int)(i * yscale) / 2,
							MARGIN, height / 2 - (int)(i * yscale) / 2);

					g.drawString(String.valueOf(i), 0, height / 2
							- (int)(i * yscale) / 2);

					g.drawLine(MARGIN - 2, height / 2 + (int)(i * yscale) / 2,
							MARGIN, height / 2 + (int)(i * yscale) / 2);

					g.drawString(String.valueOf(i), 0, height / 2
							+ (int)(i * yscale) / 2);
				}
			}

			// �N�G���p�p�l���A�������ʗp�p�l��
			if (!head2tail) {
				int start, end;
				if (peaks1 != null) {
					int its, x, y, w, h;
					double mz;
					boolean isOnPeak;		// �J�[�\���s�[�N��t���O
					boolean isSelectPeak;	// �I���ς݃s�[�N�t���O
					
					start = peaks1.getIndex(massStart);
					end = peaks1.getIndex(massStart + massRange);
					
					for (int i=start; i<end; i++) {
						
						mz = peaks1.getMz(i);
						its = peaks1.getIntensity(i);
						isOnPeak = false;
						isSelectPeak = peaks1.isSelectPeakFlag(i);
						
						x = MARGIN + (int)((mz - massStart) * xscale) - (int)Math.floor(xscale / 8);
						y = height - MARGIN - (int)(its * yscale);
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
						} else if (w < 3) {
							w = 3;
						}
						
						// y����荶���ɂ͕`�悵�Ȃ��悤�ɒ���
						if (MARGIN >= x) {
							w = (w - (MARGIN - x) > 0) ? (w - (MARGIN - x)) : 1;
							x = MARGIN + 1;
						}

						// �J�[�\���s�[�N�㔻��
						if (x <= cursorPoint.getX() 
								&& cursorPoint.getX() <= (x + w)
								&& y <= cursorPoint.getY() 
								&& cursorPoint.getY() <= (y + h)) {
							
							isOnPeak = true;
						}
						
						
						// m/z�l�APeak�`��
						g.setColor(Color.black);
						g.setFont(g.getFont().deriveFont(9.0f));
						if (isOnPeak) {
							g.setColor(onCursorColor);
							g.setFont(g.getFont().deriveFont(14.0f));
							if (isSelectPeak) {
								g.setColor(Color.cyan.darker());
							}
							g.drawString(formatMass(mz, false), x, y);
						}
						else if (isSelectPeak) {
							g.setColor(Color.cyan.darker());
							g.drawString(formatMass(mz, false), x, y);
						}
						else if (mzDisp.isSelected()) {
							if (its > intensityRange * 0.4) {
								g.setColor(Color.red);
							}
							g.drawString(formatMass(mz, false), x, y);
							g.setColor(Color.black);
						}
						else {
							if (its > intensityRange * 0.4) {
								g.drawString(formatMass(mz, false), x, y);
							}
						}
						// fill3DRect���\�b�h�ő�3�����A��4������0���w�肳����
						// �������`��ł��Ȃ��̂Œ��ӁiJava�̃o�O�j
						g.fill3DRect(x, y, w, h, true);
						
						
						if (isOnPeak || isSelectPeak) {
							// ���x�ڐ���`��
							if (isOnPeak) {
								g.setColor(onCursorColor);
							}
							if (isSelectPeak) {
								g.setColor(Color.cyan.darker());
							}
							g.drawLine(MARGIN + 4, y, MARGIN - 4, y);

							// ���x�`��
							g.setColor(Color.lightGray);
							g.setFont(g.getFont().deriveFont(9.0f));
							if (isOnPeak && isSelectPeak) {
								g.setColor(Color.gray);
							}
							g.drawString(String.valueOf(its), MARGIN + 7, y + 1);
						}
					}

					// �v���J�[�T�[m/z�ɎO�p�}�[�N�t��
					if ( !precursor.equals("") ) {
						
						int pre = Integer.parseInt(precursor);
						int preX = MARGIN + (int)((pre - massStart) * xscale) - (int)Math.floor(xscale / 8);

						// �v���J�[�T�[m/z���O���t���̏ꍇ�̂ݕ`��
						if ( preX >= MARGIN 
								&& preX <= width - MARGIN ) {
							
							int[] xp = { preX, preX+6, preX-6 };
							int[] yp = { height - MARGIN, height-MARGIN+5, height-MARGIN+5 };
							g.setColor( Color.RED );
							g.fillPolygon( xp, yp, 3 );
						}
					}
					
					allBtnCtrl(true);
				}
				// �s�[�N���Ȃ��ꍇ
				else if (isNoPeak) {
					g.setFont(new Font("Arial", Font.ITALIC, 24));
					g.setColor(Color.lightGray);
					g.drawString("No peak was observed.", width / 2 - 110,
							height / 2);
					allBtnCtrl(false);
				} else {
					selectPeakList.clear();
					allBtnCtrl(false);
				}
			}
			// ��r�p�p�l��
			else if (peaks2 != null) {
				
				// ���ʃs�[�N�����o
				int start1 = peaks1.getIndex(massStart);
				int end1 = peaks1.getIndex(massStart + massRange);
				int start2 = peaks2.getIndex(massStart);
				int end2 = peaks2.getIndex(massStart + massRange);
				if (end1 > 0) {
					end1 -= 1;
				}
				if (end2 > 0) {
					end2 -= 1;
				}
				int ind1 = start1;
				int ind2 = start2;
				double mz1 = peaks1.getMz(ind1);
				double mz2 = peaks2.getMz(ind2);
				int its1 = peaks1.getIntensity(ind1);
				int its2 = peaks2.getIntensity(ind2);

				int x = 0, y = 0, y2 = 0, w = 0, h = 0, h2 = 0;
				boolean isMz1Update = false;
				boolean isMz2Update = false;
				int mz1status = STATUS_NORAML;
				int mz2status = STATUS_NORAML;
				if (peaks1.getMz(end1) < massStart) {
					mz1status = STATUS_CLOSED;
				}
				if (peaks2.getMz(end2) < massStart) {
					mz2status = STATUS_CLOSED;
				}
				boolean isMatchPeak = false; // ���S��v�s�[�N�v���O

				while (mz1status < STATUS_CLOSED || mz2status < STATUS_CLOSED) {
					isMz1Update = false;
					isMz2Update = false;
					isMatchPeak = false;
					if (ind1 == end1 && mz1status == STATUS_NORAML) {
						mz1status = STATUS_NEXT_LAST;
					}
					if (ind2 == end2 && mz2status == STATUS_NORAML) {
						mz2status = STATUS_NEXT_LAST;
					}

					w = (int) (xscale / 8);
					
					// �`��p�����[�^�i���j����
					if (w < 2) {
						w = 2;
					} else if (w < 3) {
						w = 3;
					}
					
					g.setColor(Color.black);
					
					if (mz1 == mz2) {
						
						isMatchPeak = true;
						x = MARGIN + (int)((mz1 - massStart) * xscale) - (int)Math.floor(xscale / 8);
						y = height / 2 - (int)((its1 * yscale) / 2);
						y2 = height / 2 + 1;
						h = (int)((its1 * yscale) / 2);
						h2 = (int)((its2 * yscale) / 2);

						// �`��p�����[�^�i�����A�ʒu�j����
						if (h == 0) {
							h = 1;
						}
						if (h2 == 0) {
							h2 = 1;
						}

						
						// y����荶���ɂ͕`�悵�Ȃ��悤�ɒ���
						if (MARGIN > x) {
							w = (w - (MARGIN - x) > 0) ? (w - (MARGIN - x)) : 1;
							x = MARGIN + 1;
						}
						
						// m/z�l�`��
						if (mzDisp.isSelected()) {
							if ((int)(its1 * yscale) / 2 >= ((height - MARGIN * 2) / 2) * 0.4) {
								g.setColor(Color.red);
							}
							g.drawString(formatMass(mz1, false), x, y);
							g.setColor(Color.black);

							if ((int)(its2 * yscale) / 2 >= ((height - MARGIN * 2) / 2) * 0.4) {
								g.setColor(Color.red);
							}
							g.drawString(formatMass(mz2, false), x, (y2 + h2 + 7));
							g.setColor(Color.black);
						} else {
							if (!mzHitDisp.isSelected()) {
								if ((int)(its1 * yscale) / 2 >= ((height - MARGIN * 2) / 2) * 0.4) {
									g.drawString(formatMass(mz1, false), x, y);
								}
								if ((int)(its2 * yscale) / 2 >= ((height - MARGIN * 2) / 2) * 0.4) {
									g.drawString(formatMass(mz2, false), x, (y2
											+ h2 + 7));
								}
							}
						}

						// ���x��Cutoff�ȏ�̃s�[�N�̏ꍇ�ɐF�Â�
						if (its1 >= SearchPage.CUTOFF_THRESHOLD
								&& its2 >= SearchPage.CUTOFF_THRESHOLD) {
							// �`��F��ԐF�ɃZ�b�g
							g.setColor(Color.red);

							if (mzHitDisp.isSelected()) {
								g.drawString(formatMass(mz1, false), x, y);
								g.drawString(formatMass(mz2, false), x,
										(y2 + h2 + 7));
							}
						}

						if (mz1status == STATUS_NEXT_LAST) {
							mz1status = STATUS_CLOSED;
						}
						if (mz2status == STATUS_NEXT_LAST) {
							mz2status = STATUS_CLOSED;
						}
						isMz1Update = true;
						isMz2Update = true;
					} else if ((mz2 < mz1 && mz2status != STATUS_CLOSED)
							|| mz1status == STATUS_CLOSED) {
						
						// mz2(�Ώۂ̉�����)�̍��W���Z�b�g
						x = MARGIN + (int)((mz2 - massStart) * xscale) - (int)Math.floor(xscale / 8);
						y = height / 2 + 1;
						h = (int)(its2 * yscale / 2);
						
						// �`��p�����[�^�i�����A�ʒu�j����
						if (h == 0) {
							h = 1;
						}

						// y����荶���ɂ͕`�悵�Ȃ��悤�ɒ���
						if (MARGIN > x) {
							w = (w - (MARGIN - x) > 0) ? (w - (MARGIN - x)) : 1;
							x = MARGIN + 1;
						}
						
						// m/z�l�`��
						if (mzDisp.isSelected()) {
							if (h >= ((height - MARGIN * 2) / 2) * 0.4) {
								g.setColor(Color.red);
							}
							g.drawString(formatMass(mz2, false), x, (y + h + 7));
							g.setColor(Color.black);
						} else if (!mzHitDisp.isSelected()
								&& h >= ((height - MARGIN * 2) / 2) * 0.4) {
							g.drawString(formatMass(mz2, false), x, (y + h + 7));
						}

						// mz2(�Ώۂ̉�����)��Tolerance�����`�F�b�N
						if (checkTolerance(true, mz2, its2, peaks1)) {
							// �`��F���}�[���^�F�ɃZ�b�g
							g.setColor(Color.magenta);
							if (mzHitDisp.isSelected()) {
								g.drawString(formatMass(mz2, false), x,
										(y + h + 7));
							}
						}

						if (mz2status == STATUS_NEXT_LAST) {
							mz2status = STATUS_CLOSED;
						}
						isMz2Update = true;
					} else if ((mz1 < mz2 && mz1status != STATUS_CLOSED)
							|| mz2status == STATUS_CLOSED) {
						
						// mz1(�N�G���l)�̍��W���Z�b�g
						x = MARGIN + (int)((mz1 - massStart) * xscale) - (int)Math.floor(xscale / 8);
						y = height / 2 - (int)((its1 * yscale) / 2);
						h = (int)((its1 * yscale) / 2);
						
						// �`��p�����[�^�i�����A�ʒu�j����
						if (h == 0) {
							h = 1;
						}
						
						// y����荶���ɂ͕`�悵�Ȃ��悤�ɒ���
						if (MARGIN > x) {
							w = (w - (MARGIN - x) > 0) ? (w - (MARGIN - x)) : 1;
							x = MARGIN + 1;
						}
						
						// m/z�l�`��
						if (mzDisp.isSelected()) {
							if (h >= ((height - MARGIN * 2) / 2) * 0.4) {
								g.setColor(Color.red);
							}
							g.drawString(formatMass(mz1, false), x, y);
							g.setColor(Color.black);
						} else if (!mzHitDisp.isSelected()
								&& h >= ((height - MARGIN * 2) / 2) * 0.4) {
							g.drawString(formatMass(mz1, false), x, y);
						}

						// mz2(�Ώۂ̉�����)��Tolerance���̂��̂����邩�`�F�b�N
						if (checkTolerance(false, mz1, its1, peaks2)) {
							// �`��F��ԐF�ɃZ�b�g
							g.setColor(Color.red);
							if (mzHitDisp.isSelected()) {
								g.drawString(formatMass(mz1, false), x, y);
							}
						}

						if (mz1status == STATUS_NEXT_LAST) {
							mz1status = STATUS_CLOSED;
						}
						isMz1Update = true;
					} else {
					}

					// �s�[�N�`��
					g.fill3DRect(x, y, w, h, true);
					if (isMatchPeak) {
						g.fill3DRect(x, y2, w, h2, true);
					}
					g.setColor(Color.black);

					if (isMz1Update) {
						if (ind1 < end1) {
							mz1 = peaks1.getMz(++ind1);
							its1 = peaks1.getIntensity(ind1);
						}
					}
					if (isMz2Update) {
						if (ind2 < end2) {
							mz2 = peaks2.getMz(++ind2);
							its2 = peaks2.getIntensity(ind2);
						}
					}
				}
				allBtnCtrl(true);
			} else {
				allBtnCtrl(false);
				if (head2tail) {
					mzHitDisp.setSelected(false);
					mzHitDisp.setEnabled(false);
				}
			}

			// �X�y�N�g�����\������Ă���ꍇ�̂ݏ������s��
			if ((!head2tail && peaks1 != null) || (head2tail && peaks2 != null)) {

				if (underDrag) {// �}�E�X�Ńh���b�O�����̈�����F�����ň͂�
					g.setXORMode(Color.white);
					g.setColor(Color.yellow);
					int xpos = Math.min(fromPos.x, toPos.x);
					width = Math.abs(fromPos.x - toPos.x);
					g.fillRect(xpos, MARGIN, width, height - MARGIN * 2);
					g.setPaintMode();
				}
			}
		}

		/**
		 * �}�E�X�v���X�C�x���g
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				if (timer != null && timer.isRunning()) {
					return;
				}

				fromPos = toPos = e.getPoint();
			}
		}

		/**
		 * �}�E�X�h���b�O�C�x���g
		 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
		 */
		public void mouseDragged(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				if (timer != null && timer.isRunning()) {
					return;
				}

				underDrag = true;
				toPos = e.getPoint();
				PeakPanel.this.repaint();
			}
		}

		/**
		 * �}�E�X�����[�X�C�x���g
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent e) {
			// �������[�X�̏ꍇ
			if (SwingUtilities.isLeftMouseButton(e)) {
				if (!underDrag || (timer != null && timer.isRunning())) {
					return;
				}
				underDrag = false;
				if ((fromPos != null) && (toPos != null)) {
					if (Math.min(fromPos.x, toPos.x) < 0)
						massStart = Math.max(0, massStart - massRange / 3);

					else if (Math.max(fromPos.x, toPos.x) > getWidth())
						massStart = Math.min(massRangeMax - massRange, massStart
								+ massRange / 3);
					else {
						// �h���b�O���Y�[���C�����������ύX
						if ((!head2tail && peaks1 != null)
								|| (head2tail && peaks2 != null)) {

							PeakPanel.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
							
							isInitRate = false;
							
							timer = new Timer(30,
									new AnimationTimer(Math.abs(fromPos.x - toPos.x),
											Math.min(fromPos.x, toPos.x)));
							timer.start();
						} else {
							fromPos = toPos = null;
							PeakPanel.this.repaint();
						}
					}
				}
			}
			// �E�����[�X�̏ꍇ
			else if (SwingUtilities.isRightMouseButton(e)) {
				
				if (timer != null && timer.isRunning()) {
					return;
				}
				
				// ��r�p�X�y�N�g���p�l���ɂ͕\�����Ȃ�
				if (head2tail) {
					return;
				}
				
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
				
				if (peaks1 != null) {
					if (selectPeakList.size() != 0) {
						item1.setEnabled(true);
						item2.setEnabled(true);
					}
				}
				
				// �|�b�v�A�b�v���j���[�\��
				contextPopup.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		/**
		 * �}�E�X�N���b�N�C�x���g
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent e) {
			if (timer != null && timer.isRunning()) {
				return;
			}

			// ���N���b�N�̏ꍇ
			if (SwingUtilities.isLeftMouseButton(e)) {

				// �N���b�N�Ԋu�Z�o
				long interSec = (e.getWhen() - lastClickedTime);
				lastClickedTime = e.getWhen();

				// �_�u���N���b�N�̏ꍇ�i�N���b�N�Ԋu280�~���b�ȓ��j
				if (interSec <= 280) {

					if ((!head2tail && peaks1 != null)
							|| (head2tail && peaks2 != null)) {

						// �Y�[���A�E�g����
						searchPage.setAllPlotAreaRange();
						fromPos = toPos = null;
						intensityRange = INTENSITY_MAX;
						isInitRate = true;
					}
				}
				// �V���O���N���b�N�̏ꍇ�i�N���b�N�Ԋu281�~���b�ȏ�j
				else {

					if (searchPage == null) {
						return;
					}

					// �}�E�X�N���b�N�|�C���g
					Point p = e.getPoint();

					// ��r�p�p�l���̏ꍇ�A�X�y�N�g����null�̏ꍇ
					if (head2tail || peaks1 == null) {
						return;
					}

					ArrayList<Integer> tmpClickPeakList = new ArrayList<Integer>();

					int height = getHeight();
					double yscale = (height - 2.0d * MARGIN) / intensityRange;
					int start, end, its, tmpX, tmpY, tmpWidth, tmpHight;
					double mz;
					start = peaks1.getIndex(massStart);
					end = peaks1.getIndex(massStart + massRange);

					for (int i = start; i < end; i++) {

						mz = peaks1.getMz(i);
						its = peaks1.getIntensity(i);
						tmpX = MARGIN + (int) ((mz - massStart) * xscale)
								- (int) Math.floor(xscale / 8); // Peak�`��n�_�iX���W�j
						tmpY = height - MARGIN - (int) (its * yscale); // Peak�`��n�_�iY���W�j
						tmpWidth = (int) (xscale / 8); // �n�_����̕�
						tmpHight = (int) (its * yscale); // �n�_����̍���

						if (MARGIN > tmpX) {
							tmpWidth = tmpWidth - (MARGIN - tmpX);
							tmpX = MARGIN;
						}

						if (tmpWidth < 2) {
							tmpWidth = 2;
						} else if (tmpWidth < 3) {
							tmpWidth = 3;
						}

						// �}�E�X�_�E�������ꏊ�iX/Y���W�j��Peak�̕`��G���A�Ɋ܂܂�Ă��邩�𔻒�
						if (tmpX <= p.getX() && p.getX() <= (tmpX + tmpWidth)
								&& tmpY <= p.getY()
								&& p.getY() <= (tmpY + tmpHight)) {

							tmpClickPeakList.add(i);
						}
					}

					// �}�E�X�_�E���|�C���g��Peak��1����ꍇ�A
					// �}�E�X�N���b�N�Ɠ�����Peak�̐F��ύX����
					if (tmpClickPeakList.size() == 1) {

						int index = tmpClickPeakList.get(0);

						if (!peaks1.isSelectPeakFlag(index)) {
							if (peaks1.getSelectPeakNum() < MassBankCommon.PEAK_SEARCH_PARAM_NUM) {
								// �I����Ԃ�ݒ�
								selectPeakList.add(String.valueOf(peaks1
										.getMz(index)));
								peaks1.setSelectPeakFlag(index, true);
							} else {
								JOptionPane.showMessageDialog(PeakPanel.this,
										" m/z of " + MassBankCommon.PEAK_SEARCH_PARAM_NUM + " peak or more cannot be selected. ",
										"Warning",
										JOptionPane.WARNING_MESSAGE);
								cursorPoint = new Point();
							}
						} else if (peaks1.isSelectPeakFlag(index)) {

							// �I����Ԃ�����
							selectPeakList.remove(String.valueOf(peaks1
									.getMz(index)));
							peaks1.setSelectPeakFlag(index, false);
						}
						PeakPanel.this.repaint();
					}
					// �}�E�X�_�E���|�C���g��Peak��2�ȏ゠��ꍇ�A
					// �}�E�X�N���b�N�Ɠ����Ƀ|�b�v�A�b�v���j���[��\������
					else if (tmpClickPeakList.size() >= 2) {

						// �|�b�v�A�b�v���j���[�C���X�^���X����
						selectPopup = new JPopupMenu();
						JMenuItem item = null;
						int index = -1;

						// �|�b�v�A�b�v���j���[�ǉ�
						for (int i = 0; i < tmpClickPeakList.size(); i++) {

							index = tmpClickPeakList.get(i);
							item = new JMenuItem(String.valueOf(peaks1.getMz(index)));
							selectPopup.add(item);
							item.addActionListener(new SelectMZPopupListener(index));

							if (peaks1.getSelectPeakNum() >= MassBankCommon.PEAK_SEARCH_PARAM_NUM
									&& !peaks1.isSelectPeakFlag(index)) {

								// Peak�I�𐔂�MAX�̏ꍇ�A�I���ς�Peak�ȊO�͑I��s��ݒ�
								item.setEnabled(false);
							}
						}

						// �|�b�v�A�b�v���j���[�\��
						selectPopup.show(e.getComponent(), e.getX(), e.getY());
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
			if (searchPage == null || head2tail || peaks1 == null) {
				return;
			}
			
			// �|�b�v�A�b�v���\������Ă���ꍇ
			if ((selectPopup != null && selectPopup.isVisible())
					|| contextPopup != null && contextPopup.isVisible()) {
				
				return;
			}
			
			cursorPoint = e.getPoint();
			PeakPanel.this.repaint();
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
				double xs = (getWidth() - 2.0d * MARGIN) / massRange;
				tmpMassStart = massStart + ((toX - MARGIN) / xs);
				tmpMassRange = 10 * (fromX / (10 * xs));
				if (tmpMassRange < MASS_RANGE_MIN) {
					tmpMassRange = MASS_RANGE_MIN;
				}

				// Intensity�̃����W��ݒ�
				if ((peaks1 != null) && (massRange <= massRangeMax)) {
					// �ő�l�����o�B
					int max = 0;
					double start = Math.max(tmpMassStart, 0.0d);
					max = searchPage.getMaxIntensity(start, start + tmpMassRange);
					if (peaks2 != null)
						max = Math.max(max, peaks2.getMaxIntensity(start, start
								+ tmpMassRange));
					// 50�P�ʂɕϊ����ăX�P�[��������
					tmpIntensityRange = (int) ((1.0d + max / 50.0d) * 50.0d);
					if (tmpIntensityRange > INTENSITY_MAX)
						tmpIntensityRange = INTENSITY_MAX;
				}
			}
			
			/**
			 * �A�N�V�����C�x���g
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				xscale = (getWidth() - 2.0d * MARGIN) / massRange;
				int xpos = (movex + toX) / 2;
				if (Math.abs(massStart - tmpMassStart) <= 2
						&& Math.abs(massRange - tmpMassRange) <= 2) {
					xpos = toX;
					massStart = tmpMassStart;
					massRange = tmpMassRange;
					timer.stop();
					searchPage.setAllPlotAreaRange(PeakPanel.this);
					PeakPanel.this.setCursor(Cursor.getDefaultCursor());
				} else {
					loopCoef++;
					massStart = massStart
							+ (((tmpMassStart + massStart) / 2 - massStart)
									* loopCoef / LOOP);
					massRange = massRange
							+ (((tmpMassRange + massRange) / 2 - massRange)
									* loopCoef / LOOP);
					intensityRange = intensityRange
							+ (((tmpIntensityRange + intensityRange) / 2 - intensityRange)
									* loopCoef / LOOP);
					if (loopCoef >= LOOP) {
						movex = xpos;
						loopCoef = 0;
					}
				}
				PeakPanel.this.repaint();
			}
		}
		
		/**
		 * �s�[�N�I���|�b�v�A�b�v���j���[���X�i�[�N���X
		 * PlotPane�̃C���i�[�N���X
		 */
		class SelectMZPopupListener implements ActionListener {

			/** �C���f�b�N�X */
			private int index = -1;
			
			/**
			 * �R���X�g���N�^
			 * @param index �C���f�b�N�X
			 */
			public SelectMZPopupListener(int index) {
				this.index = index;
			}

			/**
			 * �A�N�V�����C�x���g
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {

				if (!peaks1.isSelectPeakFlag(index)
						&& peaks1.getSelectPeakNum() < MassBankCommon.PEAK_SEARCH_PARAM_NUM) {
					// �I����Ԃ�ݒ�
					selectPeakList.add(String.valueOf(peaks1.getMz(index)));
					peaks1.setSelectPeakFlag(index, true);
				} else if (peaks1.isSelectPeakFlag(index)) {
					// �I����Ԃ�����
					selectPeakList.remove(String.valueOf(peaks1.getMz(index)));
					peaks1.setSelectPeakFlag(index, false);
				}

				cursorPoint = new Point();
				PeakPanel.this.repaint();
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
					urlParam.append("&num=" + peaks1.getSelectPeakNum());			// num �F
					urlParam.append("&tol=0");										// tol �F0
					urlParam.append("&int=5");										// int �F5
					
					for (int i = 0; i < peaks1.getSelectPeakNum(); i++) {
						if (i != 0) {
							urlParam.append("&op" + i + "=and");					// op �Fand
						} else {
							urlParam.append("&op" + i + "=or");						// op �For
						}
						urlParam.append("&mz" + i + "=" + selectPeakList.get(i));	// mz �F
					}
					urlParam.append("&sortKey=name&sortAction=1&pageNo=1&exec=&inst=all");
					
					// JSP�Ăяo��
					String reqUrl = SearchPage.baseUrl + "jsp/Result.jsp"
							+ urlParam.toString();
					try {
						searchPage.getAppletContext().showDocument(new URL(reqUrl), "_blank");
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				else if (com.equals("reset")) {
					if (peaks1 != null) {
						selectPeakList = new ArrayList<String>();
						peaks1.initSelectPeakFlag();
					}
				}
				
				cursorPoint = new Point();
				PeakPanel.this.repaint();
			}
		}
	}
	
	/**
	 * �{�^���y�C���N���X
	 * PeakPanel�̃C���i�[�N���X
	 */
	class ButtonPane extends JPanel implements ActionListener {
		
		/**
		 * �R���X�g���N�^
		 */
		public ButtonPane() {
			leftMostBtn = new JButton("<<");
			leftMostBtn.setActionCommand("<<");
			leftMostBtn.addActionListener(this);
			leftMostBtn.setMargin(new Insets(0, 0, 0, 0));
			leftMostBtn.setEnabled(false);

			leftBtn = new JButton(" < ");
			leftBtn.setActionCommand("<");
			leftBtn.addActionListener(this);
			leftBtn.setMargin(new Insets(0, 0, 0, 0));
			leftBtn.setEnabled(false);

			rightBtn = new JButton(" > ");
			rightBtn.setActionCommand(">");
			rightBtn.addActionListener(this);
			rightBtn.setMargin(new Insets(0, 0, 0, 0));
			rightBtn.setEnabled(false);

			rightMostBtn = new JButton(">>");
			rightMostBtn.setActionCommand(">>");
			rightMostBtn.addActionListener(this);
			rightMostBtn.setMargin(new Insets(0, 0, 0, 0));
			rightMostBtn.setEnabled(false);

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
			mzDisp.setActionCommand("mz");
			mzDisp.addActionListener(this);
			mzDisp.setMargin(new Insets(0, 0, 0, 0));
			mzDisp.setSelected(false);
			mzDisp.setEnabled(false);

			if (head2tail) {
				mzHitDisp = new JToggleButton("show hit m/z");
				mzHitDisp.setActionCommand("mzhit");
				mzHitDisp.addActionListener(this);
				mzHitDisp.setMargin(new Insets(0, 0, 0, 0));
				mzHitDisp.setSelected(false);
				mzHitDisp.setEnabled(false);
			}

			nameLbl = new JLabel();
			nameLbl.setForeground(Color.blue);
			
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(leftMostBtn);
			add(leftBtn);
			add(rightBtn);
			add(rightMostBtn);
			add(mzDisp);
			if (head2tail) {
				add(mzHitDisp);
			}
			else {
				add(nameLbl);
			}
		}

		/**
		 * �A�N�V�����C�x���g
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent ae) {
			String com = ae.getActionCommand();
			if (com.equals("<<")) {
				massStart = Math.max(0, massStart - massRange);
			} else if (com.equals("<")) {
				massStart = Math.max(0, massStart - massRange / 4);
			} else if (com.equals(">")) {
				massStart = Math.min(massRangeMax - massRange, massStart + massRange / 4);
			} else if (com.equals(">>")) {
				massStart = Math.min(massRangeMax - massRange, massStart + massRange);
			} else if (com.equals("mz")) {
				if (head2tail && mzDisp.isSelected()) {
					mzHitDisp.setSelected(false);
				}
			} else if (com.equals("mzhit")) {
				if (mzHitDisp.isSelected()) {
					mzDisp.setSelected(false);
				}
			}
			searchPage.setAllPlotAreaRange(PeakPanel.this);
			PeakPanel.this.repaint();
		}
	}

	public void clear() {
		peaks1 = peaks2 = null;
		massStart = 0;
		massRangeMax = 0;
		massRange = 0;
		intensityRange = INTENSITY_MAX;
		isNoPeak = false;
		isInitRate = true;
		if (!head2tail) {
			setSpectrumInfo("", "", "", "", false);
		}
	}

	/**
	 * �s�[�N���ݒ�
	 * @param p �s�[�N���
	 * @param index �C���f�b�N�X
	 */
	public void setPeaks(PeakData p, int index) {
		if (index == 0) {
			peaks1 = p;
			if (!head2tail) {
				selectPeakList.clear();
			}
		} else if (index == 1) {
			peaks2 = p;
		}

		if (peaks1 != null) {
			massRange = peaks1.compMaxMzPrecusor(precursor);
		}

		if (peaks2 != null) {
			massRange = Math.max(peaks2.compMaxMzPrecusor(precursor), massRange);
			mzHitDisp.setEnabled(true);
			mzHitDisp.setSelected(true);
		}
		
		// massRange��100�Ŋ���؂��ꍇ��+100�̗]�T������
		if (massRange != 0d && (massRange % 100.0d) == 0d) {
			massRange += 100.0d;
		}
		// massRange��100�P�ʂɂ��낦��
		massRange = Math.ceil(massRange / 100.0d) * 100.0d;

		massStart = 0;
		intensityRange = INTENSITY_MAX;
		massRangeMax = (int)massRange;

		this.repaint();
	}

	/**
	 * �s�[�N���擾
	 * @param index �C���f�b�N�X
	 * @return �s�[�N���
	 */
	public PeakData getPeaks(int index) {
		if (index == 0) {
			return peaks1;
		}
		return peaks2;
	}
	
	public double getMassStart() {
		return massStart;
	}
	
	/**
	 * �}�X�����W�擾
	 * @return �}�X�����W
	 */
	public double getMassRange() {
		return massRange;
	}
	
	/**
	 * 
	 * @param s
	 * @param r
	 * @param i
	 */
	public void setMass(double s, double r, int i) {
		massStart = s;
		massRange = r;
		intensityRange = i;
		this.repaint();
	}

	/**
	 * ���x�����W�擾
	 * @return ���x�����W
	 */
	public int getIntensityRange() {
		return intensityRange;
	}

	/**
	 * ���x�����W�ݒ�
	 * @param range ���x�����W
	 */
	public void setIntensityRange(int range) {
		intensityRange = range;
	}

	/**
	 * SearchPage�I�u�W�F�N�g�ݒ�
	 * @param obj SearchPage�I�u�W�F�N�g
	 */
	public void setSearchPage(SearchPage obj) {
		searchPage = obj;
	}

	/**
	 * Tolerance���͒l�Z�b�g
	 * @param val tolerance�l
	 * @param unit unit�t���O�itrue�Funit�Afalse�Fppm�j
	 */
	public void setTolerance(String val, boolean unit) {
		tolVal = val;
		tolUnit = unit;
	}
	
	
	/**
	 * �X�y�N�g�����ݒ�
	 * �X�y�N�g�������̉��������ݒ�y�сA�c�[���`�b�v������ݒ�
	 * @param name ��������
	 * @param key �X�y�N�g�����R�[�h�����ł���L�[������
	 * @param percursor �v���J�[�T�[
	 * @param spType �X�y�N�g����ʁiQuery �܂��� Result)
	 * @param invalid �����X�y�N�g�����薳���t���O
	 */
	public void setSpectrumInfo(String name, String key, String precursor, String spType, boolean invalid) {
		
		typeLbl1 = " ";
		typeLbl2 = " ";
		
		// �����X�y�N�g���̏ꍇ�́uMERGED SPECTRUM�v��\��
		if (key.length() != 0 ) {
			typeLbl1 = spType;
			if ( !invalid ) {
				if ( name.indexOf("MERGED") != -1 ) {
					typeLbl2 = SP_TYPE_MERGED;
				}
			}
		}
		
		nameLbl.setText("  " + name);
		if (name.trim().length() != 0 && key.trim().length() != 0) {
			nameLbl.setToolTipText(key + ":  " + name);
		}
		this.precursor = precursor;
	}
	
	/**
	 * �v���J�[�T�[�擾
	 * @return �v���J�[�T�[
	 */
	public String getPrecursor() {
		return precursor;
	}
	
	/**
	 * �s�[�N�L���t���O�ݒ�
	 * @param isNoPeak �s�[�N�L����ԁitrue�F�����Afalse�F�L��j
	 */
	public void setNoPeak(boolean isNoPeak) {
		this.isNoPeak = isNoPeak;
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
	}
	
	/**
	 * Tolerance���ł��邩���`�F�b�N����iCutoff���l������j
	 * 
	 * @param mode ��r�Ώ�(true�Fmz1�Afalse�Fmz2)
	 * @param compMz ��r��m/z
	 * @param compIts ��r��Intensity
	 * @param peaks �s�[�N���
	 * @return ����(true�FTolerance���Afalse�FTorerance�O)
	 */
	private boolean checkTolerance(boolean mode, double compMz, int compIts, PeakData peaks) {
		
		// ��r�������x��Cutoff��菬�����ꍇ
		if (compIts < SearchPage.CUTOFF_THRESHOLD) {
			return false;
		}

		double tolerance = 0;
		long lngTolerance = 0;
		long mz1;
		long mz2;
		int its1 = 0;
		int its2 = 0;
		long minusRange;
		long plusRange;
		final int TO_INTEGER_VAL = 100000;	// �ۂߌ덷�������邽�ߐ���������̂Ɏg�p

		// Tolerance���͒l
		tolerance = Double.parseDouble(tolVal);
		
		mz1 = mz2 = (long) (compMz * TO_INTEGER_VAL);
		for (int i = peaks.getPeakNum() - 1; i >= 0; i--) {
			if (mode) {
				mz1 = (long) (peaks.getMz(i) * TO_INTEGER_VAL);
				its1 = peaks.getIntensity(i);
			} else {
				mz2 = (long) (peaks.getMz(i) * TO_INTEGER_VAL);
				its2 = peaks.getIntensity(i);
			}

			// unit�̏ꍇ
			if (tolUnit) {
				lngTolerance = (int) (tolerance * TO_INTEGER_VAL);
				minusRange = mz1 - lngTolerance;
				plusRange = mz1 + lngTolerance;
			}
			// ppm�̏ꍇ
			else {
				minusRange = (long) (mz1 * (1 - tolerance / 1000000));
				plusRange = (long) (mz1 * (1 + tolerance / 1000000));
			}

			// ����ȍ~�Ō����͂��肦�Ȃ�
			if ((mode && plusRange < mz2) || (!mode && minusRange > mz2)) {
				return false;
			}

			// �������ł��邩
			if (minusRange <= mz2 && mz2 <= plusRange) {
				if ((mode && its1 >= SearchPage.CUTOFF_THRESHOLD)
						|| (!mode && its2 >= SearchPage.CUTOFF_THRESHOLD)) {

					return true;
				}
			}
		}
		return false;
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
	 * �\�����摜�̓ǂݍ���
	 * @param gifMUrl �ʏ�摜��URL
	 * @param gifSUrl �������摜��UR
	 */
	public void loadStructGif(String gifMUrl, String gifSUrl) {
		try {
			if ( !gifMUrl.equals("") ) {
				this.structImgM = ImageIO.read(new URL(gifMUrl));
			}
			else {
				this.structImgM = null;
			}
			if ( !gifSUrl.equals("") ) {
				this.structImgS = ImageIO.read(new URL(gifSUrl));
			}
			else {
				this.structImgS = null;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���������̃Z�b�g
	 * @param formula ���q��
	 * @param emass ��������
	 */
	public void setCompoundInfo(String formula, String emass) {
		this.formula = formula;
		this.emass = emass;
	}
}


