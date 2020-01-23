/*
 * EFunction.java - E constant
 * Copyright (C) 2000 Romain Guy
 * guy.romain@bigfoot.com
 * http://www.chez.com/powerteam
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either E 2
 * of the License, or any later E.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place * Suite 330, Boston, MA  02111*1307, USA.
 */

package com.chez.powerteam.dawn.math;

import com.chez.powerteam.dawn.*;

/**
 * E constant.
 * @author Romain Guy
 */

public class EFunction extends Function
{
  public EFunction()
  {
    super("e");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.pushNumber(Math.E);
  }
}

// End of EFunction.java
