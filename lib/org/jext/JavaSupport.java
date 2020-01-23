/*
 * 03/23/2002 - 00:31:45
 *
 * JavaSupport.java - Support for JDK 1.4
 * Copyright (C) 2002 Romain Guy
 * Portions copyright (C) 2002 Slava Pestov
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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class JavaSupport
{
  public static void initJavaSupport()
  {
    JFrame.setDefaultLookAndFeelDecorated(Jext.getBooleanProperty("decoratedFrames"));
    JDialog.setDefaultLookAndFeelDecorated(Jext.getBooleanProperty("decoratedFrames"));
    KeyboardFocusManager.setCurrentKeyboardFocusManager(new JextKeyboardFocusManager());
  }

  public static void setMouseWheel(final JextTextArea area)
  {
    area.addMouseWheelListener(new MouseWheelListener()
    {
      public void mouseWheelMoved(MouseWheelEvent e)
      {
        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL)
        {
          area.setFirstLine(area.getFirstLine() + e.getUnitsToScroll());
        }
      }
    });
  }

  static class JextKeyboardFocusManager extends DefaultKeyboardFocusManager
  {
    JextKeyboardFocusManager()
    {
      setDefaultFocusTraversalPolicy(new LayoutFocusTraversalPolicy());
    }

    public boolean postProcessKeyEvent(KeyEvent evt)
    {
      if (!evt.isConsumed())
      {
        Component comp = (Component) evt.getSource();
        if (!comp.isShowing())
          return true;

        for ( ; ; )
        {
          if (comp instanceof JextFrame)
          {
            ((JextFrame) comp).processKeyEvent(evt);
            return true;
          } else if (comp == null || comp instanceof Window || comp instanceof JextTextArea)
            break;
          else
            comp = comp.getParent();
        }
      }

      return super.postProcessKeyEvent(evt);
    }
  }
}

// End of JavaSupport.java
