/*
 * 00:43:48 04/11/99
 *
 * MenuAction.java - Interface for menu items
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

package org.jext;

import java.util.EventObject;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JToolBar;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

/**
 * This class implements an <code>ActionListener</code> and
 * handles action events fired by <codeJMenuItem</code>. This
 * class also provides two new methods which can be used to
 * get the window which has fired the event and the associated
 * text area.
 */

public abstract class MenuAction implements ActionListener
{
  protected String name;

  /**
   * Creates a new menu action designed by its name.
   * This name is internally used by Jext to handles scripts,
   * correctly build menu bar and tool bar.
   * @param name Internal action name
   */

  public MenuAction(String name)
  {
    this.name = name;
  }

  /**
   * Returns the associated action name.
   */

  public String getName()
  {
    return name;
  }

  /**
   * This methods returns the selected text area in the window
   * which fired the event.
   * @param evt The source event
   */

  public static JextTextArea getTextArea(EventObject evt)
  {
    return getJextParent(evt).getTextArea();
  }

  /**
   * This methods returns the selected text area in the window
   * which fired the event, excluding the splitted one.
   * @param evt The source event
   */

  public static JextTextArea getNSTextArea(EventObject evt)
  {
    return getJextParent(evt).getNSTextArea();
  }

  /**
   * Returns the window which fired the event.
   * @param evt The source event
   */

  public static JextFrame getJextParent(EventObject evt)
  {
    if (evt != null)
    {
      Object o = evt.getSource();
      if (o instanceof Component)
      {
        Component c = (Component) o;
        for( ; ; )
        {
          if (c instanceof JextFrame)
            return (JextFrame) c;
          else if (c == null)
            break;
          if (c instanceof JPopupMenu)
            c = ((JPopupMenu) c).getInvoker();
          else if (c instanceof JToolBar)
            return (JextFrame) ((JComponent) c).getClientProperty("JEXT_INSTANCE");
          else
            c = c.getParent();
        }
      }
    }
    return null;
  }

  public static JextTextArea getTextArea(Component c)
  {
    return getJextParent(c).getTextArea();
  }

  public static JextTextArea getNSTextArea(Component c)
  {
    return getJextParent(c).getNSTextArea();
  }

  public static JextFrame getJextParent(Component comp)
  {
    for ( ; ; )
    {
      if (comp instanceof JextFrame)
        return (JextFrame) comp;
      else if (comp instanceof JPopupMenu)
        comp = ((JPopupMenu) comp).getInvoker();
      else if (comp != null)
        comp = comp.getParent();
      else
        break;
    }
    return null;
  }
}

// End of MenuAction.java
