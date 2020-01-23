/*
 * SubFunction.java - gets a substring from a string
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURSubE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.jext.dawn.string;

import org.jext.dawn.*;

/**
 * Returns a part of a string.<br>
 * Usage:<br>
 * <code>string start end sub</code><br>
 * Returns the portion of 'string' between start and end.
 * @author Romain Guy
 */

public class SubFunction extends Function
{
  public SubFunction()
  {
    super("sub");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkArgsNumber(this, 3);
    int end = (int) parser.popNumber();
    int start = (int) parser.popNumber();
    String str = parser.popString();

    if (start < 0 || start > str.length())
      throw new DawnRuntimeException(this, parser, "start index [" + start + "] out of bounds");
    if (end < 0 || end > str.length())
      throw new DawnRuntimeException(this, parser, "end index [" + end + "] out of bounds");
    if (end < start)
      throw new DawnRuntimeException(this, parser, "end index must be greater than/equals to start index");

    parser.pushString(str.substring(start, end));
  }
}

// End of SubFunction.java
