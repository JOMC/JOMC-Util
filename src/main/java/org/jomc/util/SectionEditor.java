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

import java.util.Stack;

/**
 * Interface to section based editing.
 * <p>Section based editing is a two phase process of parsing the editor's input into a corresponding hierarchy of
 * {@code Section} instances, followed by rendering the parsed sections to produce the output of the editor. Method
 * {@code getNextLine} returns {@code null} during parsing and the output of the editor on end of input, rendered by
 * calling method {@code getOutput}. Parsing is backed by methods {@code getSection} and {@code isSectionFinished}.</p>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class SectionEditor extends LineEditor
{

    /** Marker indicating the start of a section. */
    private static String DEFAULT_SECTION_START = "SECTION-START[";

    /** Marker indicating the end of a section. */
    private static String DEFAULT_SECTION_END = "SECTION-END";

    /** Stack of sections. */
    private Stack<Section> stack;

    /** Creates a new {@code SectionEditor} instance. */
    public SectionEditor()
    {
        super();
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

    @Override
    public final String getNextLine( String line )
    {
        if ( this.stack == null )
        {
            final Section root = new Section();
            root.setName( this.getClass().getName() );
            root.setMode( Section.MODE_HEAD );

            this.stack = new Stack<Section>();
            this.stack.push( root );
        }

        final Section current = this.stack.peek();

        if ( line != null )
        {
            Section child = this.getSection( line );
            if ( child != null )
            {
                child.setStartingLine( line );
                child.setMode( Section.MODE_HEAD );

                if ( current.getMode() == Section.MODE_HEAD )
                {
                    current.setMode( Section.MODE_TAIL );
                }
                else if ( current.getMode() == Section.MODE_TAIL )
                {
                    final StringBuffer tail = current.getTailContent();
                    current.setLevel( current.getLevel() + 1 );
                    current.getHeadContent().setLength( 0 );
                    current.getHeadContent().append( tail );
                    tail.setLength( 0 );
                }
                else
                {
                    throw new AssertionError();
                }

                current.getChildren().add( child );

                this.stack.push( child );
            }
            else if ( this.isSectionFinished( line ) )
            {
                this.stack.pop().setEndingLine( line );
            }
            else
            {
                current.addContent( line + "\n" );
            }

            line = null;
        }
        else
        {
            final Section root = this.stack.pop();

            if ( !this.stack.isEmpty() )
            {
                throw new IllegalArgumentException( root.getStartingLine() );
            }

            line = this.getOutput( root );
            this.stack = null;
        }

        return line;
    }

    /**
     * Parses the given line to mark the start of a new section.
     *
     * @param line The line to parse.
     *
     * @return The section starting at {@code line} or {@code null} if {@code line} does not mark the start of a new
     * section.
     */
    public Section getSection( final String line )
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
    public boolean isSectionFinished( final String line )
    {
        boolean end = false;

        if ( line != null )
        {
            end = line.indexOf( DEFAULT_SECTION_END ) != -1;
        }

        return end;
    }

    /**
     * Gets the output of the editor.
     * <p>This method returns the unchanged input by rendering the given sections. Overwriting classes may call this
     * method after having updated the given sections for rendering edited content.</p>
     *
     * @param root The root of the parsed sections to render the editor's output with.
     *
     * @return The output of the editor.
     *
     * @throws NullPointerException if {@code root} is {@code null}.
     *
     * @see Section#getSections()
     */
    public String getOutput( final Section root )
    {
        if ( root == null )
        {
            throw new NullPointerException( "root" );
        }

        class RecursionHelper
        {

            void render( final Section section, final StringBuffer buffer )
            {
                final int l = section.getLevel();
                for ( int i = 0; i <= l; i++ )
                {
                    section.setLevel( i );
                    if ( section.getStartingLine() != null )
                    {
                        buffer.append( section.getStartingLine() ).append( "\n" );
                    }

                    buffer.append( section.getHeadContent() );

                    for ( Section child : section.getChildren() )
                    {
                        this.render( child, buffer );
                    }

                    buffer.append( section.getTailContent() );

                    if ( section.getEndingLine() != null )
                    {
                        buffer.append( section.getEndingLine() ).append( "\n" );
                    }
                }
                section.setLevel( l );
            }

        }

        final StringBuffer output = new StringBuffer();
        new RecursionHelper().render( root, output );
        return output.toString();
    }

}
