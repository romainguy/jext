/*
 * 22:13 09/04/2003
 *
 * Jext.java - A text editor for Java
 * Copyright (C) 1999-2003 Romain Guy
 * Portions copyright (C) 1998-2000 Slava Pestov
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

package org.jext;

import java.net.*;

import java.lang.reflect.Method;
import java.io.*;
import java.text.*;

import java.util.*;
import java.util.zip.*;

import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.gjt.sp.jedit.textarea.DefaultInputHandler;
import org.gjt.sp.jedit.textarea.TextUtilities;

import org.jext.actions.*;
import org.jext.gui.*;
import org.jext.event.JextEvent;
import org.jext.misc.TabSwitcher;
import org.jext.misc.VersionCheck;
import org.jext.oneclick.*;
import org.jext.scripting.dawn.Run;
import org.jext.search.Search;
import org.jext.textarea.*;
import org.jext.xml.OneClickActionsReader;
import org.jext.xml.PyActionsReader;
import org.jext.xml.XPropertiesReader;

import org.python.util.PythonInterpreter;

/**
 * Jext is a fully featured, 100% Pure Java, text editor. It
 * has been mainly designed for programmers, and provides also
 * very useful functions for them (syntax colorization, auto
 * indentation...).
 * @author Romain Guy
 * @version 5.0
 */

public class Jext
{
  //////////////////////////////////////////////////////////////////////////////////////////////
  // PUBLIC CONSTANTS
  //////////////////////////////////////////////////////////////////////////////////////////////

  /* If you change any of the final values here, you'll need to recompile every class that uses them.
   * So some lost their "final" at some point, but mustn't be modified anyway.
   */
  /** Current Jext's release. */
  public static String RELEASE = "5.0 <Karsten/Tiger>";
  /**
   * Last Jext's build number. It's used actually only for plugin dependencies, so
   * don't change it for simple bug-fix which don't bump the release number.*/
  public static String BUILD = "05.00.01.00";

  /** If true, Jext will delete user settings if this release is newer */
  public static boolean DELETE_OLD_SETTINGS = true;

  /** Debug mode(not final to avoid it being included in other .class files) */
  public static boolean DEBUG = false;

  /** Available new lines characters */
  public static final String[] NEW_LINE = { "\r", "\n", "\r\n" };

  /** Settings directory. */
  public static final String SETTINGS_DIRECTORY = System.getProperty("user.home") +
                                                  File.separator + ".jext" + File.separator;

  /** Jext home directory. */
  public static final String JEXT_HOME = System.getProperty("user.dir");

  /** Jext server base port number. Used to load all Jext instances with only one JVM. **/
  public static final int JEXT_SERVER_PORT = 49152;

  //////////////////////////////////////////////////////////////////////////////////////////////
  // BEGINNING OF STATIC PART
  //////////////////////////////////////////////////////////////////////////////////////////////
  // STATIC FIELDS
  //////////////////////////////////////////////////////////////////////////////////////////////

  // modes
  public static ArrayList modes;
  public static ArrayList modesFileFilters;
  // selected language
  private static String language = "English";
  private static ZipFile languagePack;
  private static ArrayList languageEntries;
  // GUI option to have, or not flat menus
  private static boolean flatMenus = true;
  // GUI option to have non highlighted buttons
  private static boolean buttonsHighlight = true;
  // server socket
  private static JextLoader jextLoader;
  private static boolean isServerEnabled;
  // plugins specific variables
  private static ArrayList plugins;
  // user properties filename
  public static String usrProps;
  // the splash screen
  private static SplashScreen splash;
  // the properties files
  private static Properties props, defaultProps;
  // contains all the instances of Jext
  // this is an object we synchronize on to avoid window being created concurrently or when is not ready
  // enough(for instance by JextLoader when we didn't call initProperties() yet).
  private static ArrayList instances = new ArrayList(5);
  // contains all the actions
  private static HashMap actionHash;
  // contains all the python actions
  private static HashMap pythonActionHash = new HashMap();
  // auto check
  private static VersionCheck check;
  // input handler
  private static DefaultInputHandler inputHandler;
  // user properties file name
  private static final String USER_PROPS = SETTINGS_DIRECTORY + ".jext-props.xml";
  // this property(set by loadInSingleJVMInstance), if true, says we must not show
  private static boolean runInBg = false;
  private static boolean keepInMemory = false;
  //the default value found during loading must be stored for the option dialog
  private static boolean defaultKeepInMemory = false;
  // when the user runs jext -kill, we store this here and go to kill the server(see 
  // loadInSingleJVMInstance)
  private static boolean goingToKill = false;
  // the text area we pre-build if running in background, that will be shown when Jext is started
  // by the user(so it will be very fast!)
  private static JextFrame builtTextArea = null;

  //////////////////////////////////////////////////////////////////////////////////////////////
  // STATIC METHODS
  //////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns true if highlight buttons should not be highlighted on mouse over.
   */

  public static boolean getButtonsHighlight()
  {
    return buttonsHighlight;
  }

  /**
   * Returns true if menu should be flatened.
   */

  public static boolean getFlatMenus()
  {
    return flatMenus;
  }

  /**
   * Stop the auto check function. We just interrupt the
   * <code>Thread</code> and then 'kill' it.
   */

  public static void stopAutoCheck()
  {
    if (check != null)
    {
      check.interrupt();
      check = null;
    }
  }

  /**
   * Returns the input handler.
   */

  public static DefaultInputHandler getInputHandler()
  {
    return inputHandler;
  }

  /**
   * Add an action listener to the list.
   * @param action The action listener
   */

  public static void addAction(MenuAction action)
  {
    String name = action.getName();
    actionHash.put(name, action);
    String keyStroke = getProperty(name.concat(".shortcut"));

    if (keyStroke != null)
      inputHandler.addKeyBinding(keyStroke, action);
  }

  /**
   * Add a python action listener to the list.
   * @param name Internal action name
   * @param script The python source script
   * @param editAction True if this is an edit action
   */

  public static void addPythonAction(String name, String script, boolean editAction)
  {
    PythonAction action;

    if (!editAction)    
      action = new PythonAction(name, script);
    else
      action = new PythonEditAction(name, script);

    pythonActionHash.put(name, action);
    String keyStroke = getProperty(name.concat(".shortcut"));

    if (keyStroke != null)
      inputHandler.addKeyBinding(keyStroke, action);
  }

  /**
   * Returns a named action.
   * @param action The action
   */

  public static MenuAction getAction(String action)
  {
    Object o = actionHash.get(action);
    if (o == null)
      o = pythonActionHash.get(action);
    return (MenuAction) o;
  }

  /**
   * Load the action listeners.
   */

  private static void initActions()
  {
    actionHash = new HashMap();
    inputHandler = new DefaultInputHandler();
    inputHandler.addDefaultKeyBindings();

    // Python written actions
    loadXMLActions(Jext.class.getResourceAsStream("jext.actions.xml"), "jext.actions.xml");

    // native Java actions
    addAction(new BeginLine());
    addAction(new BoxComment());
    addAction(new CompleteWord());
    addAction(new CompleteWordAll());
    addAction(new CreateTemplate());
    addAction(new EndLine());
    addAction(new JoinAllLines());
    addAction(new JoinLines());
    addAction(new LeftIndent());
    addAction(new OpenUrl());
    addAction(new Print());
//    addAction(new RemoveSpaces());
    addAction(new RemoveWhitespace());
    addAction(new RightIndent());
    addAction(new SimpleComment());
    addAction(new SimpleUnComment());
    addAction(new SpacesToTabs());
    addAction(new TabsToSpaces());
    addAction(new ToLowerCase());
    addAction(new ToUpperCase());
    addAction(new WingComment());
    addAction(new WordCount());

    // init OneClick! actions

    addAction(new OneAutoIndent());
    // One Click !
    loadXMLOneClickActions(Jext.class.getResourceAsStream("jext.oneclickactions.xml"),
                                                          "jext.oneclickactions.xml");
    //    addAction(new OneClickAction("one_paste",             "paste"));
    //    addAction(new OneClickAction("one_reverse_paste",     "reverse_paste"));
    //    addAction(new OneClickAction("one_delete_line",       "delete_line"));
    //    addAction(new OneClickAction("one_join_lines",        "join_lines"));
    //    addAction(new OneClickAction("one_right_indent",      "right_indent"));
    //    addAction(new OneClickAction("one_left_indent",       "left_indent"));
    //    addAction(new OneClickAction("one_simple_comment",    "simple_comment"));
    //    addAction(new OneClickAction("one_simple_uncomment",  "simple_uncomment"));
    //    addAction(new OneClickAction("one_complete_word",     "complete_word"));

    // key bindings
    addJextKeyBindings();
  }

  /**
   * Adds Jext internal key bindings.
   */

  private static void addJextKeyBindings()
  {
    inputHandler.addKeyBinding("CA+UP",           new ScrollUp());
    inputHandler.addKeyBinding("CA+PAGE_UP",      new ScrollPageUp());
    inputHandler.addKeyBinding("CA+DOWN",         new ScrollDown());
    inputHandler.addKeyBinding("CA+PAGE_DOWN",    new ScrollPageDown());
    inputHandler.addKeyBinding("C+UP",            new PrevLineIndent());
    inputHandler.addKeyBinding("C+DOWN",          new NextLineIndent());

    inputHandler.addKeyBinding("ENTER",           new IndentOnEnter());
    inputHandler.addKeyBinding("TAB",             new IndentOnTab());
    inputHandler.addKeyBinding("S+TAB",           new LeftIndent());

    inputHandler.addKeyBinding("C+INSERT",        getAction("copy"));
    inputHandler.addKeyBinding("S+INSERT",        getAction("paste"));

    inputHandler.addKeyBinding("CA+LEFT",         new CsWord(CsWord.NO_ACTION, TextUtilities.BACKWARD));
    inputHandler.addKeyBinding("CA+RIGHT",        new CsWord(CsWord.NO_ACTION, TextUtilities.FORWARD));
    inputHandler.addKeyBinding("CAS+LEFT",        new CsWord(CsWord.SELECT,    TextUtilities.BACKWARD));
    inputHandler.addKeyBinding("CAS+RIGHT",       new CsWord(CsWord.SELECT,    TextUtilities.FORWARD));
    inputHandler.addKeyBinding("CA+BACK_SPACE",   new CsWord(CsWord.DELETE,    TextUtilities.BACKWARD));
    inputHandler.addKeyBinding("CAS+BACK_SPACE",  new CsWord(CsWord.DELETE,    TextUtilities.FORWARD));
    
    if (Utilities.JDK_VERSION.charAt(2) >= '4')
    {
      inputHandler.addKeyBinding("C+PAGE_UP",     new TabSwitcher(false));
      inputHandler.addKeyBinding("C+PAGE_DOWN",   new TabSwitcher(true));
    }//end if JRE 1.4 or above

  }

  /**
   * Loads plugins.
   */

  private static void initPlugins()
  {
    plugins = new ArrayList();
    loadPlugins(JEXT_HOME + File.separator + "plugins");
    loadPlugins(SETTINGS_DIRECTORY + "plugins");
  }

  /**
   * Makes each mode know what plugins to start when it is selected.
   */

  public static void assocPluginsToModes()
  {
    Mode mode;
    String modeName;
    String pluginModes;

    for (int i = 0; i < plugins.size(); i++)
    {
      Plugin plugin = (Plugin) plugins.get(i);
      pluginModes = getProperty("plugin." + plugin.getClass().getName() + ".modes");

      if (pluginModes != null)
      {
        StringTokenizer tok = new StringTokenizer(pluginModes);
        while (tok.hasMoreTokens())
        {
          modeName = tok.nextToken();
          mode = getMode(modeName);
          mode.addPlugin( plugin);
        }
      }
    }
  }

  /**
   * Loads all plugins in a directory.
   * @param directory The directory
   */

  public static void loadPlugins(String directory)
  {
    String[] args = { directory };
    System.out.println(getProperty("jar.scanningdir", args));

    File file = new File(directory);
    if (!(file.exists() || file.isDirectory()))
      return;

    String[] plugins = file.list();
    if (plugins == null)
      return;

    for (int i = 0; i < plugins.length; i++)
    {
      String plugin = plugins[i];
      if (!plugin.toLowerCase().endsWith(".jar"))
        continue;
      try
      {
        new JARClassLoader(directory + File.separator + plugin);
      } catch(IOException io) {
        String[] args2 = { plugin };
        System.err.println(getProperty("jar.error.load", args2));
        io.printStackTrace();
      }
    }
  }

  /**
   * Registers a plugin with the editor. This will also call
   * the <code>start()</code> method of the plugin.
   */

  public static void addPlugin(Plugin plugin)
  {
    plugins.add(plugin);
    try
    {
      plugin.start();
    } catch (Throwable t) {
      System.err.println("#--An exception has occurred while starting plugin:");
      t.printStackTrace();
    }

    if (plugin instanceof SkinFactory)
    {
      //System.out.println("Added a SkinPlugin named: " + plugin.getClass().getName());
      SkinManager.registerSkinFactory((SkinFactory) plugin);
    }
  }

  /**
   * Returns a plugin by it's class name.
   * @param name The plugin to return
   */

  public static Plugin getPlugin(String name)
  {
    for (int i = 0; i < plugins.size(); i++)
    {
      Plugin p = (Plugin) plugins.get(i);
      if (p.getClass().getName().equalsIgnoreCase(name))
        return p;
    }

    return null;
  }

  /**
   * Returns an array of installed plugins.
   */

  public static Plugin[] getPlugins()
  {
    /*Object[] o = plugins.toArray();
    Plugin[] p = new Plugin[o.length];
    for (int i = 0; i < o.length; i++)
      p[i] = (Plugin) o[i];*/
    Plugin[] p = (Plugin[]) plugins.toArray(new Plugin[0]);
    return p;
  }

  /**
   * Opens a new window.
   * @param args Parameters from command line
   */

  public static JextFrame newWindow(String args[])
  {
    return newWindow(args, true);
  }

  /**
   * Opens a new window.
   */

  public static JextFrame newWindow()
  {
    return newWindow(null, true);
  }

  /**
   * Opens a new window, but eventually does not show it.
   * @param args The command line arguments
   * @param toShow When true the frame is shown
   */

  //Note: until code doesn't need it, better leaving it only for the package.
  /*friendly*/ static JextFrame newWindow(final String args[], final boolean toShow)
  {
	    final JextFrame[] window = new JextFrame[1];

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
				     if (toShow && builtTextArea != null)
				     {
				       if (args != null)
				         for (int i = 0; i < args.length; i++)
				           builtTextArea.open(args[i]);
				       builtTextArea.setVisible(true);
				       window[0] = builtTextArea;
				       builtTextArea = null;
				     } else {
				       window[0] = new JextFrame(args, toShow);
				       //if (toShow)
				       instances.add(window[0]);
				     }
				}
			});
		} catch (Exception e) {
			
		}

        return window[0];
  }

  /**
   * Returns amount of opened Jext
   */

  public static int getWindowsCount()
  {
    return instances.size();
  }

  /**
   * Notify all instances of Jext and all properties listeners to reload properties.
   */

  public static void propertiesChanged()
  {
    // we send the event to all the listeners available
    for (int i = 0; i < instances.size(); i++)
      ((JextFrame) instances.get(i)).loadProperties();
  }

  /**
   * Notify all instances of Jext but the one which
   * saved the file to reload recent menu
   * @param The instance which saved a file
   */

  public static void recentChanged(JextFrame instance)
  {
    // we send the event to all the listeners available
    JextFrame listener;
    for (int i = 0; i < instances.size(); i++)
    {
      listener = (JextFrame) instances.get(i);
      if (listener != instance && listener != null)
        listener.reloadRecent();
    }
  }

  /**
   * Some external classes may need to notify
   * each instance of Jext. XTree does.
   * @return A <code>ArrayList</code> containing all instances of Jext
   */

  public static ArrayList getInstances()
  {
    return instances;
  }

  /**
   * Many methods will need to use a <code>Toolkit</code>.
   * This method simply avoid to write too many lines of
   * code.
   * @return The default <code>Toolkit</code>
   */

  public static Toolkit getMyToolkit()
  {
    return Toolkit.getDefaultToolkit();
  }

  /**
   * Jext startup directory is saved during execution.
   * @return Jext's startup directory
   */

  public static String getHomeDirectory()
  {
    return JEXT_HOME;
  }

  /**
   * Store the properties on the HD. After having
   * set up the properties, we need to store'em
   * in a file.
   * @deprecated Use <code>saveXMLProps()</code> instead
   */

  public static void saveProps()
  {
    if (usrProps != null)
    {
      try
      {
        OutputStream out = new FileOutputStream(usrProps);
        props.store(out, "Jext Properties");
        out.close();
      } catch (IOException io) { }
    }
  }

  /**
   * Saves the user's properties to a file using the XML specifications.
   * @param description is a <code>String</code> containing a little
   * description of the properties file. This String is stored on
   * topmost of the user's properties file. Can be set to
   * <code>null</code>.
   */

  public static void saveXMLProps(String description)
  {
    saveXMLProps(usrProps, description);
  }

  /**
   * Saves the user's properties to a file using the XML specifications.
   * @param userProps is the path to the file in which properties will
   * be stored. If it is set to <code>null</code>, properties are not
   * saved at all.
   * @param description is a <code>String</code> containing a little
   * description of the properties file. This String is stored on
   * topmost of the user's properties file. Can be set to
   * <code>null</code>.
   */

  public static void saveXMLProps(String userProps, String description)
  {
    if (userProps != null)
    {
      try
      {
        BufferedWriter out = new BufferedWriter(new FileWriter(userProps));

        String _out = new String("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.write(_out, 0, _out.length());
        out.newLine();

        _out = new String("<!DOCTYPE xproperties SYSTEM \"xproperties.dtd\" >");
        out.write(_out, 0, _out.length());
        out.newLine();

        _out = "<!-- Last save: " + (new Date()).toString() + " -->";
        out.write(_out, 0, _out.length());
        out.newLine();

        if (description == null)
          description = new String("Properties");
        description = "<!-- " + description + " -->";
        out.write(description, 0, description.length());
        out.newLine();
        out.newLine();

        _out = new String("<xproperties>");
        out.write(_out, 0, _out.length());
        out.newLine();

        setProperty("properties.version", BUILD);

        char c = '\0';
        StringBuffer buf;
        Enumeration k = props.keys();
        Enumeration e = props.elements();
        for ( ; e.hasMoreElements(); )
        {
          buf = new StringBuffer("  <property name=\"");
          buf.append(k.nextElement());
          buf.append("\" value=\"");
          String _e = (String) e.nextElement();
          for (int i = 0; i < _e.length(); i++)
          {
            switch(c = _e.charAt(i))
            {
              case '\\':
                buf.append('\\');
                buf.append('\\');
                break;
              case '\'':
                buf.append("&apos;");
                break;
              case '&':
                buf.append("&amp;");
                break;
              case '\"':
                buf.append("&#34;");
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
          buf.append("\" />");
          //for (int i = 0; i < buf.length(); i++)
          //  out.write(buf.charAt(i));
          out.write(buf.toString(), 0, buf.length());
          out.newLine();
        }
        _out = new String("</xproperties>");
        out.write(_out, 0, _out.length());
        out.close();
      } catch (IOException io) { }
    }
    //JARClassLoader.saveDisabledList();
  }

  /**
   * The <code>XPropertiesHandler</code> needs to get Jext's
   * <code>Properties</code> object to achieve its purpose.
   * @return The current <code>Properties</code> object
   */

  public static Properties getProperties()
  {
    return props;
  }

  /**
   * Load a set of properties from an XML file.
   * This method, and not the caller, will search for the translated version of the XML.
   * @param in An <code>InputStream</code> is specified to load properties from a JAR file
   * @param fileName The XML filename
   */

  public static void loadXMLProps(InputStream in, String fileName)
  {
    XPropertiesReader.read(in, fileName);
  }
  
  /**
   * Load a set of properties from an XML file.
   * It is provided for when the caller already provides the translated file(for instance for the plugin
   * translation); in this case toTranslate must be true, otherwise it will be translated the default way.
   * @param in An <code>InputStream</code> is specified to load properties from a JAR file
   * @param fileName The XML filename
   * @since Jext3.2pre1
   */
   
  public static void loadXMLProps(InputStream in, String fileName, boolean toTranslate)
  {
    XPropertiesReader.read(in, fileName, toTranslate);
  }

  /**
   * Load a set of actions from an XML file.
   * @param in An <code>InputStream</code> is specified to load properties from a JAR file
   * @param fileName The XML filename
   */

  public static void loadXMLActions(InputStream in, String fileName)
  {
    PyActionsReader.read(in, fileName);
  }

  /**
   * Load a set of actions from an XML file.
   * @param in An <code>InputStream</code> is specified to load properties from a JAR file
   * @param fileName The XML filename
   */

  public static void loadXMLOneClickActions(InputStream in, String fileName)
  {
    OneClickActionsReader.read(in, fileName);
  }

  /**
   * Returns an input stream corresponding to selected language.
   * @param in The default stream
   * @param fileName The requested file
   */

  public static InputStream getLanguageStream(InputStream in, String fileName)
  {
    ZipEntry entry;
    if (languagePack != null && (entry = languagePackContains(fileName)) != null)
    {
      try
      {
        return languagePack.getInputStream(entry);
      } catch (IOException ioe) {
        return in;
      }
    } else
      return in;
  }

  // returns a non-null ZipEntry object if current language pack
  // contains requested file

  public static ZipEntry languagePackContains(String fileName)
  {
    for (int i = 0; i < languageEntries.size(); i++)
    {
      ZipEntry entry = (ZipEntry) languageEntries.get(i);
      if (entry.getName().equalsIgnoreCase(fileName))
        return entry;
    }

    return null;
  }

  /**
   * Load a set of properties.
   * @param in An <code>InputStream</code> is specified to load properties from a JAR file
   * @deprecated Maintained only for plugins compliance. Use <code>loadXMLProps()</code>
   * instead of this method.
   */

  public static void loadProps(InputStream in)
  {
    try
    {
      props.load(new BufferedInputStream(in));
      in.close();
    } catch (IOException ioe) { }
  }

  /**
   * Creates the necessary directories.
   */

  public static void initDirectories()
  {
    File dir = new File(SETTINGS_DIRECTORY);
    if (!dir.exists())
    {
      dir.mkdir();

      dir = new File(SETTINGS_DIRECTORY + "plugins" + File.separator);
      if (!dir.exists())
        dir.mkdir();

      dir = new File(SETTINGS_DIRECTORY + "scripts" + File.separator);
      if (!dir.exists())
        dir.mkdir();

      dir = new File(SETTINGS_DIRECTORY + "xinsert" + File.separator);
      if (!dir.exists())
        dir.mkdir();
    }
  }

  /**
   * Init the properties.
   */

  public static void initProperties()
  {
    usrProps = SETTINGS_DIRECTORY + ".jext-props.xml";
    defaultProps = props = new Properties();

    /////////////////////////////////////////////////////////////////
    // DEPRECATED BY THE METHOD loadXMLProps()
    /////////////////////////////////////////////////////////////////
    //    loadProps(Jext.class.getResourceAsStream("jext-gui.keys"));
    //    loadProps(Jext.class.getResourceAsStream("jext-gui.text"));
    //    loadProps(Jext.class.getResourceAsStream("jext.props"));
    //    loadProps(Jext.class.getResourceAsStream("jext.tips"));
    /////////////////////////////////////////////////////////////////

    // loads specified language pack
    File lang = new File(SETTINGS_DIRECTORY + ".lang");

    if (lang.exists())
    {
      try
      {
        BufferedReader reader = new BufferedReader(new FileReader(lang));
        String language = reader.readLine();
        reader.close();

        if (language != null && !language.equals("English"))
        {
          File langPack = new File(JEXT_HOME + File.separator + "lang" +
                                               File.separator + language + "_pack.jar");
          if (langPack.exists())
          {
            languagePack = new ZipFile(langPack);
            languageEntries = new ArrayList();
            Enumeration entries = languagePack.entries();

            while (entries.hasMoreElements())
              languageEntries.add(entries.nextElement());

            setLanguage(language);
          } else
            lang.delete();
        }
      } catch (IOException ioe) { }
    }

    //loadXMLProps(Jext.class.getResourceAsStream("jext.props.xml"), "jext.props.xml");
    loadXMLProps(Jext.class.getResourceAsStream("jext-text.props.xml"), "jext-text.props.xml");
    loadXMLProps(Jext.class.getResourceAsStream("jext-keys.props.xml"), "jext-keys.props.xml");
    loadXMLProps(Jext.class.getResourceAsStream("jext-defs.props.xml"), "jext-defs.props.xml");
    loadXMLProps(Jext.class.getResourceAsStream("jext-tips.props.xml"), "jext-tips.props.xml");

    Properties pyProps = new Properties();
    pyProps.put("python.cachedir", SETTINGS_DIRECTORY + "pythoncache" + File.separator);
    PythonInterpreter.initialize(System.getProperties(), pyProps, new String[0]);

    initPlugins();

    if (usrProps != null)
    {
      props = new Properties(defaultProps);

      try
      {
        loadXMLProps(new FileInputStream(USER_PROPS), ".jext-props.xml");

        if (DELETE_OLD_SETTINGS)
        {
          String pVersion = getProperty("properties.version");
          if (pVersion == null || BUILD.compareTo(pVersion) > 0)
          {
            File userSettings = new File(USER_PROPS);
            if (userSettings.exists())
            {
              userSettings.delete();
              defaultProps = props = new Properties();
              //loadXMLProps(Jext.class.getResourceAsStream("jext.props.xml"), "jext.props.xml");
              loadXMLProps(Jext.class.getResourceAsStream("jext-text.props.xml"), "jext-text.props.xml");
              loadXMLProps(Jext.class.getResourceAsStream("jext-keys.props.xml"), "jext-keys.props.xml");
              loadXMLProps(Jext.class.getResourceAsStream("jext-defs.props.xml"), "jext-defs.props.xml");
              loadXMLProps(Jext.class.getResourceAsStream("jext-tips.props.xml"), "jext-tips.props.xml");
              JARClassLoader.reloadPluginsProperties();
              props = new Properties(defaultProps);
            }
          }
        }

      } catch (FileNotFoundException fnfe) {
      } catch (IOException ioe) { }
    }

    initModes(); //must be here since the user can change the mode filters.
    Search.load();

    if (Utilities.JDK_VERSION.charAt(2) >= '4')
    {
      try
      {
        Class cl = Class.forName("org.jext.JavaSupport");
        Method m = cl.getMethod("initJavaSupport", new Class[0]);
        if (m !=  null)
          m.invoke(null, new Object[0]);
      } catch (Exception e) { }
    }

    // Add our protocols to java.net.URL's list
    System.getProperties().put("java.protocol.handler.pkgs", "org.jext.protocol|" +
                               System.getProperty("java.protocol.handler.pkgs", ""));

    initActions();
    JARClassLoader.initPlugins();
    initUI();
    sortModes();

    assocPluginsToModes();
  }

  /**
   * Sets the current selected language.
   * @param lang The label of the language
   */

  public static void setLanguage(String lang)
  {
    language = lang;
  }

  /**
   * Returns current selected language.
   */

  public static String getLanguage()
  {
    return language;
  }

  /**
   * Execute scripts found in user home directory.
   */

  public static void executeScripts(JextFrame parent)
  {
    String dir = SETTINGS_DIRECTORY + "scripts" + File.separator;
    String[] scripts = Utilities.getWildCardMatches(dir, "*.jext-script", false);
    if (scripts == null)
      return;

    for (int i = 0; i < scripts.length; i++)
      org.jext.scripting.dawn.Run.runScript(dir + scripts[i], parent, false);

    scripts = Utilities.getWildCardMatches(dir, "*.py", false);
    if (scripts == null)
      return;

    for (int i = 0; i < scripts.length; i++)
      org.jext.scripting.python.Run.runScript(dir + scripts[i], parent);
  }

  // sort modes alphabetically

  private static void sortModes()
  {
    String[] modeNames = new String[modes.size()];
    for (int i = 0; i < modeNames.length; i++)
      modeNames[i] = ((Mode) modes.get(i)).getUserModeName();
    Arrays.sort(modeNames);

    ArrayList v = new ArrayList(modeNames.length);

    for (int i = 0; i < modeNames.length; i++)
    {
      int j = 0;
      String name = modeNames[i];

      while (!((Mode) modes.get(j)).getUserModeName().equals(name))
      {
        if (j == modes.size() - 1)
          break;
        else
          j++;
      }

      v.add(modes.get(j));
    }

    modes = v;
    v = null;
  }

  // changes some default UI settings such as trees leaf icons or font size and style...

  private static void initUI()
  {
    /*if (getBooleanProperty("useJextTheme"))
      MetalLookAndFeel.setCurrentTheme(new JextMetalTheme());*/
    SkinManager.applySelectedSkin();

    // check if menus are flat or not
    flatMenus = getBooleanProperty("flatMenus");
    // check if buttons are highlighted or not
    buttonsHighlight = getBooleanProperty("buttonsHighlight");
    // rollover
    JextButton.setRollover(getBooleanProperty("toolbarRollover"));
  }

  /**
   * Initialize syntax colorizing modes.
   */

  private static void initModes()
  {
    StringTokenizer _tok = new StringTokenizer(getProperty("jext.modes"), " ");
    Mode _mode;
    modes = new ArrayList(_tok.countTokens());
    modesFileFilters = new ArrayList(_tok.countTokens());

    for ( ; _tok.hasMoreTokens(); )
    {
      modes.add(_mode = new Mode(_tok.nextToken()));
      modesFileFilters.add(new ModeFileFilter(_mode));
    }
  }

  /**
   * Returns the mode according to its name.
   */

  public static Mode getMode(String modeName)
  {
    for (int i = 0; i < modes.size(); i++)
    {
      Mode _mode = (Mode) modes.get(i);
      if (_mode.getModeName().equalsIgnoreCase(modeName))
        return _mode;
    }

    return null;
  }

  /**
   * Returns modes list.
   */

  public static ArrayList getModes()
  {
    return modes;
  }

  /**
   * Adds a mode to Jext's syntax colorizing modes list
   */

  public static void addMode(Mode mode)
  {
    modes.add(mode);
    modesFileFilters.add(new ModeFileFilter(mode));
  }

  /**
   * Set a property.
   * @param name Property's name
   * @param value The value to store as <code>name</code>
   */

  public static void setProperty(String name, String value)
  {
    if (name == null || value == null)
      return;
    props.put(name, value);
  }

  /**
   * Returns true if the property value equals to "on" or "true"
   * @param name The name of the property to read
   */

  public static boolean getBooleanProperty(String name)
  {
    String p = getProperty(name);
    if (p == null)
      return false;
    else
      return p.equals("on") || p.equals("true");
  }

  /**
   * Returns true if the property value equals to "on" or "true"
   * @param name The name of the property to read
   */

  public static boolean getBooleanProperty(String name, String def)
  {
    String p = getProperty(name, def);
    if (p == null)
      return false;
    else
      return p.equals("on") || p.equals("true");
  }

  /**
   * If we store properties, we need to read them, too !
   * @param name The name of the property to read
   * @return The value of the specified property
   */

  public static String getProperty(String name)
  {
    return props.getProperty(name);
  }

  /**
   * Fetches a property, returning the default value if it's not
   * defined.
   * @param name The property
   * @param def The default value
   */

  public static final String getProperty(String name, String def)
  {
    return props.getProperty(name, def);
  }

  /**
   * Returns the property with the specified name, formatting it with
   * the <code>java.text.MessageFormat.format()</code> method.
   * @param name The property
   * @param args The positional parameters
   */

  public static final String getProperty(String name, Object[] args)
  {
    if (name == null)
      return null;

    if (args == null)
      return props.getProperty(name, name);
    else
      return MessageFormat.format(props.getProperty(name, name), args);
  }

  /**
   * Unsets (clears) a property.
   * @param name The property
   */

  public static void unsetProperty(String name)
  {
    if (defaultProps.get(name) != null)
      props.put(name, "");
    else
      props.remove(name);
  }

  /**
   * Exits Jext by closing all the windows. If backgrounding, it doesn't kill the jext server.
   * This is synchronized because creating two windows at a time can be problematic.
   */

  public static void exit()
  {
    synchronized (instances)
    {
      Object[] o = instances.toArray();

      for (int i = o.length - 1; i >= 0; i--)
      {
        JextFrame instance = ((JextFrame) o[i]);
        /*if (i == 0)
        {
          instance.fireJextEvent(JextEvent.KILLING_JEXT);
          if (!isRunningBg())
            stopPlugins();
        } else
          instance.fireJextEvent(JextEvent.CLOSE_WINDOW);*/
        closeToQuit(instance);
      }
      /*if (isRunningBg())
      {
        builtTextArea = newWindow(null, false);
        //instances.add(builtTextArea);
      } else
        finalCleanupBeforeExit();*/
    }
  }

  /**
   * Do the final cleanup that must be done at the real end of the session(when closing the server if
   * running background server, or after killing the last window if no server is running).
   */
   
  /* friendly*/  static void finalCleanupAndExit() {
    //currently it does almost nothing, but it's used.
    System.exit(0);
  }
  /**
   * Stop plugins.
   */
  /* friendly */ static void stopPlugins() {
    Plugin[] plugins = getPlugins();
    for (int i = 0; i < plugins.length; i++)
      try
      {
        plugins[i].stop();
      } catch (Throwable t) {
        System.err.println("#--An exception has occurred while stopping plugin:");
        t.printStackTrace();
      }
  }

  /**
   * Close a {@link JextFrame} by calling {@link JextFrame#closeToQuit()} and if it it the last window and we are not keeping the server open
   * we close Jext completely.
   */
  public static void closeToQuit(JextFrame frame) {
    closeToQuit(frame,false);
  }
  
  //For JextLoader when it's killing Jext
  /*friendly*/ static void closeToQuit(JextFrame frame, boolean isKillingServer) {
    if (isKillingServer)
      runInBg = false;//so when calling closeWindow(frame), which will happen, it will close completely Jext
    //and stop plugins.
    frame.closeToQuit();
  }
  
  public static void closeWindow(JextFrame frame) {
    synchronized (instances)
    {
      if (getWindowsCount() == 1/* && !isRunningBg()*/)
        frame.fireJextEvent(JextEvent.KILLING_JEXT);
      else
        frame.fireJextEvent(JextEvent.CLOSING_WINDOW);
      frame.closeWindow();

      if (getWindowsCount() == 0)
      {
        if (!isRunningBg())
          stopServer();

        Search.save();

        if (!isRunningBg())
          stopPlugins();

        frame.saveConsole();
        GUIUtilities.saveGeometry(frame, "jext");
        saveXMLProps("Jext v" + Jext.RELEASE + " b" + Jext.BUILD);
        //frame.cleanMemory();
        frame = null;
        System.gc();//this way, the garbage collector should do its work, without any NullPointerEx at all.

        if (isRunningBg())
          builtTextArea = newWindow(null, false);
        else
          System.exit(0);
      }
    }
  }

  /**
   * Returns splash screen
   */

  public static SplashScreen getSplashScreen()
  {
    return splash;
  }

  /**
   * Set the splash screen progress value after
   * having checked if it isn't null (in case of a new window).
   * @param val The new value
   */

  public static void setSplashProgress(int val)
  {
    if (splash != null)
      splash.setProgress(val);
  }

  /**
   * Set the splash screen text value after having checked if it
   * isn't null (in case of a new window).
   * @param text The new text
   */

  public static void setSplashText(String text)
  {
    if (splash != null)
      splash.setText(text);
  }

  /**
   * Kills splash screen
   */

  public static void killSplashScreen()
  {
    if (splash != null)
    {
      splash.dispose();
      splash = null;
    }
  }

  /**
   * Stops the Jext server which loads every Jext instance in the same JVM.
   */

  public static void stopServer()
  {
    if (jextLoader != null)
    {
      jextLoader.stop();
      jextLoader = null;
    }
  }

  /**
   * Returns true if the server is enabled.
   */

  public static boolean isServerEnabled()
  {
    return isServerEnabled;
  }

  /**
   * Returns true if the backgrounding is enabled.
   */

  public static boolean isDefaultKeepInMemory() {
    return defaultKeepInMemory;
  }

  public static void setDefaultKeepInMemory(boolean val) {
    defaultKeepInMemory = val;
    if (val) //TODO:hack to active it from this session onwards
    {
    }
  }
  /**
   * Used by security options panel to remember of the server status.
   * @param on If true, the server will be runned at next start
   */

  public static void setServerEnabled(boolean on)
  {
    isServerEnabled = on;
  }

  /**
   * Attempts to load Jext in a single JVM instance only. If this instance of
   * Jext is the very first to be loaded, then a ServerSocket is opened. Otherwise,
   * this instance attemps to connect on to a specific socket port to tell other
   * Jext instance to create a new window. This avoids to load on JVM for each
   * launch of Jext. Opened port is securised so that no security hole is created.
   * @param args The arguments of the new Jext window
   */

  public static void loadInSingleJVMInstance(String[] args)
  {
    try
    {
      File security = new File(SETTINGS_DIRECTORY + ".security");
      if (!security.exists())
        isServerEnabled = true;
      else
      {
        BufferedReader reader = new BufferedReader(new FileReader(security));
        isServerEnabled = new Boolean(reader.readLine()).booleanValue();
        reader.close();
      }
    } catch (IOException ioe) { }
    
    if (!isServerEnabled && !runInBg)
      return;

    File authorizationKey = new File(SETTINGS_DIRECTORY + ".auth-key");

    // if the authorization key exists, another Jext instance may
    // be running
    if (authorizationKey.exists())
    {
      // we attempt to log onto the other instance of Jext(but only if we are not backgrounding; no
      // more than one bg instance is started, and if we are bg we don't pass anything to the other instance.
      try
      {
        BufferedReader reader = new BufferedReader(new FileReader(authorizationKey));
        int port = Integer.parseInt(reader.readLine());
        String key = reader.readLine();
        reader.close();
      
        Socket client = new Socket("127.0.0.1", JEXT_SERVER_PORT + port);
      
        if (!runInBg)
        { //now that we made sure that the other instance exists, if backgrounding we do
          //nothing
          PrintWriter writer = new PrintWriter(client.getOutputStream());
      
          StringBuffer _args = new StringBuffer();
          if (goingToKill)
          {
            _args.append("kill");
          } else {
            _args.append("load_jext:");
            for (int i = 0; i < args.length; i++)
            {
              _args.append(args[i]);
              if (i != args.length - 1)
                _args.append('?');
            }
          }
          _args.append(':').append(key);
      
          writer.write(_args.toString());
          writer.flush();
          writer.close();
        } else
          System.out.println("Jext is already running, either in background or foreground.");

        client.close();

        System.exit(5);
      } catch (Exception e) {
        // no other jext instance is running, we delete the auth. file
        authorizationKey.delete();
        if (goingToKill) {
          System.err.println("No jext instance found!");
          System.exit(0);
        } else
          jextLoader = new JextLoader();
      }
    } else if (!goingToKill) {
      jextLoader = new JextLoader();
    } else {
      System.err.println("No jext instance found!");
      System.exit(0);
    }
  }

  /**
   * As Jext can be runned in background mode, some operations may need to know wether
   * or not current instance is "up" or "crouched". This is the purpose of this method.
   * @return A true boolean value is returned whenever Jext is running in background mode
   */

  public static boolean isRunningBg()
  {
    return runInBg;
  }

  // check the command line arguments

  private static String[] parseOptions(String [] args)
  {
    // Trap bg flag
    int argLn = args.length;
    ArrayList newArgs = new ArrayList(argLn);

    //First, it checks defaults: if the user actived -showbg by default, read this setting.
    try
    {
      File showbg = new File(SETTINGS_DIRECTORY + ".showBg");
      if (!showbg.exists())
        keepInMemory = false;
      else
      {
        BufferedReader reader = new BufferedReader(new FileReader(showbg));
        keepInMemory = new Boolean(reader.readLine()).booleanValue();
        reader.close();
      }
    } catch (IOException ioe) { }
    defaultKeepInMemory = runInBg = keepInMemory;
    
    //Then, let's read options.
    for (int i = 0; i < argLn; i++)
    {
      //Whenever it encounter an option it resets all contrary ones.
      if ("-bg".equals(args[i]))
      {
        runInBg = true;

        keepInMemory = false;
        goingToKill = false;
      }
      else if ("-kill".equals(args[i]))
      {
        goingToKill = true;

        keepInMemory = false;
        runInBg = false;
      }
      else if ("-showbg".equals(args[i]))
      {
        runInBg = true;
        keepInMemory = true;

        goingToKill = false;
      }
      //This option is unrelated.
      else if ("-debug".equals(args[i]))
        DEBUG = true;
      else
        newArgs.add(args[i]);
    }

    return (String[]) newArgs.toArray(new String[0]);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////
  // END OF STATIC PART
  //////////////////////////////////////////////////////////////////////////////////////////////
  // MAIN ENTRY POINT
  //////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Start method. Load the property file, set the look and feel, create a new GUI,
   * load the options. If a file name is specified as first argument, we pass it
   * to the window contructor which will construct its full path (because you can
   * specify, for example, ..\..\test.java or ~\jext.props or ...../hello.cpp -
   * both / and \ can be used -)
   */

  public static void main(String args[])
  {
    ///////////////////////////////// DEBUG
    System.setErr(System.out);

    initDirectories();
    args = parseOptions(args);
    synchronized (instances)
    {
      loadInSingleJVMInstance(args);
      initProperties();

      if (!isRunningBg())
      {
        splash = new SplashScreen();
        newWindow(args);
      } else {
        if (keepInMemory)
          splash = new SplashScreen();

        //FIXME:maybe it should ignore arguments when backgrounding.
        builtTextArea = newWindow(args, false);

        if (keepInMemory)
          newWindow(null, true);
      }
    }

    if (getBooleanProperty("check"))
      check = new VersionCheck();
  }
}

// End of Jext.java