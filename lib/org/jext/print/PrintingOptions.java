/*
 * PrintintOptions.java - Short Description
 * Copyright (C) 1999 Scot Bellamy, ACTS, Inc.
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

/**
 * Stores the print options for printing using the PrintText class.
*/
public class PrintingOptions
{
  private boolean printLineNumbers_ = false;
  private boolean wrapText_ = false;
  private boolean printHeader_ = false;
  private boolean printSyntax_ = false;
  private Font pageFont_ = new Font("Courier", Font.PLAIN, 10); // the page font (default)
  private Font headerFont_ = new Font("Courier", Font.BOLD, 10); // the page font (default)
  private PageFormat pageFormat_ = null;

  public PrintingOptions()
  {
    pageFormat_ = new PageFormat();
  }


  public PrintingOptions(boolean printLineNumbers, boolean wrapText, boolean printHeader, boolean printSyntax,
          Font pageFont, PageFormat pageFormat)
  {
    printLineNumbers_ = printLineNumbers;
    wrapText_ = wrapText;
    printHeader_ = printHeader;
    printSyntax_ = printSyntax;
    pageFont_ = pageFont;
    pageFormat_ = pageFormat;

    // Build the header font;
    headerFont_ = new Font(pageFont_.getName(), Font.BOLD, pageFont_.getSize());
  }

  public void setPrintLineNumbers(boolean printLineNumbers)
  {
    printLineNumbers_ = printLineNumbers;
  }

  public boolean getPrintLineNumbers()
  {
    return printLineNumbers_;
  }

  public void setWrapText(boolean wrapText)
  {
    wrapText_ = wrapText;
  }

  public boolean getWrapText()
  {
    return wrapText_;
  }

  public void setPrintHeader(boolean printHeader)
  {
    printHeader_ = printHeader;
  }

  public boolean getPrintHeader()
  {
    return printHeader_;
  }

  public void setPrintSyntax(boolean printSyntax)
  {
    printSyntax_ = printSyntax;
  }

  public boolean getPrintSyntax()
  {
    return printSyntax_;
  }

  public void setPageFont(Font pageFont)
  {
    pageFont_ = pageFont;
    // Build the header font;
    headerFont_ = new Font(pageFont_.getName(), Font.BOLD, pageFont_.getSize());
  }

  public Font getPageFont()
  {
    return pageFont_;
  }

  public Font getHeaderFont()
  {
    return headerFont_;
  }

  public void setPageFormat(PageFormat pageFormat)
  {
    pageFormat_ = pageFormat;
  }

  public PageFormat getPageFormat()
  {
    return pageFormat_;
  }
}

// End of PrintingOptions.java
