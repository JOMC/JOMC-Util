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
package org.jomc.util.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import org.apache.commons.io.IOUtils;
import org.jomc.util.LineEditor;
import org.jomc.util.Section;
import org.jomc.util.SectionEditor;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test cases for class {@code org.jomc.util.SectionEditor}.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public class SectionEditorTest extends LineEditorTest
{

    /**
     * Constant to prefix relative resource names with.
     */
    private static final String ABSOLUTE_RESOURCE_NAME_PREFIX = "/org/jomc/util/test/";

    /**
     * Creates a new {@code SectionEditorTest} instance.
     */
    public SectionEditorTest()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SectionEditor getLineEditor()
    {
        return (SectionEditor) super.getLineEditor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SectionEditor newLineEditor()
    {
        final SectionEditor sectionEditor = new SectionEditor()
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

        return sectionEditor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SectionEditor newLineEditor( final LineEditor editor )
    {
        final SectionEditor sectionEditor = new SectionEditor( editor );
        return sectionEditor;
    }

    @Test
    public final void testSectionEditor() throws Exception
    {
        String test = this.getResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "TestSections.txt" );
        String expected =
            convertLineSeparator( this.getResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "TestSectionsEdited.txt" ) );

        String edited = this.getLineEditor().edit( test );

        System.out.println( "TEST:" );
        System.out.println( test );
        System.out.println( "EXPECTED:" );
        System.out.println( expected );
        System.out.println( "EDITED:" );
        System.out.println( edited );

        assertEquals( expected, edited );
        assertTrue( this.getLineEditor().isSectionPresent( "1" ) );
        assertTrue( this.getLineEditor().isSectionPresent( "1.1" ) );
        assertTrue( this.getLineEditor().isSectionPresent( "1.1.1" ) );
        assertTrue( this.getLineEditor().isSectionPresent( "1.1.1.1" ) );
        assertTrue( this.getLineEditor().isSectionPresent( "1.2" ) );
        assertTrue( this.getLineEditor().isSectionPresent( "1.3" ) );
        assertTrue( this.getLineEditor().isSectionPresent( "1.4" ) );
        assertTrue( this.getLineEditor().isSectionPresent( "2" ) );
        assertTrue( this.getLineEditor().isSectionPresent( "3" ) );
        assertTrue( this.getLineEditor().isSectionPresent( "4" ) );
        assertTrue( this.getLineEditor().isSectionPresent( "5" ) );
        assertTrue( this.getLineEditor().isSectionPresent( "6" ) );
        assertTrue( this.getLineEditor().isSectionPresent( "7" ) );
        assertTrue( this.getLineEditor().isSectionPresent( "8" ) );
        assertTrue( this.getLineEditor().isSectionPresent( "9" ) );
        assertTrue( this.getLineEditor().isSectionPresent( "10" ) );

        test = this.getResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "TestSectionsCont.txt" );
        expected =
            convertLineSeparator( this.getResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "TestSectionsContEdited.txt" ) );

        edited = this.getLineEditor().edit( test );

        System.out.println( "TEST:" );
        System.out.println( test );
        System.out.println( "EXPECTED:" );
        System.out.println( expected );
        System.out.println( "EDITED:" );
        System.out.println( edited );

        assertEquals( expected, edited );
        assertTrue( this.getLineEditor().isSectionPresent( "1" ) );
        assertTrue( this.getLineEditor().isSectionPresent( "1.1" ) );
        assertTrue( this.getLineEditor().isSectionPresent( "1.1.1" ) );
        assertTrue( this.getLineEditor().isSectionPresent( "2" ) );
        assertFalse( this.getLineEditor().isSectionPresent( "10" ) );

        this.assertUnmatchedSections( ABSOLUTE_RESOURCE_NAME_PREFIX + "UnmatchedSectionTest.txt" );
        this.assertUnmatchedSections( ABSOLUTE_RESOURCE_NAME_PREFIX + "UnmatchedSectionsTest.txt" );
        this.assertUnmatchedSections( ABSOLUTE_RESOURCE_NAME_PREFIX + "UnmatchedSubsectionTest.txt" );
        this.assertUnmatchedSections( ABSOLUTE_RESOURCE_NAME_PREFIX + "UnmatchedSubsectionsTest.txt" );
        this.assertUnmatchedSections( ABSOLUTE_RESOURCE_NAME_PREFIX + "MissingSectionStartTest.txt" );
        this.assertUnmatchedSections( ABSOLUTE_RESOURCE_NAME_PREFIX + "MissingSectionsStartTest.txt" );

        final StringBuilder testNoSections = new StringBuilder();
        final StringBuilder expectedNoSections = new StringBuilder();

        for ( int i = 1000; i >= 0; i-- )
        {
            testNoSections.append( "Hello editor.\n" );
            expectedNoSections.append( "Hello editor." ).append( this.getLineEditor().getLineSeparator() );
        }

        assertEquals( expectedNoSections.toString(), this.getLineEditor().edit( testNoSections.toString() ) );

        try
        {
            this.getLineEditor().edit( "SECTION-START[Test\nSECTION-END\n" );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }
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
        BufferedReader reader = null;
        boolean suppressExceptionOnClose = true;

        try
        {
            final StringBuilder b = new StringBuilder();
            reader = new BufferedReader( new StringReader( s ) );

            String line;
            while ( ( line = reader.readLine() ) != null )
            {
                b.append( line ).append( System.getProperty( "line.separator", "\n" ) );
            }

            suppressExceptionOnClose = false;
            return b.toString();
        }
        finally
        {
            try
            {
                if ( reader != null )
                {
                    reader.close();
                }
            }
            catch ( final IOException e )
            {
                if ( !suppressExceptionOnClose )
                {
                    throw e;
                }
            }
        }
    }

    private String getResource( final String resourceName ) throws IOException
    {

        InputStream in = null;
        boolean suppressExceptionOnClose = true;
        assertTrue( resourceName.startsWith( "/" ) );

        try
        {
            in = this.getClass().getResourceAsStream( resourceName );
            assertNotNull( "Resource '" + resourceName + "' not found.", in );
            final String content = IOUtils.toString( in, this.getResourceEncoding() );
            suppressExceptionOnClose = false;
            return content;
        }
        finally
        {
            try
            {
                if ( in != null )
                {
                    in.close();
                }
            }
            catch ( final IOException e )
            {
                if ( !suppressExceptionOnClose )
                {
                    throw e;
                }
            }
        }
    }

}
