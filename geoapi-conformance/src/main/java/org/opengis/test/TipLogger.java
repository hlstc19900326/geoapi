/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2019 Open Geospatial Consortium, Inc.
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
package org.opengis.test;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import java.lang.reflect.Method;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.jupiter.api.extension.ExtensionContext;


/**
 * A class watching test result and logging configuration tip when a test fails.
 * If the failure occurred in an optional part of the test, then this class logs an
 * information message for helping the developer to disable that test if (s)he wish.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   3.1
 */
final strictfp class TipLogger implements TestWatcher {
    /**
     * Invoked when a test fails. If the failure occurred in an optional part of
     * the set, logs an information message for helping the developer to disable
     * that test if he wish.
     */
    @Override
    public void testFailed​(final ExtensionContext context, final Throwable cause) {
        final Object test = context.getTestInstance().orElse(null);
        if (test instanceof TestCase) {
            final Configuration.Key<Boolean> tip = ((TestCase) test).configurationTip;
            if (tip != null) {
                final Logger logger = Logger.getLogger("org.opengis.test");
                final LogRecord record = new LogRecord(Level.INFO, "A test failure occurred while "
                        + "testing an optional feature. To skip that part of the test, set the '"
                        + tip.name() + "' boolean field to false or specify that value in the "
                        + "Configuration map.");
                record.setLoggerName(logger.getName());
                record.setSourceClassName(test.getClass().getSimpleName());
                record.setSourceMethodName(context.getTestMethod().map(Method::getName).orElse("<unknown>"));
                logger.log(record);
            }
        }
    }
}
