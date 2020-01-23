/*
 * RclFunction.java - rcl command
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * http://www.jext.org
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

package org.jext.dawn.stack;

import java.util.Vector;

import org.jext.dawn.*;

/**
 * Recalls a variable value on the stack.<br>
 * Usage:<br>
 * <code>variable rcl</code>
 * @author Romain Guy
 */

public class RclFunction extends Function
{
  public RclFunction()
  {
    super("rcl");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    String var = parser.popString();
    parser.checkVarName(this, var);

    Object obj = parser.getVariable(var);
    if (obj == null)
      throw new DawnRuntimeException(this, parser, "unknown variable:" + var);
    if (obj instanceof Double)
      parser.pushNumber(((Double) obj).doubleValue());
    else if (obj instanceof Vector)
      parser.pushArray((Vector) obj);
    else if (obj instanceof String)
    {
      String str = obj.toString();
      if (str.length() == 0)
        str = "";
      else if (str.startsWith("\"") && str.endsWith("\""))
        str= str.substring(1, str.length() - 1);
      parser.pushString(str);
    } else
      parser.pushString(obj.toString());
  }
}

// End of RclFunction.java
