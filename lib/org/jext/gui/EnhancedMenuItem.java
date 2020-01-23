/*
 * EnhancedMenuItem.java - Menu item with user-specified accelerator string
 * Copyright (C) 1999 Slava Pestov, Portions Copyright 2001 Romain Guy
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
import java.awt.event.*;

import javax.swing.*;

import org.jext.*;

/**
 * The <code>EnhancedMenuItem</code> is just a <code>JItem</code>
 * which displays the key shortcuts in a different way.
 */

public class EnhancedMenuItem extends JMenuItem
{
  // private members
  private String keyBinding;
  private Font acceleratorFont;
  private Color acceleratorForeground;
  private Color acceleratorSelectionForeground;
  
  /**
   * Creates a new enhanced menu item of given label.
   * @param label The label which is displayed by the item
   */

  public EnhancedMenuItem(String label)
  {
    this(label, null);
  }

  /**
   * Creates a new enhanced menu item of given label and
   * specified attached key shortcut.
   * @param label The label which is displayed by the item
   * @param keyBinding Relative keyshortcut
   */

  public EnhancedMenuItem(String label, String keyBinding)
  {
    super(label);
    this.keyBinding = keyBinding;

    if (Jext.getFlatMenus())
      setBorder(new javax.swing.border.EmptyBorder(2, 2, 2, 2));

    acceleratorFont = UIManager.getFont("MenuItem.acceleratorFont");
    acceleratorForeground = UIManager.getColor("MenuItem.acceleratorForeground");
    acceleratorSelectionForeground = UIManager.getColor("MenuItem.acceleratorSelectionForeground");
  }

  public Dimension getPreferredSize()
  {
    Dimension d = super.getPreferredSize();
    if (keyBinding != null)
      d.width += (getToolkit().getFontMetrics(acceleratorFont).stringWidth(keyBinding) + 30);
    return d;
  }

  public void paint(Graphics g)
  {
    super.paint(g);

    if (keyBinding != null)
    {
      g.setFont(acceleratorFont);
      g.setColor(getModel().isArmed() ?	acceleratorSelectionForeground : acceleratorForeground);
      FontMetrics fm = g.getFontMetrics();
      Insets insets = getInsets();
      g.drawString(keyBinding, getWidth() - (fm.stringWidth(keyBinding) + insets.right +
                   insets.left), getFont().getSize() + (insets.top - 1));
    }
  }

  public String getActionCommand()
  {
    return getModel().getActionCommand();
  }

  protected void fireActionPerformed(ActionEvent event)
  {
    JextTextArea area = MenuAction.getTextArea(this);
    area.setOneClick(null);
    area.endCurrentEdit();
    //super.fireActionPerformed(event);

    Object[] listeners = listenerList.getListenerList();
    ActionEvent e = null;

    for (int i = listeners.length - 2; i >= 0; i -= 2)
    {
      if (listeners[i + 1] instanceof EditAction && !area.isEditable())
        continue;
  
      if (listeners[i] == ActionListener.class)
      {
        if (e == null)
        {
          String actionCommand = event.getActionCommand();
          if(actionCommand == null)
             actionCommand = getActionCommand();

          e = new ActionEvent(this,
                              ActionEvent.ACTION_PERFORMED,
                              actionCommand,
                              event.getModifiers());
        }
        ((ActionListener) listeners[i + 1]).actionPerformed(e);
      }
    }
  }
  

}

// End of EnhancedMenuItem.java