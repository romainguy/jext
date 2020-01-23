/*
 * 18:13:11 15/06/99
 *
 * PreviousTag.java
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

import javax.swing.text.Element;
import org.jext.*;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;

public class PreviousTag extends MenuAction
{
  public PreviousTag()
  {
    super("previous_tag");
  }

  public void actionPerformed(ActionEvent evt)
  {
    JextTextArea textArea = getTextArea(evt);
    Element map = textArea.getDocument().getDefaultRootElement();
    int pos = textArea.getCaretPosition();
    int index = map.getElementIndex(pos);
    for (int i = index; i >= 0; i--)
    {
      Element lineElement = map.getElement(i);
      int start = lineElement.getStartOffset();
      int end = lineElement.getEndOffset() - 1;
      end -= start;
      if (i != index)
        pos = end;
      else
        pos -= start;
      int tag = seekTag(textArea.getText(start, end), pos);
      if (tag != -1)
      {
        textArea.setCaretPosition(start + tag);
        break;
      }
      if (i == index) pos = textArea.getCaretPosition();
    }
  }

  private int seekTag(String in, int index)
  {
    boolean tag = false;
    for (int i = index - 1; i >= 0; i--)
    {
      switch (in.charAt(i))
      {
        case '>':
          tag = true;
          break;
        case '<':
          if (tag)
	    return i;
          break;
      }
    }
    return -1;
  }
}

// End of PreviousTag.java
