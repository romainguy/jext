/*
 * 05/08/2002 - 15:55:30
 *
 * AboutWindow.java - About
 * Copyright (C) 1999 Romain Guy
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

package org.jext.misc;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.Random;

import org.jext.*;
import org.jext.gui.*;

public class AboutWindow extends JDialog
{
  public AboutWindow(JextFrame parent)
  {
    super(parent, false);
    setTitle(Jext.getProperty("about.title"));

    getContentPane().setLayout(new BorderLayout());
    getContentPane().setLayout(new BorderLayout());
    getContentPane().setFont(new Font("Monospaced", 0, 14));

    getContentPane().add(BorderLayout.NORTH, new JLabel(Utilities.getIcon("images/splash" +
                                           (Math.abs(new Random().nextInt()) % 6) + ".gif", Jext.class)));

    JPanel pane =  new JPanel();
    pane.setLayout(new BorderLayout());
    pane.add(BorderLayout.NORTH,
             new JLabel("v" + Jext.RELEASE + " b" + Jext.BUILD,
             SwingConstants.CENTER));
    pane.add(BorderLayout.SOUTH, new JLabel("(C) 2004 Romain Guy -  www.jext.org",
             SwingConstants.CENTER));
    getContentPane().add(BorderLayout.CENTER, pane);

    JextHighlightButton ok = new JextHighlightButton(Jext.getProperty("general.ok.button"));
    ok.addActionListener(new AbstractAction()
    {
      public void actionPerformed(ActionEvent evt)
      {
        AboutWindow.this.dispose();
      }
    });
    getRootPane().setDefaultButton(ok);

    JPanel _pane = new JPanel();
    _pane.add(ok);
    getContentPane().add(BorderLayout.SOUTH, _pane);

    addKeyListener(new AbstractDisposer(this));

    pack();
    Utilities.centerComponent(this);
    setResizable(false);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setVisible(true);
  }
}

// End of AboutWindow.java

