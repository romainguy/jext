/*
 * 12:40:59 29/10/00
 *
 * LangOptions.java - Languages options panel
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

public class LangOptions extends AbstractOptionPane
{
  private JList langList;
  
  public LangOptions()
  {
    super("lang");
    setLayout(new GridLayout(1, 1));

    JPanel pane = new JPanel(new BorderLayout());

    DefaultListModel model = new DefaultListModel();
    model.addElement("English");

    String[] packs = Utilities.getWildCardMatches(Jext.JEXT_HOME + File.separator + "lang", "*_pack.jar", true);
    if (packs != null)
    {
      for (int i = 0; i < packs.length; i++)
        model.addElement(packs[i].substring(0, packs[i].indexOf("_pack.jar")));
    }

    langList = new JList(model);
    //langList.setVisibleRowCount(10);
    langList.setCellRenderer(new ModifiedCellRenderer());

    pane.add(BorderLayout.NORTH, new JLabel(Jext.getProperty("options.languages.title")));
    pane.add(BorderLayout.CENTER, new JScrollPane(langList));
    add(pane);
    load();
  }

  public void load()
  {
    langList.setSelectedValue(Jext.getLanguage(), true);
  }

  public void save()
  {
    if (Jext.getLanguage().equals(langList.getSelectedValue()))
      return;

    try
    {
      File lang = new File(Jext.SETTINGS_DIRECTORY + ".lang");
      BufferedWriter writer = new BufferedWriter(new FileWriter(lang));
      String language = langList.getSelectedValue().toString();
      writer.write(language, 0, language.length());
      writer.flush();
      writer.close();
      Jext.setLanguage(language);
    } catch (Exception ioe) { }
  }
  

}

// End of LangOptions.java