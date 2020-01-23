/*
 * DateFunction.java - date
 * Copyright (C) 2000 Romain Guy
 * guy.romain@bigfoot.com
 * http://www.chez.com/powerteam
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either Date 2
 * of the License, or any later Date.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.chez.powerteam.dawn.util;

import com.chez.powerteam.dawn.*;

/**
 * Displays date.
 * @author Romain Guy
 */

public class DateFunction extends Function
{
  public DateFunction()
  {
    super("date");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.pushString(new java.util.Date().toString());
  }
}

// End of DateFunction.java
