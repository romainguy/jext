/*
 * CodeSnippet.java - Coded snippet interface
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
 * The <code>CodeSnippet</code> class stands for an interface for Dawn code snippets.
 * A code snippet is a mini-script written in Dawn which will be used by
 * <code>DawnParser</code> to create a function. Code snippets avoid writting
 * annoying Java source code which can generate errors and be a pain to debug.
 * @author Romain Guy
 */

public abstract class CodeSnippet
{
  /**
   * Returns the function name. The returned name is the name which
   * must be called in a script to invoke the function.
   * @return A <code>String</code>, containing the name
   */

  public abstract String getName();

  /**
   * Returns the contexive help associated with this function.
   */

  public String getHelp()
  {
    return "";
  }

  /**
   * Returns the function code. This code must be written in Dawn scripting language.
   * A code snippet can contains any Dawn function and also use the 'needs' keyword
   * to request the installation of a specific package.
   * @return A <code>String</code>, containing the code
   */

  public abstract String getCode();
}

// End of CodeSnippet.java
