/*
 * RWModifier.java - Read/Write extension to java.lang.reflect.Modifier
 *
 * Copyright (c) 1999 George Latkiewicz	(georgel@arvotek.net)
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

import java.lang.reflect.*;

//=============================================================================
/**
 * Read/Write extension to java.lang.reflect.Modifier.
 */
public class RWModifier extends Modifier
{
	private static final int STRICT = 2048;  // for JDK 1.1.x (already in super for 1.2)
	private static final int CLASS  = 32768; // custom extension to JDK Modifier spec.

	private static final int NO_MODIFIERS = 0;

	/**
	 * Defines the set of all valid modifiers.
	 */
	private static final int ALL_MODIFIERS = 0
		| ABSTRACT
		| FINAL
		| INTERFACE
		| NATIVE
		| PRIVATE
		| PROTECTED
		| PUBLIC
		| STATIC
		| STRICT	    // nb associated token is: strictfp
		| SYNCHRONIZED
		| TRANSIENT
		| VOLATILE
		| CLASS;

	/**
	 * defines the set of modifiers that are valid for a class or interface.
	 */
	private static final int VALID_FOR_CLASS = 0
		| ABSTRACT
		| FINAL
		| INTERFACE
		| PUBLIC
//		| STATIC		// except member classes
		| STRICT	    // nb associated token is: strictfp
		| CLASS;

	/**
	 * defines the set of modifiers that are invalid for constructor methods.
	 */
	private static final int INVALID_FOR_CONSTRUCTOR = 0
		| NATIVE
		| ABSTRACT
		| STATIC
		| SYNCHRONIZED
		| FINAL;

	/**
	 * defines the set of modifiers that are invalid for a method that has been
	 * defined as abstract.
	 */
	private static final int INVALID_FOR_ABSTRACT_METHOD = 0
		| NATIVE
		| STATIC
		| SYNCHRONIZED
		| FINAL
		| PRIVATE;

	/**
	 * defines the set of modifiers that are invalid for a method defined in an
	 * interface.
	 */
	private static final int INVALID_FOR_INTERFACE_METHOD = INVALID_FOR_ABSTRACT_METHOD
		| PROTECTED;


	// constants - for visibility indicies
	static final int TOPLEVEL_VIS_PACKAGE = 0;
	static final int TOPLEVEL_VIS_PUBLIC  = 1;

	static final int MEMBER_VIS_PRIVATE   = 0;
	static final int MEMBER_VIS_PACKAGE   = 1;
	static final int MEMBER_VIS_PROTECTED = 2;
	static final int MEMBER_VIS_PUBLIC    = 3;


	//-------------------------------------------------------------------------
	// Accessors - getters for RWModifier extensions

	public static final boolean isStrict(int mod)
	{
		// for JDK 1.1.x (already in super for 1.2)
		return (mod & STRICT) > 0;
	}

	public static final boolean isClass(int mod)
	{
		// custom extension to JDK Modifier spec.
		return (mod & CLASS) > 0;
	}

	public static final boolean isClassOrInterface(int mod)
	{
		// custom extension to JDK Modifier spec.
		return (mod & (CLASS | INTERFACE)) > 0;
	}

	//-------------------------------------------------------------------------
	// Accessors - set bit ON

	public static final int setClass(int mod) { return (mod | CLASS); }

	public static final int setAbstract(int mod) { return (mod | ABSTRACT); }

	public static final int setFinal(int mod) { return (mod | FINAL); }

	public static final int setInterface(int mod) { return (mod | INTERFACE); }

	public static final int setNative(int mod) { return (mod | NATIVE); }

	public static final int setPrivate(int mod) { return (mod | PRIVATE); }

	public static final int setProtected(int mod) { return (mod | PROTECTED); }

	public static final int setPublic(int mod) { return (mod | PUBLIC); }

	public static final int setStatic(int mod) { return (mod | STATIC); }

	public static final int setStrict(int mod) { return (mod | STRICT); }

	public static final int setSynchronized(int mod) { return (mod | SYNCHRONIZED); }

	public static final int setTransient(int mod) { return (mod | TRANSIENT); }

	public static final int setVolatile(int mod) { return (mod | VOLATILE); }


	//-------------------------------------------------------------------------
	// Accessors - set bit ON/OFF by parameter

	public static final int setClass(int mod, boolean setFlag)
	{
		return (setFlag) ? (mod | CLASS) : (mod & ~CLASS) ;
	}

	public static final int setAbstract(int mod, boolean setFlag)
	{
		return (setFlag) ? (mod | ABSTRACT) : (mod & ~ABSTRACT) ;
	}

	public static final int setFinal(int mod, boolean setFlag)
	{
		return (setFlag) ? (mod | FINAL) : (mod & ~FINAL) ;
	}

	public static final int setInterface(int mod, boolean setFlag)
	{
		return (setFlag) ? (mod | INTERFACE) : (mod & ~INTERFACE) ;
	}

	public static final int setNative(int mod, boolean setFlag)
	{
		return (setFlag) ? (mod | NATIVE) : (mod & ~NATIVE) ;
	}

	public static final int setPrivate(int mod, boolean setFlag)
	{
		return (setFlag) ? (mod | PRIVATE) : (mod & ~PRIVATE) ;
	}

	public static final int setProtected(int mod, boolean setFlag)
	{
		return (setFlag) ? (mod | PROTECTED) : (mod & ~PROTECTED) ;
	}

	public static final int setPublic(int mod, boolean setFlag)
	{
		return (setFlag) ? (mod | PUBLIC) : (mod & ~PUBLIC) ;
	}

	public static final int setStatic(int mod, boolean setFlag)
	{
		return (setFlag) ? (mod | STATIC) : (mod & ~STATIC) ;
	}

	public static final int setStrict(int mod, boolean setFlag)
	{
		return (setFlag) ? (mod | STRICT) : (mod & ~STRICT) ;
	}

	public static final int setSynchronized(int mod, boolean setFlag)
	{
		return (setFlag) ? (mod | SYNCHRONIZED) : (mod & ~SYNCHRONIZED) ;
	}

	public static final int setTransient(int mod, boolean setFlag)
	{
		return (setFlag) ? (mod | TRANSIENT) : (mod & ~TRANSIENT) ;
	}

	public static final int setVolatile(int mod, boolean setFlag)
	{
		return (setFlag) ? (mod | VOLATILE) : (mod & ~VOLATILE) ;
	}


	//-------------------------------------------------------------------------
	public static boolean isValidForClass(int mod)
	{
		return ( (mod & ~VALID_FOR_CLASS) != 0 ) ;
	}


	//-------------------------------------------------------------------------
	/**
	 * Note: !isClassOrInterface(int mod) is assumed.
	 */
	public static boolean isValidForConstructor(int mod)
	{
		return ( (mod & INVALID_FOR_CONSTRUCTOR) == 0 ) ;
	}

	public static boolean isValidInterfaceMethod(int mod)
	{
		return ( (mod & INVALID_FOR_INTERFACE_METHOD) == 0 ) ;
	}

	public static boolean isValidAbstractMethod(int mod)
	{
		return ( (mod & INVALID_FOR_ABSTRACT_METHOD) == 0 ) ;
	}


	//-------------------------------------------------------------------------
	public static String toString(int mod, Options.DisplayIro displayOpt)
	{
		String rVal = "";

		if (displayOpt.getVisSymbols()) {

			if ( isPrivate(mod) )
				rVal += "-";
			else if ( isProtected(mod) )
				rVal += "#";
			else if ( isPublic(mod) )
				rVal += "+";
			else
				rVal += " ";

		} else {

			if ( isPrivate(mod) )
				rVal += "private ";
			else if ( isProtected(mod) )
				rVal += "protected ";
			else if ( isPublic(mod) )
				rVal += "public ";
			else
				rVal += "";
		}

		// static abstract final volatile transient synchronized native strictfp

		if ( !displayOpt.getStaticUlined()
				&& isStatic(mod) )    rVal += "static ";

		if ( !displayOpt.getAbstractItalic()
				&& isAbstract(mod) )  rVal += "abstract ";

		if ( isFinal(mod) )           rVal += "final ";

		if ( displayOpt.getShowMiscMod() ) {

			if ( isVolatile(mod) )        rVal += "volatile ";

			if ( isTransient(mod) )       rVal += "transient ";

			if ( isSynchronized(mod) )    rVal += "synchronized ";

			if ( isNative(mod) )          rVal += "native ";

			if ( isStrict(mod) )          rVal += "strictfp ";
		}

		if ( displayOpt.getShowIconKeywords() ) {
			if ( isClass(mod) ) {
				rVal += "class ";
			} else if ( isInterface(mod) ) {
				rVal += "interface ";
			}
		}

		return rVal;

	} // static toString(int, Options.DisplayIro): String


	//-------------------------------------------------------------------------
	public static final int getVisLevelIndex(int mod)
	{
		if ( isPublic(mod) ) return 3;
		else if ( isProtected(mod) ) return 2;
		else if ( isPrivate(mod) ) return 0;
		else /* package */ return 1;

	} // static getVisLevelIndex(int): int

	//-------------------------------------------------------------------------
	public static final int getTopLevelVisIndex(int mod)
	{
		if ( isPublic(mod) ) return TOPLEVEL_VIS_PUBLIC;
//		else if ( isProtected(mod) ) // error
//		else if ( isPrivate(mod) )   // error
		else /* package */ return TOPLEVEL_VIS_PACKAGE;

	} // static getTopLevelVisIndex(int): int

	//-------------------------------------------------------------------------
	public static final int getMemberVisIndex(int mod)
	{
		if ( isPublic(mod) ) return MEMBER_VIS_PUBLIC;
		else if ( isProtected(mod) ) return MEMBER_VIS_PROTECTED;
		else if ( isPrivate(mod) ) return MEMBER_VIS_PRIVATE;
		else /* package */ return MEMBER_VIS_PACKAGE;

	} // static getMemberVisIndex(int): int

} // class RWModifier
