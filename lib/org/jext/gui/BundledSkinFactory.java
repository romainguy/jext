/*
 * 01/20/2003 - 23:17:11
 *
 * BundledSkinFactory.java - Provides bundled Skin's.
 * Copyright (C) 2002 Paolo Giarrusso
 * Tricks by Romain GUY
 * romain.guy@jext.org
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

package org.jext.gui;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.util.ArrayList;

import com.jgoodies.clearlook.ClearLookManager;
import com.jgoodies.plaf.*;
import com.jgoodies.plaf.plastic.PlasticLookAndFeel;

class BundledSkinFactory implements SkinFactory
{
  public Skin[] getSkins()
  {
    ArrayList skins = new ArrayList(8);
    skins.add(new PlasticSkin());
    skins.add(new MetalSkin());
    skins.add(new JextSkin());
    skins.add(new GenericSkin("Unix Motif Skin", "motif", "com.sun.java.swing.plaf.motif.MotifLookAndFeel"));

    if (!UIManager.getSystemLookAndFeelClassName().equals(UIManager.getCrossPlatformLookAndFeelClassName()))
      skins.add(new GenericSkin("Native Skin", "native",  UIManager.getSystemLookAndFeelClassName()));

    // these skins are added only if they are present on the underlying system
    // I use the same name since only one of the two will really exist.
    addSkinIfPresent(skins, "MacOs Native Skin", "_macos", "javax.swing.plaf.mac.MacLookAndFeel");
    addSkinIfPresent(skins, "MacOs Native Skin", "macos", "com.sun.java.swing.plaf.mac.MacLookAndFeel");
    addSkinIfPresent(skins, "GTK Skin", "gtk", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");

    //for windows Skin, it doesn't work, since the bytecode always exists.
    //So we override the standard isAvailable() method

    skins.add(new GenericSkin("Windows Native Skin", "windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel")
    {
      public boolean isAvailable()
      {
        return (new com.sun.java.swing.plaf.windows.WindowsLookAndFeel()).isSupportedLookAndFeel();
      }
    });
    
    return (Skin[]) skins.toArray(new Skin[0]);
    //it passes a Skin[] to the toArray method;
    //otherwise the array would be an Object array with Skin's as elements;
    //so, instead it is at runtime a Skin array.
  }

  /**
   * Adds the descripted skin to the array list if the skins exists. The test is made against the
   * presence of the Look And Feel class on the system.
   * @return If the operation succeed, i.e. if the skin was added to the list
   */

  private boolean addSkinIfPresent(ArrayList skins, String description, String name, String lnfClass)
  {
    try
    {
      Class bytecode = Class.forName(lnfClass);
      if (bytecode != null)
      {
        skins.add(new GenericSkin(description, name, lnfClass));
        return true;
      }
    } catch (Exception e) { }

    return false;
  }

  private class MetalSkin extends Skin
  {
    public String getSkinName() {
      return "Standard Metal Skin";
    }
    public String getSkinInternName() {
      return "metal";
    }
    public void apply() throws Throwable {
      MetalLookAndFeel.setCurrentTheme(new javax.swing.plaf.metal.DefaultMetalTheme());
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    }
  }
  
  private class JextSkin extends Skin
  {
    public String getSkinName() {
      return "Jext Metal Skin";
    }
    public String getSkinInternName() {
      return "jext";
    }
    public void apply() throws Throwable {
      MetalLookAndFeel.setCurrentTheme(new JextMetalTheme());
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    }
  }

  private class PlasticSkin extends Skin
  {
    private PlasticSettings settings = PlasticSettings.createDefault();
    private Object oldUIClassLoader;

    public String getSkinName() {
      return "Plastic Skin";
    }
    public String getSkinInternName() {
      return "plastic";
    }
    public void unapply() throws Throwable {
      UIManager.put("ClassLoader", oldUIClassLoader);
    }
    
    public void apply() throws Throwable {
      oldUIClassLoader = UIManager.get("ClassLoader");
      UIManager.put("ClassLoader", LookUtils.class.getClassLoader());
      Options.setDefaultIconSize(new java.awt.Dimension(16, 16));
      
      UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, settings.isUseSystemFonts());
      Options.setGlobalFontSizeHints(settings.getFontSizeHints());
      Options.setUseNarrowButtons(settings.isUseNarrowButtons());
      
      Options.setTabIconsEnabled(settings.isTabIconsEnabled());
      ClearLookManager.setMode(settings.getClearLookMode());
      ClearLookManager.setPolicy(settings.getClearLookPolicyName());
      UIManager.put(Options.POPUP_DROP_SHADOW_ENABLED_KEY, settings.isPopupDropShadowEnabled());
      
      PlasticLookAndFeel.setMyCurrentTheme(settings.getSelectedTheme());
      PlasticLookAndFeel.setTabStyle(settings.getPlasticTabStyle());
      PlasticLookAndFeel.setHighContrastFocusColorsEnabled(settings.isPlasticHighContrastFocusEnabled());
      
      JRadioButton radio = new JRadioButton();
      radio.getUI().uninstallUI(radio);
      JCheckBox checkBox = new JCheckBox();
      checkBox.getUI().uninstallUI(checkBox);
      
      UIManager.setLookAndFeel(settings.getSelectedLookAndFeel());
    }
  }
}