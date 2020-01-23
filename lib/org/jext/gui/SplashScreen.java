/*
 * 01/20/2003 - 23:32:46
 *
 * SplashScreen.java - Jext Splash Screen (also a class loader)
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

package org.jext.gui;

import java.io.*;
import java.awt.*;
import java.util.*;

import java.net.URL;

import javax.swing.*;
import javax.swing.border.*;

import org.jext.*;
import org.jext.gui.*;

/**
 * Splash screen. This class can also load classes listed in
 * file classlist.
 */

public class SplashScreen extends JWindow implements Runnable
{
  // private fields
  private Thread thread;
  private boolean finished;
  private String[] classes;
  private JextProgressBar progress;
  
  /**
   * Creates a new splash screen which displays a picture,
   * a copyright and a progress bar used to indicate the
   * loading progress of the application.
   */

  public SplashScreen()
  {
    setBackground(Color.lightGray);

    JPanel pane = new JPanel(new BorderLayout());

    pane.setFont(new Font("Monospaced", 0, 14));
    pane.add(BorderLayout.NORTH,
             new JLabel(
             Utilities.getIcon("images/splash" + (Math.abs(new Random().nextInt()) % 6) + ".gif",
                               Jext.class)
             ));

    progress = new JextProgressBar(0, 100);
    progress.setStringPainted(true);
    progress.setFont(new Font("Monospaced", Font.BOLD, 9));
    progress.setString("");
    progress.setBorder(new CompoundBorder(new EmptyBorder(6, 6, 6, 6), new LineBorder(Color.black)));
    pane.add(BorderLayout.CENTER, progress);
    pane.add(BorderLayout.SOUTH, new JLabel("v" + Jext.RELEASE +
                                            " - (C) 2004 Romain Guy",
                                            SwingConstants.CENTER));

    pane.setBorder(new LineBorder(Color.black));
    getContentPane().add(pane);

    pack();

    boolean load = Jext.getBooleanProperty("load.classes");
    if (load)
    {
      createClassesList();

      thread = new Thread(this);
      thread.setDaemon(true);
      thread.setPriority(Thread.NORM_PRIORITY);
    }

    Utilities.centerComponent(this);
    Utilities.setCursorOnWait(this, true);
    setVisible(true);

    if (load)
    {
      thread.start();
    } else {
      finished = true;
      setProgress(0);
      setText(Jext.getProperty("startup.loading"));
    }
  }

  // get the classes to be loaded from the file 'classlist'.

  private void createClassesList()
  {
    Vector buf = new Vector(30);
    BufferedReader in = new BufferedReader(new InputStreamReader(
                                           Jext.class.getResourceAsStream("classlist")));
    String buffer;
    try
    {
      while ((buffer = in.readLine()) != null)
        buf.addElement(buffer);
      in.close();
    } catch (IOException ioe) { }

    classes = new String[buf.size()];
    buf.copyInto(classes);
    buf = null;
  }

  /**
   * Loads the classes dinamycally from the list.
   */

  public void run()
  {
    String packs = getClass().getName();
    int i = packs.lastIndexOf('.');
    if (i >= 0)
      packs = packs.substring(0, i + 1);
    else
      packs = "";

    for (i = 0; i < classes.length; i++)
    {
      String n = classes[i];
      int j = n.lastIndexOf('.');
      if (j < 0) n = packs + n;
      progress.setString(n);

      try
      {
        Class c = Class.forName(n);
      } catch(Exception e) { }
      progress.setValue(100 * (i + 1) / classes.length);
    }
    finished = true;
    setText(Jext.getProperty("startup.loading"));
    stop();
  }

  /**
   * Sets the current text of the progress bar but
   * only in the case the loading of classes is ended.
   */

  public void setText(String text)
  {
    if (finished)
      progress.setString(text);
  }

  /**
   * Sets the current progress value of the progress bar but
   * only in the case the loading of classes is ended.
   */

  public void setProgress(int percent)
  {
    if (finished)
      progress.setValue(percent);
  }

  /**
   * Stop the loading process.
   */

  public void stop()
  {
    thread = null;
  }
  

}

// End of SplashScreen.java