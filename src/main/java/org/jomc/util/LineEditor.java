/*
 *   Copyright (C) 2009 The JOMC Project
 *   Copyright (C) 2005 Christian Schulte <schulte2005@users.sourceforge.net>
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
 *   $JOMC$
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
 * @version $JOMC$
 *
 * @see #edit(java.lang.String)
 */
public class LineEditor
{

    /** Editor to chain. */
    private LineEditor editor;

    /** Line separator. */
    private String lineSeparator;

    /** Creates a new {@code LineEditor} instance. */
    public LineEditor()
    {
        this( null, null );
    }

    /**
     * Creates a new {@code LineEditor} instance taking a string to use for separating lines.
     *
     * @param lineSeparator String to use for separating lines.
     */
    public LineEditor( final String lineSeparator )
    {
        this( null, lineSeparator );
    }

    /**
     * Creates a new {@code LineEditor} instance taking an editor to chain.
     *
     * @param editor The editor to chain.
     */
    public LineEditor( final LineEditor editor )
    {
        this( editor, null );
    }

    /**
     * Creates a new {@code LineEditor} instance taking an editor to chain and a string to use for separating lines.
     *
     * @param editor The editor to chain.
     * @param lineSeparator String to use for separating lines.
     */
    public LineEditor( final LineEditor editor, final String lineSeparator )
    {
        super();
        this.editor = editor;
        this.lineSeparator = lineSeparator;
    }

    /**
     * Gets the line separator of the editor.
     *
     * @return The line separator of the editor.
     */
    public final String getLineSeparator()
    {
        if ( this.lineSeparator == null )
        {
            this.lineSeparator = System.getProperty( "line.separator", "\n" );
        }

        return this.lineSeparator;
    }

    /**
     * Edits text.
     * <p>This method splits the given string into lines and passes every line to method {@code editLine} in order of
     * occurrence. On end of input, method {@code editLine} is called with a {@code null} argument.</p>
     *
     * @param text The text to edit or {@code null}.
     *
     * @return The edited text or {@code null}.
     *
     * @throws IOException if editing fails.
     */
    public final String edit( final String text ) throws IOException
    {
        String edited = text;

        if ( text != null )
        {
            final BufferedReader reader = new BufferedReader( new StringReader( text ) );
            final StringBuilder buf = new StringBuilder( text.length() );

            String line = null;
            while ( ( line = reader.readLine() ) != null )
            {
                final String replacement = this.editLine( line );
                if ( replacement != null )
                {
                    buf.append( replacement ).append( this.getLineSeparator() );
                }
            }

            final String replacement = this.editLine( null );
            if ( replacement != null )
            {
                buf.append( replacement );
            }

            edited = buf.toString();

            if ( this.editor != null )
            {
                edited = this.editor.edit( edited );
            }
        }

        return edited;
    }

    /**
     * Edits a line.
     *
     * @param line The line to edit or {@code null} indicating the end of input.
     *
     * @return The string to replace {@code line} with, or {@code null} to replace {@code line} with nothing.
     *
     * @throws IOException if editing fails.
     */
    protected String editLine( final String line ) throws IOException
    {
        return line;
    }

}
