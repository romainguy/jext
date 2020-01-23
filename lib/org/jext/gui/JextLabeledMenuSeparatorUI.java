/*
 * 11/14/2000 - 17:37:52
 *
 * JextLabeledMenuSeparatorUI.java - A new UI for popup separators
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

package org.jext.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

public class JextLabeledMenuSeparatorUI extends javax.swing.plaf.metal.MetalSeparatorUI
{
  private String stext;
  private Font labelFont;
  
  public JextLabeledMenuSeparatorUI(JComponent c)
  {
    if (c instanceof JextLabeledMenuSeparator)
      stext = ((JextLabeledMenuSeparator) c).getSeparatorText();
    if (stext != null)
    {
      labelFont = new Font("Monospaced", java.awt.Font.PLAIN, 8);
      shadow = UIManager.getColor("controlDkShadow");
      highlight = UIManager.getColor("controlLtHighlight");
    }
  }

  public static ComponentUI createUI(JComponent c)
  {
    return new JextLabeledMenuSeparatorUI(c);
  }

  public void paint(Graphics g, JComponent c)
  {
    if (stext != null)
    {
      g.setFont(labelFont);
      FontMetrics fm = g.getFontMetrics();
      g.setColor(highlight);
      int h = fm.getHeight() / 2;
      g.drawString(stext, 4, h + 1);
      g.setColor(shadow);
      g.drawString(stext, 5, h);
      g.setColor(Color.black);
      g.drawLine(fm.stringWidth(stext) + 8, 4, c.getSize().width, 4);
    } else {
      g.setColor(Color.black);
      g.drawLine(0, 0, c.getSize().width, 0);
    }
  }

  public Dimension getPreferredSize(JComponent c)
  { 
    return new Dimension(0, stext == null ? 1 : 10);
  }
  

}

// End of JextLabeledMenuSeparatorUI.java
