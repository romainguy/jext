/*
 * 06/13/2003
 *
 * PluginDownload.java - The manager for plugins update.
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

import java.net.*;
import java.io.*;
import java.util.zip.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Window;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

import org.jext.Jext;
import org.jext.Utilities;
import org.jext.JARClassLoader;
import org.jext.misc.DownloaderThread;

/**
 * This is the master class of all the plugin update process; see the code of main() for
 * a sample of use.
 * @author Blaisorblade
 * @since Jext3.2pre4
 */
public class PluginDownload {
  private PluginDownload() {}//"static" class

  //URL's to refer to(they are initialized below, in the static constructor)
  private static URL autoUpdateVersionUrl, autoUpdateListUrl;

  private final static String jarName = "autoUpdate.jar";
  private static URL autoUpdateJarUrl;

  //Where we save the new version of autoUpdate.jar
  private final static File downloadedJarPath = new File(Jext.SETTINGS_DIRECTORY + jarName);

  private final static File downloadedListPath = new File(Jext.SETTINGS_DIRECTORY + "plugList.xml");

  private static String defaultJarPath;

  //classloaders for the update.
  private static ClassLoader loader = null, newVerLoader = null, defLoader = null;

  //references to data model and UI elements.
  private static AbstractPlugReader plugReader;
  private static JDialog updateWindow;
  private static JFrame parentFrame = null;

  //flags
  /**
   * Remember if we have already done the boot process(currently downloading the
   * latest version of autoUpdate.jar).
   */
  private static boolean hasBooted;
  public static boolean debug = false;

  //Property keys
  //private static final String versionKey = "plugDownload.core.version";
  //private static final String baseUrlKey = ;
  private static final String waitLabelKey = "plugDownload.core.waitWindow.label";
  private static final String waitTitleKey = "plugDownload.core.waitWindow.title";

  static {
    if (Jext.getProperties() == null) //this is needed during testing, when all starts from our main().
      Jext.initProperties();

    String baseURL = Jext.getProperty("plugDownload.core.baseAddress", "http://www.jext.org/");
    //baseURL = "http://localhost/jext/"; //Uncomment this for local testing.
    //FIXME: Also add proxing ability!! See system properties set by jEdit
    //inside jEdit.java
    try {
      autoUpdateVersionUrl = new URL(baseURL + "plugReader.version");
      autoUpdateJarUrl = new URL(baseURL + jarName);
      autoUpdateListUrl = new URL(baseURL + "plugins.xml.php");
    } catch (MalformedURLException mue) {
      mue.printStackTrace();
    }
  }

  private static String getDefaultJarPath() {
    if (defaultJarPath == null)
      defaultJarPath = Jext.JEXT_HOME + File.separator + ".." + File.separator + 
        "bin" + File.separator + jarName;
    return defaultJarPath;
  }

  private static void downloadJar() {
    //try {
      HandlingRunnable handler = new HandlingRunnable() {
        public void run(Object dial, Throwable excep) {
          if (dial != null)
            ((JDialog) dial).dispose();
        }
      };
      DownloaderThread t = new DownloaderThread(autoUpdateJarUrl, handler, downloadedJarPath.getPath()) {
        public Object work() {
          JDialog dial = null;
          //now if we didn't do this yet, check for new core version.
          if (debug || !hasBooted) {
            try {
              byte buf[] = new byte[10];

              InputStream releaseInp = autoUpdateVersionUrl.openStream();
              releaseInp.read(buf);
              releaseInp.close();

              int currVersion = Integer.parseInt(Jext.getProperty("plugDownload.core.version"));
              int newVersion = Integer.parseInt(new String(buf).trim());

              //If there is a new version, download it.
              if (currVersion < newVersion) {
                dial = new WaitDialog();
                dial.setVisible(true);
                try {
                  super.work(); //does the download work.
                } catch (Throwable t) {
                  JOptionPane.showMessageDialog(dial,
                      Jext.getProperty("plugDownload.core.coreDownError.text"),
                      Jext.getProperty("plugDownload.core.downError.title"),
                      JOptionPane.ERROR_MESSAGE);
                  throw (IOException)t;
                }
                if (!debug) { //during testing this is commented out.
                  Jext.setProperty("plugDownload.core.version", String.valueOf(newVersion));
                }
              }

              //anyway, the jar is up-to-date, and we remember this.
              hasBooted = true;
            } catch (IOException ioe) {
              //In this case, we can't update the autoUpdate.jar file; but then we use the current one.
              System.err.println("Caught exception while trying to update autoUpdate.jar");
              ioe.printStackTrace();
            }
          } //if needed, we've tried to do the update. Now let's get the list.
          downloadList();
          return dial;
        }
      };
      t.start(true);
    /*} catch (Throwable t) { //will never happen, because the above construct doesn't return any exception.
      t.printStackTrace();
    }*/
  }

  /** This method downloads the core Jar if needed and shows the dialog.*/
  /*FIXME: needs to be rewritten. As of now, it starts a thread which downloads the jar core, which run a callback in the
   * event-handling thread, which checks if things went ok and downloads the plugin list in a new thread, which then shows
   * the dialog in a callback.
   */
  /*private static void downloadJar() {
    if (debug || !hasBooted) {
      try {
        InputStream releaseInp = autoUpdateVersionUrl.openStream();
        byte buf[] = new byte[10];
        releaseInp.read(buf);
        int currVersion = Integer.parseInt(Jext.getProperty("plugDownload.core.version"));
        final int newVersion = Integer.parseInt(new String(buf).trim());
        if (currVersion < newVersion) {
          final JDialog dial = new WaitDialog(parentFrame);
          dial.setVisible(true);
          Utilities.copy(true, new DownloaderThread(autoUpdateJarUrl, null, downloadedJarPath.getPath()) {
            public Object construct() {
              Throwable ret = (Throwable) super.construct();
              dial.dispose();
              if (ret != null) {
                ret.printStackTrace();
                JOptionPane.showMessageDialog(dial,
                    Jext.getProperty("plugDownload.core.coreDownError.text"),
                    Jext.getProperty("plugDownload.core.downError.title"),
                    JOptionPane.ERROR_MESSAGE);
                return ret;
              }
              if (!debug) {
                //during testing this is commented out.
                Jext.setProperty("plugDownload.core.version", String.valueOf(newVersion));
              }
              hasBooted = true;
              downloadList();
              return null;
            }
          });
          return;
          /*Utilities.downloadFile(autoUpdateJarUrl, downloadedJarPath.getPath(), true, new HandlingRunnable() {
            public void run() {
              //let's close the window and let things go on.
              dial.dispose();
              if (excep != null) {
                excep.printStackTrace();
                JOptionPane.showMessageDialog(dial,
                    Jext.getProperty("plugDownload.core.coreDownError.text"),
                    Jext.getProperty("plugDownload.core.downError.title"),
                    JOptionPane.ERROR_MESSAGE);
                return;
              }
              if (!debug) {
                //during testing this is commented out.
                Jext.setProperty("plugDownload.core.version", String.valueOf(newVersion));
              }
              hasBooted = true;
              downloadList();
            }
          });
          //the file must be ready for the call to buildChainingClassLoader, below, so if threaded there
          //is need for special caution: fixed. The buildChainingClassLoader is done by the back-notify Runnable.
          //actually, when doing things the right way, the lib download will start together with a progress monitor.
        } else { //otherwise, the jar is up-to-date.
          hasBooted = true;
        }
      } catch (IOException ioe) {
        //In this case, we can't update the autoUpdate.jar file; but then we use the current one.
        System.err.println("Caught exception while trying to update autoUpdate.jar");
        ioe.printStackTrace();
      }
    }
    //If for any reason the jar wasn't updated(either for problems or because it was up-to-date) we show
    //here the dialog.
    downloadList();
  }*/

  /**
   * This method downloads the list of plugins and loads it into the
   * AbstractPlugReader(there is an instance which * can be got through
   * getUpdater(). It's called by the downloader thread, while its internal
   * runnable is called in the AWT thread.
   */
  private static void downloadList() {
    /*try {
      Utilities.downloadFile(autoUpdateListUrl, downloadedListPath.getPath(), false, null);
    } catch (IOException ioe) {
      JOptionPane.showMessageDialog(null,
          Jext.getProperty("plugDownload.core.downError.text"),
          Jext.getProperty("plugDownload.core.downError.title"),
          JOptionPane.ERROR_MESSAGE);
      System.err.println("Failed loading of XML!");
      ioe.printStackTrace();
      return;
    }*/
    Utilities.downloadFile(autoUpdateListUrl, downloadedListPath.getPath(), false,
	new HandlingRunnable() {
	  public void run(Object o, Throwable excep) {
	    if (excep != null) {
	      JOptionPane.showMessageDialog(null,
		  Jext.getProperty("plugDownload.core.downError.text"),
		  Jext.getProperty("plugDownload.core.downError.title"),
		  JOptionPane.ERROR_MESSAGE);
	      System.err.println("Failed loading of XML!");
	      excep.printStackTrace();
	    } else 
	      showUpdateWindow();
	  }
	});
  }

  /**
   * This method loads the list of plugins into the AbstractPlugReader instance which
   * can be got through getUpdater().
   */
  public static boolean loadList() {
    Reader reader = null;
    if (! downloadedListPath.exists())
      return false;
    try {
      reader = new BufferedReader(new FileReader(downloadedListPath.getPath()));
      return getUpdater().loadXml(reader);
    } catch (IOException ioe) {
      //HERE we must give some user visible output.(I.e. in the GUI). In fact, it happens in the caller method.
      System.err.println("Caught exception while trying to download plugin list");
      ioe.printStackTrace();
      return false;
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ioe) {}
      }
    }
  }

  private static boolean buildChainingClassLoader() {
    //Now, if autoUpdate.jar has been updated, it is in the right place in the user's home;
    //otherwise we need to use the default one, in jext/lib dir. Or maybe somewhere else, such as bin,
    //since it mustn't be loaded at startup.
    //And we build the chaining class loader that will be used to load all resources.

    //We build first a ClassLoader which loads the supplied autoUpdate.jar, then
    //another one which looks in the downloaded one BUT uses as fallback one
    //the first one. The fallback is provided trasparently by the JDK by using
    //the fallback classloader as parent of the new one. But this only works for
    //resources, since for classes we must fallback not only in the case of ClassNotFoundEx.
    try {
      defLoader = new JARClassLoader(getDefaultJarPath(), false, null);
      loader = defLoader;
      System.out.println("DefLoader");
    } catch (IOException ioe) {
      System.err.println("You haven't installed correctly Jext! The autoUpdate.jar file is missing." +
          "It should be in this position: " + getDefaultJarPath());
    }

    if (downloadedJarPath.exists())
      try {
        newVerLoader = new JARClassLoader(downloadedJarPath.getPath(), false, defLoader);
        loader = newVerLoader;
        System.out.println("NewVerLoader");
        //here defLoader becomes the father of newVerLoader
      } catch (IOException ioe) {
        ioe.printStackTrace();
        //The file is there, but there could be problems anyway.
      }
    if (loader == null)
      return false;
    return true;
  }

  private static Object getInstanceFromLoader(String className) {
    if (loader != null)
      try {
        return loader.loadClass(className).newInstance();
      } catch (InstantiationException ie) {
      } catch (IllegalAccessException ie) {
      } catch (ClassNotFoundException ie) {
        return null;//the class is missing in both chained classLoaders.
      }

    //if the class was found in newVerLoader, we've tried with it, but it didn't work.
    //So we only need trying with defLoader, and only if != loader.
    if (defLoader != null && defLoader != loader)
      try {
        return defLoader.loadClass(className).newInstance();
      } catch (InstantiationException ie) {
      } catch (IllegalAccessException ie) {
      } catch (ClassNotFoundException ie) {
      }
    return null;
  }

  private static AbstractPlugReader newUpdater() {
    return (AbstractPlugReader) getInstanceFromLoader("PlugReader");
  }

  public static JPanel newUpdatePanel() {
    return (JPanel) getInstanceFromLoader("ChoiceForm");
  }

  public static AbstractPlugReader getUpdater() {
    if (plugReader == null)
      plugReader = newUpdater();
    return plugReader;
  }

  public static Reader getDtd() {
    return new BufferedReader(new InputStreamReader(loader.getResourceAsStream("pluglist.dtd")));
  }

  public static JDialog getUpdateWindow() {
    return updateWindow;
  }

  /**
   * This method starts the update: downloads the new autoUpdate.jar if needed, and when this is 
   * done shows the window.
   */
  public static void startUpdate() {
    PluginDesc.initDirectories();
    downloadJar();
  }

  public static void showUpdateWindow() {
    if (!buildChainingClassLoader()) {
      JOptionPane.showMessageDialog(null,
          Jext.getProperty("plugDownload.core.instError.text", new Object[] {getDefaultJarPath()}),
          Jext.getProperty("plugDownload.core.instError.title"),
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (loadList()) {
      updateWindow = new JDialog(parentFrame, Jext.getProperty("plugDownload.core.mainWindow.title", "Download plugins"));
      JPanel updatePanel = newUpdatePanel();
      updateWindow.setContentPane(updatePanel);
      if (debug)
        updateWindow.addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            System.exit(0);
          }
        });
      updateWindow.pack();
      updateWindow.setVisible(true);
    } else {
      JOptionPane.showMessageDialog(null,
          Jext.getProperty("plugDownload.core.downError.text"),
          Jext.getProperty("plugDownload.core.downError.title"),
          JOptionPane.ERROR_MESSAGE);
      System.err.println("Failed loading of XML!");
    }
  }

  public static void main(String[] args) {// for testing
    debug = true;
    startUpdate();
  }

  private static class WaitDialog extends JDialog {
    WaitDialog() {
      super(parentFrame, Jext.getProperty(waitTitleKey, "Wait please!"));
      getContentPane().add(new JLabel(Jext.getProperty(waitLabelKey, "Please wait while updating PluginGet...")));
      pack();
    }
  }
}
