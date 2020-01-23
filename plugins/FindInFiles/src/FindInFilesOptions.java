/*
 * Copyright (C) 2002 James Kolean
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

import java.awt.*;
import javax.swing.*;

import org.jext.*;
import org.jext.gui.*;

public class FindInFilesOptions extends AbstractOptionPane {
  private JCheckBox useGlob;

  public  FindInFilesOptions() {
    super("find_in_files");

    JLabel n = new JLabel(Jext.getProperty("findinfiles.title"), SwingConstants.CENTER);
    n.setFont(new Font("dialog", Font.ITALIC | Font.BOLD, 14));
    addComponent(n);

    addComponent(useGlob = new JCheckBox(Jext.getProperty("findinfiles.useglob.label")));
    useGlob.setSelected("on".equals(Jext.getProperty(FindInFilesPlugin.USE_GLOB_PROP, "off")));
  }

  public void save() {
    Jext.setProperty(FindInFilesPlugin.USE_GLOB_PROP, useGlob.isSelected() ? "on" : "off");
  }

}
