/*
 * EndsWithFunction.java - test the end of a string
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.jext.dawn.string;

import org.jext.dawn.*;

/**
 * Test if a string ends with another one.<br>
 * Usage:<br>
 * <code>string1 string2 endsWith</code><br>
 * Returns 1.0 if string1 ends with string2.
 * @author Romain Guy
 */

public class EndsWithFunction extends Function
{
  public EndsWithFunction()
  {
    super("endsWith");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkArgsNumber(this, 2);
    String string2 = parser.popString();
    String string1 = parser.popString();
    parser.pushNumber(string1.endsWith(string2) ? 1.0 : 0.0);
  }
}

// End of EndsWithFunction.java
