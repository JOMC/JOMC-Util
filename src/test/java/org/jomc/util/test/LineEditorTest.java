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
package org.jomc.util.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.jomc.util.LineEditor;

/**
 * Test cases for the {@code LineEditor} class.
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
 * @version $Id$
 */
public class LineEditorTest extends TestCase
{

    /** The {@code LineEditor} instance tests are performed with. */
    private LineEditor testEditor;

    /**
     * Gets the {@code LineEditor} instance tests are performed. with.
     *
     * @return The {@code LineEditor} instance tests are performed. with.
     */
    public LineEditor getTestEditor()
    {
        if ( this.testEditor == null )
        {
            this.testEditor = this.newTestEditor();
        }

        return this.testEditor;
    }

    /**
     * Gets a new {@code LineEditor} instance to test.
     *
     * @return A new {@code LineEditor} instance to test.
     */
    protected LineEditor newTestEditor()
    {
        return new LineEditor();
    }

    public void testEdit() throws Exception
    {
        Assert.assertEquals( "", this.getTestEditor().edit( "" ) );
        Assert.assertEquals( this.getTestEditor().getLineSeparator(), this.getTestEditor().edit( "\n" ) );
    }

}
