/*
 * JextActionFunction.java - executes a Jext action
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

import org.jext.dawn.*;
import org.jext.*;

/**
 * Executes a jext action.<br>
 * Usage:<br>
 * <code>actionName jextAction</code>
 * @author Romain Guy
 */

public class JextActionFunction extends Function
{
  public JextActionFunction()
  {
    super("jextAction");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);

    String cmd = parser.popString();
    MenuAction action = Jext.getAction(cmd);
    if (action == null)
      throw new DawnRuntimeException(this, parser, "unkown jext action named " + cmd);
    else
      action.actionPerformed(new ActionEvent((JextFrame) parser.getProperty("JEXT.JEXT_FRAME"),
                             ActionEvent.ACTION_PERFORMED, null));
  }
}

// End of JextActionFunction.java
