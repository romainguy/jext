/*
 * 17:08:04 05/09/00
 *
 * XInsertHandler.java - Handles xml-insert files for Jext
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

import java.util.Stack;

import org.jext.xinsert.*;
import com.microstar.xml.*;

public class XInsertHandler extends HandlerBase
{
  // private members
  private XTree tree;
  private Stack stateStack;
  private String lastAttr, lastName, lastValue, lastAttrValue, lastModes, type;
  
  public XInsertHandler(XTree tree) { this.tree = tree; }

  public void attribute(String aname, String value, boolean isSpecified)
  {
    if (aname.equalsIgnoreCase("TYPE") && isSpecified)
      type = value;
    else if (aname.equalsIgnoreCase("MODES"))
      lastModes = value;
    else if (aname.equalsIgnoreCase("NAME"))
    {
      lastAttr = aname;
      lastAttrValue = value;
    }
  }

  public void doctypeDecl(String name, String publicId, String systemId) throws Exception
  {
    if (!"XINSERT".equalsIgnoreCase(name))
      throw new Exception("Not a valid XInsert file !");
  }

  public void charData(char[] c, int off, int len)
  {
    if ("ITEM".equalsIgnoreCase(((String) stateStack.peek())))
      lastValue = new String(c, off, len);
  }

  public void startElement(String name)
  {
    stateStack.push(name);
    if ("NAME".equalsIgnoreCase(lastAttr))
    {
      if ("MENU".equalsIgnoreCase(name))
      {
        tree.addMenu(lastAttrValue, lastModes);
        lastModes = null;
      }
    }
  }

  public void endElement(String name)
  {
    if (name == null)
      return;
    String lastStartTag = (String) stateStack.peek();

    if (name.equalsIgnoreCase(lastStartTag))
    {
      if (lastStartTag.equalsIgnoreCase("MENU"))
        tree.closeMenu();
      else if (lastStartTag.equalsIgnoreCase("ITEM"))
      {
        int _type = XTreeItem.PLAIN;
        if (type != null)
        {
          if (type.equalsIgnoreCase("MIXED"))
            _type = XTreeItem.MIXED;
          else if (type.equalsIgnoreCase("SCRIPT"))
            _type = XTreeItem.SCRIPT;
          else
            _type = XTreeItem.PLAIN;
        }
        tree.addInsert(lastAttrValue, lastValue, _type);
        type = null;
      }

      stateStack.pop();
    } else
      System.err.println("XInsert: Unclosed tag: " + stateStack.peek());

    lastAttr = null;
    lastAttrValue = null;
  }

  public void startDocument()
  {
    try
    {
      stateStack = new Stack();
      stateStack.push(null);
    } catch (Exception e) { }
  }
}
// End of XInsertHandler.java