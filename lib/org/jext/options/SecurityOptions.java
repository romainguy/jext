/*
 * 16:24:41 31/10/00
 *
 * SecurityOptions.java - Security options panel
 * Copyright (C) 2000 Romain Guy
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

package org.jext.options;

import java.awt.*;
import javax.swing.*;

import java.io.*;

import org.jext.*;
import org.jext.gui.*;

public class SecurityOptions extends AbstractOptionPane
{
  private JextCheckBox enableServer;
  
  public SecurityOptions()
  {
    super("security");
    addComponent(enableServer = new JextCheckBox(Jext.getProperty("options.security.enableServer")));
    load();
  }

  public void load()
  {
    enableServer.setSelected(Jext.isServerEnabled());
  }

  public void save()
  {
    if (enableServer.isSelected() == Jext.isServerEnabled())
      return;

    try
    {
      File sec = new File(Jext.SETTINGS_DIRECTORY + ".security");
      //Writer writer = new BufferedWriter(new FileWriter(sec));
      Writer writer = new FileWriter(sec);
      String val = Boolean.toString(enableServer.isSelected());
      writer.write(val);
      //writer.flush();
      writer.close();
      Jext.setServerEnabled(enableServer.isSelected());
    } catch (Exception ioe) { }
  }
  

}

// End of SecurityOptions.java