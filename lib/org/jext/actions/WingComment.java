/*
 * 20:35:36 21/05/99
 *
 * WingComment.java
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

package org.jext.actions;

import org.jext.*;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;

public class WingComment extends MenuAction implements EditAction
{
  public WingComment()
  {
    super("wing_comment");
  }

  public void actionPerformed(ActionEvent evt)
  {
    JextTextArea textArea = getTextArea(evt);
    textArea.beginCompoundEdit();
    String commentStart = textArea.getProperty("commentStart");
    String commentEnd = textArea.getProperty("commentEnd");
    if (commentStart == null || commentEnd == null)
      return;
    commentStart = commentStart + ' ';
    commentEnd = ' ' + commentEnd;
    try
    {
      textArea.getDocument().insertString(textArea.getSelectionStart(), commentStart, null);
      textArea.getDocument().insertString(textArea.getSelectionEnd(), commentEnd, null);
    } catch(BadLocationException ble) { }
    textArea.setCaretPosition(textArea.getCaretPosition());
    textArea.endCompoundEdit();
  }
}

// End of WingComment.java
