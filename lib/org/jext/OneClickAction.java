/*
 * 12/26/2000 - 19:28:07
 *
 * OneClickAction.java - Interface for one click menu items
 * Copyright (C) 2000 Romain Guy
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

package org.jext;

import java.awt.event.ActionEvent;

/**
 * This class handle One Click! actions. Such an action is performed
 * on each mouse click within text area. There are two ways to create
 * an One Click! action: hard coding the action or calling an existing
 * action.
 */

public class OneClickAction extends MenuAction
{
  private MenuAction action;
  
  public OneClickAction(String name)
  {
    super(name);
  }

  public OneClickAction(String name, String action)
  {
    super(name);
    this.action = Jext.getAction(action);
  }

  public void actionPerformed(ActionEvent evt)
  {
    getTextArea(evt).setOneClick(this, evt);
  }

  public void oneClickActionPerformed(ActionEvent evt)
  {
    if (action != null)
      action.actionPerformed(evt);
  }
  

}

// End of OneClickAction.java