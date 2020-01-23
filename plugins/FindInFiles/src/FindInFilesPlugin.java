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

public class FindInFilesPlugin implements Plugin {
  public static final String USE_GLOB_PROP="findinfiles.useglob";
  private  FindInFiles findInFiles = null;


  public void createOptionPanes(OptionsDialog optionsDialog) {
    optionsDialog.addOptionPane(new FindInFilesOptions());
  }

  public void start(){
    Jext.addAction(new MenuAction("FindInFiles"){
      public void actionPerformed(ActionEvent evt) {
        JextFrame jextFrame = getJextParent(evt);
        if ( findInFiles==null ) {
          findInFiles = new FindInFiles(jextFrame);
          jextFrame.getVerticalTabbedPane().add(Jext.getProperty("findinfiles.title"), findInFiles);
        } else {
          jextFrame.getVerticalTabbedPane().remove(findInFiles);
          findInFiles = null;
        }
      }
    });
  }

  public void stop() { }

  public void createMenuItems(JextFrame parent, Vector pluginsMenus, Vector pluginsMenuItems){
    pluginsMenuItems.add(GUIUtilities.loadMenuItem("FindInFiles"));
  }

}
