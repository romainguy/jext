/*
 * 06/13/2003
 *
 * Copyright (C) 2003 Paolo Giarrusso
 * blaisorblade_work@yahoo.it
 * www.jext.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import org.jext.misc.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.IOException;
import java.awt.event.*;

public class ChoiceForm extends JPanel implements ActionListener {
  private JTable plugTable;
  private JPanel rightPane, downPane, upPane;
  private JComboBox mirrorsBox;
  private JButton downBin, downSrc, install, close, details;
  private JDialog parent;

  private PluginDesc[] plugins;
  private AbstractPlugReader dataProvider;

  public ChoiceForm() {
    //data building part
    dataProvider = PluginDownload.getUpdater();
    plugins = dataProvider.getPlugins();
    parent = PluginDownload.getUpdateWindow();

    //----GUI
    setLayout(new BorderLayout());

    //--Table and its scroller
    plugTable = new JTable(new PluginTableModel());
    plugTable.getTableHeader().setReorderingAllowed(false);
    plugTable.setCellSelectionEnabled(false);
    plugTable.setRowSelectionAllowed(true);
    plugTable.setColumnSelectionAllowed(false);

    TableColumn column = null;
    for (int i = 0; i < 4; i++) {
      column = plugTable.getColumnModel().getColumn(i);
      if (i == 0) {
        column.setPreferredWidth(100);
      } else {
        column.setPreferredWidth(50);
      }
    }

    Dimension dim = plugTable.getPreferredSize();
    JScrollPane scroller = new JScrollPane(plugTable);
    scroller.setPreferredSize(new Dimension((int) dim.width, 250));

    //right panel
    rightPane = new JPanel();
    BoxLayout box = new BoxLayout(rightPane, BoxLayout.Y_AXIS);
    rightPane.setLayout(box);

    downBin = new JButton("Download binary");
    downSrc = new JButton("Download source");
    details = new JButton("Details");
    install = new JButton("Install");

    downBin.setAlignmentX(CENTER_ALIGNMENT);
    downSrc.setAlignmentX(CENTER_ALIGNMENT);
    details.setAlignmentX(CENTER_ALIGNMENT);
    install.setAlignmentX(CENTER_ALIGNMENT);

    rightPane.add(downBin);
    rightPane.add(Box.createRigidArea(new Dimension(0,5)));
    rightPane.add(downSrc);
    rightPane.add(Box.createRigidArea(new Dimension(0,5)));
    rightPane.add(details);
    rightPane.add(Box.createRigidArea(new Dimension(0,5)));
    rightPane.add(install);

    //down panel
    downPane = new JPanel();
    /*box = new BoxLayout(downPane, BoxLayout.X_AXIS);
    downPane.setLayout(box);
    downPane.add(Box.createHorizontalGlue());*/
    downPane.add(close = new JButton("Close"));

    //upper panel
    mirrorsBox = new JComboBox(dataProvider.getMirrors());
    mirrorsBox.setMaximumSize(mirrorsBox.getPreferredSize());
    upPane = new JPanel();
    box = new BoxLayout(upPane, BoxLayout.X_AXIS);
    upPane.setLayout(box);
    //upPane.setLayout(new BorderLayout());
    upPane.add(Box.createRigidArea(new Dimension(5,0)));
    upPane.add(new JLabel("Choose mirror to use: "));//, BorderLayout.WEST);
    upPane.add(Box.createRigidArea(new Dimension(10,0)));
    upPane.add(mirrorsBox);//, BorderLayout.EAST);

    add(upPane, BorderLayout.NORTH);
    add(scroller, BorderLayout.CENTER);
    add(rightPane, BorderLayout.EAST);
    add(downPane, BorderLayout.SOUTH);

    close.addActionListener(this);
    downBin.addActionListener(this);
    downSrc.addActionListener(this);
    details.addActionListener(this);
    install.addActionListener(this);
  }

  private void end() {
    parent.dispose();
    if (PluginDownload.debug)
      PluginDownload.startUpdate(); //to restart things quickly. To exit, the user must close the window with
    //the title-bar button.
  }

  private String getMirror() {
    return (String) mirrorsBox.getSelectedItem();
  }

  public void actionPerformed(ActionEvent ae) {
    Object src = ae.getSource();

    if (src == close) {
      end();
    } else {
      int rows[] = plugTable.getSelectedRows();
      if (rows == null)
        return;

      for (int i = 0; i < rows.length; i++) {
        int row = rows[i];
        if (src == details) {
          (new DetailForm(plugins[row], true)).show();
          return; // so only one form is shown.
        }

        try {
          if (src == install) {
            plugins[row].install(new Runnable() {
              public void run() {
                JOptionPane.showMessageDialog(parent,
                    "You did not download the file before installing it!",
                    "Install error",
                    JOptionPane.ERROR_MESSAGE);
              }
            });
          }
        } catch (IOException ioe) {
          ioe.printStackTrace();
          JOptionPane.showMessageDialog(parent,
              "The installation of " + plugins[row].getDisplayName() + " failed!",
              "Install error",
              JOptionPane.ERROR_MESSAGE);
        }

        if (src == downBin) {
          plugins[row].downloadBin(new DownloadErrorNotify(plugins[row].getDisplayName()), getMirror());
        } else if (src == downSrc) {
          plugins[row].downloadSrc(new DownloadErrorNotify(plugins[row].getDisplayName()), getMirror());
        }
      } //end for on rows
    } //end if (src == close)
  } //end method

  class DownloadErrorNotify implements HandlingRunnable {
    private String name;
    public DownloadErrorNotify(String name) {
      this.name = name;
    }

    public void run(Object result, Throwable excep) {
      if (excep != null) {
        excep.printStackTrace();
        JOptionPane.showMessageDialog(parent,
            "The download of " + name + " failed!",
            "Download error",
            JOptionPane.ERROR_MESSAGE);
      }
    }

  }

  class DetailForm extends JDialog {
    private PluginDesc plugin;
    private JPanel mainPane;
    private GridBagLayout gridbag;
    private GridBagConstraints c;

    DetailForm(PluginDesc plugin, boolean modal) {
      super(ChoiceForm.this.parent, "Details for " + plugin.getDisplayName(), modal);
      this.plugin = plugin;
      buildUI();
    }

    private void buildUI() {
      //JTable table;
      getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

      JButton close = new JButton("Close");
      close.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          DetailForm.this.dispose();
        }
      });
      close.setAlignmentX(CENTER_ALIGNMENT);

      /*getContentPane().add(new JLabel("Name: " + plugin.getDisplayName()));
      getContentPane().add(new JLabel("Version: " + plugin.getRelease()));
      getContentPane().add(new JLabel("Binary size: " + plugin.getBinSize()));
      getContentPane().add(new JLabel("Source size: " + plugin.getSrcSize()));
      getContentPane().add(new JLabel("Description: " + plugin.getDesc()));*/
      /*table = new JTable(new DetailsTableModel(plugin));
      table.setAlignmentX(CENTER_ALIGNMENT);

      TableColumn column = null;
      column = table.getColumnModel().getColumn(0);
      column.setPreferredWidth(90);
      column = table.getColumnModel().getColumn(1);
      column.setPreferredWidth(300);*/

      //join the author names
      StringBuffer authTextBuf = new StringBuffer(100);
      PluginAuthor authors[] = plugin.getAuthors();
      int authLen = authors.length;
      if (authLen > 0)
        authTextBuf.append(authors[0]);
      for (int i = 1; i < authLen; i++) {
        authTextBuf.append("<br>").append(authors[i]);
      }
      String authText = authTextBuf.toString();

      //build the two HTML panes.
      JEditorPane desc = new JEditorPane("text/html", plugin.getDesc());
      desc.setEditable(false);
      desc.setBorder(BorderFactory.createEtchedBorder());
      JEditorPane authorPane = new JEditorPane("text/html", authText);
      authorPane.setEditable(false);
      authorPane.setBorder(BorderFactory.createEtchedBorder());

      JComponent comp;
      mainPane = new JPanel();
      gridbag = new GridBagLayout();
      c = new GridBagConstraints();
      c.gridy = 0;
      c.gridx = 0;
      mainPane.setLayout(gridbag);

      c.anchor = GridBagConstraints.NORTHEAST;
      shortAdd(new JLabel("Name: "));
      shortAdd(new JLabel("Version: "));
      shortAdd(new JLabel("Description: "));
      shortAdd(new JLabel("Authors: "));
      shortAdd(new JLabel("Binary URL: "));
      shortAdd(new JLabel("Source URL: "));
      shortAdd(new JLabel("Binary size: "));
      shortAdd(new JLabel("Source size: "));

      c.anchor = GridBagConstraints.CENTER;
      c.gridy = 0;
      c.gridx = 1;
      shortAdd(new JLabel(plugin.getDisplayName()));
      shortAdd(new JLabel(plugin.getRelease()));
      c.ipadx = 100;
      c.fill = GridBagConstraints.HORIZONTAL;
      shortAdd(desc);
      shortAdd(authorPane);
      c.ipadx = 0;
      c.fill = GridBagConstraints.NONE;
      shortAdd(new JLabel("" + plugin.getSrcUrl(getMirror())));
      shortAdd(new JLabel("" + plugin.getBinUrl(getMirror())));
      shortAdd(new JLabel("" + plugin.getBinSize()));
      shortAdd(new JLabel("" + plugin.getSrcSize()));

      getContentPane().add(mainPane);
      getContentPane().add(Box.createVerticalGlue());
      getContentPane().add(close);
      pack();
    }

    private void shortAdd(JComponent comp) {
      gridbag.setConstraints(comp, c);
      mainPane.add(comp);
      c.gridy++;
    }
  }

  /*class DetailsTableModel extends AbstractTableModel {
    private PluginDesc plugin;

    public DetailsTableModel(PluginDesc plugin) {
      this.plugin = plugin;
    }

    private String[] names = { "Name", "Version", "Binary size", "Source size", "Description: " };
    public int getColumnCount() { return 2; }
    public int getRowCount() { return 5; }
    public Object getValueAt(int row, int col) {
      if (col == 0)
        return names[row];
      switch(row) {
        case 0:
          return plugin.getDisplayName();
        case 1:
          return plugin.getRelease();
        case 2:
          return new Integer(plugin.getBinSize());
        case 3:
          return new Integer(plugin.getSrcSize());
        //case 4:
          //return plugin.getDesc();
      }
      return null;
    }

    public String getColumnName(int n) { return null; }
    public boolean isCellEditable(int row, int col) { return false; }
  }*/

  class PluginTableModel extends AbstractTableModel {
    private String[] names = { "Name", "Version", "Binary size", "Source size" };
    public int getColumnCount() { return 4; }
    public int getRowCount() { return plugins.length;}
    public Object getValueAt(int row, int col) {
      switch(col) {
        case 0:
          return plugins[row].getDisplayName();
        case 1:
          return plugins[row].getRelease();
        case 2:
          return new Integer(plugins[row].getBinSize());
        case 3:
          return new Integer(plugins[row].getSrcSize());
        /*case 4:
          return plugins[row].getBinUrl((String)mirrorsBox.getSelectedItem());*/
      }
      return null;
    }

    public String getColumnName(int n) {
      return names[n];
    }

    public boolean isCellEditable(int row, int col) {
      return false;
    }
  }
}
