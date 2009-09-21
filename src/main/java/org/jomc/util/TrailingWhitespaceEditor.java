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

/**
 * {@code LineEditor} removing trailing whitespace.
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
 * @version $Id$
 *
 * @see #edit(java.lang.String)
 */
public final class TrailingWhitespaceEditor extends LineEditor
{

    /** Creates a new {@code TrailingWhitespaceEditor} instance. */
    public TrailingWhitespaceEditor()
    {
        super();
    }

    /**
     * Creates a new {@code TrailingWhitespaceEditor} instance taking a string to use for separating lines.
     *
     * @param lineSeparator String to use for separating lines.
     */
    public TrailingWhitespaceEditor( final String lineSeparator )
    {
        super( lineSeparator );
    }

    /**
     * Creates a new {@code TrailingWhitespaceEditor} instance taking an editor to chain.
     *
     * @param editor The editor to chain.
     */
    public TrailingWhitespaceEditor( final LineEditor editor )
    {
        super( editor );
    }

    /**
     * Creates a new {@code TrailingWhitespaceEditor} instance taking an editor to chain and a string to use for separating lines.
     *
     * @param editor The editor to chain.
     * @param lineSeparator String to use for separating lines.
     */
    public TrailingWhitespaceEditor( final LineEditor editor, final String lineSeparator )
    {
        super( editor, lineSeparator );
    }

    @Override
    protected String editLine( final String line )
    {
        String replacement = line;

        if ( line != null )
        {
            StringBuilder whitespace = null;
            boolean sawWhitespace = false;
            final StringBuilder buf = new StringBuilder( line.length() );
            final char[] chars = line.toCharArray();

            for ( int i = 0; i < chars.length; i++ )
            {
                if ( Character.isWhitespace( chars[i] ) )
                {
                    if ( whitespace == null )
                    {
                        whitespace = new StringBuilder();
                    }

                    whitespace.append( chars[i] );
                    sawWhitespace = true;
                }
                else
                {
                    if ( sawWhitespace )
                    {
                        buf.append( whitespace );
                        sawWhitespace = false;
                        whitespace = null;
                    }
                    buf.append( chars[i] );
                }
            }

            replacement = buf.toString();
        }

        return replacement;
    }

}
