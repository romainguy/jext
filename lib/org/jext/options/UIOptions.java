/*
 * 03/30/2002 - 15:40:03
 *
 * UIOptions.java - The interface options pane
 * Copyright (C) 2001 Romain Guy
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
 
/*
 * Patch - Code: BB1(From BlaisorBlade)
 * Date : 14 December 2002
 * This patch supports the new Skin architecture(see org.jext.gui.*Skin*)
 * with the ability to change the skin without restarting Jext.
 * Note : modifications from this patch are marked as follow by BB1
 * Thanks to this one:
 * Date : April, 13th 2001
 * Purpose : adds the ability to change the skin without having to restart Jext.
 * Author : Julien Ponge (julien@izforge.com - http://www.izforge.com)
 */

package org.jext.options;

import java.io.File;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jext.*;
import org.jext.gui.*;

import java.util.*;

public class UIOptions extends AbstractOptionPane 
{
  private JComboBox icons;
  private JComboBox skins;
  private JextCheckBox showToolbar, labeledSeparator, gray, flatMenus,
                       buttonsHighlight, //antiAliasing,
                       toolbarRollover, decoratedFrames;
  private static final String[] iconsInternNames = { "_eclipse", "_16",  "_s16", "_20", "_s24", "_24" };
  private static final String[] iconsNames = { "Eclipse", "Tiny", "Tiny Swing", "20x20", "Swing", "Gnome" };
  private int currSkinIndex;
  private SkinItem[] skinsNames;

  public UIOptions()
  {
    super("ui");
    
    HashMap skinList = SkinManager.getSkinList();
    String[] skinsFullNames = new String[skinList.size()];
    skinsNames = new SkinItem[skinList.size()];
    int n = 0;

    for (Iterator i = skinList.values().iterator(); i.hasNext(); n++)
    {
      Skin currSkin = (Skin) i.next();
      String skinIntName = currSkin.getSkinInternName();
      String skinName = currSkin.getSkinName();
      skinsFullNames[n] = skinName;
      skinsNames[n] = new SkinItem(skinName, skinIntName);
    }
    
    Arrays.sort(skinsNames);
    Arrays.sort(skinsFullNames);

    skins = new JComboBox(skinsFullNames);
    skins.setRenderer(new ModifiedCellRenderer());
    addComponent(Jext.getProperty("options.skins.label", "Select skin:"), skins);
    
    icons = new JComboBox(iconsNames);
    addComponent(Jext.getProperty("options.icons.label"), icons);

    //addComponent(antiAliasing = new JextCheckBox(Jext.getProperty("options.antialiasing.label")));
    //// WAITING FOR ANTI-ALIAS BUG FIX /////
    //antiAliasing.setEnabled(false);
    //addComponent(jextTheme = new JextCheckBox(Jext.getProperty("options.jexttheme.label")));

    addComponent(decoratedFrames = new JextCheckBox(Jext.getProperty("options.decoratedframes.label")));
    decoratedFrames.setEnabled(Utilities.JDK_VERSION.charAt(2) >= '4');
    addComponent(flatMenus = new JextCheckBox(Jext.getProperty("options.flatmenus.label")));
    addComponent(toolbarRollover = new JextCheckBox(Jext.getProperty("options.toolbarrollover.label")));
    addComponent(buttonsHighlight = new JextCheckBox(Jext.getProperty("options.buttonshighlight.label")));
    addComponent(labeledSeparator = new JextCheckBox(Jext.getProperty("options.separator.label")));
    addComponent(showToolbar = new JextCheckBox(Jext.getProperty("options.toolbar.label")));
    addComponent(gray = new JextCheckBox(Jext.getProperty("options.graytoolbar.label")));

    ActionListener al = new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        control(true);
      }
    };
    flatMenus.addActionListener(al);
    showToolbar.addActionListener(al);
    load();
  }
  
  public void load()
  {
    String size = Jext.getProperty("jext.look.icons");
    int i = 0;
    for ( ; i < 5; i++)
    {
      if (size.equals(iconsInternNames[i]))
        break;
    }
    icons.setSelectedIndex(i);

    String skin = Jext.getProperty("current_skin");
    i = 0;
    for ( ; i < skinsNames.length; i++)
    {
      if (skin.equals(skinsNames[i].skinIntName))
        break;
    }

    currSkinIndex = i;
    if (i >= skinsNames.length)
      i = 0;
    skins.setSelectedIndex(i);
    
    //// WAITING FOR ANTI-ALIAS BUG FIX /////
    //antiAliasing.setSelected(Jext.getBooleanProperty("editor.antiAliasing"));
    decoratedFrames.setSelected(Jext.getBooleanProperty("decoratedFrames"));
    flatMenus.setSelected(Jext.getBooleanProperty("flatMenus"));
    toolbarRollover.setSelected(Jext.getBooleanProperty("toolbarRollover"));
    buttonsHighlight.setSelected(Jext.getBooleanProperty("buttonsHighlight"));
    labeledSeparator.setSelected(Jext.getBooleanProperty("labeled.separator"));
    showToolbar.setSelected(Jext.getBooleanProperty("toolbar", "on"));
    gray.setSelected(Jext.getBooleanProperty("toolbar.gray"));
    control(false);
  }

  public Component getComponent()
  {
    JScrollPane scroller = new JScrollPane(this);
    Dimension _dim = this.getPreferredSize();
    scroller.setPreferredSize(new Dimension((int) _dim.width, 410));
    //scroller.setBorder(javax.swing.border.LineBorder.createBlackLineBorder());
    return scroller;
  }

  private void control(boolean selection)
  {
    labeledSeparator.setEnabled(flatMenus.isSelected());
    gray.setEnabled(showToolbar.isSelected());
    toolbarRollover.setEnabled(showToolbar.isSelected());
  }

  public void save()
  {
    Jext.setProperty("decoratedFrames", decoratedFrames.isSelected() ? "on" : "off");
    Jext.setProperty("toolbar", showToolbar.isSelected() ? "on" : "off");
    Jext.setProperty("toolbar.gray", (gray.isEnabled() && gray.isSelected()) ? "on" : "off");
    Jext.setProperty("labeled.separator", (labeledSeparator.isEnabled() && labeledSeparator.isSelected()) ? "on" : "off");
    Jext.setProperty("flatMenus", flatMenus.isSelected() ? "on" : "off");
    Jext.setProperty("buttonsHighlight", (buttonsHighlight.isEnabled() && buttonsHighlight.isSelected()) ? "on" : "off");
    //Jext.setProperty("editor.antiAliasing", antiAliasing.isSelected() ? "on" : "off");
    Jext.setProperty("toolbarRollover", (toolbarRollover.isEnabled() && toolbarRollover.isSelected()) ? "on" : "off");
    Jext.setProperty("jext.look.icons", iconsInternNames[icons.getSelectedIndex()]);

    ///////////////////////////////////////////////////////////////////////////////
    // BB1
    int newSkinIndex = skins.getSelectedIndex();
    if (currSkinIndex != newSkinIndex)
    {
      currSkinIndex = newSkinIndex;
      Jext.setProperty("current_skin", skinsNames[currSkinIndex].skinIntName);
      SkinManager.applySelectedSkin();
      updateUIs();
    }
    ///////////////////////////////////////////////////////////////////////////////
  }
  ///////////////////////////////////////////////////////////////////////////////
  // BB1
  
  // Updates all Jext instances + the options dialog box
  private void updateUIs()
  {
    SwingUtilities.updateComponentTreeUI(OptionsDialog.getInstance());
    OptionsDialog.getInstance().pack();

    ArrayList instances = Jext.getInstances();
    int nInstances = instances.size();
    for (int i = 0; i < nInstances; i++)
    {
      JextFrame frame = (JextFrame) instances.get(i);
      SwingUtilities.updateComponentTreeUI(frame);
      //frame.pack(); //it would make sense, but the window becomes too big.
      //So REMOVED.
    }
  }
  
  ///////////////////////////////////////////////////////////////////////////////

  class SkinItem implements Comparable
  {
    public String skinName, skinIntName;

    public SkinItem(String skinName, String skinIntName)
    {
      this.skinName = skinName;
      this.skinIntName = skinIntName;
    }

    public boolean equals(Object o)
    {
      return skinName.equals(o);
    }

    public int compareTo(Object o)
    {
      return skinName.compareTo(((SkinItem) o).skinName);
    }

    public String toString()
    {
      return skinName;
    }
  }


}

// End of UIOptions.java