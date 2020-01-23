/*
 * 07/23/2001 - 19:47:52
 *
 * ProjectPanel.java - A simple project manager
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

package org.jext.misc;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import org.jext.*;
import org.jext.gui.*;

public class ProjectPanel extends JPanel
{
  private JPanel panelCard;
  private CardLayout carder;
  private JextToggleButton workspaces, bookmarks;

  public ProjectPanel(JextFrame parent)
  {
    super(new BorderLayout());

    // adds components
    panelCard = new JPanel(carder = new CardLayout());
    panelCard.add(parent.getWorkspaces(), "workspaces");
    panelCard.add(parent.getVirtualFolders(), "virtual folders");
    //panelCard.setBorder(LineBorder.createBlackLineBorder());//new BevelBorder(BevelBorder.LOWERED));

    // buttons
    JToolBar buttonsPanel = new JToolBar();
    buttonsPanel.setFloatable(false);
    //JPanel buttonsPanel = new JPanel();
    workspaces = new JextToggleButton(Jext.getProperty("ws.tab"));
    bookmarks = new JextToggleButton(Jext.getProperty("vf.tab"));

    // group
    ButtonGroup group = new ButtonGroup();
    group.add(workspaces);
    group.add(bookmarks);
    workspaces.setSelected(true);

    ActionListener toggler = new ToggleHandler();
    workspaces.addActionListener(toggler);
    bookmarks.addActionListener(toggler);

    buttonsPanel.add(workspaces);
    buttonsPanel.add(bookmarks);

    // show and set
    carder.first(panelCard);
    add(BorderLayout.NORTH, buttonsPanel);
    add(BorderLayout.CENTER, panelCard);
  }

  class ToggleHandler implements ActionListener
  {
    public void actionPerformed(ActionEvent evt)
    {
      Object o = evt.getSource();

      if (o == workspaces)
        carder.first(panelCard);
      else if (o == bookmarks)
        carder.last(panelCard);
    }
  }
}

// End of ProjectPanel.java
