/*
 * 15:52:21 27/11/99
 *
 * SpacesToTabs.java - By Slava Pestov
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

public class SpacesToTabs extends MenuAction implements EditAction
{
  public SpacesToTabs()
  {
    super("spaces_to_tabs");
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
        String text = doSpacesToTabs(textArea.getText(start, end), textArea.getTabSize());
        doc.remove(start, end);
        doc.insertString(start, text, null);
      }
    } catch (BadLocationException ble) { }
    textArea.endCompoundEdit();
  }

  private String doSpacesToTabs(String in, int tabSize)
  {
    StringBuffer buf = new StringBuffer();
    for(int i = 0, width = 0, whitespace = 0; i < in.length(); i++)
    {
      switch(in.charAt(i))
      {
        case ' ':
          whitespace++;
          width++;
          break;
        case '\t':
          int tab = tabSize - (width % tabSize);
          width += tab;
          whitespace += tab;
          break;
        case '\n':
          whitespace = 0;
          width = 0;
          buf.append('\n');
          break;
        default:
          if (whitespace != 0)
          {
            if (whitespace >= tabSize / 2 && whitespace > 1)
            {
              int indent = whitespace +	((width - whitespace) % tabSize);
              int tabs = indent / tabSize;
              int spaces = indent % tabSize;
              while (tabs-- > 0)
                buf.append('\t');
              while (spaces-- > 0)
                buf.append(' ');
            } else {
              while (whitespace-- > 0)
                buf.append(' ');
            }
            whitespace = 0;
          }
          buf.append(in.charAt(i));
          width++;
          break;
      }
    }
    return buf.toString();
  }
}

// End of SpacesToTabs.java