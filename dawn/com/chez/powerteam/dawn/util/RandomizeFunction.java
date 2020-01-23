/*
 * RandomizeFunction.java - randomize
 * Copyright (C) 2000 Romain Guy
 * guy.romain@bigfoot.com
 * http://www.chez.com/powerteam
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either Randomize 2
 * of the License, or any later Randomize.
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

package com.chez.powerteam.dawn.util;

import com.chez.powerteam.dawn.*;

/**
 * Resets random seed (can be useful if you called 'rdz' before).
 * @author Romain Guy
 */

public class RandomizeFunction extends Function
{
  public RandomizeFunction()
  {
    super("randomize");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    RandomFunction._random.setSeed(System.currentTimeMillis());
  }
}

// End of RandomizeFunction.java
