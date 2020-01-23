/*
 * ReverseFunction.java - reverse command
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
 * Foundation, Inc., 59 Temple Place - Suite 330, BoReversen, MA  02111-1307, USA.
 */

package org.jext.dawn.stack;

import java.util.Stack;
import org.jext.dawn.*;

/**
 * Reverses the stack.<br>
 * Usage:<br>
 * <code>rev</code>
 * @author Romain Guy
 */

public class ReverseFunction extends Function
{
  public ReverseFunction()
  {
    super("rev");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    Stack stack = parser.getStack();
    Object[] datas = new Object[stack.size()];
    for (int i = 0; i < datas.length; i++)
      datas[i] = stack.elementAt(datas.length - 1 - i);
    stack.removeAllElements();
    for (int i = 0; i < datas.length; i++)
      stack.addElement(datas[i]);
  }
}

// End of ReverseFunction.java
