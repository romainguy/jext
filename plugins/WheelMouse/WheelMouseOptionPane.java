/*
 * Copyright (C) 1999 Scott Wyatt
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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import org.jext.gui.*;
import org.jext.Jext;

import org.jext.Utilities;

public class WheelMouseOptionPane extends AbstractOptionPane
{
  private JextCheckBox cLineEnabled;
  private JextCheckBox cImageEnabled;
  private JTextField tWheelMouseLineIncrement;

  public WheelMouseOptionPane()
  {
    super("Wheel Mouse");

    setBorder(new EmptyBorder(5, 5, 5, 5));

    addComponent(cImageEnabled = new JextCheckBox(Jext.getProperty("options.wheelmouse.imgenabled")));
    addComponent(cLineEnabled = new JextCheckBox(Jext.getProperty("options.wheelmouse.lineenabled")));
    addComponent(Jext.getProperty("options.wheelmouse.line"), tWheelMouseLineIncrement =
                                  new JTextField(Jext.getProperty("wheelmouse.line"), 15));

    boolean imageEnabled = "on".equals(Jext.getProperty("wheelmouse.imgenabled"));
    cImageEnabled.setSelected(imageEnabled);

    boolean lineEnabled = "on".equals(Jext.getProperty("wheelmouse.lineenabled"));
    cLineEnabled.setSelected(lineEnabled);
    tWheelMouseLineIncrement.setEnabled(lineEnabled);

    cImageEnabled.addActionListener(new ActionListener()
                                    {
                                      public void actionPerformed(ActionEvent e)
                                      {
                                        boolean enbld = cImageEnabled.isSelected();
                                        cImageEnabled.setSelected(enbld);
                                      }
                                    }
                                   );

    cLineEnabled.addActionListener(new ActionListener()
                                   {
                                     public void actionPerformed(ActionEvent e)
                                     {
                                       boolean enbld = cLineEnabled.isSelected();
                                       cLineEnabled.setSelected(enbld);
                                       tWheelMouseLineIncrement.setEnabled(enbld);
                                     }
                                   }
                                  );
  }


  /**
    * Called when the options dialog's `OK' button is pressed.
    * This should save any properties saved in this option pane.
    */
  public void save()
  {
    Jext.setProperty("wheelmouse.imgenabled", cImageEnabled.isSelected() ? "on" : "off");
    Jext.setProperty("wheelmouse.lineenabled", cLineEnabled.isSelected() ? "on" : "off");
    Jext.setProperty("wheelmouse.line", tWheelMouseLineIncrement.getText());
  }
}

