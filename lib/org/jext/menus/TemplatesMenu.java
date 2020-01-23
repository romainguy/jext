/*
 * 11/18/2000 - 00:51:18
 *
 * TemplatesMenu.java - Templates menu
 * Copyright (C) 2000 Blake Winton, modifs by Romain Guy
 * romain.guy@jext.org
 * http://www.jext.org
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

package org.jext.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.util.Arrays;

import org.jext.Jext;
import org.jext.MenuAction;
import org.jext.Utilities;
import org.jext.actions.CreateTemplate;
import org.jext.gui.EnhancedMenuItem;
import org.jext.gui.JextMenu;
import org.jext.gui.JextMenuSeparator;

public class TemplatesMenu extends JextMenu
{
  CreateTemplate creater;
  
  public TemplatesMenu()
  {
    super(Jext.getProperty("templates.label"));
    creater = new CreateTemplate();
    processDirectory(this, Jext.getProperty("templates.directory",  
                                            Jext.JEXT_HOME + File.separator + "templates"));
  }

  public void processDirectory(JMenu menu, String file)
  {
    JMenuItem retval;
    File directory = new File(file);

    if (!directory.exists())
    {
      retval = new EnhancedMenuItem(Jext.getProperty("templates.none"));
      retval.setEnabled(false);
      menu.add(retval);
      return;
    }

    String fileName = directory.getName();
    if (directory.isDirectory())
    {
      //if (file.equals(Jext.getProperty("templates.directory")))
      //  fileName = Jext.getProperty("templates.label");

      if (file.equals(Jext.getProperty("templates.directory",
                                       Jext.JEXT_HOME + File.separator + "templates")))
      {
        retval = menu;
      } else
        retval = new JextMenu(fileName);
  
      String[] files = directory.list();
      if (files.length == 0)
      {
        retval = new EnhancedMenuItem(Jext.getProperty("templates.none"));
        retval.setEnabled(false);
        JextMenu _menu = new JextMenu(fileName);
        _menu.add(retval);
        menu.add(_menu);
        return;
      }

      Arrays.sort(files);
      for (int i = 0; i < files.length; i++)
        processDirectory((JMenu) retval, file + File.separator + files[i]);

    } else {
      int last = fileName.lastIndexOf( '.' );
      if( last != -1 )
        fileName = fileName.substring( 0, last );

      retval = new EnhancedMenuItem(fileName);
      retval.setActionCommand(file);
      retval.addActionListener(creater);
    }

    menu.add(retval);
    return;
  }
  

}

// End of TemplatesMenu.java