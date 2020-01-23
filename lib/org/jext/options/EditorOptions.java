/*
 * 01/20/2003 - 23:11:56
 *
 * EditorOptions.java - The editor options pane
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
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

package org.jext.options;

import java.awt.*;

import javax.swing.*;

import org.jext.*;
import org.jext.gui.*;

public class EditorOptions extends AbstractOptionPane //JPanel implements OptionPane
{
  private FontSelector fonts;
  private JTextField autoScroll, linesInterval, wrapGuide;
  private JComboBox newline, tabSize, modes, encoding, orientation;
  private JextCheckBox enterIndent, tabIndent, softTabs, blockCaret, selection, smartHomeEnd,
                       splitArea, fullFileName, lineHighlight, eolMarkers, blinkCaret, tabStop,
                       linesIntervalEnabled, wrapGuideEnabled, dirDefaultDialog, overSpace,
                       addExtraLineFeed, preserveLineTerm;
  private String modeNames[];

  public EditorOptions()
  {
    super("editor");

    addComponent(Jext.getProperty("options.autoscroll.label"), autoScroll = new JTextField(4));
    autoScroll.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

    addComponent(Jext.getProperty("options.linesinterval.label"), linesInterval = new JTextField(4));
    linesInterval.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

    addComponent(Jext.getProperty("options.wrapguide.label"), wrapGuide = new JTextField(4));
    wrapGuide.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

    String[] encodings = { "ASCII", "Cp850", "Cp1252", "iso-8859-1", "iso-8859-2", "KOI8_R","MacRoman",
                           "UTF8", "UTF16", "Unicode" };
    encoding = new JComboBox(encodings);
    encoding.setRenderer(new ModifiedCellRenderer());
    encoding.setEditable(true);
    addComponent(Jext.getProperty("options.encoding.label"), encoding);

    fonts = new FontSelector("editor");
    addComponent(Jext.getProperty("options.fonts.label"), fonts);

    String sizes[] = { "2", "4", "8", "16" };
    tabSize = new JComboBox(sizes);
    tabSize.setEditable(true);
    addComponent(Jext.getProperty("options.tabsize.label"), tabSize);
    tabSize.setRenderer(new ModifiedCellRenderer());

    int nModes = Jext.modes.size();
    String modeUserNames[] = new String[nModes];
    modeNames = new String[nModes];

    for (int i = 0; i < nModes; i++)
    {
      Mode syntaxMode = (Mode) Jext.modes.get(i);
      modeNames[i] = syntaxMode.getModeName();
      modeUserNames[i] = syntaxMode.getUserModeName();
    }

    modes = new JComboBox(modeUserNames);
    modes.setRenderer(new ModifiedCellRenderer());
    addComponent(Jext.getProperty("options.syntax.mode.label"), modes);

    String newlines[] = { "MacOS (\\r)", "Unix (\\n)", "Windows (\\r\\n)" };
    newline = new JComboBox(newlines);
    newline.setRenderer(new ModifiedCellRenderer());
    addComponent(Jext.getProperty("options.newline.label"), newline);

    String _or[] = { "Vertical", "Horizontal" };
    orientation = new JComboBox(_or);
    orientation.setRenderer(new ModifiedCellRenderer());
    addComponent(Jext.getProperty("options.orientation.label"), orientation);

    addComponent(linesIntervalEnabled = new JextCheckBox(Jext.getProperty("options.linesintervalenabled.label")));
    addComponent(wrapGuideEnabled = new JextCheckBox(Jext.getProperty("options.wrapguideenabled.label")));
    addComponent(splitArea = new JextCheckBox(Jext.getProperty("options.splitarea.label")));
    addComponent(blockCaret = new JextCheckBox(Jext.getProperty("options.blockcaret.label")));
    addComponent(blinkCaret = new JextCheckBox(Jext.getProperty("options.blinkingcaret.label")));
    addComponent(lineHighlight = new JextCheckBox(Jext.getProperty("options.linehighlight.label")));
    addComponent(eolMarkers = new JextCheckBox(Jext.getProperty("options.eolmarkers.label")));
    addComponent(softTabs = new JextCheckBox(Jext.getProperty("options.softtabs.label")));
    addComponent(tabIndent = new JextCheckBox(Jext.getProperty("options.tabindent.label")));
    addComponent(enterIndent = new JextCheckBox(Jext.getProperty("options.enterindent.label")));
    addComponent(tabStop = new JextCheckBox(Jext.getProperty("options.tabstop.label")));
    addComponent(overSpace = new JextCheckBox(Jext.getProperty("options.wordmove.go_over_space.label")));
    addComponent(smartHomeEnd = new JextCheckBox(Jext.getProperty("options.smartHomeEnd.label")));
    addComponent(dirDefaultDialog = new JextCheckBox(Jext.getProperty("options.defaultdirloaddialog.label")));
    addComponent(selection = new JextCheckBox(Jext.getProperty("options.selection.label")));
    addComponent(addExtraLineFeed = new JextCheckBox(Jext.getProperty("options.extra_line_feed.label")));
    addComponent(preserveLineTerm = new JextCheckBox(Jext.getProperty("options.line_end_preserved.label")));

    load();
  }

  public void load()
  {
    autoScroll.setText(Jext.getProperty("editor.autoScroll"));
    linesInterval.setText(Jext.getProperty("editor.linesInterval"));
    wrapGuide.setText(Jext.getProperty("editor.wrapGuideOffset"));
    encoding.setSelectedItem(Jext.getProperty("editor.encoding", System.getProperty("file.encoding")));
    tabSize.setSelectedItem(Jext.getProperty("editor.tabSize"));

    int selMode = 0;
    String currMode = Jext.getProperty("editor.colorize.mode");
    for ( ; selMode < modeNames.length; selMode++)
      if (currMode.equals(modeNames[selMode])) break;
    modes.setSelectedIndex(selMode);

    int i = 0;
    String currNewLine = Jext.getProperty("editor.newLine");
    for ( ; i < Jext.NEW_LINE.length; i++)
      if (Jext.NEW_LINE[i].equals(currNewLine)) break;
    newline.setSelectedIndex(i);

    orientation.setSelectedItem(Jext.getProperty("editor.splitted.orientation"));

    linesIntervalEnabled.setSelected(Jext.getBooleanProperty("editor.linesIntervalEnabled"));
    wrapGuideEnabled.setSelected(Jext.getBooleanProperty("editor.wrapGuideEnabled"));
    splitArea.setSelected(Jext.getBooleanProperty("editor.splitted"));
    blockCaret.setSelected(Jext.getBooleanProperty("editor.blockCaret"));
    blinkCaret.setSelected(Jext.getBooleanProperty("editor.blinkingCaret"));
    lineHighlight.setSelected(Jext.getBooleanProperty("editor.lineHighlight"));
    eolMarkers.setSelected(Jext.getBooleanProperty("editor.eolMarkers"));
    tabIndent.setSelected(Jext.getBooleanProperty("editor.tabIndent"));
    enterIndent.setSelected(Jext.getBooleanProperty("editor.enterIndent"));
    softTabs.setSelected(Jext.getBooleanProperty("editor.softTab"));
    tabStop.setSelected(Jext.getBooleanProperty("editor.tabStop"));
    smartHomeEnd.setSelected(Jext.getBooleanProperty("editor.smartHomeEnd"));
    dirDefaultDialog.setSelected(Jext.getBooleanProperty("editor.dirDefaultDialog"));
    selection.setSelected(Jext.getBooleanProperty("use.selection"));
    overSpace.setSelected(Jext.getBooleanProperty("editor.wordmove.go_over_space"));
    addExtraLineFeed.setSelected(Jext.getBooleanProperty("editor.extra_line_feed"));
    preserveLineTerm.setSelected(Jext.getBooleanProperty("editor.line_end.preserve"));
    fonts.load();
  }

  public Component getComponent()
  {
    JScrollPane scroller = new JScrollPane(this);
    Dimension _dim = this.getPreferredSize();
    scroller.setPreferredSize(new Dimension((int) _dim.width, 410));
    //scroller.setBorder(javax.swing.border.LineBorder.createBlackLineBorder());
    return scroller;
  }

  public void save()
  {
    /*Jext.setProperty("editor.colorize.mode",
                     ((Mode) Jext.modes.get(modes.getSelectedIndex())).getModeName());*/
    Jext.setProperty("editor.colorize.mode", modeNames[modes.getSelectedIndex()]);
    Jext.setProperty("editor.tabIndent", tabIndent.isSelected() ? "on" : "off");
    Jext.setProperty("editor.enterIndent", enterIndent.isSelected() ? "on" : "off");
    Jext.setProperty("editor.softTab", softTabs.isSelected() ? "on" : "off");
    Jext.setProperty("editor.tabStop", tabStop.isSelected() ? "on" : "off");
    Jext.setProperty("editor.tabSize", (String) tabSize.getSelectedItem());
    Jext.setProperty("editor.encoding", (String) encoding.getSelectedItem());
    Jext.setProperty("editor.blockCaret", blockCaret.isSelected() ? "on" : "off");
    Jext.setProperty("editor.blinkingCaret", blinkCaret.isSelected() ? "on" : "off");
    Jext.setProperty("editor.lineHighlight", lineHighlight.isSelected() ? "on" : "off");
    Jext.setProperty("editor.newLine", (String) Jext.NEW_LINE[newline.getSelectedIndex()]);
    Jext.setProperty("editor.eolMarkers", eolMarkers.isSelected() ? "on" : "off");
    Jext.setProperty("editor.smartHomeEnd", smartHomeEnd.isSelected() ? "on" : "off");
    Jext.setProperty("editor.dirDefaultDialog", dirDefaultDialog.isSelected() ? "on" : "off");
    Jext.setProperty("editor.splitted", splitArea.isSelected() ? "on" : "off");
    Jext.setProperty("editor.autoScroll", autoScroll.getText());
    Jext.setProperty("editor.linesInterval", linesInterval.getText());
    Jext.setProperty("editor.linesIntervalEnabled", linesIntervalEnabled.isSelected() ? "on" : "off");
    Jext.setProperty("editor.wrapGuideOffset", wrapGuide.getText());
    Jext.setProperty("editor.wrapGuideEnabled", wrapGuideEnabled.isSelected() ? "on" : "off");
    Jext.setProperty("editor.splitted.orientation", (String) orientation.getSelectedItem());
    Jext.setProperty("use.selection", selection.isSelected() ? "on" : "off");
    Jext.setProperty("editor.wordmove.go_over_space", overSpace.isSelected() ? "on" : "off");
    Jext.setProperty("editor.extra_line_feed", addExtraLineFeed.isSelected() ? "on" : "off");
    Jext.setProperty("editor.line_end.preserve", preserveLineTerm.isSelected() ? "on" : "off");
    fonts.save();
  }


}

// End of EditorOptions.java