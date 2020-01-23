/*
 * 08/26/2001 - 14:58:11
 *
 * PyActionsHandler.java - Handles PyActions files for Jext
 * Copyright (C) 2001 Romain Guy
 * romain.guy@jext.org
 * www.jext.org
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.jext.xml;

import com.microstar.xml.*;
import org.jext.*;

public class PyActionsHandler extends HandlerBase
{
  // private members
  private boolean pEditAction;
  private String pName, pValue;

  public PyActionsHandler() { }

  public void attribute(String aname, String value, boolean isSpecified)
  {
    if (aname.equalsIgnoreCase("NAME"))
      pName = value;
    else if (aname.equalsIgnoreCase("EDIT"))
      pEditAction = "yes".equals(value);
  }

  public void doctypeDecl(String name, String publicId, String systemId) throws Exception
  {
    if (!"pyactions".equalsIgnoreCase(name))
      throw new Exception("Not a valid PyActions file !");
  }

  public void charData(char[] c, int off, int len)
  {
    pValue = new String(c, off, len);
  }

  public void endElement(String name)
  {
    if (name == null)
      return;
    if (name.equalsIgnoreCase("ACTION"))
    {
      if (pName != null && pValue != null)
      {
        Jext.addPythonAction(pName, pValue, pEditAction);
        pName = pValue = null;
      }
    }
  }
}

// End of PyActionsHandler.java
