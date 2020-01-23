/*
 * 16:28:21 23/02/00
 *
 * XPropertiesReader.java - Reads XML-Properties files
 * Copyright (C) 1999 Romain Guy
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
import com.microstar.xml.*;
import org.jext.Jext;

public class XPropertiesReader
{
  public XPropertiesReader() { }

  /**
   * This method loads XML properties from the specified InputStream or from the translated
   * file named "name". So the caller should not provide the translated one(that is, calling
   * getLanguageString to obtain parameters.
   */
  public static boolean read(InputStream fileStream, String name)
  {
    /*InputStream in = Jext.getLanguageStream(fileName, name);
    if (in == null)
      return false;
    else
      return read(in);*/
    return read(fileStream, name, true);
  }
  /**
   * If toTranslate is true, it behaves just like read(InputStream, String); else it loads XML properties
   * from the specified InputStream, which must be the right(i.e. already translated) one.
   * @since Jext3.2pre1   
   */
  public static boolean read(InputStream fileStream, String name, boolean toTranslate)
  {
    InputStream in;
    if (toTranslate) {
      in = Jext.getLanguageStream(fileStream, name);
      if (in == null)
        return false;
    }
    else
      in = fileStream;
    
    InputStreamReader reader = new InputStreamReader(in);
    if (reader == null)
      return false;

    XmlParser parser = new XmlParser();
    parser.setHandler(new XPropertiesHandler());

    try
    {
      parser.parse(Jext.class.getResource("xproperties.dtd").toString(), null, reader);
    } catch (XmlException e) {
      System.err.println("XProperties: Error parsing grammar " + name);
      System.err.println("XProperties: Error occured at line " + e.getLine() +
                         ", column " + e.getColumn());
      System.err.println("XProperties: " + e.getMessage());
      return false;
    } catch (Exception e) {
      return false;
    }

    try
    {
      fileStream.close();
      in.close();
      reader.close();
    } catch (IOException ioe) { }

    return true;
  }
}

// End of XPropertiesReader.java