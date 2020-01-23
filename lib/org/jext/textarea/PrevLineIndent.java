/*
 * 19:38:05 17/01/00
 *
 * PrevLineIndent.java - Go to previous line start, no indent
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.jext.textarea;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.text.*;

import org.jext.*;
import org.gjt.sp.jedit.textarea.*;

public final class PrevLineIndent extends MenuAction
{
  public PrevLineIndent()
  {
    super("prev_line_indent");
  }

  public void actionPerformed(ActionEvent evt)
  {
    JEditTextArea textArea = getTextArea(evt);

    Document doc = textArea.getDocument();
    Element map = doc.getDefaultRootElement();

    int caret = map.getElementIndex(textArea.getCaretPosition());
    if (caret == 0)
    {
      textArea.setCaretPosition(map.getElement(caret).getStartOffset());
      return;
    }

    Element lineElement = map.getElement(caret - 1);
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

// End of PrevLineIndent.java
