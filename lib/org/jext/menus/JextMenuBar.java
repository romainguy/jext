/*
 * 11/25/2000 - 18:13:12
 *
 * JextMenuBar.java - Extended JMenuBar
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

package org.jext.menus;

import java.util.Hashtable;

import java.awt.Container;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.MenuElement;

import org.jext.Jext;
import org.jext.JextFrame;
import org.jext.gui.JextMenu;
import org.jext.gui.JextMenuSeparator;

/**
 * The menu bar of Jext windows. This menu bar allows
 * to add items and menus according to a position indicated
 * by a menu ID. Indeed, this class allows to register menus
 * with an ID.<br>Using IDs allows, for instance, to add
 * plugins submenus in other menus than in Plugins.
 * (see Java and HTML plugins to see how this works).
 * @author Romain Guy
 */

public class JextMenuBar extends JMenuBar
{
  private Hashtable menus = new Hashtable();
  private int fileMenusAdded, fileItemsAdded;
  private int editMenusAdded, editItemsAdded;
  
  /**
   * Creates a new menu bar.
   */

  public JextMenuBar()
  {
    super();
  }

  /**
   * Adds a menu in the menu bar and register it with
   * an identifier which can be used later to add items
   * or submenus in this menu.
   * @param menu The menu to be added
   * @param ID The identification string for this menu
   */

  public void addIdentifiedMenu(JMenu menu, String ID)
  {
    if (menus.containsKey(ID))
      System.err.println("JextMenuBar: There is already a menu with ID '" + ID + "'!");

    menus.put(ID, menu);
    add(menu);
    JextFrame frame = getJextFrame();
    if (frame != null)
      frame.itemAdded(menu);
  }

  /**
   * Adds a submenu in a menu designed by an ID string.
   * @param item The menu to be added
   * @param ID The ID string of the menu in which item has to be added
   */

  public void addMenu(JMenu item, String ID)
  {
    int pos = -1;
    JMenu _menu = (JMenu) menus.get(ID);
    if (_menu == null)
      return;

    if (ID.equals("Edit"))
    {
      pos = 13 + editMenusAdded;
      editMenusAdded++;
    } else if (ID.equals("File")) {
      pos = 22 + fileMenusAdded;
      fileMenusAdded++;
    }

    if (pos == -1)
    {
      if (!(_menu.getMenuComponent(_menu.getItemCount() - 2) instanceof JSeparator))
      {
        if (Jext.getFlatMenus())
          _menu.getPopupMenu().add(new JextMenuSeparator());
        else
          _menu.getPopupMenu().addSeparator();
      }
      _menu.add(item);
    } else
      _menu.insert(item, pos);

    JextFrame frame = getJextFrame();
    if (frame != null)
      frame.itemAdded(item);
  }

  /**
   * Adds an item in a menu designed by an ID string.
   * @param item The menu item to be added
   * @param ID The ID string of the menu in which item has to be added
   */

  public void addMenuItem(JMenuItem item, String ID)
  {
    int pos = -1;
    JMenu _menu = (JMenu) menus.get(ID);
    if (_menu == null)
      return;
    if (ID.equals("Edit"))
    {
      pos = 16 + editMenusAdded + editItemsAdded;
      editItemsAdded++;
    } else if (ID.equals("File")) {
      pos = 22 + fileMenusAdded + fileItemsAdded;
      fileItemsAdded++;
    }

    if (pos == -1)
    {
      if (!(_menu.getMenuComponent(_menu.getItemCount() - 2) instanceof JSeparator))
      {
        if (Jext.getFlatMenus())
          _menu.getPopupMenu().add(new JextMenuSeparator());
        else
          _menu.getPopupMenu().addSeparator();
      }
      _menu.add(item);
    } else {
      if (fileItemsAdded == 1)
      {
        if (Jext.getFlatMenus())
          _menu.getPopupMenu().insert(new JextMenuSeparator(), pos);
        else
          _menu.getPopupMenu().insert(new JSeparator(), pos);
      }
      _menu.insert(item, pos);
    }

    JextFrame frame = getJextFrame();
    if (frame != null)
      frame.itemAdded(item);
  }

  /**
   * Restores the menu bar.
   */

  public void reset()
  {
    fileMenusAdded = 0;
    fileItemsAdded = 0;
    editMenusAdded = 0;
    editItemsAdded = 0;
  }

  /**
   * Get the parent JextFrame
   * @return org.jext.JextFrame
   */

  private JextFrame getJextFrame()
  {
    Container parent;
    parent = getParent();          // JLayeredPane
    if (parent != null)
      parent = parent.getParent(); // JRootPane
    if (parent != null)
      parent = parent.getParent(); // JextFrame
    return (JextFrame) parent;
  }
  


}

// End of JextMenuBar