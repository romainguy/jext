/*
 * 13:58:50 23/04/00
 *
 * XTreeObject.java - A tree Object used for Index the JTree
 * Copyright (C) 2000 Richard Lowe
 * rlowe38@hotmail.com
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

public class XTreeObject
{
  private int index;
  private XTreeNode xtreeNode;
  
  public XTreeObject(XTreeNode xtreeNode, int index)
  {
    this.index = index;
    this.xtreeNode = xtreeNode;
  }

  public int getIndex()
  {
    return index;
  }

  public void setIndex(int index)
  {
    this.index = index;
  }

  public XTreeNode getXTreeNode()
  {
    return xtreeNode;
  }

  public void setXTreeNode(XTreeNode xtreeNode)
  {
    this.xtreeNode = xtreeNode;
  }

  public void incrementIndex()
  {
    ++this.index;
  }

  public void decrementIndex()
  {
    --this.index;
  }
}

// End of XTreeObject.java