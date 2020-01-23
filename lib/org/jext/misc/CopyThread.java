/*
 * 06/13/2003
 *
 * Copyright (C) 2003 Paolo Giarrusso
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
package org.jext.misc;

import java.io.*;

public class CopyThread extends SwingWorker {
  protected InputStream in;
  protected OutputStream out;

  /** If you call this, you must set the <code>in</code> and <code>out</code> stream by yourself.*/
  protected CopyThread(HandlingRunnable notifier) {
    super(notifier);
  }

  public CopyThread(InputStream in, OutputStream out, HandlingRunnable notifier) {
    super(notifier);
    this.in = in;
    this.out = out;
  }

  public Object work() throws IOException {
    //this is very large to reduce the load imposed by the thread: the read() call
    //will return a very few times.
    byte[] buf = new byte[2048];
    int nRead;
    try {
      while ((nRead = in.read(buf)) != -1) {
        out.write(buf, 0, nRead);
      }
    } finally {
      try {
        in.close();
      } catch (IOException ioe) {}
      try {
        out.close();
      } catch (IOException ioe) {}
    }
    return null;
  }
}

