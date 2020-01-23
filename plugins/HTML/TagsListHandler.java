/*
 * 08/19/2001 - 00:01:24
 *
 * TagsListHandler.java - Handles tags list files for Jext
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

import com.microstar.xml.*;

import java.util.Vector;

public class TagsListHandler extends HandlerBase
{
  private Tag tag;
  private boolean empty;
  private String name, value;
  
  public void attribute(String aname, String value, boolean isSpecified)
  {
    if (aname.equalsIgnoreCase("NAME"))
      name = value;
    else if (aname.equalsIgnoreCase("EMPTY"))
      empty = value.equals("true");
    else if (aname.equalsIgnoreCase("VALUE"))
      this.value = value;
  }

  public void startElement(String name)
  {
    if (name.equalsIgnoreCase("TAG"))
      tag = new Tag(this.name, empty);
    else if (name.equalsIgnoreCase("ATTRIBUTE"))
    {
      if (tag != null)
        tag.addAttribute(new Attribute(this.name, value));
    }
  }

  public void endElement(String name)
  {
    if (name.equalsIgnoreCase("TAG"))
    {
      TagsCompletion.tagsList.add(tag);
      tag = null;
    }

    name = null;
    value = null;
    empty = false;
  }

  public void startDocument()
  {
    TagsCompletion.tagsList = new Vector();
  }
}

// End of TagsListHandler.java
