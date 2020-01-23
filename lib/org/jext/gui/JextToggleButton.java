/*
 * 09/12/2001 - 23:15:07
 *
 * JextToggleButton.java - A modified toggle button
 * Copyright (C) 2001 Romain Guy
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

import javax.swing.Icon;
import javax.swing.JToggleButton;

import java.awt.Color;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.jext.Jext;

public class JextToggleButton extends JToggleButton
{
  private Color nColor;
  private MouseHandler _mouseListener;
  private static Color commonHighlightColor = new Color(192, 192, 210);
  private static boolean blockHighlightChange = false;

  public static void setHighlightColor(Color color)
  {
    if (!blockHighlightChange)
      commonHighlightColor = color;
  }

  public static Color getHighlightColor()
  {
    return commonHighlightColor;
  }

  public static void blockHighlightChange()
  {
    blockHighlightChange = true;
  }

  public static void unBlockHighlightChange()
  {
    blockHighlightChange = false;
  }

  private void init()
  {
    if (Jext.getButtonsHighlight())
    {
      setFocusPainted(false);
      nColor = getBackground();
      addMouseListener(_mouseListener = new MouseHandler());
    }
  }

  public JextToggleButton()
  {
    super();
    init();
  }

  public JextToggleButton(String label)
  {
    super(label);
    init();
  }

  public JextToggleButton(Icon icon)
  {
    super(icon);
    init();
  }

  public JextToggleButton(String label, Icon icon)
  {
    super(label, icon);
    init();
  }

  class MouseHandler extends MouseAdapter
  {
    public void mouseEntered(MouseEvent me)
    {
      if (isEnabled())
        setBackground(commonHighlightColor);
    }

    public void mouseExited(MouseEvent me)
    {
      if (isEnabled())
        setBackground(nColor);
    }
  }
  

}

// End of JextToggleButton.java