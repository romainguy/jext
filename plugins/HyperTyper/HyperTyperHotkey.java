/*
 * 18:55:56 20/01/00
 *
 * HyperTyperHotkey.java - create a fast-key from the selected text
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

import org.jext.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 * Creates a fast-key sequence, using the selected text as the
 * expanded part, if given.
 */
public class HyperTyperHotkey extends MenuAction
{
    protected static final String PROP_NAME = HyperTyperPlugin.PROP_NAME +
        ".hotkey";
    protected static final String PROP_TITLE = PROP_NAME +
        ".dialog.title";
    protected static final String PROP_EXPANDED_LABEL = PROP_NAME +
        ".dialog.expanded_label";
    protected static final String PROP_ENTRY_FIELD = PROP_NAME +
        ".dialog.entry_field";
    protected static final String PROP_USE_TRANSIENT = PROP_NAME +
        ".dialog.transient_label";
    protected static final String PROP_USE_PERSISTENT = PROP_NAME +
        ".dialog.persistent_label";
    protected static final String TRANS = "trans";
    protected static final String PERSIS = "persis";




    private HyperTyperMapping htMap;

    // delay initialization until necessary
    private JTextArea expandedArea;
    private JPanel fullPanel;
    private JScrollPane scrollArea;
    private ButtonGroup whichIsSelected;

    public HyperTyperHotkey( HyperTyperMapping htm )
    {
        super( PROP_NAME );
        this.htMap = htm;
    }



    public void actionPerformed( ActionEvent evt )
    {
        // Get the selected text
        JextTextArea jta = getTextArea( evt );

        // lazy-load components
        if (this.fullPanel == null)
        {
            this.expandedArea = new JTextArea( 6, 30 );

            // use the same font as the editor
            int size = 0;
            try
            {
                size = Integer.parseInt( HyperTyperObjectManager.getProperty(
                    "editor.fontSize") );
            }
            catch (NumberFormatException nfe) { size = 12; }
            Font f = new Font( HyperTyperObjectManager.getProperty(
                "editor.font"), Font.PLAIN, size );
            this.expandedArea.setFont( f );

            this.fullPanel = new JPanel( new BorderLayout() );
            this.fullPanel.add( "North", new JLabel(
                HyperTyperObjectManager.getProperty( PROP_EXPANDED_LABEL ) ) );

            this.scrollArea = new JScrollPane( this.expandedArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );

            this.fullPanel.add( "Center", this.scrollArea );

            JRadioButton useTransient = new JRadioButton(
                HyperTyperObjectManager.getProperty( PROP_USE_TRANSIENT ));
            useTransient.setActionCommand( TRANS );
            useTransient.setSelected( true );
            JRadioButton usePersistent = new JRadioButton(
                HyperTyperObjectManager.getProperty( PROP_USE_PERSISTENT ));
            usePersistent.setActionCommand( PERSIS );

            this.whichIsSelected = new ButtonGroup();
            this.whichIsSelected.add( useTransient );
            this.whichIsSelected.add( usePersistent );

            JPanel panel = new JPanel( new BorderLayout() );

            JPanel buttonPanel = new JPanel( new FlowLayout() );
            buttonPanel.add( usePersistent );
            buttonPanel.add( useTransient );
            panel.add( "Center", buttonPanel );

            panel.add( "South", new JLabel(
                HyperTyperObjectManager.getProperty( PROP_ENTRY_FIELD ) ) );

            this.fullPanel.add( "South", panel );
        }


        String sel = jta.getSelectedText();

        if (sel == null)
        {
            // now, we allow no selection!
            sel = "";
        }


        // Add an editable field for changing the selected text
        this.expandedArea.setText( sel );

        this.scrollArea.setSize( this.expandedArea.getPreferredSize() );


        String title = HyperTyperObjectManager.getProperty( PROP_TITLE );

        JOptionPane pane = new JOptionPane( this.fullPanel,
            JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION );
        pane.setWantsInput( true );
        JDialog dialog = pane.createDialog( jta, title );
        dialog.show();
        Object selectedValue = pane.getValue();
        if (selectedValue == null)
        {
            return; // CLOSED_OPTION;
        }

        //If there is not an array of option buttons:
        if (selectedValue instanceof Integer)
        {
            int val = ((Integer)selectedValue).intValue();
            if (val != JOptionPane.OK_OPTION)
            {
                return; // clicked cancel
            }
        }

        String shorthand = (String)pane.getInputValue();

        // get the expanded text from the dialog's text area
        if (shorthand != null && shorthand.length() > 0)
        {
            if (isTransientSelected())
            {
                this.htMap.getTransientMappings().addPlainMapping(
                    shorthand, this.expandedArea.getText() );
            }
            else
            {
                this.htMap.getPersistentMappings().addPlainMapping(
                    shorthand, this.expandedArea.getText() );
            }
        }
    }

    /**
     * helper method
     */
    private boolean isTransientSelected()
    {
        return TRANS.equals(
            this.whichIsSelected.getSelection().getActionCommand() );
    }
}


