/*
 * 10/29/2001 - 22:19:41
 *
 * ConsoleTextPane.java - Console text component
 * Copyright (C) 1999 Romain Guy
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

import javax.swing.*;

import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.*;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.jext.*;
import org.gjt.sp.jedit.textarea.*;

/**
 * <code>ConsoleTextPane</code> is the text area of Jext
 * console. That's where user types his commands, and also
 * where command outputs are displayed.
 * This class handles key and mouse events.
 */

public class ConsoleTextPane extends JTextPane
{
  private Console parent;
  private ConsoleKeyAdapter _keyListener;
  private MouseAdapter _mouseListener;
  
  /**
   * Creates a new text area for the console.
   * @param parent <code>Console</code> parent
   */

  public ConsoleTextPane(Console parent)
  {
    super();
    this.parent = parent;

    new DropTarget(this, new DnDHandler());
    _keyListener = new ConsoleKeyAdapter();
    addKeyListener(_keyListener);
    /*_mouseListener = new MouseAdapter()
    {
      public void mousePressed(MouseEvent evt)
      {
        evt.consume();
        if (getCaretPosition() < ConsoleTextPane.this.parent.getUserLimit())
          setCaretPosition(getDocument().getLength());
      }
    };
    addMouseListener(_mouseListener);*/
  }

  class ConsoleKeyAdapter extends KeyAdapter
  {
    public void keyPressed(KeyEvent evt)
    {
      int key = evt.getKeyCode();
      if (evt.isControlDown()) // event of the form Ctrl+KEY
      {
        switch (key)
        {
          case KeyEvent.VK_C:                       // Ctrl+C copies selected text
            return;
          case KeyEvent.VK_D:                       // Ctrl+D kills current task
            parent.stop();
            // we wait 1 second otherwise the prompt could not display as expected
            try
            {
              Thread.sleep(1000);
            } catch (InterruptedException ie) { }
            parent.displayPrompt();
	    break;
        }
	evt.consume();
	return;
      } else if (evt.isShiftDown()) {
        if (key == KeyEvent.VK_TAB)
        {
          parent.doBackwardSearch();
          evt.consume();
          return;
        }
      }

      switch (key)
      {
        case KeyEvent.VK_DELETE:                    // we delete a char
          parent.deleteChar();
          evt.consume();
          break;
        case KeyEvent.VK_BACK_SPACE:                // we delete a char
          parent.removeChar();
          evt.consume();
          break;
        case KeyEvent.VK_ENTER:                     // we execute command
          String command = parent.getText();
          if (!command.equals(""))
          {
	    parent.addHistory(command);
          }
          parent.execute(command);
          evt.consume();
          break;
        case KeyEvent.VK_UP:                        // we get previous typed command
          parent.historyPrevious();
          evt.consume();
          break;
        case KeyEvent.VK_DOWN:                      // we get next typed command
          parent.historyNext();
          evt.consume();
        case KeyEvent.VK_LEFT:                      // we override the press on LEFT
                                                    // to ensure caret is not on prompt
          if (getCaretPosition() > parent.getUserLimit())
            setCaretPosition(getCaretPosition() - 1);
          evt.consume();
          break;
        case KeyEvent.VK_TAB:                       // complete filename
          parent.doCompletion(); //doBackwardSearch();
          evt.consume();
          break;
        case KeyEvent.VK_HOME:                      // implementation of HOME to go to
          setCaretPosition(parent.getUserLimit());  // beginning of prompt and not line
          evt.consume();
          break;
        case KeyEvent.VK_END:                       // see HOME
          setCaretPosition(parent.getTypingLocation());
          evt.consume();
          break;
        case KeyEvent.VK_ESCAPE:                   // delete line
          parent.setText("");
          evt.consume();
      }
    }

    public void keyTyped(KeyEvent evt)
    {
      if (parent.getTypingLocation() < getDocument().getLength())//this forbids from
         //inserting text until prompt is reshown or Enter is pressed(maybe to remove?)
      {
        evt.consume();
        return;
      }

      if (getCaretPosition() < parent.getUserLimit())
        setCaretPosition(parent.getUserLimit());

      char c = evt.getKeyChar();

      if (c != KeyEvent.CHAR_UNDEFINED && !evt.isAltDown())
      {
        if (c >= 0x20 && c != 0x7f)
          parent.add(String.valueOf(c));
      }
      evt.consume();
    }
  }

  class DnDHandler implements DropTargetListener
  {
    public void dragEnter(DropTargetDragEvent evt) { }
    public void dragOver(DropTargetDragEvent evt) { }
    public void dragExit(DropTargetEvent evt) { }
    public void dragScroll(DropTargetDragEvent evt) { }
    public void dropActionChanged(DropTargetDragEvent evt) { }

    public void drop(DropTargetDropEvent evt)
    {
      DataFlavor[] flavors = evt.getCurrentDataFlavors();
      if (flavors == null)
        return;

      boolean dropCompleted = false;
      for (int i = flavors.length - 1; i >= 0; i--)
      {
        if (flavors[i].isFlavorJavaFileListType())
        {
          evt.acceptDrop(DnDConstants.ACTION_COPY);
          Transferable transferable = evt.getTransferable();
          try
          {
            StringBuffer buf = new StringBuffer();
            Iterator iterator = ((List) transferable.getTransferData(flavors[i])).iterator();
            while (iterator.hasNext())
              buf.append(' ').append(((File) iterator.next()).getPath());
            parent.add(buf.toString());
            dropCompleted = true;
          } catch (Exception e) { }
        }
      }
      evt.dropComplete(dropCompleted);
    }
  }
  

}

// End of ConsoleTextPane.java