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

import java.util.ArrayList;
import java.util.List;

/**
 * Section of text.
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
 * @version $Id$
 */
public class Section
{

    /** Constant for the mode during parsing the head content of a section. */
    static final int MODE_HEAD = 1;

    /** Constant for the mode during parsing the tail content of a section. */
    static final int MODE_TAIL = 2;

    /** The current parsing mode. */
    private int mode = MODE_HEAD;

    /** The name of this section. */
    private String name;

    /** The parsed head content of this section. */
    private StringBuilder headContent;

    /** The parsed tail content of this section. */
    private StringBuilder tailContent;

    /** Line marking the start of this section. */
    private String startingLine;

    /** Line marking the end of this section. */
    private String endingLine;

    /** The child sections of this section. */
    private List<Section> sections;

    /** Creates a new {@code Section} instance. */
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
            this.headContent = new StringBuilder();
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
            this.tailContent = new StringBuilder();
        }

        return this.tailContent;
    }

    /**
     * Gets the child sections of this section.
     *
     * @return A list of child sections of this section.
     */
    public List<Section> getSections()
    {
        if ( this.sections == null )
        {
            this.sections = new ArrayList<Section>();
        }

        return this.sections;
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
