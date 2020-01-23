/*
 * 19:40:46 31/01/00
 *
 * AutoSave.java - Notifies text area to save its content
 * Copyright (C) 1999 Romain Guy
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

package org.jext.misc;

import org.jext.*;
import org.jext.JextTextArea;

public class AutoSave extends Thread
{
  private JextFrame parent;
  private static int interval;
  
  /**
   * Starts the auto save thread.
   */

  public AutoSave(JextFrame parent)
  {
    super("----thread: autosave: jext----");
    this.parent = parent;
    setDaemon(true);
    start();
  }

  /**
   * Returns the intervale, in seconds, between each save.
   */

  public static int getInterval()
  {
    return interval;
  }

  /**
   * Sets the interval, in seconds, between each save.
   */

  public static void setInterval(int newInterval)
  {
    interval = newInterval;
  }

  public void run()
  {
    try
    {
      interval = Integer.parseInt(Jext.getProperty("editor.autoSaveDelay"));
    } catch(NumberFormatException nf) {
      interval = 60;
    }

    if (interval == 0)
      return;
    interval *= 1000;

    while(true)
    {
      try
      {
        Thread.sleep(interval);
      }	catch(InterruptedException i) {
        return;
      }

      JextTextArea[] areas = parent.getTextAreas();
      for (int i = 0; i < areas.length; i++)
        areas[i].autoSave();

      if (interrupted())
        return;
    }
  }
  

}

// End of AutoSave.java