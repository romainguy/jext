/*
 * 13:39:57 02/02/00
 *
 * Plugin.java - All plugins MUST implement this
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

import java.util.Vector;
import org.jext.options.OptionsDialog;

/**
 * An interface which defines the basical behavior
 * of a Jext plugin. A plugin MUST implement this.
 */

public interface Plugin
{
  /**
   * Called by parent (instance of Jext) to requires menu items
   * specific to the plugin. Menu items have to be added into the
   * Vector pluginsMenuItems and submenus have to be added into the
   * Vector pluginsMenu.
   */

  public void createMenuItems(JextFrame parent, Vector pluginsMenus, Vector pluginsMenuItems) throws Throwable;

  /**
   * Called by Jext when user request plugins options. Plugin has to
   * add an AbstractOptionPane to the AbstractOptionsDialog parent.
   */

  public void createOptionPanes(OptionsDialog parent) throws Throwable;

  /**
   * Called by Jext on startup to start plugin.
   */

  public void start() throws Throwable;

  /**
   * Called by Jext on close to stop plugin activities.
   * This is called, in the background mode, only when the JVM exits completely, differently from the
   * {@link org.jext.event.JextEvent#KILLING_JEXT} event.
   * See its docs for a discussion of differencies.
   */

  public void stop() throws Throwable;
}

// End of Plugin.java
