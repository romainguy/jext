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

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import java.util.Vector;
import org.jext.Plugin;
import org.jext.JextFrame;
import org.jext.options.OptionsDialog;
import org.jext.Utilities;

public class WheelMousePlugin implements Plugin
{

  static
  {
    try
    {
      System.loadLibrary("MouseWheel");
      // Load the shared library for the native functions
    }
    catch (UnsatisfiedLinkError ule)
    {
      throw ule;
    }
  }

  public void start()
  {
    startListeningForOtherWindows();
  }

  public void createOptionPanes(OptionsDialog optionsDialog)
  {
    optionsDialog.addOptionPane(new WheelMouseOptionPane());
  }

  public void addWheelMouseListener(Component comp)
  {
    JFrameEx jframeex = new JFrameEx();
    jframeex.setComponent(comp);
    jframeex.setHook(jframeex.getHWND());
  }

  public void removeWheelMouseListener(Component comp)
  {
    JFrameEx jframeex = new JFrameEx();
    jframeex.setComponent(comp);
    jframeex.resetHook(jframeex.getHWND());
  }

  public void startListeningForOtherWindows()
  {
    Toolkit.getDefaultToolkit().addAWTEventListener(new WindowAWTEvent(),
            AWTEvent.WINDOW_EVENT_MASK);
  }

  public class WindowAWTEvent implements AWTEventListener
  {
    public void eventDispatched(AWTEvent awtevt)
    {
      if (awtevt instanceof WindowEvent)
      {
        WindowEvent wevt = (WindowEvent) awtevt;
        if (wevt.getID() == WindowEvent.WINDOW_OPENED)
          addWheelMouseListener(wevt.getComponent());
        if (wevt.getID() == WindowEvent.WINDOW_CLOSING)
          removeWheelMouseListener(wevt.getComponent());
      }
    }
  }

  public void createMenuItems(JextFrame parent, Vector pluginsMenus,
          Vector pluginsMenuItems)
  {
  }

  public void stop()
  {
  }
}
