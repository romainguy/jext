/*
 * RandomFunction.java - random
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * http://www.jext.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either Random 2
 * of the License, or any later Random.
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

package org.jext.dawn.util;

import java.util.Random;
import org.jext.dawn.*;

/**
 * Returns a random number.
 * @author Romain Guy
 */

public class RandomFunction extends Function
{
  public static Random _random = new Random();

  public RandomFunction()
  {
    super("rand");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.pushNumber(_random.nextDouble());
  }
}

// End of RandomFunction.java
