/*
 * 17:36:02 09/09/00
 *
 * JextProgressBarUI.java - A new UI for progress bar
 * Copyright (C) 2000 Romain Guy
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

package org.jext.gui;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

import com.jgoodies.plaf.LookUtils;

public class JextProgressBarUI extends javax.swing.plaf.basic.BasicProgressBarUI
{
  public JextProgressBarUI()
  {
  }

  public static ComponentUI createUI(JComponent c)
  {
    return new JextProgressBarUI();
  }

  public void paint(Graphics g, JComponent c)
  {
    Insets b = progressBar.getInsets();
    int barRectX = b.left;
    int barRectY = b.top;
    int barRectWidth = progressBar.getWidth() - (b.right + barRectX);
    int barRectHeight = progressBar.getHeight() - (b.bottom + barRectY);
    int amountFull = getAmountFull(b, barRectWidth, barRectHeight);

    if (amountFull > 0)
    {
      GradientPaint painter = new GradientPaint(barRectX, barRectY,
                                                getHeaderBackground(),
                                                barRectX + barRectWidth, barRectY + barRectHeight,
                                                UIManager.getColor("control"));
      Graphics2D g2 = (Graphics2D) g;
      g2.setPaint(painter);
      g2.fill(new Rectangle(barRectX, barRectY, amountFull, barRectHeight));
    }

    if (progressBar.isStringPainted())
      paintString(g, barRectX, barRectY, barRectWidth, barRectHeight, amountFull, b);
  }

  protected Color getHeaderBackground() {
      Color c = UIManager.getColor("SimpleInternalFrame.activeTitleBackground");
      if (c != null)
          return c;
      if (LookUtils.IS_LAF_WINDOWS_XP_ENABLED)
          c = UIManager.getColor("InternalFrame.activeTitleGradient");
      return c != null ? c : UIManager.getColor("InternalFrame.activeTitleBackground");
  }
}

// End of JextProgressBarUI.java