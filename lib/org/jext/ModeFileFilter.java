/*
 * 12:21:49 08/05/00
 *
 * ModeFileFilter.java - A file filter for syntax modes
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

package org.jext;

import gnu.regexp.RE;
import gnu.regexp.REException;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * <code>ModeFileFilter</code> is a file filter specific to a given
 * syntax colorizing mode.
 */

public class ModeFileFilter extends FileFilter
{
  private RE regexp;
  private String modeName;
  private String description;
  
  /**
   * Creates a new file filter for given syntax colorizing mode.
   * @param mode The syntax colorizing mode which gives file filters infos
   */

  public ModeFileFilter(Mode mode)
  {
    String filterDescription = Jext.getProperty("file.filters");

    modeName = mode.getModeName();

    description = mode.getUserModeName();
    if (!description.endsWith(filterDescription))
      description += filterDescription;
  }

  /**
   * Reload the filter; use this when the user changes it.
   * Called from a JextFrame inner class by a JextListener when properties change.
   */
  /*friendly*/ void rebuildRegexp() {
    try
    {
      String filter = Jext.getProperty("mode." + modeName + ".fileFilter");
      if (filter != null)
      {
        regexp = new RE(Utilities.globToRE(filter), RE.REG_ICASE);
      }
    } catch (REException re) {
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns Jext internal mode name of the mode used to
   * build this file filter.
   */

  public String getModeName()
  {
    return modeName;
  }

  /**
   * If given file is correctly named, it is accepted by the
   * file filter.
   */

  public boolean accept(File file)
  {
    /* Lazy building of regexp is also needed because custom properties are
     * loaded after building these filters.*/
    if (regexp == null) {
      rebuildRegexp();
    }
    if (file != null)
    {
      if (file.isDirectory() || regexp == null)
        return true;

      String _file = new String();
      int index = file.getPath().lastIndexOf(File.separatorChar);
      if (index != -1)
        _file = file.getPath().substring(index + 1);

      try
      {
        return regexp.isMatch(_file);
      } catch (Exception e) { }
    }

    return false;
  }

  /**
   * Returns a simple description of the file filter.
   */

  public String getDescription()
  {
    return description;
  }
  

}

// End of ModeFileFilter.java