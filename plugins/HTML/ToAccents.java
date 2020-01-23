/*
 * 13:54:21 03/04/99
 *
 * ToAccents.java
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

import java.util.Hashtable;
import javax.swing.text.Element;
import org.jext.*;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;

public class ToAccents extends MenuAction
{
  public ToAccents()
  {
    super("to_accents");
  }

  public void actionPerformed(ActionEvent evt)
  {
    JextTextArea textArea = getTextArea(evt);
    String selection = textArea.getSelectedText();
    if (selection != null)
      textArea.setSelectedText(doAccents(selection));
    else
    {
      try
      {
        textArea.beginCompoundEdit();
        Element map = textArea.getDocument().getDefaultRootElement();
        int count = map.getElementCount();
        for (int i = 0; i < count; i++)
        {
          Element lineElement = map.getElement(i);
          int start = lineElement.getStartOffset();
          int end = lineElement.getEndOffset() - 1;
          end -= start;
          String text = doAccents(textArea.getText(start, end));
          textArea.getDocument().remove(start, end);
          textArea.getDocument().insertString(start, text, null);
        }
        textArea.endCompoundEdit();
      }
      catch (BadLocationException ble) { }
    }
  }

  private String doAccents(String html)
  {
    Hashtable replace = new Hashtable(19);
    replace.put("&eacute;", "é");
    replace.put("&egrave;", "è");
    replace.put("&ecirc;", "ê");
    replace.put("&euml;", "ë");
    replace.put("&agrave;", "à");
    replace.put("&acirc;", "â");
    replace.put("&auml;", "ä");
    replace.put("&icirc;", "î");
    replace.put("&iuml;", "ï");
    replace.put("&ugrave;", "ù");
    replace.put("&uuml;", "ü");
    replace.put("&ucirc;", "û");
    replace.put("&ograve;", "ô");
    replace.put("&ouml;", "ö");
    replace.put("&ccedil;", "ç");
    replace.put("&szlig;", "ß");
    replace.put("&Auml;", "Ä");
    replace.put("&Ouml;", "Ö");
    replace.put("&Uuml;", "Ü");

    StringBuffer buf = new StringBuffer();
    int entityOff = -1;

    for (int i = 0; i < html.length(); i++)
    {
      switch(html.charAt(i))
      {
        case '&':
          if (entityOff == -1)
            entityOff = i;
          break;
        case ';':
          if (entityOff != -1)
          {
            String entity = html.substring(entityOff, i + 1);
            String accent = (String)replace.get(entity);
            buf.append(accent == null ? entity : accent);
	    entityOff = -1;
	    break;
	  }
        default:
          if (entityOff == -1)
            buf.append(html.charAt(i));
          break;
      }
    }
    return buf.toString();
  }
}

// End of ToAccents.java
