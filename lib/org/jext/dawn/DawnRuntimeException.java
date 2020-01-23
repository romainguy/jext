/*
 * DawnRuntimeException.java - A runtime exception
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

package org.jext.dawn;

/**
 * <code>DawnRuntimeException</code> is thrown whenever a function encounter
 * an error. Basically, this exception is thrown if a function is not implemented.
 * Yet, many functions will use it to warn user (i.e: empty stack, not enough
 * arguments, etc...).
 */

public class DawnRuntimeException extends Exception
{
  /**
   * Create a new exception. The message is built here to avoid keeping pointers to many
   * other objects.
   * @param parser The <code>DawnParser</code> responsible of the invocation
   * @param message A short description of the error
   */

  public DawnRuntimeException(DawnParser parser, String message)
  {
    super("Error at line:" + parser.lineno() + ':' + message);
  }

  /**
   * Create a new exception. The message is built here to avoid keeping pointers to many
   * other objects.
   * @param function The <code>Function</code> which thrown the exception
   * @param parser The <code>DawnParser</code> responsible of the invocation
   * @param message A short description of the error
   */

  public DawnRuntimeException(Function function, DawnParser parser, String message)
  {
    super("Error at line:" + parser.lineno() + ((function == null) ? ":" : (':' + function.getName() + ':')) + message);
  }
}

// End of DawnRuntimeException.java

