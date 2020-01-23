/*
 * 19:23:53 13/12/99
 *
 * IndenterOptionPane.java - 
 * Copyright (C) 1999 Romain Guy
 * powerteam@chez.com
 * www.chez.com/powerteam
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

public class IndenterOptionPane extends AbstractOptionPane
{
  private JTextField maxLineWidth;

  public IndenterOptionPane()
  {
    super("html.indenter");

    JLabel n = new JLabel(Jext.getProperty("html.indenter.title"), SwingConstants.CENTER);
    n.setFont(new Font("dialog", Font.ITALIC | Font.BOLD, 14));
    addComponent(n);

    addComponent(Jext.getProperty("html.indenter.maxLineWidth.label"),
                 maxLineWidth = new JTextField(4));
    maxLineWidth.setText(Jext.getProperty("html.indenter.maxLineWidth"));
    maxLineWidth.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
  }

  public void save()
  {
    Jext.setProperty("html.indenter.maxLineWidth", maxLineWidth.getText());
  }  
}

// End of FastFindOptions.java
