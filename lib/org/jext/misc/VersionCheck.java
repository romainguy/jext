/*
 * 11/15/2000 - 22:05:27
 *
 * VersionCheck.java - Version Check
 * Copyright (C) 1999 Romain Guy
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

package org.jext.misc;

import java.io.*;
import java.util.Vector;

import java.net.URL;

import java.awt.*;
import javax.swing.*;

import org.jext.*;

public class VersionCheck extends Thread
{
  public VersionCheck()
  {
    super("----thread: version check: jext----");
    start();
  }

  public void run()
  {
    try
    {
      URL url = new URL(Jext.getProperty("check.url"));
      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

      StringBuffer buf = new StringBuffer();

      String line;
      String build = null;
      String version = null;

      while ((line = in.readLine()) != null)
      {
        if (line.startsWith("#"))
          continue;
        else if (line.startsWith(".release"))
          version = line.substring(8).trim();
        else if (line.startsWith(".build"))
          build = line.substring(6).trim();
        else if (line.startsWith(".description"))
        {
          while ((line = in.readLine()) != null && !line.equals(".end"))
            buf.append(line);
        } else if (line.startsWith(".end"))
          break;
      }

      in.close();

      if (version != null && build != null)
      {
        if (Jext.BUILD.compareTo(build) < 0)
        {
          String[] args2 = { version, build };

          JEditorPane textArea = new JEditorPane();
          textArea.setContentType("text/html");
          textArea.setText(buf.toString());
          textArea.setEditable(false);

          JPanel pane = new JPanel(new BorderLayout());
          pane.add(BorderLayout.NORTH, new JLabel(Jext.getProperty("check.changes")));
          pane.add(BorderLayout.CENTER, new JScrollPane(textArea));

          JOptionPane.showMessageDialog((JextFrame) Jext.getInstances().get(0), pane,
                                        Jext.getProperty("check.new"),
                                        JOptionPane.INFORMATION_MESSAGE);
        }
      }
    } catch (Exception e) {
    } finally { Jext.stopAutoCheck(); }
  }
}

// End of VersionCheck.java
