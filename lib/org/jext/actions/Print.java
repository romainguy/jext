/*
 * Print.java
 * Copyright (C) 2000 Scot Bellamy
 *
 * This	free software; you can redistribute it and/or
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

package org.jext.actions;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import org.jext.*;
import org.jext.print.*;

public class Print extends MenuAction
{
  public Print()
  {
    super("print");
  }

  public void actionPerformed(ActionEvent evt)
  {
    JextFrame parent = getJextParent(evt);
    JextTextArea textArea = parent.getTextArea();
    parent.hideWaitCursor();

    try
    {
      if (Jext.getBooleanProperty("print.syntax"))
      {
        PrintSyntax printSyntax = new PrintSyntax();
        printSyntax.print(parent, textArea);
      } else {
        PrintingOptions printOptions = new PrintingOptions();
        printOptions.setPrintLineNumbers(Jext.getBooleanProperty("print.lineNumbers"));
        printOptions.setPrintHeader(Jext.getBooleanProperty("print.header"));
        printOptions.setWrapText(Jext.getBooleanProperty("print.wrapText"));
        printOptions.setPageFont(new Font(Jext.getProperty("print.font"), Font.PLAIN,
                                          (new Integer(Jext.getProperty("print.fontSize"))).intValue()));
    
        PageFormat pgfmt = new PageFormat();
        Paper paper = pgfmt.getPaper();
        pgfmt.setOrientation((new Integer(Jext.getProperty("print.pageOrientation"))).intValue());
        double width = ((new Double(Jext.getProperty("print.pageWidth"))).doubleValue());
        double height = ((new Double(Jext.getProperty("print.pageHeight"))).doubleValue());
        double imgX = ((new Double(Jext.getProperty("print.pageImgX"))).doubleValue());
        double imgY = ((new Double(Jext.getProperty("print.pageImgY"))).doubleValue());
        double imgWidth = ((new Double(Jext.getProperty("print.pageImgWidth"))).doubleValue());
        double imgHeight = ((new Double(Jext.getProperty("print.pageImgHeight"))).doubleValue());
    
        paper.setSize(width, height);
        paper.setImageableArea(imgX, imgY, imgWidth, imgHeight);
        pgfmt.setPaper(paper);
        printOptions.setPageFormat(pgfmt);
    
        PrintText print = new PrintText(textArea.getDocument(), textArea.getName(), printOptions,
                                        textArea.getSoftTab(), textArea.getTabSize());
      }
    } catch (Exception ioe) {
      Utilities.showError(Jext.getProperty("textarea.print.error"));
    }

    parent.hideWaitCursor();
  }
}

// End of Print.java
