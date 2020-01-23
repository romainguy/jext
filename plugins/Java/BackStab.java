/*
 * BackStab.java
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


/**
 * BackStab subclasses UML.Element and stabs it in the back by giving away the
 * secret of its line number to whoever asks.
 */
public class BackStab extends UML.Element implements Comparable
{

	public BackStab(String name)
	{
		super(name);
	}//end BackStab(String)
	
	
	private BackStab(UML.Element elm)
	{
		super(elm.name, elm.type, elm.mod, elm.parent, elm.line);
	}//end BackStab(UML.Element)
	
	
	public static BackStab fromElement(UML.Element elm)
	{
		return new BackStab(elm);
	}//end fromElement
	
	
	public int getLine()
	{
		return this.line;
	}//end getLine()

	
	public static int getLine(UML.Element elm)
	{
		return elm.line;
	}//end getLine()
	
	
	public int compareTo(Object o)
	 throws ClassCastException
	{
		BackStab b = (BackStab)o;
		if (this.line == b.line)
		{
			return 0;
		}//end if line numbers are equal
		else
		{
			return (this.line < b.line) ? -1 : 1;
		}//end else
	}//end compareTo()
	
	
	public String toString()
	{
		return new String(name + " at " + line);
	}//end toString()

}//end class BackStab
