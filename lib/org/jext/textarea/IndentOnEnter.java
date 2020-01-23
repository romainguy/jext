/*
 * 06/07/2001 - 00:44:34
 *
 * IndentOnEnter.java - Indents when enter is pressed
 * Copyright (C) 1998-1999 Romain Guy
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

package org.jext.textarea;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jext.*;
import org.gjt.sp.jedit.textarea.*;
import org.jext.misc.Indent;

public final class IndentOnEnter extends MenuAction implements EditAction
{
  public IndentOnEnter()
  {
    super("indent_on_enter");
  }

  public void actionPerformed(ActionEvent evt)
  {
    JextTextArea textArea = getTextArea(evt);
    textArea.beginCompoundEdit();
    textArea.setSelectedText("\n");
    if (textArea.getEnterIndent())
      Indent.indent(textArea, textArea.getCaretLine(), true, false);
    textArea.endCompoundEdit();
  }
}

// End of IndentOnEnter.java
