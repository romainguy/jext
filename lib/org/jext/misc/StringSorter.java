/*
 * 18:59:23 06/09/99
 *
 * StringSorter.java - Sort an array of Strings
 * Copyright (C) 1999 Romain Guy
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

package org.jext.misc;

import javax.swing.text.Element;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;

import java.util.*;
import org.jext.*;

public class StringSorter
{
  public static void sort(Document doc, boolean reverse)
  {
    sort(doc, 0, doc.getLength(), reverse);
  }

  public static void sort(Document doc, int offset, int length, boolean reverse)
  {
    if (doc == null) return;

    Element root = doc.getDefaultRootElement();
    Element lineElement;
    int fromIndex = root.getElementIndex(offset);
    int toIndex = root.getElementIndex(offset + length);
    String[] lines = new String[toIndex - fromIndex + 1];

    try
    {
      for (int i = 0; i < lines.length; i++)
      {
        lineElement = root.getElement(fromIndex + i);
        lines[i] = doc.getText(lineElement.getStartOffset(),
        lineElement.getEndOffset() - lineElement.getStartOffset());
        if (lines[i].endsWith("\n"))
          lines[i] = lines[i].substring(0, lines[i].length() - 1);
      }
      Arrays.sort(lines);

      StringBuffer buf = new StringBuffer();
      if (reverse)
      {
        for (int i = lines.length - 1; i > 0; i--)
          buf.append(lines[i].concat("\n"));
        buf.append(lines[0]);
      } else {
        for (int i = 0; i < lines.length - 1; i++)
          buf.append(lines[i].concat("\n"));
        buf.append(lines[lines.length - 1]);
      }

      int selStart = root.getElement(fromIndex).getStartOffset();
      int selLength = root.getElement(toIndex).getEndOffset() - selStart - 1;

      doc.remove(selStart, selLength);
      doc.insertString(selStart, buf.toString(), null);
    } catch (BadLocationException ble) { }
  }
}

// End of StringSorter.java
