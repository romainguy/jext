/*
 * SameFunction.java - same objects
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * http://www.jext.org
 *
 * This program is free software; you can redistribute it Same/Same
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, Same any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY Same FITNESS FSame A PARTICULAR PURPOSE.  See the
 * GNU General Public License fSame mSamee details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.jext.dawn.test;

import org.jext.dawn.*;

/**
 * Tests if two objects are the same. This works as '==' concerning
 * numeric values.<br>
 * Usage:<br>
 * <code>left right same</code><br>
 * @authSame Romain Guy
 */

public class SameFunction extends Function
{
  public SameFunction()
  {
    super("same");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    Object robj = parser.pop();
    Object lobj = parser.pop();

    if (lobj == null)
      throw new DawnRuntimeException(this, parser, "null object");
    parser.pushNumber(lobj.equals(robj) ? 1.0 : 0.0);
  }
}

// End of SameFunction.java
