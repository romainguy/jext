/*
 * NewFunction.java - creates a new instance
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
 * Create an object.
 * Usage:<br>
 * <code>class new</code><br>
 * Returns the new instance.
 * @author Guillaume Desnoix
 */

public class NewFunction extends Function
{
  public NewFunction()
  {
    super("new");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    Object o = parser.pop();

    if (o instanceof Class)
      useDefaultConstructor(parser, (Class)o);
    else
    if (o instanceof Constructor)
      invokeConstructor(parser, (Constructor)o);
    else
      throw new DawnRuntimeException
	(this, parser, "" + o + " is not a class or a constructor");
  }

  private void useDefaultConstructor(DawnParser parser, Class clazz)
       throws DawnRuntimeException
  {
    try
    {
      Object r = clazz.newInstance();

      if (r != null)
      {
        parser.push(r);
	// System.out.println("RESULT="+r);
      }
    } catch(IllegalAccessException ex) {
      throw new DawnRuntimeException(this, parser, "illegal access");
    } catch(InstantiationException ex) {
      throw new DawnRuntimeException(this, parser, "instantiation failed");
    }
  }

  private void invokeConstructor(DawnParser parser, Constructor c)
       throws DawnRuntimeException
  {
    Class[] t = c.getParameterTypes();
    int     n = t.length;
    parser.checkArgsNumber(this, n);

    // this code is the same than invoke() in InvokeFunction.

    try
    {
      Object[] p = new Object[n];
      for(int i = n - 1; i >= 0; i--)
        p[i] = parser.pop();

      for(int i = 0; i < n; i++)
      {
        if (p[i] == NullFunction.NULL)
          p[i] = null;
        else if ((t[i] == Integer.TYPE) || (t[i] == Integer.class))
        {
	  if(p[i] instanceof Number)
	    p[i]=new Integer(((Number)p[i]).intValue());
	}
        else if ((t[i] == Boolean.TYPE) || (t[i] == Boolean.class))
        {
          if (p[i] instanceof Number)
            p[i] = ((((Number) p[i]).doubleValue() != 0.0) ? Boolean.TRUE : Boolean.FALSE);
          else if (!"\"\"".equals(p[i]))
            p[i] = Boolean.TRUE;
          else
            p[i] = Boolean.FALSE;
        } else if (t[i] == String.class) {
          String s = "" + p[i];
          int    l = s.length();

          if ((l >= 2) && (s.charAt(0) == '\"') && (s.charAt(l - 1) == '\"'))
            s = s.substring(1, l - 1);
          p[i] = s;
        } else if ((t[i] == Float.TYPE) || (t[i] == Float.class)) {
          if (p[i] instanceof Number)
            p[i] = new Float(((Number) p[i]).floatValue());
        } else if ((t[i] == Character.TYPE) || (t[i] == Character.class)) {
          if (p[i] instanceof Number)
            p[i] = new Character((char) ((Number) p[i]).intValue());
        }
        else if ((t[i] == Short.TYPE) || (t[i] == Short.class))
        {
	  if(p[i] instanceof Number)
	    p[i]=new Short(((Number)p[i]).shortValue());
	}
      }

      parser.push(c.newInstance(p));

    } catch(IllegalAccessException ex) {
      throw new DawnRuntimeException(this, parser, "illegal access");
    } catch(InstantiationException ex) {
      throw new DawnRuntimeException(this, parser, "instantiation failed");
    } catch(InvocationTargetException ex) {
      throw new DawnRuntimeException
	(this, parser, "invocation failed: " +
	 ex.getTargetException().getMessage());	
    }
  }
}

// End of NewFunction.java
