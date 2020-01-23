/*
 * 09/14/2001 - 13:27:55
 *
 * EntitiesListReader.java - Reads entities list files
 * Copyright (C) 2001 Romain Guy
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

import java.io.*;
import org.jext.Jext;
import com.microstar.xml.*;

public class EntitiesListReader
{
  public EntitiesListReader() { }

  public static boolean read(InputStream fileName, String file)
  {
    InputStreamReader reader = new InputStreamReader(fileName);
    EntitiesListHandler xmh = new EntitiesListHandler();
    XmlParser parser = new XmlParser();
    parser.setHandler(xmh);

    try
    {
      parser.parse(TagsCompletion.class.getResource("tagslist.dtd").toString(), null, reader);
    } catch (XmlException e) {
      System.err.println("Entities list: Error parsing grammar " + fileName);
      System.err.println("Entities list: Error occured at line " + e.getLine() +
                         ", column " + e.getColumn());
      System.err.println("Entities list: " + e.getMessage());
      return false;
    } catch (Exception e) {
      // Should NEVER happend !
      e.printStackTrace();
      return false;
    }

    try
    {
      fileName.close();
      reader.close();
    } catch (IOException ioe) { }

    return true;
  }
}

// End of EntitiesListReader.java
