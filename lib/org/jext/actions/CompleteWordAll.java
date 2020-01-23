/*
 * 22:19:57 27/08/00
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

 * Modified in 2002 to CompleteWordAll.java by Joao Carvalho
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

public class CompleteWordAll extends MenuAction implements EditAction
{
	public CompleteWordAll()
	{
		super("complete_word_all");
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		JextFrame parent = getJextParent(evt);
		JextTextArea textArea;
		JextTextArea textAreaAnt = parent.getTextArea();
		TreeSet completions = new TreeSet();
		JextTextArea aTextArea[] = parent.getTextAreas();
		
		Document buffer = textAreaAnt.getDocument();
		String noWordSep = textAreaAnt.getProperty("noWordSep");
		if (noWordSep == null)
			noWordSep = "";
		
		String line = textAreaAnt.getLineText(textAreaAnt.getCaretLine());
		int dot = textAreaAnt.getCaretPosition() - textAreaAnt.getLineStartOffset(textAreaAnt.getCaretLine());
		if (dot == 0)
			return;
		
		int wordStart = TextUtilities.findWordStart(line, dot - 1, noWordSep);
		String word = line.substring(wordStart, dot);
		if (word.length() == 0)
			return;
		
		int wordLen = word.length();
		
		parent.showWaitCursor();
		
		
		for(int h=0; h < aTextArea.length; h++){
			textArea = aTextArea[h];
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
			
			
		}//end of for.
		
		
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
				textAreaAnt.setSelectedText(
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
				textAreaAnt.setSelectedText(
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
