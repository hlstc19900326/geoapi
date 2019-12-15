/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2011-2019 Open Geospatial Consortium, Inc.
 *    All Rights Reserved. http://www.opengeospatial.org/ogc/legal
 *
 *    Permission to use, copy, and modify this software and its documentation, with
 *    or without modification, for any purpose and without fee or royalty is hereby
 *    granted, provided that you include the following on ALL copies of the software
 *    and documentation or portions thereof, including modifications, that you make:
 *
 *    1. The full text of this NOTICE in a location viewable to users of the
 *       redistributed or derivative work.
 *    2. Notice of any changes or modifications to the OGC files, including the
 *       date changes were made.
 *
 *    THIS SOFTWARE AND DOCUMENTATION IS PROVIDED "AS IS," AND COPYRIGHT HOLDERS MAKE
 *    NO REPRESENTATIONS OR WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *    TO, WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE OR THAT
 *    THE USE OF THE SOFTWARE OR DOCUMENTATION WILL NOT INFRINGE ANY THIRD PARTY
 *    PATENTS, COPYRIGHTS, TRADEMARKS OR OTHER RIGHTS.
 *
 *    COPYRIGHT HOLDERS WILL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL OR
 *    CONSEQUENTIAL DAMAGES ARISING OUT OF ANY USE OF THE SOFTWARE OR DOCUMENTATION.
 *
 *    The name and trademarks of copyright holders may NOT be used in advertising or
 *    publicity pertaining to the software without specific, written prior permission.
 *    Title to copyright in this software and any associated documentation will at all
 *    times remain with copyright holders.
 */
package org.opengis.test.runner;

import java.util.Set;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.logging.Logger;
import java.util.Optional;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.opengis.test.TestSuite;

import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.engine.TestExecutionResult;

import static org.opengis.test.runner.ResultEntry.Status.*;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;


/**
 * Provides methods for running the tests.
 * This class does not depend on Swing widgets or on console program.
 * This class is <strong>not</strong> thread-safe.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   3.1
 */
final class Runner implements TestExecutionListener, TestWatcher {
    /**
     * The logger for this package.
     */
    static final Logger LOGGER = Logger.getLogger("org.opengis.test.runner");

    /**
     * The platform-specific line separator.
     */
    static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    /**
     * The result of each tests. All a access to this list must be synchronized.
     */
    private final Set<ResultEntry> entries;

    /**
     * The listeners to inform of any new entry. Note that those listeners will
     * <strong>not</strong> be notified from the Swing thread. It is listener
     * responsibility to be safe regarding the Swing events queue.
     */
    private ChangeListener[] listeners;

    /**
     * The single change event to reuse every time an event is fired.
     */
    private final ChangeEvent event;

    /**
     * Result of the last test executed. This field is set to {@code null} before each test.
     * Its value will be constructed by {@link TestWatcher} if possible because we can get
     * more information that way, or by {@link TestExecutionListener} as a fallback.
     */
    private transient ResultEntry result;

    /**
     * Creates a new, initially empty, runner.
     */
    Runner() {
        entries   = new LinkedHashSet<>();
        listeners = new ChangeListener[0];
        event     = new ChangeEvent(this);
    }

    /**
     * Sets the class loader to use for running the tests.
     *
     * @param files  the JAR files that contain the implementation to test.
     */
    static void setClassLoader(final File... files) throws MalformedURLException {
        final URL[] urls = new URL[files.length];
        for (int i=0; i<urls.length; i++) {
            urls[i] = files[i].toURI().toURL();
        }
        AllTests.setFactoryClassLoader(urls);
    }

   /**
     * Runs the JUnit tests.
     *
     * @todo We register {@link TestExecutionListener}, but it does not provide enough information
     *       (we need class and method name for creating a link to Javadoc, and we need the test
     *       instance for configuration tip). We can register {@link TestWatcher} for more information,
     *       but I have not yet found another way to register such watcher than enabling
     *       {@link java.util.ServiceLoader} (provider not implemented yet).
     */
    void run() {
        System.setProperty("junit.jupiter.extensions.autodetection.enabled", "true");
        final LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(TestSuite.class)).build();
        Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(this);
        launcher.execute(request);
    }

    /**
     * Returns all entries. This method returns a copy of the internal array.
     * Changes to this {@code ReportData} object will not be reflected in that array.
     */
    ResultEntry[] getEntries() {
        synchronized (entries) {
            return entries.toArray(new ResultEntry[entries.size()]);
        }
    }

    /**
     * Adds the result stored in the {@link #result} field.
     */
    private void storeResult() {
        final ChangeListener[] list;
        synchronized (entries) {
            entries.add(result);
            list = listeners;
            result = null;
        }
        for (final ChangeListener listener : list) {
            listener.stateChanged(event);
        }
    }

    /**
     * Invoked when a test is about to start. This method clears the {@link #result} field
     * for avoiding the risk to confuse the result of new test with result of previous test.
     */
    @Override
    public void executionStarted​(final TestIdentifier id) {
        result = null;
    }

    /**
     * Called when a test succeed. This method prepares a result with the {@code SUCCESS} status.
     */
    @Override
    public void testSuccessful​(final ExtensionContext event) {
        result = new ResultEntry(event, SUCCESS, null);
    }

    /**
     * Called when a test failed. This method prepares a result with the {@code FAILURE} status
     * and the stack trace.
     */
    @Override
    public void testFailed​(final ExtensionContext event, final Throwable cause) {
        result = new ResultEntry(event, FAILURE, cause);
    }

    /**
     * Called when a test has been skipped. This method prepares a result with the
     * {@code ASSUMPTION_NOT_MET} status and the stack trace.
     */
    @Override
    public void testAborted​(final ExtensionContext event, final Throwable cause) {
        result = new ResultEntry(event, ASSUMPTION_NOT_MET, cause);
    }

    /**
     * Called when a test will not be run, generally because a test method is annotated with
     * {@link org.junit.Ignore}. This method prepares a result with the {@code IGNORED} status.
     */
    @Override
    public void testDisabled​(final ExtensionContext event, final Optional<String> reason) {
        result = new ResultEntry(event, IGNORED, null);
    }

    /**
     * Called when a test will not be run, generally because a test method is annotated
     * with {@link org.junit.jupiter.api.Disabled}. If the result has not been created
     * by {@link #testAborted(ExtensionContext, Throwable)}, creates it now.
     */
    @Override
    public void executionSkipped​(final TestIdentifier id, final String reason) {
        if (result == null) {
            result = new ResultEntry(id, ResultEntry.Status.IGNORED, null);
        }
        storeResult();
    }

    /**
     * Called when a test completed. If the result has not been created by
     * {@link #testSuccessful(ExtensionContext)} or similar methods, creates it now.
     */
    @Override
    public void executionFinished(final TestIdentifier id, final TestExecutionResult r) {
        if (result == null) {
            result = new ResultEntry(id, ResultEntry.Status.fromJupiter(r.getStatus()), r.getThrowable().orElse(null));
        }
        storeResult();
    }

    /**
     * Adds a change listener to be invoked when new entries are added.
     * This is of interest mostly to swing widgets - we don't use this
     * listener for collecting new information.
     *
     * <p>Note that the listeners given to this method will <strong>not</strong> be notified from the
     * Swing thread. It is listener responsibility to be safe regarding the Swing events queue.</p>
     */
    void addChangeListener(final ChangeListener listener) {
        synchronized (entries) {
            ChangeListener[] list = listeners;
            final int length = list.length;
            list = Arrays.copyOf(list, length + 1);
            list[length] = listener;
            listeners = list;
        }
    }
}
