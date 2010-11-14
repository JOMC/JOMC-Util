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
package org.jomc.util.test;

import org.junit.Test;
import java.util.Properties;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.apache.commons.io.IOUtils;
import org.jomc.util.Section;
import org.jomc.util.SectionEditor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test cases for class {@code org.jomc.util.SectionEditor}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class SectionEditorTest extends LineEditorTest
{

    /** Properties backing the instance. */
    private Properties testProperties;

    /** Creates a new {@code SectionEditorTest} instance. */
    public SectionEditorTest()
    {
        super();
    }

    /** {@code inheritDoc} */
    @Override
    public SectionEditor getLineEditor()
    {
        return (SectionEditor) super.getLineEditor();
    }

    /** {@code inheritDoc} */
    @Override
    protected SectionEditor newLineEditor()
    {
        return new SectionEditor();
    }

    @Test
    public void testSectionEditor() throws Exception
    {
        final SectionEditor editor = new SectionEditor()
        {

            @Override
            protected void editSection( final Section section ) throws IOException
            {
                super.editSection( section );

                if ( section.getName() != null )
                {
                    section.getHeadContent().append( section.getName() ).append( " Head" ).
                        append( this.getLineSeparator() );

                    section.getTailContent().append( section.getName() ).append( " Tail" ).
                        append( this.getLineSeparator() );

                }
            }

        };

        String test = this.getResource( "TestSections.txt" );
        String expected = convertLineSeparator( this.getResource( "TestSectionsEdited.txt" ) );
        String edited = editor.edit( test );

        System.out.println( "TEST:" );
        System.out.println( test );
        System.out.println( "EXPECTED:" );
        System.out.println( expected );
        System.out.println( "EDITED:" );
        System.out.println( edited );

        assertEquals( expected, edited );
        assertTrue( editor.isSectionPresent( "1" ) );
        assertTrue( editor.isSectionPresent( "1.1" ) );
        assertTrue( editor.isSectionPresent( "1.1.1" ) );
        assertTrue( editor.isSectionPresent( "1.1.1.1" ) );
        assertTrue( editor.isSectionPresent( "1.2" ) );
        assertTrue( editor.isSectionPresent( "1.3" ) );
        assertTrue( editor.isSectionPresent( "1.4" ) );
        assertTrue( editor.isSectionPresent( "2" ) );
        assertTrue( editor.isSectionPresent( "3" ) );
        assertTrue( editor.isSectionPresent( "4" ) );
        assertTrue( editor.isSectionPresent( "5" ) );
        assertTrue( editor.isSectionPresent( "6" ) );
        assertTrue( editor.isSectionPresent( "7" ) );
        assertTrue( editor.isSectionPresent( "8" ) );
        assertTrue( editor.isSectionPresent( "9" ) );
        assertTrue( editor.isSectionPresent( "10" ) );

        test = this.getResource( "TestSectionsCont.txt" );
        expected = convertLineSeparator( this.getResource( "TestSectionsContEdited.txt" ) );

        edited = editor.edit( test );

        System.out.println( "TEST:" );
        System.out.println( test );
        System.out.println( "EXPECTED:" );
        System.out.println( expected );
        System.out.println( "EDITED:" );
        System.out.println( edited );

        assertEquals( expected, edited );
        assertTrue( editor.isSectionPresent( "1" ) );
        assertTrue( editor.isSectionPresent( "1.1" ) );
        assertTrue( editor.isSectionPresent( "1.1.1" ) );
        assertTrue( editor.isSectionPresent( "2" ) );
        assertFalse( editor.isSectionPresent( "10" ) );

        this.assertUnmatchedSections( "UnmatchedSectionTest.txt" );
        this.assertUnmatchedSections( "UnmatchedSectionsTest.txt" );
        this.assertUnmatchedSections( "UnmatchedSubsectionTest.txt" );
        this.assertUnmatchedSections( "UnmatchedSubsectionsTest.txt" );
        this.assertUnmatchedSections( "MissingSectionStartTest.txt" );
        this.assertUnmatchedSections( "MissingSectionsStartTest.txt" );

        final StringBuilder testNoSections = new StringBuilder();
        final StringBuilder expectedNoSections = new StringBuilder();

        for ( int i = 1000; i >= 0; i-- )
        {
            testNoSections.append( "Hello editor.\n" );
            expectedNoSections.append( "Hello editor." ).append( this.getLineEditor().getLineSeparator() );
        }

        assertEquals( expectedNoSections.toString(), this.getLineEditor().edit( testNoSections.toString() ) );
    }

    private void assertUnmatchedSections( final String resourceName ) throws Exception
    {
        try
        {
            this.getLineEditor().edit( this.getResource( resourceName ) );
            fail( "Expected IOException not thrown for resource '" + resourceName + "'." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }
    }

    private static String convertLineSeparator( final String s ) throws IOException
    {
        final StringBuilder b = new StringBuilder();
        final BufferedReader r = new BufferedReader( new StringReader( s ) );

        String line;
        while ( ( line = r.readLine() ) != null )
        {
            b.append( line ).append( System.getProperty( "line.separator" ) );
        }

        r.close();
        return b.toString();
    }

    private String getTestProperty( final String key ) throws IOException
    {
        if ( this.testProperties == null )
        {
            this.testProperties = new java.util.Properties();
            final InputStream in = this.getClass().getResourceAsStream( "SectionEditorTest.properties" );
            this.testProperties.load( in );
            in.close();
        }

        final String value = this.testProperties.getProperty( key );
        assertNotNull( value );
        return value;
    }

    private String getResource( final String resourceName ) throws IOException
    {
        InputStream in = null;

        try
        {
            in = this.getClass().getResourceAsStream( resourceName );
            assertNotNull( "Resource '" + resourceName + "' not found.", in );
            return IOUtils.toString( in, this.getTestProperty( "resourceEncoding" ) );
        }
        finally
        {
            IOUtils.closeQuietly( in );
        }
    }

}
