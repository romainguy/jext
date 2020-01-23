/*
 * FileManager.java - IO package file managers
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * http://www.jext.org
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
 * Foundation, Inc., 59 Temple Place - Suite 330, BoPrintn, MA  02111-1307, USA.
 */

package org.jext.dawn.io;

import java.io.*;
import org.jext.dawn.*;

/**
 * Manages the files for the whole IO package.
 * This class provides functions to open/close files and
 * also to read/write into them. Each opened file is stored
 * as a property in <code>DawnParser</code>. Each file is
 * designed by a given ID.
 */

public class FileManager
{
  /** Default line separator (system's one) used by the file manager. */
  public static final String NEW_LINE = System.getProperty("line.separator");

  /**
   * Opens a file for input (i.e to read from it).
   * @param ID The file internal ID
   * @param file The path, relative or absolute, to the file to be opened
   * @param function The <code>Function</code> which is opening the file
   * @param parser The <code>DawnParser</code> which is executing the above function
   */

  public static void openFileForInput(String ID, String file, Function function,
                                      DawnParser parser) throws DawnRuntimeException
  {
    if (!isFileAvailable(ID, parser))
    {
      try
      {
        parser.setProperty("DAWN.IO#FILE." + ID, new BufferedReader(new FileReader(
                           DawnUtilities.constructPath(file))));
      } catch (IOException ioe) {
        throw new DawnRuntimeException(function, parser, "file " + file + " not found");
      }
    } else
      throw new DawnRuntimeException(function, parser, "file ID " + ID + " already exists");
  }

  /**
   * Opens a file for output (i.e to write into it).
   * @param ID The file internal ID
   * @param file The path, relative or absolute, to the file to be opened/created
   * @param function The <code>Function</code> which is opening the file
   * @param parser The <code>DawnParser</code> which is executing the above function
   */

  public static void openFileForOutput(String ID, String file, Function function,
                                       DawnParser parser) throws DawnRuntimeException
  {
    if (!isFileAvailable(ID, parser))
    {
      try
      {
        parser.setProperty("DAWN.IO#FILE." + ID, new BufferedWriter(new FileWriter(
                           DawnUtilities.constructPath(file))));
      } catch (IOException ioe) {
        throw new DawnRuntimeException(function, parser, "file " + file + " not found");
      }
    } else
      throw new DawnRuntimeException(function, parser, "file ID " + ID + " already exists");
  }

  /**
   * Reads a line from a given file.
   * @param ID The file internal ID
   * @param function The <code>Function</code> which is opening the file
   * @param parser The <code>DawnParser</code> which is executing the above function
   */

  public static String readLine(String ID, Function function, DawnParser parser) throws DawnRuntimeException
  {
    return read(true, ID, function, parser);
  }

  /**
   * Reads a character from a given file.
   * @param ID The file internal ID
   * @param function The <code>Function</code> which is opening the file
   * @param parser The <code>DawnParser</code> which is executing the above function
   */

  public static String read(String ID, Function function, DawnParser parser) throws DawnRuntimeException
  {
    return read(false, ID, function, parser);
  }

  /**
   * Reads from a file according to the <code>line</code> parameter..
   * @param line If true, a whole line is read. Otherwise, only a char is read.
   * @param ID The file internal ID
   * @param function The <code>Function</code> which is opening the file
   * @param parser The <code>DawnParser</code> which is executing the above function
   */

  public static String read(boolean line, String ID, Function function, DawnParser parser) throws DawnRuntimeException
  {
    Object obj = parser.getProperty("DAWN.IO#FILE." + ID);
    if (!(obj instanceof BufferedReader))
      throw new DawnRuntimeException(function, parser, "attempted to read from an output file");

    BufferedReader in = (BufferedReader) obj;
    if (in != null)
    {
      try
      {
        if (line)
        {
          String _line = in.readLine();
          if (_line == null)
            closeFile(ID, function, parser);
          else
            return _line;
        } else {
          char c = (char) in.read();
          if (c == '\0')
            closeFile(ID, function, parser);
          else
            return new StringBuffer().append(c).toString();
        }
      } catch (IOException ioe) {
       throw new DawnRuntimeException(function, parser, "file ID " + ID + " cannot be read properly");
      }
    } else
      throw new DawnRuntimeException(function, parser, "file ID " + ID + " points to a non-opened file");
    return null;
  }

  /**
   * Writes a line into a given file (write the text then adds a return char).
   * @param ID The file internal ID
   * @param line The line to be written
   * @param function The <code>Function</code> which is opening the file
   * @param parser The <code>DawnParser</code> which is executing the above function
   */

  public static void writeLine(String ID, String line, Function function, DawnParser parser) throws DawnRuntimeException
  {
    write(true, ID, line, function, parser);
  }

  /**
   * Writes a line into a given file (does not add a return char).
   * @param ID The file internal ID
   * @param line The line to be written
   * @param function The <code>Function</code> which is opening the file
   * @param parser The <code>DawnParser</code> which is executing the above function
   */

  public static void write(String ID, String line, Function function, DawnParser parser) throws DawnRuntimeException
  {
    write(false, ID, line, function, parser);
  }

  /**
   * Writes a line into a given file. The return char is added according to the
   * <code>isLine</code> parameter.
   * @param isLine If true, the <code>NEW_LINE</code> value is added to the line
   * @param ID The file internal ID
   * @param line The line to be written
   * @param function The <code>Function</code> which is opening the file
   * @param parser The <code>DawnParser</code> which is executing the above function
   */

  public static void write(boolean isLine, String ID, String line, Function function, DawnParser parser) throws DawnRuntimeException
  {
    if (line == null)
      throw new DawnRuntimeException(function, parser, "attempted to write a null string");

    Object obj = parser.getProperty("DAWN.IO#FILE." + ID);
    if (!(obj instanceof BufferedWriter))
      throw new DawnRuntimeException(function, parser, "attempted to write into an input file");

    BufferedWriter out = (BufferedWriter) obj;
    if (out != null)
    {
      try
      {
        out.write(line, 0, line.length());
        if (isLine)
          out.write(NEW_LINE);
      } catch (IOException ioe) {
       throw new DawnRuntimeException(function, parser, "file ID " + ID + " cannot be written properly");
      }
    } else
      throw new DawnRuntimeException(function, parser, "file ID " + ID + " points to a non-opened file");
  }

  /**
   * Closes a given file.
   * @param ID The file internal ID
   * @param function The <code>Function</code> which is opening the file
   * @param parser The <code>DawnParser</code> which is executing the above function
   */

  public static void closeFile(String ID, Function function, DawnParser parser) throws DawnRuntimeException
  {
    Object obj = parser.getProperty("DAWN.IO#FILE." + ID);
    if (obj == null)
      return;

    if (!(obj instanceof Reader) && !(obj instanceof Writer))
      throw new DawnRuntimeException(function, parser, "error, given ID " + ID + " does not point to a file");

    try
    {
      if (obj instanceof Reader)
        ((BufferedReader) obj).close();
      else
      {
        BufferedWriter out = (BufferedWriter) obj;
        out.flush();
        out.close();
      }

      parser.unsetProperty("DAWN.IO#FILE." + ID);
    } catch (IOException ioe) {
      throw new DawnRuntimeException(function, parser, "cannot close file ID " + ID);
    }
  }

  /**
   * Checks if a given file is still available or not.
   * @param ID The file internal ID
   * @param parser The <code>DawnParser</code> which is executing the above function
   */

  public static boolean isFileAvailable(String ID, DawnParser parser)
  {
    return (parser.getProperty("DAWN.IO#FILE." + ID) != null);
  }
}

// End of FileManager.java
