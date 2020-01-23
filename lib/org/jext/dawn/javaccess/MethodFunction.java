/*
 * MethodFunction.java - gets a method from a class and a string
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
 * Returns a method.<br>
 * Usage:<br>
 * <code>class string method</code><br>
 * Returns the method of the given class and declaration.
 * @author Guillaume Desnoix
 */

public class MethodFunction extends Function
{
  public MethodFunction()
  {
    super("method");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkArgsNumber(this, 2);
    String decl  = parser.popString();
    Object clazz = parser.pop();

    if (!(clazz instanceof Class))
      throw new DawnRuntimeException(this, parser, "" + clazz + " is not a class");

    Method r=null;
    try
    {
      Method[] methods = ((Class) clazz).getMethods();

      for(int i = 0; i < methods.length; i++)
      {
        Method  m = methods[i];
        Class[] p = m.getParameterTypes();
        StringBuffer d = new StringBuffer(m.getName() + "(");

        for(int j = 0; j < p.length; j++)
        {
          if (j > 0)
            d.append(',');
          d.append(p[j].getName());
        }
        d.append(')');

        if (decl.equals(d.toString()))
        {
          r = methods[i];
          break;
        }
      }

      if (r == null)
        throw new DawnRuntimeException(this, parser, "method " + decl + " can not be found");
    } catch(SecurityException ex) {
      throw new DawnRuntimeException(this, parser, "security violation");
    }

    // System.out.println("RESULT="+r);
    parser.push(r);
  }
}

// End of MethodFunction.java
