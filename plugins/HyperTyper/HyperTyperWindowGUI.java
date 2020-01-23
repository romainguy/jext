/*
 * 18:55:56 20/01/00
 *
 * HyperTyperWindowGUI.java - plugin interface to the HyperTyper window
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
 * Combined the Plugin and Option Pane together for better control
 * of the toolbar.  To speed up initial start time, the actual GUI
 * isn't constructed until it is needed.
 */
public class HyperTyperWindowGUI extends MenuAction
{
    protected static final String PROP_NAME = HyperTyperPlugin.PROP_NAME +
        ".window";
    protected static final String PROP_TITLE = PROP_NAME + ".title";
    protected static final String PROP_WIDTH = PROP_NAME + ".width";
    protected static final String PROP_HEIGHT = PROP_NAME + ".height";
    protected static final String PROP_POSX = PROP_NAME + ".posx";
    protected static final String PROP_POSY = PROP_NAME + ".posy";
    private static final int DEFAULT_HEIGHT = 400;
    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_POSX = 0;
    private static final int DEFAULT_POSY = 0;


    private JPanel win;
    private JFrame frame = null;
    private HyperTyperMappingGrid persistentGrid, transientGrid;

    // this field is only valid while the grids are null,
    // for lazy-loading purposes
    private HyperTyperObjectManager htOMan;

    /**
     * Create the GUI reference.  However, we need to lazy-load the
     * grids to speed up start time.
     */
    public HyperTyperWindowGUI( HyperTyperObjectManager htOMan )
    {
        super( PROP_NAME );

        this.htOMan = htOMan;
    }

    public void actionPerformed( ActionEvent evt )
    {
        if (this.win == null)
        {
            this.win = constructWindow();
        }

        if (this.frame == null)
        {
            this.frame = new JFrame(HyperTyperObjectManager.getProperty(
                PROP_TITLE ));
            this.frame.setIconImage( GUIUtilities.getJextIconImage() );
            this.frame.getContentPane().add( "Center", this.win );
            int h, w, x, y;
            h = getIntProperty( PROP_HEIGHT );
            w = getIntProperty( PROP_WIDTH );
            x = getIntProperty( PROP_POSX );
            y = getIntProperty( PROP_POSY );
            if (h < 0) h = DEFAULT_HEIGHT;
            if (w < 0) w = DEFAULT_WIDTH;
            if (x < 0) x = DEFAULT_POSX;
            if (y < 0) y = DEFAULT_POSY;
            this.frame.setSize( w, h );
            this.frame.setLocation( x, y );
            this.frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        }

        frame.setVisible( true );
    }


    public void shutdown()
    {
        if (this.frame != null)
        {
            Rectangle size = this.frame.getBounds();
            Jext.setProperty( PROP_HEIGHT, ""+size.height );
            Jext.setProperty( PROP_WIDTH, ""+size.width );
            Jext.setProperty( PROP_POSX, ""+size.x );
            Jext.setProperty( PROP_POSY, ""+size.y );
            this.frame.setVisible( false );
            this.frame.dispose();
            this.frame = null;
        }

        this.win = null;

        this.htOMan = null;
        this.persistentGrid = null;
        this.transientGrid = null;
    }


    protected JPanel constructWindow()
    {
        // lazy-load the grids
        this.persistentGrid = this.htOMan.getPersistentGrid();
        this.transientGrid = this.htOMan.getTransientGrid();
        // no longer need htOMan
        this.htOMan = null;

        JPanel mainPanel = new JPanel( new GridLayout(1,1) );

        JTabbedPane jtp = new JTabbedPane( JTabbedPane.BOTTOM );
        jtp.addTab( this.persistentGrid.getName(), this.persistentGrid );
        jtp.addTab( this.transientGrid.getName(), this.transientGrid );
        mainPanel.add( jtp );

        return mainPanel;
    }


    public static final int getIntProperty( String propName )
    {
        String s = Jext.getProperty( propName );
        if (s == null) return -1;
        try
        {
            return Integer.parseInt( s );
        }
        catch (NumberFormatException nfe)
        {
            return -2;
        }
    }
}


