/*
 * 09/12/2001 - 23:42:54
 *
 * LoadingOptions.java - Loading options panel
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

package org.jext.options;

import java.awt.*;
import javax.swing.*;

import java.io.*;

import org.jext.*;
import org.jext.gui.*;

public class LoadingOptions extends AbstractOptionPane
{
  private JextCheckBox xtreeEnabled, consoleEnabled, loadClasses, keepInMemory;
  
  public LoadingOptions()
  {
    super("loading");

    addComponent(loadClasses = new JextCheckBox(Jext.getProperty("options.loadClasses.label")));
    addComponent(xtreeEnabled = new JextCheckBox(Jext.getProperty("options.xtreeEnabled.label")));
    addComponent(consoleEnabled = new JextCheckBox(Jext.getProperty("options.consoleEnabled.label")));
    addComponent(keepInMemory = new JextCheckBox(Jext.getProperty("options.autoBg.label", 
            "Keep in memory at exit to make startup quicker.")));
    load();
  }

  public void load()
  {
    loadClasses.setSelected(Jext.getBooleanProperty("load.classes"));
    xtreeEnabled.setSelected(Jext.getBooleanProperty("xtree.enabled"));
    consoleEnabled.setSelected(Jext.getBooleanProperty("console.enabled"));
    keepInMemory.setSelected(Jext.isDefaultKeepInMemory());
  }

  public void save()
  {
    Jext.setProperty("load.classes", loadClasses.isSelected() ? "on" : "off");
    Jext.setProperty("xtree.enabled", xtreeEnabled.isSelected() ? "on" : "off");
    Jext.setProperty("console.enabled", consoleEnabled.isSelected() ? "on" : "off");

    if (keepInMemory.isSelected() == Jext.isDefaultKeepInMemory())
      return;

    try
    {
      File bg = new File(Jext.SETTINGS_DIRECTORY + ".showBg");
      Writer writer = new FileWriter(bg);
      writer.write(Boolean.toString(keepInMemory.isSelected()));
      writer.close();
      Jext.setDefaultKeepInMemory(keepInMemory.isSelected());
    } catch (Exception ioe) { }
  }
  

}

// End of LoadingOptions.java