/*
 * Dawn.java - Dawn interpreter
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

package org.jext.dawn;

import java.io.StringReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Dawn
{
  private static String line = null;
  private static DawnParser parser = null;
  private static BufferedReader in = null;

  private static final String consoleCode = "\"\" command ->\n" +
  "while inputLine dup command -> \"exit\" same not repeat\n" +
  "command rcl eval consoleDump \"\\n>\" print\nwend";

  public static void main(String[] args)
  {
    if (args.length == 0)
    {
      System.out.println(DawnParser.DAWN_VERSION +
                         "\nUsage: java org.jext.dawn.Dawn <scipt file>\n" +
                         "Optional parameter:\n\t" +
                         "-console (enables Dawn-written console)\n\t" +
                         "-nativeConsole (enables native console");
      return;
    }

    if (args[0].equals("-console"))
    {
      DawnParser.init();
      System.out.print("Dawn console\nType some code then ENTER to execute it\nType exit to quit\n\n>");

      DawnParser.addGlobalFunction(new Function()
      {
        public String getName() { return "consoleDump"; }
        public void invoke(DawnParser parser) throws DawnRuntimeException
        {
          System.out.print('\n' + parser.dump());
        }
      });

      parser = new DawnParser(new StringReader(consoleCode));
      console();
    } else if (args[0].equals("-nativeConsole")) {
      DawnParser.init();
      System.out.print("Dawn console\nType some code then ENTER to execute it\nType exit to quit\n\n>");

      in = new BufferedReader(new InputStreamReader(System.in));

      nativeConsole();
    } else {
      try
      {
        in = new BufferedReader(new InputStreamReader(
                                new FileInputStream(DawnUtilities.constructPath(args[0]))));
        StringBuffer buf = new StringBuffer();

        for ( ; (line = in.readLine()) != null; )
          buf.append(line).append('\n');

        in.close();

        DawnParser.init();
        parser = new DawnParser(new StringReader(buf.toString()));
        parser.exec();

        System.out.print(parser.dump());
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  public static void console()
  {
    try
    {
      parser.exec();
    } catch (DawnRuntimeException dre) {
      System.out.println(dre.getMessage());
      System.out.print("\n>");
      parser = new DawnParser(new StringReader(consoleCode));
      console();
    }
  }

  public static void nativeConsole()
  {
    try
    {
      while (!(line = in.readLine()).equals("exit"))
      {
        parser = new DawnParser(new StringReader(line));
        parser.exec();
        System.out.print(parser.dump());
        System.out.print("\n>");
      }
    } catch (Exception dre) {
      System.out.println(dre.getMessage());
      System.out.print("\n>");
      nativeConsole();
    }
  }
}

// End of Dawn.java
