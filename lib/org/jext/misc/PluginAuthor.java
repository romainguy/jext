/*
 * 03/23/2003
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

public class PluginAuthor {
  private String name, email, content;
  public PluginAuthor(String _content) {
    content = _content;
  }

  public PluginAuthor(String _name, String _email) {
    name = _name;
    email = _email;
  }

  public String toString() {
    if (content != null)
      return content;
    else
      return content = ( new StringBuffer("<a href=\"mailto:"). append(email). append("\">").
                         append(name). append("</a>")   ).
                        toString();
  }
}
