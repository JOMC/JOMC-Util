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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Section of text.
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
 * @version $Id$
 */
public class Section
{

    /** The name of this section. */
    private String name;

    /** The level of this section. */
    private int level;

    /** The parsed head content of this section. */
    private Map<Integer, StringBuffer> headContent;

    /** The parsed tail content of this section. */
    private Map<Integer, StringBuffer> tailContent;

    /** Line marking the start of this section. */
    private Map<Integer, String> startingLine;

    /** Line marking the end of this section. */
    private Map<Integer, String> endingLine;

    /** The child sections of this section. */
    private Map<Integer, List<Section>> children;

    /**
     * Gets the name of this section.
     *
     * @return The name of this section.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Sets the name of this section.
     *
     * @param value The name of this section.
     */
    public void setName( final String value )
    {
        this.name = value;
    }

    /**
     * Gets the line marking the start of this section.
     *
     * @return The line marking the start of this section.
     */
    public String getStartingLine()
    {
        if ( this.startingLine == null )
        {
            this.startingLine = new HashMap<Integer, String>();
        }

        return this.startingLine.get( this.level );
    }

    /**
     * Sets the line marking the start of this section.
     *
     * @param value The line marking the start of this section.
     */
    public void setStartingLine( final String value )
    {
        if ( this.startingLine == null )
        {
            this.startingLine = new HashMap<Integer, String>();
        }

        this.startingLine.put( this.level, value );
    }

    /**
     * Gets the line marking the end of this section.
     *
     * @return The line marking the end of this section.
     */
    public String getEndingLine()
    {
        if ( this.endingLine == null )
        {
            this.endingLine = new HashMap<Integer, String>();
        }

        return this.endingLine.get( this.level );
    }

    /**
     * Sets the line marking the end of this section.
     *
     * @param value The line marking the end of this section.
     */
    public void setEndingLine( final String value )
    {
        if ( this.endingLine == null )
        {
            this.endingLine = new HashMap<Integer, String>();
        }

        this.endingLine.put( this.level, value );
    }

    /**
     * Gets the parsed head content of this section.
     *
     * @return The parsed head content of this section.
     */
    public StringBuffer getHeadContent()
    {
        if ( this.headContent == null )
        {
            this.headContent = new HashMap<Integer, StringBuffer>();
        }

        StringBuffer b = this.headContent.get( this.level );
        if ( b == null )
        {
            b = new StringBuffer();
            this.headContent.put( this.level, b );
        }

        return b;
    }

    /**
     * Gets the parsed tail content of this section.
     *
     * @return The parsed tail content of this section.
     */
    public StringBuffer getTailContent()
    {
        if ( this.tailContent == null )
        {
            this.tailContent = new HashMap<Integer, StringBuffer>();
        }

        StringBuffer b = this.tailContent.get( this.level );
        if ( b == null )
        {
            b = new StringBuffer();
            this.tailContent.put( this.level, b );
        }

        return b;
    }

    /**
     * Gets the child sections of this section.
     *
     * @return The child sections of this section.
     */
    public List<Section> getChildren()
    {
        if ( this.children == null )
        {
            this.children = new HashMap<Integer, List<Section>>();
        }

        List<Section> c = this.children.get( this.level );
        if ( c == null )
        {
            c = new LinkedList<Section>();
            this.children.put( this.level, c );
        }

        return c;
    }

    /**
     * Gets all sections recursively.
     *
     * @return A list of all sections collected recursively.
     */
    public List<Section> getSections()
    {
        class RecursionHelper
        {

            void collect( final Section section, final List<Section> list )
            {
                list.add( section );

                final int l = section.getLevel();
                for ( int i = 0; i <= l; i++ )
                {
                    section.setLevel( i );
                    for ( Section child : section.getChildren() )
                    {
                        this.collect( child, list );
                    }
                }
                section.setLevel( l );
            }

        }

        final List<Section> sections = new LinkedList<Section>();
        new RecursionHelper().collect( this, sections );
        return sections;
    }

    /** Constant for the mode when parsing the head of a section. */
    static final int MODE_HEAD = 1;

    /** Constant for the mode when parsing the tail of a section. */
    static final int MODE_TAIL = 2;

    /** The current parsing mode. */
    private int mode = MODE_HEAD;

    int getLevel()
    {
        return this.level;
    }

    void setLevel( final int level )
    {
        this.level = level;
    }

    int getMode()
    {
        return this.mode;
    }

    void setMode( final int value )
    {
        this.mode = value;
    }

    void addContent( final String content )
    {
        if ( this.mode == MODE_HEAD )
        {
            this.getHeadContent().append( content );
        }
        else if ( this.mode == MODE_TAIL )
        {
            this.getTailContent().append( content );
        }
    }

}
