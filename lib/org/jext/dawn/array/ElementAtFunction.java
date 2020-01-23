/*
 * ElementAtFunction.java - element at
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

package org.jext.dawn.array;

import org.jext.dawn.*;

/**
 * Returns the element stored at the given index of an array.<br>
 * Usage:<br>
 * <code>array index elementAt</code><br>
 * This function then pushes both the array and the object on the stack.
 * @author Romain Guy
 */

public class ElementAtFunction extends Function
{
  public ElementAtFunction()
  {
    super("elementAt");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkArgsNumber(this, 2);
    int index = (int) parser.popNumber();

    try
    {
      parser.push(parser.peekArray().elementAt(index));
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      throw new DawnRuntimeException(this, parser, "array index " + index + " out of bounds");
    }
  }
}

// End of ElementAtFunction.java
