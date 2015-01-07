/*
 * Copyright (C) 2014 JST-BIRD MassBank
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */


package jp.massbank.spectrumsearch;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import jp.massbank.spectrumsearch.model.UserFileData;

import org.apache.log4j.Logger;

public class QueryFileLoader implements ActionListener {
  private static final Logger LOGGER = Logger.getLogger(QueryFileLoader.class);

  private SearchPage targetFrame;
  private JFileChooser fc;

  public QueryFileLoader(SearchPage targetFrame) {
    this.targetFrame = targetFrame;
    fc = new JFileChooser();
  }



  @Override
  public void actionPerformed(ActionEvent e) {
    int returnVal = fc.showOpenDialog(targetFrame);
//    LOGGER.info("returnVal" + returnVal);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      LOGGER.info(file.getAbsolutePath());
      loadFile(file.getAbsolutePath());
    } else {
      LOGGER.info("Open command cancelled by user.");
    }
  }

  /**
   * @param fileName file name with absolute path.
   */
  private void loadFile(String fileName) {
    targetFrame.seaqCompound = 0;
    targetFrame.seaqId = 0;
    List<String> lineList = new ArrayList<>();
    File targetFile = new File(fileName);

    try (BufferedReader in =
        new BufferedReader(new InputStreamReader(new FileInputStream(targetFile), StandardCharsets.UTF_8));) {
      String line = null;
      while ((line = in.readLine()) != null) {
        LOGGER.info(line);
        lineList.add(line);
      }

    } catch (IOException ex) {
      LOGGER.error(ex.getMessage(), ex);
      // ERROR：サーバーエラー
      JOptionPane.showMessageDialog(null, "Server error.", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    // 1行も読み込めなかった場合
    if (lineList.size() == 0) {
      JOptionPane.showMessageDialog(null, "No file.", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    DefaultTableModel dataModel = (DefaultTableModel) targetFrame.fileSorter.getTableModel();
    dataModel.setRowCount(0);

    Object[] row;
    String line = "";
    String peaksLine = "";
    UserFileData usrData = null;
    List<UserFileData> tmpUserDataList = new ArrayList<>();
    int dataNum = 0;
    try {
      for (int i = 0; i < lineList.size(); i++) {

        line = lineList.get(i);

        // コメント行読み飛ばし
        if (line.trim().startsWith("//")) {
          continue;
        }

        // レコード情報取得処理
        if (line.trim().indexOf(":") == -1 && line.trim().length() != 0) {
          if (usrData == null) {
            usrData = new UserFileData();
          }
          if (line.lastIndexOf(";") != -1) {
            peaksLine += line.trim();
          } else {
            peaksLine += line.trim() + ";";
          }
        } else if (line.trim().startsWith("Name:")) {
          if (usrData == null) {
            usrData = new UserFileData();
          }
          usrData.setName(line.substring(5).trim());
        } else if (line.trim().startsWith("ID:")) {
          if (usrData == null) {
            usrData = new UserFileData();
          }
          usrData.setId(line.substring(3).trim());
        }

        // レコード情報追加処理
        if (line.trim().length() == 0 || i == lineList.size() - 1) {

          if (usrData != null) {

            dataNum++;

            // === ID ===
            if (usrData.getId().equals("")) {
              usrData.setId(createId());
            }

            // === 化合物名 ===
            if (usrData.getName().equals("")) {
              usrData.setName(createName());
            }

            if (peaksLine.length() != 0) {

              // ピーク情報加工(m/z昇順のm/zと強度の組み合わせ)
              double max = 0d;
              List<String> peakList = new ArrayList<>(Arrays.asList(peaksLine.split(";")));
              for (int j = 0; j < peakList.size(); j++) {
                peakList.set(j, peakList.get(j).replaceAll("^ +", ""));
                peakList.set(j, peakList.get(j).replaceAll(" +", "\t"));

                // 最大強度保持
                if (max < Double.parseDouble(peakList.get(j).split("\t")[1])) {
                  max = Double.parseDouble(peakList.get(j).split("\t")[1]);
                }
              }
              Collections.sort(peakList, new PeakComparator());

              // 強制的に強度を相対強度に変換
              for (int j = 0; j < peakList.size(); j++) {

                // m/z退避
                String tmpMz = peakList.get(j).split("\t")[0];

                // 元の強度
                String beforeVal = peakList.get(j).split("\t")[1];

                // 相対強度
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
              usrData.setPeaks((String[]) peakList.toArray(new String[peakList.size()]));

            }

            // ユーザデータ情報追加
            tmpUserDataList.add(usrData);

            // テーブル情報追加
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
    } catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
      // WARNING：ファイルフォーマットが不正です
      JOptionPane.showMessageDialog(null, "Illegal file format.", "Warning",
          JOptionPane.WARNING_MESSAGE);
      return;
    }
    targetFrame.userDataList =
        (UserFileData[]) tmpUserDataList.toArray(new UserFileData[tmpUserDataList.size()]);
    targetFrame.queryTabPane.setSelectedIndex(SearchPage.TAB_ORDER_FILE);
  }

  /**
   * ID生成 IDを自動生成し返却する
   * 
   * @return 化合物名
   */
  private String createId() {
    String tmpId = "US";

    // TODO consider this 'synchronized' is needed or not.
    synchronized (QueryFileLoader.class) {
      if (targetFrame.seaqId < Integer.MAX_VALUE) {
        targetFrame.seaqId++;
      } else {
        targetFrame.seaqId = 0;
      }
    }
    DecimalFormat df = new DecimalFormat("000000");
    return tmpId + df.format(targetFrame.seaqId);
  }

  /**
   * 化合物名生成 化合物名を自動生成し返却する
   * 
   * @return 化合物名
   */
  private String createName() {
    String tmpName = "Compound_";

    // TODO consider this 'synchronized' is needed or not.
    synchronized (QueryFileLoader.class) {
      if (targetFrame.seaqCompound < Integer.MAX_VALUE) {
        targetFrame.seaqCompound++;
      } else {
        targetFrame.seaqCompound = 0;
      }
    }
    DecimalFormat df = new DecimalFormat("000000");
    return tmpName + df.format(targetFrame.seaqCompound);
  }

  /**
   * ピークコンパレータ SearchPageのインナークラス。 m/zの昇順ソートを行う。
   */
  static class PeakComparator implements Comparator<String> , Serializable {
    public int compare(String o1, String o2) {
      String mz1 = String.valueOf(o1).split("\t")[0];
      String mz2 = String.valueOf(o2).split("\t")[0];
      return Double.valueOf(mz1).compareTo(Double.valueOf(mz2));
    }
  }
}
