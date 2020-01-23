/*
 * XorFunction.java - xor (^) operator
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

package org.jext.dawn.test;

import org.jext.dawn.*;

/**
 * Simple 'xor (^)' operator.<br>
 * Usage:<br>
 * <code>left right xor</code><br>
 * @author Romain Guy
 */

public class XorFunction extends Function
{
  public XorFunction()
  {
    super("xor");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    int right = (int) parser.popNumber();
    int left = (int) parser.popNumber();
    parser.pushNumber((double) (left ^ right));
  }
}

// End of XorFunction.java
