/*
 * 11/27/2000 - 15:04:22
 *
 * PrevCsWord.java - Finds previous case-sensitive word.
 * Copyright (C) 2000 Matt Benson
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

import java.util.Arrays;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;

import org.gjt.sp.jedit.textarea.TextUtilities;

import org.jext.JextTextArea;
import org.jext.MenuAction;

public class CsWord extends MenuAction
{
  public static final String[] DIRECTIONS = { "bkd", "fwd"};

  public static final String[] ACTIONS = { "nil", "sel", "del"};

  public static final int NO_ACTION = 0;
  public static final int SELECT = 1;
  public static final int DELETE = 2;

  private int action;

  private int direction;

  public CsWord(int action, int direction)
  {
    super("CsWord_" + "_" + ACTIONS[action] + "_" + DIRECTIONS[((direction > 0) ? 1 : 0)]);
    this.action = action;
    this.direction = direction;
  }//end constructor

  public void actionPerformed(ActionEvent evt)
  {
    JextTextArea textArea = getTextArea(evt);
    int start = textArea.getSelectionStart();
    if (action == DELETE)
    {
      if (start != textArea.getSelectionEnd())
      {
        textArea.setSelectedText("");
        return;
      }//end if a selection exists
    }//end if action == DELETE

    int caret = textArea.getCaretPosition();
    int line = textArea.getCaretLine();
    int lineStart = textArea.getLineStartOffset(line);
    caret -= lineStart;

    String lineText = textArea.getLineText(textArea.getCaretLine());

    caret += direction;
    try
    {
      int origCaret = caret;
      char origChar = lineText.charAt(caret);
      if (direction == TextUtilities.FORWARD)
      {
        char checkChar = lineText.charAt(caret - direction);
        if (!(Character.isLetterOrDigit(checkChar)))
        {
          caret -= direction;
          origChar = checkChar;
        }//end if Character.isLetterOrDigit(checkChar)
      }//end if FORWARD and not letter or digit
      caret = TextUtilities.findTypeChange(lineText, caret, direction);

      if (origCaret != caret)
      {
        char caretChar = lineText.charAt(caret);

        if ((!(Character.isLetterOrDigit(origChar) && Character.isLetterOrDigit(caretChar))// (these two
             || (Character.isUpperCase(origChar) && Character.isLowerCase(caretChar)))// or these two)
            && (direction == TextUtilities.BACKWARD))//and this

        {
          caret -= direction;
        }//end big fat if statement
        if ((Character.isLetterOrDigit(origChar) && Character.isLetterOrDigit(lineText.charAt(caret))) &&
                (caret + 1 == lineText.length()) && direction == TextUtilities.FORWARD)
        {
          caret += direction;
        }//end 2nd big fat if statement
        if (Character.isWhitespace(origChar) && Character.isWhitespace(caretChar))
        {
          try
          {
            while (Character.isWhitespace(lineText.charAt(caret)))
            {
              caret += direction;
            }//end while more white space (different types)
          }//end try
          catch (IndexOutOfBoundsException oobe_wan_kenoobi)
          {
            caret -= direction;
          }//end catch IndexOutOfBoundsException
        }//end if whitespace characters
      }//end if origCaret == caret
    }//end try
    catch (IndexOutOfBoundsException oobe)
    {
      try
      {
        textArea.getText().charAt(lineStart + caret);
      }//end try
      catch (IndexOutOfBoundsException oobeII)
      {
        textArea.getToolkit().beep();
        return;
      }//end catch
    }//end catch

    if (action == SELECT)
    {
      textArea.select(textArea.getMarkPosition(), lineStart + caret);
    }//end if select
    else
    {
      if (action == DELETE)
      {
        try
        {
          int documentPosition = caret + lineStart;
          int length = Math.abs(start - documentPosition);
          textArea.getDocument().remove(
                  ((direction == TextUtilities.FORWARD) ? start : documentPosition), length);
        }//end try to remove word
        catch (BadLocationException bl)
        {
          bl.printStackTrace();
        }//end catch
      }//end else
      else
      {
        textArea.setCaretPosition(lineStart + caret);
      }//end else
    }//end else
  }//end actionPerformed
}//end class CsWord

// End of CsWord.java
