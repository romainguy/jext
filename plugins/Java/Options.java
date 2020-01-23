/*
 * Options.java - Options for JBrowse
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

import javax.swing.event.*;

//=============================================================================
public class Options
{
	private boolean showStatusBar;

	private Filter filterOpt;   // (WHAT to display)
	private Display displayOpt;	// (HOW  to display)

	private ChangeListener listener;

	public Options()
	{
		filterOpt  = new Filter();
		displayOpt = new Display();

	} // Options(): <init>


	public ChangeListener getListener() { return listener; }


	//-------------------------------------------------------------------------
	/**
	 * The method that sets the option object's state to reflect the values
	 * specified by the passed PropertyAccessor.
	 */
	public void load(PropertyAccessor props)
	{
		// General Options
		setShowStatusBar(
				!"off".equals(props.getProperty("jbrowse.showStatusBar")));

		// Filter Options
		filterOpt.setShowAttributes(
				"on".equals(props.getProperty("jbrowse.showAttr")));
		filterOpt.setShowPrimitives(
				"on".equals(props.getProperty("jbrowse.showPrimAttr")));
		filterOpt.setShowGeneralizations(
				"on".equals(props.getProperty("jbrowse.showGeneralizations")));
		filterOpt.setShowThrows(
				"on".equals(props.getProperty("jbrowse.showThrows")));

		int topLevelVisIndex;
		try {
			topLevelVisIndex = Integer.parseInt(
					props.getProperty("jbrowse.topLevelVisIndex"));
		} catch(NumberFormatException nf) {
			topLevelVisIndex = RWModifier.TOPLEVEL_VIS_PACKAGE;
		}
		if (topLevelVisIndex < RWModifier.TOPLEVEL_VIS_PACKAGE
				|| topLevelVisIndex > RWModifier.TOPLEVEL_VIS_PUBLIC ) {
			topLevelVisIndex = RWModifier.TOPLEVEL_VIS_PACKAGE;
		}
		filterOpt.setTopLevelVisIndex( topLevelVisIndex );

		int memberVisIndex;
		try {
			memberVisIndex = Integer.parseInt(
					props.getProperty("jbrowse.memberVisIndex"));
		} catch(NumberFormatException nf) {
			memberVisIndex = RWModifier.MEMBER_VIS_PRIVATE;
		}
		if (memberVisIndex < RWModifier.MEMBER_VIS_PRIVATE
				|| memberVisIndex > RWModifier.MEMBER_VIS_PUBLIC ) {
			memberVisIndex = RWModifier.MEMBER_VIS_PRIVATE;
		}
		filterOpt.setMemberVisIndex( memberVisIndex );

		// Display Options
		displayOpt.setShowArguments(
				"on".equals(props.getProperty("jbrowse.showArgs")));
		displayOpt.setShowArgumentNames(
				"on".equals(props.getProperty("jbrowse.showArgNames")));
		displayOpt.setShowNestedName(
				"on".equals(props.getProperty("jbrowse.showNestedName")));
		displayOpt.setShowIconKeywords(
				"on".equals(props.getProperty("jbrowse.showIconKeywords")));
		displayOpt.setShowMiscMod(
				"on".equals(props.getProperty("jbrowse.showMiscMod")));
		displayOpt.setAlphaSort(
				"on".equals(props.getProperty("jbrowse.alphaSortMethods")));
		displayOpt.setShowLineNum(
				"on".equals(props.getProperty("jbrowse.showLineNums")));

		int styleIndex;
		try
		{
			styleIndex = Integer.parseInt(
					props.getProperty("jbrowse.displayStyle"));
		}
		catch(NumberFormatException nf)
		{
			styleIndex = Options.Display.STYLE_UML;
		}
		if (styleIndex < Options.Display.STYLE_FIRST
				|| styleIndex > Options.Display.STYLE_LAST ) {
			styleIndex = Options.Display.STYLE_UML;
		}
		displayOpt.setStyleIndex( styleIndex );

		displayOpt.setVisSymbols(
				"on".equals(props.getProperty("jbrowse.custVisAsSymbol")));
		displayOpt.setAbstractItalic(
				"on".equals(props.getProperty("jbrowse.custAbsAsItalic")));
		displayOpt.setStaticUlined(
				"on".equals(props.getProperty("jbrowse.custStaAsUlined")));
		displayOpt.setTypeIsSuffixed(
				"on".equals(props.getProperty("jbrowse.custTypeIsSuffixed")));

	} // load(PropertyAccessor props): void


	//-------------------------------------------------------------------------
	/**
	 * The method that sets the passed PropertyAccessor's state to reflect
	 * the current state of this Options object.
	 */
	public void save(PropertyAccessor props)
	{
		// General Options
		//----------------
		props.setProperty( "jbrowse.showStatusBar",
				getShowStatusBar() ? "on" : "off" );


		// Filter Options
		//---------------
		props.setProperty( "jbrowse.showAttr",
				filterOpt.getShowAttributes() ? "on" : "off" );
		props.setProperty( "jbrowse.showPrimAttr",
				filterOpt.getShowPrimitives() ? "on" : "off" );
		props.setProperty( "jbrowse.showGeneralizations",
				filterOpt.getShowGeneralizations() ? "on" : "off" );
		props.setProperty( "jbrowse.showThrows",
				filterOpt.getShowThrows() ? "on" : "off" );

		/* Visibility Level */
		props.setProperty( "jbrowse.topLevelVisIndex",
				String.valueOf(filterOpt.getTopLevelVisIndex()) );
		props.setProperty( "jbrowse.memberVisIndex",
				String.valueOf(filterOpt.getMemberVisIndex()) );


		// Display Options
		//----------------
		props.setProperty( "jbrowse.showArgs",
				displayOpt.getShowArguments() ? "on" : "off" );
		props.setProperty( "jbrowse.showArgNames",
				displayOpt.getShowArgumentNames() ? "on" : "off" );
		props.setProperty( "jbrowse.showNestedName",
				displayOpt.getShowNestedName() ? "on" : "off" );
		props.setProperty( "jbrowse.showIconKeywords",
				displayOpt.getShowIconKeywords() ? "on" : "off" );
		props.setProperty( "jbrowse.showMiscMod",
				displayOpt.getShowMiscMod() ? "on" : "off" );
		props.setProperty( "jbrowse.alphaSortMethods",
				displayOpt.getAlphaSort() ? "on" : "off" );
		props.setProperty( "jbrowse.showLineNums",
				displayOpt.getShowLineNum() ? "on" : "off" );

		/* Display Style */
		props.setProperty("jbrowse.displayStyle",
				String.valueOf(displayOpt.getStyleIndex()) );

		/* Custom Style Options */
		props.setProperty("jbrowse.custVisAsSymbol",
				displayOpt.getVisSymbols() ? "on" : "off" );
		props.setProperty("jbrowse.custAbsAsItalic",
				displayOpt.getAbstractItalic() ? "on" : "off" );
		props.setProperty("jbrowse.custStaAsUlined",
				displayOpt.getStaticUlined() ? "on" : "off" );
		props.setProperty("jbrowse.custTypeIsSuffixed",
				displayOpt.getTypeIsSuffixed() ? "on" : "off" );

	} // save(PropertyAccessor props): void


	// Accessor methods
	//-------------------------------------------------------------------------

	public final boolean getShowStatusBar()  { return showStatusBar; }

	public final void setShowStatusBar(boolean flag) {
		showStatusBar = flag;
	}

	public final Filter  getFilterOptions()  { return filterOpt; }
	public final Display getDisplayOptions() { return displayOpt; }


	//-------------------------------------------------------------------------
	/**
	 * This is the method that is called in order to associate the JBrowse
	 * session's ChangeListener with this Option object.
	 */
	public void addChangeListener(ChangeListener listener)
	{
		this.listener = listener; // there can only be one at this time
	}


	// Other Object methods
	//-------------------------------------------------------------------------

	public final String toString()
	{
		return  filterOpt.toString() + "\n"
				+ displayOpt.toString();
	}


	//=========================================================================
	public static class Display implements DisplayIro
	{
		// Display Style options (HOW)

		private boolean showArguments;
		private boolean showArgumentNames;
		private boolean showNestedName;
		private boolean showIconKeywords;
		private boolean showMiscMod;
		private boolean alphaSort;
		private boolean showLineNum;

		private int styleIndex = STYLE_UML;

		private boolean visSymbols;
		private boolean abstractItalic;
		private boolean staticUlined;
		private boolean typeIsSuffixed;


		// Accessor methods
		//-------------------------------------------------------------------------

		public final boolean getShowArguments()     { return showArguments; }
		public final boolean getShowArgumentNames() { return showArgumentNames; }
		public final boolean getShowNestedName()    { return showNestedName; }
		public final boolean getShowIconKeywords()  { return showIconKeywords; }
		public final boolean getShowMiscMod()       { return showMiscMod; }
		public final boolean getAlphaSort()         { return alphaSort; }
		public final boolean getShowLineNum()       { return showLineNum; }

		public final int getStyleIndex()            { return styleIndex; }

		public final boolean getVisSymbols()        { return visSymbols; }
		public final boolean getAbstractItalic()    { return abstractItalic; }
		public final boolean getStaticUlined()      { return staticUlined; }
		public final boolean getTypeIsSuffixed()    { return typeIsSuffixed; }

		public final void setShowArguments(boolean flag)
		{
			showArguments = flag;
		}

		public final void setShowArgumentNames(boolean flag)
		{
			showArgumentNames = flag;
		}

		public final void setShowNestedName(boolean flag)
		{
			showNestedName = flag;
		}

		public final void setShowIconKeywords(boolean flag)
		{
			showIconKeywords = flag;
		}

		public final void setShowMiscMod(boolean flag)
		{
			showMiscMod = flag;
		}

		public final void setAlphaSort(boolean flag)
		{
			alphaSort = flag;
		}

		public final void setShowLineNum(boolean flag)
		{
			showLineNum = flag;
		}

		public final void setStyleIndex(int index)
		{
			styleIndex = index;
		}

		public final void setVisSymbols(boolean flag)
		{
			visSymbols = flag;
		}

		public final void setAbstractItalic(boolean flag)
		{
			abstractItalic = flag;
		}

		public final void setStaticUlined(boolean flag)
		{
			staticUlined = flag;
		}

		public final void setTypeIsSuffixed(boolean flag)
		{
			typeIsSuffixed = flag;
		}

		public final DisplayIro getInverseOptions() {
			Display inverseOpt = new Display();

			inverseOpt.showArguments     = !showArguments;
			inverseOpt.showArgumentNames = !showArgumentNames;
			inverseOpt.showNestedName    = !showNestedName;
			inverseOpt.showIconKeywords  = !showIconKeywords;
			inverseOpt.showMiscMod       = !showMiscMod;
			inverseOpt.alphaSort         = !alphaSort;
			inverseOpt.showLineNum       = !showLineNum;

			inverseOpt.visSymbols      = !visSymbols;
			inverseOpt.abstractItalic  = !abstractItalic;
			inverseOpt.staticUlined    = !staticUlined;
			inverseOpt.typeIsSuffixed  = !typeIsSuffixed;

			if (styleIndex == STYLE_UML) {
				inverseOpt.styleIndex = STYLE_JAVA;
			} else if (styleIndex == STYLE_JAVA) {
				inverseOpt.styleIndex = STYLE_UML;
			}

			return inverseOpt;
		}

		public String toString()
		{
			return  "How to display:"
					+ "\n\tshowArguments     = " + showArguments
					+ "\n\tshowArgumentNames = " + showArgumentNames
					+ "\n\tshowNestedName    = " + showNestedName
					+ "\n\tshowIconKeywords  = " + showIconKeywords
					+ "\n\tshowMiscMod       = " + showMiscMod
					+ "\n\talphaSort         = " + alphaSort
					+ "\n\tshowLineNum       = " + showLineNum

					+ "\n\tstyleIndex        = " + styleIndex

					+ "\n\tvisSymbols        = " + visSymbols
					+ "\n\tabstractItalic    = " + abstractItalic
					+ "\n\tstaticUlined      = " + staticUlined
					+ "\n\ttypeIsSuffixed    = " + typeIsSuffixed;

		} // toString(): String

	} // class Options.Display implements Options.DisplayIro


	//=========================================================================
	/**
	 * Options.DisplayIro - Interface for Accessing Display options for JBrowse
	 */
	public static interface DisplayIro
	{
		// Display Style options (HOW)

		// constants - for styleIndex
		static final int STYLE_FIRST  = 0;
		static final int STYLE_UML    = 0;
		static final int STYLE_JAVA   = 1;
		static final int STYLE_CUSTOM = 2;
		static final int STYLE_LAST   = 2;


		// Accessor methods
		//-------------------------------------------------------------------------

		public boolean getShowArguments();
		public boolean getShowArgumentNames();
		public boolean getShowNestedName();
		public boolean getShowIconKeywords();
		public boolean getShowMiscMod();
		public boolean getAlphaSort();
		public boolean getShowLineNum();

		public int getStyleIndex();

		public boolean getVisSymbols();
		public boolean getAbstractItalic();
		public boolean getStaticUlined();
		public boolean getTypeIsSuffixed();

		public DisplayIro getInverseOptions();

	} // interface Options.DisplayIro


	//=========================================================================
	public static class Filter implements FilterIro
	{
		// Filter options (WHAT)

		private boolean showAttributes;
		private boolean showPrimitives;
		private boolean showGeneralizations;
		private boolean showThrows;

		private int topLevelVisIndex = 0;
		private int memberVisIndex   = 0;

		// Accessor methods
		//-------------------------------------------------------------------------
		public final boolean getShowAttributes()      { return showAttributes; }
		public final boolean getShowPrimitives()      { return showPrimitives; }
		public final boolean getShowGeneralizations() { return showGeneralizations; }
		public final boolean getShowThrows()          { return showThrows; }

		public final int getTopLevelVisIndex()   { return topLevelVisIndex; }
		public final int getMemberVisIndex()     { return memberVisIndex; }

		public final void setShowAttributes(boolean flag)
		{
			showAttributes = flag;
		}

		public final void setShowPrimitives(boolean flag)
		{
			showPrimitives = flag;
		}

		public final void setShowGeneralizations(boolean flag)
		{
			showGeneralizations = flag;
		}

		public final void setShowThrows(boolean flag)
		{
			showThrows = flag;
		}

		public final void setTopLevelVisIndex(int level)
		{
			topLevelVisIndex = level;
		}

		public final void setMemberVisIndex(int level)
		{
			memberVisIndex = level;
		}

		public String toString()
		{
			return    "What to include:"
					+ "\n\tshowAttributes      = " + showAttributes
					+ "\n\tshowPrimitives      = " + showPrimitives
					+ "\n\tshowGeneralizations = " + showGeneralizations
					+ "\n\tshowThrows          = " + showThrows

					+ "\n\ttopLevelVisIndex    = " + topLevelVisIndex
					+ "\n\tmemberVisIndex      = " + memberVisIndex;

		} // toString(): String

	} // class Options.Filter implements Options.FilterIro {


	//=========================================================================
	/**
	 *  FilterIro - Interface for Accessing Filter options for JBrowse
	 */
	public static interface FilterIro
	{
		// Filter options (WHAT)

		// Accessor methods
		//---------------------------------------------------------------------
		public boolean getShowAttributes();
		public boolean getShowPrimitives();
		public boolean getShowGeneralizations();
		public boolean getShowThrows();

		public int getTopLevelVisIndex();
		public int getMemberVisIndex();

	} // interface Options.FilterIro

} // public class Options
