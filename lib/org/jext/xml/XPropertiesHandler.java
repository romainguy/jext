/*
 * 19:23:31 18/01/00
 *
 * XPropertiesHandler.java - Handles XML-Properties files for Jext
 * Copyright (C) 1999 Romain Guy
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
import java.util.Properties;
import org.jext.*;

public class XPropertiesHandler extends HandlerBase
{
  // private members
  private Properties props;
  private String pName, pValue;
  
  public XPropertiesHandler() { }

  private String parse(String in)
  {
    StringBuffer _out = new StringBuffer(in.length());
    char c = '\0';
    for (int i = 0; i < in.length(); i++)
    {
      switch(c = in.charAt(i))
      {
        case '\\':
          if (i < in.length() - 1)
          {
            char p = '\0';
            switch(p = in.charAt(++i))
            {
              case 'n':
                _out.append('\n');
                break;
              case 'r':
                _out.append('\r');
                break;
              case 't':
                _out.append('\t');
                break;
              case 'w':
                _out.append(' ');
                break;
              case '\\':
                _out.append('\\');
                break;
            }
          } else
            _out.append(c);
          break;
        default:
          _out.append(c);
          break;
      }
    }
    return _out.toString();
  }


  public void attribute(String aname, String value, boolean isSpecified)
  {
    if (aname.equalsIgnoreCase("VALUE"))
      pValue = parse(value);
    else if (aname.equalsIgnoreCase("NAME"))
      pName = value;
  }

  public void doctypeDecl(String name, String publicId, String systemId) throws Exception
  {
    if (!"xproperties".equalsIgnoreCase(name))
      throw new Exception("Not a valid XProperties file !");
  }

  public void charData(char[] c, int off, int len)
  {
  }

  public void startElement(String name)
  {
  }

  public void endElement(String name)
  {
    if (name == null) return;
    if (name.equalsIgnoreCase("PROPERTY"))
    {
      if (pName != null && pValue != null)
      {
        props.put(pName, pValue);
        pName = pValue = null;
      }
    }
  }

  public void startDocument()
  {
    props = Jext.getProperties();
  }
}

// End of XPropertiesHandler.java