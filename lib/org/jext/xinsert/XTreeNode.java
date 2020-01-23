/*
 * 17:09:52 05/09/00
 *
 * XTreeNode.java - Part of the XTree system
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

import java.util.StringTokenizer;

import javax.swing.tree.DefaultMutableTreeNode;

public class XTreeNode extends DefaultMutableTreeNode
{
  private int pos = -1;
  private String modes;
  
  public XTreeNode(String userObject)
  {
    super(userObject);
  }

  public XTreeNode(String userObject, String modes)
  {
    super(userObject);
    this.modes = modes;
  }

  public XTreeNode(String userObject, String modes, int pos)
  {
    super(userObject);
    this.modes = modes;
    this.pos = pos;
  }

  public int getIndex()
  {
    return pos;
  }

  public void setIndex(int pos)
  {
    this.pos = pos;
  }
  
  public boolean isPermanent() 
  {
    return (modes == null);
  }
  
  public boolean isAssociatedToMode(String mode)
  {
    if (modes == null)
      return true;
    else
    {
      StringTokenizer token = new StringTokenizer(modes);
      while (token.hasMoreTokens())
      {
        if (token.nextToken().equals(mode))
          return true;
      }

      return false;
    }
  }
}
// End of XTreeNode.java