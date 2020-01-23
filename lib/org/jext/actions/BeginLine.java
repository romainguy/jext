/*
 * 08/06/2001 - 23:42:32
 *
 * BeginLine.java
 * Copyright (C) 2001 Romain Guy
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
import javax.swing.text.Document;
import org.jext.*;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;

public class BeginLine extends MenuAction implements EditAction
{
  public BeginLine()
  {
    super("begin_lines_with");
  }

  public void actionPerformed(ActionEvent evt)
  {
    String response = JOptionPane.showInputDialog(getJextParent(evt),
                                                  Jext.getProperty("add.line.label"),
                                                  Jext.getProperty("begin.line.title"),
                                        		      JOptionPane.QUESTION_MESSAGE);
    JextTextArea textArea = getTextArea(evt);
    if (response == null)
      return;

    textArea.beginCompoundEdit();
    Document doc = textArea.getDocument();

    try
    {
      Element map = doc.getDefaultRootElement();
      int firstLine =  map.getElementIndex(textArea.getSelectionStart());
      int lastLine =  map.getElementIndex(textArea.getSelectionEnd());
      for (int i = firstLine; i <= lastLine; i++)
      {
        Element lineElement = map.getElement(i);
        int start = lineElement.getStartOffset();
        int end = lineElement.getEndOffset() - 1;
        end -= start;
        String text = response + textArea.getText(start, end);
        doc.remove(start, end);
        doc.insertString(start, text, null);
      }
    } catch (BadLocationException ble) { }
    textArea.endCompoundEdit();
  }
}

// End of BeginLine.java
