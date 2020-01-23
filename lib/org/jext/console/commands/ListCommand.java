/*
 * ListCommand.java - Implementation of ls:dir command
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * http://www.jext.org
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
import org.jext.console.ConsoleListDir;

/**
 * This command lists current dir contents.
 * @author Romain Guy
 */

public class ListCommand extends Command
{
  private static final String COMMAND_NAME = "ls";
  private static final String COMMAND_NAME_ALTERNATE = "dir";

  public String getCommandName()
  {
    return COMMAND_NAME;
  }

  public String getCommandSummary()
  {
    return Jext.getProperty("console.ls.command.help");
  }

  public boolean handleCommand(Console console, String command)
  {
    if (command.equals(COMMAND_NAME) || command.equals(COMMAND_NAME_ALTERNATE))
    {
      ConsoleListDir.list(console, null);
      return true;
    } else if (command.startsWith(COMMAND_NAME) || command.startsWith(COMMAND_NAME_ALTERNATE)) {
      ConsoleListDir.list(console, command.substring(2));
      return true;
    }

    return false;
  }
}

// End of ListCommand.java
