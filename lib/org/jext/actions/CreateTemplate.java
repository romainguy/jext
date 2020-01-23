/*
 * 11/18/2000 - 01:33:45
 *
 * CreateTemplate.java - Creates a file from a template
 * Copyright (C) 2000 Blake Winton, modifs by Romain Guy
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

package org.jext.actions;

import java.awt.event.ActionEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.HashMap;

import javax.swing.JOptionPane;

import gnu.regexp.RE;
import gnu.regexp.REMatch;

import org.jext.Jext;
import org.jext.JextFrame;
import org.jext.MenuAction;
import org.jext.Utilities;

public class CreateTemplate extends MenuAction
{
  public CreateTemplate()
  {
    super("create_template");
  }

  public void actionPerformed(ActionEvent evt)
  {
    try
    {
      String fileName = evt.getActionCommand();
      String input = loadFile(fileName);
    
      JextFrame parent = getJextParent(evt);
      HashMap tokens = new HashMap();
      tokens.put("____", "__");
      addTokensFromInput(parent, input, tokens);
    
      parent.open(saveOutput(parent, replace(input, tokens)));
    } catch(Exception e) {
      System.err.println(e);
    }
  }
  
  private String loadFile(String fileName) throws Exception
  {
    File source = new File(fileName);
    if(!source.exists() || !source.canRead())
      throw new Exception("Could not read file " + source.getName());

    String line;
    StringBuffer buf = new StringBuffer((int) source.length());
    BufferedReader reader = new BufferedReader(new FileReader(source));
    while ((line = reader.readLine()) != null)
      buf.append(line).append('\n');
    reader.close();

    return buf.toString();
  }
  
  private void addTokensFromInput(JextFrame parent, String input, HashMap tokens) throws Exception
  {
    String pattern = "__([^_]|_[^_])*__";
    RE re = new RE( pattern );
    REMatch[] matches = re.getAllMatches(input);

    for (int i = 0; i < matches.length; i++ )
    {
      String key = matches[i].toString();
      String value = "";
      if (!tokens.containsKey(key))
      {
        String var = key.substring(2, key.length() - 2);
        value = (String) JOptionPane.showInputDialog(parent,
                Jext.getProperty("templates.input", new String[] { var }),
                Jext.getProperty("templates.title"), JOptionPane.QUESTION_MESSAGE, null, null, var);
        tokens.put(key, value);
      }
    }
  }

  private String replace(String input, HashMap tokens) throws Exception
  {
    String retval = input;
    String[] keys = (String[]) tokens.keySet().toArray(new String[0]);

    for (int i = 0; i < keys.length; i++)
    {
      String currKey = keys[i];
      if (currKey.equals( "____" ))
        continue;
      RE re = new RE(currKey);
      retval = re.substituteAll(retval, (String) tokens.get(currKey));
    }

    RE re = new RE("____");
    retval = re.substituteAll(retval, (String) tokens.get("__"));
    return retval;
  }
  
  private String saveOutput(JextFrame parent, String output) throws Exception
  {
    String fileName = Utilities.chooseFile(parent, Utilities.SAVE);
    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
    writer.write(output, 0, output.length());
    writer.flush();
    writer.close();

    return fileName;
  }
}

// End of CreateTemplate.java
