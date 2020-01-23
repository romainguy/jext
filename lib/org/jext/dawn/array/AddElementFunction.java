/*
 * AddElementFunction.java - add element
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
 * Adds an element at the end of a given array.<br>
 * Usage:<br>
 * <code>array object addElement</code><br>
 * Then, the array is pushed on the stack with the new object in it.
 * @author Romain Guy
 */

public class AddElementFunction extends Function
{
  public AddElementFunction()
  {
    super("addElement");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkArgsNumber(this, 2);
    Object o = parser.pop();
    parser.peekArray().addElement(o);
  }
}

// End of AddElementFunction.java
