/*
 * 13:40:07 02/02/00
 *
 * OptionPane.java - Option pane interface
 * Copyright (C) 1999 Romain Guy
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

package org.jext.gui;

import java.awt.Component;

/**
 * An interface which defines the basical behavior
 * of an option pane. Options panes are targeted to
 * be displayed in an option dialogs.
 */

public interface OptionPane
{
  /**
   * Returns the name of the option pane.
   * This name can be required by componens holder
   * such as tabbed panes.
   */

  public String getName();

  /**
   * Returns the component which stands for the option
   * pane itself. In fact, an option pane can be label,
   * a checkbox, etc...
   */

  public Component getComponent();

  /**
   * When user closes an option dialog by clicking ok,
   * the settings have to be changed. So, all the settings
   * relative to an option pane have to be saved in this method.
   */

  public void save();
  /**
   * This is implemented in AbstractOptionPane and returns false by default; if your plugin
   * has a working load() method (not the one provided by AbstractOptionPane), you must override
   * it as
   * <code>public boolean isCacheable() {return true;} </code>
   * It is not allowed to return different values.
   */
  public boolean isCacheable();
  /**
   * When the user closes the option dialog by clicking cancel, the settings have to be
   * reloaded next time the dialog appears, so this method is called. For old plugins, however,
   * it won't be called, since the default implementation of isCacheable returns false, and
   * the pane will be completely rebuilt.
   * If you implement it, note that the constructor or however the createOptionPanes method of your
   * plugin must call it. The constructor should not contain the code to load values; only this method
   * should.
   */
  public void load();
}

// End of OptionPane.java
