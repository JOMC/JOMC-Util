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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Section of text.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public class Section
{

    /**
     * Constant for the mode during parsing the head content of a section.
     */
    static final int MODE_HEAD = 1;

    /**
     * Constant for the mode during parsing the tail content of a section.
     */
    static final int MODE_TAIL = 2;

    /**
     * The current parsing mode.
     */
    private int mode = MODE_HEAD;

    /**
     * The name of this section.
     */
    private volatile String name;

    /**
     * The parsed head content of this section.
     */
    private volatile StringBuilder headContent;

    /**
     * The parsed tail content of this section.
     */
    private volatile StringBuilder tailContent;

    /**
     * Line marking the start of this section.
     */
    private volatile String startingLine;

    /**
     * Line marking the end of this section.
     */
    private volatile String endingLine;

    /**
     * The child sections of this section.
     */
    private volatile List<Section> sections;

    /**
     * Creates a new {@code Section} instance.
     */
    public Section()
    {
        super();
    }

    /**
     * Gets the name of this section.
     *
     * @return The name of this section or {@code null}.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Sets the name of this section.
     *
     * @param value The new name of this section or {@code null}.
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
        return this.startingLine;
    }

    /**
     * Sets the line marking the start of this section.
     *
     * @param value The new line marking the start of this section.
     */
    public void setStartingLine( final String value )
    {
        this.startingLine = value;
    }

    /**
     * Gets the line marking the end of this section.
     *
     * @return The line marking the end of this section.
     */
    public String getEndingLine()
    {
        return this.endingLine;
    }

    /**
     * Sets the line marking the end of this section.
     *
     * @param value The new line marking the end of this section.
     */
    public void setEndingLine( final String value )
    {
        this.endingLine = value;
    }

    /**
     * Gets the content of this section preceding any child section content.
     *
     * @return The content of this section preceding any child section content.
     */
    public StringBuilder getHeadContent()
    {
        if ( this.headContent == null )
        {
            this.headContent = new StringBuilder( 512 );
        }

        return this.headContent;
    }

    /**
     * Gets the content of this section succeeding any child section content.
     *
     * @return The content of this section succeeding any child section content.
     */
    public StringBuilder getTailContent()
    {
        if ( this.tailContent == null )
        {
            this.tailContent = new StringBuilder( 512 );
        }

        return this.tailContent;
    }

    /**
     * Gets the child sections of this section.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * sections property.
     * </p>
     *
     * @return A list of child sections of this section.
     */
    public List<Section> getSections()
    {
        if ( this.sections == null )
        {
            this.sections = new CopyOnWriteArrayList<Section>();
        }

        return this.sections;
    }

    /**
     * Gets a child section matching a given name.
     *
     * @param sectionName The name of the section to return.
     *
     * @return The first child section matching {@code sectionName} or {@code null}, if no such section is found.
     *
     * @throws NullPointerException if {@code sectionName} is {@code null}.
     */
    public Section getSection( final String sectionName )
    {
        if ( sectionName == null )
        {
            throw new NullPointerException( "sectionName" );
        }

        return this.getSection( this, sectionName );
    }

    private Section getSection( final Section current, final String sectionName )
    {
        Section section = null;

        if ( sectionName.equals( current.getName() ) )
        {
            section = current;
        }

        if ( section == null )
        {
            for ( final Section child : current.getSections() )
            {
                if ( sectionName.equals( child.getName() ) )
                {
                    section = child;
                    break;
                }

                final Section s = child.getSection( sectionName );

                if ( s != null )
                {
                    section = s;
                    break;
                }
            }
        }

        return section;
    }

    /**
     * Gets the parsing mode of the instance.
     *
     * @return The parsing mode of the instance.
     *
     * @see #MODE_HEAD
     * @see #MODE_TAIL
     */
    int getMode()
    {
        return this.mode;
    }

    /**
     * Sets the parsing mode of the instance.
     *
     * @param value The new parsing mode of the instance.
     *
     * @see #MODE_HEAD
     * @see #MODE_TAIL
     */
    void setMode( final int value )
    {
        this.mode = value;
    }

}
