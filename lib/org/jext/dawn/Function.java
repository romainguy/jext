/*
 * Function.java - Function Interface
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
 * The <code>Function</code> class defines the standard framework of any
 * Dawn function. A function is defined by its name, the one which will
 * be used to call it from scripts. The function also provides an
 * <code>invoke()</code> method which can throw a <code>DawnRuntimeException</code>
 * on error.
 * @author Romain Guy
 */

public abstract class Function
{
  // function name
  // this name will be used to call the function
  private String name;

  /**
   * Creates a new Dawn function, unnamed.
   */

  public Function()
  {
  }

  /**
   * Creates a new Dawn function.
   * @param name The function name
   */

  public Function(String name)
  {
    this.name = name;
  }

  /**
   * Returns the help associated with this function.
   */

  public String getHelp()
  {
    return "";
  }

  /**
   * Returns the function name. The returned name is the name which
   * must be called in a script to invoke the function.
   * @return A <code>String</code>, containing the name
   */

  public String getName()
  {
    return name;
  }

  /**
   * Executes the function. Usually, the function should make use of
   * the <code>DawnParser</code> given as parameter.
   * @param parser The <code>DawnParser</code> which inoked the function
   * @throws A <code>DawnRuntimeException</code> if an error occures
   */

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    throw new DawnRuntimeException(this, parser, "function is not implemented");
  }
}

// End of Function.java
