/*
 * Copyright (C) 2002 James Kolean
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

import java.awt.event.*;
import java.util.*;

import org.jext.*;

public class Track implements org.gjt.sp.jedit.textarea.InputHandler.MacroRecorder {
  private Vector keyStrokes=new Vector();


  public void actionPerformed(ActionListener listener, String actionCommand) {
    if (Jext.getBooleanProperty("keystroke_recorder.debug"))
      System.out.println("Recorder got:"+actionCommand+":"+listener);
    keyStrokes.addElement(new Command(listener,actionCommand));
  }

  public void erase() {
    keyStrokes.clear();
  }

  public void play(JextTextArea jextTextArea) {
    if (Jext.getBooleanProperty("keystroke_recorder.debug"))
    {
      System.out.println("TextArea:"+jextTextArea);
      System.out.println("Keystrokes: "+keyStrokes.toString());
    }//end if debug
    for ( Enumeration enum=keyStrokes.elements(); enum.hasMoreElements(); ){
      ((Command)enum.nextElement()).run(jextTextArea);
    }
  }

  class Command {
    private ActionListener listener;
    private String actionCommand;
    Command(ActionListener listener, String actionCommand) {
      this.listener=listener;
      this.actionCommand=actionCommand;
    }
    void run(JextTextArea jextTextArea) {
      jextTextArea.getInputHandler().executeAction(listener, jextTextArea, actionCommand);
    }
    
    public String toString()
    {
      return new StringBuffer("Track$Command: ").append(
       listener.getClass().getName()).append(' ').append(
       actionCommand).toString();
    }//end toString
  
  }
}
