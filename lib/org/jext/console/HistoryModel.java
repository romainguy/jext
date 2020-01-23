/*
 * 19:31:13 31/01/00
 *
 * HistoryModel.java - An HistoryModel for the Console
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

package org.jext.console;

import java.util.*;

public class HistoryModel
{
  // Private members
  private int max;
  private Vector data;
  
  /**
   * Creates a new history model, seizing it
   * according to the specified size.
   * @param max The maximum numbers of items this history can hold
   */

  public HistoryModel(int max)
  {
    this.max = max;
    data = new Vector(max);
  }

  /**
   * When the user validate a new entry, we add it to the
   * history.
   * @param text The String to be added to the history
   */

  public void addItem(String text)
  {
    if (text == null || text.length() == 0) return;

    int index = data.indexOf(text);
    if (index != -1)
      data.removeElementAt(index);
    data.insertElementAt(text, 0);

    if (getSize() > max)
      data.removeElementAt(getSize() - 1);
  }

  /**
   * When user press UP or DOWN, we need to get
   * a previous typed String, stored in the Vector.
   * @param index The index of the String to get
   * @return A String corresponding to a previous entry
   */

  public String getItem(int index)
  {
    return (String) data.elementAt(index);
  }

  /**
   * As the user can use arrows to get up and down
   * in the list, we need to know its max capacity.
   * @return Maximum capacity of the history
   */

  public int getSize()
  {
    return data.size();
  }

  /**
   * We can need to add an item directly at the end of
   * the list.
   * @param item The String to add at the end
   */

  private void addItemToEnd(String item)
  {
    data.addElement(item);
  }
  

}

// End of HistoryModel.java