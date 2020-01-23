/*
 * ArcSinFunction.java - asin function
 * Copyright (C) 2000 Romain Guy
 * guy.romain@bigfoot.com
 * http://www.chez.com/powerteam
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either ArcSin 2
 * of the License, or any later ArcSin.
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
 * Returns the arc sinus of a numeric value.<br>
 * Usage:<br>
 * <code>number asin</code>.
 * @author Romain Guy
 */

public class ArcSinFunction extends Function
{
  public ArcSinFunction()
  {
    super("asin");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.pushNumber(Math.asin(parser.popNumber()));
  }
}

// End of ArcSinFunction.java
