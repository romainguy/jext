/*
 * 15:52:56 27/11/99
 *
 * TabsToSpaces.java - By Slava Pestov
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
import org.jext.*;
import javax.swing.text.Document;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;

public class TabsToSpaces extends MenuAction implements EditAction
{
  public TabsToSpaces()
  {
    super("tabs_to_spaces");
  }

  public void actionPerformed(ActionEvent evt)
  {
    JextTextArea textArea = getTextArea(evt);
    textArea.beginCompoundEdit();
    Document doc = textArea.getDocument();
    try
    {
      Element map = doc.getDefaultRootElement();
      int count = map.getElementCount();
      for (int i = 0; i < count; i++)
      {
        Element lineElement = map.getElement(i);
        int start = lineElement.getStartOffset();
        int end = lineElement.getEndOffset() - 1;
        end -= start;
        int tabSize = textArea.getTabSize();
        String text = doTabsToSpaces(textArea.getText(start, end), tabSize);
        doc.remove(start, end);
        doc.insertString(start, text, null);
      }
    } catch (BadLocationException ble) { }
    textArea.endCompoundEdit();
  }

  private String doTabsToSpaces(String in, int tabSize)
  {
    StringBuffer buf = new StringBuffer();
    int width = 0;
    for (int i = 0; i < in.length(); i++)
    {
      switch (in.charAt(i))
      {
        case '\t':
          int count = tabSize - (width % tabSize);
          width += count;
          while(--count >= 0)
          buf.append(' ');
          break;
        case '\n':
          width = 0;
          buf.append(in.charAt(i));
          break;
        default:
          width++;
          buf.append(in.charAt(i));
          break;
      }
    }
    return buf.toString();
  }
}

// End of TabsToSpaces.java
