/*
 * 12:59:43 22/02/00
 *
 * XPopupReader.java - Reads xml-menus files
 * Copyright (C) 2000 Romain Guy
 * www.jext.org
 * romain.guy@jext.org
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

package org.jext.xml;

import java.io.*;
import javax.swing.*;
import com.microstar.xml.*;
import org.jext.*;

public class XPopupReader
{
  public XPopupReader() { }

  public static JPopupMenu read(InputStream fileName, String file)
  {
    InputStreamReader reader = new InputStreamReader(Jext.getLanguageStream(fileName, file));

    XPopupHandler xmh = new XPopupHandler();
    XmlParser parser = new XmlParser();
    parser.setHandler(xmh);

    try
    {
      parser.parse(Jext.class.getResource("xpopup.dtd").toString(), null, reader);
    } catch (XmlException e) {
      System.err.println("XPopup: Error parsing grammar " + fileName);
      System.err.println("XPopup: Error occured at line " + e.getLine() +
                         ", column " + e.getColumn());
      System.err.println("XPopup: " + e.getMessage());
      return null;
    } catch (Exception e) {
      // Should NEVER happend !
      e.printStackTrace();
      return null;
    }

    try
    {
      fileName.close();
      reader.close();
    } catch (IOException ioe) { }

    return xmh.getPopupMenu();
  }
}

// End of XPopupReader.java
