/*
 * TokenMarkerDebugger - A debug class
 * Copyright (c) 1999 Andre Kaplan
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
 
package org.gjt.sp.jedit.syntax;

/**
 *
 * @author  Andre Kaplan
 * @version 0.6
 */
class TokenMarkerDebugger {
	public static final int MAX_COUNT = 100;

	TokenMarkerDebugger() {}

	// Following is a way to detect whether tokenContext.pos is not 
	// correctly incremented. This is for debugging purposes
	public boolean isOK(final TokenMarkerContext tokenContext)
	{
		if (tokenContext.pos <= this.pos) {
			this.count++;
			if (this.count > MAX_COUNT) {
				// Seems that we got stuck somewhere
				this.pos   = tokenContext.pos + 1;
				this.count = 0;
				return false;
			}
			return true;
		} else {
			this.pos   = tokenContext.pos;
			this.count = 0;
			return true;
		}
	}

	public void reset()
	{
		this.pos   = -1;
		this.count = 0;
	}
	private int pos   = -1;
	private int count = 0;
}
