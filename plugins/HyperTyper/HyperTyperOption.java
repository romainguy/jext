/*
 * 18:55:56 20/01/00
 *
 * HyperTyperOption.java - extends the FastTyper capabilities of Jext
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
import org.jext.gui.*;
import javax.swing.*;
import java.awt.FlowLayout;

/**
 * Option pane for controlling basic settings for the HyperTyper
 */
public class HyperTyperOption extends AbstractOptionPane
{
    protected static final String PROP_NAME = HyperTyperPlugin.PROP_NAME;
    protected static final String PROP_TITLE = PROP_NAME + ".title";
    protected static final String PROP_KEYBIND =
        HyperTyperAction.PROP_KEYBIND;
    protected static final String PROP_KEYBINDING_LABEL = PROP_NAME +
        ".keybinding.label";
    protected static final String PROP_WIN_AUTOSTART_STATE =
        PROP_NAME + ".window_autostart";
    protected static final String PROP_WIN_AUTOSTART_LABEL =
        PROP_WIN_AUTOSTART_STATE + ".label";
    protected static final String PROP_PERSIST_TAB_AUTOSTART_STATE =
        PROP_NAME + ".persist_tab_autostart";
    protected static final String PROP_PERSIST_TAB_AUTOSTART_LABEL =
        PROP_PERSIST_TAB_AUTOSTART_STATE + ".label";
    protected static final String PROP_TRANSIENT_TAB_AUTOSTART_STATE =
        PROP_NAME + ".transient_tab_autostart";
    protected static final String PROP_TRANSIENT_TAB_AUTOSTART_LABEL =
        PROP_TRANSIENT_TAB_AUTOSTART_STATE + ".label";



    // selected option states
    private JextCheckBox autoExpand;
    private JextCheckBox winAutoStart;
    private JextCheckBox persistTabAutoStart;
    private JextCheckBox transTabAutoStart;
    private JTextField keyBinding;

    // Support for plugins
    private HyperTyperAction htAct;


    public HyperTyperOption( HyperTyperAction htAct )
    {
        super( PROP_NAME );
        this.htAct = htAct;
        construct();
        load();

        // the initial action keybinding is done in the action itself.
    }

    public HyperTyperAction getAction()
    {
        return this.htAct;
    }

    public void save()
    {
        Jext.setProperty( "hyper_typer.autoExpand",
            HyperTyperObjectManager.toOnOffString( this.autoExpand ) );

        Jext.setProperty( PROP_WIN_AUTOSTART_STATE,
            HyperTyperObjectManager.toOnOffString( this.winAutoStart ) );

        Jext.setProperty( PROP_PERSIST_TAB_AUTOSTART_STATE,
            HyperTyperObjectManager.toOnOffString( this.persistTabAutoStart ) );

        Jext.setProperty( PROP_TRANSIENT_TAB_AUTOSTART_STATE,
            HyperTyperObjectManager.toOnOffString( this.transTabAutoStart ) );


        String val = this.keyBinding.getText();
        this.htAct.setKeyBinding( val );
    }



    protected void construct()
    {
        JLabel label = new JLabel( HyperTyperObjectManager.getProperty(
            HyperTyperPlugin.PROP_TITLE ) );
        addComponent( label );

        this.winAutoStart = new JextCheckBox(
            HyperTyperObjectManager.getProperty(
                PROP_WIN_AUTOSTART_LABEL ) );
        addComponent( this.winAutoStart );

        this.autoExpand = new JextCheckBox(HyperTyperObjectManager.getProperty("hyper_typer.autoexpand.label"));
        addComponent(this.autoExpand);

        this.persistTabAutoStart = new JextCheckBox(
            HyperTyperObjectManager.getProperty(
            PROP_PERSIST_TAB_AUTOSTART_LABEL ) );
        addComponent( this.persistTabAutoStart );

        this.transTabAutoStart = new JextCheckBox(
            HyperTyperObjectManager.getProperty(
            PROP_TRANSIENT_TAB_AUTOSTART_LABEL ) );
        addComponent( this.transTabAutoStart );

        keyBinding = new JTextField( 12 );
        addComponent(HyperTyperObjectManager.getProperty(
            PROP_KEYBINDING_LABEL ), keyBinding );
    }
    //Code added for the new Jext3.2pre1; it enables the caching of the option panes(and doesn't hurt
    //for previous releases of Jext, even if for this plugin it isn't important).
    public boolean isCacheable() {
        return true;
    }

    public void load() {
        this.winAutoStart.setSelected( HyperTyperObjectManager.isPropertyOn(
            PROP_WIN_AUTOSTART_STATE ) );
        this.autoExpand.setSelected(HyperTyperObjectManager.isPropertyOn("hyper_typer.autoExpand"));
        this.persistTabAutoStart.setSelected(
            HyperTyperObjectManager.isPropertyOn(
                PROP_PERSIST_TAB_AUTOSTART_STATE ) );
        this.transTabAutoStart.setSelected(
            HyperTyperObjectManager.isPropertyOn(
                PROP_TRANSIENT_TAB_AUTOSTART_STATE ) );
        keyBinding.setText(HyperTyperObjectManager.getProperty( PROP_KEYBIND ));
    }
}


