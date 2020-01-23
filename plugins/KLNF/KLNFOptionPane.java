/*
 * 08/30/2001 - 10:16:42
 *
 * KLNFOptionPane.java - KLNF plugin options
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

import java.awt.*;
import javax.swing.*;

import org.jext.*;
import org.jext.gui.*;

public class KLNFOptionPane extends AbstractOptionPane
{
  private JextCheckBox enable;

  public KLNFOptionPane()
  {
    super("klnf");
    addComponent(enable = new JextCheckBox(Jext.getProperty("klnf.enable.label")));
    enable.setSelected("on".equals(Jext.getProperty("klnf.enable")));
  }

  public void save()
  {
    Jext.setProperty("klnf.enable", enable.isSelected() ? "on" : "off");
  }  
}

// End of KLNFOptionPan.java
