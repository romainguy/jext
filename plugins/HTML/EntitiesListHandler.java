/*
 * 08/19/2001 - 00:01:24
 *
 * EntitiesListHandler.java - Handles tags list files for Jext
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

public class EntitiesListHandler extends HandlerBase
{
  private Entity entity;
  private String name;
  
  public void attribute(String aname, String value, boolean isSpecified)
  {
    if (aname.equalsIgnoreCase("NAME"))
      name = value;
  }

  public void startElement(String name)
  {
    if (name.equalsIgnoreCase("ENTITY"))
      entity = new Entity(this.name);
  }

  public void endElement(String name)
  {
    if (name.equalsIgnoreCase("ENTITY"))
    {
      TagsCompletion.entitiesList.add(entity);
      entity = null;
    }

    name = null;
  }

  public void startDocument()
  {
    TagsCompletion.entitiesList = new Vector();
  }
}

// End of EntitiesListHandler.java
