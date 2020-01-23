/*
 * 12:00:00 15/12/02
 *
 * SkinManager.java - The skin selector
 * Copyright (C) 2002 Paolo Giarrusso
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

import java.util.*;
import org.jext.Jext;

public class SkinManager {
  private static HashMap skinList = new HashMap();
  private SkinManager() {}
  private static Skin currSkin = null;//we remember the last apply()ed to unapply() it.
  static {
    registerSkinFactory(new BundledSkinFactory());
  }

  /**
   * This internal method returns an HashMap containing all available skins.
   */
  public static HashMap getSkinList() {
    return skinList;//this should be modified, maybe; used in org.jext.options.
    //UIOptions
  }

  public static void registerSkinFactory(SkinFactory sf) {
    Skin[] skins = sf.getSkins();
    //System.out.println("Added a SkinFactory named: " + sf.getClass().getName());
    if (skins != null) {
      int ln = skins.length;
      for (int i = 0; i < ln; i++)
	if (skins[i] != null && skins[i].isAvailable())
          skinList.put(skins[i].getSkinInternName(), skins[i]);
    }
  }
  
  public static boolean applySelectedSkin() {
    Skin newSkin = (Skin)skinList.get(Jext.getProperty("current_skin"));
    //System.out.println("The current skin is:" + Jext.getProperty("current_skin","metal"));
    try {
      if (newSkin != null) {
	if (currSkin != null)
	  try {
	    currSkin.unapply();
	  } catch (Throwable t) {}
	newSkin.apply();
	currSkin = newSkin;
	return true;
      }
      else
	System.err.println("Selected skin not found");
    } catch (Throwable t) {
      System.err.println("An Exception occurred while selecting the skin " + 
	  Jext.getProperty("current_skin") + "; stack trace:");
      t.printStackTrace();
    }
    newSkin = (Skin)skinList.get("jext");
    if (newSkin != null)
      try {
	newSkin.apply();
	currSkin = newSkin;
      } catch (Throwable t) {
	System.err.println("Impossible to apply the skin \"jext\"; serious problem! ");
      }
    else
      System.err.println("Missing skin \"jext\"; serious problem! ");
    return false;
  }
}
