/*
 * 18:55:56 20/01/00
 *
 * Sequence.java - encompases the shorthand/expanded relationship
 * Copyright (C) 2000 Romain Guy, Matt Albrecht
 * powerteam@chez.com
 * www.chez.com/powerteam
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

import org.jext.*;
import java.util.Hashtable;
import java.util.Vector;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 * Creates a fast-key sequence, using the selected text as the
 * expanded part.  The expanded text may be changed, but not the
 * shorthand text.
 */
class Sequence
{
    private String shorthand, expanded;

    Sequence()
    {
    }

    Sequence(String shorthand, String expanded)
    {
        this.shorthand = shorthand;
        this.expanded = expanded;
    }

    public String getShorthand()
    {
        return this.shorthand;
    }

/*
    public void setShorthand(String shorthand)
    {
        this.shorthand = shorthand;
    }
*/

    public String getExpanded()
    {
        return this.expanded;
    }

    public void setExpanded(String expanded)
    {
        this.expanded = expanded;
    }

    public boolean equals( Object o )
    {
        if (o == null) return false;
        if (o == this) return true;
        if (o instanceof Sequence)
        {
            Sequence seq = (Sequence)o;
            if (getShorthand().equals( seq.getShorthand() ))
            {
                return true;
            }
        }
        else
        if (o instanceof String)
        {
            if (o.equals( getShorthand() ))
            {
                return true;
            }
        }
        return false;
    }


    public String toString()
    {
        return "["+this.shorthand+" -> "+this.expanded+"]";
    }
}

