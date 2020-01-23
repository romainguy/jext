/*
 * 21:16:50 16/04/00
 *
 * DawnUtilities.java - Some utilities for Jext and its classes
 * Copyright (C) 1999-2000 Romain Guy
 * guy.romain@bigfoot.com
 * www.chez.com/powerteam
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

package com.chez.powerteam.dawn;

import java.io.File;
import java.util.Vector;
import java.lang.reflect.*;

/**
 * This class contains some utility methods needed by Dawn or its functions.
 */

public class DawnUtilities
{
  /**
   * Constructs a new path from current user path. This is an easy way to get a path
   * if the user specified, for example, "..\Java" as new path. This method will return
   * the argument if this one is a path to a root (i.e, if <code>change</code> is equal
   * to C:\Jdk, constructPath will return C:\Jdk).
   * @param change The modification to apply to the path
   */

  public static String constructPath(String change)
  {
    if (beginsWithRoot(change))
      return change;

    StringBuffer newPath = new StringBuffer(getUserDirectory());

    char current;
    char lastChar = '\0';
    boolean toAdd = false;
    change = change.trim();
    StringBuffer buf = new StringBuffer(change.length());

    for (int i = 0; i < change.length(); i++)
    {
      switch ((current = change.charAt(i)))
      {
        case '.':
          if (lastChar == '.')
          {
            String parent = (new File(newPath.toString())).getParent();
            if (parent != null) newPath = new StringBuffer(parent);
          } else if ((lastChar != '\0' && lastChar != '\\' && lastChar != '/') ||
                     (i < change.length() - 1 && change.charAt(i + 1) != '.'))
            buf.append('.');
          lastChar = '.';
          break;
        case '\\': case '/':
          if (lastChar == '\0')
            newPath = new StringBuffer(getRoot(newPath.toString()));
          else {
            char c = newPath.charAt(newPath.length() - 1);
            if (c != '\\' && c != '/')
              newPath.append(File.separator).append(buf.toString());
            else
              newPath.append(buf.toString());
            buf = new StringBuffer();
            toAdd = false;
          }
          lastChar = '\\';
          break;
        case '~':
          if (i < change.length() - 1)
          {
            if (change.charAt(i + 1) == '\\' || change.charAt(i + 1) == '/')
              newPath = new StringBuffer(getHomeDirectory());
            else
              buf.append('~');
          } else if (i == 0)
            newPath = new StringBuffer(getHomeDirectory());
          else
            buf.append('~');
          lastChar = '~';
          break;
        default:
          lastChar = current;
          buf.append(current);
          toAdd = true;
          break;
      }
    }

    if (toAdd)
    {
      char c = newPath.charAt(newPath.length() - 1);
      if (c != '\\' && c != '/')
        newPath.append(File.separator).append(buf.toString());
      else
        newPath.append(buf.toString());
    }

    return newPath.toString();
  }

  /**
   * It can be necessary to check if a path specified by the user is an absolute
   * path (i.e C:\Gfx\3d\Utils is absolute whereas ..\Jext is relative).
   * @param path The path to check
   * @return <code>true</code> if <code>path</code> begins with a root name
   */

  public static boolean beginsWithRoot(String path)
  {
    if (path.length() == 0)
      return false;

    File file = new File(path);
    Class clazz = file.getClass();
    try
    {
      Class[] signature = new Class[0];
      Method method = clazz.getMethod("listRoots", signature);
      Object[] args = new Object[0];
      File[] roots = (File[]) method.invoke(file, args);
      for (int i = 0; i < roots.length; i++)
        if (path.regionMatches(true, 0, roots[i].getPath(), 0, roots[i].getPath().length()))
          return true;
      return false;
    } catch (Exception e) {
      if (path.charAt(0) == '/')
        return true;
      else if (path.length() >= 3 && path.charAt(1) == ':' && path.charAt(2) == '\\')
        return true;
      else
        return false;
    }
  }

  /**
   * Returns user directory.
   */

  public static String getUserDirectory()
  {
    return System.getProperty("user.dir");
  }

  /**
   * Returns user's home directory.
   */

  public static String getHomeDirectory()
  {
    return System.getProperty("user.home");
  }

  /**
   * It can be necessary to determine which is the root of a path.
   * For example, the root of D:\Projects\Java is D:\.
   * @param path The path used to get a root
   * @return The root which contais the specified path
   */

  public static String getRoot(String path)
  {
    File file = new File(path);
    Class clazz = file.getClass();
    try
    {
      Class[] signature = new Class[0];
      Method method = clazz.getMethod("listRoots", signature);
      Object[] args = new Object[0];
      File[] roots = (File[]) method.invoke(file, args);
      for (int i = 0; i < roots.length; i++)
        if (path.startsWith(roots[i].getPath())) return roots[i].getPath();
      return path;
    } catch (Exception e) {
      if (path.charAt(0) == '/')
        return "/";
      else if (path.length() >= 3 && path.charAt(1) == ':' && path.charAt(2) == '\\')
        return path.substring(0, 3);
      else
        return "/";
    }
  }

  /**
   * When the user has to specify file names, he can use wildcards (*, ?). This methods
   * handles the usage of these wildcards.
   * @param s Wilcards
   * @param sort Set to true will sort file names
   * @return An array of String which contains all files matching <code>s</code>
   * in current directory.
   */

  public static String[] getWildCardMatches(String s, boolean sort)
  {
    return getWildCardMatches(null, s, sort);
  }

   /**
   * When the user has to specify file names, he can use wildcards (*, ?). This methods
   * handles the usage of these wildcards.
   * @param path The path were to search
   * @param s Wilcards
   * @param sort Set to true will sort file names
   * @return An array of String which contains all files matching <code>s</code>
   * in current directory.
   */

  public static String[] getWildCardMatches(String path, String s, boolean sort)
  {
    String args = new String(s.trim());
    String files[];
    Vector filesThatMatchVector = new Vector();
    String filesThatMatch[];

    if (path == null)
      files = (new File(getUserDirectory())).list();
    else
      files = (new File(path)).list();

    for (int i = 0; i < files.length; i++)
    {
      if (match(args, files[i]))
      {
        File temp = new File(getUserDirectory(), files[i]);
        filesThatMatchVector.addElement(new String(temp.getName()));
      }
    }

    filesThatMatch = new String[filesThatMatchVector.size()];
    filesThatMatchVector.copyInto(filesThatMatch);

    if (sort) sortStrings(filesThatMatch);

    return filesThatMatch;
  }

  /**
   * This method can determine if a String matches a pattern of wildcards
   * @param pattern The pattern used for comparison
   * @param string The String to be checked
   * @return true if <code>string</code> matches <code>pattern</code>
   */

  public static boolean match(String pattern, String string)
  {
    for (int p = 0; ; p++)
    {
      for (int s = 0; ; p++, s++)
      {
        boolean sEnd = (s >= string.length());
        boolean pEnd = (p >= pattern.length() || pattern.charAt(p) == '|');
        if (sEnd && pEnd)
          return true;
        if (sEnd || pEnd)
          break;
        if (pattern.charAt(p) == '?')
          continue;
        if (pattern.charAt(p) == '*')
        {
          int i;
          p++;
          for (i = string.length(); i >= s; --i)
            if (match(pattern.substring(p), string.substring(i))) return true;
          break;
        }
        if (pattern.charAt(p) != string.charAt(s))
          break;
      }
      p = pattern.indexOf('|', p);
      if (p == -1)
        return false;
    }
  }

  /**
   * Quick sort an array of Strings.
   * @param string Strings to be sorted
   */

  public static void sortStrings(String[] strings)
  {
    sortStrings(strings, 0, strings.length - 1);
  }

  /**
   * Quick sort an array of Strings.
   * @param a Strings to be sorted
   * @param lo0 Lower bound
   * @param hi0 Higher bound
   */

  public static void sortStrings(String a[], int lo0, int hi0)
  {
    int lo = lo0;
    int hi = hi0;
    String mid;

    if (hi0 > lo0)
    {
      mid = a[(lo0 + hi0) / 2];

      while (lo <= hi)
      {
        while (lo < hi0 && a[lo].compareTo(mid) < 0)
          ++lo;

        while (hi > lo0 && a[hi].compareTo(mid) > 0)
          --hi;

        if (lo <= hi)
        {
          swap(a, lo, hi);
          ++lo;
          --hi;
        }
      }

      if (lo0 < hi)
        sortStrings(a, lo0, hi);

      if (lo < hi0)
        sortStrings(a, lo, hi0);
    }
  }

  /**
   * Swaps two Strings.
   * @param a The array to be swapped
   * @param i First String index
   * @param j Second String index
   */

  public static void swap(String a[], int i, int j)
  {
    String T;
    T = a[i];
    a[i] = a[j];
    a[j] = T;
  }

  /**
   * Turns special chars into escape sequences.
   */

  public static String unescape(String in)
  {
    StringBuffer buf = new StringBuffer(in.length());
    char c = '\0';
    for (int i = 0; i < in.length(); i++)
    {
      switch(c = in.charAt(i))
      {
        case '\\':
          buf.append('\\');
          buf.append('\\');
          break;
        case '\"':
          buf.append('\\');
          buf.append('"');
          break;
        case '\'':
          buf.append('\\');
          buf.append('\'');
          break;
        case '\n':
          buf.append('\\');
          buf.append('n');
          break;
        case '\r':
          buf.append('\\');
          buf.append('r');
          break;
        default:
          buf.append(c);
      }
    }
    return buf.toString();
  }

  /**
   * Parses a string and turn common escape sequences into special chars like
   * \n (carriage return) \t (tab space) \\ (single \ character) ...
   * @param in The <code>String</code> to be parsed
   */

  public static String escape(String in)
  {
    StringBuffer _out = new StringBuffer(in.length());
    char c = '\0';
    for (int i = 0; i < in.length(); i++)
    {
      switch(c = in.charAt(i))
      {
        case '\\':
          if (i < in.length() - 1)
          {
            char p = '\0';
            switch(p = in.charAt(++i))
            {
              case 'n':
                _out.append('\n');
                break;
              case 'r':
                _out.append('\r');
                break;
              case 't':
                _out.append('\t');
                break;
              case '"':
                _out.append('\"');
                break;
              case '\'':
                _out.append('\'');
                break;
              case '\\':
                _out.append('\\');
                break;
              default:
                _out.append('\\').append(p);
            }
          } else
            _out.append(c);
          break;
        default:
          _out.append(c);
          break;
      }
    }
    return _out.toString();
  }
}

// End of DawnUtilities.java
