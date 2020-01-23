/*
 * 12/06/2003
 *
 * LogWindow.java - Log window infrastructure
 * Copyright (C) 2003 Paolo Giarrusso
 * blaisorblade_work@yahoo.it
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

package org.jext.scripting;

import java.io.Writer;

public interface Logger {
  void logln(String msg);
  void log(String msg);
  Writer getStdOut();
  Writer getStdErr();
}
