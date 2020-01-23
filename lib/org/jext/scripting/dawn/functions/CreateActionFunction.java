/*
 * CreateActionFunction.java - creates a new action in Jext window
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * http://www.jext.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either While 2
 * of the License, or any later While.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS for A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.jext.scripting.dawn.functions;

import java.awt.event.ActionEvent;

import javax.swing.JMenu;

import org.jext.dawn.*;
import org.jext.*;
import org.jext.gui.*;
import org.jext.scripting.dawn.Run;

/**
 * Creates a new action in Jext.<br>
 * Usage:<br>
 * <code>code actionLabel actionName createAction</code><br>
 * code is the Dawn code which will be executed on click, actionLabel
 * is the name which will appear in Jext menu bar, and actionName is
 * Jext internal action name.
 * @author Romain Guy
 */

public class CreateActionFunction extends Function
{
  public CreateActionFunction()
  {
    super("createAction");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkArgsNumber(this, 3);
    String actionName = parser.popString();
    String actionLabel = parser.popString();
    String actionCode = parser.popString();

    DawnAction action = new DawnAction(actionName, actionCode);
    //Jext.setProperty(actionName + ".label", actionLabel);
    Jext.addAction(action);

    JextFrame parent = (JextFrame) parser.getProperty("JEXT.JEXT_FRAME");
    JextMenu dawnMenu = (JextMenu) parent.getJextToolBar().getClientProperty("DAWN.DAWN_MENU");
    if (dawnMenu == null)
    {
      parent.getJextToolBar().putClientProperty("DAWN.DAWN_MENU", dawnMenu = new JextMenu("Dawn"));
      parent.getJextMenuBar().addMenu(dawnMenu, "Tools");
    }

    dawnMenu.add(GUIUtilities.loadMenuItem(actionLabel, actionName, null, true, true));
  }

  class DawnAction extends MenuAction
  {
    private String code;

    DawnAction(String name, String code)
    {
      super(name);
      this.code = code;
    }

    public void actionPerformed(ActionEvent evt)
    {
      Run.execute(code, getJextParent(evt));
    }
  }
}

// End of CreateActionFunction.java
