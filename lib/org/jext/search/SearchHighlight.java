/*
 * 20:10:43 05/05/00
 *
 * SearchHighlight.java - Highlights anchor
 * Copyright (C) 2000 Romain Guy
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

package org.jext.search;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

import javax.swing.text.Element;

import org.jext.*;
import org.gjt.sp.jedit.textarea.*;

public class SearchHighlight implements TextAreaHighlight
{
  private ArrayList matches;
  private JextTextArea textArea;
  private TextAreaHighlight next;
  private boolean enabled = false;
  
  public void disable()
  {
    enabled = false;
  }

  public void enable()
  {
    enabled = true;
  }

  public void trigger(boolean on)
  {
    enabled = on;
  }

  public void setMatches(ArrayList matches)
  {
    this.matches = matches;
  }

  public void init(JEditTextArea textArea, TextAreaHighlight next)
  {
    this.textArea = (JextTextArea) textArea;
    this.next = next;
  }

  public void paintHighlight(Graphics gfx, int line, int y)
  {
    if (enabled && matches != null)
    {
      //gfx.setColor(Color.red);
      gfx.setColor(Color.blue);

      Element lineElement;
      Element map = textArea.getDocument().getDefaultRootElement();

      FontMetrics fm = textArea.getPainter().getFontMetrics();

      int[] pos = new int[2];
      int width = fm.charWidth('w');
      int myY = y + fm.getHeight() + fm.getLeading() + fm.getMaxDescent() + 1;
      int horOffset = textArea.getHorizontalOffset();
      int _width = textArea.getWidth();

      for (int i = 0; i < matches.size(); i++)
      {
        pos = ((SearchResult) matches.get(i)).getPos();
        int matchLine = map.getElementIndex(pos[0]);

        if (line == matchLine)
        {
          lineElement = map.getElement(line);

          //int off = (pos[0] - lineElement.getStartOffset()) * width + horOffset;
          int off = textArea.offsetToX(line, pos[0] - lineElement.getStartOffset());

          if (off >= horOffset && off < horOffset + _width)
          {
            int matchWidth = (pos[1] - pos[0]) * width + off;
  
            for ( ; off < matchWidth; off += 4)
            {
              gfx.drawLine(off, myY, off + 2, myY - 2);
              gfx.drawLine(off + 2, myY - 2, off + 4, myY);
            }
          }
        }
      }
    }

    if (next != null)
      next.paintHighlight(gfx, line, y);
  }

  public String getToolTipText(MouseEvent evt)
  {
    return null;
  }
  

}

// End of SearchHighlight.java