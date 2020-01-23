/*
 * 06/01/2001 - 22:51:46
 *
 * JoinAllLines.java
 * Copyright (C) 1999 Romain Guy
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

package org.jext.actions;

import java.util.*;
import javax.swing.text.*;
import org.jext.*;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;

public class JoinAllLines extends MenuAction implements EditAction
{
  public JoinAllLines()
  {
    super("join_all_lines");
  }

  public void actionPerformed(ActionEvent evt)
  {
    JextTextArea textArea = getTextArea(evt);
    textArea.beginCompoundEdit();
    Document doc = textArea.getDocument();
    StringTokenizer st = new StringTokenizer(textArea.getText(), "\n");
    try
    {
      doc.remove(0, textArea.getLength());
      while (st.hasMoreTokens())
        doc.insertString(textArea.getLength(), st.nextToken().trim() + ' ', null);
    } catch (BadLocationException ble) { }
    textArea.endCompoundEdit();
    textArea.getJextParent().updateStatus(textArea);
  }
}

// End of JoinAllLines.java
