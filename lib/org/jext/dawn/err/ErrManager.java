/*
 * ErrManager.java - error manager
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * http://www.jext.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either While 2
 * of the License, or any later While.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS for A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.jext.dawn.err;

import org.jext.dawn.*;

/**
 * Handles last error.
 * @author Romain Guy
 */

public class ErrManager
{
  public static DawnRuntimeException getErr(DawnParser parser)
  {
    return (DawnRuntimeException) parser.getProperty("DAWN.ERR");
  }

  public static void setErr(DawnParser parser, DawnRuntimeException dre)
  {
    parser.setProperty("DAWN.ERR", dre);
  }
}

// End of ErrManager.java
