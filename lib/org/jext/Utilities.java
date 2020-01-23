/*
 * 05/25/2001 - 22:06:52
 *
 * Utilities.java - Some utilities for Jext and its classes
 * Copyright (C) 1999-2000 Romain Guy
 * romain.guy@jext.org
 * www.jext.org
 * Portions Copyright (C) 2003 Paolo Giarrusso
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

package org.jext;

import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.net.URL;
import java.net.URLConnection;
import org.jext.misc.SwingWorker;
import org.jext.misc.HandlingRunnable;
import org.jext.misc.CopyThread;
import org.jext.misc.DownloaderThread;

/**
 * This class contains a bunch of methods, useful for the programmer.
 * @author Romain Guy, Slava Pestov, James Gosling, Paolo Giarrusso
 * @version 2.3.0
 * @see Jext
 */

public class Utilities
{
  /** This constant defines an open dialog box. */
  public static final int OPEN = 0;
  /** This constant defines a save dialog box. */
  public static final int SAVE = 1;
  /** This constant defines an open dialog box. */
  public static final int SCRIPT = 2;

  /** JDK release version. */
  public static final String JDK_VERSION = System.getProperty("java.version");

  /**
   * Display a sample message in a dialog box.
   * @param message The message to display
   */

  public static void showMessage(String message)
  {
    JOptionPane.showMessageDialog(null, message, Jext.getProperty("utils.message"),
                                  JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Display an error message in a dialog box.
   * @param message The message to display
   */

  public static void showError(String message)
  {
    JOptionPane.showMessageDialog(null, message, Jext.getProperty("utils.error"),
                                  JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Display a sample message in a dialog box.
   * @param message The message to display
   */

  public static void showMessage(String title, String message)
  {
    JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * This methods is used to determine screen's dimensions.
   * @return A <code>Dimension</code> object containing screen's resolution
   */

  public static Dimension getScreenDimension()
  {
    return Jext.getMyToolkit().getScreenSize();
  }

  /**
   * A very nice trick is to center windows on screen, this method
   * helps you to to that.
   * @param compo The <code>Component</code> to center
   */

  public static void centerComponent(Component compo)
  {
    compo.setLocation(new Point((getScreenDimension().width - compo.getSize().width) / 2,
                      (getScreenDimension().height - compo.getSize().height) / 2));
  }

  /**
   * A very nice trick is to center dialog with their parent.
   * @param parent The parent <code>Component</code>
   * @param child The <code>Component</code> to center
   */

  public static void centerComponentChild(Component parent, Component child)
  {
    Rectangle par = parent.getBounds();
    Rectangle chi = child.getBounds();
    child.setLocation(new Point(par.x + (par.width - chi.width) / 2,
                                par.y + (par.height - chi.height) / 2));
  }

  /**
   * Converts a clas name to a file name. All periods are replaced
   * with slashes and the '.class' extension is added.
   * @param name The class name
   */

  public static String classToFile(String name)
  {
    return name.replace('.', '/').concat(".class");
  }

  /**
   * Converts a file name to a class name. All slash characters are
   * replaced with periods and the trailing '.class' is removed.
   * @param name The file name
   */

  public static String fileToClass(String name)
  {
    char[] clsName = name.toCharArray();
    for (int i = clsName.length - 6; i >= 0; i--)
      if (clsName[i] == '/')
        clsName[i] = '.';
    return new String(clsName, 0, clsName.length - 6);
  }

  /**
   * Used to 'beep' the user.
   */

  public static void beep()
  {
    Jext.getMyToolkit().beep();
  }

  /**
   * Long operations need to display an hourglass.
   * @param comp The <code>JComponent</code> on which to apply the hour glass cursor
   * @param on If true, we set the cursor on the hourglass
   */

  public static void setCursorOnWait(Component comp, boolean on)
  {
    if (on)
    {
      if (comp instanceof JextFrame)
        ((JextFrame) comp).showWaitCursor();
      else
        comp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    } else {
      if (comp instanceof JextFrame)
        ((JextFrame) comp).hideWaitCursor();
      else
        comp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
  }

  /**
   * We may need to load and display images.
   * @param picture The path to the image
   * @param source The class 'root'
   * @return An <code>ImageIcon</code>
   */

  public static ImageIcon getIcon(String picture, Class source)
  {
    return new ImageIcon(Jext.getMyToolkit().getImage(source.getResource(picture)));
  }

  /**
   * We may need to load and display images.
   * @param picture The path to the image
   * @param source The class 'root'
   * @return An <code>Image</code>
   */

  public static Image getImage(String picture, Class source)
  {
    return Jext.getMyToolkit().getImage(source.getResource(picture));
  }

  /**
   * Display a file chooser dialog box and returns selected files.
   * @param owner <code>Component</code> which 'owns' the dialog
   * @param mode Can be either <code>OPEN</code>, <code>SCRIPT</code> or <code>SAVE</code>
   * @return The path to selected file, null otherwise
   */

  public static String[] chooseFiles(Component owner, int mode)
  {
    if (JDK_VERSION.charAt(2) <= '2')
      return new String[] { chooseFile(owner, mode) };

    JFileChooser chooser = getFileChooser(owner, mode);
    chooser.setMultiSelectionEnabled(true);

    if (chooser.showDialog(owner, null) == JFileChooser.APPROVE_OPTION)
    {
      Jext.setProperty("lastdir." + mode, chooser.getSelectedFile().getParent());

      File[] _files = chooser.getSelectedFiles();
      if (_files == null)
        return null;

      String[] files = new String[_files.length];
      for (int i = 0; i < files.length; i++)
        files[i] = _files[i].getAbsolutePath();

      return files;
    } else
      owner.repaint();

    return null;
  }

  /**
   * Display a file chooser dialog box.
   * @param owner <code>Component</code> which 'owns' the dialog
   * @param mode Can be either <code>OPEN</code>, <code>SCRIPT</code> or <code>SAVE</code>
   * @return The path to selected file, null otherwise
   */

  public static String chooseFile(Component owner, int mode)
  {
    JFileChooser chooser = getFileChooser(owner, mode);
    chooser.setMultiSelectionEnabled(false);

    if (chooser.showDialog(owner, null) == JFileChooser.APPROVE_OPTION)
    {
      Jext.setProperty("lastdir." + mode, chooser.getSelectedFile().getParent());
      return chooser.getSelectedFile().getAbsolutePath();
    } else
      owner.repaint();

    return null;
  }

  private static JFileChooser getFileChooser(Component owner, int mode)
  {
    JFileChooser chooser = null;
    String last = Jext.getProperty("lastdir." + mode);
    if (last == null)
      last = Jext.getHomeDirectory();

    if (owner instanceof JextFrame)
    {
      chooser = ((JextFrame) owner).getFileChooser(mode);
      if (Jext.getBooleanProperty("editor.dirDefaultDialog") && mode != SCRIPT)
      {
        String file = ((JextFrame) owner).getTextArea().getCurrentFile();
        if (file != null)
          chooser.setCurrentDirectory(new File(file));
      } else
        chooser.setCurrentDirectory(new File(last));
    } else {
      chooser = new JFileChooser(last);
      if (mode == SAVE)
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
      else
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
    }

    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setFileHidingEnabled(true);

    return chooser;
  }

  /**
   * Create a blank String made of spaces.
   * @param len Amount of spaces contained in the String
   * @return A blank <code>String</code>
   */

  public static String createWhiteSpace(int len)
  {
    return createWhiteSpace(len, 0);
  }

  /**
   * Create a blank String made of tabs.
   * @param len Amount of spaces contained in the String
   * @param tabSize Tabulation size
   * @return A blank <code>String</code>
   */

  public static String createWhiteSpace(int len, int tabSize)
  {
    StringBuffer buf = new StringBuffer();

    if (tabSize == 0)
    {
      while(len-- > 0)
        buf.append(' ');
    } else {
      int count = len / tabSize;
      while(count-- > 0)
        buf.append('\t');

      count = len % tabSize;
      while(count-- > 0)
        buf.append(' ');
    }

    return buf.toString();
  }

  /**
   * Returns the number of leading white space characters in the
   * specified string.
   * @param str The string
   */

  public static int getLeadingWhiteSpace(String str)
  {
    int whitespace = 0;
    loop: for( ; whitespace < str.length(); )
    {
      switch(str.charAt(whitespace))
      {
        case ' ': case '\t':
          whitespace++;
          break;
        default:
          break loop;
      }
    }
    return whitespace;
  }

  /**
   * Returns the width of the leading white space in the specified
   * string.
   * @param str The string
   * @param tabSize The tab size
   */

  public static int getLeadingWhiteSpaceWidth(String str, int tabSize)
  {
    int whitespace = 0;
    loop: for (int i = 0; i < str.length(); i++)
    {
      switch(str.charAt(i))
      {
        case ' ':
          whitespace++;
          break;
        case '\t':
          whitespace += (tabSize - whitespace % tabSize);
          break;
        default:
          break loop;
      }
    }
    return whitespace;
  }

  public static int getRealLength(String str, int tabSize)
  {
    int pos = 0;
    for (int i = 0; i < str.length(); i++)
    {
      switch(str.charAt(i))
      {
        case '\t':
          pos += tabSize;
          break;
        default:
          pos++;
      }
    }
    return pos;
  }

  /**
   * Some String can be too long to be correctly displayed on the screen.
   * Mainly when it is a path to a file. This method truncate a String.
   * @param longString The <code>String</code> to be truncated
   * @param maxLength The maximum length of the <code>String</code>
   * @return The truncated string
   */

  public static String getShortStringOf(String longString, int maxLength)
  {
    int len = longString.length();

    if (len <= maxLength)
      return longString;
    else if (longString.indexOf('\\') == -1 && longString.indexOf('/') == -1)
    {
      StringBuffer buff = new StringBuffer(longString.substring(longString.length() - maxLength));
      for(int i =0; i < 3; i++)
          buff.setCharAt(i, '.');
      return  buff.toString();
    } else {
      int first = len / 2;
      int second = first;

      for (int i = first - 1; i >= 0; i--)
      {
        if (longString.charAt(i) == '\\' || longString.charAt(i) == '/')
        {
          first = i;
          break;
        }
      }

      for (int i = second + 1; i < len; i++)
      {
        if (longString.charAt(i) == '\\' || longString.charAt(i) == '/')
        {
          second = i;
          break;
        }
      }

loop: while ((len - (second - first)) > maxLength)
      {
out:    for (int i = first - 1; i >= 0; i--)
        {
          switch (longString.charAt(i))
          {
            case '\\': case '/':
              first = i;
              break out;
          }
        }

        if ((len - (second - first)) < maxLength)
          break loop;

out2:   for (int i = second + 1; i < len; i++)
        {
          switch (longString.charAt(i))
          {
            case '\\': case '/':
              second = i;
              break out2;
          }
        }
      }

      return longString.substring(0, first + 1) + "..." + longString.substring(second);

      //return longString.substring(0, maxLength / 2) + "..." +
      //       longString.substring(len - (maxLength / 2));
    }
  }

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
          {
            newPath = new StringBuffer(getRoot(newPath.toString()));
          } else {
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
    File[] roots = file.listRoots();
    for (int i = 0; i < roots.length; i++)
      if (path.regionMatches(true, 0, roots[i].getPath(), 0, roots[i].getPath().length()))
        return true;
    return false;
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
    File[] roots = file.listRoots();
    for (int i = 0; i < roots.length; i++)
      if (path.startsWith(roots[i].getPath()))
        return roots[i].getPath();
    return path;
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
   * @return An array of String which contains all file names matching <code>s</code>
   * in <code>path</code> directory.
   */

  public static String[] getWildCardMatches(String path, String s, boolean sort)
  {
    if (s == null)
      return null;

    String files[];
    String filesThatMatch[];
    String args = new String(s.trim());
    ArrayList filesThatMatchVector = new ArrayList();
    File fPath;

    if (path == null || path == "")
      fPath = new File(getUserDirectory());
    else {
      fPath = new File(path);
      if (! fPath.isAbsolute())
        fPath = new File(getUserDirectory(), path);
    }

    files = fPath.list();
    if (files == null)
      return null;

    for (int i = 0; i < files.length; i++)
    {
      if (match(args, files[i]))
      {
        //File temp = new File(getUserDirectory(), files[i]);
        File temp = new File(path, files[i]);
        filesThatMatchVector.add(new String(temp.getName()));
      }
    }

    filesThatMatch = (String[]) filesThatMatchVector.toArray(new String[filesThatMatchVector.size()]);
    filesThatMatchVector = null;

    if (sort)
      Arrays.sort(filesThatMatch);

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
        int end = pattern.indexOf('|', p);
        if (end == -1)
          end = pattern.length();
        if (sEnd && ! pEnd && pattern.substring(p, end).equals("*"))
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
   * @deprecated Use the standard Java java.util.Array.sort instead.
   */

  public static void sortStrings(String[] strings)
  {
    Arrays.sort(strings);
  }
  //The below code is left here, even if it is useless, because maybe there are 
  //plugins which need it. sortStrings is needed by jftp, so it's there.
  //Remember that sortStrings is listed in the docs, so you can't remove it.

  /*
   * Quick sort an array of Strings.
   * @param a Strings to be sorted
   * @param lo0 Lower bound
   * @param hi0 Higher bound
   */

  /*public static void sortStrings(String a[], int lo0, int hi0)
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
  }*/

  /*
   * Swaps two Strings.
   * @param a The array to be swapped
   * @param i First String index
   * @param j Second String index
   */

  /*public static void swap(String a[], int i, int j)
  {
    String T;
    T = a[i];
    a[i] = a[j];
    a[j] = T;
  }*/

  /**
   * Lists content of a directory.
   * @param names Names of the files
   * @param construct Set it to true if names does not contain full paths
   * @return An array of Files
   */

  public static File[] listFiles(String[] names, boolean construct)
  {
    return listFiles(names, null, construct);
  }

  /**
   * Lists content of a directory.
   * @param names Names of the files
   * @param path Base path for files
   * @param construct Set it to true if names do not contain full paths
   * @return An array of Files
   */

  public static File[] listFiles(String[] names, String path, boolean construct)
  {
    if (names == null)
      return null;

    File fPath;
    if (path == null || path == "")
      fPath = new File(getUserDirectory());
    else {
      fPath = new File(path);
      if (! fPath.isAbsolute())
        fPath = new File(getUserDirectory(), path);
    }

    File[] files = new File[names.length];

    for (int i = 0; i < files.length; i++)
    {
      if (construct)
        files[i] = new File(fPath, names[i]);
      else
        files[i] = new File(names[i]);
    }

    return files;
  }

  /**
   * Turns a Un*x glob filter to regexp one
   * @param glob Globbed filter
   */

  public static String globToRE(String glob)
  {
    char c = '\0';
    boolean escape = false, enclosed = false;
    StringBuffer _buf = new StringBuffer(glob.length());

    for (int i = 0; i < glob.length(); i++)
    {
      c = glob.charAt(i);

      if (escape)
      {
        _buf.append('\\');
        _buf.append(c);
        escape = false;
        continue;
      }

      switch(c)
      {
        case '*':
          _buf.append('.').append('*');
          break;
        case '?':
          _buf.append('.');
          break;
        case '\\':
          escape = true;
          break;
        case '.':
          _buf.append('\\').append('.');
          break;
        case '{':
          _buf.append('(');
          enclosed = true;
          break;
        case '}':
          _buf.append(')');
          enclosed = false;
          break;
        case ',':
          if (enclosed)
            _buf.append('|');
          else
            _buf.append(',');
          break;
        default:
          _buf.append(c);
      }
    }
    return _buf.toString();
  }

  /*public static void downloadFile(URL source, String outPath, boolean threaded,
      HandlingRunnable notify) throws IOException {
    try {

      //String tempPath = outPath + "__FRAG__";
      //final File outFile = new File(outPath), tempFile = new File(tempPath);
      //FIXME: think about the case below. The caller must avoid that we download the file 2 times.
      //Not us!
      /*if (tempFile.exists())
        tempFile.renameTo(new File(tempPath + ".bak")); //Could fail and return false!*/

      /*HandlingRunnable renamer = new HandlingRunnable() {
        public void run() {
          if (expectedLen != -1 && expectedLen != tempFile.length()) {
            //exceptional condition:
            if (excep == null && notify != null)
              notify.setException(new IOException("The download was not completed"));
          }
          if (outFile.exists()) {
            outFile.renameTo(new File(outPath + ".bak")); //Could fail and return false!
            outFile.delete();
          }
          tempFile.renameTo(outFile);
          if (notify != null) {
            notify.setException(excep); //we won't handle it: this will be done by notify.
            notify.run();
          }
        } //run method end
      };
      copy(threaded, renamer);
    } catch (IOException ioe) {
      try {
        if (in != null)
          in.close();//if things go well, copy() closes the streams!
      } catch (IOException ioe2) {}

      throw ioe;
    }
  }*/

  /**
   * @since Jext 3.2pre4
   */
  /*public static void downloadFile(URL source, String outPath, boolean threaded) throws IOException {
    downloadFile(source, outPath, threaded, null);
  }*/

  /**
   * Downloads the file specified in the URL to the File with the <code>outPath</code> path using
   * copy() (so see copy() for infos about notify and threaded).
   * @since Jext 3.2pre4
   */
  public static void downloadFile(URL source, String outPath, boolean threaded,
      HandlingRunnable notify) /*throws IOException*/ {
    DownloaderThread downloader = new DownloaderThread(source, notify, outPath);
    /*try {
      if (threaded)
        downloader.start();
      else
        downloader.run();
    } catch (Throwable t) {
      throw (IOException) t;
    }*/
    downloader.start(threaded);
  }
  /**
   * Convenience method for calling 
   * @link{#copy(java.io.InputStream,java.io.OutputStream,boolean,org.jext.misc.HandlingRunnable)} passing a null 
   * <code>notify</code>callback.
   * @since Jext 3.2pre4
   */
  /*public static void copy(final InputStream in, final OutputStream out, boolean threaded) throws IOException {
    copy(in, out, threaded, null);
  }*/

  /**
   * This method copy the content of the InputStream in to the OutputStream out, in the calling thread or
   * in a new one.
   * If threaded is true, a new thread is created, and notify will be called at the end, and will have passed
   * the exception eventually thrown while doing the copy.
   * Otherwise, the copy is done synchronously; if an exception is thrown during the copy it is dispatched to the calling 
   * method, otherwise the notify is called.
   * @since Jext 3.2pre4
   */
  public static void copy(InputStream in, OutputStream out, boolean threaded,
      HandlingRunnable notify) throws IOException {
    CopyThread copier = new CopyThread(in, out, notify);
    /*try {
      if (threaded)
        copier.start();
      else
        copier.run();
    } catch (Throwable t) {
      throw (IOException) t;
    }*/
    copier.start(threaded);
  }
}

// End of Utilities.java
