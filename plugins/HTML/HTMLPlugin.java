/*
 * 11/04/2001 - 18:35:37
 *
 * HTMLPlugin.java - HTML Plugin
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
import org.jext.gui.*;
import org.jext.options.*;

public class HTMLPlugin implements Plugin, RegisterablePlugin
{
  public void createMenuItems(JextFrame parent, Vector menus, Vector menuItems)
  {
    //menus.addElement(GUIUtilities.loadMenu("HTML_menu"));
    parent.getJextMenuBar().addMenu(GUIUtilities.loadMenu("HTML_menu"), "Edit");
  }

  public void register(JextFrame parent)
  {
    if ("on".equals(Jext.getProperty("html.completion.activateTool")))
      parent.addJextListener(new TagsCompletion());
  }

  public void createOptionPanes(OptionsDialog parent)
  {
    OptionGroup htmlGroup = new OptionGroup("html");
    htmlGroup.addOptionPane(new TagsCompletionOptionPane());
    htmlGroup.addOptionPane(new IndenterOptionPane());
    parent.addOptionGroup(htmlGroup);
  }

  public void start()
  {
    Jext.addAction(new NextTag());
    Jext.addAction(new PreviousTag());
    Jext.addAction(new ToAccents());
    Jext.addAction(new ToEntities());
    Jext.addAction(new HTMLIndenter());

    int maxWidth;
    try
    {
      maxWidth = Integer.parseInt(Jext.getProperty("html.indenter.maxLineWidth"));
    } catch (NumberFormatException nfe) {
      maxWidth = 80;
      Jext.setProperty("html.indenter.maxLineWidth", "80");
    }

    if (maxWidth <= 0)
    {
      maxWidth = 80;
      Jext.setProperty("html.indenter.maxLineWidth", "80");
    }

    HTMLIndenter.MAX_LINE_WIDTH = maxWidth;
  }

  public void stop() { }
}

// End of HTMLPlugin.java
