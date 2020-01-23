/*
* 08/15/2001 - 14:37:26
*
* FindAllPlugin.java - Find all docked
* Copyright (C) 2001 Grant Stead
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
import java.awt.event.ActionEvent;
import org.jext.MenuAction;

public class FindAllPlugin implements Plugin
{
  FindAll findAll = null;
  FindAllAction action = null;  
  
  public void createMenuItems(JextFrame parent, Vector pluginsMenus, Vector pluginsMenuItems)
  {
    findAll = new FindAll(parent);
    parent.getVerticalTabbedPane().add(Jext.getProperty("FindAll.title"), findAll);
    action.setFindAll(findAll);
    
    pluginsMenuItems.add(GUIUtilities.loadMenuItem("FindAllAction"));
  }

  public void createOptionPanes(OptionsDialog parent) { }
  
  public void start()
  {
    action = new FindAllAction();
    Jext.addAction(action);
  }
  
  public void stop()
  {
    action = null;
    findAll.exit();
  }
}

// End of FindAllPlugin.java
