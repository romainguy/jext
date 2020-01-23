/*
 * 01/25/2003 - 19:36:55
 *
 * PythonAction.java - Interface for menu items
 * Copyright (C) 2003 Romain Guy
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

package org.jext;

import java.awt.event.ActionEvent;

import org.jext.scripting.python.Run;
import org.python.util.PythonInterpreter;

/**
 * This class implements an <code>ActionListener</code> and
 * handles action events fired by <codeJMenuItem</code>.
 * The action is performed by executing a Python script.
 */

public class PythonAction extends MenuAction
{
  private String script;

  /**
   * Creates a new menu action designed by its name and
   * the pyhon execution script.
   * @param name Internal action name
   * @param script Python script
   */

  public PythonAction(String name, String script)
  {
    super(name);
    this.script = script;
  }

  public void actionPerformed(ActionEvent evt)
  {
    if (script != null && script.length() > 0)
    {
      try
      {
        PythonInterpreter parser = Run.getPythonInterpreter(getJextParent(evt));
        parser.set("__evt__", evt);
        parser.exec(script);
      } catch (Exception pe) {
        System.out.println("python action: " + getName());
        //pe.printStackTrace();
        System.out.println(pe);
        //getJextParent(evt).getPythonLogWindow().logln(pe.toString());
      }
    }
  }
}

// End of PythonAction.java