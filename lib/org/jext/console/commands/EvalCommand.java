/*
 * EvalCommand.java - Implementation of eval: command
 * Copyright (C) 2001 Romain Guy
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

import javax.swing.JOptionPane;

import org.jext.Jext;
import org.jext.Utilities;
import org.jext.console.Console;
import org.jext.scripting.python.Run;
import org.python.util.PythonInterpreter;

/**
 * This command runs a specified script.
 * @author Romain Guy
 */

public class EvalCommand extends Command
{
  private static final String COMMAND_NAME = "eval:";

  public String getCommandName()
  {
    return COMMAND_NAME + "scriptlet";
  }

  public String getCommandSummary()
  {
    return Jext.getProperty("console.eval.command.help");
  }

  public boolean handleCommand(Console console, String command)
  {
    if (command.startsWith(COMMAND_NAME))
    {
      String argument = command.substring(5);
      if (argument.length() > 0)
      {
        try
        {
          PythonInterpreter parser = Run.getPythonInterpreter(console.getParentFrame(), console);
          parser.set("__evt__", new java.awt.event.ActionEvent(console.getParentFrame().getTextArea(), 1705, null));
          parser.exec("import org.jext\n" + argument);
        } catch (Exception pe) {
          //useless since messages already go to the console.
          /*JOptionPane.showMessageDialog(console.getParentFrame(), Jext.getProperty("python.script.errMessage"),
                                        Jext.getProperty("python.script.error"),
                                        JOptionPane.ERROR_MESSAGE);
          if (Jext.getBooleanProperty("dawn.scripting.debug"))
            console.getParentFrame().getPythonLogWindow().logln(pe.toString());*/
	  pe.printStackTrace();
        }
      } else
        console.error(Jext.getProperty("console.missing.argument"));
      return true;
    }

    return false;
  }
}

// End of EvalCommand.java
