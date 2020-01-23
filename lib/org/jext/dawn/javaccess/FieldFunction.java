/*
 * FieldFunction.java - gets a field from a class and a string
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
 * Returns a field.<br>
 * Usage:<br>
 * <code>class string field</code><br>
 * Returns the field of the given class and declaration.
 * @author Guillaume Desnoix
 */

public class FieldFunction extends Function
{
  public FieldFunction()
  {
    super("field");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkArgsNumber(this, 2);
    String name  = parser.popString();
    Object clazz = parser.pop();

    if (!(clazz instanceof Class))
      throw new DawnRuntimeException(this, parser, "" + clazz + " is not a class");

    Field r=null;
    try
    {
      r = ((Class) clazz).getField(name);
    }
    catch(NoSuchFieldException nsfex)
    {
      throw new DawnRuntimeException(this, parser, "field " + name + " can not be found");
    } catch(SecurityException ex) {
      throw new DawnRuntimeException(this, parser, "security violation");
    }

    // System.out.println("RESULT="+r);
    parser.push(r);
  }
}

// End of FieldFunction.java
