/*
 * JythonCommand.java - Implementation of jython command
 * Copyright (C) 2003 Tom Whittaker
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

package org.jext.console.commands;

import org.jext.Jext;
import org.jext.console.Console;

/**
 * This command exits Jext or closes current window.
 * @author Romain Guy
 */

public class JythonCommand extends Command
{
  private static final String COMMAND_NAME = "jython";

  public String getCommandName()
  {
    return COMMAND_NAME;
  }

  public String getCommandSummary()
  {
    return Jext.getProperty("console.jython.command.help");
  }

  public boolean handleCommand(Console console, String command)
  {
    if (command.equals(COMMAND_NAME))
    {
      if (Jext.getBooleanProperty("console.jythonMode")) {
        Jext.setProperty("console.jythonMode","false");
      } else {
        Jext.setProperty("console.jythonMode","true");
      }

      return true;
    }

    return false;
  }
}

// End of ExitCommand.java
