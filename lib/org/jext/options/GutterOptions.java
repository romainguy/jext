/*
 * 14:39:32 28/08/00
 *
 * GutterOptions.java - Gutter options panel
 * Copyright (C) 2000 mike dillon
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

public class GutterOptions extends AbstractOptionPane
{
  private FontSelector font;
  private JComboBox numberAlignment;
  private JextCheckBox gutterExpanded, lineNumbersEnabled;
  private JTextField highlightInterval, gutterBorderWidth, gutterWidth;
  
  public GutterOptions()
  {
    super("gutter");

    gutterWidth = new JTextField();
    addComponent(Jext.getProperty("options.gutter.width"), gutterWidth);

    gutterBorderWidth = new JTextField();
    addComponent(Jext.getProperty("options.gutter.borderWidth"), gutterBorderWidth);

    highlightInterval = new JTextField();
    addComponent(Jext.getProperty("options.gutter.interval"), highlightInterval);

    String[] alignments = new String[] { "Left", "Center", "Right" };
    numberAlignment = new JComboBox(alignments);
    numberAlignment.setRenderer(new ModifiedCellRenderer());
    addComponent(Jext.getProperty("options.gutter.numberAlignment"), numberAlignment);

    font = new FontSelector("textArea.gutter");
    addComponent(Jext.getProperty("options.gutter.font"), font);

    gutterExpanded = new JextCheckBox(Jext.getProperty("options.gutter.expanded"));
    addComponent(gutterExpanded);

    lineNumbersEnabled = new JextCheckBox(Jext.getProperty("options.gutter.lineNumbers"));
    addComponent(lineNumbersEnabled);
    load();
  }

  public void load()
  {
    gutterWidth.setText(Jext.getProperty("textArea.gutter.width"));
    gutterBorderWidth.setText(Jext.getProperty("textArea.gutter.borderWidth"));
    highlightInterval.setText(Jext.getProperty("textArea.gutter.highlightInterval"));
    String alignment = Jext.getProperty("textArea.gutter.numberAlignment");
    if ("right".equals(alignment))
      numberAlignment.setSelectedIndex(2);
    else if ("center".equals(alignment))
      numberAlignment.setSelectedIndex(1);
    else
      numberAlignment.setSelectedIndex(0);
    /*gutterExpanded.getModel().setSelected(!"yes".equals(Jext.getProperty("textArea.gutter.collapsed")));
    lineNumbersEnabled.getModel().setSelected(!"no".equals(Jext.getProperty("textArea.gutter.lineNumbers")));*/
    gutterExpanded.setSelected(!"yes".equals(Jext.getProperty("textArea.gutter.collapsed")));
    lineNumbersEnabled.setSelected(!"no".equals(Jext.getProperty("textArea.gutter.lineNumbers")));
    font.load();
  }

  public void save()
  {
    Jext.setProperty("textArea.gutter.collapsed", gutterExpanded.getModel().isSelected() ? "no" : "yes");
    Jext.setProperty("textArea.gutter.lineNumbers", lineNumbersEnabled.getModel().isSelected() ? "yes" : "no");
    Jext.setProperty("textArea.gutter.width", gutterWidth.getText());
    Jext.setProperty("textArea.gutter.borderWidth", gutterBorderWidth.getText());
    Jext.setProperty("textArea.gutter.highlightInterval", highlightInterval.getText());
    String alignment = null;
    switch(numberAlignment.getSelectedIndex())
    {
      case 2:
        alignment = "right";
        break;
      case 1:
        alignment = "center";
        break;
      case 0: default:
        alignment = "left";
    }
    Jext.setProperty("textArea.gutter.numberAlignment", alignment);
    font.save();
  }
  

}

// End of GutterOptions.java