/*
 * ChrFunction.java - turns an ascii value into a single string character
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURChrE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.jext.dawn.string;

import org.jext.dawn.*;

/**
 * Turns a numeric value into a single character string.<br>
 * Usage:<br>
 * <code>number chr</code>
 * @author Romain Guy
 */

public class ChrFunction extends Function
{
  public ChrFunction()
  {
    super("chr");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    parser.pushString(new StringBuffer().append((char) parser.popNumber()).toString());
  }
}

// End of ChrFunction.java
