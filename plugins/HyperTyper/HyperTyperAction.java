/*
 * 11/11/2000 - 19:30:37
 *
 * HyperTyperAction.java - extends the FastTyper capabilities of Jext
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
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import org.jext.misc.Indent;
import org.gjt.sp.jedit.textarea.DefaultInputHandler;
import org.gjt.sp.jedit.textarea.InputHandler;
import org.jext.event.JextEvent;
import org.jext.event.JextListener;


/**
 * Performs the action bound to the keystroke for expanding a shorthand.
 * <P>
 * It could maintain a list of all Jext Text Areas that it has been
 * associated with, to avoid duplicating extra calls to add key bindings,
 * but that would require a WeakReference to prevent this handler from
 * keeping those removed areas around, which would pin us to JDK 1.2.
 */
public class HyperTyperAction extends KeyAdapter implements ActionListener
{
    // HyperTyper delimiters for finding between-spaces
    // Should this be a property?
    public static final String delimiters = " \t;.,\"\'(){}[]%+=-*|&^~";
		public static final char SPACE = ' ';

    private static final String DEFAULT_KEY_BIND = "ESCAPE";

    protected static final String PROP_NAME = HyperTyperHotkey.PROP_NAME;
    protected static final String PROP_KEYBIND = PROP_NAME + ".keybinding";
    protected static final String PROP_ERROR_MESSAGE = PROP_NAME +
        ".dialog.error_message";
    protected static final String PROP_ERROR_TITLE = PROP_NAME +
        ".dialog.error_title";

    private static final boolean IS_REMOVE_BINDING_IMPLEMENTED = false;

    private HyperTyperMapping htMap;
    private ActionListener originalKeyAction = null;


    /**
     * Constructor to make the HyperTyper action.
     */
    public HyperTyperAction( HyperTyperMapping htMap )
    {
        this.htMap = htMap;


        // setup the initial key handler
        setKeyBinding( HyperTyperObjectManager.getProperty(
            PROP_KEYBIND ) );

        // Hack:
        // don't forget to do it with the very first Jext Area
        Jext.getInputHandler().addKeyBinding(
            HyperTyperObjectManager.getProperty( PROP_KEYBIND ), this );
    }


    /**
     * Called when a new file was added to the window.  This may mean
     * a new text area.
     */
    public void newFileAdded( JextTextArea area )
    {
        // reset all the properties, I guess.
        String bind = HyperTyperObjectManager.getProperty( PROP_KEYBIND );
        area.getInputHandler().addKeyBinding( bind, this );
        area.getInputHandler().setInputAction(new AutoExpander());
    }

    /**
     * Set this action's key binding!
     * <P>
     * This routine may need to be changed in the future - it only
     * extracts a single action handler from the default text area;
     * it doesn't consider multiple text areas for restoring the
     * original bindings.
     */
    public void setKeyBinding( String bind )
    {
        DefaultInputHandler ih =  Jext.getInputHandler();

        String orig = Jext.getProperty( PROP_KEYBIND );
        if (ih.parseKeyStroke( bind ) == null)
        {
            // not a well formed keystroke
            if (ih.parseKeyStroke( orig ) == null)
            {
                // the original property is bad, so set it to the default
                orig = DEFAULT_KEY_BIND;

                // this does require an actual binding, though
                this.originalKeyAction = null;
                setKeyBinding( orig );
            }

            Jext.setProperty( PROP_KEYBIND, orig );

            JOptionPane.showMessageDialog( null,
                HyperTyperObjectManager.getProperty( PROP_ERROR_MESSAGE ) + " '" +
                orig + "'.",
                HyperTyperObjectManager.getProperty( PROP_ERROR_TITLE ),
                JOptionPane.ERROR_MESSAGE );

            return;
        }

        if (this.originalKeyAction != null)
        {
            addKeyBinding( orig, this.originalKeyAction );
        }
        else
        {
            if (ih.parseKeyStroke( orig ) != null)
            {
                removeKeyBinding( orig );
            }
        }

        this.originalKeyAction = ih.getAction( bind );

        Jext.setProperty( PROP_KEYBIND, bind );
        addKeyBinding( bind, this );
    }


    /**
     * Adds the given binding to all Jext text areas.  Should only be called
     * when the key binding has changed.
     */
    protected static void addKeyBinding( String bind, ActionListener action )
    {
        Iterator enum = getInputHandlers();

        while (enum.hasNext())
        {
            ((InputHandler)enum.next()).addKeyBinding( bind, action );
        }
    }



    /**
     * Removes the given binding from all Jext text areas.
     */
    protected static void removeKeyBinding( String bind )
    {
        Iterator enum = getInputHandlers();

        while (enum.hasNext())
        {
            enum.next();
            if (IS_REMOVE_BINDING_IMPLEMENTED)
            {
                // If not implemented, but above is true, this throws
                // a not-yet-implemented exception.
                ((InputHandler)enum.next()).removeKeyBinding( bind );
            }
        }
    }


    /**
     * Retrieve a list of every single known Jext instance's text area's
     * input handler.
     */
    protected static Iterator getInputHandlers()
    {
        ArrayList v = Jext.getInstances();
        ArrayList areaV = new ArrayList ();
        JextTextArea[] areas;

        for (int i = 0; i < v.size(); i++)
        {
            areas = ((JextFrame) v.get(i)).getTextAreas();
            for (int j = 0; j < areas.length; j++)
            {
                areaV.add(areas[j].getInputHandler());
            }
        }

        return areaV.listIterator();
    }


    /**
     * The HyperTyper text-finder and replacer.  To perform proper
     * indention, we need to do incremental outputting of text.
     * <P>
     * This method is overly complex, and should be broken up.
     *
     * @author Romain Guy, Matt Albrecht
     */
    public void actionPerformed(ActionEvent evt)
    {
        JextTextArea textArea = (JextTextArea) InputHandler.getTextArea(evt);
        textArea.beginCompoundEdit();

        Document doc = textArea.getDocument();
        Element map = doc.getDefaultRootElement();
        Element lineElement = map.getElement(map.getElementIndex(
            textArea.getCaretPosition()));


        //
        // First, discover which sequence the user wants.
        //


        int start = lineElement.getStartOffset();
        int end = lineElement.getEndOffset() - 1;
        int length = end - start;
        int startPos = textArea.getCaretPosition();

        if (startPos == end)
        {
            if (startPos == start)
            {
                textArea.endCompoundEdit();
                return;
            }
            else
            {
                startPos--;
            }
        }

        String _line = textArea.getText(start, length);
        int linePos = startPos - start;

        int wordStart = -1;
        int wordEnd = -1;

        if (delimiters.indexOf(_line.charAt(linePos)) != -1)
        {
            if (linePos > 0)
            {
                if (delimiters.indexOf(_line.charAt(linePos - 1)) != -1)
                {
                    textArea.endCompoundEdit();
                    return;
                }
                else
                {
                    wordEnd = linePos;
                }
            }

            for (int i = linePos - 1; i >= 0; i--)
            {
                if (delimiters.indexOf(_line.charAt(i)) != -1)
                {
                    wordStart = i + 1;
                    break;
                }
            }
        }
        else
        {
            // case caret is placed on a word

            for (int i = linePos; i < length; i++)
            {
                if (delimiters.indexOf(_line.charAt(i)) != -1)
                {
                    wordEnd = i;
                    break;
                }
            }

            for (int i = linePos; i >= 0; i--)
            {
                if (delimiters.indexOf(_line.charAt(i)) != -1)
                {
                    wordStart = i + 1;
                    break;
                }
            }
        }

        if (wordStart == -1)
        {
            wordStart = 0;
        }

        if (wordEnd == -1)
        {
            wordEnd = length;
        }

        //
        // Expand the text
        //


        String expanded = this.htMap.getExpandedText( _line.substring( wordStart, wordEnd ) );
        int insertPos = 0;
        if (expanded != null)
        {
            textArea.beginProtectedCompoundEdit();
            try
            {
						    if (wordEnd != _line.length() && _line.charAt(wordEnd) == SPACE)
								{
//									could just trim but we only want to treat spaces, not other whitespace chars.
									StringBuffer workBuf = new StringBuffer(expanded);
									int wbLen;
									for (wbLen = workBuf.length();
									 workBuf.charAt(wbLen - 1) == SPACE; wbLen--);
									workBuf.setLength(wbLen);
									expanded = workBuf.toString();
								}//end if expanded ends with a space and next char is also a space

                doc.remove(wordStart + start, wordEnd - wordStart);
                // avoid computing this operation all the other times
                wordStart += start;

                StringBuffer _buf = new StringBuffer(expanded.length());
                // 0 = not set
                // 1 = set
                // 2 = set & indent once
                int caretState = 0;
                boolean onFirstLine = true, wasFirstLine = false;
                int lastBreak = -1;
                // by default, the caret position is at the end of the
                // expanded text.
                int caret = expanded.length();
                char c;

out:            for (int i = 0 ; i < expanded.length(); i++)
                {
                    switch (c = expanded.charAt(i))
                    {
                        case '|':
                            if (i < expanded.length() - 1 &&
                                expanded.charAt(i + 1) == '|')
                            {
                                i++;
                                _buf.append('|');
                            }
                            else
                            {
                                // Only set the caret the first time
                                // This is a disputable check.
                                if (caretState == 0)
                                {
                                    caret = insertPos + _buf.length();
                                    caretState = 1;

                                    // don't do indent on next line!
                                    if (onFirstLine)
                                    {
                                        wasFirstLine = true;
                                    }
                                }
                            }
                          break;
                        case '\n':
                            // put the proper tabbing after the new-line
                            // but only if we are not on the first line
                            if (textArea.getEnterIndent() && !onFirstLine)
                            {
                                // In order to indent correctly,
                                // we need to put the so-far text
                                // into the document.
                                doc.insertString( wordStart +
                                    insertPos, _buf.toString(), null );
                                // So increment the incremental inserting
                                // position counter
                                insertPos += _buf.length();
                                // and start the buffer over clean
// this is a JDK1.2 call - so 86 it.
//                                _buf.delete( 0, _buf.length() );
// and instead use a JDK1.1 call; here we need to create a new buffer
                                _buf = new StringBuffer( expanded.length() -
                                    _buf.length() );

                                // and indent
                                int tempLen = doc.getLength();
                                Indent.indent(textArea, textArea.getCaretLine(), true, false);
                                // Now we need to increment the
                                // incremental inserting position counter
                                // by the indention amount.
                                int indentLen = doc.getLength() - tempLen;

                                // Adjust the caret position to
                                // reflect the insertion, if it has just been
                                // set.
                                if (caretState == 1)
                                {
                                    if (!wasFirstLine)
                                    {
                                        caret += indentLen;
                                    }
                                    caretState = 2;
                                }
                                insertPos += indentLen;

                                wasFirstLine = false;
                            }
                            _buf.append('\n');
                            onFirstLine = false;
                            lastBreak = i;
                            break;
                        default:
                            _buf.append(c);
                    }
                }
                doc.insertString(wordStart + insertPos,
                    _buf.toString(), null);

                // perform a final insert, if we're not on the first line
                if (!onFirstLine)
                {
                    int tempLen = doc.getLength();
                    Indent.indent(textArea, textArea.getCaretLine(), true, false);
                    // possibly adjust the caret position
                    if (lastBreak < caret && caretState <= 1)
                    {
                        caret += doc.getLength() - tempLen;
                    }
                }

                // set the caret position correctly,
                // without going past the end of the file.
                int caretPos = wordStart + caret;
                int tempLen = doc.getLength();
                if (caretPos > tempLen)
                {
                    caretPos = tempLen;
                }
                textArea.setCaretPosition( caretPos );
            }
            catch (BadLocationException ble)
            {
                // do nothing
            }

            textArea.endProtectedCompoundEdit();
        }

        textArea.endCompoundEdit();
    } // end HyperTyper action!

}


