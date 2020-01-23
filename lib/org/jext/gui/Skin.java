/*
 * 12:00:00 15/12/02
 *
 * Skin.java - The generic skin base abstract class
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

/**
 * This is the generic Skin base class. Override it to provide a new skin.
 * @author  BlaisorBlade
 */

public abstract class Skin
{
  //as with plugins, we catch all sort of exceptions to not stop jext for
  //buggy skins.

  /**
   * It must actually apply the Skin; to indicate errors it will throw any sort of
   * exception. Tipically it will include a call to UIManager.setLookAndFeel
   * to apply actual LookAndFeel object.
   */
  public abstract void apply() throws Throwable;
  
  /**
   * The name to show inside the option dialog; this should be get using a Jext property
   * with Jext.getProperty.
   */

  public abstract String getSkinName();

  /** The internal name of the skin, used to identify it; it mustn't be translated.*/
  public abstract String getSkinInternName();

  /**If your skin is not available in certain cases, override this; it could even call
   * the LookAndFeel.isAvailable method. (But you shouldn't since LookAndFeel creation
   * is expensive).*/
  public boolean isAvailable() {
    return true;
  }

  /**
   * You can override this to execute code when this skin is replaced by another.
   */
  public void unapply() throws Throwable {
  }
}

// End of Skin.java
