/*
 *   Copyright (c) 2009 The JOMC Project
 *   Copyright (c) 2005 Christian Schulte <schulte2005@users.sourceforge.net>
 *   All rights reserved.
 *
 *   Redistribution and use in source and binary forms, with or without
 *   modification, are permitted provided that the following conditions
 *   are met:
 *
 *     o Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     o Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in
 *       the documentation and/or other materials provided with the
 *       distribution.
 *
 *   THIS SOFTWARE IS PROVIDED BY THE JOMC PROJECT AND CONTRIBUTORS "AS IS"
 *   AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *   THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *   PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE JOMC PROJECT OR
 *   CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *   EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 *   OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 *   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 *   OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *   ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *   $Id$
 *
 */
package org.jomc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Interface to line based editing.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class LineEditor
{

    /** Flag indicating that this editor changed its input. */
    private boolean inputModified;

    /** Editor to chain. */
    private LineEditor editor;

    /** Creates a new {@code LineEditor} instance. */
    public LineEditor()
    {
        this( null );
    }

    /**
     * Creates a new {@code LineEditor} instance taking an editor to chain.
     *
     * @param editor The editor to chain.
     */
    public LineEditor( final LineEditor editor )
    {
        super();
        this.editor = editor;
    }

    /**
     * Edits texts.
     * <p>This method splits the given string into lines and passes every line to method {@code getNextLine} in order of
     * occurrence in the given string.</p>
     *
     * @param text The text to edit.
     *
     * @return The edited text.
     */
    public final String edit( String text )
    {
        try
        {
            final BufferedReader reader = new BufferedReader( new StringReader( text ) );
            final StringBuffer edited = new StringBuffer();

            String line = null;
            while ( ( line = reader.readLine() ) != null )
            {
                final String replacement = this.getNextLine( line );
                if ( replacement != null )
                {
                    edited.append( replacement ).append( "\n" );
                }
            }

            final String replacement = this.getNextLine( line );
            if ( replacement != null )
            {
                edited.append( replacement );
            }

            if ( !text.equals( ( text = edited.toString() ) ) )
            {
                this.inputModified = true;
            }

            if ( this.editor != null )
            {
                text = this.editor.edit( text );
                if ( this.editor.isInputModified() )
                {
                    this.inputModified = true;
                }
            }

            return text;
        }
        catch ( IOException e )
        {
            throw new AssertionError( e );
        }
    }

    /**
     * Processes the next line of input.
     *
     * @param line The next line of input or {@code null}, if the end of the input has been reached.
     *
     * @return The string to replace {@code line} with, or {@code null} to replace {@code line} with nothing.
     */
    public String getNextLine( final String line )
    {
        return line;
    }

    /**
     * Flag indicating that this editor changed its input.
     *
     * @return {@code true} if this editor changed its input; {@code false} if not.
     */
    public final boolean isInputModified()
    {
        return this.inputModified;
    }

    /**
     * Sets the flag indicating that this editor changed its input.
     *
     * @param value {@code true} if this editor changed its input; {@code false} if not.
     */
    public final void setInputModified( final boolean value )
    {
        this.inputModified = value;
    }

}
