/*
 * LengthFunction.java - array length
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
 * Returns the length of a given array. The length of an array
 * represents the total amount of elements put in it.<br>
 * Usage:<br>
 * <code>array length</code><br>
 * Then this function pushes the array length AND the array on stack.
 * @author Romain Guy
 */

public class LengthFunction extends Function
{
  public LengthFunction()
  {
    super("length");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    parser.pushNumber((double) parser.peekArray().size());
  }
}

// End of LengthFunction.java
