/*
 * 06/27/2002 - 16:34:29
 *
 * CompleteWord.java
 * Portions copyright (C) 1998-2000 Slava Pestov and Valery Kondakoff
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

package org.jext.actions;

import java.awt.event.ActionEvent;

import java.util.TreeSet;
import java.util.Iterator;

import javax.swing.text.Element;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;

import org.jext.*;
import org.jext.misc.*;

import org.gjt.sp.jedit.textarea.*;

public class CompleteWord extends MenuAction implements EditAction
{
  public CompleteWord()
  {
    super("complete_word");
  }

  public void actionPerformed(ActionEvent evt)
  {
    JextFrame parent = getJextParent(evt);
    JextTextArea textArea = parent.getTextArea();
    Document buffer = textArea.getDocument();
    String noWordSep = textArea.getProperty("noWordSep");
    if (noWordSep == null)
      noWordSep = "";

    String line = textArea.getLineText(textArea.getCaretLine());
    int dot = textArea.getCaretPosition() - textArea.getLineStartOffset(textArea.getCaretLine());
    if (dot == 0)
      return;

    int wordStart = TextUtilities.findWordStart(line, dot - 1, noWordSep);
    String word = line.substring(wordStart, dot);
    if (word.length() == 0)
      return;

    parent.showWaitCursor();

		TreeSet completions = new TreeSet();
    int wordLen = word.length();

    for (int i = 0; i < textArea.getLineCount(); i++)
    {
      line = textArea.getLineText(i);

      if (line.startsWith(word))
      {
        String _word = getWord(line, 0, noWordSep);
        if (_word.length() != wordLen)
        {
					completions.add(_word);
        }
      }

      int len = line.length() - word.length();
      for (int j = 0; j < len; j++)
      {
        char c = line.charAt(j);
        if (!Character.isLetterOrDigit(c) && noWordSep.indexOf(c) == -1)
        {
          if (line.regionMatches(j + 1, word, 0, wordLen))
          {
            String _word = getWord(line, j + 1, noWordSep);
            if (_word.length() != wordLen)
            {
							completions.add(_word);
            }
          }
        }
      }
    }
		
		if (completions.size() > 1)
		{
//look for a common partial match
			int endIndex = String.valueOf(completions.first()).length();
			Iterator iter = completions.iterator();
			while (iter.hasNext())
			{
				endIndex = Math.min(endIndex,
				 getDivergentIndex(String.valueOf(completions.first()), String.valueOf(iter.next())));
			}//end while more elements

			parent.hideWaitCursor();
			
			if (endIndex > wordLen)
			{
				textArea.setSelectedText(
				 String.valueOf(completions.first()).substring(wordLen, endIndex));
			}//end if a partial match was found
			else
			{
				new CompleteWordList(parent, word,
				 (String[])(completions.toArray(new String[completions.size()])));
			}//end else--all matches unique beyond what was originally typed
		}//end if more than one possible completion
		else
		{
			parent.hideWaitCursor();
			if (completions.size() == 1)
			{
				textArea.setSelectedText(
				 String.valueOf(completions.first()).substring(wordLen));
			}//end if only one possible completion
		}//end else, one or zero completions
  }//end actionPerformed
	
	
	private int getDivergentIndex(String str1, String str2)
	{
		int result = str1.length();
		if (!(str1.equals(str2)))
		{
			for (result = 0;
			 result < str1.length() &&
			 result < str2.length() &&
			 str1.charAt(result) == str2.charAt(result);
			 result++);
		}//end if the Strings are not equal
		return result;
	}//end getDivergentIndex
	

  private String getWord(String line, int offset, String noWordSep)
  {
    int wordEnd = TextUtilities.findWordEnd(line, offset + 1, noWordSep);
    return line.substring(offset, wordEnd);
  }
}

// End of CompleteWord.java
