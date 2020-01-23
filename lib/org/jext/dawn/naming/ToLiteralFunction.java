/*
 * ToLiteralFunction.java - turns a String into a literal
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

package org.jext.dawn.naming;

import org.jext.dawn.*;

/**
 * Turns a string into a literal
 * @author Romain Guy
 */

public class ToLiteralFunction extends Function
{
  public ToLiteralFunction()
  {
    super("->lit");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    if (!parser.isTopString())
      throw new DawnRuntimeException(this, parser, "topmost stack element is not a string");

    parser.push(parser.popString());
  }
}

// End of ToLiteralFunction.java
