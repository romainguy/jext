/*
 * 08/06/2001 - 23:43:17
 *
 * SimpleUnComment.java
 * Copyright (C) 2001 Romain Guy
 * Portions copyright (C) 1998-2000 Slava Pestov
 *
 * This particular class was written by Greg Brouelette
 * broulet@ixpres.com
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

public class SimpleUnComment extends MenuAction implements EditAction
{
  public SimpleUnComment()
  {
    super("simple_uncomment");
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
   
   try
   {
      String line = doc.getText(selectionStart, selectionEnd - selectionStart);
      
      for (int i = startLine ; i <= endLine; i++)
      {
        int startOffset = map.getElement(i).getStartOffset();
        int endOffset = map.getElement(i).getEndOffset();
        if (startOffset < selectionEnd)
          possiblyUncomentThisLine(doc, textArea, startOffset , endOffset - startOffset );
      }  
   } catch(BadLocationException ble) { }

   textArea.setCaretPosition(textArea.getCaretPosition());
   textArea.endCompoundEdit();
  }
  
  private void possiblyUncomentThisLine(Document doc, JextTextArea textArea, int startIndex, int runLength)
  {
    String line = new String();
    try
    {
      line = doc.getText(startIndex, runLength);
    } catch (BadLocationException ble) {
      return;
    }  
  
    String comment = textArea.getProperty("blockComment");
    String tmp = line.trim();
    int index = tmp.indexOf(comment);

    if (index == 0)
    {
      int trueIndex = line.indexOf(comment);
      // Now we know how much to delete so DO IT!
      try
      {
        if ((startIndex + comment.length()) <= textArea.getSelectionEnd())
          doc.remove(startIndex + trueIndex, comment.length());
      } catch (BadLocationException ble) {
        return;
      }
    }
  }
}

// End of SimpleUnComment.java
