/*
 * FastSyntax.java - Easy accessible Syntax switcher
 * Copyright (C) 2000 Romain Guy
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

package org.jext.toolbar;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.jext.*;
import org.jext.gui.*;
import org.jext.event.*;

public class FastSyntax extends JComboBox implements ActionListener, JextListener
{
  private JextFrame parent;

  private static String[] modeNames;
  static
  {
    modeNames = new String[Jext.modes.size()];
    for (int i = 0; i < modeNames.length; i++)
      modeNames[i] = ((Mode) Jext.modes.get(i)).getUserModeName();
  }
  
  public FastSyntax(JextFrame parent)
  {
    super(modeNames);

    this.parent = parent;
    addActionListener(this);
    parent.addJextListener(this);

    setRenderer(new ModifiedCellRenderer());

    selectMode(parent.getTextArea());
    setMaximumSize(getPreferredSize());
  }

  public void jextEventFired(JextEvent evt)
  {
    int type = evt.getWhat();
    if (type == JextEvent.TEXT_AREA_SELECTED || type == JextEvent.PROPERTIES_CHANGED)
      selectMode(evt.getTextArea());
  }

  private void selectMode(JextTextArea textArea)
  {
    int i = 0;
    String _mode;

    if (textArea == null)
    {
      _mode = Jext.getProperty("editor.colorize.mode");
    } else
      _mode = textArea.getColorizingMode();

    for ( ; i < modeNames.length; i++)
    {
      if (_mode.equals(((Mode) Jext.modes.get(i)).getModeName()))
        break;
    }

    // selectMode = true;
    setSelectedItem(modeNames[i]);
  }

  public void actionPerformed(ActionEvent evt)
  {
    JextTextArea textArea = parent.getTextArea();
    if (evt.getSource() == this && textArea != null)
    {
      String mode = ((Mode) Jext.modes.get(getSelectedIndex())).getModeName();
      if (!mode.equalsIgnoreCase(textArea.getColorizingMode()))
        textArea.setColorizing((Mode) Jext.modes.get(getSelectedIndex()));
      textArea.grabFocus();
    }
  }
}

// End of FastSyntax.java