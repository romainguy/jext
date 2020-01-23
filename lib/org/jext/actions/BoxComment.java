/*
 * 11/19/2001 - 00:47:08
 *
 * BoxComment.java
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

import javax.swing.text.Element;
import org.jext.*;
import javax.swing.text.Document;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;

public class BoxComment extends MenuAction implements EditAction
{
  public BoxComment()
  {
    super("box_comment");
  }

  public void actionPerformed(ActionEvent evt)
  {
    JextTextArea textArea = getTextArea(evt);
    Document doc = textArea.getDocument();

    String commentStart = textArea.getProperty("commentStart");
    String commentEnd = textArea.getProperty("commentEnd");
    String boxComment = textArea.getProperty("boxComment");

    if (commentStart == null || commentEnd == null || boxComment == null)
      return;

    commentStart = commentStart + ' ';
    commentEnd = ' ' + commentEnd;
    boxComment = boxComment + ' ';

    int selectionStart = textArea.getSelectionStart();
    int selectionEnd = textArea.getSelectionEnd();

    Element map = doc.getDefaultRootElement();
    int startLine = map.getElementIndex(selectionStart);
    int endLine = map.getElementIndex(selectionEnd);

    textArea.beginCompoundEdit();

    try
    {
      Element lineElement = map.getElement(startLine);
      int start = lineElement.getStartOffset();
      int indent = Utilities.getLeadingWhiteSpace(doc.getText(start, lineElement.getEndOffset() - start));
      doc.insertString(Math.max(start + indent, selectionStart), commentStart, null);

      for (int i = startLine + 1; i < endLine; i++)
      {
        lineElement = map.getElement(i);
        start = lineElement.getStartOffset();
        indent = Utilities.getLeadingWhiteSpace(doc.getText(start, lineElement.getEndOffset() - start));
        doc.insertString(start + indent, boxComment, null);
      }

      lineElement = map.getElement(endLine);
      start = lineElement.getStartOffset();
      indent = Utilities.getLeadingWhiteSpace(doc.getText(start, lineElement.getEndOffset() - start));

      if (startLine < endLine)
        doc.insertString(start + indent, boxComment, null);
      doc.insertString(Math.max(start + indent, textArea.getSelectionEnd()), commentEnd, null);
      
      textArea.setCaretPosition(textArea.getCaretPosition());
    } catch(BadLocationException ble) { }
    textArea.endCompoundEdit();
  }
}

// End of BoxComment.java
