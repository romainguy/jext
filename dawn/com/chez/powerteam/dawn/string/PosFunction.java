/*
 * PosFunction.java - index of a string in another one
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

package com.chez.powerteam.dawn.string;

import com.chez.powerteam.dawn.*;

/**
 * Returns the index of a string in another one.<br>
 * Usage:<br>
 * <code>string1 string2 pos</code><br>
 * Returns index of string2 in string1.
 * @author Romain Guy
 */

public class PosFunction extends Function
{
  public PosFunction()
  {
    super("pos");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkArgsNumber(this, 2);
    String string2 = parser.popString();
    String string1 = parser.popString();
    parser.pushNumber(string1.indexOf(string2));
  }
}

// End of PosFunction.java
