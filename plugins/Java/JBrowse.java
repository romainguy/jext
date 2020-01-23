/*
 * JBrowse.java - JBrowse GUI and Engine, v1.0.1
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

import java.awt.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;  // required for KeyListener and ActionListener

import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.border.*;

import org.jext.event.*;
import org.jext.gui.*;

//=============================================================================
/**
 * The class that defines the non-modal dialog window that provides the gui
 * for the JBrowse plugin.
 * @author George Latkiewicz
 * @version 1.0.1 - Nov. 16, 1999
 */
public class JBrowse extends JPanel // JFrame
		implements ActionListener, JextListener
{

	/* public class attributes */
	public static final String VER_NUM = "1.0.1";

	/* public instance attributes */
	public JBrowseParser.Results results;

	/* private instance attributes */
	private Frame frame; // passed by activator
	private PropertyAccessor props; // passed by activator
	private JBrowseParser parser; // passed by activator
	private UMLTree umlTree; // passed by activator

	private String fileName; // set on JBrowse.TreePane.init(),
	private UMLTree.Model treeModel; // set on JBrowse.TreePane.init()
	private UMLTree.Node root;

	private JButton parseBtn;
	private JButton resizeBtn;
	private JButton configBtn;

	// status bar
	JPanel statusPanel;
	JLabel classLabel, interfaceLabel, attributeLabel, methodLabel, errorLabel;

	JPanel topPanel;
	private TreePane treePane;

	private OptionDialog optionDialog;
	private Options options;
	private Options.Filter  filterOpt;
	private Options.Display displayOpt;

	private ChangeListener optionListener;

	private boolean hasJavaFileExtension = false;

	JScrollPane scpTreePane;


	//-------------------------------------------------------------------------
	/**
	 * This method creates a new instance of JBrowse GUI and Parsing engine.
	 * @param
	 */
	public JBrowse(JBrowse.Activator activator)
	{
		// third option of false (i.e. non-modal) is the default for JDialog constructor
//		super(activator.getOwner(), "JBrowse", false);
//		super("JBrowse"); // for a JFrame


		frame = activator.getOwner(); // i.e. the associated editor view, if present
		props = activator.getPropertyAccessor();
		parser = activator.getJBrowseParser();
		umlTree = activator.getUMLTree();

		if (props == null ) {
			return;
		}
		if (parser == null ) {
			return;
		}
		if (umlTree == null ) {
			return;
		}

//		if (frame != null) {
//			Image pluginIcon = org.gjt.sp.jedit.GUIUtilities.getPluginIcon();
//			if (pluginIcon != null) {
//				setIconImage( pluginIcon );
//			}
//		}

//		URL url = getClass().getResource("StructIcon.gif");
//		if (url != null) {
//			setIconImage( (new ImageIcon(url)).getImage() );
//		}

		treePane   = new TreePane("jbrowse_tree");

		// Add tree pane to center of content pane
		Container contentPane = this;
		contentPane.setLayout(new BorderLayout());
		contentPane.add(BorderLayout.CENTER, treePane);

		// Connect to the optionPane's options
		optionDialog = new OptionDialog(frame, this, "JBrowse - Configure Options");
		options = optionDialog.getOptions();
		filterOpt  = options.getFilterOptions();
		displayOpt = options.getDisplayOptions();
		treePane.init();

		// Configure Parser
		parser.setOptions(options);
		parser.setRootNode(root);

		// create the ChangeListener for this JBrowse session, this
		// is the means by which this session is notified of any changes
		// to the current option values.
		optionListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				Object src = e.getSource();

				if (src == options) {
					// Display or hide the status bar
					if (options.getShowStatusBar() ) {
						statusPanel.setVisible(true);
					} else {
						statusPanel.setVisible(false);
					}
					setPreferredSize();

				} else if (src == filterOpt ) {

					// Reload the tree (if necessary)
					if ( root.getChildCount() > 0) {
						// there are nodes below the root, therefore need
						// to reload
						umlTree.display(treeModel, options, results);
					}

				} else if (src == displayOpt ) {

					// Update the display , if necessary (without reloading)
					if ( root.getChildCount(filterOpt) > 0) {
						// there are nodes below the root, therefore update
						// of the display is necessary
//						treeModel.updateVisibleChildren(root);
//						umlTree.updateVisible(options);
						umlTree.updateVisibleToggled(options);
					}
				}
			} // stateChanged(ChangeEvent): void
		} ;


		if (hasJavaFileExtension) {
			results = parser.parse();
			showResults(results);
		} else {
			showResults(results);
		}

		// Determine size and position of the GUI
/*
		Dimension screen = getToolkit().getScreenSize();
		pack();
		setPreferredSize();
		setLocation((screen.width - getSize().width) / 2,
				(screen.height - getSize().height) / 2);

		show();
*/
	} // JBrowse(JBrowse.Activator): <init>

    private void parseNow()
    {
    			// Set Wait Cursor
			setCursor(new Cursor(Cursor.WAIT_CURSOR));

			umlTree.setModel(null); // set model to null to speed up
			results = parser.parse();
			showResults(results);
			paintAll(getGraphics());

			// Set Default Cursor
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    public void jextEventFired(JextEvent evt)
    {
      //evt.getWhat() == JextEvent.BATCH_MODE_UNSET ||
      if ((!evt.getJextFrame().getBatchMode() && evt.getWhat() == JextEvent.TEXT_AREA_SELECTED))
        parseNow();
    }

	public Options.DisplayIro getDisplayOptions() { return displayOpt; }
	public ChangeListener getOptionListener() { return optionListener; }


	//-------------------------------------------------------------------------
	public void actionPerformed(ActionEvent evt)
	{
		Object source = evt.getSource();

		if (source == parseBtn) {

          parseNow();


		} else if (source == resizeBtn) {
			setPreferredSize();

		} else if (source == configBtn) {

			optionDialog.reInit(); // check if first time, init as required

			// Determine size and position of the OptionDialog
			Dimension screen = getToolkit().getScreenSize();
			Dimension optSize = optionDialog.getSize();
			int treeX = getLocation().x; // ???1.1 for 1.2 replace getLocation().x with getX()
			int optY  = (screen.height - optSize.height) / 2;

			if (treeX + getSize().width + 12 + optSize.width > screen.width ) {
				// i.e. won't fit at right so...
				if (treeX - optSize.width - 12 < 0 ) {
					// i.e. won't fit at left either so overlap at right end
					optionDialog.setLocation( screen.width - optSize.width - 2, optY);
				} else {
					// left of
					optionDialog.setLocation( treeX - optSize.width - 10, optY);
				}
			} else {
				// right of
				optionDialog.setLocation( treeX + getSize().width + 10, optY);
			}

			optionDialog.setVisible(true);
			optionDialog.paintAll(getGraphics());

			// Repaint the JBrowse Dialog, required to eliminate
			// any contamination from the OptionDialog (really!)
			paintAll(getGraphics());

		}

	} // actionPerformed(ActionEvent): void


	//-------------------------------------------------------------------------
	public void setPreferredSize()
	{
		topPanel.validate();
		Dimension dFrame    = getSize();          // current internal frame
		Dimension dViewCur  = scpTreePane.getViewport().getSize();
		Dimension dViewPref = scpTreePane.getViewport().getPreferredSize();
		Dimension dTreePref = umlTree.getPreferredSize();
		int newFrameWidth;

		// Set vertical scrollbar to visible, if it should be
		if (dViewCur.height < dViewPref.height) {
			scpTreePane.getVerticalScrollBar().setVisible(true);
		}

		// Set width to preferred width based on view and scroll bar
		if ( scpTreePane.getVerticalScrollBar().isVisible() ) {
			// i.e. 15 = scpTreePane.getVerticalScrollBar().getSize().width;
			newFrameWidth = dViewPref.width + 12 + 16;
		} else {
			newFrameWidth = dViewPref.width + 12;
		}

		// Adjust width for status panel, if visible
		if ( statusPanel.isVisible() ) {
			statusPanel.validate();
			newFrameWidth = Math.max(newFrameWidth, statusPanel.getPreferredSize().width + 8);
		}

		// Adjust width for top panel
		dFrame.width = Math.max( newFrameWidth, topPanel.getPreferredSize().width + 8);

		setSize(dFrame);
		paintAll(getGraphics());

	} // setPreferredSize(): void


	//-------------------------------------------------------------------------
	/**
	 * This method should be called after every parse. It updates the text of
	 * the status bar's labels to the results counts, displays the error
	 * indicator (if appropriate) and then calls umlTree.display() to reload
	 * the tree.
	 */
	public void showResults(JBrowseParser.Results results)
	{
		//System.out.println("showResults for options: " + options);

		umlTree.display(treeModel, options, results);

		if (results != null) {

			// Update Status Bar
			classLabel.setText(     "" + results.getClassCount() );
			interfaceLabel.setText( "" + results.getInterfaceCount() );
			attributeLabel.setText( "" + (results.getObjAttrCount() + results.getPrimAttrCount() ) );
			methodLabel.setText(    "" + results.getMethodCount() );

			// Update Parse Error Indicator
			if (results.getErrorCount() > 0) {
				errorLabel.setText( "" + results.getErrorCount() + " error(s)");
				errorLabel.setVisible(true);
				topPanel.validate();

				Dimension dFrame    = getSize();
				Dimension dTopPanel = topPanel.getPreferredSize();
				if (dFrame.width < dTopPanel.width + 8) {
					setPreferredSize();
				}

			} else {
				errorLabel.setVisible(false);
			}

//%			log(7, this, "DONE. (" + curTokenLine + " lines, "
//%					+ tokenCount + " tokens.)\n"
//%					+ "\n             Classes: " + results.getClassCount
//%					+ "\n          Interfaces: " + results.getInterfaceCount
//%					+ "\n   Object Attributes: " + results.getObjAttrCount
//%					+ "\nPrimitive Attributes: " + results.getPrimAttrCount
//%					+ "\n             Methods: " + results.getMethodCount
//%
//%					+ "\n\n              Errors: " + results.geterrorCount
//%					+ "\n\n     Final state was: " + parseState + " " + parseSubState
//%					+ "\n         Brace Count: " + methodBraceCount
//%					+ "\n  curElementStartPos: " + curElementStartPos
//%					+ "\n	        memberMod: " + memberMod
//%					+ "\n	       memberType: " + memberType
//%					+ "\n	       memberName: " + memberName );


			if ( results.getClassCount() + results.getInterfaceCount() == 0)
				getToolkit().beep();

		} // if (results != null) - required until File parser is implemented (???)

	} // showResults(JBrowseParser.Results): void


	//=========================================================================
	/**
	 * This class defines the JPanel that provides the gui content of the
	 * JBrowse dialog (the actual tree display).
	 */
	class TreePane extends JPanel
	{
		//---------------------------------------------------------------------
		/**
		 * Creates a new JBrowse.TreePane. This is called in the in the
		 * JBrowse constructor.
		 * @param name The internal name
		 */
		public TreePane(String title)
		{
			super();
			this.setName(title);

			this.setLayout(new BorderLayout());

			// Define and add JButtons for Action Panel
			URL url;

			url = this.getClass().getResource("Parse.gif");
			if (url == null) {
				parseBtn = new JextHighlightButton("Parse");
			} else {
				parseBtn  = new JextButton( new ImageIcon(url) );
			}

			url = this.getClass().getResource("Resize.gif");
			if (url == null) {
				resizeBtn = new JextHighlightButton("Resize");
			} else {
				resizeBtn = new JextButton( new ImageIcon(url) );
			}

			url = this.getClass().getResource("Config.gif");
			if (url == null) {
				configBtn = new JextHighlightButton("Config");
			} else {
				configBtn = new JextButton( new ImageIcon(url) );
			}

			Insets zeroMargin = new Insets(0, 0, 0, 0);
			parseBtn.setMargin(zeroMargin);
			resizeBtn.setMargin(zeroMargin);
			configBtn.setMargin(zeroMargin);

			parseBtn.setToolTipText("Parse the buffer");
			resizeBtn.setToolTipText("Adjust width");
			configBtn.setToolTipText("Set Options");

			parseBtn.addActionListener(JBrowse.this);
			resizeBtn.addActionListener(JBrowse.this);
			configBtn.addActionListener(JBrowse.this);

			// Error Indicator
			errorLabel = new JLabel(UML.Type.ERROR.getIcon());
			errorLabel.setIconTextGap(2);
//			errorLabel.setToolTipText();
			errorLabel.setFont(new Font("Helvetica", Font.PLAIN, 11));

			// Build Top Panel for BorderLayout
			JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));

			leftPanel.add(parseBtn);
			leftPanel.add(resizeBtn);
			leftPanel.add(configBtn);
			leftPanel.add(errorLabel);

			topPanel = new JPanel( new BorderLayout(0, 0) );
			topPanel.add(leftPanel,  BorderLayout.WEST);
			this.add(topPanel, BorderLayout.NORTH);

			scpTreePane = new JScrollPane(umlTree);

			this.add(scpTreePane, BorderLayout.CENTER);

		} // TreePane(String): <init>


		//---------------------------------------------------------------------
		/**
		 * The initialization process for the TreePane, called as the last
		 * step of the constructor.
		 */
		private void init()
		{
			//JBrowse.this.getRootPane().setDefaultButton(parseBtn);

			// Status Panel
			statusPanel = new JPanel(new GridLayout(1, 4, 0, 1));
			classLabel     = new JLabel(UML.Type.CLASS.getIcon());
			interfaceLabel = new JLabel(UML.Type.INTERFACE.getIcon());
			attributeLabel = new JLabel(UML.Type.ATTRIBUTE.getIcon());
			methodLabel    = new JLabel(UML.Type.METHOD.getIcon());

			classLabel.setIconTextGap(2);
			interfaceLabel.setIconTextGap(2);
			attributeLabel.setIconTextGap(2);
			methodLabel.setIconTextGap(2);

			classLabel.setToolTipText("classes");
			interfaceLabel.setToolTipText("interfaces");
			attributeLabel.setToolTipText("attributes");
			methodLabel.setToolTipText("methods");

			classLabel.setBorder(new EtchedBorder());
			interfaceLabel.setBorder(new EtchedBorder());
			attributeLabel.setBorder(new EtchedBorder());
			methodLabel.setBorder(new EtchedBorder());

			Font monoFont = new Font("Monospaced", Font.PLAIN, 11);

			classLabel.setFont(monoFont);
			interfaceLabel.setFont(monoFont);
			attributeLabel.setFont(monoFont);
			methodLabel.setFont(monoFont);

			statusPanel.add(classLabel);
			statusPanel.add(interfaceLabel);
			statusPanel.add(attributeLabel);
			statusPanel.add(methodLabel);
			this.add(statusPanel, BorderLayout.SOUTH);

			if (options.getShowStatusBar() ) {
				statusPanel.setVisible(true);
			} else {
				statusPanel.setVisible(false);
			}


			// Setup Tree

			fileName = parser.getSourceName(); // get fileName

			// set root node to file name
//			if (!fileName.toUpperCase().endsWith(".JAVA") ) {
			if (!((parser instanceof JBrowseLineParser
			 && ((JBrowseLineParser)parser).usesJavaTokenMarker())
			 || (fileName.toUpperCase().endsWith(".JAVA"))))
			{
				// N.B. some JDK's (1.1.x) will not compile += for a reference
				// to a private member of an enclosing class.
				fileName = fileName + " (NON-java file?)";
				hasJavaFileExtension = false;
				errorLabel.setText("" + "Un-parsed");
				topPanel.validate();
			} else {
				hasJavaFileExtension = true;
			}
			root = new UMLTree.Node(fileName);

			// get tree model and tree
			treeModel = new UMLTree.Model(root);

			//setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		} // init(): void

	} // public class JBrowse.TreePane extends JPanel


	//=========================================================================
	public static interface Activator
	{
		public Frame getOwner();
		public PropertyAccessor getPropertyAccessor();
		public JBrowseParser getJBrowseParser();
		public UMLTree getUMLTree();

	} // public static interface JBrowse.Activator


	//-------------------------------------------------------------------------
	PropertyAccessor getPropertyAccessor() { return props; }


	//=========================================================================
	static class OptionDialog extends JDialog implements ActionListener
	{

		private JBrowse parent;
		JBrowseOptionPane optionPane;

		// private members
		private JButton btnSetAsDefaults;
		private JButton btnRestoreDefaults;

		//---------------------------------------------------------------------
		OptionDialog(Frame jparent, JBrowse parent, String title)
		{
			super(jparent, title, true);
			this.parent = parent;
/*
			parent.addWindowListener(new WindowAdapter() {
					public void windowClosed(WindowEvent e) {
						dispose();
					}
			} );
*/
			optionPane = new JBrowseOptionPane("jbrowse_options");

			optionPane.setPropertyAccessor(parent.getPropertyAccessor());
			optionPane.load();

			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(BorderLayout.CENTER, optionPane);

			JPanel buttons = new JPanel();
            btnSetAsDefaults = new JextHighlightButton("Set As Defaults");
			btnSetAsDefaults.addActionListener(this);
			buttons.add(btnSetAsDefaults);

			btnRestoreDefaults = new JextHighlightButton("Restore Defaults");
			btnRestoreDefaults.addActionListener(this);
			buttons.add(btnRestoreDefaults);

			getContentPane().add(BorderLayout.SOUTH, buttons);

//			addKeyListener(this);
//			addWindowListener(this);

		} // OptionDialog(JBrowse, String): <init>


		/**
		 * Returns the option object associated with this OptionDialog's
		 * JBrowseOptionPane.
		 */
		Options getOptions() { return optionPane.getOptions(); }


		//---------------------------------------------------------------------
		/**
		 * Sets the state of the option object associated with this JBrowse
		 * session's JBrowseOptionPane to that pane's current state as specified
		 * by its GUI.
		 */
		void setOptions() { optionPane.setOptions(); }


		//--------------------------------------------------------------------
		void reInit()
		{
			// set optionPane GUI state to the inital model (as defined in properties)
			if (!optionPane.isInitGui() ) {
				optionPane.initGui();
				pack();
			}

			// Synchronize Options object and JBrowseOptionPane to property values.
			if (!optionPane.isInitModel() ) {
				optionPane.initModel();
				getOptions().addChangeListener( parent.getOptionListener() );
			}

		} // reInit(): void


		//--------------------------------------------------------------------
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();
			if (source == btnSetAsDefaults) {
				optionPane.save();
			} else if(source == btnRestoreDefaults) {
				boolean wasShowingStatusBar = getOptions().getShowStatusBar();
				optionPane.load();
				optionPane.initModel();
				parent.showResults(parent.results);

				// Display or hide the status bar
				boolean setShowingStatusBar = getOptions().getShowStatusBar();
				if (wasShowingStatusBar != setShowingStatusBar ) {
					if ( setShowingStatusBar ) {
						parent.statusPanel.setVisible(true);
					} else {
						parent.statusPanel.setVisible(false);
					}
					parent.setPreferredSize();
				}
			}
		} // actionPerformed(ActionEvent): void

	} // static class JBrowse.OptionDialog

} // public class JBrowse extends JDialog

// End of JBrowse.java
