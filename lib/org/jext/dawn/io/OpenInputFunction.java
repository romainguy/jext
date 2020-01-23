/*
 * OpenInputFunction.java - opens a file
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
 * Opens a file from the HD. The opened file is designed by an ID.<br>
 * Usage:<br>
 * <code>file ID openForInput</code><br>
 * Where file and ID are both string. First one is the path to the file -
 * which can be either absolute, either relative -, and the second one
 * is the file ID.
 * @author Romain Guy
 */

public class OpenInputFunction extends Function
{
  public OpenInputFunction()
  {
    super("openForInput");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkArgsNumber(this, 2);
    FileManager.openFileForInput(parser.popString(), parser.popString(), this, parser);
  }
}

// End of OpenInputFunction.java
