/*
 * NullFunction.java - returns null
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
 * Return NULL.
 * Usage:<br>
 * <code>null</code><br>
 * Returns NULL.
 * @author Guillaume Desnoix
 */

public class NullFunction extends Function
{
  public final static Object NULL = new Object()
  {
    public String toString() { return "null"; }
  };

  public NullFunction()
  {
    super("null");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.push(NULL);
  }
}

// End of NullFunction.java
