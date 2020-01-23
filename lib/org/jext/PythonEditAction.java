/*
 * 08/26/2001 - 14:57:13
 *
 * PythonEditAction.java - Interface for menu items
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

package org.jext;

import java.awt.event.ActionEvent;

import org.jext.scripting.python.Run;
import org.python.util.PythonInterpreter;

/**
 * This class implements an <code>ActionListener</code> and
 * handles action events fired by <codeJMenuItem</code>.
 * The action is performed by executing a Python script.
 */

public class PythonEditAction extends PythonAction implements EditAction
{
  /**
   * Creates a new menu action designed by its name and
   * the pyhon execution script.
   * @param name Internal action name
   * @param script Python script
   */

  public PythonEditAction(String name, String script)
  {
    super(name, script);
  }
}

// End of PythonEditAction.java
