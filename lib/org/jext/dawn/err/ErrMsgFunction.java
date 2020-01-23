/*
 * ErrMsgFunction.java - last error message
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
 * Put last error message on stack.<br>
 * Usage:<br>
 * <code>errMsg</code>
 * @author Romain Guy
 */

public class ErrMsgFunction extends Function
{
  public ErrMsgFunction()
  {
    super("errMsg");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    DawnRuntimeException dre = ErrManager.getErr(parser);
    if (dre != null)
      parser.pushString(dre.getMessage());
    else
      parser.pushString("no error has occured");
  }
}

// End of ErrMsgFunction.java
