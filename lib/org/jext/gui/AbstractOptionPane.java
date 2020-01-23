/*
 * 14:12:13 19/02/00
 *
 * AbstractOptionPane.java - The Jext's option pane
 * Copyright (C) 1999 Romain Guy
 * Portions copyright (C) 1998-2000 Slava Pestov
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

package org.jext.gui;

import java.awt.*;
import javax.swing.*;
import org.jext.*;

/**
 * Abstract implementation of the <code>OptionPane</code> interface.
 */

public class AbstractOptionPane extends JPanel implements OptionPane
{
  protected int y = 0;
  protected GridBagLayout gridBag;

  // private fields
  private String name;

  public boolean isCacheable() {
    return false;
  }
  public void load() {}

  /**
   * Adds a labeled component in the pane. All the components
   * are placed on bottom of each other (vertically sorted).
   * @param label The label to be displayed next to the component
   * @param comp The component to be added
   */

  protected void addComponent(String label, Component comp)
  {
    GridBagConstraints cons = new GridBagConstraints();
    cons.gridy = y++;
    cons.gridheight = 1;
    cons.gridwidth = 3;
    cons.fill = GridBagConstraints.BOTH;
    cons.weightx = 1.0f;

    cons.gridx = 0;
    cons.anchor = GridBagConstraints.WEST;
    JLabel l = new JLabel(label, SwingConstants.LEFT);
    gridBag.setConstraints(l, cons);
    add(l);

    cons.gridx = 3;
    cons.gridwidth = 1;
    cons.anchor = GridBagConstraints.EAST;

    gridBag.setConstraints(comp, cons);
    add(comp);
  }

  /**
   * Does the same as <code>addComponent(String, Component)</code>
   * but don't add a label next to the component.
   * @param comp The component to be added
   */

  protected void addComponent(Component comp)
  {
    GridBagConstraints cons = new GridBagConstraints();
    cons.gridy = y++;
    cons.gridheight = 1;
    cons.gridwidth = cons.REMAINDER;
    cons.fill = GridBagConstraints.NONE;
    cons.anchor = GridBagConstraints.WEST;
    cons.weightx = 1.0f;

    gridBag.setConstraints(comp, cons);
    add(comp);
  }

  /**
   * Creates a new option pane.
   * @param name The name used by OptionDialog to display title
   */

  public AbstractOptionPane(String name)
  {
    this.name = name; //Jext.getProperty("options." + name + ".label");
    setLayout(gridBag = new GridBagLayout());
    setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
  }

  /**
   * Returns itself.
   */

  public Component getComponent()
  {
    return this;
  }

  /**
   * Overrides default getName() method. Needed by
   * tabbed panes to display a title on the parent tab.
   */

  public String getName()
  {
    return name;
  }

  /**
   * Empty implementation of save() method, inherited
   * from the OptionPane interface.
   */

  public void save() { }
  

}

// End of AbstractOptionPane.java