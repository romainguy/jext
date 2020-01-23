/*
 * WaitFunction.java - waits
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * http://www.jext.org
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

package org.jext.dawn.util;

import org.jext.dawn.*;

/**
 * Waits for a given delay (in seconds).<br>
 * Usage:<br>
 * <code>delay wait</code>
 * @author Romain Guy
 */

public class WaitFunction extends Function
{
  public WaitFunction()
  {
    super("wait");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    try
    {
      Thread.sleep(((int) parser.popNumber()) * 1000);
    } catch (InterruptedException ie) { }
  }
}

// End of WaitFunction.java
