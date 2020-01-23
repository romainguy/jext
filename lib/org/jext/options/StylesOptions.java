/*
 * StylesOptions.java - Color/style option pane
 * Copyright (C) 1999 Slava Pestov
 * Portions copyright (C) 1999 mike dillon
 * Portions copyright (C) 1999-2001 Romain Guy
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
import java.awt.event.*;

import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.EmptyBorder;

import org.jext.*;
import org.jext.gui.*;
//import org.gjt.sp.jedit.syntax.SyntaxStyle;

public class StylesOptions extends AbstractOptionPane
{
  public static final EmptyBorder noFocusBorder = new EmptyBorder(1, 1, 1, 1);

  public StylesOptions()
  {
    super("styles");

    setLayout(new GridLayout(2, 1));

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(BorderLayout.NORTH, new JLabel(Jext.getProperty("options.styles.colors")));
    panel.add(BorderLayout.CENTER, createColorTableScroller());
    add(panel);

    setLayout(new GridLayout(2, 1));

    panel = new JPanel(new BorderLayout());
    panel.add(BorderLayout.NORTH, new JLabel(Jext.getProperty("options.styles.styles")));
    panel.add(BorderLayout.CENTER, createStyleTableScroller());
    add(panel);
  }

  public void save()
  {
    colorModel.save();
    styleModel.save();
  }
  public void load()
  {
    colorModel.load();
    styleModel.load();
  }

  // private members
  private ColorTable.ColorTableModel colorModel;
  private ColorTable colorTable;
  private StyleTable.StyleTableModel styleModel;
  private StyleTable styleTable;
  
  private JScrollPane createColorTableScroller()
  {
    colorModel = createColorTableModel();
    colorTable = new ColorTable(colorModel);
    Dimension d = colorTable.getPreferredSize();
    d.height = Math.min(d.height, 100);
    JScrollPane scroller = new JScrollPane(colorTable);
    scroller.setPreferredSize(d);
    return scroller;
  }

  private ColorTable.ColorTableModel createColorTableModel()
  {
    ColorTable.ColorTableModel model = new ColorTable.ColorTableModel();
    model.addColorChoice("options.styles.bgColor", "editor.bgColor");
    model.addColorChoice("options.styles.fgColor", "editor.fgColor");
    model.addColorChoice("options.styles.caretColor", "editor.caretColor");
    model.addColorChoice("options.styles.selectionColor", "editor.selectionColor");
    model.addColorChoice("options.styles.highlightColor", "editor.highlightColor");
    model.addColorChoice("options.styles.lineHighlightColor", "editor.lineHighlightColor");
    model.addColorChoice("options.styles.linesHighlightColor", "editor.linesHighlightColor");
    model.addColorChoice("options.styles.bracketHighlightColor", "editor.bracketHighlightColor");
    model.addColorChoice("options.styles.wrapGuideColor", "editor.wrapGuideColor");
    model.addColorChoice("options.styles.eolMarkerColor", "editor.eolMarkerColor");
    model.addColorChoice("options.styles.gutterBgColor", "textArea.gutter.bgColor");
    model.addColorChoice("options.styles.gutterFgColor", "textArea.gutter.fgColor");
    model.addColorChoice("options.styles.gutterHighlightColor", "textArea.gutter.highlightColor");
    model.addColorChoice("options.styles.gutterBorderColor", "textArea.gutter.borderColor");
    model.addColorChoice("options.styles.gutterAnchorMarkColor", "textArea.gutter.anchorMarkColor");
    model.addColorChoice("options.styles.gutterCaretMarkColor", "textArea.gutter.caretMarkColor");
    model.addColorChoice("options.styles.gutterSelectionMarkColor", "textArea.gutter.selectionMarkColor");
    model.addColorChoice("options.styles.consoleBgColor", "console.bgColor");
    model.addColorChoice("options.styles.consoleOutputColor", "console.outputColor");
    model.addColorChoice("options.styles.consolePromptColor", "console.promptColor");
    model.addColorChoice("options.styles.consoleErrorColor", "console.errorColor");
    model.addColorChoice("options.styles.consoleInfoColor", "console.infoColor");
    model.addColorChoice("options.styles.consoleSelectionColor", "console.selectionColor");
    model.addColorChoice("options.styles.vfSelectionColor", "vf.selectionColor");
    model.addColorChoice("options.styles.buttonsHighlightColor", "buttons.highlightColor");
    return model;
  }

  private JScrollPane createStyleTableScroller()
  {
    styleModel = createStyleTableModel();
    styleTable = new StyleTable(styleModel);
    Dimension d = styleTable.getPreferredSize();
    d.height = Math.min(d.height, 100);
    JScrollPane scroller = new JScrollPane(styleTable);
    scroller.setPreferredSize(d);
    return scroller;
  }

  private StyleTable.StyleTableModel createStyleTableModel()
  {
    StyleTable.StyleTableModel model = new StyleTable.StyleTableModel();
    model.addStyleChoice("options.styles.comment1Style", "editor.style.comment1");
    model.addStyleChoice("options.styles.comment2Style", "editor.style.comment2");
    model.addStyleChoice("options.styles.literal1Style", "editor.style.literal1");
    model.addStyleChoice("options.styles.literal2Style", "editor.style.literal2");
    model.addStyleChoice("options.styles.labelStyle", "editor.style.label");
    model.addStyleChoice("options.styles.keyword1Style", "editor.style.keyword1");
    model.addStyleChoice("options.styles.keyword2Style", "editor.style.keyword2");
    model.addStyleChoice("options.styles.keyword3Style", "editor.style.keyword3");
    model.addStyleChoice("options.styles.operatorStyle", "editor.style.operator");
    model.addStyleChoice("options.styles.invalidStyle", "editor.style.invalid");
    model.addStyleChoice("options.styles.methodStyle", "editor.style.method");
    return model;
  }
  


}//end class StylesOptions