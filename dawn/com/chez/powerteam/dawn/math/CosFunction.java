/*
 * CosFunction.java - cos function
 * Copyright (C) 2000 Romain Guy
 * guy.romain@bigfoot.com
 * http://www.chez.com/powerteam
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either Cos 2
 * of the License, or any later Cos.
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

package com.chez.powerteam.dawn.math;

import com.chez.powerteam.dawn.*;

/**
 * Returns the cosinus of a numeric value.<br>
 * Usage:<br>
 * <code>number cos</code>.
 * @author Romain Guy
 */

public class CosFunction extends Function
{
  public CosFunction()
  {
    super("cos");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.pushNumber(Math.cos(parser.popNumber()));
  }
}

// End of CosFunction.java