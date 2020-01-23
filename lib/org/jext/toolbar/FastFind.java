/*
 * FastFind.java - Easy accessible finder text field
 * Copyright (C) 2000 Romain Guy
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

package org.jext.toolbar;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.jext.*;
import org.jext.search.Search;

public class FastFind extends JTextField implements ActionListener, KeyListener
{
  private JextFrame parent;

  public FastFind(JextFrame parent)
  {
    super();
    this.parent = parent;
    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    addActionListener(this);
    FontMetrics fm = getFontMetrics(getFont());
    Dimension dim = new Dimension(fm.charWidth('m') * 10, getPreferredSize().height);
    setMinimumSize(dim);
    setPreferredSize(dim);
    setMaximumSize(new Dimension(fm.charWidth('m') * 80, getPreferredSize().height));
    addKeyListener(this);
  }

  public void keyPressed(KeyEvent evt) { }
  public void keyTyped(KeyEvent evt) { }

  public void keyReleased(KeyEvent evt)
  {
    if (Jext.getBooleanProperty("find.incremental"))
    {
      JextTextArea textArea = parent.getTextArea();
      textArea.setCaretPosition(textArea.getSelectionStart());
      find(textArea, false);
      requestFocus();
    }
  }

  public void actionPerformed(ActionEvent evt)
  {
    if (evt.getSource() == this)
    {
      JextTextArea textArea = parent.getTextArea();
      textArea.requestFocus();
      find(textArea, true);
      //setText(null);
    }
  }

  private void find(JextTextArea textArea, boolean showError)
  {
    Search.setFindPattern(getText());

    try
    {
      if (!Search.find(textArea, textArea.getCaretPosition()) && showError)
      {
        String[] args = { textArea.getName() };
        int response = JOptionPane.showConfirmDialog(null,
                                                     Jext.getProperty("find.matchnotfound", args),
                                                     Jext.getProperty("find.title"),
                                                     JOptionPane.YES_NO_OPTION,
                                                     JOptionPane.QUESTION_MESSAGE);
        switch (response)
        {
          case 0:
            textArea.setCaretPosition(0);
            find(textArea, false);
            break;
        }
      }
    } catch (Exception e) { }
  }


}

// End of FastFind.java