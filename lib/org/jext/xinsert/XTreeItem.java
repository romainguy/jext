/*
 * 01/12/2001 - 19:31:29
 *
 * XTreeItem.java - 
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

package org.jext.xinsert;

public class XTreeItem
{
  public static final int PLAIN = 0;
  public static final int SCRIPT = 1;
  public static final int MIXED = 2;

  private int type;
  private String content;
  
  public XTreeItem(String content)
  {
    this(content, 0);
  }

  public XTreeItem(String content, int type)
  {
    this.content = content;
    this.type = type;
  }

  public boolean isMixed()
  {
    return type == MIXED;
  }

  public boolean isScript()
  {
    return type == SCRIPT;
  }

  public int getType()
  {
    return type;
  }

  public void setType(int type)
  {
    this.type = type;
  }

  public String getContent()
  {
    return content;
  }

  public void setContent(String content)
  {
    this.content = content;
  }
}

// End of XTreeItem.java