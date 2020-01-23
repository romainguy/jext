/*
 * ClassFunction.java - gets a class from a string
 * Copyright (C) 2000 Guillaume Desnoix
 * guillaume-desnoix@memoire.com
 * http://www.memoire.com/guillaume-desnoix/
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURSubE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.jext.dawn.javaccess;

import org.jext.dawn.*;

/**
 * Returns a class.<br>
 * Usage:<br>
 * <code>string class</code><br>
 * Returns the class of the given name.
 * @author Guillaume Desnoix
 */

public class ClassFunction extends Function
{
  public ClassFunction()
  {
    super("class");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    String name = parser.popString();

    Class r = null;

    try
    {
      r = Class.forName(name);
    } catch(ClassNotFoundException ex) {
      throw new DawnRuntimeException(this, parser, "class " + name + " can not be found");
    }

    // System.out.println("RESULT="+r);
    parser.push(r);
  }
}

// End of ClassFunction.java
