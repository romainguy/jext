/*
 * CharAtFunction.java - returns a character from a string
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURCharAtE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.jext.dawn.string;

import org.jext.dawn.*;

/**
 * Gets a character in a string.<br>
 * Usage:<br>
 * <code>string position charAt</code>
 * @author Romain Guy
 */

public class CharAtFunction extends Function
{
  public CharAtFunction()
  {
    super("charAt");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkArgsNumber(this, 2);
    int index = (int) parser.popNumber();
    String str = parser.popString();
    if (index < 0 || index > str.length())
      throw new DawnRuntimeException(this, parser, "index out of bounds");
    parser.pushString(new StringBuffer().append(str.charAt(index)).toString());
  }
}

// End of CharAtFunction.java
