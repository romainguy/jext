/*
 * 18:55:56 20/01/00
 *
 * HyperTyperObjectManager.java - extends the FastTyper capabilities of Jext
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

import org.jext.Jext;

/**
 * The Object Manager HyperTyper class handles the "what" of object
 * storage and life-cycle maintenance.
 * <P>
 * This manager keeps track of objects that need to be created each time,
 * or that are singletons.  It also lazy-loads as much as possible for
 * singletons.
 */
public class HyperTyperObjectManager
{
    protected static final String PROP_NAME = HyperTyperPlugin.PROP_NAME;
    protected static final String PROP_PERSIST =
        PROP_NAME + ".mapping.persistent_table";
    protected static final String PROP_TRANSIENT =
        PROP_NAME + ".mapping.transient_table";



    // Plugin helpers - these are one per plugin
    private HyperTyperHotkey htHotkey;
    private HyperTyperWindowGUI htWindow;

    // Support for plugin helpers
    //    these are one per plugin as well
    private HyperTyperMapping htMap;
    private HyperTyperOption htOption;
    private HyperTyperAction htAction;


    public HyperTyperObjectManager()
    {
        // do nothing
    }


    /**
     * Retrieves the singleton mapping handler.
     */
    public HyperTyperMapping getMapping()
    {
        if (this.htMap == null)
        {
            this.htMap = new HyperTyperMapping();
        }
        return this.htMap;
    }


    /**
     * Retrieves the singleton action handler.
     */
    public HyperTyperAction getAction()
    {
        if (this.htAction == null)
        {
            this.htAction = new HyperTyperAction( getMapping() );
        }
        return this.htAction;
    }

    /**
     * Retrives the singleton hotkey dialog.
     */
    public HyperTyperHotkey getHotkey()
    {
        if (this.htHotkey == null)
        {
            this.htHotkey = new HyperTyperHotkey( getMapping() );
        }
        return this.htHotkey;
    }

    /**
     * Retrieves the singleton mapping window editor.
     */
    public HyperTyperWindowGUI getWindowGUI()
    {
        if (this.htWindow == null)
        {
            this.htWindow = new HyperTyperWindowGUI( this );
        }
        return this.htWindow;
    }


    /**
     * Retrieves the singleton option pane.
     */
    public HyperTyperOption getOptionPane()
    {
        if (this.htOption == null)
        {
            this.htOption = new HyperTyperOption( getAction() );
        }
        return this.htOption;
    }


    /**
     * Retrieves an instance of a Mapping Grid editor for persistent
     * maps.
     */
    public HyperTyperMappingGrid getPersistentGrid()
    {
        HyperTyperMappingGrid grid = new HyperTyperMappingGrid(
            getMapping().getPersistentMappings(),
            getMapping().getTransientMappings(),
            PROP_PERSIST  );
        return grid;
    }


    /**
     * Retrieves an instance of a Mapping Grid editor for transient
     * maps.
     */
    public HyperTyperMappingGrid getTransientGrid()
    {
        HyperTyperMappingGrid grid = new HyperTyperMappingGrid(
            getMapping().getTransientMappings(),
            getMapping().getPersistentMappings(),
            PROP_TRANSIENT );
        return grid;
    }



    /**
     * Kills all Objects.
     */
    public void stop()
    {
        // Shutdown the dynamic data correctly
        // and close the window
        if (this.htWindow != null)
        {
            this.htWindow.shutdown();
            this.htWindow = null;
        }

        if (this.htMap != null)
        {
            this.htMap.shutdown();
            this.htMap = null;
        }

        this.htHotkey = null;
        this.htAction = null;
        this.htOption = null;
    }



    //----------------------------
    // Static Methods


    /**
     * Converts a checkbox to an on/off string for properties purposes.
     */
    public static String toOnOffString( javax.swing.JCheckBox cb )
    {
        return cb.isSelected() ? "on" : "off";
    }


    /**
     * Checks of the given property name is "on" or "off", and returns
     * true or false, respectively.
     */
    public static boolean isPropertyOn( String propertyName )
    {
        return "on".equalsIgnoreCase(
            HyperTyperObjectManager.getProperty( propertyName ) );
    }


    /**
     * Returns the value of the given property name, as set in the
     * Jext properties.  If the value is null, an error message is
     * generated, and an empty string is returned.
     */
    public static final String getProperty( String name )
    {
        if (name == null || name.equals("null"))
        {
            System.out.println("A \"getProperty\" called with a null name");
            throw new IllegalArgumentException( "null name" );
        }
        String n = Jext.getProperty( name );
        if (n == null)
        {
            System.out.println("Jext Property \""+name+"\" not defined.");
            return "";
        }
        return n;
    }
}




