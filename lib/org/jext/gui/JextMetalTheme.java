/*
 * 11:46:35 06/06/00
 *
 * JextMetalTheme.java - A new theme for Metal L&F
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

import java.awt.Font;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

public class JextMetalTheme extends DefaultMetalTheme
{
  private ColorUIResource color = new ColorUIResource(0, 0, 0);
  private FontUIResource font = new FontUIResource("Dialog", Font.PLAIN, 11);

  public JextMetalTheme()
  {
    super();
  }

  public ColorUIResource getControlTextColor()
  {
    return color;
  }

  public ColorUIResource getMenuTextColor()
  {
    return color;
  }

  public ColorUIResource getSystemTextColor()
  {
    return color;
  }

  public ColorUIResource getUserTextColor()
  {
    return color;
  }

  public FontUIResource getControlTextFont()
  {
    return font;
  }

  public FontUIResource getMenuTextFont()
  {
    return font;
  }

  public FontUIResource getSystemTextFont()
  {
    return font;
  }
      
  public FontUIResource getUserTextFont()
  {
    return font;
  }

  public FontUIResource getWindowTitleFont()
  {
    return font;
  }
}

// End of JextMetalTheme.java
