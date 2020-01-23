/*
 * 03/31/2001 - 12:41:59
 *
 * ConsoleListDir.java - A ls function for Java Shell (Jext adapted)
 * Copyright (C) 1998-1999 Romain Guy
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

package org.jext.console;

import java.io.File;
import java.io.IOException;

import java.util.Date;
import java.util.StringTokenizer;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;

import org.jext.*;

/**
 * A ls function for Java Shell. Adapted to Jext.
 * @author Romain Guy
 * @version 1.9.4
 */

//Thread-safe? Don't joke, too many statics...
public class ConsoleListDir
{
  private static Console parent;

  //the command line splitted into options.
  private static String pattern = new String();
  private static boolean moreInfos, fullNames, longDates, hiddenFiles, noDates, onlyDirs,
                         onlyFiles, recursive, noInfos, kiloBytes, sort;

  // these instances are used to improve speed of dates calculations.
  private static StringBuffer buffer = new StringBuffer();
  private static Date date = new Date();
  private static FieldPosition field = new FieldPosition(0);
  private static SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy - HH:mm:ss");

  /**
   * Exec the equivalent of system's 'ls' or 'dir' command.
   * @param cparent Console which executed the command
   * @param args The command arguments
   */

  public static void list(Console cparent, String args)
  {
    parent = cparent;
    boolean list = true;

    if (buildFlags(args))
    {
      String old = Utilities.getUserDirectory();
      run(0);
      print("");

      if (recursive)
        System.getProperties().put("user.dir", old);

      // we reset flags
      sort = kiloBytes = recursive = onlyFiles = onlyDirs = noDates
           = moreInfos = hiddenFiles = longDates = fullNames = false;
      pattern = "";
    }
  }

  /**
   * Output a <code>String</code> in the parent console.
   * @param print <code>String</code> to output
   */

  private static void print(String print)
  {
    parent.output(print);
  }

  /**
   * Determine which options are enabled.
   * @param args The arguments containing the option flags
   */

  private static boolean buildFlags(String arg)
  {
    if (arg == null)
      return true;

    StringTokenizer tokens = new StringTokenizer(arg);
    String argument;

    while (tokens.hasMoreTokens())
    {
      argument = tokens.nextToken();

      if (argument.startsWith("-"))
      {
        if (argument.equals("-help"))
        {
          help();
          return false;
        }

        for (int j = 1; j < argument.length(); j++)
        {
          switch (argument.charAt(j))
          {
            case 'h':               // hidden files to be shown
              hiddenFiles = true;
              break;
            case 'm':               // display full infos
              moreInfos = true;
              break;
            case 'l':               // use long dates format
              longDates = true;
              break;
            case 'f':               // display full names (don't cut with '...')
              fullNames = true;
              break;
            case 'n':               // don't show last modified dates
              noDates = true;
              break;
            case 'd':               // lists dirs only
              onlyDirs = true;
              break;
            case 'a':               // lists files only
              onlyFiles = true;
              break;
            case 'r':               // lists subdirectories
              recursive = true;
              break;
            case 'i':               // don't show infos
              noInfos = true;
              break;
            case 'k':               // display file sizes in kb instead of bytes
              kiloBytes = true;
              break;
            case 's':               // alphabetically sort files
              sort = true;
              break;
          }
        }
      } else
        pattern = argument;
    }

    return true;
  }

  private static void displayFile(File current, String indent) {
    String currentName = current.getName();
    StringBuffer display = new StringBuffer();
    if (!fullNames)
      currentName =  Utilities.getShortStringOf(currentName, 24);
    int amountOfSpaces = 32 - currentName.length();

    if (current.isDirectory())
    {
      if (amountOfSpaces > 7)
        amountOfSpaces -= 6;
      else
        amountOfSpaces = 1;
      display.append(currentName).append(Utilities.createWhiteSpace(amountOfSpaces)).append("<DIR>");
      if (moreInfos)
        display = (new StringBuffer("   ")).append(Utilities.createWhiteSpace(8)).append(display);
    } else if (current.isFile()) {
      if (amountOfSpaces < 1)
        amountOfSpaces = 1;
      display.append(currentName).append(Utilities.createWhiteSpace(amountOfSpaces)).append(current.length());
      if (moreInfos)
      {
        StringBuffer info = new StringBuffer();
        info.append(current.canWrite() ? 'w' : '-');         // file is writable
        info.append(current.canRead() ? 'r' : '-');          // file is readable
        info.append(current.isHidden() ? 'h': '-');          // file is hidden
        info.append(Utilities.createWhiteSpace(8));
        display = info.append(display);
      }
    }

    StringBuffer time = new StringBuffer();
    if (!noDates)
    {
      date.setTime(current.lastModified());

      if (longDates)
      {
        time.append(date.toString());
      } else {
        buffer.setLength(0);
        time.append(formatter.format(date, buffer, field));
      }
      time.append(Utilities.createWhiteSpace(8));
    }
    print(indent + time.toString() + display.toString());
  }

  /**
   * List according to the options flag activated.
   */

  private static void run(int indentSize)
  {
    //---1st PART: get file list.
    String path = null;

    //if no wildcards, interpret things as .. or . or ~, since constructPath doesn't like
    //wildcards. FIXME: it seems it instead does like them.
    if (pattern.indexOf("*") == -1 && pattern.indexOf("?") == -1 &&
        pattern.indexOf("|") == -1)
    {
      pattern = Utilities.constructPath(pattern);
      File f = new File(pattern);
      if (f.isDirectory())
      {
        path = pattern;
        pattern = "";
      }
    }

    if (path == null)
    {
      int separatorIdx = pattern.lastIndexOf(File.separatorChar);
      if (separatorIdx != -1)
      {
        path = pattern.substring(0, separatorIdx + 1); //the slash is inside path.
        pattern = pattern.substring(separatorIdx + 1);
      } else {
        path = Utilities.getUserDirectory();
      }
    }

    // default pattern used is '*'
    pattern = pattern.equals("") ? "*" : pattern;

    File[] files = Utilities.listFiles(
                   Utilities.getWildCardMatches(path, pattern, sort), path, true);

    if (files == null || files.length == 0)
    {
      //if no match were found and only if there was no wildcard, show an error.
      if (pattern.indexOf("*") == -1 && pattern.indexOf("?") == -1 &&
          pattern.indexOf("|") == -1)
      {
        //parent.error(Jext.getProperty("console.ls.error"));
        parent.error(Jext.getProperty("console.ls.noFileError", "ls: No such file or directory"));
        return;
      }
      files = new File[0];
    }

    //---2nd PART: produce the output.
    String indent = createIndent(indentSize);
    long totalSize;
    int totalDir, totalFiles;
    totalSize = totalFiles = totalDir = 0;

    for (int i = 0; i < files.length; i++)
    {
      File current = files[i];

      // determine if we must show or not (according to flags) found file
      if (! ((hiddenFiles || !current.isHidden()) &&
            Utilities.match(pattern, current.getName())))
        continue;

      if ((current.isFile() && !onlyDirs) || (current.isDirectory() && !onlyFiles) ||
          (onlyDirs && onlyFiles))
      {
        displayFile(current, indent);
        if (current.isDirectory()) {
          totalDir++;

          // if we are dealing with a dir and -r flag is set, we browse it
          if (recursive)
          {
            System.getProperties().put("user.dir", Utilities.constructPath(current.getAbsolutePath()));
            print("");

            String oldPattern = pattern;
            pattern = "";
            run(indentSize + 1);
            pattern = oldPattern;

            if (!onlyDirs)
              print("");
          }

        } else if (current.isFile()) {
          totalSize += current.length();
          totalFiles++;
        }
      }
    } //loop over files[] end

    // display summary infos
    StringBuffer size = new StringBuffer();
    if (kiloBytes)
      size.append(formatNumber(Long.toString(totalSize / 1024))).append('k');
    else
      size.append(formatNumber(Long.toString(totalSize))).append("bytes");

    if (!noInfos) {
      if (!recursive || !(indentSize == 0))
        print("");
      print(indent + totalFiles + " files - " + totalDir + " directories - " +
            size.toString());
    }
    return;
  }

  /**
   * Format a number from 12000123 to 12 000 123.
   * @param number Number to be formatted
   */

  private static String formatNumber(String number)
  {
    StringBuffer formatted = new StringBuffer(number);
    for (int i = number.length(); i > 0; i -= 3)
      formatted.insert(i, ' ');
    return formatted.toString();
  }

  /**
   * Creates the indent for the recursive option.
   * An indent unit adds two '-'.
   * @param len Length of indentation
   */

  private static String createIndent(int len)
  {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < len; i++)
    {
      buf.append('-');
      buf.append('-');
    }
    return buf.toString();
  }

  /**
   * Display command help in the console.
   */

  public static void help()
  {
    parent.help(Jext.getProperty("console.ls.help"));
  }
}

// End of ConsoleListDir.java
