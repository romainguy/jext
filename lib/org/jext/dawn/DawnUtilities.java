/*
 * 21:16:50 16/04/00
 *
 * DawnUtilities.java - Some utilities for Jext and its classes
 * Copyright (C) 1999-2000 Romain Guy
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

package org.jext.dawn;

import org.jext.Utilities;

/**
 * This class contains some utility methods needed by Dawn or its functions.
 */

public class DawnUtilities extends Utilities
{
  public static String unescape(String in)
  {
    StringBuffer buf = new StringBuffer(in.length());
    char c = '\0';
    for (int i = 0; i < in.length(); i++)
    {
      switch(c = in.charAt(i))
      {
        case '\\':
          buf.append('\\');
          buf.append('\\');
          break;
        case '\"':
          buf.append('\\');
          buf.append('"');
          break;
        case '\'':
          buf.append('\\');
          buf.append('\'');
          break;
        case '\n':
          buf.append('\\');
          buf.append('n');
          break;
        case '\r':
          buf.append('\\');
          buf.append('r');
          break;
        default:
          buf.append(c);
      }
    }
    return buf.toString();
  }

  /**
   * Parsers a string and turn common escape sequences into special chars like
   * \n (carriage return) \t (tab space) \\ (single \ character) ...
   * @param in The <code>String</code> to be parsed
   */

  public static String escape(String in)
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
              case '"':
                _out.append('\"');
                break;
              case '\'':
                _out.append('\'');
                break;
              case '\\':
                _out.append('\\');
                break;
              default:
                _out.append('\\').append(p);
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
}

// End of DawnUtilities.java
