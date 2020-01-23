/*
 * JextListener.java - Listens to jext events
 * Copyright (C) 2000 Romain Guy
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

package org.jext.event;

/**
 * The listener interface for receiving Jext
 * events. This events can be of different kinds:
 * options change, text area change, etc...
 * @author Romain Guy
 */

public interface JextListener
{
  /**
   * Invoked when a class fires a <code>JextEvent</code> object
   * by the method <code>fireJextEvent(short eventType)</code>
   * in <code>Jext</code> class.
   * @param evt The received <code>JextEvent</code>
   */

  public void jextEventFired(JextEvent evt);
}

// End of JextListener.java
