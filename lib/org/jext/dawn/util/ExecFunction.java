/*
 * ExecFunction.java - executes an OS command
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * http://www.jext.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either Exec 2
 * of the License, or any later Exec.
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

package org.jext.dawn.util;

import java.io.*;
import org.jext.dawn.*;

/**
 * Executes an OS command.<br>
 * Usage:<br>
 * <code>string exec</code><b>
 * Where string contains the command to be executed. When command terminates,
 * Dawn pushes the exit code on stack.
 * @author Romain Guy
 */

public class ExecFunction extends Function
{
  public ExecFunction()
  {
    super("exec");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    String command = parser.popString();

    try
    {
      Process process = Runtime.getRuntime().exec(command);
      process.getOutputStream().close();
      parser.pushNumber(process.waitFor());
    } catch(Exception e) {
      throw new DawnRuntimeException(this, parser, "error occured attempting to execute command: "
                                     + command + "\n:" + e.getMessage());
    }
  }
}

// End of ExecFunction.java
