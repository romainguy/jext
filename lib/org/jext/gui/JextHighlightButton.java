/*
 * 10/29/2001 - 22:26:49
 *
 * JextHighlightButton.java - A modified button
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
import javax.swing.JButton;

import java.awt.Color;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.jext.Jext;

public class JextHighlightButton extends JButton
{
  private Color nColor; //, savedNColor;
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
      /*savedNColor =*/ nColor = getBackground();
      addMouseListener(_mouseListener = new MouseHandler());
    }
  }

  public JextHighlightButton()
  {
    super();
    init();
  }

  public JextHighlightButton(String label)
  {
    super(label);
    init();
  }

  public JextHighlightButton(Icon icon)
  {
    super(icon);
    init();
  }

  public JextHighlightButton(String label, Icon icon)
  {
    super(label, icon);
    init();
  }

  class MouseHandler extends MouseAdapter
  {
    public void mouseEntered(MouseEvent me)
    {
      nColor = getBackground();
      if (isEnabled())
        setBackground(commonHighlightColor);
    }
    
    /*public void mousePressed(MouseEvent me)
    {
      nColor = savedNColor; //without this, when a button is pressed(and becomes darker)
      //we remember as nColor(normal color) the darker one.
    }*/

    public void mouseExited(MouseEvent me)
    {
      if (isEnabled())
        setBackground(nColor);
    }
  }
  

}

// End of JextHighlightButton.java