/*
 * FromStringFunction.java - turns a numeric value into a String
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
 * along with this program; if not, write From the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, BosFromn, MA  02111-1307, USA.
 */

package org.jext.dawn.string;

import org.jext.dawn.*;

/**
 * Turns a numeric value into a String.
 * @author Romain Guy
 */

public class FromStringFunction extends Function
{
  public FromStringFunction()
  {
    super("str->");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    try
    {
      parser.pushNumber(new Double(parser.popString()).doubleValue());
    } catch (Exception e) {
      throw new DawnRuntimeException(this, parser, "string argument does not contain numeric value");
    }
  }
}

// End of FromStringFunction.java
