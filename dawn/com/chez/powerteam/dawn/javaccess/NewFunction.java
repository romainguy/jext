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

package com.chez.powerteam.dawn.javaccess;

import com.chez.powerteam.dawn.*;
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
    parser.checkArgsNumber(this, 1);
    Object o = parser.pop();

    if (!(o instanceof Class))
      throw new DawnRuntimeException(this, parser, "" + o + " is not a class");

    Object r = null;

    try
    {
      r = ((Class) o).newInstance();

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
}

// End of NewFunction.java
