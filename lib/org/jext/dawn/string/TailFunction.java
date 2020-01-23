/*
 * TailFunction.java - remove first character of a string
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURTailE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.jext.dawn.string;

import org.jext.dawn.*;

/**
 * Get first char of a string.<br>
 * Usage:<br>
 * <code>string Tail</code>
 * @author Romain Guy
 */

public class TailFunction extends Function
{
  public TailFunction()
  {
    super("tail");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    String str = parser.popString();
    if (str.length() != 0)
      parser.pushString(str.substring(1, str.length()));
  }
}

// End of TailFunction.java
