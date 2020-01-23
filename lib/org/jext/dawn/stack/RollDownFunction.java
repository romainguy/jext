/*
 * RollDownFunction.java - roll down command
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
 * Foundation, Inc., 59 Temple Place - Suite 330, BoRolln, MA  02111-1307, USA.
 */

package org.jext.dawn.stack;

import java.util.Stack;
import org.jext.dawn.*;

/**
 * Rotates the n first elements of the stack by putting each one to the previous
 * level but the first one which goes at last pos.<br>
 * Usage:<br>
 * <code>number rolld</code>
 * @author Romain Guy
 */

public class RollDownFunction extends Function
{
  public RollDownFunction()
  {
    super("rolld");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    int levels = (int) parser.popNumber();
    if (levels == 0)
      return;
    parser.checkLevel(this, levels - 1);

    Stack stack = parser.getStack();
    Object[] datas = new Object[levels];

    datas[0] = stack.lastElement();
    for (int i = 1; i < levels; i++)
      datas[i] = stack.elementAt(stack.size() - levels + i - 1);

    for (int i = 0; i < levels; i++)
      stack.setElementAt(datas[i], stack.size() - levels + i);
  }
}

// End of RollDownFunction.java
