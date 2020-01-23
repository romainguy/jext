/*
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

import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

import org.jext.gui.*;

public class JextKLNFTheme extends JextMetalTheme
{
  private final ColorUIResource primary1 = new ColorUIResource(32, 32, 64);
  private final ColorUIResource primary2 = new ColorUIResource(160, 160, 180);
  private final ColorUIResource primary3 = new ColorUIResource(200, 200, 224);
  
  private final ColorUIResource secondary1 = new ColorUIResource(130, 130, 130);
  private final ColorUIResource secondary2 = new ColorUIResource(180, 180, 180);
  private final ColorUIResource secondary3 = new ColorUIResource(224, 224, 224);
  
  public ColorUIResource getPrimary1() { return primary1; }
  public ColorUIResource getPrimary2() { return primary2; }
  public ColorUIResource getPrimary3() { return primary3; }
  
  public ColorUIResource getSecondary1() { return secondary1; }
  public ColorUIResource getSecondary2() { return secondary2; }
  public ColorUIResource getSecondary3() { return secondary3; }
}

// End of JextKLNFTheme.java
