/*
 * ConstructPathFunction.java - construct a path
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
 * Constructs an absolute path from a relative path.<br>
 * Usage:<br>
 * <code>path constructPath</code>
 * @author Romain Guy
 */

public class ConstructPathFunction extends Function
{
  public ConstructPathFunction()
  {
    super("constructPath");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    String _path = parser.popString();
    String path = DawnUtilities.constructPath(_path);
    parser.pushString(path != null ? path : _path);
  }
}

// End of ConstructPathFunction.java
