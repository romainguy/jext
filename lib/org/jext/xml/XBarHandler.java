/*
 * 11/12/2000 - 00:52:23
 *
 * XBarHandler.java - Handles xml-toolbar files for Jext
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

package org.jext.xml;

import javax.swing.*;

import java.util.Stack;

import com.microstar.xml.*;

import org.jext.*;
import org.jext.gui.*;
import org.jext.toolbar.*;

public class XBarHandler extends HandlerBase
{
  // private members
  private JextFrame parent;
  private JextToolBar tbar;
  private Stack stateStack;
  private boolean enabled = true;
  private String lastShortcut, lastAction, lastPicture, lastLabel, lastTip;
  
  public XBarHandler(JextFrame parent) { this.parent = parent; }

  public void attribute(String aname, String value, boolean isSpecified)
  {
    if (aname.equalsIgnoreCase("ACTION"))
      lastAction = value;
    else if (aname.equalsIgnoreCase("MNEMONIC"))
      lastShortcut = value;
    else if (aname.equalsIgnoreCase("LABEL"))
      lastLabel = value;
    else if (aname.equalsIgnoreCase("PICTURE"))
      lastPicture = value;
    else if (aname.equalsIgnoreCase("TIP"))
      lastTip = value;
    else if (aname.equalsIgnoreCase("ENABLED"))
      enabled = value.equalsIgnoreCase("YES");
  }

  public void doctypeDecl(String name, String publicId, String systemId) throws Exception
  {
    if (!"XTOOLBAR".equalsIgnoreCase(name))
      throw new Exception("Not a valid XBar file !");
  }

  public void startElement(String name)
  {
    stateStack.push(name);
  }

  public void endElement(String name)
  {
    if (name == null) return;
    String lastStartTag = (String) stateStack.peek();
    if (name.equalsIgnoreCase(lastStartTag))
    {
      if ("BUTTON".equalsIgnoreCase(lastStartTag))
      {
        if (lastAction == null)
          return;

        JextButton btn = new JextButton();
        btn.setFocusPainted(false);

        if (lastLabel != null)
          btn.setText(lastLabel);

        if (lastPicture != null)
        {
          ImageIcon icon = new ImageIcon(Jext.class.getResource(
                           lastPicture.concat(Jext.getProperty("jext.look.icons")).concat(".gif")));
          if (icon != null)
            btn.setIcon(icon);
        }

        if (lastTip != null)
          btn.setToolTipText(lastTip);

        if (lastShortcut != null)
          btn.setMnemonic(lastShortcut.charAt(0));

        btn.setActionCommand(lastAction);
        MenuAction a = Jext.getAction(lastAction);

        if (a == null)
          btn.setEnabled(false);
        else
        {
          btn.addActionListener(a);
          btn.setEnabled(enabled);
        }

        tbar.addButton(btn);
        enabled = true;
        lastAction = lastLabel = lastPicture = lastTip = lastShortcut = null;
      } else if ("SEPARATOR".equalsIgnoreCase(lastStartTag))
        tbar.addButtonSeparator();
      Object o = stateStack.pop();
    } else
      System.err.println("XBar: Unclosed tag: " + stateStack.peek());
  }

  public void startDocument()
  {
    try
    {
      stateStack = new Stack();
      stateStack.push(null);
      tbar = new JextToolBar(parent);
    } catch (Exception e) { }
  }

  public void endDocument()
  {
    // tbar.setFloatable(false);
    parent.setJextToolBar(tbar);
    tbar = null;
  }
 
}

// End of XBarHandler.java