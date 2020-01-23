/*
 * ArcTanFunction.java - atan function
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * http://www.jext.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either ArcTan 2
 * of the License, or any later ArcTan.
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
 * Returns the arc tangeant of a numeric value.<br>
 * Usage:<br>
 * <code>number atan</code>.
 * @author Romain Guy
 */

public class ArcTanFunction extends Function
{
  public ArcTanFunction()
  {
    super("atan");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.pushNumber(Math.atan(parser.popNumber()));
  }
}

// End of ArcTanFunction.java
