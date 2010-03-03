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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.jomc.util.Section;
import org.jomc.util.SectionEditor;

/**
 * Test cases for the {@code SectionEditor} class.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class SectionEditorTest extends LineEditorTest
{

    @Override
    protected SectionEditor newTestEditor()
    {
        return new SectionEditor();
    }

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

        String test = IOUtils.toString( this.getClass().getResourceAsStream( "TestSections.txt" ) );
        String expected = convertLineSeparator(
            IOUtils.toString( this.getClass().getResourceAsStream( "TestSectionsEdited.txt" ) ) );

        String edited = editor.edit( test );

        System.out.println( "TEST:" );
        System.out.println( test );
        System.out.println( "EXPECTED:" );
        System.out.println( expected );
        System.out.println( "EDITED:" );
        System.out.println( edited );

        Assert.assertEquals( expected, edited );
        Assert.assertTrue( editor.isSectionPresent( "1" ) );
        Assert.assertTrue( editor.isSectionPresent( "1.1" ) );
        Assert.assertTrue( editor.isSectionPresent( "1.1.1" ) );
        Assert.assertTrue( editor.isSectionPresent( "1.1.1.1" ) );
        Assert.assertTrue( editor.isSectionPresent( "1.2" ) );
        Assert.assertTrue( editor.isSectionPresent( "1.3" ) );
        Assert.assertTrue( editor.isSectionPresent( "1.4" ) );
        Assert.assertTrue( editor.isSectionPresent( "2" ) );
        Assert.assertTrue( editor.isSectionPresent( "3" ) );
        Assert.assertTrue( editor.isSectionPresent( "4" ) );
        Assert.assertTrue( editor.isSectionPresent( "5" ) );
        Assert.assertTrue( editor.isSectionPresent( "6" ) );
        Assert.assertTrue( editor.isSectionPresent( "7" ) );
        Assert.assertTrue( editor.isSectionPresent( "8" ) );
        Assert.assertTrue( editor.isSectionPresent( "9" ) );
        Assert.assertTrue( editor.isSectionPresent( "10" ) );

        test = IOUtils.toString( this.getClass().getResourceAsStream( "TestSectionsCont.txt" ) );
        expected = convertLineSeparator(
            IOUtils.toString( this.getClass().getResourceAsStream( "TestSectionsContEdited.txt" ) ) );

        edited = editor.edit( test );

        System.out.println( "TEST:" );
        System.out.println( test );
        System.out.println( "EXPECTED:" );
        System.out.println( expected );
        System.out.println( "EDITED:" );
        System.out.println( edited );

        Assert.assertEquals( expected, edited );
        Assert.assertTrue( editor.isSectionPresent( "1" ) );
        Assert.assertTrue( editor.isSectionPresent( "1.1" ) );
        Assert.assertTrue( editor.isSectionPresent( "1.1.1" ) );
        Assert.assertTrue( editor.isSectionPresent( "2" ) );
        Assert.assertFalse( editor.isSectionPresent( "10" ) );

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
            expectedNoSections.append( "Hello editor." ).append( this.getTestEditor().getLineSeparator() );
        }

        Assert.assertEquals( expectedNoSections.toString(), this.getTestEditor().edit( testNoSections.toString() ) );
    }

    public void assertUnmatchedSections( final String resourceName ) throws Exception
    {
        try
        {
            this.getTestEditor().edit( IOUtils.toString( this.getClass().getResourceAsStream( resourceName ) ) );
            Assert.fail( "Expected IOException not thrown for resource '" + resourceName + "'." );
        }
        catch ( IOException e )
        {
            Assert.assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }
    }

    public static String convertLineSeparator( final String s ) throws IOException
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

}
