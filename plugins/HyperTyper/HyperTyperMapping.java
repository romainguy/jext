/*
 * 18:55:56 20/01/00
 *
 * HyperTyperMapping.java - Handles the fast-typer mappings and properties
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

import java.util.Hashtable;

/**
 * Controlls the access to the different kinds of known sequence sets;
 * in particular, the persistent and transient sets.
 */
public class HyperTyperMapping implements SequenceChangedListener
{
    private final static String FAST_TYPER_NAME = "fastTyper.";

    // key is shorthand, value is plain text; both persist and transient
    private Hashtable translateMap = new Hashtable();

    // Vector of Sequences; expanded text is escaped.
    private HyperTyperSequenceList
        persistList = new HyperTyperSequenceList( FAST_TYPER_NAME ),
        transientList = new HyperTyperSequenceList( null );


    public HyperTyperMapping()
    {
        this.persistList.addSequenceChangedListener( this );
        this.transientList.addSequenceChangedListener( this );
    }


    /**
     * Saves all persistant mappings to the Jext properties.
     */
    public void save()
    {
        this.persistList.save();
        this.transientList.save();
    }


    /**
     * Retrieves the expanded text from the given shorthand.  This text
     * may be extracted from either the persistent store or the
     * transient store.
     */
    public String getExpandedText( String shorthand )
    {
        return (String)this.translateMap.get( shorthand );
    }


    /**
     * Retrieves the expanded text from the given shorthand as an
     * escape sequence.  This text
     * may be extracted from either the persistent store or the
     * transient store.
     */
    public String getEscapedExpandedText( String shorthand )
    {
        return HyperTyperSequenceList.plainToEscape(
            getExpandedText( shorthand ) );
    }


    /**
     * Get all persistent mappings.
     */
    public HyperTyperSequenceList getPersistentMappings()
    {
        return this.persistList;
    }


    /**
     * Get all transient mappings.
     */
    public HyperTyperSequenceList getTransientMappings()
    {
        return this.transientList;
    }


    /**
     * Shutdown the fast typer.
     */
    public void shutdown()
    {
        this.persistList.shutdown();
        this.transientList.shutdown();

        this.persistList = null;
        this.transientList = null;
    }



    public void sequenceAdded( SequenceChangedEvent sce )
    {
        Sequence seq = sce.seq;
        translateMap.put(
            seq.getShorthand(),
            HyperTyperSequenceList.escapeToPlain(
                seq.getExpanded() ) );
    }

    public void sequenceRemoved( SequenceChangedEvent sce )
    {
        translateMap.remove( sce.seq.getShorthand() );
    }

}
// End of HyperTyperMapping.java
