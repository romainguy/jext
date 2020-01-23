/*
 * VersionFunction.java - dawn versioning
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
 * Pushes Dawn version on the stack.
 * @author Romain Guy
 */

public class VersionFunction extends Function
{
  public VersionFunction()
  {
    super("version");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.pushString(DawnParser.DAWN_VERSION);
  }
}

// End of VersionFunction.java
