/*
 * 19:00:51 17/03/00
 *
 * ClassBrowserOptions.java - The ClassBrowser options pane
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

public class ClassBrowserOptions extends AbstractOptionPane
{
  private JTextField defaultDocURL;

  public ClassBrowserOptions()
  {
    super("class_browser");

    addComponent(Jext.getProperty("class_browser.doc_url.label"), defaultDocURL = new JTextField(
                 Jext.getProperty("class_browser.base_api_doc_url"), 20));
  }

  public void save()
  {
    Jext.setProperty("class_browser.base_api_doc_url", defaultDocURL.getText());
  }  
}

// End of ClassBrowserOptions.java
