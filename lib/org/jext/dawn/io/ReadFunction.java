/*
 * ReadFunction.java - reads from an opened file
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
 * Reads from an opened file, designed by its ID.<br>
 * Usage:<br>
 * <code>ID read</code>
 * @author Romain Guy
 */

public class ReadFunction extends Function
{
  public ReadFunction()
  {
    super("read");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    String _char = FileManager.read(parser.popString(), this, parser);
    if (_char != null)
      parser.pushString(_char);
  }
}

// End of ReadFunction.java
