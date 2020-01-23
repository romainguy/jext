/*
 * 06/13/2003
 *
 * Copyright (C) 2003 Paolo Giarrusso
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
package org.jext.misc;

import java.io.*;
import java.net.*;

public class DownloaderThread extends CopyThread {
  
  protected URL source;
  protected String outPath, tempPath;
  protected File outFile, tempFile;

  /*public static DownloaderThread newInstance(InputStream _in, OutputStream _out, HandlingRunnable _notify,
      int _expectedLen, String _outPath) {
  }*/

  public DownloaderThread(URL source, HandlingRunnable notify, String outPath) {
    super(notify);
    this.outPath = outPath;
    this.source = source;
  }

  public Object work() throws IOException {
    URLConnection conn = source.openConnection();
    int expectedLen = conn.getContentLength();

    String tempPath = outPath + "__FRAG__";
    File outFile = new File(outPath);
    File tempFile = new File(tempPath);

    //FIXME: think about the case below. The caller must avoid that we download the file 2 times.
    //Not us!
    /*if (tempFile.exists())
      tempFile.renameTo(new File(tempPath + ".bak")); //Could fail and return false!*/

    this.in = new BufferedInputStream(conn.getInputStream());
    this.out = new BufferedOutputStream(new FileOutputStream(tempFile));

    super.work();

    if (expectedLen != -1 && expectedLen != tempFile.length())
      throw new IOException("The download was not completed");

    if (outFile.exists()) {
      outFile.renameTo(new File(outPath + ".bak"));
      outFile.delete();
    }

    tempFile.renameTo(outFile);
    return null;
  }
}
