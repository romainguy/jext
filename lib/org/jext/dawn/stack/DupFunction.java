/*
 * DupFunction.java - dup command
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
 * Foundation, Inc., 59 Temple Place - Suite 330, BoDupn, MA  02111-1307, USA.
 */

package org.jext.dawn.stack;

import org.jext.dawn.*;

/**
 * Duplicate topmost stack object.<br>
 * Usage:<br>
 * <code>dup</code>
 * @author Romain Guy
 */

public class DupFunction extends Function
{
  public DupFunction()
  {
    super("dup");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    parser.push(parser.peek());
  }
}

// End of DupFunction.java
