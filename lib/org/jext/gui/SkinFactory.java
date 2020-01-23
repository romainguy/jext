/*
 * 12:00:00 15/12/02
 *
 * SkinFactory.java - The generic Skin factory.
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
 * This is the generic SkinFactory base class.
 * Override it to let Jext access a new skin group, plugin and so on; if
 * the inheriting class is also a Plugin descendant the method is called 
 * authomatically for you when loading the plugin.
 * @author  Blaisorblade
 */

public interface SkinFactory
{
  /**Must return an array of already constructed Skin's.*/
  public Skin[] getSkins();
}

// End of Skin.java
