/*
 * 09/28/2001 - 16:15:58
 *
 * WorkspaceSwitcher.java - Manages drop down list
 * Copyright (C) 2001 Romain Guy
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
 
import java.awt.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.jext.Jext;
import org.jext.JextFrame;
import org.jext.JextTabbedPane;
import org.jext.JextTextArea;

import org.jext.gui.EnhancedMenuItem;
import org.jext.gui.JextMenuSeparator;

public class WorkspaceSwitcher extends MouseAdapter
{
  private String mode;
  private JextFrame parent;
  private JPopupMenu dropDown;

  public WorkspaceSwitcher(JextFrame parent)
  {
    this.parent = parent;
    //buildDropDownList();
  }

  private void buildDropDownList()
  {
    dropDown = new JPopupMenu();

    EnhancedMenuItem title = new EnhancedMenuItem(Jext.getProperty("ws.sendTo.title"));
    title.setEnabled(false);
    dropDown.add(title);
    dropDown.add(new JextMenuSeparator());

    if (Jext.getFlatMenus())
      dropDown.setBorder(javax.swing.border.LineBorder.createBlackLineBorder());

    Switcher switcher = new Switcher();
    String current = parent.getWorkspaces().getName();
    String[] names = parent.getWorkspaces().getWorkspacesNames();

    for (int i = 0; i < names.length; i++)
    {
      if (!names[i].equals(current))
      {
        title = new EnhancedMenuItem(names[i]);
        title.setActionCommand(names[i]);
        title.addActionListener(switcher);
        dropDown.add(title);
      }
    }
  }

  public void mouseClicked(MouseEvent me)
  {
    buildDropDownList();

    JComponent c = (JComponent) me.getComponent();
    dropDown.show(c, 0, c.getHeight());
  }

  class Switcher implements ActionListener
  {
    public void actionPerformed(ActionEvent evt)
    {
      String sendToWorkspace = ((JMenuItem) evt.getSource()).getActionCommand();
      final JextTextArea textArea = parent.getTextArea();
      final Workspaces workspaces = parent.getWorkspaces();
      final JextTabbedPane textAreasPane = parent.getTabbedPane();

      int index = textAreasPane.indexOfComponent(textArea);
      if (index != -1)
      {
        workspaces.removeFile(textArea);
        textAreasPane.removeTabAt(index);
        if (parent.getTextAreas().length == 0)
          parent.createFile();
        workspaces.selectWorkspaceOfName(sendToWorkspace);
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            workspaces.addFile(textArea);
            textAreasPane.add(textArea);
            textAreasPane.setSelectedComponent(textArea);
          }
        });
      }
    }
  }
}

// End of WorkspaceSwitcher.java
