/*
Copyright (c) 2000 Scott Wyatt (scwyatt@fedex.com)

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import sun.awt.*;

import org.jext.Jext;

public class JFrameEx
{
  private Component comp = null;

  public void setComponent(Component comp)
  {
    this.comp = comp;
  }

  // This is undocumented, but works on JDK1.1.8, JDK1.2.2 and JDK1.3
  public int getHWND()
  {
    DrawingSurfaceInfo drawingSurfaceInfo;
    Win32DrawingSurface win32DrawingSurface;
    int hwnd = 0;

    // Get the drawing surface
    drawingSurfaceInfo = ((DrawingSurface)(comp.getPeer())).getDrawingSurfaceInfo();

    if (null != drawingSurfaceInfo)
    {
      drawingSurfaceInfo.lock();
      // Get the Win32 specific information
      win32DrawingSurface = (Win32DrawingSurface) drawingSurfaceInfo.getSurface();
      hwnd = win32DrawingSurface.getHWnd();
      drawingSurfaceInfo.unlock();
    }
    return hwnd;
  }

  // native entry point for subclassing the JFrame window
  public native void setHook(int hwnd);

  // native entry point for removing the hook.
  public native void resetHook(int hwnd);

  // this is the function which serves as a call back when
  // a mouse wheel movement is detected.
  public void notifyMouseWheel(short fwKeys, short zDelta, long xPos, long yPos)
  {

    // Convert screen coordinates to component specific offsets.
    Point p = new Point((int) xPos, (int) yPos);
    SwingUtilities.convertPointFromScreen(p, comp);

    // Find the embedded Swing component which should receive the scroll messages
    Component c = SwingUtilities.getDeepestComponentAt(comp, p.x, p.y);
    try
    {
      Container cont = c.getParent();
      do
      {
        if (cont != null)
        {
          for (int i = 0; i < cont.getComponentCount(); ++i)
          {
            if ((cont.getComponent(i)) instanceof JScrollBar)
            {
              JScrollBar scrollBar = (JScrollBar)(cont.getComponent(i));
              if (scrollBar.getOrientation() == JScrollBar.VERTICAL)
              {
                // This prevents flashing on JScrollPane viewports
                if (cont instanceof JScrollPane)
                {
                  JViewport viewPort = ((JScrollPane) cont).getViewport();
                  viewPort.setBackingStoreEnabled("on".equals(Jext.getProperty("wheelmouse.imgenabled")));
                }

                // Get the current value and set the new value depending on
                // the direction of the mouse wheel.

                int oldLineIncr = scrollBar.getUnitIncrement();
                int unitIncr = scrollBar.getUnitIncrement((zDelta > 0) ? -1 : 1);

                if (Jext.getProperty("wheelmouse.lineenabled") != null &&
                        Jext.getProperty("wheelmouse.lineenabled").equals("on") &&
                        Jext.getProperty("wheelmouse.line") != null)
                {
                  int newLineIncr = Integer.valueOf(Jext.getProperty("wheelmouse.line")).intValue();
                  // Divide down to per line increment
                  unitIncr = (unitIncr / oldLineIncr) * newLineIncr;
                }

                int nValue = scrollBar.getValue();
                nValue = nValue - ((zDelta > 0) ? unitIncr : -unitIncr);

                scrollBar.setValue(nValue);
                return;
              }
            }
          }
        }
      }
      while ((cont = cont.getParent()) != null)
        ;
    }
    catch (Exception e)
    {
      e.printStackTrace(System.out);
    }
  }
}

