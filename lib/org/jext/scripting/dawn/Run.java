/*
 * 05/22/2001 - 20:38:27
 *
 * DawnRunScript.java
 * Copyright (C) 2001 Romain Guy
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

package org.jext.scripting.dawn;

import java.io.*;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;

import org.jext.*;
import org.jext.dawn.*;

public final class Run
{
  /**
   * Executes some Dawn code.
   * @param code The script code to be interpreted
   * @param parent The window which execuets the script
   */

  public static void execute(String code, JextFrame parent)
  {
    execute(code, parent, true);
  }

  /**
   * Executes some Dawn code.
   * @param code The script code to be interpreted
   * @param parent The window which execuets the script
   * @param isThreaded If true, Dawn libraries loading is threaded
   */

  public static void execute(String code, JextFrame parent, boolean isThreaded)
  {
    if (!isThreaded)
    {
      try
      {
        if (!DawnParser.isInitialized())
        {
          DawnParser.init();
          DawnParser.installPackage(Jext.class, "dawn-jext.scripting");
        }

        DawnParser parser = new DawnParser(new StringReader(code));
        parser.setProperty("JEXT.JEXT_FRAME", parent);
        parser.exec();

        if (Jext.getBooleanProperty("dawn.scripting.debug"))
        {
          String dumped = parser.dump();
          if (dumped.length() > 0)
            parent.getDawnLogWindow().logln(dumped);
        }
      } catch (DawnRuntimeException dre) {
        if (Jext.getBooleanProperty("dawn.scripting.debug"))
        {
          JOptionPane.showMessageDialog(parent, dre.getMessage(), Jext.getProperty("dawn.script.error"),
                                        JOptionPane.ERROR_MESSAGE);
        }
      }
    } else
      new ThreadExecuter(code, parent);
  }

  // the script is executed within a thread

  static class ThreadExecuter extends Thread
  {
    private String code;
    private JextFrame parent;

    ThreadExecuter(String code, JextFrame parent)
    {
      super("---Thread:Dawn runtime---");

      this.code = code;
      this.parent = parent;

      start();
    }

    public void run()
    {
      try
      {
        if (!DawnParser.isInitialized())
        {
          DawnParser.init();
          DawnParser.installPackage(Jext.class, "dawn-jext.scripting");
        }

        DawnParser parser = new DawnParser(new StringReader(code));
        parser.setProperty("JEXT.JEXT_FRAME", parent);
        parser.exec();

        if (Jext.getBooleanProperty("dawn.scripting.debug"))
        {
          String dumped = parser.dump();
          if (dumped.length() > 0)
            parent.getDawnLogWindow().logln(dumped);
        }
      } catch (DawnRuntimeException dre) {
        JOptionPane.showMessageDialog(parent, dre.getMessage(), Jext.getProperty("dawn.script.error"),
                                      JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  /**
   * Runs a Jext script from a file.
   * @param fileName Path to the script
   * @param parent The Jext window which have to execute the script
   */

  public static void runScript(String fileName, JextFrame parent)
  {
    runScript(fileName, parent, true);
  }

  /**
   * Runs a Jext script from a file.
   * @param fileName Path to the script
   * @param parent The Jext window which have to execute the script
   * @param isThreaded If true, loading of libraries is threaded
   */

  public static void runScript(String fileName, JextFrame parent, boolean isThreaded)
  {
    try
    {
      BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));

      String line;
      StringBuffer buf = new StringBuffer();
      for ( ; (line = in.readLine()) != null; )
        buf.append(line).append('\n');

      in.close();
      execute(buf.toString(), parent, isThreaded);

    } catch (IOException ioe) {
      Utilities.showError(Jext.getProperty("dawn.script.cannotread"));
    }
  }
}

// End of Run.java
