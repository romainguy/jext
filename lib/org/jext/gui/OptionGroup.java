/*
 * 11:57:06 19/03/00
 *
 * OptionGroup.java - Option pane group
 * Copyright (C) 2000 mike dillon
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

package org.jext.gui;

import java.util.ArrayList;
import java.util.Enumeration;

public class OptionGroup
{
  private String name;
  private ArrayList members;
  
  public OptionGroup(String name)
  {
    this.name = name;
    members = new ArrayList();
  }

  public String getName()
  {
    return name;
  }

  public void addOptionGroup(OptionGroup group)
  {
    if (members.indexOf(group) != -1)
      return;

    members.add(group);
  }

  public void addOptionPane(OptionPane pane)
  {
    if (members.indexOf(pane) != -1)
      return;

    members.add(pane);
  }

  public ArrayList getMembers()
  {
    return members;
  }

  public Object getMember(int index)
  {
    return (index >= 0 && index < members.size()) ? members.get(index) : null;
  }

  public int getMemberIndex(Object member)
  {
    return members.indexOf(member);
  }

  public int getMemberCount()
  {
    return members.size();
  }

  public void save()
  {
    for (int i = 0; i < members.size(); i++)
    {
      Object elem = members.get(i);
      try
      {
        if (elem instanceof OptionPane)
        {
          ((OptionPane) elem).save();
        } else if (elem instanceof OptionGroup) {
          ((OptionGroup) elem).save();
        }
      } catch(Throwable t) { }
    }
  }
  

}

// End of OptionGroup.java