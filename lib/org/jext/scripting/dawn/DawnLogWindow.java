/*
 * 01/25/2003 - 19:31:25
 *
 * DawnLogWindow.java - Scripts log window
 * Copyright (C) 2003 Romain Guy
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

package org.jext.scripting.dawn;

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

import javax.swing.*;

import org.jext.*;
import org.jext.gui.*;
import org.jext.scripting.*;

public class DawnLogWindow extends AbstractLogWindow implements ActionListener
{
  private JextHighlightButton clear;
  private JTextField immediate;

  public static Dockable getInstance(JextFrame parent) {
    return buildInstance(new DawnLogWindow(parent), Jext.getProperty("dawn.window.title"), parent);
  }

  private DawnLogWindow(JextFrame parent)
  {
    super(parent, Jext.getProperty("dawn.window.title"));

    JPanel pane = new JPanel();
    pane.add(new JLabel(Jext.getProperty("dawn.window.immediate")));
    immediate = new JTextField(40);
    pane.add(immediate);

    clear = new JextHighlightButton(Jext.getProperty("dawn.window.clear"));
    pane.add(clear);
    getContentPane().add(BorderLayout.SOUTH, pane);

    clear.addActionListener(this);
    immediate.addActionListener(this);

    pack();
    Utilities.centerComponent(this);
  }

  public void actionPerformed(ActionEvent evt)
  {
    Object o = evt.getSource();
    if (o == clear)
      textArea.setText("");
    else if (o == immediate)
    {
      Run.execute(immediate.getText(), parent);
      immediate.setText("");
    }
  }
}

// End of DawnLogWindow.java
