/*
 * 00:23:37 03/08/00
 *
 * HyperTyperMappingGrid.java - handles editing of existing mappings
 * Copyright (C) 2000 Romain Guy, Matt Albrecht
 * romain.guy@jext.org
 * www.jext.org
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
import org.jext.gui.*;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;


/**
 * GUI for handling the display of a mapping set.
 */
public class HyperTyperMappingGrid extends JPanel
        implements SequenceChangedListener
{
    private static final String PROP_TITLE = ".title";
    private static final String PROP_COL1 = ".column1";
    private static final String PROP_COL2 = ".column2";
    private static final String PROP_MOVE_BUTTON = ".move_button.text";
    private static final String PROP_MOVE_BUTTON_ACCEL =
        ".move_button.accelerator";
    private static final String PROP_DEL_BUTTON = ".delete_button.text";
    private static final String PROP_DEL_BUTTON_ACCEL =
        ".delete_button.accelerator";


    private JTable table;
    private AbstractTableModel model;
    private HyperTyperSequenceList htsl, otherList;
    private String propertyBase;

    public HyperTyperMappingGrid( HyperTyperSequenceList htsl,
            HyperTyperSequenceList otherList,
            String propertyBase )
    {
        this.htsl = htsl;
        this.otherList = otherList;
        this.propertyBase = propertyBase;

        construct();

        htsl.addSequenceChangedListener( this );
    }

    public String getName()
    {
        return HyperTyperObjectManager.getProperty(
            this.propertyBase + PROP_TITLE );
    }

    public void sequenceAdded( SequenceChangedEvent sce )
    {
        model.fireTableRowsInserted( sce.index, sce.index );
    }

    public void sequenceRemoved( SequenceChangedEvent sce )
    {
        model.fireTableRowsDeleted( sce.index, sce.index );
    }

    protected void construct()
    {
        setLayout( new GridLayout( 1,1 ) );
        setBorder( BorderFactory.createTitledBorder( HyperTyperObjectManager.getProperty(
            this.propertyBase + PROP_TITLE ) ) );

        this.model = new TyperTableModel();
        this.table = new JTable( model );
        this.table.getTableHeader().setReorderingAllowed( false );
        JScrollPane jsp = new JScrollPane( this.table );
        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( "Center", jsp );

        JPanel p2 = new JPanel( new FlowLayout() );
        JextHighlightButton jb = new JextHighlightButton( HyperTyperObjectManager.getProperty( this.propertyBase +
            PROP_DEL_BUTTON ) );
        jb.setMnemonic( HyperTyperObjectManager.getProperty( this.propertyBase +
            PROP_DEL_BUTTON_ACCEL ).charAt(0) );
        jb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae )
            {
                int row = table.getSelectedRow();
                if (row >= 0 && row < htsl.getSequenceCount())
                {
                    Sequence seq = htsl.getSequenceAt( row );
                    htsl.removeMapping( seq.getShorthand() );
                }
            } } );
        p2.add( jb );

        jb = new JextHighlightButton( HyperTyperObjectManager.getProperty( this.propertyBase +
            PROP_MOVE_BUTTON ) );
        jb.setMnemonic( HyperTyperObjectManager.getProperty( this.propertyBase +
            PROP_MOVE_BUTTON_ACCEL ).charAt(0) );
        jb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae )
            {
                int row = table.getSelectedRow();
                if (row >= 0 && row < htsl.getSequenceCount())
                {
                    Sequence seq = htsl.getSequenceAt( row );
                    htsl.removeMapping( seq.getShorthand() );
                    otherList.addMapping( seq );
                }
            } } );
        p2.add( jb );

        panel.add( "South", p2 );

        add( panel );
    }

    public int getSelectedRow()
    {
        return this.table.getSelectedRow();
    }

    public String getSelectedShorthand()
    {
        Sequence seq = getSelectedSequence();
        return (seq == null ? null : getSelectedSequence().getShorthand() );
    }

    public Sequence getSelectedSequence()
    {
        int i = getSelectedRow();
        return (i < 0 ? null : this.htsl.getSequenceAt( i ) );
    }

    class TyperTableModel extends AbstractTableModel
    {
        private String tempShorthand = "", tempExpanded = "";


        TyperTableModel()
        {
        }

        public int getColumnCount()
        {
            return 2;
        }

        public int getRowCount()
        {
            return htsl.getSequenceCount() + 1;
        }

        public Object getValueAt(int row, int col)
        {
            if (row > htsl.getSequenceCount())
            {
                return null;
            }

            if (row == htsl.getSequenceCount())
            {
                return (col == 0 ? tempShorthand : tempExpanded );
            }

            Sequence sequence = htsl.getSequenceAt( row );
            return (col == 0 ? sequence.getShorthand() :
                sequence.getExpanded() );
        }

        public boolean isCellEditable(int row, int col)
        {
            return true;
        }

        public String getColumnName(int index)
        {
            switch(index)
            {
                case 0:
                    return HyperTyperObjectManager.getProperty( propertyBase + PROP_COL1 );
                case 1:
                    return HyperTyperObjectManager.getProperty( propertyBase + PROP_COL2 );
                default:
                    return null;
            }
        }

        public void setValueAt(Object value, int row, int col)
        {
            String val = (String) value;

            if (col == 0 && val.indexOf(' ') != -1)
            {
                Utilities.showError(
                    HyperTyperObjectManager.getProperty("options.fastTyper.errorMessage"));
                return;
            }

            if (row == htsl.getSequenceCount())
            {
                // last entry - used for creating a new entry.
                if (col == 0)
                {
                    tempShorthand = val;
                }
                else
                {
                    tempExpanded = val;
                }

                if (tempShorthand.length() > 0 && tempExpanded.length() > 0)
                {
                    // make this a new entry
                    htsl.addMapping( tempShorthand, tempExpanded );
                    tempShorthand = "";
                    tempExpanded = "";
                }
                return;
            }

            Sequence seq = htsl.getSequenceAt(row);
            htsl.removeMapping( seq.getShorthand() );
            if (col == 0)
            {
                if (val.length() > 0)
                {
                    htsl.addMapping( val, seq.getExpanded() );
                }
                // else, keep the mapping deleted
            }
            else
            {
                // don't delete the mapping if the length is <= 0
                htsl.addMapping( seq.getShorthand(), val );
            }

            fireTableRowsUpdated( row, row );
        }
    }
}


