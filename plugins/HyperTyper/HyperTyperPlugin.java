/*
 * 18:55:56 20/01/00
 *
 * HyperTyperPlugin.java - extends the FastTyper capabilities of Jext
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
import org.jext.event.*;
import org.jext.options.*;

import java.util.Vector;

/**
 * The Plugin HyperTyper class handles the "when" adjustments of state -
 * i.e. it handles the registering of event listening.
 */
public class HyperTyperPlugin implements Plugin, JextListener
{
    protected static final String PROP_NAME = "hyper_typer";
    protected static final String PROP_TITLE = PROP_NAME + ".title";
    protected static final String PROP_KEYBIND = PROP_NAME + ".keybinding";
    protected static final String PROP_MENU = PROP_NAME + ".menu";


    private transient boolean htWindowNotStarted = true;

    public static HyperTyperObjectManager htOMan;
    private static boolean autoExpand = false;

    public static boolean isAutoExpandOn()
    {
      return autoExpand;
    }

    /**
     * Default constructor.
     */
    public HyperTyperPlugin()
    {
        // do nothing
    }


    /**
     * Called when the plugin is to initialize itself.
     */
    public void start()
    {
        this.htOMan = new HyperTyperObjectManager();
        autoExpand = "on".equals(Jext.getProperty("hyper_typer.autoExpand"));

        // add controls
        Jext.addAction( this.htOMan.getHotkey() );
        Jext.addAction( this.htOMan.getWindowGUI() );
        //Jext.addAction( );
        //Jext.addAction( );


        // Starting up the KeyBinding for the HyperTyperAction is in
        // the Option pane, since it is in charge of changing it
        // if the user modifies the options.
    }



    /**
     * Called when the plugin is to shutdown.
     */
    public void stop()
    {
        // Shutdown the dynamic data correctly
        // and close the window
        this.htOMan.stop();
        this.htOMan = null;
    }

    public void createOptionPanes( OptionsDialog parent )
    {
        // make sure to create pane on demand, not on creation
        parent.addOptionPane( this.htOMan.getOptionPane() );
    }



    /**
     * Add a submenu
     *      menus.addElement(GUIUtilities.loadMenu("menu_name");
     * Add an item
     *      items.addElement(GUIUtilities.loadMenuItem("action_name");
     * <p>
     * This method allows to do a lot of other stuffs as you can
     * use the Jext instance known as 'parent' in method body to
     * do something totally different
     * <P>
     * This is called on every new window instance.
     */
    public void createMenuItems( JextFrame parent, Vector menus, Vector items )
    {
        menus.addElement( GUIUtilities.loadMenu( PROP_MENU ) );

        // Add the Jext Listener to this window
        parent.addJextListener( this );

        // give the action a signal of a new file
        this.htOMan.getAction().newFileAdded( parent.getTextAreas()[0] );

        // delay start of hyper typer window until the first Jext window
        // has been created.
        if (this.htWindowNotStarted &&
            HyperTyperObjectManager.isPropertyOn(
            HyperTyperOption.PROP_WIN_AUTOSTART_STATE ))
        {
            // open autostart window.
            // window is Jext instance independent.
            this.htOMan.getWindowGUI().actionPerformed(
                new java.awt.event.ActionEvent( this,
                0, "" ) );
            this.htWindowNotStarted = false;
        }

        // Check if a tab should be inserted
        if (HyperTyperObjectManager.isPropertyOn(
            HyperTyperOption.PROP_PERSIST_TAB_AUTOSTART_STATE ))
        {
            HyperTyperMappingGrid grid = this.htOMan.getPersistentGrid();
            parent.getVerticalTabbedPane().addTab( grid.getName(), grid );
        }

        // test if we need to add the window's transient tag to the
        // Jext window's gui.
        if (HyperTyperObjectManager.isPropertyOn(
            HyperTyperOption.PROP_TRANSIENT_TAB_AUTOSTART_STATE ))
        {
            HyperTyperMappingGrid grid = this.htOMan.getTransientGrid();
            parent.getVerticalTabbedPane().addTab( grid.getName(), grid );
        }
    }


    /**
     * Tests to see if a new TextArea has been added; if so, fire all
     * pending events in associated objects.
     * Inherited from JextListener.
     */
    public void jextEventFired( JextEvent evt )
    {
        int type = evt.getWhat();
        if (type == JextEvent.TEXT_AREA_OPENED)
        {
            this.htOMan.getAction().newFileAdded( evt.getTextArea() );
        } else if (type == JextEvent.PROPERTIES_CHANGED) {
          autoExpand = "on".equals(Jext.getProperty("hyper_typer.autoExpand"));
        }
    }

}




