/*
 * 16:43:44 17/11/99
 *
 * LeftIndent.java
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

public class LeftIndent extends MenuAction implements EditAction
{
  public LeftIndent()
  {
    super("left_indent");
  }

  public void actionPerformed(ActionEvent evt)
  {
    JextTextArea textArea = getTextArea(evt);
    Document doc = textArea.getDocument();
    textArea.beginCompoundEdit();
    try
    {
      int tabSize = textArea.getTabSize();
      boolean noTabs = textArea.getSoftTab();
      Element map = textArea.getDocument().getDefaultRootElement();
      int start = map.getElementIndex(textArea.getSelectionStart());
      int end = map.getElementIndex(textArea.getSelectionEnd());
      for (int i = start; i <= end; i++)
      {
        Element lineElement = map.getElement(i);
        int lineStart = lineElement.getStartOffset();
        String line = doc.getText(lineStart, lineElement.getEndOffset() - lineStart - 1);
        int whiteSpace = Utilities.getLeadingWhiteSpace(line);
        if( whiteSpace == 0) continue;
        int whiteSpaceWidth = Math.max(0, Utilities.getLeadingWhiteSpaceWidth(line, tabSize)
                                          - tabSize);
        doc.remove(lineStart, whiteSpace);
        doc.insertString(lineStart, Utilities.createWhiteSpace(whiteSpaceWidth,
                                    (noTabs ? 0 : tabSize)), null);
      }
    } catch(BadLocationException ble) {
      ble.printStackTrace();
    }
    textArea.endCompoundEdit();
  }
}

// End of LeftIndent.java
