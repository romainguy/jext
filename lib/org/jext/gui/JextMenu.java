/*
 * 15:47:20 26/08/00
 *
 * JextMenu.java - A flat bordered menu
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

package org.jext.gui;

import java.awt.Component;

import javax.swing.JMenu;

import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.jext.Jext;

public class JextMenu extends JMenu
{
  private Component[] menuComponents;
  
  public JextMenu()
  {
    super();
    setBorders();
  }

  public JextMenu(String label)
  {
    super(label);
    setBorders();
  }

  private void setBorders()
  {
    if (Jext.getFlatMenus())
    {
      setBorder(new EmptyBorder(2, 2, 2, 2));
      getPopupMenu().setBorder(LineBorder.createBlackLineBorder());
    }
  }

  /**
   * Stores the menu before starting plugins.
   */

  public void freeze() 
  {
    menuComponents = getMenuComponents();
  }

  /**
   * Restores the menu.
   */

  public void reset()
  {
    if (menuComponents == null) return;
    removeAll();
    for (int i = 0; i < menuComponents.length; i++)
      add(menuComponents[i]);
  }
  

}

// End of JextMenu.java