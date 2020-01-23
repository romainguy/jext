/*
 * ConstructorFunction.java - gets a constructor from a class and a string
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
import java.lang.reflect.*;

/**
 * Returns a constructor.<br>
 * Usage:<br>
 * <code>class string constructor</code><br>
 * Returns the constructor of the given class and declaration.
 * @author Guillaume Desnoix
 */

public class ConstructorFunction extends Function
{
  public ConstructorFunction()
  {
    super("constructor");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkArgsNumber(this, 2);
    String decl  = parser.popString();
    Object clazz = parser.pop();

    if (!(clazz instanceof Class))
      throw new DawnRuntimeException(this, parser, "" + clazz + " is not a class");

    Constructor r=null;
    try
    {
      Constructor[] constructors = ((Class) clazz).getConstructors();

      for(int i = 0; i < constructors.length; i++)
      {
        Constructor  m = constructors[i];
        Class[] p = m.getParameterTypes();
        StringBuffer d = new StringBuffer("(");

        for(int j = 0; j < p.length; j++)
        {
          if (j > 0)
            d.append(',');
          d.append(p[j].getName());
        }
        d.append(')');

        if (decl.equals(d.toString()))
        {
          r = constructors[i];
          break;
        }
      }

      if (r == null)
        throw new DawnRuntimeException(this, parser, "constructor " + decl + " can not be found");
    } catch(SecurityException ex) {
      throw new DawnRuntimeException(this, parser, "security violation");
    }

    // System.out.println("RESULT="+r);
    parser.push(r);
  }
}

// End of ConstructorFunction.java
