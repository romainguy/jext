/*
 * HttpCommand.java - Implementation of http:// command
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

import java.io.*;
import java.net.*;

import org.jext.Jext;
import org.jext.JextFrame;
import org.jext.JextTextArea;
import org.jext.Utilities;
import org.jext.console.Console;

/**
 * This command opens an url in text area.
 * @author Romain Guy
 */

public class HttpCommand extends Command
{
  private static final String COMMAND_NAME = "http://";

  public String getCommandName()
  {
    return COMMAND_NAME + "url";
  }

  public String getCommandSummary()
  {
    return Jext.getProperty("console.http.command.help");
  }

  public boolean handleCommand(Console console, String command)
  {
    if (command.startsWith(COMMAND_NAME))
    {
      boolean err = true;
      JextFrame parent = console.getParentFrame();
      JextTextArea textArea = parent.createFile();

      try
      {
        URL url = new URL(command);
        textArea.open(command, new InputStreamReader(url.openStream()), 1024);
        err = false;
      } catch (MalformedURLException mue) {
        Utilities.showError(Jext.getProperty("url.malformed"));
      } catch (IOException ioe) {
        Utilities.showError(ioe.toString());
      }

      if (err)
        parent.close(textArea);

      return true;
    }

    return false;
  }
}

// End of HttpCommand.java
