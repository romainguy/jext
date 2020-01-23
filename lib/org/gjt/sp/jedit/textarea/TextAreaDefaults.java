/*
 * TextAreaDefaults.java - Encapsulates default values for various settings
 * Copyright (C) 1999 Slava Pestov
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

package org.gjt.sp.jedit.textarea;

import org.gjt.sp.jedit.syntax.*;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;

/**
 * Encapsulates default settings for a text area. This can be passed
 * to the constructor once the necessary fields have been filled out.
 * The advantage of doing this over calling lots of set() methods after
 * creating the text area is that this method is faster.
 */
public class TextAreaDefaults
{
  private static TextAreaDefaults DEFAULTS;

  public InputHandler inputHandler;
  public boolean editable;

  public boolean caretVisible;
  public boolean caretBlinks;
  public boolean blockCaret;
  public int electricScroll;

  public boolean gutterCollapsed;
  public int gutterWidth;
  public Color gutterBgColor;
  public Color gutterFgColor;
  public Color gutterHighlightColor;
  public Color gutterBorderColor;
  public Color caretMarkColor;
  public Color anchorMarkColor;
  public Color selectionMarkColor;
  public int gutterBorderWidth;
  public int gutterNumberAlignment;
  public Font gutterFont;

  public int cols;
  public int rows;
  public SyntaxStyle[] styles;
  public Color caretColor;
  public Color selectionColor;
  public Color lineHighlightColor;
  public boolean lineHighlight;
  public Color bracketHighlightColor;
  public boolean bracketHighlight;
  public Color eolMarkerColor;
  public boolean eolMarkers;
  public boolean paintInvalid;

  public boolean wrapGuide;
  public Color wrapGuideColor;
  public int wrapGuideOffset;

  public boolean linesIntervalHighlight;
  public Color linesIntervalColor;
  public int linesInterval;

  public boolean antiAliasing;

  public JPopupMenu popup;

  /**
   * Returns a new TextAreaDefaults object with the default values filled
   * in.
   */
  public static TextAreaDefaults getDefaults()
  {
    if (DEFAULTS == null)
    {
      DEFAULTS = new TextAreaDefaults();

      DEFAULTS.editable = true;

      DEFAULTS.caretVisible = true;
      DEFAULTS.caretBlinks = true;
      DEFAULTS.electricScroll = 3;

      DEFAULTS.gutterCollapsed = true;
      DEFAULTS.gutterWidth = 40;
      DEFAULTS.gutterBgColor = Color.white;
      DEFAULTS.gutterFgColor = Color.black;
      DEFAULTS.gutterHighlightColor = new Color(0x8080c0);
      DEFAULTS.gutterBorderColor = Color.gray;
      DEFAULTS.caretMarkColor = Color.green;
      DEFAULTS.anchorMarkColor = Color.red;
      DEFAULTS.selectionMarkColor = Color.blue;
      DEFAULTS.gutterBorderWidth = 4;
      DEFAULTS.gutterNumberAlignment = Gutter.RIGHT;
      DEFAULTS.gutterFont = new Font("monospaced", Font.PLAIN, 10);

      DEFAULTS.cols = 80;
      DEFAULTS.rows = 25;
      DEFAULTS.styles = SyntaxUtilities.getDefaultSyntaxStyles();
      DEFAULTS.caretColor = Color.red;
      DEFAULTS.selectionColor = new Color(0xccccff);
      DEFAULTS.lineHighlightColor = new Color(0xe0e0e0);
      DEFAULTS.lineHighlight = true;
      DEFAULTS.bracketHighlightColor = Color.black;
      DEFAULTS.bracketHighlight = true;
      DEFAULTS.eolMarkerColor = new Color(0x009999);
      DEFAULTS.eolMarkers = true;
      DEFAULTS.paintInvalid = true;

      DEFAULTS.linesIntervalColor = new Color(0xe6e6ff);
      DEFAULTS.linesIntervalHighlight = false;
      DEFAULTS.linesInterval = 5;

      DEFAULTS.wrapGuideColor = Color.red;
      DEFAULTS.wrapGuide = false;
      DEFAULTS.wrapGuideOffset = 80;
    }

    return DEFAULTS;
  }
}
