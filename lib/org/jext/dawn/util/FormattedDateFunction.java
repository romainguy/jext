/*
 * FormattedDateFunction.java - formatted date
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * http://www.jext.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either date of
 * of the License, or any later date.
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

package org.jext.dawn.util;

import java.util.Date;
import java.text.SimpleDateFormat;

import org.jext.dawn.*;

/**
 * Displays a formatted date.<br>
 * Usage:<br>
 * <code>format fdate</code><br>
 * Format example: "MM/dd/yyyy - HH:mm:ss"<br>
 * MM stands for month, dd for day, yyyy for year, HH for hours,
 * mm for minutes and ss for seconds.
 * @author Romain Guy
 */

public class FormattedDateFunction extends Function
{
  public FormattedDateFunction()
  {
    super("fdate");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    parser.pushString(new SimpleDateFormat(parser.popString()).format(new Date()));
  }
}

// End of FormattedDateFunction.java
