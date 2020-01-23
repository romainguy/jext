/*
 * 20:35:36 21/05/99
 *
 * SimpleComment.java
 * Copyright (C) 1999 Romain Guy
 * Portions copyright (C) 1998-2000 Slava Pestov
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

import javax.swing.text.*;
import org.jext.*;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;

public class SimpleComment extends MenuAction implements EditAction
{
  public SimpleComment()
  {
    super("simple_comment");
  }

  public void actionPerformed(ActionEvent evt)
  {
    JextTextArea textArea = getTextArea(evt);
    String comment = textArea.getProperty("blockComment");
    if (comment == null)
      return;

    Document doc = textArea.getDocument();

    int selectionStart = textArea.getSelectionStart();
    int selectionEnd = textArea.getSelectionEnd();
    Element map = doc.getDefaultRootElement();
    int startLine = map.getElementIndex(selectionStart);
    int endLine = map.getElementIndex(selectionEnd);

    textArea.beginCompoundEdit();

    // better use textArea.getLineStartOffset(line)
    //            textArea.getLineEndOffset(line) and so on..
    try
    {
      StringBuffer buf = new StringBuffer(selectionEnd - selectionStart + 
                                          ((comment.length() + 1) * (endLine - startLine)));

      for (int i = startLine; i <= endLine; i++)
      {
        int start = map.getElement(i).getStartOffset();
        int end = map.getElement(i).getEndOffset() - 1;
        end -= start;
        buf.append(comment).append(textArea.getText(start, end));
        if (i != endLine)
          buf.append('\n');
      }

      int start = map.getElement(startLine).getStartOffset();
      doc.remove(start, map.getElement(endLine).getEndOffset() - 1 - start);
      doc.insertString(start, buf.toString(), null);
    } catch(BadLocationException ble) { }

    textArea.setCaretPosition(textArea.getCaretPosition());
    textArea.endCompoundEdit();
  }
}

// End of SimpleComment.java
