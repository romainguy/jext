/*
 * 09/27/2002 - 12:57:46
 *
 * RemoveWhitespace.java
 * Copyright (C) 2002 Matt Benson
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

import javax.swing.text.Element;
import javax.swing.text.Document;
import org.jext.*;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;

/**
 * Remove whitespace from ends of lines.  Based on <CODE>RemoveSpaces</CODE>.
 *
 * @author <a href="mailto:orangeherbert@users.sourceforge.net">Matt Benson</a>
 */
public class RemoveWhitespace
 extends MenuAction
 implements EditAction
{
	
/**
 * Construct a new <CODE>RemoveWhitespace</CODE> action.
 */
  public RemoveWhitespace()
  {
    super("remove_end_whitespace");
  }//end constructor

//inherit doc
  public void actionPerformed(ActionEvent evt)
  {
    JextTextArea textArea = getTextArea(evt);
    textArea.beginCompoundEdit();
    Document doc = textArea.getDocument();
    try
    {
      Element map = doc.getDefaultRootElement();
      int count = map.getElementCount();
      for (int i = 0; i < count; i++)
      {
        Element lineElement = map.getElement(i);
        int start = lineElement.getStartOffset();
        int end = lineElement.getEndOffset() - 1;
        end -= start;
        String text = doRemove(textArea.getText(start, end));
				doc.remove(start, end);
				if (text != null)
				{
					doc.insertString(start, text, null);
				}//end if text is not null
      }//end for through lines
      textArea.endCompoundEdit();
    }//end try to do the work
		catch (BadLocationException ble)
		{
			//do nothing
		}//end catch BadLocationException
  }//end actionPerformed

  private String doRemove(String in)
  {
		int end = in.length();
		while (--end >= 0 && Character.isWhitespace(in.charAt(end)));
		return (end < 0) ? null : in.substring(0, end + 1);
  }//end doRemove
	
}//end class RemoveWhitespace
