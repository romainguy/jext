/*
 * JBrowseOptionPane.java - JBrowse options panel
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

import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.event.*;

import org.jext.gui.*;

//=============================================================================
public class JBrowseOptionPane
		extends AbstractOptionPane implements ActionListener
{
	// protected members

	/**
	 * The layout manager.
	 */
	//protected GridBagLayout gridBag;//it is inherited now!

	/**
	 * The number of components already added to the layout manager.
	 */
	//protected int y;//it is inherited now!

	// private state
	boolean isInitGui;
	boolean isInitModel;

	// private gui components

	// general options
	private JextCheckBox cbxStatusBar;
//	private JextCheckBox cbxUseFrame;

	// filter options
	private JextCheckBox cbxShowAttributes;
	private JextCheckBox cbxShowPrimitives;
	private JextCheckBox cbxShowGeneralizations;
	private JextCheckBox cbxShowThrows;

	private JComboBox cmbTopLevelVis;
	private JComboBox cmbMemberVis;
	private int topLevelVisIndex;
	private int memberVisIndex;

	// display options
	private JextCheckBox cbxShowArguments;
	private JextCheckBox cbxShowArgumentNames;
	private JextCheckBox cbxShowNestedName;
	private JextCheckBox cbxShowIconKeywords;
	private JextCheckBox cbxShowMiscMod;
	private JextCheckBox cbxAlphaSort;
	private JextCheckBox cbxShowLineNum;

	private JComboBox cmbStyle;
	private int styleIndex = Options.Display.STYLE_UML;

	private JextCheckBox cbxVisSymbols;
	private JextCheckBox cbxAbstractItalic;
	private JextCheckBox cbxStaticUlined;
	private JextCheckBox cbxTypeIsSuffixed;

	// Options object
	private Options options = new Options();
	private Options.Filter  filterOpt  = options.getFilterOptions();
	private Options.Display displayOpt = options.getDisplayOptions();

	// Property Accessor
	private PropertyAccessor props;

	private boolean batchUpdate = false;

	//-------------------------------------------------------------------------
	public JBrowseOptionPane()
	{
		/*super();
		setName("jbrowse");*/
    super("jbrowse");
		setLayout(gridBag = new GridBagLayout());

		props = new JBrowsePlugin.PropAccessor();
		options.load(props);

		initGui();	// setup display from property values
		initModel();  // set GUI to model (as defined in Options object)

	} // public JBrowseOptionPane(): <init>


	//-------------------------------------------------------------------------
	public JBrowseOptionPane(String title)
	{
		super("jbrowse");
		setName(title);
		setLayout(gridBag = new GridBagLayout());

		// It is the instantiating code's responsibility to call:
		// initGui(), initModel(), and setOptions() before displaying

	} // public JBrowseOptionPane(String): <init>


	//-------------------------------------------------------------------------
	PropertyAccessor getPropertyAccessor() { return props; }


	//-------------------------------------------------------------------------
	void setPropertyAccessor(PropertyAccessor props) {
		this.props = props;
	}

	public boolean isInitGui() { return isInitGui; }
	public boolean isInitModel() { return isInitModel; }


	//-------------------------------------------------------------------------
	/**
	 * Update options object to reflect the action by triggering the
	 * appropriate ChangeEvent.
	 */
	public void actionPerformed(ActionEvent evt)
	{
		Object source = evt.getSource();
		Object newSource;

		// General  Options
		if (source == cbxStatusBar) {
			newSource = options;
//		} else if (source == cbxUseFrame) {
//			newSource = options;

		// Filter Options

		} else if (source == cbxShowAttributes) {
			newSource = filterOpt;

			if(cbxShowAttributes.getModel().isSelected()) {
				cbxShowPrimitives.getModel().setEnabled(true);
			} else {
				cbxShowPrimitives.getModel().setSelected(false);
				cbxShowPrimitives.getModel().setEnabled(false);
			}

		} else if (source == cbxShowPrimitives) {
			newSource = filterOpt;

		} else if (source == cbxShowGeneralizations) {
			newSource = filterOpt;

		} else if (source == cbxShowThrows) {
			newSource = filterOpt;

		} else if (source == cmbTopLevelVis) {
			newSource = filterOpt;
			topLevelVisIndex = cmbTopLevelVis.getSelectedIndex();

		} else if (source == cmbMemberVis) {
			newSource = filterOpt;
			memberVisIndex = cmbMemberVis.getSelectedIndex();

		// Display Style Options
		} else if (source == cmbStyle) {
			newSource = displayOpt;
			styleIndex = cmbStyle.getSelectedIndex();

			refreshDisplayOptions(styleIndex);

		} else if (source == cbxShowArguments) {
			newSource = displayOpt;

			if(cbxShowArguments.getModel().isSelected()) {
				cbxShowArgumentNames.getModel().setEnabled(true);
			} else {
				cbxShowArgumentNames.getModel().setSelected(false);
				cbxShowArgumentNames.getModel().setEnabled(false);
			}

		} else {
			// All other (i.e. display) options
			newSource = displayOpt;
		}


		if (!batchUpdate) {

			// Update Option object to reflect new GUI state.
			setOptions();

			// Forward event, if there is a listener			
			ChangeListener cl = options.getListener();
			if (cl != null) {
				//getParent().setCursor(new Cursor(Cursor.WAIT_CURSOR));
				ChangeEvent event = new ChangeEvent(newSource);
				cl.stateChanged(event);
				//getParent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}

	} // actionPerformed(ActionEvent): void


	//-------------------------------------------------------------------------
	/**
	 * Sets this OptionPane's options object to the state specified by the 
	 * the OptionPane's associated PropertyAccessor.
	 */
	public void load()
	{
		batchUpdate = true;
		options.load(props);
		batchUpdate = false;

	} // load(): void


	//-------------------------------------------------------------------------
	/**
	 * Setup the GUI (with no current state).
	 * This should only be called once in the constructor for this
	 * JBrowseOptionPane.
	 */
	void initGui()
	{
		//------------
		// Tile, the Option Panel Label
		//------------
		JLabel titleLabel = new JLabel(
				props.getProperty("options." + getName() + ".panel_label") + ":",
						JLabel.LEFT );
		titleLabel.setFont(new Font("Helvetica", Font.BOLD + Font.ITALIC, 13));

		addComponent(titleLabel);


		//---------------
		// General Options
		//---------------
//		OptionPanel generalPanel = new OptionPanel();
//		TitledBorder generalBorder = new TitledBorder(new BevelBorder(BevelBorder.LOWERED),
//				" " + props.getProperty("options.jbrowse.generalOptions") + " ",
//		TitledBorder.CENTER, TitledBorder.TOP);
//		generalPanel.setBorder(new CompoundBorder(generalBorder, new EmptyBorder(0, 3, 0, 1)));

		JPanel generalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 0));
		cbxStatusBar = new JextCheckBox(
				props.getProperty("options.jbrowse.showStatusBar"));
		generalPanel.add(cbxStatusBar);
		cbxStatusBar.addActionListener(this);

//		if ( "jbrowse".equals(getName()) ) {
//			cbxUseFrame  = new JextCheckBox(
//				props.getProperty("options.jbrowse.useFrame"));
//			generalPanel.add(cbxUseFrame);
//			cbxUseFrame.addActionListener(this);
//		}

		addComponent(generalPanel);

		//---------------
		// Filter Options
		//---------------
		OptionPanel filterPanel = new OptionPanel();
		TitledBorder filterBorder = new TitledBorder(new BevelBorder(BevelBorder.LOWERED),
				" " + props.getProperty("options.jbrowse.filterOptions") + " ",
		TitledBorder.CENTER, TitledBorder.TOP);
		filterPanel.setBorder(new CompoundBorder(filterBorder, new EmptyBorder(0, 3, 1, 1)));


		/* Attributes */
		JPanel attrPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		cbxShowAttributes = new JextCheckBox(
				props.getProperty("options.jbrowse.showAttr") + " ");
		attrPanel.add(cbxShowAttributes);

		/* Primitive Attributes */
		cbxShowPrimitives = new JextCheckBox(
				props.getProperty("options.jbrowse.showPrimAttr"));
		attrPanel.add(cbxShowPrimitives);
		filterPanel.addComponent(attrPanel);

		cbxShowAttributes.addActionListener(this);
		cbxShowPrimitives.addActionListener(this);

		/* Generalizations */
		cbxShowGeneralizations = new JextCheckBox(
				props.getProperty("options.jbrowse.showGeneralizations") + " ");
		filterPanel.addComponent(cbxShowGeneralizations);
		cbxShowGeneralizations.addActionListener(this);

		/* Throws */
		cbxShowThrows = new JextCheckBox(
				props.getProperty("options.jbrowse.showThrows") + " ");
		filterPanel.addComponent(cbxShowThrows);
		cbxShowThrows.addActionListener(this);

		/* Visibility Level */
		JLabel visLevelLabel = new JLabel(
				props.getProperty("options.jbrowse.visLevelLabel") );
		filterPanel.addComponent(visLevelLabel);

		/* Top-Level Visibility Options */
		String[] topLevelVisNames = { "package", "public" };
		cmbTopLevelVis = new JComboBox(topLevelVisNames);
    cmbTopLevelVis.setRenderer(new ModifiedCellRenderer());
		filterPanel.addComponent(props.getProperty("options.jbrowse.topLevelVis"),
			cmbTopLevelVis);
		cmbTopLevelVis.addActionListener(this);


		/* Member-Level Visibility Options */
		String[] memberVisNames = { "private", "package", "protected", "public" };
		cmbMemberVis = new JComboBox(memberVisNames);
    cmbMemberVis.setRenderer(new ModifiedCellRenderer());
		filterPanel.addComponent(props.getProperty("options.jbrowse.memberVis"),
			cmbMemberVis);
		cmbMemberVis.addActionListener(this);

		addComponent(filterPanel);


		//----------------
		// Display Options
		//----------------
		OptionPanel displayPanel = new OptionPanel();
		TitledBorder displayBorder = new TitledBorder(new BevelBorder(BevelBorder.LOWERED),
				" " + props.getProperty("options.jbrowse.displayOptions") + " ",
		TitledBorder.CENTER, TitledBorder.TOP);
		displayPanel.setBorder(new CompoundBorder(displayBorder, new EmptyBorder(0, 3, 0, 1)));

		/* Arguments */
		JPanel argPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

		cbxShowArguments = new JextCheckBox(
				props.getProperty("options.jbrowse.showArgs") + " ");
		argPanel.add(cbxShowArguments);
		cbxShowArguments.addActionListener(this);

		/* Argument Names */
		cbxShowArgumentNames = new JextCheckBox(
				props.getProperty("options.jbrowse.showArgNames"));
		argPanel.add(cbxShowArgumentNames);
		displayPanel.addComponent(argPanel);
		cbxShowArgumentNames.addActionListener(this);

		/* qualify nested class/interface names */
		cbxShowNestedName = new JextCheckBox(
				props.getProperty("options.jbrowse.showNestedName"));
		displayPanel.addComponent(cbxShowNestedName);
		cbxShowNestedName.addActionListener(this);

		/* class/interface modifiers */
		cbxShowIconKeywords = new JextCheckBox(
				props.getProperty("options.jbrowse.showIconKeywords"));
		displayPanel.addComponent(cbxShowIconKeywords);
		cbxShowIconKeywords.addActionListener(this);

		/* misc. detail modifiers */
		cbxShowMiscMod = new JextCheckBox(
				props.getProperty("options.jbrowse.showMiscMod"));
		displayPanel.addComponent(cbxShowMiscMod);
		cbxShowMiscMod.addActionListener(this);

		/* Alpha Sort Methods */
		cbxAlphaSort = new JextCheckBox(
				props.getProperty("options.jbrowse.alphaSort"));
		displayPanel.addComponent(cbxAlphaSort);
		cbxAlphaSort.addActionListener(this);

		/* Line Numbers */
		cbxShowLineNum = new JextCheckBox(
				props.getProperty("options.jbrowse.showLineNums"));
		displayPanel.addComponent(cbxShowLineNum);
		cbxShowLineNum.addActionListener(this);

		/* Display Style */
		String[] styleNames = {
			props.getProperty("options.jbrowse.umlStyle"),
			props.getProperty("options.jbrowse.javaStyle"),
			props.getProperty("options.jbrowse.customStyle") };
		cmbStyle = new JComboBox(styleNames);
    cmbStyle.setRenderer(new ModifiedCellRenderer());
		cmbStyle.addActionListener(this);
		displayPanel.addComponent(props.getProperty("options.jbrowse.displayStyle"),
			cmbStyle);


		/* Custom Display Style Options */
 		JLabel customOptionsLabel = new JLabel(
 				props.getProperty("options.jbrowse.customOptions"));
 		displayPanel.addComponent(customOptionsLabel);

		cbxVisSymbols = new JextCheckBox(
				props.getProperty("options.jbrowse.custVisAsSymbol"));

		cbxAbstractItalic = new JextCheckBox(
				props.getProperty("options.jbrowse.custAbsAsItalic"));

		cbxStaticUlined = new JextCheckBox(
				props.getProperty("options.jbrowse.custStaAsUlined"));

		cbxTypeIsSuffixed = new JextCheckBox(
				props.getProperty("options.jbrowse.custTypeIsSuffixed"));


		cbxVisSymbols.addActionListener(this);
		cbxAbstractItalic.addActionListener(this);
		cbxStaticUlined.addActionListener(this);
		cbxTypeIsSuffixed.addActionListener(this);

		displayPanel.addComponent(cbxVisSymbols);
		displayPanel.addComponent(cbxAbstractItalic);
		displayPanel.addComponent(cbxStaticUlined);
		displayPanel.addComponent(cbxTypeIsSuffixed);

		addComponent(displayPanel);

		isInitGui = true;

	} // initGui(): void


	//-------------------------------------------------------------------------
	/**
	 * This method sets the GUI representation of the model to the state 
	 * specified  by the current option object's state.
	 */
	public void initModel()
	{
		batchUpdate = true;

		// General Options
		cbxStatusBar.getModel().setSelected( options.getShowStatusBar() );
//		cbxUseFrame.getModel().setSelected( options.getUseFrame() );

		// Filter Options
		cbxShowAttributes.getModel().setSelected( filterOpt.getShowAttributes() );
		cbxShowPrimitives.getModel().setSelected( filterOpt.getShowPrimitives() );
		cbxShowGeneralizations.getModel().setSelected( filterOpt.getShowGeneralizations() );
		cbxShowThrows.getModel().setSelected( filterOpt.getShowThrows() );

		cmbTopLevelVis.setSelectedIndex( filterOpt.getTopLevelVisIndex() );
		cmbMemberVis.setSelectedIndex(   filterOpt.getMemberVisIndex() );

		// Display Options
		cbxShowArguments.getModel().setSelected(     displayOpt.getShowArguments() );
		cbxShowArgumentNames.getModel().setSelected( displayOpt.getShowArgumentNames() );
		cbxShowNestedName.getModel().setSelected(    displayOpt.getShowNestedName() );
		cbxShowIconKeywords.getModel().setSelected(  displayOpt.getShowIconKeywords() );
		cbxShowMiscMod.getModel().setSelected(       displayOpt.getShowMiscMod() );
		cbxAlphaSort.getModel().setSelected(         displayOpt.getAlphaSort() );
		cbxShowLineNum.getModel().setSelected(       displayOpt.getShowLineNum() );

		cmbStyle.setSelectedIndex(displayOpt.getStyleIndex() );

		cbxVisSymbols.getModel().setSelected(     displayOpt.getVisSymbols() );
		cbxAbstractItalic.getModel().setSelected( displayOpt.getAbstractItalic() );
		cbxStaticUlined.getModel().setSelected(   displayOpt.getStaticUlined() );
		cbxTypeIsSuffixed.getModel().setSelected( displayOpt.getTypeIsSuffixed() );


		// Set enabled/disabled on showArgumentNames, showPrimitives checkboxes
		if(cbxShowArguments.getModel().isSelected()) {
			cbxShowArgumentNames.getModel().setEnabled(true);
		} else {
			cbxShowArgumentNames.getModel().setSelected(false);
			cbxShowArgumentNames.getModel().setEnabled(false);
		}

		if(cbxShowAttributes.getModel().isSelected()) {
			cbxShowPrimitives.getModel().setEnabled(true);
		} else {
			cbxShowPrimitives.getModel().setSelected(false);
			cbxShowPrimitives.getModel().setEnabled(false);
		}

		refreshDisplayOptions(styleIndex);

		isInitModel = true;
		batchUpdate = false;

	} // initModel(): void


	//-------------------------------------------------------------------------
	/**
	 * The method called by the File->Plugin Options save button for
	 * setting the JBrowse plugin options for all future sessions.
	 * It saves the current view state to the associated property values.
	 */
	public void save()
	{
		options.save(props);

	} // save(): void


	//-------------------------------------------------------------------------
	/**
	 * Set the enabled and selected/index state of all the display options
	 * that are dependant on the cmbStyle control. The state to be set to
	 * is determined by the passed styleIndex value. This method is called
	 * on init() and upon each change to the sytleIndex via its associated
	 * cmbStyle JComboBox.
	 */
	private void refreshDisplayOptions(int styleIndex)
	{
		if (styleIndex == Options.Display.STYLE_UML) {
			// UML
			cbxVisSymbols.getModel().setSelected(true);
			cbxAbstractItalic.getModel().setSelected(true);
			cbxStaticUlined.getModel().setSelected(true);
			cbxTypeIsSuffixed.getModel().setSelected(true);

			cbxVisSymbols.getModel().setEnabled(false);
			cbxAbstractItalic.getModel().setEnabled(false);
			cbxStaticUlined.getModel().setEnabled(false);
			cbxTypeIsSuffixed.getModel().setEnabled(false);

		} else if (styleIndex == Options.Display.STYLE_JAVA) {
			// Java
			cbxVisSymbols.getModel().setSelected(false);
			cbxAbstractItalic.getModel().setSelected(false);
			cbxStaticUlined.getModel().setSelected(false);
			cbxTypeIsSuffixed.getModel().setSelected(false);

			cbxVisSymbols.getModel().setEnabled(false);
			cbxAbstractItalic.getModel().setEnabled(false);
			cbxStaticUlined.getModel().setEnabled(false);
			cbxTypeIsSuffixed.getModel().setEnabled(false);

		} else if (styleIndex == Options.Display.STYLE_CUSTOM) {
			// Custom
			cbxVisSymbols.getModel().setEnabled(true);
			cbxAbstractItalic.getModel().setEnabled(true);
			cbxStaticUlined.getModel().setEnabled(true);
			cbxTypeIsSuffixed.getModel().setEnabled(true);

		} else {
			// error, unknown style index
		}

	} // refreshDisplayOptions(int index): void


	public Options getOptions() { return options; }


	//-------------------------------------------------------------------------
	/**
	 * The method that sets the option object's state to reflect the values
	 * specified by the current state of the JBrowseOptionPane.
	 */
	public void setOptions()
	{
		// General Options
		options.setShowStatusBar( cbxStatusBar.getModel().isSelected() );
//		options.setUseFrame( cbxUseFrame.getModel().isSelected() );

		// Filter Options
		filterOpt.setShowAttributes( cbxShowAttributes.getModel().isSelected() );
		filterOpt.setShowPrimitives( cbxShowPrimitives.getModel().isSelected() );
		filterOpt.setShowGeneralizations( cbxShowGeneralizations.getModel().isSelected() );
		filterOpt.setShowThrows( cbxShowThrows.getModel().isSelected() );

		filterOpt.setTopLevelVisIndex( topLevelVisIndex );
		filterOpt.setMemberVisIndex( memberVisIndex );

		// Display Options
		displayOpt.setShowArguments( cbxShowArguments.getModel().isSelected() );
		displayOpt.setShowArgumentNames( cbxShowArgumentNames.getModel().isSelected() );
		displayOpt.setShowNestedName( cbxShowNestedName.getModel().isSelected() );
		displayOpt.setShowIconKeywords( cbxShowIconKeywords.getModel().isSelected() );
		displayOpt.setShowMiscMod( cbxShowMiscMod.getModel().isSelected() );
		displayOpt.setAlphaSort( cbxAlphaSort.getModel().isSelected() );
		displayOpt.setShowLineNum( cbxShowLineNum.getModel().isSelected() );

		displayOpt.setStyleIndex( styleIndex );

		displayOpt.setVisSymbols( cbxVisSymbols.getModel().isSelected() );
		displayOpt.setAbstractItalic( cbxAbstractItalic.getModel().isSelected() );
		displayOpt.setStaticUlined( cbxStaticUlined.getModel().isSelected() );
		displayOpt.setTypeIsSuffixed( cbxTypeIsSuffixed.getModel().isSelected() );

	} // setOptions(): void


	//-------------------------------------------------------------------------
	protected void addComponent(Component comp)
	{
		GridBagConstraints cons = new GridBagConstraints();
		cons.gridy = y++;
		cons.gridheight = 1;
		cons.gridwidth = cons.REMAINDER;
		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.anchor = GridBagConstraints.CENTER;
		cons.weightx = 1.0f;

		gridBag.setConstraints(comp,cons);
		add(comp);

	} // addComponent(Component): void


	//=========================================================================
	/**
	 * This class is used to for panels that require a gridBag layout for
	 * placement into (for example) an OptionPane.
	 */
	static class OptionPanel extends JPanel
	{
		/**
		 * Creates a new option pane.
		 * @param name The internal name
		 */
		public OptionPanel()
		{
			setLayout(gridBag = new GridBagLayout());
		}

		// protected members

		/**
		 * The layout manager.
		 */
		protected GridBagLayout gridBag;

		/**
		 * The number of components already added to the layout manager.
		 */
		protected int y;

		/**
		 * Adds a labeled component to the option pane.
		 * @param label The label
		 * @param comp The component
		 */
		protected void addComponent(String label, Component comp)
		{
			GridBagConstraints cons = new GridBagConstraints();
			cons.gridy = y++;
			cons.gridheight = 1;
			cons.gridwidth = 3;
			cons.fill = GridBagConstraints.BOTH;
			cons.weightx = 1.0f;

			cons.gridx = 0;
			JLabel l = new JLabel(label,SwingConstants.RIGHT);
			gridBag.setConstraints(l,cons);
			add(l);

			cons.gridx = 3;
			cons.gridwidth = 1;
			gridBag.setConstraints(comp,cons);
			add(comp);
		}

		/**
		 * Adds a component to the option pane.
		 * @param comp The component
		 */
		protected void addComponent(Component comp)
		{
			GridBagConstraints cons = new GridBagConstraints();
			cons.gridy = y++;
			cons.gridheight = 1;
			cons.gridwidth = cons.REMAINDER;
			cons.fill = GridBagConstraints.HORIZONTAL;
			cons.anchor = GridBagConstraints.WEST;
			cons.weightx = 1.0f;

			gridBag.setConstraints(comp,cons);
			add(comp);
		}
	} // static class OptionPanel extends JPanel

} // public class JBrowseOptionPane extends JPanel
