/*
 * 08/01/2002 - 14:35:04
 *
 * TabSwitcher.java
 * Copyright (C) 2002 Matt Benson
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

package org.jext.misc;

import java.awt.event.ActionEvent;

import org.jext.MenuAction;
import org.jext.JextTabbedPane;

/**
 * Switches tabs due to the failure of JRE 1.4 to properly do so.
 *
 * @author <a href="mailto:orangeherbert@users.sourceforge.net">Matt Benson</a>
 */
public class TabSwitcher extends MenuAction
{
  private boolean right;

/**
 * Construct a new <CODE>TabSwitcher</CODE>.
 * @param right   whether to switch the selected tab index right or left.
 */
  public TabSwitcher(boolean right)
  {
    super(new StringBuffer("TabSwitcher_").append(
		 (right) ? "left" : "right").toString());
    this.right = right;
  }//end constructor

//inherit doc
  public void actionPerformed(ActionEvent evt)
  {
		JextTabbedPane tabbed = getTextArea(evt).getJextParent().getTabbedPane();
		
		if (right)
		{
			tabbed.nextTab();
		}//end if right
		else
		{
			tabbed.previousTab();
		}//end if not right
  }//end actionPerformed

}//end class TabSwitcher
