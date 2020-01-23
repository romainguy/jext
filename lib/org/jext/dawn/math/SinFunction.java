/*
 * SinFunction.java - sin function
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * http://www.jext.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either Sin 2
 * of the License, or any later Sin.
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

package org.jext.dawn.math;

import org.jext.dawn.*;

/**
 * Returns the sinus of a numeric value.<br>
 * Usage:<br>
 * <code>number sin</code>.
 * @author Romain Guy
 */

public class SinFunction extends Function
{
  public SinFunction()
  {
    super("sin");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.pushNumber(Math.sin(parser.popNumber()));
  }
}

// End of SinFunction.java
