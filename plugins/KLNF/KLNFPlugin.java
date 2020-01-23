/*
 * KLNFPlugin.java - KLNF plugin
 * Copyright (C) 2001 Romain Guy
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

import java.awt.Color;
 
import java.util.Vector;

//import javax.swing.UIManager;

import org.jext.*;
import org.jext.gui.*;
import org.jext.options.*;
import javax.swing.UIManager; 
import com.incors.plaf.kunststoff.*;

/**
 * KLNF is a plugin for the Jext java text editor.
 * @author Romain Guy
 */

public class KLNFPlugin implements Plugin, SkinFactory 
{
  public void createMenuItems(JextFrame parent, Vector menus, Vector menuItems)
  {
    // creates menu items
  }

  public void createOptionPanes(OptionsDialog parent)
  {
    //parent.addOptionPane(new KLNFOptionPane());
  }

  public void start() { }

  public Skin[] getSkins()
  {
    return new Skin[] { new GenericSkin("Kunststoff Skin", "kunststoff", 
	                                      new KunststoffLookAndFeel(), KLNFPlugin.class.getClassLoader())
    {
      	public void apply() throws Throwable
      	{
      	  if (Jext.getBooleanProperty("toolbar.gray"))
      	    Jext.setProperty("toolbar.gray", "off");
      	  Jext.setProperty("jext.look.icons", "_s16");

      	  JextButton.setRollover(false);
      	  JextButton.setHighlightColor(new Color(255, 255, 255));
      	  JextButton.blockHighlightChange();//these calls are needed because 

      	  JextHighlightButton.setHighlightColor(new Color(255, 255, 255));
      	  JextHighlightButton.blockHighlightChange();

      	  JextToggleButton.setHighlightColor(new Color(255, 255, 255));
      	  JextToggleButton.blockHighlightChange();

          KunststoffLookAndFeel.setCurrentTheme(new JextKLNFTheme()); 
          super.apply();
          //UIManager.put("ClassLoader", KLNFPlugin.class.getClassLoader());
          //KunststoffLookAndFeel.setCurrentTheme(new JextKLNFTheme()); 
          //UIManager.setLookAndFeel(new KunststoffLookAndFeel());
      	}
      
      	public void unapply() throws Throwable
      	{
      	  JextButton.unBlockHighlightChange();
      	  JextHighlightButton.unBlockHighlightChange();
      	  JextToggleButton.unBlockHighlightChange();
      	}
      }
    };
  }

  public void stop()
  {
    // stops the plugin
  }
}

// End of KLNFPlugin.java
