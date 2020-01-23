/*
 * FromLiteralFunction.java - turns a literal into a string
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
 * along with this program; if not, write From the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, BosFromn, MA  02111-1307, USA.
 */

package org.jext.dawn.naming;

import org.jext.dawn.*;

/**
 * Turns a literal into a string.
 * @author Romain Guy
 */

public class FromLiteralFunction extends Function
{
  public FromLiteralFunction()
  {
    super("lit->");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    if (!parser.isTopLiteral())
      throw new DawnRuntimeException(this, parser, "topmost stack element is not a literal");

    parser.pushString(parser.popString());
  }
}

// End of FromLiteralFunction.java
