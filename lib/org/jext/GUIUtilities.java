/*
 * 03/31/2002 - 16:00:13
 *
 * GUIUtilities.java - Very useful methods to build a GUI
 * Portions copyright (C) 1998-2000 Slava Pestov
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

import java.lang.reflect.*;

import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.Window;

import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import org.gjt.sp.jedit.syntax.*;
import org.jext.*;
import org.jext.gui.*;

/**
 * Contains a bunch of methods used to build the GUI.
 */

public class GUIUtilities
{
  // Jext icon
  private static final Image ICON_IMAGE = Utilities.getImage("images/window_icon.gif", Jext.class);

  /** Contains the action name and label of each menu item */
  public static Hashtable menuItemsActions = new Hashtable();

  /**
   * Returns the Jext environment icon.
   */

  public static final Image getJextIconImage()
  {
    return ICON_IMAGE;
  }

  /**
   * Sets the scrollable behavior of a JTabbedPane.
   */

  public static void setScrollableTabbedPane(JTabbedPane pane)
  {
    if (!Jext.getBooleanProperty("scrollableTabbedPanes"))
      return;

    try
    {
      Class cl = pane.getClass();
      Method m = cl.getMethod("setTabLayoutPolicy", new Class[] { int.class });
      if (m !=  null)
      {
        Field f = cl.getField("SCROLL_TAB_LAYOUT");
        m.invoke(pane, new Object[] { new Integer(f.getInt(pane)) });
      }
    } catch (Exception e) { }
  }

  /**
   * Focuses on the specified component as soon as the window becomes
   * active.
   * @param win The window
   * @param comp The component
   */

  public static void requestFocus(final Window win, final Component comp)
  {
    win.addWindowListener(new WindowAdapter()
    {
      public void windowActivated(WindowEvent evt)
      {
        comp.requestFocus();
        win.removeWindowListener(this);
      }
    });
   }

  /**
   * Saves a window geometry
   * @param win The <code>Window</code>
   * @param name Name of the properties containing the geometry
   */

  public static void saveGeometry(Window win, String name)
  {
    Dimension size = win.getSize();
    Jext.setProperty(name + ".width", String.valueOf(size.width));
    Jext.setProperty(name + ".height", String.valueOf(size.height));

    Point location = win.getLocation();
    int x = location.x;
    int y = location.y;
    if (x < -4)
      x = -4;
    if (y < -4)
      y = -4;
    Jext.setProperty(name + ".x", String.valueOf(x));
    Jext.setProperty(name + ".y", String.valueOf(y));
  }

  /**
   * Load a window geometry from a propery file and apply it to
   * the specified window.
   * @param win The <code>Window</code>
   * @param name Name of the properties containing the geometry
   */

  public static void loadGeometry(Window win, String name)
  {
    int x, y, width, height;

    try
    {
      width = Integer.parseInt(Jext.getProperty(name + ".width"));
      height = Integer.parseInt(Jext.getProperty(name + ".height"));
    } catch (NumberFormatException nf) {
      Dimension size = win.getSize();
      width = size.width;
      height = size.height;
    }

    try
    {
      x = Integer.parseInt(Jext.getProperty(name + ".x"));
      y = Integer.parseInt(Jext.getProperty(name + ".y"));
    } catch (NumberFormatException nf) {
      Dimension screen = win.getToolkit().getScreenSize();
      x = (screen.width - width) / 2;
      y = (screen.height - height) / 2;
    }

    win.setLocation(x < -4 ? -4 : x, y < -4 ? -4 : y);
    win.setSize(width, height);
  }

  /**
   * Displays a dialog box.
   * The title of the dialog is fetched from
   * the <code><i>name</i>.title</code> property. The message is fetched
   * from the <code><i>name</i>.message</code> property. The message
   * is formatted by the property manager with <code>args</code> as
   * positional parameters.
   * @param frame The frame to display the dialog for
   * @param name The name of the dialog
   * @param args Positional parameters to be substituted into the
   * message text
   */

  public static void message(Frame frame, String name, Object[] args)
  {
    JOptionPane.showMessageDialog(frame,
                                  Jext.getProperty(name.concat(".message"), args),
                                  Jext.getProperty(name.concat(".title"), args),
                                  JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Displays an error dialog box.
   * The title of the dialog is fetched from
   * the <code><i>name</i>.title</code> property. The message is fetched
   * from the <code><i>name</i>.message</code> property. The message
   * is formatted by the property manager with <code>args</code> as
   * positional parameters.
   * @param frame The frame to display the dialog for
   * @param name The name of the dialog
   * @param args Positional parameters to be substituted into the
   * message text
   */

  public static void error(Frame frame, String name, Object[] args)
  {
    JOptionPane.showMessageDialog(frame,
                                  Jext.getProperty(name.concat(".message"), args),
                                  Jext.getProperty(name.concat(".title"), args),
                                  JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Converts a hex color value prefixed with #, for example #ff0088.
   * @param name The color value
   */

  public static Color parseColor(String name)
  {
    if (name == null)
      return Color.black;
    else if (name.startsWith("#"))
    {
      try
      {
        return Color.decode(name);
      } catch (NumberFormatException nfe) {
        return Color.black;
      }
    }
    return Color.black;
  }

  /**
   * Converts a color object to its hex value. The hex value
   * prefixed is with #, for example #ff0088.
   * @param c The color object
   */

  public static String getColorHexString(Color c)
  {
    String colString = Integer.toHexString(c.getRGB() & 0xffffff);
    return "#000000".substring(0, 7 - colString.length()).concat(colString);
  }

  /**
   * Converts a style string to a style object.
   * @param str The style string
   * @exception IllegalArgumentException if the style is invalid
   */

  public static SyntaxStyle parseStyle(String str) throws IllegalArgumentException
  {
    Color color = Color.black;
    boolean italic = false;
    boolean bold = false;
    StringTokenizer st = new StringTokenizer(str);
    while (st.hasMoreTokens())
    {
      String s = st.nextToken();
      if (s.startsWith("color:"))
        color = GUIUtilities.parseColor(s.substring(6));
      else if (s.startsWith("style:"))
      {
        for (int i = 6; i < s.length(); i++)
        {
          if (s.charAt(i) == 'i')
            italic = true;
          else if (s.charAt(i) == 'b')
            bold = true;
          else
            throw new IllegalArgumentException("Invalid style: " + s);
        }
      }
      else
        throw new IllegalArgumentException("Invalid directive: " + s);
    }
    return new SyntaxStyle(color, italic, bold);
  }

  /**
   * Converts a style into it's string representation.
   * @param style The style
   */

  public static String getStyleString(SyntaxStyle style)
  {
    StringBuffer buf = new StringBuffer();

    buf.append("color:" + getColorHexString(style.getColor()));
    if (!style.isPlain())
    {
      buf.append(" style:" + (style.isItalic() ? "i" : "") +
                             (style.isBold() ? "b" : ""));
    }

    return buf.toString();
  }

  public static JMenu loadMenu(String name)
  {
    return loadMenu(name, false);
  }

  /**
   * Loads a menu from the properties.
   * The white space separated list of menu items is obtained from
   * the property named <code>name</code>. The menu label is
   * obtained from the <code>name.label</code> property.
   * @param name The menu name
   * @param isLabel True if name is label, too
   */

  public static JMenu loadMenu(String name, boolean isLabel)
  {
    if (name == null)
      return null;

    String label;
    if (!isLabel)
    {
      label = Jext.getProperty(name + ".label");
      if (label == null)
        label = name;
    }
    else
      label = name;

    JextMenu menu;

    int index = label.indexOf('$');
    if (index != -1 && label.length() - index > 1)
    {
      menu = new JextMenu(label.substring(0, index).concat(label.substring(++index)));
      menu.setMnemonic(Character.toLowerCase(label.charAt(index)));
    }
    else
      menu = new JextMenu(label);

    if (isLabel)
      return menu;

    String menuItems = Jext.getProperty(name);
    if (menuItems != null)
    {
      StringTokenizer st = new StringTokenizer(menuItems);
      while (st.hasMoreTokens())
      {
        String menuItemName = st.nextToken();
        if (menuItemName.equals("-"))
        {
          if (Jext.getFlatMenus())
            menu.getPopupMenu().add(new JextMenuSeparator());
          else
            menu.getPopupMenu().addSeparator();
        } else {
          JMenuItem mi = loadMenuItem(menuItemName);
          if (mi != null)
            menu.add(mi);
        }
      }
    }

    return menu;
  }

  /**
   * This method creates a new JMenuItem. This special version is
   * targeted to be used with plugins.
   * @param action Plugin action name
   * @return A new <code>JMenuItem</code>
   */

  public static JMenuItem loadMenuItem(String action)
  {
    String name = Jext.getProperty(action.concat(".label"));
    if (name == null)
      name = (new java.util.Date()).toString();
    return loadMenuItem(name, action, null, true, true);
  }

  /**
   * This method creates a new JMenuItem. See menus/XMenuHandler.class
   * for more informations about its usage.
   * @param label The menu item label
   * @param action The name of the action, specified in Jext
   * @param keyStroke The keystroke used as accelerator
   * @param picture Relative path to an icon
   * @param enabled Disable the item if false
   * @return A new <code>JMenuItem</code>
   */

  public static JMenuItem loadMenuItem(String label, String action,
                                       String picture, boolean enabled)
  {
    return loadMenuItem(label, action, picture, enabled, true);
  }

  /**
   * This method creates a new JMenuItem. See menus/XMenuHandler.class
   * for more informations about its usage.
   * @param label The menu item label
   * @param action The name of the action, specified in Jext
   * @param keyStroke The keystroke used as accelerator
   * @param picture Relative path to an icon
   * @param enabled Disable the item if false
   * @param list If true adds item info to a list
   * @return A new <code>JMenuItem</code>
   */

  public static JMenuItem loadMenuItem(String label, String action,
                                       String picture, boolean enabled, boolean list)
  {
    String keyStroke = new String();

    if (label == null)
      return null;

    EnhancedMenuItem mi;
    int index = label.indexOf('$');

    if (action != null)
    {
      String _keyStroke = Jext.getProperty(action.concat(".shortcut"));
      if (_keyStroke != null)
        keyStroke = _keyStroke;
    }

    if (index != -1 && label.length() - index > 1)
    {
      mi = new EnhancedMenuItem(label.substring(0, index).concat(label.substring(++index)),
                                keyStroke);
      mi.setMnemonic(Character.toLowerCase(label.charAt(index)));
    } else
      mi = new EnhancedMenuItem(label, keyStroke);

    if (picture != null)
    {
      ImageIcon icon = Utilities.getIcon(picture.concat(Jext.getProperty("jext.look.icons")).concat(".gif"),
                       Jext.class);
      if (icon != null)
       mi.setIcon(icon);
    }

    // if (keyStroke != null) mi.setAccelerator(parseKeyStroke(keyStroke));

    if (action != null)
    {
      MenuAction a = Jext.getAction(action);
      if (a == null)
        mi.setEnabled(false);
      else
      {
        mi.addActionListener(a);
        mi.setEnabled(enabled);

        if (list)
        {
          StringBuffer _buf = new StringBuffer(label.length());
          char c;
          for (int i = 0; i < label.length(); i++)
          {
            if ((c = label.charAt(i)) != '$')
              _buf.append(c);
          }

          if (action.startsWith("one_"))
            _buf.append(" (One Click!)");

          if (menuItemsActions.get(action) == null)
            menuItemsActions.put(action, _buf.toString());
        }
      }
    } else
      mi.setEnabled(enabled);

    return mi;
  }
}

// End of GUIUtilities.java
