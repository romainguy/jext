/*
 * IncreaseFunction.java - ++ operator
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * http://www.jext.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either Increase 2
 * of the License, or any later Increase.
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

package org.jext.dawn.math;

import org.jext.dawn.*;

/**
 * Increases a numeric value (which can be in a variable) by one.<br>
 * Usage:<br>
 * <code>number ++</code> or <code>var ++</code>.
 * @author Romain Guy
 */

public class IncreaseFunction extends Function
{
  public IncreaseFunction()
  {
    super("++");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    if (parser.isTopNumeric())
      parser.pushNumber(parser.popNumber() + 1);
    else
    {
      String var = parser.popString();
      parser.checkVarName(this, var);
      Object obj = parser.getVariable(var);
      if (obj instanceof Double)
      {
        double value = ((Double) obj).doubleValue();
        parser.setVariable(var, new Double(value + 1));
      } else
        throw new DawnRuntimeException(this, parser, "variable " + var +
                                       " does not contains a numeric value");
    }
  }
}

// End of IncreaseFunction.java
