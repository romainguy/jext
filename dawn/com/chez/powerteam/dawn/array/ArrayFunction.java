/*
 * ArrayFunction.java - create a new array
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

package com.chez.powerteam.dawn.array;

import com.chez.powerteam.dawn.*;

/**
 * Creates a new array.<br>
 * Usage:<br>
 * <code>array</code>
 * @author Romain Guy
 */

public class ArrayFunction extends Function
{
  public ArrayFunction()
  {
    super("array");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.pushArray(new java.util.Vector());
  }
}

// End of ArrayFunction.java