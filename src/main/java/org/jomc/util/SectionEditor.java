/*
 *   Copyright (c) 2009 The JOMC Project
 *   Copyright (c) 2005 Christian Schulte <cs@jomc.org>
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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Stack;

/**
 * Interface to section based editing.
 * <p>Section based editing is a two phase process of parsing the editor's input into a corresponding hierarchy of
 * {@code Section} instances, followed by rendering the parsed sections to produce the output of the editor. Method
 * {@code editLine} returns {@code null} during parsing and the output of the editor on end of input, rendered by
 * calling method {@code getOutput}. Parsing is backed by methods {@code getSection} and {@code isSectionFinished}.</p>
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
 * @version $Id$
 *
 * @see #edit(java.lang.String)
 */
public class SectionEditor extends LineEditor
{

    /** Marker indicating the start of a section. */
    private static final String DEFAULT_SECTION_START = "SECTION-START[";

    /** Marker indicating the end of a section. */
    private static final String DEFAULT_SECTION_END = "SECTION-END";

    /** Stack of sections. */
    private Stack<Section> stack;

    /** Creates a new {@code SectionEditor} instance. */
    public SectionEditor()
    {
        super();
    }

    /**
     * Creates a new {@code SectionEditor} instance taking a string to use for separating lines.
     *
     * @param lineSeparator String to use for separating lines.
     */
    public SectionEditor( final String lineSeparator )
    {
        super( lineSeparator );
    }

    /**
     * Creates a new {@code SectionEditor} instance taking an editor to chain.
     *
     * @param editor The editor to chain.
     */
    public SectionEditor( final LineEditor editor )
    {
        super( editor );
    }

    /**
     * Creates a new {@code SectionEditor} instance taking an editor to chain and a string to use for separating lines.
     *
     * @param editor The editor to chain.
     * @param lineSeparator String to use for separating lines.
     */
    public SectionEditor( final LineEditor editor, final String lineSeparator )
    {
        super( editor, lineSeparator );
    }

    @Override
    protected final String editLine( final String line ) throws IOException
    {
        if ( this.stack == null )
        {
            final Section root = new Section();
            root.mode = Section.MODE_HEAD;
            this.stack = new Stack<Section>();
            this.stack.push( root );
        }

        Section current = this.stack.peek();
        String replacement = null;

        if ( line != null )
        {
            final Section child = this.getSection( line );

            if ( child != null )
            {
                child.setStartingLine( line );
                child.mode = Section.MODE_HEAD;

                if ( current.mode == Section.MODE_TAIL && current.getTailContent().length() > 0 )
                {
                    final Section s = new Section();
                    s.getHeadContent().append( current.getTailContent() );
                    current.getTailContent().setLength( 0 );
                    current.getSections().add( s );
                    current = s;
                    this.stack.push( current );
                }

                current.getSections().add( child );
                current.mode = Section.MODE_TAIL;
                this.stack.push( child );
            }
            else if ( this.isSectionFinished( line ) )
            {
                this.stack.pop().setEndingLine( line );
                if ( this.stack.peek().getName() == null && this.stack.size() > 1 )
                {
                    this.stack.pop();
                }
            }
            else
            {
                if ( current.mode == Section.MODE_HEAD )
                {
                    current.getHeadContent().append( line ).append( this.getLineSeparator() );
                }
                else if ( current.mode == Section.MODE_TAIL )
                {
                    current.getTailContent().append( line ).append( this.getLineSeparator() );
                }
            }
        }
        else
        {
            final Section root = this.stack.pop();

            if ( !this.stack.isEmpty() )
            {
                this.stack = null;
                throw new IOException( this.getMessage( "unexpectedEndOfSection", new Object[]
                    {
                        root.getName() == null ? "/" : root.getName()
                    } ) );

            }

            replacement = this.getOutput( root );
            this.stack = null;
        }

        return replacement;
    }

    /**
     * Parses the given line to mark the start of a new section.
     *
     * @param line The line to parse.
     *
     * @return The section starting at {@code line} or {@code null} if {@code line} does not mark the start of a
     * section.
     */
    protected Section getSection( final String line )
    {
        Section s = null;

        if ( line != null )
        {
            final int startIndex = line.indexOf( DEFAULT_SECTION_START );
            if ( startIndex != -1 )
            {
                final String name = line.substring( startIndex + DEFAULT_SECTION_START.length(),
                                                    line.indexOf( ']', startIndex + DEFAULT_SECTION_START.length() ) );

                s = new Section();
                s.setName( name );
            }
        }

        return s;
    }

    /**
     * Parses the given line to mark the end of a section.
     *
     * @param line The line to parse.
     *
     * @return {@code true} if {@code line} marks the end of a section; {@code false} if {@code line} does not mark the
     * end of a section.
     */
    protected boolean isSectionFinished( final String line )
    {
        return line != null && line.indexOf( DEFAULT_SECTION_END ) != -1;
    }

    /**
     * Edits a section.
     * <p>This method does not change any content by default. Overriding classes may use this method for editing
     * sections prior to rendering.</p>
     *
     * @param section The section to edit.
     *
     * @throws NullPointerException if {@code section} is {@code null}.
     * @throws IOException if editing fails.
     */
    protected void editSection( final Section section ) throws IOException
    {
        if ( section == null )
        {
            throw new NullPointerException( "section" );
        }
    }

    /**
     * Edits a section recursively.
     *
     * @param section The section to edit recursively.
     *
     * @throws NullPointerException if {@code section} is {@code null}.
     * @throws IOException if editing fails.
     */
    private void editSections( final Section section ) throws IOException
    {
        if ( section == null )
        {
            throw new NullPointerException( "section" );
        }

        this.editSection( section );
        for ( Section child : section.getSections() )
        {
            this.editSections( child );
        }
    }

    /**
     * Gets the output of the editor.
     * <p>This method calls method {@code editSection()} for each section of the editor prior to rendering the sections
     * to produce the output of the editor.</p>
     * 
     * @param section The section to start rendering the editor's output with.
     *
     * @return The output of the editor.
     *
     * @throws NullPointerException if {@code section} is {@code null}.
     * @throws IOException if editing or rendering fails.
     */
    protected String getOutput( final Section section ) throws IOException
    {
        if ( section == null )
        {
            throw new NullPointerException( "section" );
        }

        this.editSections( section );
        return this.renderSections( section, new StringBuilder() ).toString();
    }

    /**
     * Appends the content of a given section to a given buffer.
     *
     * @param section The section to render.
     * @param buffer The buffer to append the content of {@code section} to.
     *
     * @return {@code buffer} with content of {@code section} appended.
     */
    private StringBuilder renderSections( final Section section, final StringBuilder buffer )
    {
        if ( section.getStartingLine() != null )
        {
            buffer.append( section.getStartingLine() ).append( this.getLineSeparator() );
        }

        buffer.append( section.getHeadContent() );

        for ( Section child : section.getSections() )
        {
            this.renderSections( child, buffer );
        }

        buffer.append( section.getTailContent() );

        if ( section.getEndingLine() != null )
        {
            buffer.append( section.getEndingLine() ).append( this.getLineSeparator() );
        }

        return buffer;
    }

    private String getMessage( final String key, final Object arguments )
    {
        return new MessageFormat( ResourceBundle.getBundle( SectionEditor.class.getName().
            replace( '.', '/' ) ).getString( key ) ).format( arguments );

    }

}
