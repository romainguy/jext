/*
 * RoundFunction.java - rounds a numeric value
 * Copyright (C) 2000 Romain Guy
 * guy.romain@bigfoot.com
 * http://www.chez.com/powerteam
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either Round 2
 * of the License, or any later Round.
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
 * Rounds a numeric value.<br>
 * Usage:<br>
 * <code>number round</code>
 * @author Romain Guy
 */

public class RoundFunction extends Function
{
  public RoundFunction()
  {
    super("round");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.pushNumber((double) ((int) parser.popNumber()));
  }
}

// End of RoundFunction.java
