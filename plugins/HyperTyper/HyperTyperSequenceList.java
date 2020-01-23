/*
 * 18:55:56 20/01/00
 *
 * HyperTyperSequenceList.java - Handles a specific list of sequences
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

import java.util.*;
import org.jext.*;
import org.gjt.sp.jedit.textarea.*;

public class HyperTyperSequenceList
{
    // Vector of Sequences; expanded text is escaped.
    private Vector list = new Vector();
    private Vector listeners = new Vector();
    private String prefix;


    public HyperTyperSequenceList( String propertyPrefix )
    {
        if (propertyPrefix != null)
        {
            if (!propertyPrefix.endsWith("."))
            {
                propertyPrefix += ".";
            }
        }
        this.prefix = propertyPrefix;
        init();
    }


    /**
     * Adds a new sequence changed listener.  After being added, it is
     * sent the list of currently known sequences.
     */
    public void addSequenceChangedListener( SequenceChangedListener scl )
    {
        if (scl != null)
        {
            this.listeners.addElement( scl );

            SequenceChangedEvent sce = new SequenceChangedEvent( this,
                0, null );
            int i = 0, len = this.list.size();
            for (; i < len; i++)
            {
                sce.index = i;
                sce.seq = (Sequence)this.list.elementAt(i);
                scl.sequenceAdded( sce );
            }
        }
    }

    /**
     * Removes an existing registered sequence changed listener.
     */
    public void removeSequenceChangedListener( SequenceChangedListener scl )
    {
        this.listeners.removeElement( scl );
    }


    public int getSequenceCount()
    {
        return this.list.size();
    }


    /**
     * Retrieve a sequence at the given index.
     */
    public Sequence getSequenceAt( int index )
    {
        return (Sequence)this.list.elementAt( index );
    }


    /**
     * Loads all settings from the properties list.
     */
    public void init()
    {
        removeAllMappings();

        if (this.prefix == null)
        {
            return;
        }

        int index = -1;
        String  value, shorthand, exp;
        for (int i = 0 ; ; i++)
        {
            value = Jext.getProperty( this.prefix + i);
            if (value == null)
            {
                break;
            }
            index = value.indexOf('=');
            if (index == -1)
            {
                break;
            }
            shorthand = value.substring(0, index);
            exp = value.substring( index + 1 );
            addMapping( shorthand, exp );
        }
    }


    /**
     * Saves all persistant sequences to the Jext properties.
     */
    public void save()
    {
        if (prefix == null)
        {
            return;
        }

        // check if mappings have been initialized yet
        int pos = 0, size = this.list.size();
        Sequence seq;

        // overwrite existing entries
        for (; pos < size; pos++)
        {
            seq = (Sequence)this.list.elementAt( pos );

            Jext.setProperty( this.prefix + pos,
                seq.getShorthand() + '=' +
                seq.getExpanded() );
        }

        // remove all others
        String prop, val;
        for (; ; pos++)
        {
            prop = this.prefix + pos;
            val = Jext.getProperty( prop );
            if (val == null) break;
            Jext.unsetProperty( prop );
        }
    }


    /**
     * Get vector of all persistent mappings, as an escape sequence.
     */
    public Vector getMappings()
    {
        return this.list;
    }


    /**
     * Sets a persistent mapping, where the expanded text is an
     * escape sequence.
     */
    public void addMapping( String shorthand, String expanded )
    {
        addMapping( new Sequence( shorthand, expanded ) );
    }


    /**
     * Sets a persistent mapping, where the expanded text is an
     * escape sequence.
     */
    public void addMapping( Sequence seq )
    {
        // if the shorthand is already defined, then remove it.
        this.removeMapping( seq.getShorthand() );

        int size = this.list.size();
        this.list.addElement( seq );

        fireSequenceAdded( seq, size );
    }

    /**
     * Sets a persistent mapping, where the expanded text is a
     * plain text.
     */
    public void addPlainMapping( String shorthand, String expanded )
    {
        addMapping( new Sequence( shorthand, plainToEscape( expanded ) ) );
    }


    /**
     * Removes the given shorthand mapping from the persistent or
     * transient list, depending where it is currently stored.
     */
    public void removeMapping( String shorthand )
    {
        // Sequence .equals() also works if a string is compared to it;
        // if it's seq.equals( string ), but not the other way.
        int index = this.list.indexOf( new Sequence( shorthand, "" ) );

        if (index < 0) return;

        Sequence seq = getSequenceAt( index );

        this.list.removeElementAt( index );

        fireSequenceRemoved( seq, index );
    }

    /**
     * Remove all mappings, both persistent and transient.
     */
    public void removeAllMappings()
    {
        int listIndex = 0, listLen = this.list.size();
        for (; listIndex < listLen; listIndex++)
        {
            fireSequenceRemoved( getSequenceAt( listIndex ), listIndex );
        }

        this.list.removeAllElements();
    }



    /**
     * Shutdown the list.
     */
    public void shutdown()
    {
        save();

        this.list.removeAllElements();
        this.list = null;
        this.listeners.removeAllElements();
        this.listeners = null;
    }


    /**
     * Converts an escape-encoded text string into plain text.
     */
    public static String escapeToPlain(String value)
    {
        char c;
        StringBuffer _buf = new StringBuffer(value.length());

        for (int i = 0; i < value.length(); i++)
        {
            c = value.charAt(i);
            if (i < value.length() - 1 && c == '\\')
            {
                i++;
                c = value.charAt(i);
                switch (c)
                {
                    case 'n':
                        _buf.append( '\n' );
                        break;
                    case 't':
                        _buf.append('\t');
                        break;
                    default:
                        _buf.append(c);
                }
            }
            else
            {
                _buf.append(c);
            }
        }
        return _buf.toString();
    }


    /**
     * Converts plain text strings into an escape-encoded text string.
     */
    public static String plainToEscape(String value)
    {
        char c;
        StringBuffer _buf = new StringBuffer(value.length());

        for (int i = 0; i < value.length(); i++)
        {
            c = value.charAt(i);
            switch (c)
            {
                case '\n':
                    _buf.append("\\n");
                    break;
                case '\t':
                    _buf.append("\\t");
                    break;
                case '\\':
                    _buf.append("\\\\");
                    break;
                default:
                    _buf.append( c );
            }
        }
        return _buf.toString();
    }



    /**
     * Fires a mapping was added
     */
    protected void fireSequenceAdded( Sequence seq, int index )
    {
        int i = 0, len = this.listeners.size();
        SequenceChangedEvent sce = new SequenceChangedEvent( this,
            index, seq );
        for (; i < len; i++)
        {
            ((SequenceChangedListener)this.listeners.elementAt(i)).
                sequenceAdded( sce );
        }
    }


    /**
     * Fires a mapping was removed
     */
    protected void fireSequenceRemoved( Sequence seq, int index )
    {
        int i = 0, len = this.listeners.size();
        SequenceChangedEvent sce = new SequenceChangedEvent( this,
            index, seq );
        for (; i < len; i++)
        {
            ((SequenceChangedListener)this.listeners.elementAt(i)).
                sequenceRemoved( sce );
        }
    }

}
// End of HyperTyperSequenceList.java
