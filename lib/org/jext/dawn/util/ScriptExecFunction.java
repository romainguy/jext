/*
 * ScriptExecFunction.java - exec another script
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

import java.io.StringReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.jext.dawn.*;

/**
 * Executes another script.<br>
 * Usage:<br>
 * <code>string run</code><br>
 * Where string is the path (absolute or relative) to a script.
 * @author Romain Guy
 */

public class ScriptExecFunction extends Function
{
  public ScriptExecFunction()
  {
    super("run");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    String script = parser.popString();
    try
    {
      BufferedReader in = new BufferedReader(new InputStreamReader(
                          new FileInputStream(DawnUtilities.constructPath(script))));
      String line;
      StringBuffer buf = new StringBuffer();

      for ( ; (line = in.readLine()) != null; )
        buf.append(line).append('\n');

      in.close();

      DawnParser _parser = new DawnParser(new StringReader(buf.toString()));
      _parser.exec();
      parser.out.print('\n' + _parser.dump());
    } catch (Exception e) {
      throw new DawnRuntimeException(this, parser, "error occured attempting to execute script: "
                                     + script + "\n:" + e.getMessage());
    }
  }
}

// End of ScriptExecFunction.java
