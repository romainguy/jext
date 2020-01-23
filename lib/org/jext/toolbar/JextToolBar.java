/*
 * 19:13:06 08/11/00
 *
 * JextToolBar.java - Extended JMenuBar
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

package org.jext.toolbar;

import java.awt.Component;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;

//import javax.swing.UIManager;
//import javax.swing.border.EtchedBorder;
//import javax.swing.border.LineBorder;

import org.jext.Jext;
import org.jext.JextFrame;
import org.jext.gui.JextButton;
import org.jext.gui.JextSeparator;

/**
 * This toolbar allows plugins to append buttons after Jext
 * default ones. Indeed, depending the order the plugins are
 * loaded, added buttons might appear after another component
 * like a JChooseBox.
 * @author Romain Guy
 */

public class JextToolBar extends JToolBar
{
  private boolean grayed= false;
  private JToolBar buttonsPanel;
  private JToolBar persistentToolBar = new JToolBar();
  private JToolBar transientToolBar = new JToolBar();
  
  /**
   * Creates a new tool bar.
   */

  public JextToolBar(JextFrame parent)
  {
    super();
    setFloatable(false);
    //super(Jext.getProperty("jext.toolbar.title"));

    persistentToolBar.putClientProperty("JEXT_INSTANCE", parent);
    persistentToolBar.setFloatable(false);
    persistentToolBar.setBorderPainted(false);
    persistentToolBar.setOpaque(false);

    super.add(persistentToolBar);

    transientToolBar.putClientProperty("JEXT_INSTANCE", parent);
    transientToolBar.setFloatable(false);
    transientToolBar.setBorderPainted(false);
    transientToolBar.setOpaque(false);

    super.add(transientToolBar);

    //addMisc(parent);
    buttonsPanel = persistentToolBar;  // first we load persistent plugins
  }

  public void addMisc(JextFrame parent)
  {
    // fast find
    add(Box.createHorizontalStrut(10));
    JextButton iFind = new JextButton(
                           Jext.getProperty(
                           Jext.getBooleanProperty("find.incremental") ? "find.incremental.label" : "find.label"));
    iFind.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        if (Jext.getBooleanProperty("find.incremental"))
        {
          ((JextButton) evt.getSource()).setText(Jext.getProperty("find.label"));
          Jext.setProperty("find.incremental", "off");
        } else {
          ((JextButton) evt.getSource()).setText(Jext.getProperty("find.incremental.label"));
          Jext.setProperty("find.incremental", "on");
        }
      }
    });
    add(iFind);
    Box box = new Box(BoxLayout.Y_AXIS);
    box.add(Box.createVerticalGlue());
    box.add(new FastFind(parent));
    box.add(Box.createVerticalGlue());
    add(box);

    // fast syntax
    add(Box.createHorizontalStrut(10));
    Box boxx = new Box(BoxLayout.Y_AXIS);
    boxx.add(Box.createVerticalGlue());
    boxx.add(new FastSyntax(parent));
    boxx.add(Box.createVerticalGlue());
    add(boxx);
  }

  public void setGrayed(boolean on)
  {
    if (grayed == on)
      return;

    int i = -1;
    java.awt.Component c;

    while ((c = buttonsPanel.getComponentAtIndex(++i)) != null)
    {
      if (c instanceof JextButton)
        ((JextButton) c).setGrayed(on);
    }

    grayed = on;
  }

  /**
   * Appends a button in the tool bar.
   * @param button The button to be added
   */

  public void addButton(JextButton button)
  {
    button.setMargin(new Insets(1, 1, 1, 1));        // added by Steve Lawson
    buttonsPanel.add(button);
  }

  /**
   * Adds a separator in the buttons panel.
   */

  public void addButtonSeparator()
  {
    JToolBar.Separator s = new JToolBar.Separator();
    buttonsPanel.add(s);
  }

  /**
   * Stores the toolbar before starting mode-specific plugins.
   * (Actually switches the toolbar)
   */

  public void freeze()
  {
    buttonsPanel = transientToolBar;
  }

  /**
   * Restores the toolbar.
   */

  public void reset()
  {
    transientToolBar.removeAll();
  }
}

// End of JextToolBar