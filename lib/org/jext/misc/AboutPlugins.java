/*
 * 11/12/2000 - 00:59:47
 *
 * AboutPlugins.java - Displays infos about plugins
 * Copyright (C) 2000 Romain Guy
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

package org.jext.misc;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import org.jext.*;
import org.jext.gui.*;

public class AboutPlugins extends JDialog implements ActionListener
{
  // private members
  private JextHighlightButton ok;
  private InstalledPlugin[] plugs;

  private JextCheckBox createBox(String name) {
    String[] args = new String[3];

    args[0] = Jext.getProperty("plugin." + name + ".name");
    args[1] = Jext.getProperty("plugin." + name + ".version");
    args[2] = Jext.getProperty("plugin." + name + ".author");

    JextCheckBox box = new JextCheckBox(Jext.getProperty("about.plugins.sentence", args));
    box.setSelected(JARClassLoader.isEnabled(name));
    return box;
  }

  public AboutPlugins(JextFrame parent)
  {
    super(parent, Jext.getProperty("about.plugins.title"), false);
    getContentPane().setLayout(new BorderLayout());

    Box boxer = Box.createVerticalBox();
    JComponent box;

    ArrayList _plugins = JARClassLoader.pluginsNames;
    plugs = new InstalledPlugin[_plugins.size()];

    if (plugs.length != 0)
    {
      String name;
  
      for (int i = 0; i < plugs.length; i++)
      {
        name = (String) _plugins.get(i);
	int dot = name.lastIndexOf('/');
	name = name.substring((dot == -1 ? 0 : dot + 1), name.indexOf(".class"));

	JextCheckBox _box = createBox(name);
	boxer.add(_box);
	plugs[i] = new InstalledPlugin(_box, name);
      }
  
      box = plugs[0].getCheckBox();
    } else {
      box = new JLabel(' ' + Jext.getProperty("no.plugins"));
      box.setForeground(Color.black);
      boxer.add(box);
    }

    getContentPane().add(new JLabel(Jext.getProperty("about.plugins.header")), BorderLayout.NORTH);
    JScrollPane scrollPane = new JScrollPane(boxer);
    FontMetrics fm = getFontMetrics(box.getFont());
    scrollPane.getViewport().setPreferredSize(new Dimension(30 * fm.charWidth('m'),
                                                            8 * box.getPreferredSize().height));
    getContentPane().add(scrollPane, BorderLayout.CENTER);

    ok = new JextHighlightButton(Jext.getProperty("general.ok.button"));
    ok.addActionListener(this);
    getRootPane().setDefaultButton(ok);

    JPanel pane = new JPanel();
    pane.add(ok);
    getContentPane().add(BorderLayout.SOUTH, pane);

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    addKeyListener(new AbstractDisposer(this));

    pack();
    Utilities.centerComponentChild(parent, this);
    setVisible(true);
  }

  public void actionPerformed(ActionEvent evt)
  {
    if (evt.getSource() == ok)
    {
      for (int i = 0; i < plugs.length; i++)
        plugs[i].save();
      dispose();
    }
  }

  class InstalledPlugin
  {
    // private fields
    private String name;
    private JCheckBox box;
    
    InstalledPlugin(JCheckBox box, String name)
    {
      this.box = box;
      this.name = name;
    }

    public JCheckBox getCheckBox()
    {
      return box;
    }

    public void save()
    {
      //Jext.setProperty("plugin." + name + ".disabled", box.isSelected() ? "no" : "yes");
      JARClassLoader.setEnabled(name, box.isSelected());
    }
  }
}

// End of AboutPlugins.java