/*
 * PyBrowsePlugin.java - Python Plugin
 * Copyright (C) 2001 Romain Guy
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

import java.util.Vector;
import org.jext.*;
import org.jext.options.*;

public class PyBrowsePlugin implements Plugin
{
  public void createMenuItems(JextFrame parent, Vector menus, Vector menuItems)
  {
    parent.getJextMenuBar().addMenu(GUIUtilities.loadMenu("Python_menu"), "Edit");
  }

  public void createOptionPanes(OptionsDialog parent) { }
  public void start() { }
  public void stop() { }
}

// End of PyBrowsePlugin.java
