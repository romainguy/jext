/*
 * 19:50:05 21/10/99
 *
 * ClassBrowserGUI.java
 * Copyright (C) 1999 Romain Guy
 *
 * This	free software; you can redistribute it and/or
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

import java.awt.Dimension;
import javax.swing.JFrame;
import org.jext.*;
import java.awt.event.ActionEvent;

public class ClassBrowserGUI extends MenuAction
{
  public ClassBrowserGUI()
  {
    super("class_browser");
  }

  public void actionPerformed(ActionEvent evt)
  {
    ClassBrowser cb = new ClassBrowser();
    cb.init();

    JFrame f = new JFrame(Jext.getProperty("class_browser.title"));
    f.getContentPane().add(cb);
    cb.setFrame(f);
    f.setSize(new Dimension(700, 450));
    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    f.setIconImage(GUIUtilities.getJextIconImage());
    Utilities.centerComponent(f);
    f.setVisible(true);
  }
}

// End of ClassBrowserGUI.java
