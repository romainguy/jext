/*
 * 06/08/2001 - 23:35:37
 *
 * FindAllDialog.java - Find all occurences of a pattern in current text area
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

import gnu.regexp.*;
import javax.swing.text.*;

import org.jext.*;

public class SearchResult
{
  private String str;
  private Position end;
  private Position start;
  private JextTextArea textArea;

  public SearchResult(JextTextArea textArea, Position start, Position end)
  {
    this.start = start;
    this.end = end;
    this.textArea = textArea;
    Element map = textArea.getDocument().getDefaultRootElement();
    int line = map.getElementIndex(start.getOffset());
    str = (line + 1) + ":" + getLine(map.getElement(line));
  }

  public int[] getPos()
  {
    int[] ret = new int[2];
    ret[0] = start.getOffset();
    ret[1] = end.getOffset();
    return ret;
  }

  private String getLine(Element elem)
  {
    if (elem == null)
      return "";
    String text = textArea.getText(elem.getStartOffset(), elem.getEndOffset() -
                                   elem.getStartOffset() - 1);
    text = text.substring(org.jext.Utilities.getLeadingWhiteSpace(text));
    if (text.length() > 70)
      text = text.substring(0, 70) + "...";
    return text;
  }

  public JextTextArea getTextArea()
  {
    return textArea;
  }

  public String toString()
  {
    return str;
  }
}

