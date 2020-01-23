/*
 * 08/30/2001 - 10:27:04
 *
 * ModifiedCellRenderer.java - Cell renderer for non-editable cells
 * Copyright (C) 2001 Romain Guy
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

import java.awt.*;
import javax.swing.*;

public class ModifiedCellRenderer extends DefaultListCellRenderer
{
  public Component getListCellRendererComponent(JList list,
                                                Object value,
                                                int index,
                                                boolean isSelected,
                                                boolean cellHasFocus)
  {
    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    setOpaque(isSelected);
    return this;
  }
}

// End of ModifiedCellRenderer.java
