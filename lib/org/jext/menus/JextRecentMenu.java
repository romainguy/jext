/*
 * 01/01/2001 - 22:10:27
 *
 * JextRecentMenu.java - A recent menu
 * Copyright (C) 2000 Romain Guy
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

package org.jext.menus;

import java.io.File;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.jext.Jext;
import org.jext.JextFrame;
import org.jext.MenuAction;
import org.jext.Utilities;

import org.jext.gui.EnhancedMenuItem;

public class JextRecentMenu
{
  private MenuAction opener;
  private JMenu recentMenu;
  private JextFrame parent;

  private int maxRecent;
  private String recent[] = new String[8];
  
  public JextRecentMenu(JextFrame parent, JMenu recentMenu)
  {
    this.parent = parent;
    this.recentMenu = recentMenu;
    opener = Jext.getAction("open_recent");
  }

  /**
   * Cleans recent menu.
   */

  public void removeRecent()
  {
    recentMenu.removeAll();

    for (int i = 0; i < maxRecent; i++)
    {
      String prop = Jext.getProperty("recent." + i);
      if (prop != null && !prop.equals(""))
        Jext.unsetProperty("recent." + i);
      recent[i] = null;
    }

    EnhancedMenuItem nothing = new EnhancedMenuItem(Jext.getProperty("editor.norecent"));
    nothing.setEnabled(false);
    recentMenu.add(nothing);
    Jext.recentChanged(parent);
  }

  /**
   * Creates the recent menu from an array of <code>String</code>
   * containing paths to the most recently opened files.
   * @param mnu The menu where to put last opened files
   */

  public void createRecent()
  {
    try
    {
      maxRecent = Integer.parseInt(Jext.getProperty("max.recent"));
    } catch (NumberFormatException nf) {
      maxRecent = 8;
    }

    String[] _tmp = recent;
    recent = new String[maxRecent];

    for (int i = 0; i < _tmp.length; i++)
    {
      if (i == recent.length)
        break;
      recent[i] = _tmp[i];
    }

    boolean empty = true;
    recentMenu.removeAll();

    for (int i = 0; i < maxRecent; i++)
    {
      recent[i] = Jext.getProperty("recent." + i);

      if (recent[i] != null && !recent[i].equals("") && (new File(recent[i])).exists())
      {
        EnhancedMenuItem recentItem = new EnhancedMenuItem(
                                      Utilities.getShortStringOf(recent[i], 70));
        recentItem.setActionCommand(recent[i]);
        recentItem.addActionListener(opener);
        recentMenu.add(recentItem);
        empty = false;
      } else
        Jext.unsetProperty("recent." + i);
    }

    if (empty)
    {
      EnhancedMenuItem nothing = new EnhancedMenuItem(Jext.getProperty("editor.norecent"));
      nothing.setEnabled(false);
      recentMenu.add(nothing);
    }
  }

  /**
   * Add an item to the recent menu.
   * @param file The path of the file to be added
   */

  public void saveRecent(String file)
  {
    if (file == null)
      return;

    for (int i = 0; i < maxRecent; i++)
    {
      if (file.equals(recent[i]))
        return;
    }

    for (int i = maxRecent - 1; i > 0; i--)
    {
      recent[i] = recent[i - 1];
      if (recent[i] != null && !recent[i].equals(""))
        Jext.setProperty("recent." + i, recent[i]);
    }

    recent[0] = file;
    Jext.setProperty("recent.0", file);
    createRecent();

    Jext.recentChanged(parent);
  }
  

}

// End of JextRecentMenu.java