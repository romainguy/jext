/*
 * 05/23/2003
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

import org.jext.misc.*;
import com.microstar.xml.*;
import java.io.Reader;
import java.util.*;
import java.text.*;
import java.net.URL;

public class PlugReader extends HandlerBase implements AbstractPlugReader {
  private boolean loaded = false;
  private PluginDesc[] plugins = null;
  private String[] mirrors = null;

  public boolean loadXml(Reader in) {
    XmlParser parser = new XmlParser();
    parser.setHandler(this);
    try {
      parser.parse(null, null, in);
      loaded = true;
      return true;
    } catch (Exception e) { //the parser throws ONLY this exception. So I can't do
      //anything else :-((( !
      e.printStackTrace();
      loaded = false;
      return false;
    }
  }

  public String[] getMirrors() {
    if (!loaded)
      return null;
    return mirrors;
  }

  public PluginDesc[] getPlugins() {
    if (!loaded)
      return null;
    return plugins;
  }

  private Format buildFormatter(String urlTempl) {
    return new MessageFormat(urlTempl);
  }//must return the template string wrapped inside a class.

  //XML parsing part.
  ArrayList plugList, authors, deps, mirrorList;//, files;
  PluginDesc currPlugin;
  Format currUrlTemplate;
  StringBuffer content;
  HashMap attribs;

  public void startDocument() {
    plugList = new ArrayList(40);
    authors = new ArrayList(5);
    deps = new ArrayList(5);
    //files = new ArrayList(3);
    mirrorList = new ArrayList(10);
    attribs = new HashMap(6);
    content = new StringBuffer(1000);
  }

  public void endDocument() {
    plugins = (PluginDesc[]) plugList.toArray(new PluginDesc[0]);
    mirrors = (String[]) mirrorList.toArray(new String[0]);

    plugList = null;
    attribs = null;
    content = null;
    authors = null;
    deps = null;
    //files = null;
  }

  public void attribute(String atName, String value, boolean isSpecified) {
    attribs.put(atName, value);
  }

  public void startElement(String elname) {
    content.setLength(0);
    if (elname.equals("plugin")) {
      currPlugin = new PluginDesc((String) attribs.get("name"), (String) attribs.get("version"),
          (String) attribs.get("displayName"));
      currPlugin.setUrlFormatter(currUrlTemplate);
    } else if (elname.equals("dependStr")) {
      deps.add((String) attribs.get("value"));
    } else if (elname.equals("file")) {
      int size;
      String type = (String) attribs.get("type");
      String name = (String) attribs.get("name");
      String _size = (String) attribs.get("size");
      if (! ( _size == null || "".equals(_size) ) )
        size = Integer.parseInt(_size);
      else
        size = -1;
      if (type.equals("bin"))
        currPlugin.setBinName(name, size);
      else
        currPlugin.setSrcName(name, size);
    } else if (elname.equals("mirror")) {
      mirrorList.add((String) attribs.get("name"));
    } else if (elname.equals("author")) {
      authors.add(new PluginAuthor((String) attribs.get("email"), (String) attribs.get("name")));
    } else if (elname.equals("plugList")) {
      currUrlTemplate = buildFormatter((String) attribs.get("downloadUrlTempl"));
    }
  }

  public void charData(char[] ch, int start, int length) {
    content.append(ch, start, length);
  }

  public void endElement(String elname) {
    if (elname.equals("plugin")) {
      currPlugin.setDeps( (String []) deps.toArray(new String[0]));
      currPlugin.setAuthors( (PluginAuthor []) authors.toArray(new PluginAuthor[0]));
      plugList.add(currPlugin);
      authors.clear();
      deps.clear();
      currPlugin = null;
    } else if (elname.equals("description"))
      currPlugin.setDesc(content.toString());
    else if (elname.equals("htmlauthor"))
      authors.add(new PluginAuthor(content.toString()));
    attribs.clear();
  }
}
