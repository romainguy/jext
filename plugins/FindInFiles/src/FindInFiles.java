/*
 * Copyright (C) 2002 James Kolean
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

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jext.*;
import org.jext.gui.*;
import org.jext.event.*;
import org.jext.search.*;

public class FindInFiles extends JPanel implements JextListener {
  private static final boolean DEBUG=false;
  private static final int MAX_PATHS=25;
  private static final int MAX_FILTERS=25;
  private static final int MAX_PATTERNS=25;
  private static final String USE_REGX_PROP="findInFiles.useregexp";
  private static final String SEARCH_SUB_DIR_PROP="findInFiles.searchsubdirs";
  private static final String IGNORE_CASE_PROP="findInFiles.ignorecase";
  private static final String PATH_PROP="findInFiles.path.history.";
  private static final String FILTER_PROP="findInFiles.filter.history.";
  private static final String PATTERN_PROP="findInFiles.pattern.history.";

  private JextFrame parent;
  private FindInFiles me;
  private JList dataList = new JList();
  private JextHighlightButton searchButton = null;;
  private JButton chooseButton = null;
  private JComboBox pathCombo = new JComboBox();
  private JComboBox filterCombo = new JComboBox();
  private JComboBox patternCombo = new JComboBox();
  private JextCheckBox regexChechBox = null;
  private JextCheckBox subDirChechBox = null;
  private JextCheckBox ignoreCaseChechBox = null;
  private JLabel statusLabel = new JLabel(" ");

  public FindInFiles(JextFrame parent) {
    this.parent=parent;
    parent.addJextListener(this);
    me=this;

    setLayout(new BorderLayout());
    Box controlPanel = new Box(BoxLayout.Y_AXIS);
    add(controlPanel,"North");

    //build the panel with the combobox
    JPanel comboPanel = new JPanel(new BorderLayout());
    controlPanel.add(comboPanel,"North");
    JPanel labelPanel = new JPanel(new GridLayout(3,1));
    comboPanel.add(labelPanel,"West");
    JLabel aLabel;
    aLabel = new JLabel(Jext.getProperty("findinfiles.path.label"),javax.swing.SwingConstants.RIGHT);
    aLabel.setToolTipText(Jext.getProperty("findinfiles.path.tooltip"));
    labelPanel.add(aLabel);
    aLabel = new JLabel(Jext.getProperty("findinfiles.filter.label"),javax.swing.SwingConstants.RIGHT);
    aLabel.setToolTipText(Jext.getProperty("findinfiles.filter.tooltip"));
    labelPanel.add(aLabel);
    aLabel = new JLabel(Jext.getProperty("findinfiles.pattern.label"),javax.swing.SwingConstants.RIGHT);
    aLabel.setToolTipText(Jext.getProperty("findinfiles.pattern.tooltip"));
    labelPanel.add(aLabel);
    JPanel entryPanel = new JPanel(new GridLayout(3,1));

    comboPanel.add(entryPanel,"Center");
    pathCombo.setToolTipText(Jext.getProperty("findinfiles.path.tooltip"));
    pathCombo.setMinimumSize(new Dimension(0,0)); //let it shrink
    entryPanel.add(pathCombo);
    filterCombo.setToolTipText(Jext.getProperty("findinfiles.filter.tooltip"));
    filterCombo.setMinimumSize(new Dimension(0,0)); //let it shrink
    entryPanel.add(filterCombo);
    patternCombo.setToolTipText(Jext.getProperty("findinfiles.pattern.tooltip"));
    patternCombo.setMinimumSize(new Dimension(0,0)); //let it shrink
    entryPanel.add(patternCombo);
    pathCombo.setEditable(true);
    filterCombo.setEditable(true);
    patternCombo.setEditable(true);
    loadProp(pathCombo, PATH_PROP, MAX_PATHS);
    loadProp(filterCombo, FILTER_PROP, MAX_FILTERS);
    loadProp(patternCombo, PATTERN_PROP, MAX_PATTERNS);
    JPanel buttonPanel = new JPanel(new GridLayout(3,1));
    comboPanel.add(buttonPanel,"East");
    ImageIcon openIcon = Utilities.getIcon(
        "images/menu_open"+Jext.getProperty("jext.look.icons")+".gif", Jext.class);
    buttonPanel.add(chooseButton = new JextHighlightButton(openIcon));
    chooseButton.setPreferredSize(new Dimension(openIcon.getIconWidth()+5,openIcon.getIconHeight()));

    add(new JScrollPane(dataList = new JList(),
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS),"Center");
    dataList.setCellRenderer(new MyCellRenderer());

    //check boxes
    JPanel bottomPanel = new JPanel(new BorderLayout());
    JPanel checkBoxPanel = new JPanel(new GridLayout(3,1));
    controlPanel.add(checkBoxPanel);
    checkBoxPanel.add(regexChechBox = new JextCheckBox(Jext.getProperty("findinfiles.regexp.label")));
    checkBoxPanel.add(subDirChechBox = new JextCheckBox(Jext.getProperty("findinfiles.searchsubdir.label")));
    checkBoxPanel.add(ignoreCaseChechBox = new JextCheckBox(Jext.getProperty("findinfiles.ignorecase.label")));
    loadProp(regexChechBox, USE_REGX_PROP);
    loadProp(subDirChechBox, SEARCH_SUB_DIR_PROP);
    loadProp(ignoreCaseChechBox, IGNORE_CASE_PROP);
    bottomPanel.add(checkBoxPanel, "North");

    bottomPanel.add(checkBoxPanel, "North");
    bottomPanel.add(searchButton = new JextHighlightButton(Jext.getProperty("findinfiles.search.label")), "West");
    bottomPanel.add(statusLabel, "South");

    add(bottomPanel, "South");
    addListeners();
  }

  private void addListeners() {
    searchButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (DEBUG) System.out.println("Searching");
        Utilities.setCursorOnWait(me,true);
        Vector data=new Vector();
        try {
          String filter=(String)filterCombo.getSelectedItem();
          data=FindInFilesHelper.search(
              new File((String)pathCombo.getSelectedItem()),
              filter,
              (String)patternCombo.getSelectedItem(),
              subDirChechBox.isSelected(),
              ignoreCaseChechBox.isSelected(),
              regexChechBox.isSelected());
          addHistory(pathCombo);
          addHistory(filterCombo);
          addHistory(patternCombo);
          if ( data.size()==0 )
            showStatus(Jext.getProperty("findinfiles.msg.nomatch"));
          else
            showStatus("");
        } catch (IllegalArgumentException iaex) {
          showStatus(iaex.getMessage());
        } catch (Exception ex) {
          showStatus(ex.getMessage());
          ex.printStackTrace(System.err);
        }
        dataList.setListData(data);
        Utilities.setCursorOnWait(me,false);
      }
    });
    chooseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (DEBUG) System.out.println("Choosing");
        Utilities.setCursorOnWait(me,true);
        javax.swing.JFileChooser chooser=parent.getFileChooser(Utilities.OPEN);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(chooser.showOpenDialog(me) == JFileChooser.APPROVE_OPTION) {
          String fName=chooser.getSelectedFile().getAbsolutePath();
          if (DEBUG) System.out.println("You chose to open this file: "+chooser.getSelectedFile().getName());
          pathCombo.insertItemAt(fName,0);
          pathCombo.setSelectedIndex(0);
        }
        Utilities.setCursorOnWait(me,false);
      }
    });
    dataList.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if ( DEBUG ) System.out.println("Multi-click");
        if (e.getClickCount() >= 2) {
          int index = dataList.locationToIndex(e.getPoint());
          FindInFilesMatch match = (FindInFilesMatch)dataList.getModel().getElementAt(index);
          JextTextArea textArea = null;
          if ( DEBUG ) System.out.println("Parent: "+parent);
          JextTextArea[] openAreas =parent.getTextAreas();
          boolean checkBeforeOpening=false;
          if ( DEBUG ) System.out.println("textAreas count: "+openAreas.length);
          File targetFile = new File(match.getFilename());
          for ( int i=0; i<openAreas.length; i++) {
            if ( targetFile.equals(openAreas[i].getFile()) ) {
              if (!openAreas[i].isDirty())
                parent.getTabbedPane().setSelectedComponent(textArea=openAreas[i]);
              break;
            }
          }
          if ( DEBUG ) System.out.println("textArea: "+textArea);
          if ( textArea==null) textArea = parent.open(match.getFilename());
          textArea.setCaretPosition(textArea.getLineStartOffset(((int)match.getLineNumber())-1));
        }
      }
    });
  }

  private void addHistory(JComboBox combo){
    String item = (String)combo.getSelectedItem();
    if ( (item==null) || (item.trim().length()==0) ) return;
    combo.removeItem(item);
    combo.insertItemAt(item,0);
    combo.setSelectedIndex(0);
  }

  public void exit() {
    if (DEBUG) System.out.println("saving props");
    saveProp(pathCombo, PATH_PROP, MAX_PATHS);
    saveProp(filterCombo, FILTER_PROP, MAX_PATHS);
    saveProp(patternCombo, PATTERN_PROP, MAX_PATHS);

    saveProp(regexChechBox, USE_REGX_PROP);
    saveProp(subDirChechBox, SEARCH_SUB_DIR_PROP);
    saveProp(ignoreCaseChechBox, IGNORE_CASE_PROP);

    me=null;
    parent.getTextArea().repaint();
  }

  private void saveProp(JComboBox combo, String propName, int maxProps){
    for (int i = 0; i < combo.getItemCount(); i++)
      Jext.setProperty(propName+i, (String) combo.getItemAt(i));
    for (int i = combo.getItemCount(); i < maxProps; i++)
      Jext.unsetProperty(propName+i);
  }

  private void saveProp(JCheckBox checkBox, String propName){
    Jext.setProperty(propName, (checkBox.isSelected() ? "on" : "off"));
  }

  private void loadProp(JComboBox combo, String propName, int maxProps){
    for (int i = 0; i < maxProps; i++) {
      String s = Jext.getProperty(propName+i);
      if (s != null)
        combo.addItem(s);
      else
        break;
    }
    if (combo.getItemCount() > 0) combo.setSelectedIndex(0);
  }

  private void loadProp(JCheckBox checkBox, String propName) {
    checkBox.setSelected("on".equals(Jext.getProperty(propName)));
  }

  public void jextEventFired(JextEvent evt) {
    if (DEBUG) System.out.println("Jext event");
    if (evt.getWhat() == JextEvent.KILLING_JEXT) {
      exit();
    }
  }

  public void showStatus(String message) {
    if ( (message==null) || (message.length()==0) ) message=" ";
    statusLabel.setText(message);
  }

  class MyCellRenderer extends DefaultListCellRenderer {

    // This is the only method defined by ListCellRenderer.
    // We just reconfigure the JLabel each time we're called.

    public Component getListCellRendererComponent(
          JList list,
          Object value,            // value to display
          int index,               // cell index
          boolean isSelected,      // is the cell selected
          boolean cellHasFocus) {  // the list and the cell have the focus
      FindInFilesMatch match=(FindInFilesMatch)value;
      String s;
      if ( match.isHeader() ) {
        s="\""+match.getFilename()+"\"    "+match.getMatchCount()+" matches found.";
      } else {
        s="  "+match.getLineNumber()+":"+match.getLineText();
      }
      return super.getListCellRendererComponent(list,s,index,isSelected,cellHasFocus);
    }
  }
}
