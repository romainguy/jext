/*
 * 12:00:00 15/12/02
 *
 * GenericSkin.java - A generic Skin implementation, useful for most Look and Feel's.
 * Copyright (C) 2002 Paolo Giarrusso
 * blaisorblade_work@yahoo.it
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

package org.jext.gui;

import javax.swing.UIManager;
import javax.swing.LookAndFeel;

/**
 * This is a generic skin class which will be able to wrap most possible skin,
 * actually the normal LookAndFeel's. If you need special processing, write
 * your own Skin interface implementation or extend this one.
 */

public class GenericSkin extends Skin {
  private String name;
  private String intName;
  protected String lafClassName = null;
  protected LookAndFeel laf;
  protected ClassLoader skLoader = null;
  
  /**
   * Constructor; use it to provide the caption, the internal name and the name of
   * the class to use as LookAndFeel(the same you would pass to UIManager.setLookAndFeel).
   */
  public GenericSkin(String name, String intName, String lafClassName) {
    this(name, intName, lafClassName, null);
  }

  /**
   * Constructor; use it to provide the caption, the internal name, the name of
   * the class to use as LookAndFeel(the same you would pass to UIManager.setLookAndFeel)
   * and the ClassLoader that will be used to load the LookAndFeel and related classes; 
   * useful especially if you write a plugin.
   * In this case pass as classLoader <Your class>.class.getClassLoader(), as KLNF does.
   */
  public GenericSkin(String name, String intName, String lafClassName, ClassLoader cl) {
    this.name = name;
    this.intName = intName;
    this.lafClassName = lafClassName;
    this.skLoader = cl;
  }

  /**
   * Constructor; use it to provide the caption, the internal name and the built 
   * LookAndFeel instance (the same you would pass to UIManager.setLookAndFeel).
   */
  public GenericSkin(String name, String intName, LookAndFeel laf) {
    this(name, intName, laf, null);
  }

  /**
   * Constructor; use it to provide the caption, the internal name and the built 
   * LookAndFeel instance (the same you would pass to UIManager.setLookAndFeel).
   * This constructor allows you to specify a ClassLoader that will be used to load
   * the LookAndFeel and related classes; useful especially if you write a plugin.
   * In this case pass as classLoader <Your class>.class.getClassLoader(), as KLNF does.
   */
  public GenericSkin(String name, String intName, LookAndFeel laf, ClassLoader cl) {
    this.name = name;
    this.intName = intName;
    this.laf = laf;
    this.skLoader = cl;
  }

  /*public void setValues(String name, String intName, String lafClassName, ClassLoader cl) {
    built = true;
    this.name = name;
    this.intName = intName;
    this.lafClassName = lafClassName;
    this.skLoader = cl;
  }*/
  /*
   * If you extend this class, use this method to pass the value you would pass instead
   * to the constructor; note that you can't call it
   
  public void setValues(String name, String intName, String lafClassName) {
    built = true;
    this.name = name;
    this.intName = intName;
    this.lafClassName = lafClassName;
  }*/

  public boolean isAvailable() {
    return true;
  }

  public String getSkinName() {
    return name;
  }

  public String getSkinInternName() {
    return intName;
  }
  /**
   * This method applies the skin; if you have not called one of the constructors, it will fail silently.
   */
  public void apply() throws Throwable {
    if (skLoader != null)
      UIManager.put("ClassLoader", skLoader);//check this!

    if (lafClassName != null)
      UIManager.setLookAndFeel(lafClassName);
    else
      UIManager.setLookAndFeel(laf);
  }
}
