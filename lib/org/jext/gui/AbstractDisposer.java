/*
 * 19:06:59 13/12/99
 *
 * AbstractDisposer.java - Offer an abstract dispose behavior
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

package org.jext.gui;

import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

/**
 * This class handles a press on the ESCAPE key in a
 * <code>java.awt.Window</code> instance. This includes
 * <code>JFrame</code> and <code>JDialog</code>. When such
 * an event is caught, the instance is simply disposed.
 * @author Romain Guy
 */

public class AbstractDisposer extends KeyAdapter
{
  // private members
  private Window parent;

  /**
   * Creates a new KeyListener which register a
   * specified Window. Normally, this window should
   * register this class as one of its key listeners.
   */

  public AbstractDisposer(Window parent)
  {
    this.parent = parent;
  }

  public void keyPressed(KeyEvent evt)
  {
    switch (evt.getKeyCode())
    {
      case KeyEvent.VK_ESCAPE:
        parent.dispose();
        evt.consume();
        break;
    }
  }

  public void finalize() throws Throwable
  {
    super.finalize();
    parent = null;
  }
}

// End of AbstractDisposer.java
