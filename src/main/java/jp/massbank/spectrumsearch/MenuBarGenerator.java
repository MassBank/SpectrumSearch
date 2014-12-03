/*
 * Copyright (C) 2014 JST-BIRD MassBank
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
 */


package jp.massbank.spectrumsearch;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

class MenuBarGenerator {
  private static final Logger LOGGER = Logger.getLogger(MenuBarGenerator.class);

  static JMenuBar generateMenuBar(final SearchPage targetFrame) {

    JMenuBar menuBar;
    JMenu menu;
    // JMenu submenu;
    JMenuItem menuItem;
    JMenuItem menuItemSync;
    // JRadioButtonMenuItem rbMenuItem;
    // JCheckBoxMenuItem cbMenuItem;

    // Create the menu bar.
    menuBar = new JMenuBar();

    // Build the first menu.
    menu = new JMenu("File");
    // menu.setMnemonic(KeyEvent.VK_A);
    menuBar.add(menu);

    // a group of JMenuItems
    menuItem = new JMenuItem("Open Query File");
    // menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
    menuItem.addActionListener(new QueryFileLoader(targetFrame));
    // menuItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
    
    // a menu for sync files content into database
    menuItemSync = new JMenuItem("Synchronous MassBank Records");
	menuItemSync.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			SyncDialog dialog = new SyncDialog(targetFrame);
			dialog.showDialog();
		}
	});

    menu.add(menuItem);
    menu.add(menuItemSync);
    menu.addSeparator();

    menuItem = new JMenuItem("Exit");
    // menuItem.setMnemonic(KeyEvent.VK_B);
    menuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        LOGGER.info("Close application...");
        System.exit(0);
      }
    });
    menu.add(menuItem);

    // menuItem = new JMenuItem(new ImageIcon("images/middle.gif"));
    // menuItem.setMnemonic(KeyEvent.VK_D);
    // menu.add(menuItem);

    // a group of radio button menu items

    // ButtonGroup group = new ButtonGroup();
    // rbMenuItem = new JRadioButtonMenuItem("A radio button menu item");
    // rbMenuItem.setSelected(true);
    // rbMenuItem.setMnemonic(KeyEvent.VK_R);
    // group.add(rbMenuItem);
    // menu.add(rbMenuItem);
    //
    // rbMenuItem = new JRadioButtonMenuItem("Another one");
    // rbMenuItem.setMnemonic(KeyEvent.VK_O);
    // group.add(rbMenuItem);
    // menu.add(rbMenuItem);
    //
    // // a group of check box menu items
    // menu.addSeparator();
    // cbMenuItem = new JCheckBoxMenuItem("A check box menu item");
    // cbMenuItem.setMnemonic(KeyEvent.VK_C);
    // menu.add(cbMenuItem);
    //
    // cbMenuItem = new JCheckBoxMenuItem("Another one");
    // cbMenuItem.setMnemonic(KeyEvent.VK_H);
    // menu.add(cbMenuItem);

    // // a submenu
    // menu.addSeparator();
    // submenu = new JMenu("A submenu");
    // submenu.setMnemonic(KeyEvent.VK_S);
    //
    // menuItem = new JMenuItem("An item in the submenu");
    // menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
    // submenu.add(menuItem);
    //
    // menuItem = new JMenuItem("Another item");
    // submenu.add(menuItem);
    // menu.add(submenu);
    //
    // Build second menu in the menu bar.
    menu = new JMenu("Help");
    // menu.setMnemonic(KeyEvent.VK_N);
    // menu.getAccessibleContext().setAccessibleDescription("This menu does nothing");

    menuItem = new JMenuItem("About Spectrum Search");
    // menuItem.setMnemonic(KeyEvent.VK_B);
    menuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        StringBuilder sb = new StringBuilder("Spectrum Search\n");
        // String messgae = "Spectrum Search\n" ;
        sb.append("version " + SearchPage.class.getPackage().getImplementationVersion() +"\n");
        sb.append("\n");
        sb.append("Copyright (C) 2014 JST-BIRD MassBank");

        JOptionPane.showMessageDialog(targetFrame, sb.toString(), "About Spectrum Search",
            JOptionPane.INFORMATION_MESSAGE);
        return;
      }
    });
    menu.add(menuItem);


    menuBar.add(menu);

    // ...
    // setJMenuBar(menuBar);
    return menuBar;
  }
}
