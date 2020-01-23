/*
 * 22:50:44 04/12/99
 *
 * WordCount.java
 * Copyright (C) 1999 Romain Guy
 * Portions copyright (C) 1998-2000 Slava Pestov
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

import javax.swing.JOptionPane;
import javax.swing.text.Element;
import org.jext.*;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;

public class WordCount extends MenuAction
{
  private boolean wing;
  private String[] comments = new String[3];
  private int characters, lines, words, codeLines;

  public WordCount()
  {
    super("word_count");
  }

  public void actionPerformed(ActionEvent evt)
  {
    JextTextArea textArea = getTextArea(evt);
    textArea.beginCompoundEdit();

    wing = false;
    characters = lines = words = codeLines = 0;
    comments[0] = textArea.getProperty("blockComment");
    comments[1] = textArea.getProperty("commentStart");
    comments[2] = textArea.getProperty("commentEnd");

    Element map = textArea.getDocument().getDefaultRootElement();
    int count = map.getElementCount();

    for (int i = 0; i < count; i++)
    {
      Element lineElement = map.getElement(i);
      int start = lineElement.getStartOffset();
      int end = lineElement.getEndOffset() - 1;
      end -= start;
      doWordCount(textArea.getText(start, end));
    }

    textArea.endCompoundEdit();

    Object[] args = { Jext.getProperty("wordcount.characters") + String.valueOf(characters),
                      Jext.getProperty("wordcount.words") + String.valueOf(words),
                      Jext.getProperty("wordcount.lines") + String.valueOf(lines),
                      Jext.getProperty("wordcount.codeLines") +
                      (codeLines == -1 ? "n/a" : String.valueOf(codeLines)) };
    JOptionPane.showMessageDialog(getJextParent(evt), args, Jext.getProperty("wordcount.title"),
                                  JOptionPane.INFORMATION_MESSAGE);
  }

  private void doWordCount(String text)
  {
    lines++;
    boolean word = false;
    characters += text.length();

    if (comments[0] != null && comments[1] != null && comments[2] != null)
    {
      String buf = text.trim();
      if (wing)
        wing = !buf.endsWith(comments[2]);
      else
      {
        if (!buf.startsWith(comments[1]))
        {
          if (!buf.startsWith(comments[0]) && !buf.equals("") )
            codeLines++;
        } else
          wing = !buf.endsWith(comments[2]);
      }
    } else
      codeLines = -1;

    for (int i = 0; i < text.length(); i++)
    {
      switch (text.charAt(i))
      {
        case ' ': case '\t':
          if (word)
          {
            words++;
            word = false;
          }
          break;
        default:
          word = true;
          break;
      }
    }
  }
}

// End of WordCount.java
