/*
 * 08/29/2001 - 22:30:58
 *
 * IndentOnTab.java - Indents when TAB is pressed
 * Copyright (C) 2001 Romain Guy
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

package org.jext.textarea;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Document;

import org.jext.*;
import org.jext.misc.Indent;

import org.gjt.sp.jedit.textarea.*;

public final class IndentOnTab extends MenuAction implements EditAction
{
  public IndentOnTab()
  {
    super("indent_on_tab");
  }

  public void actionPerformed(ActionEvent evt)
  {
    JextTextArea textArea = getTextArea(evt);

    Document doc = textArea.getDocument();
    Element map = doc.getDefaultRootElement();
    int start = map.getElementIndex(textArea.getSelectionStart());
    int end = map.getElementIndex(textArea.getSelectionEnd());

    if (end - start != 0)
    {
      Jext.getAction("right_indent").actionPerformed(evt);
      return;
    }

    textArea.beginCompoundEdit();
    int len;
    int tabSize = textArea.getTabSize();
    int currLine = textArea.getCaretLine();

    if (Jext.getBooleanProperty("editor.tabStop"))
    {
      try
      {
        Element lineElement = map.getElement(currLine);
        int off = Utilities.getRealLength(doc.getText(lineElement.getStartOffset(),
                                                      textArea.getCaretPosition() - lineElement.getStartOffset()),
                                          tabSize);
        len = tabSize - (off % tabSize);
      } catch (BadLocationException ble) {
        len = tabSize;
      }
    } else
      len = tabSize;

    if (textArea.getTabIndent())
    {
      if (!Indent.indent(textArea, currLine, true, false))
        textArea.setSelectedText(Utilities.createWhiteSpace(len,
                                 textArea.getSoftTab() ? 0 : tabSize));
    } else {
      textArea.setSelectedText(Utilities.createWhiteSpace(len,
                               textArea.getSoftTab() ? 0 : tabSize));
    }
    textArea.endCompoundEdit();
  }
}

// End of IndentOnTab.java
