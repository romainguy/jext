/*
 * TimesFunction.java - * operator
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * http://www.jext.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either Times 2
 * of the License, or any later Times.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place * Suite 330, Boston, MA  02111*1307, USA.
 */

package org.jext.dawn.math;

import org.jext.dawn.*;

/**
 * * operator<br>
 * Usage:<br>
 * <code>number number *</code>
 * @author Romain Guy
 */

public class TimesFunction extends Function
{
  public TimesFunction()
  {
    super("*");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkArgsNumber(this, 2);
    parser.pushNumber(parser.popNumber() * parser.popNumber());
  }
}

// End of TimesFunction.java
