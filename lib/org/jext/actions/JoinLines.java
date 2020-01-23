/*
 * 06/01/2001 - 22:51:25
 *
 * JoinLines.java
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

import javax.swing.text.Document;
import javax.swing.text.Element;
import org.jext.*;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;

public class JoinLines extends MenuAction implements EditAction
{
  public JoinLines()
  {
    super("join_lines");
  }

  public void actionPerformed(ActionEvent evt)
  {
    JextTextArea textArea = getTextArea(evt);
    textArea.beginCompoundEdit();
    StringBuffer buffer = new StringBuffer();
    Document doc = textArea.getDocument();
    Element map = doc.getDefaultRootElement();
    try
    {
      int index = map.getElementIndex(textArea.getCaretPosition());
      if (index == map.getElementCount() - 1)
      {
        textArea.endCompoundEdit();
        return;
      }

      Element lineElement = map.getElement(index + 1);
      int start = lineElement.getStartOffset();
      int end = lineElement.getEndOffset() - 1;
      end -= start;
      buffer.append(' ').append(textArea.getText(start, end).trim());

      doc.remove(start, index == map.getElementCount() - 2 ? end : end + 1);
      doc.insertString(map.getElement(index).getEndOffset() - 1, buffer.toString(), null);
    } catch (BadLocationException ble) { }
    textArea.endCompoundEdit();
    textArea.getJextParent().updateStatus(textArea);
  }
}

// End of JoinLines.java
