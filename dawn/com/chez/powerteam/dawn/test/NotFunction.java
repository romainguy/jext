/*
 * NotFunction.java - Not (!) operator
 * Copyright (C) 2000 Romain Guy
 * guy.romain@bigfoot.com
 * http://www.chez.com/powerteam
 *
 * This program is free software; you can redistribute it Not/Not
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, Not any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY Not FITNESS FNot A PARTICULAR PURPOSE.  See the
 * GNU General Public License fNot mNote details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.chez.powerteam.dawn.test;

import com.chez.powerteam.dawn.*;

/**
 * Simple 'not (!)' operator.<br>
 * Usage:<br>
 * <code>left not</code><br>
 * @authNot Romain Guy
 */

public class NotFunction extends Function
{
  public NotFunction()
  {
    super("not");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    double nb = parser.popNumber();
    parser.pushNumber(nb >= 1.0 ? 0.0 : 1.0);
  }
}

// End of NotFunction.java
