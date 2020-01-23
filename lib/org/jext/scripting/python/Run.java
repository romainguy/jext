/*
 * 01/25/2003 - 19:41:47
 *
 * Run.java
 * Copyright (C) 2001 Romain Guy
 *
 * This free software; you can redistribute it and/or
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

package org.jext.scripting.python;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;

import org.jext.*;
import org.jext.console.Console;
import org.python.core.*;
import org.python.util.PythonInterpreter;

public final class Run
{
  private static PythonInterpreter parser;

  /**
   * This is the list of the packages. It should be used to add here other packages:
   * JARClassLoader, while scanning for plugins, should add here new packages found.
   * @since Jext3.2pre4
   */
  private static ArrayList packageList;

  /**
   * Use this to add a package to the list of packages usable by Python. It will work
   * only for Interpreters we setup after this call.
   * @since Jext3.2pre5
   */
  public static void addPackage(String packageName) {
    buildPackageList(); //else the following line could create a NPE.
    packageList.add(packageName);
  }

  private static void buildPackageList() {
    if (packageList == null) {
      packageList = new ArrayList();

      InputStream packages = Jext.class.getResourceAsStream("packages");
      if (packages != null)
      {
        String line;
        BufferedReader in = new BufferedReader(new InputStreamReader(packages));
        try
        {
          while ((line = in.readLine()) != null)
            packageList.add(line);
          in.close(); 
        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
      }
    }
  }

  /**
   * This method does the global setup for PythonInterpreter: on each one
   * you'll need to call it only once.
   * @since Jext3.2pre4
   */
  public static void startupPythonInterpreter(PythonInterpreter interp) {
    PyModule mod = imp.addModule("__main__");
    interp.setLocals(mod.__dict__);

    buildPackageList();
    PySystemState sys = Py.getSystemState();
    for (Iterator i = packageList.iterator(); i.hasNext(); )
      sys.add_package((String)i.next());
  }

  /**
   * This method does the local setup for PythonInterpreter: on each one
   * you'll need to call it every time parent changes. It setups the __jext__
   * script variable and the I/O streams.
   * @param interp the PythonInterpreter to setup
   * @param parent the value to give to the __jext__ var; if null, output will go to Jext's
   * stdout and stderr(i.e. System.out and System.err)
   * @param console if null, output will go to the log window of parent; else, it will go
   * inside this console
   * @since Jext3.2pre4
   */
  public static void setupPythonInterpreter(PythonInterpreter interp, JextFrame parent,
      Console console) {
    interp.set("__jext__", parent);

    if (console != null) {
      interp.setErr(console.getStdErr());
      interp.setOut(console.getStdOut());
    } else if (parent != null) {
      interp.setErr(parent.getPythonLogWindow().getStdErr());
      interp.setOut(parent.getPythonLogWindow().getStdOut());
    } else {
      interp.setOut(System.out);
      interp.setErr(System.err);
    }
  }

  /**
   * Creates an interpreter.
   * @param parent The window which executes the script
   */

  public static PythonInterpreter getPythonInterpreter(JextFrame parent) {
    return getPythonInterpreter(parent, null);
  }

  /**
   * Creates an interpreter.
   * @param parent The window which executes the script
   * @param console
   */

  public static PythonInterpreter getPythonInterpreter(JextFrame parent, Console console)
  {
    if (parser == null)
    {
      parser = new PythonInterpreter();
      startupPythonInterpreter(parser);
    }
    setupPythonInterpreter(parser, parent, console);

    return parser;
  }

  /**
   * Evaluates some Python code.
   * @param code The script code to be evaluated
   * @param map A map of properties to add to interpreter
   * @param parent The window which executes the script
   * @return The result of the evaluation
   */

  public static PyObject eval(String code, String mapName, Object[] map, JextFrame parent)
  {
    try
    {
      PythonInterpreter parser = getPythonInterpreter(parent);

      if (map != null && mapName != null)
        parser.set(mapName, map);

      return parser.eval(code);
    } catch (Exception pe) {
      JOptionPane.showMessageDialog(parent, Jext.getProperty("python.script.errMessage"),
                                    Jext.getProperty("python.script.error"),
                                    JOptionPane.ERROR_MESSAGE);
      if (Jext.getBooleanProperty("dawn.scripting.debug"))
        System.err.println(pe.toString());
      // security ?
      parser = null;
    }

    return null;
  }

  /**
   * Executes some Python code.
   * @param code The script code to be interpreted
   * @param parent The window which executes the script
   */

  public static void execute(String code, JextFrame parent)
  {
    try
    {
      PythonInterpreter parser = getPythonInterpreter(parent);
      parser.exec(code);
    } catch (Exception pe) {
      if (Jext.getBooleanProperty("dawn.scripting.debug"))
      {
        JOptionPane.showMessageDialog(parent, Jext.getProperty("python.script.errMessage"),
                                      Jext.getProperty("python.script.error"),
                                      JOptionPane.ERROR_MESSAGE);
        parent.getPythonLogWindow().logln(pe.toString());
      }
      // security ?
      // FIXME: I don't understand the above comment. But isn't this a too
      // drastic way to solve things?
      parser = null;
    }
  }

  /**
   * Runs a Jext script from a file.
   * @param fileName Path to the script
   * @param parent The Jext window which have to execute the script
   */

  public static void runScript(String fileName, JextFrame parent)
  {
    try
    {
      PythonInterpreter parser = getPythonInterpreter(parent);
      parser.execfile(fileName);
    } catch (Exception pe) {
      JOptionPane.showMessageDialog(parent, Jext.getProperty("python.script.errMessage"),
                                    Jext.getProperty("python.script.error"),
                                    JOptionPane.ERROR_MESSAGE);
      if (Jext.getBooleanProperty("dawn.scripting.debug"))
        parent.getPythonLogWindow().logln(pe.toString());
      // security ?
      parser = null;
    }
  }
}

// End of Run.java
