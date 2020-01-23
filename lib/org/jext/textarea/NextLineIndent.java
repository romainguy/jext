/*
 * 19:42:39 17/01/00
 *
 * NextLineIndent.java - Go to start of next line no indent
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.jext.textarea;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.text.*;
import org.jext.*;
import org.gjt.sp.jedit.textarea.*;

public final class NextLineIndent extends MenuAction
{
  public NextLineIndent()
  {
    super("next_line_indent");
  }

  public void actionPerformed(ActionEvent evt)
  {
    JEditTextArea textArea = getTextArea(evt);

    Document doc = textArea.getDocument();
    Element map = doc.getDefaultRootElement();

    int caret = map.getElementIndex(textArea.getCaretPosition());
    if (map.getElementCount() == caret + 1)
    {
      textArea.setCaretPosition(map.getElement(caret).getStartOffset());
      return;
    }

    Element lineElement = map.getElement(caret + 1);
    int start = lineElement.getStartOffset();
    int length = lineElement.getEndOffset() - 1 - start;

    char c;
    int i = 0;
    String _line = textArea.getText(start, length);

out:  for ( ; i < length; i++)
    {
      c = _line.charAt(i);
      switch(c)
      {
        case ' ': case '\t':
          break;
        default:
          break out;
      }
    }

    textArea.setCaretPosition(start + i);
  }
}

// End of NextLineIndent.java
