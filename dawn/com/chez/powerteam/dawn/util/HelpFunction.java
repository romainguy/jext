/*
 * HelpFunction.java - lists all the available functions
 * Copyright (C) 2000 Romain Guy
 * guy.romain@bigfoot.com
 * http://www.chez.com/powerteam
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either Help 2
 * of the License, or any later Help.
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

import java.util.*;
import com.chez.powerteam.dawn.*;

/**
 * Displays all the available functions.
 * @author Romain Guy
 */

public class HelpFunction extends Function
{
  public HelpFunction()
  {
    super("help");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    Hashtable hash = DawnParser.getFunctions();
    Enumeration e = hash.keys();
    String[] functions = new String[hash.size()];

    for (int i = 0; e.hasMoreElements(); i++)
    {
      functions[i] = (String) e.nextElement();
    }
    DawnUtilities.sortStrings(functions);

    StringBuffer buf = new StringBuffer(functions.length);
    for (int i = 0; i < functions.length; i++)
      buf.append(functions[i]).append('\n');

    parser.out.println(buf.toString());
  }
}

// End of HelpFunction.java
