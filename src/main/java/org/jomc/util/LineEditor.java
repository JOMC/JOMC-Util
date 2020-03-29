/*
 *   Copyright (C) 2005 Christian Schulte <cs@schulte.it>
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
 *   THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 *   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 *   AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 *   THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *   INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *   NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *   DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *   THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *   THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *   $JOMC$
 *
 */
package org.jomc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Objects;
import java.util.Optional;

/**
 * Interface to line based editing.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 *
 * @see #edit(java.lang.String)
 */
public class LineEditor
{

    /**
     * Editor to chain.
     */
    private final LineEditor editor;

    /**
     * Line separator.
     */
    private String lineSeparator;

    /**
     * Current line number.
     *
     * @since 1.2
     */
    private long lineNumber;

    /**
     * Creates a new {@code LineEditor} instance.
     */
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
        this.lineNumber = 0L;
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
     * Gets the current line number.
     *
     * @return The current line number.
     *
     * @since 1.2
     */
    public final long getLineNumber()
    {
        return this.lineNumber;
    }

    /**
     * Edits text.
     * <p>
     * This method splits the given string into lines and passes every line to method {@code editLine} in order of
     * occurrence. On end of input, method {@code editLine} is called with a {@code null} argument.
     * </p>
     *
     * @param text The text to edit.
     *
     * @return The output of the editor or no value, if the editor did not produce output.
     *
     * @throws NullPointerException if {@code text} is {@code null}.
     * @throws IOException if editing fails.
     */
    public final Optional<String> edit( final String text ) throws IOException
    {
        String edited = Objects.requireNonNull( text, "text" );
        this.lineNumber = 0L;

        final StringBuilder buf = new StringBuilder( edited.length() + 16 );
        boolean appended = false;

        if ( edited.length() > 0 )
        {
            try ( final BufferedReader reader = new BufferedReader( new StringReader( edited ) ) )
            {
                String line;
                while ( ( line = reader.readLine() ) != null )
                {
                    this.lineNumber++;
                    final String replacement = this.editLine( line );
                    if ( replacement != null )
                    {
                        buf.append( replacement ).append( this.getLineSeparator() );
                        appended = true;
                    }
                }
            }
        }
        else
        {
            this.lineNumber++;
            final String replacement = this.editLine( edited );
            if ( replacement != null )
            {
                buf.append( replacement ).append( this.getLineSeparator() );
                appended = true;
            }
        }

        final String replacement = this.editLine( null );
        if ( replacement != null )
        {
            buf.append( replacement );
            appended = true;
        }

        edited = appended ? buf.toString() : null;

        Optional<String> output = Optional.ofNullable( edited );

        if ( this.editor != null )
        {
            output = this.editor.edit( edited );
        }

        return output;
    }

    /**
     * Edits a line.
     *
     * @param line The line to edit or {@code null}, indicating the end of input.
     *
     * @return The string to replace {@code line} with or {@code null}, to replace {@code line} with nothing.
     *
     * @throws IOException if editing fails.
     */
    protected String editLine( final String line ) throws IOException
    {
        return line;
    }

}
