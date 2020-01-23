/*
 * 06/13/2003
 *
 * PluginDesc.java - Represents a plugin which can be downloaded.
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

import org.jext.Jext;
import org.jext.Utilities;

import java.net.*;
import java.io.*;
import java.text.Format;

import java.util.zip.*;
import java.util.*;

public class PluginDesc {

  //these are the datas which are stored. This class is mainly a get/set method collector.
  private String srcName, binName;
  private int srcSize, binSize;
  private String desc;
  private String name;
  private String displayName;
  private String release;
  private PluginAuthor[] authors;
  private String[] deps;

  /**this is the format string which is used to build the Url using the mirror and file names*/
  private Format format;

  //maybe an additional fields with the jar name to check if it is already installed?

  //FIXME: add fields to remember whether a plugin has been downloaded in this session; give errors if redownloading,
  //offering to do it anyway.
  private boolean binDownloaded;
  private boolean srcDownloaded;
  //User-specific installation folders.
  private static final String LOCAL_DOWNLOAD_PATH   = Jext.SETTINGS_DIRECTORY + "downloadedPlugins" + File.separator; 
  private static final String LOCAL_DOC_PATH        = Jext.SETTINGS_DIRECTORY + "doc" + File.separator;
  private static final String LOCAL_BINLIBRARY_PATH = Jext.SETTINGS_DIRECTORY + "bin" + File.separator; 
  private static final String LOCAL_LIBRARY_PATH    = Jext.SETTINGS_DIRECTORY + "lib" + File.separator; 
  private static final String LOCAL_PLUGINS_PATH    = Jext.SETTINGS_DIRECTORY + "plugins" + File.separator; 

  //System-wide installation folders.
  private static final String SYSTEM_DOWNLOAD_PATH   = Jext.JEXT_HOME +
    File.separator + "downloadedPlugins" + File.separator; 
  private static final String SYSTEM_DOC_PATH        = Jext.JEXT_HOME +
    File.separator + "doc" + File.separator;
  private static final String SYSTEM_BINLIBRARY_PATH = Jext.JEXT_HOME +
    File.separator + "bin" + File.separator; 
  private static final String SYSTEM_LIBRARY_PATH    = Jext.JEXT_HOME +
    File.separator + "lib" + File.separator; 
  private static final String SYSTEM_PLUGINS_PATH    = Jext.JEXT_HOME +
    File.separator + "plugins" + File.separator; 

  //This is an enumeration of path positions inside the array.
  private static final int DOWNLOAD_PATH_ID               = 0;
  private static final int DOC_PATH_ID                    = 1;
  private static final int BINLIBRARY_PATH_ID             = 2;
  private static final int LIBRARY_PATH_ID                = 3;
  private static final int PLUGINS_PATH_ID                = 4;

  private static final String[] LOCAL_PATHS = {
    LOCAL_DOWNLOAD_PATH, LOCAL_DOC_PATH, LOCAL_BINLIBRARY_PATH, LOCAL_LIBRARY_PATH, LOCAL_PLUGINS_PATH };

  private static final String[] SYSTEM_PATHS = {
    SYSTEM_DOWNLOAD_PATH, SYSTEM_DOC_PATH, SYSTEM_BINLIBRARY_PATH, SYSTEM_LIBRARY_PATH, SYSTEM_PLUGINS_PATH };

  private static String[] PATHS = LOCAL_PATHS; //FIXME: this should be true on Windows 9x system.

  public String toString() {
    return "Name: " + name + "; displayName: " + displayName + "; binSize: " +  binSize;
  }

  public PluginDesc(String _name, String _release, String _displayName) {
    name = _name;
    release = _release;
    displayName = _displayName;
  }

  /**
   * Return the "local installation" flag. We say that a plugin is installed
   * <i>locally</i> if it is put in the user-specific <code>Jext.SETTINGS_DIRECTORY</code>,
   * (i.e. .jext); otherwise, it is installed in the <code>Jext.JEXT_HOME</code>.
   * Call only from the AWT EventQueue thread.
   * @return The value of the "local installation" flag.
   * @see #setLocalInstallation(boolean)
   */
  public static boolean isLocalInstallation() {
    return PATHS == LOCAL_PATHS;
  }

  /**
   * Sets the "local installation" flag.
   * Call only from the AWT EventQueue thread.
   * @see #isLocalInstallation() for the meaning of this flag.
   */
  public static void setLocalInstallation(boolean local) {
    PATHS = local ? LOCAL_PATHS : SYSTEM_PATHS;
  }

  /**
   * This method tries to build all folders used by PluginGet. CHECKED
   */
  /* friendly */ static boolean initDirectories() {
    boolean result = true;
    for (int i = 0; i < LOCAL_PATHS.length; i++) {
      StringBuffer sb = new StringBuffer(LOCAL_PATHS[i]);
      sb.deleteCharAt(sb.length() - 1); //delete the final Char.
      File currDir = new File(sb.toString());
      if (currDir.exists()) {
        if(!currDir.isDirectory())
          currDir.renameTo(new File( sb.append(".bak").toString() )); //preserve possible existing files.
        else
          continue;
      }
      result = currDir.mkdir() && result; //it keeps on going, however.
    }
    return result;
  }

  //FIXME: instead of using all these "notify"-back runnables, let's establish
  //one general purpose logger to use. Maybe(runnables can have other side effects).
  //But let's keep them only as possibility, adding another logging way.

  //FIXME: passing the mirror is broken, since we must have a list of mirror, when one of them fails.
  public void downloadSrc(HandlingRunnable notifier, String mirror) {
    URL url = getSrcUrl(mirror);
    System.out.println(url.toString());
    Utilities.downloadFile(url, PATHS[DOWNLOAD_PATH_ID] + srcName, true, notifier);
    //srcDownloaded = true;
  }

  public void downloadBin(HandlingRunnable notifier, String mirror) {
    URL url = getBinUrl(mirror);
    System.out.println(url.toString());
    Utilities.downloadFile(url, PATHS[DOWNLOAD_PATH_ID] + binName, true, notifier);
    //binDownloaded = true;
  }

  public void install(Runnable notifyMissing) throws IOException {
    install(notifyMissing, PATHS[DOWNLOAD_PATH_ID] + binName);
  }

  public void install(Runnable notifyMissing, String path) throws IOException {
    ZipFile zip = null;
    System.out.println("Path is : " + path);
    try {
      File file = new File(path);
      if (!file.exists()) {
        if (notifyMissing != null)
          notifyMissing.run();
        return;
      }
      zip = new ZipFile(file);
      Enumeration entries = zip.entries();
      while (entries.hasMoreElements()) {
        ZipEntry entry = (ZipEntry) entries.nextElement();
        String entryName = entry.getName();

        if (entry.isDirectory()) { //For instance the themes directory for SkinLF.
          File outDir = new File(PATHS[BINLIBRARY_PATH_ID] + entryName);
          if (!outDir.exists())
            outDir.mkdir();
          continue;
        }

        InputStream in = new BufferedInputStream(zip.getInputStream(entry));
        int len = entryName.length();
        String outPath = null, plugJar = name + ".jar";
        if (entryName.indexOf(File.separatorChar) != -1) { //i.e. it is under a folder!!
          outPath = PATHS[BINLIBRARY_PATH_ID]; //but many plugins would require installing themselves
          //within system bin folder. How to solve this? FIXME
        } else if (entryName.endsWith(".jar")) {
          if (entryName.equals(plugJar)) //the plugin's jar has the same name as the plugin,
          //while the other jars are libraries.
            outPath = PATHS[PLUGINS_PATH_ID];
          else
            outPath = PATHS[LIBRARY_PATH_ID];
        } else if (entryName.endsWith(".dll") || entryName.endsWith(".so")) {
          outPath = PATHS[BINLIBRARY_PATH_ID];
        } else if (entryName.endsWith(".txt")) {
          outPath = PATHS[DOC_PATH_ID] + name + File.separator; //for text files; we create a doc/<plugName> folder.
          (new File(outPath)).mkdir();
        } else { //Other files.
          outPath = PATHS[BINLIBRARY_PATH_ID];//??
        }
        if (outPath != null) {
          File outFile = new File(outPath + entryName);
          if (outFile.exists())
            outFile.renameTo(new File(outPath + entryName + ".bak")); //Could fail and return false!
          OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
          Utilities.copy(in, out, false, null);
        }
      }
    } catch (IOException e) {//FIXME: Utilities.copy() doesn't throw any more any exception.
      throw e;
    } finally {
      if (zip != null) {
        try {
          zip.close();
        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
      }
    }
  }

  /** Returns the url of the source pack of this plugin*/
  public URL getSrcUrl(String mirror) {
    try {
      return new URL (format.format(new String[] {mirror, srcName}));
    } catch (MalformedURLException mue) {
      return null;
    }
  }

  /** Returns the url of the binary pack of this plugin*/
  public URL getBinUrl(String mirror) {
    try {
      return new URL (format.format(new String[] {mirror, binName}));
    } catch (MalformedURLException mue) {
      return null;
    }
  }

  //--FIELDS part.
  //SET

  public void setName(String _name) {
    name = _name;
  }

  public void setDisplayName(String _displayName) {
    displayName = _displayName;
  }

  public void setRelease(String _release) {
    release = _release;
  }

  public void setDesc(String _desc) {
    desc = _desc;
  }

  public void setSrcName(String _srcName, int _size) {
    srcName = _srcName;
    srcSize = _size;
  }

  public void setBinName(String _binName, int _size) {
    binName = _binName;
    binSize = _size;
  }

  public void setUrlFormatter(Format _format) {
    format = _format;
  }

  public void setAuthors(PluginAuthor[] _authors) {
    authors = _authors;
  }

  public void setDeps(String[] _deps) {
    deps = _deps;
  }

  //GET

  public String getDisplayName() {
    return displayName;
  }

  public int getBinSize() {
    return binSize;
  }

  public int getSrcSize() {
    return srcSize;
  }

  public String getName() {
    return name;
  }

  public String getDesc() {
    return desc;
  }

  public String getRelease() {
    return release;
  }

  public PluginAuthor[] getAuthors() {
    return authors;
  }

  public String[] getDeps() {
    return deps;
  }
}
