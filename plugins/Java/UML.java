/*
 * UML.java - UML
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

import java.util.Vector; // required for UML.Operation.argList
import javax.swing.ImageIcon; // icons

public class UML
{
	//=========================================================================
	public static class Type
	{
		public static final Type CLASS       = new Type("class",
				new ImageIcon(UML.class.getResource("Class.gif")));

		public static final Type INTERFACE   = new Type("interface",
				new ImageIcon(UML.class.getResource("Interface.gif")));

		public static final Type INNER_CLASS = new Type("inner-class",
				new ImageIcon(UML.class.getResource("InnerClass.gif")));

		public static final Type METHOD      = new Type("",
				new ImageIcon(UML.class.getResource("Operation.gif")));

		public static final Type ATTRIBUTE   = new Type("",
				new ImageIcon(UML.class.getResource("Attribute.gif")));

		public static final Type EXTENDS   = new Type("extends",
				new ImageIcon(UML.class.getResource("Extends.gif")));

		public static final Type IMPLEMENTS   = new Type("implements",
				new ImageIcon(UML.class.getResource("Implements.gif")));

		public static final Type THROWS   = new Type("throws",
				new ImageIcon(UML.class.getResource("Throws.gif")));

		public static final Type ERROR       = new Type("ERROR",
				new ImageIcon(UML.class.getResource("Error.gif")));

		// use POSSIBLE_VALUES to build an iterator
		public static final Type[] POSSIBLE_VALUES = {
			CLASS, INTERFACE, INNER_CLASS, METHOD, ATTRIBUTE, ERROR };

		protected String label = null;
		protected ImageIcon icon = null;

		//---------------------------------------------------------------------
		private Type(String label, ImageIcon icon)
		{
			this.label = label;
			this.icon = icon;
		}

		public ImageIcon getIcon() { return icon; }

		public String toString() { return label.toString(); }

	} // class UML.Type


	//=========================================================================
	static class Element
	{
		// The class which all elements that are to be displayed in a UMLTree
		// must inherit from.

		protected String name = null;
		protected UML.Type type = null;
		protected int mod = 0;
	//	protected int canonicalMod = 0;
		protected Element parent = null;
		protected int line = -1;

		Element(String name, UML.Type type, int mod, Element parent, int line)
		{
			this.name   = name;
			this.type   = type;
			this.mod    = mod;
			this.parent = parent;
			this.line   = line;

			// canonicalMod =
		}

		Element(String name) { this.name = name; }

		public final UML.Type getElementType() { return (UML.Type) type; }
		public final void setElementType(UML.Type type) { this.type = type; }

		public final Element getParentElement() { return (Element) parent; }
		public final void setParentElement(Element e) { this.parent = parent; }

		public final UML.Type getParentElementType()
		{
			if ( parent == null ) {
				return null;
			} else {
				return parent.getElementType();
			}
		}

		public String getName() { return name; }
		public void setName(String name) { this.name = name; }


		public String toString(Options.DisplayIro displayOpt) { return name; }
		public String toString() { return name; }

		public boolean isVisible(Options.FilterIro filterOpt) {
			return true;
		}

		// ??? later use canonical modifier
		public /*final*/ boolean isAbstract() {
			return ( RWModifier.isAbstract(mod) || RWModifier.isInterface(mod) ); }

		public final boolean isStatic() { return RWModifier.isStatic(mod); }
		public final boolean isInterface() { return RWModifier.isInterface(mod); }
		public final boolean isClass() { return RWModifier.isClass(mod); }

	} // class UML.Element


	//=========================================================================
	static class PackageMember extends Element
	{
		// A top-level package member (i.e. class/interface that is a
		// direct member of a package)

		PackageMember(String name, UML.Type type, int mod, int line)
		{
			super(name, type, mod, null, line);
		}

		public String toString(Options.DisplayIro displayOpt)
		{
			return ( (displayOpt.getShowLineNum()) ? ( (line + 1) + ":") : "" )
					+ RWModifier.toString(mod, displayOpt)
					+ name;

		} // toString(Options.DisplayIro): String


		//---------------------------------------------------------------------
		public boolean isVisible(Options.FilterIro filterOpt)
		{
			boolean rVal = false;

			if ( RWModifier.getTopLevelVisIndex(mod)
					>= filterOpt.getTopLevelVisIndex() ) {

				rVal = true;
			}

			return rVal;

		} // isVisible(Options.FilterIro): boolean

	} // class UML.PackageMember extends UML.Element


	//=========================================================================
	static class NestedMember extends Element
	{
		// A top-level package member (i.e. class/interface that is a
		// direct member of a package)

		NestedMember(String name, UML.Type type, int mod, Element parent, int line)
		{
			super(name, type, mod, parent, line);
		}

		public String toString(Options.DisplayIro displayOpt) {

			String myPath = "";
			Element parent = getParentElement();
			while (parent != null) {
				myPath = parent.getName() + " ." + myPath;
				parent = parent.getParentElement();
			}

			return ( (displayOpt.getShowLineNum()) ? ( (line + 1) + ":") : "" )
					+ RWModifier.toString(mod, displayOpt)
					+ ( displayOpt.getShowNestedName() ? (myPath + name) : name );

		} // toString(Options.DisplayIro): String


		//---------------------------------------------------------------------
		public boolean isVisible(Options.FilterIro filterOpt)
		{
			boolean rVal = false;

			if ( RWModifier.getVisLevelIndex(mod)
					>= filterOpt.getMemberVisIndex() ) {

				rVal = true;
			}

			return rVal;

		} // isVisible(Options.FilterIro): boolean

	} // class UML.NestedMember extends UML.Element


	//=========================================================================
	static class Generalization extends Element
	{
		// A top-level package member (i.e. class/interface that is a
		// direct member of a package)

		Generalization(String name, UML.Type type, Element parent, int line)
		{
			// (String name, UML.Type type, int mod, Element parent, int line)
			super(name, type, 0, parent, line);
		}

		public String toString(Options.DisplayIro displayOpt)
		{
			String generalizationOf = "interface";
			if (parent.isClass() && type == Type.EXTENDS ) {
				generalizationOf = "class";
			}
											
			return ( (displayOpt.getShowLineNum()) ? ( (line + 1) + ":") : "" )
					+ ( (displayOpt.getShowIconKeywords()) ? (type.toString() + " ") : "" )
					+ generalizationOf + " " + name;

		} // toString(Options.DisplayIro): String


		//---------------------------------------------------------------------
		public boolean isVisible(Options.FilterIro filterOpt)
		{
			boolean rVal = false;

			if ( filterOpt.getShowGeneralizations() ) rVal = true;

			return rVal;

		} // isVisible(Options.FilterIro): boolean

	} // class UML.Generalization extends UML.Element


	//=========================================================================
	static class Message extends Element
	{
		Message(String name, UML.Type type, Element parent, int line)
		{
			super(name, type, 0, parent, line);
		}

		//---------------------------------------------------------------------
		public boolean isVisible(Options.FilterIro filterOpt)
		{
			return true;
		}

		public String toString(Options.DisplayIro displayOpt)
		{
			return ( displayOpt.getShowLineNum() ? ( (line + 1) + ":") : "" )
					+ ( displayOpt.getShowIconKeywords() ? (type + ": " + name) : (name) );
		} // toString(Options.DisplayIro): String

	} // class UML.Message extends UML.Element


	//=========================================================================
	static class Operation extends Element
	{
		private String returnType;
		private Vector argList = null;
		private boolean isConstructor = false;

		Operation(String name, String returnType, int mod, Element parent, int line)
		{
			super(name, UML.Type.METHOD, mod, parent, line);

			this.returnType = returnType;
		}

		public final void addArgument(String type, String name)
		{
			if (argList == null) {
				argList = new Vector();
			}
			// for JDK 1.2 can use argList.add(Object)
			argList.addElement( new Argument(type, name) );
		}

		public boolean isConstructor() { return isConstructor; }

		public void setConstructor(boolean isConstructor)
		{
			this.isConstructor = isConstructor;
		}

	 	public final boolean isAbstract()
	 	{
	 		return ( RWModifier.isAbstract(mod)
	 				|| (getParentElementType() == UML.Type.INTERFACE) );
		}

	 	public final boolean isBodyRequired()
	 	{
	 		return (!RWModifier.isNative(mod) && !isAbstract() );
		}


		//---------------------------------------------------------------------
		public boolean isVisible(Options.FilterIro filterOpt)
		{
			boolean rVal = false;

			if ( RWModifier.getMemberVisIndex(mod)
					>= filterOpt.getMemberVisIndex() ) {

				rVal = true;
			}

			return rVal;

		} // isVisible(Options.FilterIro): boolean


		//---------------------------------------------------------------------
		public final String listArgs(Options.DisplayIro displayOpt)
		{
			String rVal = "";

			if (argList != null && displayOpt.getShowArguments()) {
				for (int i = 0; i < argList.size(); i++) {
					if (i > 0) {
						// for JDK 1.2 can use argList.get(int)
						rVal += ", " + ( (Argument) argList.elementAt(i)).toString(displayOpt);
					} else {
						// for JDK 1.2 can use argList.get(int)
						rVal += ( (Argument) argList.elementAt(i)).toString(displayOpt);
					}
				} // for
			}
			return rVal;

		} // listArgs(Options.DisplayIro): String


		//---------------------------------------------------------------------
		public String toString(Options.DisplayIro displayOpt)
		{
			if ( displayOpt.getTypeIsSuffixed() ) {
				return ( (displayOpt.getShowLineNum() ) ? (line + 1 + ":") : "" )
						+ RWModifier.toString(mod, displayOpt)
						+ name + "(" + listArgs(displayOpt) + ")" +
						( (isConstructor) ? ": <init>" : " : " + returnType );
			} else {
				return ( (displayOpt.getShowLineNum() ) ? (line + 1 + ":") : "" )
						+ RWModifier.toString(mod, displayOpt) +
						( (isConstructor) ? "/*constructor*/ " :  (returnType + " ") )
						+ name + "(" + listArgs(displayOpt) + ")";

			}
		} // toString(Options.DisplayIro): String


		//=====================================================================
		static class Argument
		{
			private String type;
			private String name;

			public Argument(String type, String name)
			{
				this.type = type;
				this.name = name;
			}

			public final String toString(Options.DisplayIro displayOpt)
			{
				if (displayOpt.getTypeIsSuffixed() ) {
					return ( displayOpt.getShowArgumentNames() ? (name + " : ") : " : " )
							+ type;
				} else {
					return type +
							( displayOpt.getShowArgumentNames() ? (" " + name) : "" );
				}
			} // toString(Options.DisplayIro): String

			public final String getType() { return type; }

		} // class UML.Operation.Argument

	} // class UML.Operation extends UML.Element


	//=========================================================================
	static class Throws extends Element
	{
		// A top-level package member (i.e. class/interface that is a
		// direct member of a package)

		Throws(String name, Element parent, int line)
		{
			// (String name, UML.Type type, int mod, Element parent, int line)
			super(name, UML.Type.THROWS, 0, parent, line);
		}

		public String toString(Options.DisplayIro displayOpt)
		{
			return ( (displayOpt.getShowLineNum()) ? ( (line + 1) + ":") : "" )
					+ ( (displayOpt.getShowIconKeywords()) ? (type.toString() + " ") : "" )
					+ name;

		} // toString(Options.DisplayIro): String


		//---------------------------------------------------------------------
		public boolean isVisible(Options.FilterIro filterOpt)
		{
			boolean rVal = false;

			if ( filterOpt.getShowThrows() ) rVal = true;

			return rVal;

		} // isVisible(Options.FilterIro): boolean

	} // class UML.Throws extends UML.Element


	//=========================================================================
	static class Attribute extends Element
	{
		private String type;

		static final String PRIMITIVE_TYPES =
				":boolean:char:byte:short:int:long:float:double:";


		Attribute(String name, String type, int mod, Element parent, int line)
		{
			super(name, UML.Type.ATTRIBUTE, mod, parent, line);

			this.type = type;
		}

		public final boolean isAbstract() { return ( false ); }


		public final boolean isPrimitive()
		{
			if ( ( name.indexOf("[") == -1 )
					&& ( PRIMITIVE_TYPES.indexOf(":" + type + ":") != -1) )
				return true;
			else
				return false;
		}

		//---------------------------------------------------------------------
		public boolean isVisible(Options.FilterIro filterOpt)
		{
			boolean rVal = false;

			if ( RWModifier.getMemberVisIndex(mod)
					>= filterOpt.getMemberVisIndex() ) {

				if (filterOpt.getShowAttributes()) {

					if ( filterOpt.getShowPrimitives() || !isPrimitive() )
						rVal = true;
				}
			}

			return rVal;

		} // isVisible(Options.FilterIro): boolean


		public String toString()
		{
			return ( "" + (line + 1) + ":" + name + " : " + type );
		} // toString(): String

		public String toString(Options.DisplayIro displayOpt)
		{
			return ( displayOpt.getShowLineNum() ? ( (line + 1) + ":") : "" )
					+ RWModifier.toString(mod, displayOpt)
					+ ( displayOpt.getTypeIsSuffixed()
						? (name + " : " + type) : (type + " " + name) );

		} // toString(Options.DisplayIro): String

	} // class UML.Attribute extends UML.Element

} // class UML



