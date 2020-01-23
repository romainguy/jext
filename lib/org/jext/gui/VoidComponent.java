/*
 * 02:24:22 14/09/00
 *
 * VoidComponent.java - An empty component
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

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * <code>VoidComponent</code> is a component of both
 * height and width set to zero which cannot contain
 * anything.
 */

public class VoidComponent extends JComponent
{
  private Dimension zero = new Dimension(0, 0);

  public int getHeight()
  {
    return 0;
  }

  public Dimension getMaximumSize()
  {
    return zero;
  }

  public Dimension getMinimumSize()
  {
    return zero;
  }

  public Dimension getPreferredSize()
  {
    return zero;
  }

  public Dimension getSize()
  {
    return zero;
  }

  public int getWidth()
  {
    return 0;
  }

  public void paint(Graphics g)
  {
  }

  public void setSize(Dimension d)
  {
  }

  public void setSize(int w, int h)
  {
  }

  public void update(Graphics g)
  {
  }
}

// End of VoidComponent.java

