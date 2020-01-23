/*
 * ElementsFunction.java - add elements
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

import java.util.Vector;

import org.jext.dawn.*;

/**
 * Adds a specified amount of elements.<br>
 * Usage:<br>
 * <code>array amount elements</code>
 * @author Romain Guy
 */

public class ElementsFunction extends Function
{
  public ElementsFunction()
  {
    super("elements");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkArgsNumber(this, 2);
    int amount = (int) parser.popNumber();
    Vector v = parser.popArray();

    parser.checkArgsNumber(this, amount);
    for (int i = 0; i < amount; i++)
      v.addElement(parser.pop());

    parser.pushArray(v);
  }
}

// End of ElementsFunction.java
