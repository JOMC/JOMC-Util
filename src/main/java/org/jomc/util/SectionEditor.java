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

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Interface to section based editing.
 * <p>
 * Section based editing is a two phase process of parsing the editor's input into a corresponding hierarchy of
 * {@code Section} instances, followed by rendering the parsed sections to produce the output of the editor. Method
 * {@code editLine} returns {@code null} during parsing and the output of the editor on end of input, rendered by
 * calling method {@code getOutput}. Parsing is backed by methods {@code getSection} and {@code isSectionFinished}.
 * </p>
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 *
 * @see #edit(java.lang.String)
 */
public class SectionEditor extends LineEditor
{

    /**
     * Marker indicating the start of a section.
     */
    private static final String DEFAULT_SECTION_START = "SECTION-START[";

    /**
     * Marker indicating the end of a section.
     */
    private static final String DEFAULT_SECTION_END = "SECTION-END";

    /**
     * Stack of sections.
     */
    private Stack<Section> stack;

    /**
     * Mapping of section names to flags indicating presence of the section.
     */
    private final Map<String, Boolean> presenceFlags = new ConcurrentHashMap<>( 32 );

    /**
     * The {@code ExecutorService} of the instance.
     *
     * @since 1.10
     */
    private ExecutorService executorService;

    /**
     * Creates a new {@code SectionEditor} instance.
     */
    public SectionEditor()
    {
        this( null, null );
    }

    /**
     * Creates a new {@code SectionEditor} instance taking a string to use for separating lines.
     *
     * @param lineSeparator String to use for separating lines.
     */
    public SectionEditor( final String lineSeparator )
    {
        this( null, lineSeparator );
    }

    /**
     * Creates a new {@code SectionEditor} instance taking an editor to chain.
     *
     * @param editor The editor to chain.
     */
    public SectionEditor( final LineEditor editor )
    {
        this( editor, null );
    }

    /**
     * Creates a new {@code SectionEditor} instance taking an editor to chain and a string to use for separating lines.
     *
     * @param editor The editor to chain.
     * @param lineSeparator String to use for separating lines.
     */
    public SectionEditor( final LineEditor editor, final String lineSeparator )
    {
        super( editor, lineSeparator );
    }

    /**
     * Gets an {@code ExecutorService} used to edit sections in parallel.
     *
     * @return An {@code ExecutorService} used to edit sections in parallel or {@code null}, if no such service has
     * been provided by an application.
     *
     * @since 1.10
     *
     * @see #setExecutorService(java.util.concurrent.ExecutorService)
     */
    public final ExecutorService getExecutorService()
    {
        return this.executorService;
    }

    /**
     * Sets the {@code ExecutorService} to be used to edit sections in parallel.
     * <p>
     * The {@code ExecutorService} to be used to edit sections in parallel is an optional entity. If no such service is
     * provided by an application, no parallelization is performed. Configuration or lifecycle management of the given
     * {@code ExecutorService} is the responsibility of the application.
     * </p>
     *
     * @param value The {@code ExecutorService} to be used to edit sections in parallel or {@code null}, to disable any
     * parallelization.
     *
     * @since 1.10
     *
     * @see #getExecutorService()
     */
    public final void setExecutorService( final ExecutorService value )
    {
        this.executorService = value;
    }

    @Override
    protected final String editLine( final String line ) throws IOException
    {
        if ( this.stack == null )
        {
            final Section root = new Section();
            root.setMode( Section.MODE_HEAD );
            this.stack = new Stack<>();
            this.stack.push( root );
        }

        Section current = this.stack.peek();
        String replacement = null;

        if ( line != null )
        {
            final Section child = this.getSection( line );

            if ( child != null )
            {
                child.setStartingLine( line );
                child.setMode( Section.MODE_HEAD );

                if ( current.getMode() == Section.MODE_TAIL && current.getTailContent().length() > 0 )
                {
                    final Section s = new Section();
                    s.getHeadContent().append( current.getTailContent() );
                    current.getTailContent().setLength( 0 );
                    current.getSections().add( s );
                    current = s;
                    this.stack.push( current );
                }

                current.getSections().add( child );
                current.setMode( Section.MODE_TAIL );
                this.stack.push( child );
            }
            else if ( this.isSectionFinished( line ) )
            {
                final Section s = this.stack.pop();
                s.setEndingLine( line );

                if ( this.stack.isEmpty() )
                {
                    this.stack = null;
                    throw new IOException( getMessage( "unexpectedEndOfSection", this.getLineNumber() ) );
                }

                if ( this.stack.peek().getName() == null && this.stack.size() > 1 )
                {
                    this.stack.pop();
                }
            }
            else
            {
                switch ( current.getMode() )
                {
                    case Section.MODE_HEAD:
                        current.getHeadContent().append( line ).append( this.getLineSeparator() );
                        break;

                    case Section.MODE_TAIL:
                        current.getTailContent().append( line ).append( this.getLineSeparator() );
                        break;

                    default:
                        throw new AssertionError( current.getMode() );

                }
            }
        }
        else
        {
            final Section root = this.stack.pop();

            if ( !this.stack.isEmpty() )
            {
                this.stack = null;
                throw new IOException( getMessage( "unexpectedEndOfFile", this.getLineNumber(), root.getName() ) );
            }

            replacement = this.getOutput( root );
            this.stack = null;
        }

        return replacement;
    }

    /**
     * Parses the given line to mark the start of a new section.
     *
     * @param line The line to parse or {@code null}.
     *
     * @return The section starting at {@code line} or {@code null}, if {@code line} does not mark the start of a
     * section.
     *
     * @throws IOException if parsing fails.
     */
    protected Section getSection( final String line ) throws IOException
    {
        Section s = null;

        if ( line != null )
        {
            final int markerIndex = line.indexOf( DEFAULT_SECTION_START );

            if ( markerIndex != -1 )
            {
                final int startIndex = markerIndex + DEFAULT_SECTION_START.length();
                final int endIndex = line.indexOf( ']', startIndex );

                if ( endIndex == -1 )
                {
                    throw new IOException( getMessage( "sectionMarkerParseFailure", line, this.getLineNumber() ) );
                }

                s = new Section();
                s.setName( line.substring( startIndex, endIndex ) );
            }
        }

        return s;
    }

    /**
     * Parses the given line to mark the end of a section.
     *
     * @param line The line to parse or {@code null}.
     *
     * @return {@code true}, if {@code line} marks the end of a section; {@code false}, if {@code line} does not mark
     * the end of a section.
     *
     * @throws IOException if parsing fails.
     */
    protected boolean isSectionFinished( final String line ) throws IOException
    {
        return line != null && line.contains( DEFAULT_SECTION_END );
    }

    /**
     * Edits a section.
     * <p>
     * This method does not change any content by default. Overriding classes may use this method for editing
     * sections prior to rendering.
     * </p>
     *
     * @param section The section to edit.
     *
     * @throws NullPointerException if {@code section} is {@code null}.
     * @throws IOException if editing fails.
     */
    protected void editSection( final Section section ) throws IOException
    {
        if ( section == null )
        {
            throw new NullPointerException( "section" );
        }

        if ( section.getName() != null )
        {
            this.presenceFlags.put( section.getName(), Boolean.TRUE );
        }
    }

    /**
     * Creates tasks recursively for editing sections in parallel.
     *
     * @param section The section to edit recursively.
     * @param tasks The collection of tasks to run in parallel.
     *
     * @throws NullPointerException if {@code section} or {@code tasks} is {@code null}.
     * @throws IOException if editing fails.
     */
    private void editSections( final Section section, final Collection<EditSectionTask> tasks ) throws IOException
    {
        if ( section == null )
        {
            throw new NullPointerException( "section" );
        }
        if ( tasks == null )
        {
            throw new NullPointerException( "tasks" );
        }

        tasks.add( new EditSectionTask( section ) );
        for ( int i = 0, s0 = section.getSections().size(); i < s0; i++ )
        {
            this.editSections( section.getSections().get( i ), tasks );
        }
    }

    /**
     * Gets the output of the editor.
     * <p>
     * This method calls method {@code editSection()} for each section of the editor prior to rendering the sections
     * to produce the output of the editor.
     * </p>
     *
     * @param section The section to start rendering the editor's output with.
     *
     * @return The output of the editor.
     *
     * @throws NullPointerException if {@code section} is {@code null}.
     * @throws IOException if editing or rendering fails.
     */
    protected String getOutput( final Section section ) throws IOException
    {
        if ( section == null )
        {
            throw new NullPointerException( "section" );
        }

        try
        {
            this.presenceFlags.clear();
            final List<EditSectionTask> tasks = new LinkedList<>();
            this.editSections( section, tasks );

            if ( this.getExecutorService() != null && tasks.size() > 1 )
            {
                for ( final Future<Void> task : this.getExecutorService().invokeAll( tasks ) )
                {
                    task.get();
                }
            }
            else
            {
                for ( int i = 0, s0 = tasks.size(); i < s0; i++ )
                {
                    tasks.get( i ).call();
                }
            }

            return this.renderSections( section, new StringBuilder( 512 ) ).toString();
        }
        catch ( final CancellationException | InterruptedException e )
        {
            throw new IOException( getMessage( e ), e );
        }
        catch ( final ExecutionException e )
        {
            if ( e.getCause() instanceof IOException )
            {
                throw (IOException) e.getCause();
            }
            else if ( e.getCause() instanceof RuntimeException )
            {
                // The fork-join framework breaks the exception handling contract of Callable by re-throwing any
                // exception caught using a runtime exception.
                if ( e.getCause().getCause() instanceof IOException )
                {
                    throw (IOException) e.getCause().getCause();
                }
                else if ( e.getCause().getCause() instanceof RuntimeException )
                {
                    throw (RuntimeException) e.getCause().getCause();
                }
                else if ( e.getCause().getCause() instanceof Error )
                {
                    throw (Error) e.getCause().getCause();
                }
                else if ( e.getCause().getCause() instanceof Exception )
                {
                    // Checked exception not declared to be thrown by the Callable's 'call' method.
                    throw new UndeclaredThrowableException( e.getCause().getCause() );
                }
                else
                {
                    throw (RuntimeException) e.getCause();
                }
            }
            else if ( e.getCause() instanceof Error )
            {
                throw (Error) e.getCause();
            }
            else
            {
                // Checked exception not declared to be thrown by the Callable's 'call' method.
                throw new UndeclaredThrowableException( e.getCause() );
            }
        }
    }

    /**
     * Gets a flag indicating that the input of the editor contained a named section.
     *
     * @param sectionName The name of the section to test or {@code null}.
     *
     * @return {@code true}, if the input of the editor contained a section with name {@code sectionName};
     * {@code false}, if the input of the editor did not contain a section with name {@code sectionName}.
     */
    public boolean isSectionPresent( final String sectionName )
    {
        return sectionName != null && this.presenceFlags.get( sectionName ) != null
                   && this.presenceFlags.get( sectionName );

    }

    /**
     * Appends the content of a given section to a given buffer.
     *
     * @param section The section to render.
     * @param buffer The buffer to append the content of {@code section} to.
     *
     * @return {@code buffer} with content of {@code section} appended.
     */
    private StringBuilder renderSections( final Section section, final StringBuilder buffer )
    {
        if ( section.getStartingLine() != null )
        {
            buffer.append( section.getStartingLine() ).append( this.getLineSeparator() );
        }

        buffer.append( section.getHeadContent() );

        for ( int i = 0, s0 = section.getSections().size(); i < s0; i++ )
        {
            this.renderSections( section.getSections().get( i ), buffer );
        }

        buffer.append( section.getTailContent() );

        if ( section.getEndingLine() != null )
        {
            buffer.append( section.getEndingLine() ).append( this.getLineSeparator() );
        }

        return buffer;
    }

    private final class EditSectionTask implements Callable<Void>
    {

        private final Section section;

        EditSectionTask( final Section section )
        {
            super();
            this.section = section;
        }

        @Override
        public Void call() throws IOException
        {
            editSection( this.section );
            return null;
        }

    }

    private static String getMessage( final String key, final Object... arguments )
    {
        return MessageFormat.format( ResourceBundle.getBundle( SectionEditor.class.getName() ).getString( key ),
                                     arguments );

    }

    private static String getMessage( final Throwable t )
    {
        return t != null
                   ? t.getMessage() != null && t.getMessage().trim().length() > 0
                         ? t.getMessage()
                         : getMessage( t.getCause() )
                   : null;

    }

}
