/*
 * 15:50:41 18/03/00
 *
 * DisabledCellRenderer.java - Cell renderer for non-editable cells
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

package org.jext.gui;

import java.awt.Component;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class DisabledCellRenderer extends JLabel implements TableCellRenderer
{
  public DisabledCellRenderer()
  {
    setOpaque(true);
    setBorder(new EmptyBorder(1, 1, 1, 1));
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean cellHasFocus,
                                                 int row, int col)
  {
    setBackground(table.getBackground());
    setForeground(table.getForeground());
    setText(value.toString());
    return this;
  }
}

// End of DisabledCellRenderer.java
