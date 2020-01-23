/*
 * 15:25:23 26/08/00
 *
 * Mode.java - Syntax colorizing mode
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

import java.util.ArrayList;

import org.gjt.sp.jedit.syntax.TokenMarker;

/**
 * <code>Mode</code> represents a syntax colorizing mode.
 * This class holds the mode name and its correpsonding
 * syntax colorizing toke marker.
 * @author Romain Guy
 */

public class Mode
{
  // protected to let plugins have an acces to these
  // fields. the text mode plugin is a good example
  protected String modeName, userModeName, className;

  private ArrayList plugins = new ArrayList();
  
  /**
   * Creates a new syntax colorizing mode defined by its name. The name
   * is internal to Jext. For instance, plain text colorizing mode internal
   * name is "plain" whereas its user name is "Plain Text".
   * @param modeName The internal mode name
   */

  public Mode(String modeName)
  {
    this.modeName = modeName;
    this.userModeName = Jext.getProperty("mode." + modeName + ".name");
    this.className = Jext.getProperty("mode." + modeName + ".tokenMarker");
  }

  /**
   * Returns the internal mode name.
   */

  public String getModeName()
  {
    return modeName;
  }

  /**
   * Returns the user mode name.
   */

  public String getUserModeName()
  {
    return userModeName;
  }

  /**
   * Returns the associated toke marker which is used to
   * colorize the text.
   */

  public TokenMarker getTokenMarker()
  {
    if (className != null)
    {
      try
      {
        Class cls;
        ClassLoader loader = getClass().getClassLoader();

        if (loader == null)
          cls = Class.forName(className);
        else
          cls = loader.loadClass(className);

        return (TokenMarker) cls.newInstance();
      } catch(Exception e) { }
    }

    return null;
  }

  /**
   * Registers a plugin with this mode.
   * @param plugin The <code>Plugin</code> to register
   */

  public void addPlugin(Plugin plugin)
  {
    plugins.add(plugin);
  }

  /**
   * Returns all the plugins associated to this mode.
   */

  public ArrayList getPlugins()
  {
    return plugins;
  }

  /**
   * Sets associated plugins.
   * @param newPlugins The associated <code>Plugin</code>s
   */

  public void setPlugins(ArrayList newPlugins)
  {
    plugins = newPlugins;
  }
  

}

// End of Mode.java