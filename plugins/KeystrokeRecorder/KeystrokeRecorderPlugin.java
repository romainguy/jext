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

import java.util.Vector;
import java.awt.event.ActionEvent;

import org.jext.*;
import org.jext.options.*;

public class KeystrokeRecorderPlugin implements Plugin {
  private Track aTrack=null;

  public void createOptionPanes(OptionsDialog optionsDialog) {}

  public void start(){
    aTrack = new Track();
    Jext.addAction(new MenuAction("keystroke_recorder.menu.record"){
      public void actionPerformed(ActionEvent evt) {
        aTrack.erase();
        getJextParent(evt).getNSTextArea().getInputHandler().setMacroRecorder(aTrack);
      }
    });
    Jext.addAction(new MenuAction("keystroke_recorder.menu.stop"){
      public void actionPerformed(ActionEvent evt) {
        if (Jext.getBooleanProperty("keystroke_recorder.debug"))
          System.out.println("Stop:"+evt);
        getJextParent(evt).getNSTextArea().getInputHandler().setMacroRecorder(null);
      }
    });
    Jext.addAction(new MenuAction("keystroke_recorder.menu.play"){
      public void actionPerformed(ActionEvent evt) {
        if (Jext.getBooleanProperty("keystroke_recorder.debug"))
          System.out.println("Play:"+evt);
        aTrack.play(getJextParent(evt).getNSTextArea());
      }
    });
  }

  public void stop() { }

  public void createMenuItems(JextFrame parent, Vector pluginsMenus, Vector pluginsMenuItems){
      pluginsMenus.addElement( GUIUtilities.loadMenu( "keystroke_recorder.menu" ) );
  }

}
