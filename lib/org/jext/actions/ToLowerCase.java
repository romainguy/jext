/*
 * 18:46:33 25/03/99
 *
 * ToLowerCase.java
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

import javax.swing.text.Element;
import javax.swing.text.Document;
import org.jext.*;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;

public class ToLowerCase extends MenuAction implements EditAction
{
  public ToLowerCase()
  {
    super("to_lower_case");
  }

  public void actionPerformed(ActionEvent evt)
  {
    JextTextArea textArea = getTextArea(evt);
    textArea.beginCompoundEdit();
    String selection = textArea.getSelectedText();

    if (selection != null)
    {
      textArea.setSelectedText(selection.toLowerCase());
    } else {
      Document doc = textArea.getDocument();
      try
      {
        int pos = textArea.getCaretPosition();
        int line = textArea.getLineOfOffset(pos);
        int start = textArea.getLineStartOffset(line);
        int end = textArea.getLineEndOffset(line) - 1;

        if (pos == end)
        {
          textArea.endCompoundEdit();
          return;
        }

        end -= start;

        char c = textArea.getText(start, end).charAt(pos - start);
        doc.remove(pos, 1);
        doc.insertString(pos, new StringBuffer(1).append(c).toString().toLowerCase(), null);
        textArea.setCaretPosition(pos + 1);

//        Element map = doc.getDefaultRootElement();
//        int count = map.getElementCount();
//        for (int i = 0; i < count; i++)
//        {
//          Element lineElement = map.getElement(i);
//          int start = lineElement.getStartOffset();
//          int end = lineElement.getEndOffset() - 1;
//          end -= start;
//          String text = textArea.getText(start, end).toLowerCase();
//          doc.remove(start, end);
//          doc.insertString(start, text, null);
//        }
      } catch (BadLocationException ble) { }
    }
    textArea.endCompoundEdit();
  }
}

// End of ToLowerCase.java
