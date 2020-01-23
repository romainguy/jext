/*
 * InsertElementAtFunction.java - insert element at
 * Copyright (C) 2000 Romain Guy
 * guy.romain@bigfoot.com
 * http://www.chez.com/powerteam
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

package com.chez.powerteam.dawn.array;

import com.chez.powerteam.dawn.*;

/**
 * Inserts the element at given index.<br>
 * Usage:<br>
 * <code>array object index insertElementAt</code>
 * @author Romain Guy
 */

public class InsertElementAtFunction extends Function
{
  public InsertElementAtFunction()
  {
    super("insertElementAt");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkArgsNumber(this, 3);
    int index = (int) parser.popNumber();
    Object o = parser.pop();

    try
    {
      parser.peekArray().insertElementAt(o, index);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      throw new DawnRuntimeException(this, parser, "array index " + index + " out of bounds");
    }
  }
}

// End of InsertElementAtFunction.java
