/*
 * JARClassLoader.java - Loads classes from JAR files
 * Copyright (C) 1999 Slava Pestov
 * Portions copyright (C) 1999 mike dillon, (C) 2000 Romain Guy
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
import java.net.*;

import java.util.*;
import java.util.zip.*;

import javax.swing.JOptionPane;

import java.lang.reflect.Modifier;

/**
 * A class loader implementation that loads classes from JAR files.
 * @author Slava Pestov
 */

public class JARClassLoader extends ClassLoader
{
  public static ArrayList pluginsNames = new ArrayList();
  //public static ArrayList pluginsRealNames = new ArrayList();
  /* This is the prefix for entries inside .jar's containing only translations.
   * I.e., .xml files for translations must be inside trans/&lt;languagecode&gt;/
   * folder. Note that any file which name starts with "trans" will be ignored.
   * The prefix here was "trans" + File.separator, but entries were always read
   * with forward slashes(I don't know why, it's a characteristic of ZIP file,
   * it seems)*/
  private static final String langsPrefix = "trans";

  public JARClassLoader(String path) throws IOException
  {
    this(path, true, null);
  }

  //Now I've added the parameter isPlugin(which defaults to true) to use the code
  //for the plugin downloader, which hot-loads a JAR.
  public JARClassLoader(String path, boolean isPlugin, ClassLoader parent) throws IOException
  {
    super(parent);
    url = new File(path).toURL();
    zipFile = new ZipFile(path);

    if (isPlugin)
    {
      String langSearchPrefix = langsPrefix + File.separator + Jext.getLanguage() + File.separator;
      Enumeration entries = zipFile.entries();

      while (entries.hasMoreElements())
      {
        ZipEntry entry = (ZipEntry) entries.nextElement();
        String name = entry.getName();
        String lowName = name.toLowerCase();

	//the files in trans/* dirs must be loaded only as translations;
	//this loop over entries elements must not iterate over them.
	if (lowName.startsWith(langsPrefix))
	  continue;

	if (lowName.endsWith(".props")) {
	  Jext.loadProps(zipFile.getInputStream(entry));//This could be removed(no plugin using it).
	} else if (lowName.endsWith(".props.xml")) {
	  InputStream in;
	  //We load first the untranslated one, then the translated which contains
	  //only some properties!
	  in = zipFile.getInputStream(entry);
	  Jext.loadXMLProps(in, name, false); //not to translate.

	  //First search translation inside the plugin's jar
	  ZipEntry translEntry = zipFile.getEntry(langSearchPrefix + name);
	  if (translEntry != null)
	    in = zipFile.getInputStream(translEntry);
	  else
	    //fall back to old search method.
	    in = Jext.getLanguageStream(zipFile.getInputStream(entry), name);
	  Jext.loadXMLProps(in, name, false);//already translated.
	} else if (lowName.endsWith(".actions.xml")) {
	  Jext.loadXMLActions(zipFile.getInputStream(entry), name);
	} else if (name.endsWith("Plugin.class")) {
	  pluginClasses.add(name);
	  pluginsNames.add(name);
	  //pluginsRealNames.add(baseName);
	}
      }

      // If this is done before the above while() statement
      // and an exception is thrown while the ZIP file is
      // being loaded, weird things happen...
      index = classLoaders.size();
      classLoaders.add(this);
    }
  }

  private static ArrayList disabledPlugins = new ArrayList();

  public static void setEnabled(String name, boolean toEnable) {
    /*int i = disabledPlugins.indexOf(name);
    if (toEnable) {
      if (i != -1)
        disabledPlugins.remove(i);
    } else {
      if (i == -1)
        disabledPlugins.add(name);
    }*/
    Jext.setProperty("plugin." + name + ".disabled", toEnable? "no" : "yes");
  }

  public static boolean isEnabled(String name) {
    return ! ("yes".equals(Jext.getProperty("plugin." + name + ".disabled")));
    //return disabledPlugins.indexOf(name) == -1;
  }

  /*private final static String DISABLED_LIST_PATH = Jext.SETTINGS_DIRECTORY + 
    ".disabledPlugins";
  
  static {
    try {
      File f = new File(DISABLED_LIST_PATH);
      if (f.exists()) {
        BufferedReader in = new BufferedReader(new FileReader(f));
        String line;
        while ( (line = in.readLine()) != null)
          disabledPlugins.add(line);
        in.close();
      }
    } catch (IOException ioe) {ioe.printStackTrace();}
  }

  static void saveDisabledList() {
    try {
      PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(DISABLED_LIST_PATH)));
      for (Iterator i = disabledPlugins.iterator(); i.hasNext(); ) {
        out.println((String) i.next());
      }
      out.close();
    } catch (IOException ioe) {}
  }*/

  /**
    * @exception ClassNotFoundException if the class could not be found
    */
  public Class loadClass(String clazz, boolean resolveIt) throws ClassNotFoundException
  {
    return loadClassFromZip(clazz, resolveIt, true);
  }

  public InputStream getResourceAsStream(String name)
  {
    try
    {
      ZipEntry entry = zipFile.getEntry(name);
      if (entry == null)
        return getSystemResourceAsStream(name);
      else
        return zipFile.getInputStream(entry);
    } catch (IOException io) {
      return null;
    }
  }

  public URL getResource(String name)
  {
    try
    {
      return new URL(getResourceAsPath(name));
    } catch (MalformedURLException mu) {
      return null;
    }
  }

  public String getResourceAsPath(String name)
  {
    return "jextresource:" + index + "/" + name;
  }

  public String getPath()
  {
    return zipFile.getName();
  }

  public static void initPlugins()
  {
    for (int i = 0; i < classLoaders.size(); i++)
    {
      JARClassLoader classLoader = (JARClassLoader) classLoaders.get(i);
      classLoader.loadAllPlugins();
    }
  }

  public static JARClassLoader getClassLoader(int index)
  {
    return (JARClassLoader) classLoaders.get(index);
  }

  public static int getClassLoaderCount()
  {
    return classLoaders.size();
  }

  public static void reloadPluginsProperties() throws IOException
  {
    for (int i = 0; i < classLoaders.size(); i++) {
      JARClassLoader classLoader = (JARClassLoader) classLoaders.get(i);
      ZipFile zipFile = classLoader.getZipFile();
      Enumeration entries = zipFile.entries();

      String langSearchPrefix = langsPrefix + File.separator + Jext.getLanguage() + File.separator;
      while (entries.hasMoreElements()) {
        ZipEntry entry = (ZipEntry) entries.nextElement();
        String name = entry.getName();
        String lowName = name.toLowerCase();
        if (! lowName.startsWith(langsPrefix)) {//the files in trans/* dirs must be loaded only as translations;
          //this loop over entries elements must not iterate over them.
          if (lowName.endsWith(".props"))
            Jext.loadProps(zipFile.getInputStream(entry));
          else if (lowName.endsWith(".props.xml")) {
            InputStream in;
	    //We load first the untranslated one, then the translated which contains
	    //only some properties!
            in = zipFile.getInputStream(entry);
            Jext.loadXMLProps(in, name, false); //not to translate.

            //First search translation inside the plugin's jar
            ZipEntry translEntry = zipFile.getEntry(langSearchPrefix + name);
            if (translEntry != null)
              in = zipFile.getInputStream(translEntry);
            else
              //fall back to old search method.
              in = Jext.getLanguageStream(zipFile.getInputStream(entry), name);
            Jext.loadXMLProps(in, name, false);//already translated.
          }
        }
      }
    }
  }

  public static void executeScripts(JextFrame parent)
  {
    for (int i = 0; i < classLoaders.size(); i++)
    {
      JARClassLoader classLoader = (JARClassLoader) classLoaders.get(i);
      ZipFile zipFile = classLoader.getZipFile();
      Enumeration entries = zipFile.entries();

      while (entries.hasMoreElements())
      {
        ZipEntry entry = (ZipEntry) entries.nextElement();
        String name = entry.getName().toLowerCase();
        if (name.endsWith(".jext-script") || name.endsWith(".py"))
        {
          try
          {
            BufferedReader in = new BufferedReader(
                                new InputStreamReader(zipFile.getInputStream(entry)));
            String line;
            StringBuffer buf = new StringBuffer();
            while ((line = in.readLine()) != null)
              buf.append(line).append('\n');

            if (name.endsWith(".jext-script"))
              org.jext.scripting.dawn.Run.execute(buf.toString(), parent, false);
            else
              org.jext.scripting.python.Run.execute(buf.toString(), parent);
          } catch (IOException ioe) { }
        }
      }
    }
  }

  public ZipFile getZipFile()
  {
    return zipFile;
  }

  // private members

  /*
   * Loading of plugin classes is deferred until all JARs
   * are loaded - this is necessary because a plugin might
   * depend on classes stored in other JARs.
   */
  private static ArrayList classLoaders = new ArrayList();
  private int index;
  private ArrayList pluginClasses = new ArrayList();
//  replaced fileName attribute with URL as fileName was not used, and URL is now used for package sealing.
  private URL url;
  private ZipFile zipFile;

  private void loadAllPlugins()
  {
    for (int i = 0; i < pluginClasses.size(); i++)
    {
      String name = (String) pluginClasses.get(i);

      try
      {
        loadPluginClass(name);
      } catch (Throwable t) {
        String[] args = { name };
        System.err.println(Jext.getProperty("jar.error.init", args));
        t.printStackTrace();
      }
    }
  }

  private void loadPluginClass(String name) throws Exception
  {
    name = Utilities.fileToClass(name);

    //if ("yes".equals(Jext.getProperty("plugin." + name + ".disabled")))
    if (!isEnabled(name))
    {
      String[] args = { Jext.getProperty("plugin." + name + ".name") };
      System.err.println(Jext.getProperty("jar.disabled", args));
      return;
    }

    Plugin[] plugins = Jext.getPlugins();

    for (int i = 0; i < plugins.length; i++)
    {
      if (plugins[i].getClass().getName().equals(name))
      {
        String[] args = { name };
        System.err.println(Jext.getProperty("jar.error.duplicateName", args));
        return;
      }
    }

    // Check dependencies
    if (!checkDependencies(name))
      return;

    Class clazz = loadClass(name, true);
    int modifiers = clazz.getModifiers();

    if (Plugin.class.isAssignableFrom(clazz) &&
            !Modifier.isInterface(modifiers) &&
            !Modifier.isAbstract(modifiers))
    {
      Plugin plugin = (Plugin) clazz.newInstance();
      Jext.addPlugin(plugin);

      int dot = name.lastIndexOf('.');
      name = name.substring((dot == -1 ? 0 : dot + 1));
      String[] args = { Jext.getProperty("plugin." + name + ".name") };
      System.out.println(Jext.getProperty("jar.loaded", args));
    }
  }

  private boolean checkDependencies(String name)
  {
    int i = 0;

    StringBuffer deps = new StringBuffer();

    boolean ok = true;

    String dep;
    while ((dep = Jext.getProperty("plugin." + name + ".depend." + i++)) != null)
    {
      int index = dep.indexOf(' ');
      if (index == -1)
      {
        deps.append(dep);
        deps.append('\n');
        ok = false;
        continue;
      }

      String what = dep.substring(0, index);
      String arg = dep.substring(index + 1);

      String[] args2 = new String[1];
      if (what.equals("jext"))
        args2[0] = Jext.BUILD; //Utilities.buildToVersion(arg);
      else
        args2[0] = arg;

      deps.append(Jext.getProperty("jar.what." + what, args2));
      deps.append('\n');

      if (what.equals("jdk"))
      {
        if (System.getProperty("java.version").compareTo(arg) < 0)
          ok = false;
      } else if (what.equals("deprecateJDK")) {
        if (System.getProperty("java.version").compareTo(arg) >= 0)
          ok = false;
      } else if (what.equals("jext")) {
        if (Jext.BUILD.compareTo(arg) < 0)
          ok = false;
      } else if (what.equals("os")) {
        ok = (System.getProperty("os.name").indexOf(arg) != -1);
      } else if (what.equals("class")) {
        try
        {
          loadClass(arg, false);
        } catch (Exception e) {
          ok = false;
        }
      } else
        ok = false;
    }

    if (!ok && Jext.getProperty("plugin." + name + ".disabled") == null)
    {
      int dot = name.lastIndexOf('.');
      name = name.substring((dot == -1 ? 0 : dot + 1));

      String[] _args = { Jext.getProperty("plugin." + name + ".name"), deps.toString() };
      int response = JOptionPane.showConfirmDialog(null,
                                 Jext.getProperty("plugin.disable.question", _args),
                                 Jext.getProperty("plugin.disable.title"),
                                 JOptionPane.YES_NO_OPTION,
                                 JOptionPane.QUESTION_MESSAGE);
      //Jext.setProperty("plugin." + name + ".disabled", response == 0 ? "yes" : "no");
      setEnabled(name, response == 0 ? false : true);
    }

    return ok;
  }

  private Class findOtherClass(String clazz, boolean resolveIt) throws ClassNotFoundException
  {
    for (int i = 0; i < classLoaders.size(); i++)
    {
      JARClassLoader loader = (JARClassLoader) classLoaders.get(i);
      Class cls = loader.loadClassFromZip(clazz, resolveIt, false);
      if (cls != null)
        return cls;
    }

    /* Defer to whoever loaded us (such as JShell, Echidna, etc) */
    ClassLoader loader = getClass().getClassLoader();
    if (loader != null)
      return loader.loadClass(clazz);

    /* Doesn't exist in any other plugin, look in system classes */
    return findSystemClass(clazz);
  }

  private Class loadClassFromZip(String clazz, boolean resolveIt,
          boolean doDepencies) throws ClassNotFoundException
  {
    Class cls = findLoadedClass(clazz);
    if (cls != null)
    {
      if (resolveIt)
        resolveClass(cls);
      return cls;
    }

    String name = Utilities.classToFile(clazz);

    try
    {
      ZipEntry entry = zipFile.getEntry(name);

      if (entry == null)
      {
        if (doDepencies)
          return findOtherClass(clazz, resolveIt);
        else
          return null;
      }

      InputStream in = zipFile.getInputStream(entry);

      int len = (int) entry.getSize();
      byte[] data = new byte[len];
      int success = 0;
      int offset = 0;
      while (success < len)
      {
        len -= success;
        offset += success;
        success = in.read(data, offset, len);
        if (success == -1)
        {
          String[] args = { clazz, zipFile.getName()};
          System.err.println(Jext.getProperty("jar.error.zip", args));
          throw new ClassNotFoundException(clazz);
        }
      }
      
      int dot = clazz.lastIndexOf('.');
      String pkgName = (dot < 0) ? null : name.replace('/', '.').substring(0, dot);
      if (pkgName != null && getPackage(pkgName) == null)
      {
        Package p = definePackage(pkgName, null, null, null, null, null, null, url);
      }//end if there is a Package but it has not yet been defined 

      cls = defineClass(clazz, data, 0, data.length);
      if (resolveIt)
        resolveClass(cls);

      return cls;
    } catch (IOException io) {
      throw new ClassNotFoundException(clazz);
    }
  }


}

// End of JARCLassLoader.java