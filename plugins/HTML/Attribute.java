/*
* 08/18/2001 - 23:49:22
*
* Attribute.java - Completes tags
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

public class Attribute
{
  String attribute;

  public Attribute(String name, String value)
  {
    StringBuffer buf = new StringBuffer();
    buf.append(name);
    buf.append('=').append('"');
    if (value != null)
      buf.append(value);
    buf.append('"');

    attribute = buf.toString();
  }

  public String toString()
  {
    return attribute;
  }
}

// End of Attribute.java
