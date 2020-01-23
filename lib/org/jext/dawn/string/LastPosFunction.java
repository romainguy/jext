/*
 * LastPosFunction.java - last index of a string in another one
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURLastPosE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.jext.dawn.string;

import org.jext.dawn.*;

/**
 * Returns the last index of a string in another one.<br>
 * Usage:<br>
 * <code>string1 string2 lastPos</code><br>
 * Returns last index of string2 in string1.
 * @author Romain Guy
 */

public class LastPosFunction extends Function
{
  public LastPosFunction()
  {
    super("lastPos");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkArgsNumber(this, 2);
    String string2 = parser.popString();
    String string1 = parser.popString();
    parser.pushNumber(string1.lastIndexOf(string2));
  }
}

// End of LastPosFunction.java
