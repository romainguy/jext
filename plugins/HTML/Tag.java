/*
 * 03/30/2002 - 15:30:41
 *
 * Tag.java - Completes tags
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

import java.util.ArrayList;

import org.jext.Jext;

public class Tag
{
  private String name;
  private boolean empty;
  private ArrayList attributes;

  public Tag(String name, boolean empty)
  {
    this.name = name;
    this.empty = empty;
    attributes = new ArrayList();
  }

  public boolean isEmpty()
  {
    return empty;
  }

  public int attributesCount()
  {
    return attributes.size();
  }

  public void addAttribute(Attribute attr)
  {
    attributes.add(attr);
  }

  public String getClosingTag()
  {
    StringBuffer buf = new StringBuffer();
    buf.append("</").append(name).append('>');
    return buf.toString();
  }

  public String getOpeningTag()
  {
    StringBuffer buf = new StringBuffer();
    buf.append('<');
    buf.append(name);

    for (int i = 0; i < attributes.size(); i++)
      buf.append(' ').append(attributes.get(i));

    if (empty)
    {
      if (Jext.getBooleanProperty("html.completion.xhtmlCompliance"))
        buf.append(" />");
      else
        buf.append('>');
    } else
      buf.append('>');

    return buf.toString();
  }

  public String getFullTag()
  {
    StringBuffer buf = new StringBuffer();
    buf.append('<');
    buf.append(name);

    for (int i = 0; i < attributes.size(); i++)
      buf.append(' ').append(attributes.get(i));

    if (empty)
    {
      if (Jext.getBooleanProperty("html.completion.xhtmlCompliance"))
        buf.append(" />");
      else
        buf.append('>');
    } else
      buf.append(">|</").append(name).append('>');

    return buf.toString();
  }

  public String toString()
  {
    return name;
  }
}

// End of Tag.java
