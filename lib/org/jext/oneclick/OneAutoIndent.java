/*
 * OneAutoIndent.java
 * Copyright (C) 2000 Romain Guy
 *
 * This	free software; you can redistribute it and/or
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

package org.jext.oneclick;

import java.awt.event.ActionEvent;

import org.jext.JextTextArea;
import org.jext.OneClickAction;
import org.jext.misc.Indent;

public class OneAutoIndent extends OneClickAction
{
  public OneAutoIndent()
  {
    super("one_auto_indent");
  }

  public void oneClickActionPerformed(ActionEvent evt)
  {
    JextTextArea area = getTextArea(evt);
    Indent.indent(area, area.getCaretLine(), true, false);
  }
}

// End of OneAutoIndent.java
