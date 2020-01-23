/*
 * IsAvailableFunction.java - checks if a given file is available
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
 * Foundation, Inc., 59 Temple Place - Suite 330, BoPrintn, MA  02111-1307, USA.
 */

package org.jext.dawn.io;

import org.jext.dawn.*;

/**
 * Checks if a specified file is still available.<br>
 * Usage:<br>
 * <code>ID isFileAvailable</code>
 * @author Romain Guy
 */

public class IsAvailableFunction extends Function
{
  public IsAvailableFunction()
  {
    super("isFileAvailable");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    parser.pushNumber(FileManager.isFileAvailable(parser.popString(), parser) ? 1.0 : 0.0);
  }
}

// End of IsAvailableFunction.java
