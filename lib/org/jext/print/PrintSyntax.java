/*
 * 09/27/2001 - 16:37:37
 *
 * PrintSyntax.java - Prints colors and styles
 * Copyright (C) 2001 Slava Pestov
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

package org.jext.print;

import java.util.*;

import java.awt.*;
import java.awt.print.*;
import javax.swing.text.*;

import org.jext.*;
import org.gjt.sp.jedit.syntax.*;

public class PrintSyntax
{
  private Segment seg = new Segment();

  public void print(JextFrame parent, JextTextArea textArea)
  {
    PrintJob job = parent.getToolkit().getPrintJob(parent, "Jext:" + textArea.getName(), null);
    if (job == null)
      return;

    int topMargin;
    int leftMargin;
    int bottomMargin;
    int rightMargin;
    int ppi = job.getPageResolution();

    topMargin = (int) (0.5 * ppi);
    leftMargin = (int) (0.5 * ppi);
    bottomMargin = (int) (0.5 * ppi);
    rightMargin = (int) (0.5 * ppi);

    boolean printHeader = Jext.getBooleanProperty("print.header");
    boolean printFooter = Jext.getBooleanProperty("print.footer");
    boolean printLineNumbers = Jext.getBooleanProperty("print.lineNumbers");

    String header = textArea.getName();
    String footer = new java.util.Date().toString();
    int lineCount = textArea.getDocument().getDefaultRootElement().getElementCount();

    TabExpander expander = null;
    Graphics gfx = null;

    String fontFamily = Jext.getProperty("print.font");
    int fontSize;
    try
    {
      fontSize = Integer.parseInt(Jext.getProperty("print.fontSize"));
    } catch (NumberFormatException nf) {
      fontSize = 10;
    }

    int fontStyle = Font.PLAIN;

    SyntaxStyle[] styles = textArea.getPainter().getStyles();//GUIUtilities.loadStyles(fontFamily, fontSize);
    Font font = new Font(fontFamily, fontStyle, fontSize);
    FontMetrics fm = null;
    Dimension pageDimension = job.getPageDimension();

    int pageWidth = pageDimension.width;
    int pageHeight = pageDimension.height;
    int y = 0;
    int tabSize = 0;
    int lineHeight = 0;
    int page = 0;

    int lineNumberDigits = (int) Math.ceil(Math.log(lineCount) / Math.log(10));
    int lineNumberWidth = 0;

    for (int i = 0; i < lineCount; i++)
    {
      if (gfx == null)
      {
        page++;

        gfx = job.getGraphics();

        gfx.setFont(font);
        fm = gfx.getFontMetrics();

        if (printLineNumbers)
          lineNumberWidth = fm.charWidth('0') * lineNumberDigits;
        else
          lineNumberWidth = 0;

        lineHeight = fm.getHeight();
        tabSize = textArea.getTabSize() * fm.charWidth(' ');
        expander = new PrintTabExpander(leftMargin + lineNumberWidth, tabSize);

        y = topMargin + lineHeight - fm.getDescent() - fm.getLeading();

        if (printHeader)
        {
          gfx.setColor(Color.lightGray);
          gfx.fillRect(leftMargin, topMargin, pageWidth - leftMargin - rightMargin, lineHeight);
          gfx.setColor(Color.black);
          gfx.drawString(header, leftMargin, y);
          y += lineHeight;
        }
      }

      y += lineHeight;

      gfx.setColor(Color.black);
      gfx.setFont(font);

      int x = leftMargin;
      if (printLineNumbers)
      {
        String lineNumber = String.valueOf(i + 1);
        gfx.drawString(lineNumber, (leftMargin + lineNumberWidth) - fm.stringWidth(lineNumber), y);
        x += lineNumberWidth + fm.charWidth('0');
      }

      paintSyntaxLine(textArea, gfx, expander, textArea.getTokenMarker(), styles, fm,
                      i, font, Color.black, x, y);

      int bottomOfPage = pageHeight - bottomMargin - lineHeight;
      if (printFooter)
        bottomOfPage -= lineHeight * 2;

      if (y >= bottomOfPage || i == lineCount - 1)
      {
        if (printFooter)
        {
          y = pageHeight - bottomMargin;

          gfx.setColor(Color.lightGray);
          gfx.setFont(font);
          gfx.fillRect(leftMargin, y - lineHeight, pageWidth - leftMargin - rightMargin, lineHeight);
          gfx.setColor(Color.black);
          y -= (lineHeight - fm.getAscent());
          gfx.drawString(footer, leftMargin, y);

          String pageStr = Jext.getProperty("print.page.footer", new Integer[] { new Integer(page) });
          int width = fm.stringWidth(pageStr);
          gfx.drawString(pageStr, pageWidth - rightMargin - width, y);
        }

        gfx.dispose();
        gfx = null;
      }
    }

    job.end();
  }

  protected int paintSyntaxLine(JextTextArea textArea, Graphics gfx, TabExpander expander,
                                TokenMarker tokenMarker, SyntaxStyle[] styles, FontMetrics fm,
                                int line, Font defaultFont, Color defaultColor, int x, int y)
  {
    gfx.setFont(defaultFont);
    gfx.setColor(defaultColor);
    //y += fm.getHeight();

    textArea.getLineText(line, seg);
    x = SyntaxUtilities.paintSyntaxLine(seg, tokenMarker.markTokens(seg, line), styles, expander, gfx, x, y);

    return x;
  }

  static class PrintTabExpander implements TabExpander
  {
    private int leftMargin;
    private int tabSize;

    public PrintTabExpander(int leftMargin, int tabSize)
    {
      this.leftMargin = leftMargin;
      this.tabSize = tabSize;
    }

    public float nextTabStop(float x, int tabOffset)
    {
      int ntabs = ((int) x - leftMargin) / tabSize;
      return (ntabs + 1) * tabSize + leftMargin;
    }
  }
}

// End of PrintSyntax.java
