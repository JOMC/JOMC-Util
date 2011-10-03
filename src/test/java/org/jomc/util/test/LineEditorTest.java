/*
 *   Copyright (C) Christian Schulte, 2005-07-25
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
package org.jomc.util.test;

import org.jomc.util.LineEditor;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Test cases for class {@code org.jomc.util.LineEditor}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $JOMC$
 */
public class LineEditorTest
{

    /** Constant for the name of the system property holding the name of the encoding of resources backing the test. */
    private static final String RESOURCE_ENCODING_PROPERTY_NAME = "jomc.test.resourceEncoding";

    /** The {@code LineEditor} instance tests are performed with. */
    private LineEditor lineEditor;

    /** The name of the encoding to use when reading resources. */
    private String resourceEncoding;

    /** Creates a new {@code LineEditorTest} instance. */
    public LineEditorTest()
    {
        super();
    }

    /**
     * Gets the {@code LineEditor} instance tests are performed with.
     *
     * @return The {@code LineEditor} instance tests are performed with.
     *
     * @see #newLineEditor()
     */
    public LineEditor getLineEditor()
    {
        if ( this.lineEditor == null )
        {
            this.lineEditor = this.newLineEditor();
        }

        return this.lineEditor;
    }

    /**
     * Gets a new {@code LineEditor} instance to test.
     *
     * @return A new {@code LineEditor} instance to test.
     *
     * @see #getLineEditor()
     */
    protected LineEditor newLineEditor()
    {
        return new LineEditor();
    }

    /**
     * Gets the name of the encoding used when reading resources.
     *
     * @return The name of the encoding used when reading resources.
     *
     * @see #setResourceEncoding(java.lang.String)
     */
    public final String getResourceEncoding()
    {
        if ( this.resourceEncoding == null )
        {
            this.resourceEncoding = System.getProperty( RESOURCE_ENCODING_PROPERTY_NAME );
            assertNotNull( "Expected '" + RESOURCE_ENCODING_PROPERTY_NAME + "' system property not found.",
                           this.resourceEncoding );

        }

        return this.resourceEncoding;
    }

    /**
     * Sets the name of the encoding to use when reading resources.
     *
     * @param value The new name of the encoding to use when reading resources or {@code null}.
     *
     * @see #getResourceEncoding()
     */
    public final void setResourceEncoding( final String value )
    {
        this.resourceEncoding = value;
    }

    @Test
    public final void testLineEditor() throws Exception
    {
        assertEquals( "", this.getLineEditor().edit( "" ) );
        assertEquals( this.getLineEditor().getLineSeparator(), this.getLineEditor().edit( "\n" ) );
        assertNull( this.getLineEditor().edit( null ) );
    }

}
