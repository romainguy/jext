/*
 * 22:21:17 17/12/99
 *
 * XPopupHandler.java - Handles xml-menus files for Jext
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

package org.jext.xml;

import javax.swing.*;

import java.util.Stack;

import com.microstar.xml.*;

import org.jext.*;
import org.jext.gui.*;

public class XPopupHandler extends HandlerBase
{
  // private members
  private JPopupMenu popup;
  private Stack stateStack;
  private JMenu currentMenu;
  private String lastAttrValue;
  private boolean enabled = true;
  private String lastAttr, lastName, lastAction, lastPicture;
  
  public XPopupHandler() { }

  public void attribute(String aname, String value, boolean isSpecified)
  {
    if (aname.equalsIgnoreCase("ACTION"))
      lastAction = value;
    else if (aname.equalsIgnoreCase("PICTURE"))
      lastPicture = value;
    else if (aname.equalsIgnoreCase("ENABLED"))
      enabled = value.equalsIgnoreCase("YES");
    else if (aname.equalsIgnoreCase("LABEL"))
    {
      lastAttr = aname;
      lastAttrValue = value;
    }
  }

  public void doctypeDecl(String name, String publicId, String systemId) throws Exception
  {
    if (!"XPOPUP".equalsIgnoreCase(name))
      throw new Exception("Not a valid XPopup file !");
  }

  public void startElement(String name)
  {
    stateStack.push(name);
    if ("LABEL".equalsIgnoreCase(lastAttr))
    {
      if ("SUBMENU".equalsIgnoreCase(name))
        currentMenu = GUIUtilities.loadMenu(lastAttrValue, true);
      else if (name.toUpperCase().equals("ITEM"))
        lastName = lastAttrValue;
    }
    lastAttr = null;
    lastAttrValue = null;
  }

  public void endElement(String name)
  {
    if (name == null)
      return;

    String lastStartTag = (String) stateStack.peek();
    if (name.equalsIgnoreCase(lastStartTag))
    {
      if ("ITEM".equalsIgnoreCase(lastStartTag))
      {
        JMenuItem mi = GUIUtilities.loadMenuItem(lastName, lastAction, lastPicture,
                                                 enabled, false);
        if (mi != null)
        {
          if (currentMenu != null)
            currentMenu.add(mi);
          else
            popup.add(mi);
        }
        enabled = true;
        lastPicture = lastName = lastAction = null;
      } else if ("SEPARATOR".equalsIgnoreCase(lastStartTag)) {
        if (currentMenu != null)
        {
          if (Jext.getFlatMenus())
            currentMenu.getPopupMenu().add(new JextMenuSeparator());
          else
            currentMenu.getPopupMenu().addSeparator();
        } else {
          if (Jext.getFlatMenus())
            popup.add(new JextMenuSeparator());
          else
            popup.addSeparator();
        }
      } else if ("SUBMENU".equalsIgnoreCase(lastStartTag)) {
        if (currentMenu != null)
        {
          popup.add(currentMenu);
          currentMenu = null;
        }
      }
      Object o = stateStack.pop();
    } else
      System.err.println("XPopup: Unclosed tag: " + stateStack.peek());
  }

  public void startDocument()
  {
    try
    {
      stateStack = new Stack();
      stateStack.push(null);
      popup = new JPopupMenu();
    } catch (Exception e) { }
  }

  public void endDocument()
  {
  }

  public JPopupMenu getPopupMenu()
  {
    return popup;
  }

}

// End of XPopupHandler.java