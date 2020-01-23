/*
 * 09/26/2001 - 20:10:28
 *
 * XMenuHandler.java - Handles xml-menus files for Jext
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

package org.jext.xml;

import javax.swing.*;

import java.util.Stack;

import com.microstar.xml.*;

import org.jext.*;
import org.jext.gui.*;
import org.jext.menus.*;

public class XMenuHandler extends HandlerBase
{
  // private members
  private JextFrame parent;
  private JextMenuBar mbar;
  private Stack stateStack;
  private String lastAttrValue;
  private JMenu lastMenu, currentMenu;
  private boolean enabled = true, labelSeparator = false, debug = false;
  private String lastAttr, lastName, lastAction, lastPicture, lastID, lastLabel;
  
  public XMenuHandler(JextFrame parent) { this.parent = parent; }

  public void attribute(String aname, String value, boolean isSpecified)
  {
    if (aname.equalsIgnoreCase("ACTION"))
      lastAction = value;
    else if (aname.equalsIgnoreCase("PICTURE"))
      lastPicture = value;
    else if (aname.equalsIgnoreCase("ID"))
      lastID = value;
    else if (aname.equalsIgnoreCase("ENABLED"))
      enabled = value.equalsIgnoreCase("YES");
    else if (aname.equalsIgnoreCase("TEXT"))
      lastLabel = value;
    else if (aname.equalsIgnoreCase("LABEL"))
    {
      lastAttr = aname;
      lastAttrValue = value;
    } else if (aname.equalsIgnoreCase("DEBUG"))
      debug = value.equalsIgnoreCase("YES");
  }

  public void doctypeDecl(String name, String publicId, String systemId) throws Exception
  {
    if (!"XMENUBAR".equalsIgnoreCase(name))
      throw new Exception("Not a valid XMenu file !");
  }

  public void startElement(String name)
  {
    stateStack.push(name);

    if (debug && !Jext.DEBUG)
      return;

    if ("LABEL".equalsIgnoreCase(lastAttr))
    {
      if ("MENU".equalsIgnoreCase(name))
      {
        currentMenu = GUIUtilities.loadMenu(lastAttrValue, true);
        lastMenu = currentMenu;
      } else if ("SUBMENU".equalsIgnoreCase(name))
        currentMenu = GUIUtilities.loadMenu(lastAttrValue, true);
      else if ("RECENTS".equalsIgnoreCase(name))
      {
        currentMenu = GUIUtilities.loadMenu(lastAttrValue, true);
        parent.setRecentMenu(new JextRecentMenu(parent, currentMenu));
      } else if ("PLUGINS".equalsIgnoreCase(name)) {
        currentMenu = GUIUtilities.loadMenu(lastAttrValue, true);
        parent.setPluginsMenu(currentMenu);
      } else if ("ITEM".equalsIgnoreCase(name))
        lastName = lastAttrValue;
    } else if ("TEMPLATES".equalsIgnoreCase(name))
      currentMenu = new TemplatesMenu();

    lastAttr = null;
    lastAttrValue = null;
  }

  public void endElement(String name)
  {
    if (name == null)
      return;

    if (debug && !Jext.DEBUG)
    {
      debug = false;
      stateStack.pop();
      return;
    }

    String lastStartTag = (String) stateStack.peek();
    if (name.equalsIgnoreCase(lastStartTag))
    {
      if ("ITEM".equalsIgnoreCase(lastStartTag))
      {
        JMenuItem mi = GUIUtilities.loadMenuItem(lastName, lastAction, lastPicture, enabled);
        if (mi != null)
          currentMenu.add(mi);
        enabled = true;
        lastPicture = lastName = lastAction = null;
      } else if ("SEPARATOR".equalsIgnoreCase(lastStartTag)) {
        if (labelSeparator && lastLabel != null && lastLabel.length() > 0)
        {
          if (Jext.getFlatMenus())
            currentMenu.getPopupMenu().add(new JextLabeledMenuSeparator(lastLabel));
          else
            currentMenu.getPopupMenu().addSeparator();
        } else {
          if (Jext.getFlatMenus())
            currentMenu.getPopupMenu().add(new JextMenuSeparator());
          else
            currentMenu.getPopupMenu().addSeparator();
        }
        lastLabel = null;
      } else if ("MENU".equalsIgnoreCase(lastStartTag) || "PLUGINS".equalsIgnoreCase(lastStartTag)) {
        if (currentMenu != null)
        {
          if (lastID == null)
            mbar.add(currentMenu);
          else
          {
            mbar.addIdentifiedMenu(currentMenu, lastID);
            lastID = null;
          }
          currentMenu = lastMenu = null;
        }
      } else if ("SUBMENU".equalsIgnoreCase(lastStartTag) ||
                 "RECENTS".equalsIgnoreCase(lastStartTag) ||
                 "TEMPLATES".equalsIgnoreCase(lastStartTag)) {
        if (currentMenu != null)
        {
          lastMenu.add(currentMenu);
          currentMenu = lastMenu;
        }
      }
      Object o = stateStack.pop();
    } else
      System.err.println("XMenu: Unclosed tag: " + stateStack.peek());
    debug = false;
  }

  public void startDocument()
  {
    try
    {
      labelSeparator = Jext.getBooleanProperty("labeled.separator");
      stateStack = new Stack();
      stateStack.push(null);
      mbar = new JextMenuBar();
    } catch (Exception e) { }
  }

  public void endDocument()
  {
    parent.setJMenuBar(mbar);
    mbar = null;
  }
}

// End of XMenuHandler.java