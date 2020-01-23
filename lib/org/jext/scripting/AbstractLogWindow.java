/*
 * 12/06/2003
 *
 * AbstractLogWindow.java - Log window infrastructure
 * Copyright (C) 2003 Paolo Giarrusso
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

package org.jext.scripting;

import java.awt.BorderLayout;
import java.awt.Container;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import java.io.Writer;

import javax.swing.*;

import org.jext.*;
import org.jext.gui.*;

public abstract class AbstractLogWindow extends JFrame implements Logger {
  protected Dockable contDock;
  protected JextFrame parent;
  protected JTextArea textArea = new JTextArea(15, 40);

  public Dockable getContainingDock() {
    if (contDock.getFrame() != this) {
      System.err.println("contDock.getFrame() is: " + contDock.getFrame() +
	  "\nwhile this is: " + this);
      throw new RuntimeException("Emulated assertion failed in AbstractLogWindow");
    }
    return contDock;
  }

  public void log(String msg)
  {
    textArea.append(msg);
    textArea.setSelectionStart(textArea.getDocument().getLength());
    textArea.setSelectionEnd(textArea.getDocument().getLength());
  }

  public void logln(String msg)
  {
    log(msg + '\n');
  }

  private Writer writerStdOut = new LoggingWriter();
  private Writer writeStdErr = new LoggingWriter();

  /**
   * Returns a writer in which external classes can send
   * <code>String</code> to make them being displayed in the
   * console as standard output.
   */

  public Writer getStdOut()
  {
    return writerStdOut;
  }

  /**
   * Returns a writer in which external classes can send
   * <code>String</code> to make them being displayed in the
   * console as error output.
   */

  public Writer getStdErr()
  {
    return writeStdErr;
  }

  class LoggingWriter extends Writer {

    public void close() { }

    public void flush()
    {
      textArea.repaint();
    }

    public void write(char cbuf[], int off, int len)
    {
      log(new String(cbuf, off, len));
    }
  }

  protected static Dockable buildInstance(AbstractLogWindow frame, String tabTitle, JextFrame parent) {
    Dockable dock = new Dockable(frame, tabTitle, parent, null);
    frame.contDock = dock;
    return dock;
  }

  protected AbstractLogWindow(JextFrame parent, String title) {
    super(title);
    this.parent = parent;

    textArea.setEditable(false);

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(BorderLayout.CENTER, new JScrollPane(textArea,
                                              ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                              ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS));

    setDefaultCloseOperation(HIDE_ON_CLOSE);
    addKeyListener(new KeyAdapter()
    {
      public void keyPressed(KeyEvent evt)
      {
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
          setVisible(false);
      }
    });

    setIconImage(GUIUtilities.getJextIconImage());
  }
}
