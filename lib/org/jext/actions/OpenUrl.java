/*
 * 01/01/2001 - 22:27:03
 *
 * OpenUrl.java
 * Copyright (C) 2000 Romain Guy
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

package org.jext.actions;

import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;
import org.jext.*;
import java.awt.event.ActionEvent;

public class OpenUrl extends MenuAction
{
  public OpenUrl()
  {
    super("open_url");
  }

  public void actionPerformed(ActionEvent evt)
  {
    JextFrame parent = getJextParent(evt);
    JextTextArea textArea = parent.createFile();

    String response = JOptionPane.showInputDialog(getJextParent(evt),
                      Jext.getProperty("openurl.label"), Jext.getProperty("openurl.title"),
                      JOptionPane.QUESTION_MESSAGE);
    boolean err = true;
    if (response != null)
    {
      try
      {
        URL url = new URL(response);
        textArea.open(response, new InputStreamReader(url.openStream()), 1024, true, false);
        err = false;
      } catch (MalformedURLException mue) {
        Utilities.showError(Jext.getProperty("url.malformed"));
      } catch (IOException ioe) {
        Utilities.showError(ioe.toString());
      }

      if (err)
        parent.close(textArea);

    } else
      parent.close(textArea);
  }
}

// End of OpenUrl.java
