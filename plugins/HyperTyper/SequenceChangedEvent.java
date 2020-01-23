/*
 * 18:55:56 20/01/00
 *
 * SequenceChangedEvent.java - Contains all info with a sequence change
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

public class SequenceChangedEvent implements Cloneable
{
    public HyperTyperSequenceList sequenceList;
    public int index;
    public Sequence seq;

    public SequenceChangedEvent( HyperTyperSequenceList sequenceList,
            int index, Sequence seq )
    {
        this.sequenceList = sequenceList;
        this.index = index;
        this.seq = seq;
    }

    public Object clone()
    {
        return new SequenceChangedEvent( this.sequenceList, this.index,
            this.seq );
    }
}
// End of HyperTyperSequenceList.java
