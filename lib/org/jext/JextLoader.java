/*
 * 01/20/2003 - 23:56:01
 *
 * JextLoader.java - Loads Jext instances in a single JVM
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

package org.jext;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.JOptionPane;

/**
 * This class creates a new socket connection which listens to client requests. Whenever
 * a client logs onto the socket, we check its IP address.<br>
 * Security implementation:<p>
 * <ul>
 * <li>If this address isn't 127.0.0.1 (aka localhost), the connection is rejected.</li>
 * <li>The socket also needs to get the good message which must contain a give authorization key.
 * If the given key or message is wrong, the connection is rejected.</li>
 * <li>If the server rejects a connection, it also closes itself to avoid Denial Of Service
 * attacks or brutal ones (trying for instance random keys).</li>
 * </ul>
 * @author Romain Guy
 * @version 1.1.1
 */

final class JextLoader implements Runnable
{
  private int port;
  private File auth;
  private String key;
  private Thread tServer;
  private ServerSocket server;

  JextLoader()
  {
    auth = new File(Jext.SETTINGS_DIRECTORY + ".auth-key");
    // creates the authorization key
    try
    {
      BufferedWriter writer = new BufferedWriter(new FileWriter(auth));
      // 16 383 = unreserved range of ports
      port = Math.abs(new Random().nextInt()) % (16383);
      String portStr = Integer.toString(port);
      key = Integer.toString(Math.abs(new Random().nextInt()) % (int) Math.pow(2, 30));
      writer.write(portStr, 0, portStr.length());
      writer.newLine();
      writer.write(key, 0, key.length());
      writer.flush();
      writer.close();

      // creates the server
      server = new ServerSocket(Jext.JEXT_SERVER_PORT + port);

    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

    // server is necessarily threaded
    tServer = new Thread(this);
    tServer.start();
  }

  /**
   * Stops the server by inerrupting the thread, killing it and then
   * closing the server itself. Finally, it erases the authorization
   * key.
   */

  public void stop()
  {
    tServer.interrupt();
    tServer = null;

    try
    {
      if (server != null)
        server.close();
      auth.delete();
    } catch (IOException ioe) { }
  }

  /**
   * Waits for a connexion request and handle it. The client should provide a special line
   * containing a message, the arguments for the loading and the authorization key. This
   * key is used to avoid security holes in your system.
   */

  public void run()
  {
    while (tServer != null)
    {
      try
      {
        Socket client = server.accept();
        if (client == null)
          continue;

        if (!"127.0.0.1".equals(client.getLocalAddress().getHostAddress()))
        {
          client.close();
          Jext.stopServer();
          intrusion();
          return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String givenKey = reader.readLine();
        reader.close();

        if (givenKey != null)//when backgrounding connects to check existance of server but sends
          //nothing.
          if (givenKey.startsWith("load_jext:") && givenKey.endsWith(":" + key))
          {
            Vector args = new Vector(1);
            StringTokenizer st = new StringTokenizer(givenKey.substring(10,
        					     givenKey.length() - (key.length() + 1)), "?");
            while (st.hasMoreTokens())
              args.addElement(st.nextToken());

            if (args.size() > 0)
            {
              String arguments[] = new String[args.size()];
              args.copyInto(arguments);
              args = null;

              if (Jext.getBooleanProperty("jextLoader.newWindow"))
              {
        	Jext.newWindow(arguments);
              } else if (!Jext.isRunningBg()) {
        	ArrayList instances = Jext.getInstances();
                synchronized(instances) {
                  if (instances.size() != 0) {//can be 0 when backgrounding.??? No more!
                    JextFrame parent = (JextFrame) instances.get(0);
                    for (int i = 0; i < arguments.length; i++)
                      parent.open(arguments[i]);
                    //parent.setVisible(true);//this code is not good when running background server,
                    //since Jext keeps builtTextArea set(and doesn't open a new one until it isn't unset).
                    //And so setVisible is not needed.
                  } else {
                    Jext.newWindow(arguments);
                    System.err.println("DEBUG - instances.size() == 0 in JextLoader.java!");
                  }
                }

              } else                    //when Jext.isRunningBg()
                Jext.newWindow(arguments);
            } else
              Jext.newWindow();

            client.close();
          } else if (givenKey.equals("kill:" + key)) {
            if (Jext.isRunningBg())// && Jext.getWindowsCount() <= 1 )
            {
              ArrayList instances = Jext.getInstances();
              synchronized (instances)
              {
                //normally at least one window is always open, even if hidden, but in some moments
                //this isn't true(when the user exits jext and it has not still started a new window).
                //If one window is open, we must check it is not shown.
                JextFrame lastInstance = null;
                if (instances.size() == 0 || instances.size() == 1 && 
                    ! ( lastInstance = (JextFrame)instances.get(0) ) .isVisible())
                {
                  if (instances.size() != 0)
                  {
                    Jext.closeToQuit(lastInstance, true);
                    //since the window has not been shown this could be useless, but maybe not, especially to
                    //dispatch JextEvent's.
                  }
                  //I've commented out the above code since it causes bugs with the ProjectManagement,
                  //that is NullPointerEx. I'm trying if this doesn't happen without cleanMemory.
                  Jext.finalCleanupAndExit();//check well this! TODO
                }
              }
            }
          } else {
            client.close();
            Jext.stopServer();
            intrusion();
            return;
          }
      } catch (IOException ioe) { }
    }
  }

  // warn the user that someone is attempting to break into his system

  private void intrusion()
  {
    JOptionPane.showMessageDialog(null,
            "An intrusion is attempted against your system !\nJext will close its opened " + "sockets to preserve system integrity.\nYou should warn the network administrator.",
            "Intrusion attempt...", JOptionPane.WARNING_MESSAGE);
  }


}

// End of JextLoader.java