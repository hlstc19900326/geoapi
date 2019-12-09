/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2005-2019 Open Geospatial Consortium, Inc.
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
package org.opengis.geoapi;

import java.util.List;
import java.net.MalformedURLException;
import java.io.IOException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Verifies the compatibility of a GeoAPI JAR file compared to its previous version.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 4.0
 * @since   3.1
 */
public final strictfp class CompatibilityTest extends APIChangeReport {
    /**
     * Creates a new API comparator.
     *
     * @throws MalformedURLException if an error occurred while building the URL to the JAR files.
     */
    public CompatibilityTest() throws MalformedURLException {
        super("3.0.1", "3.1-SNAPSHOT");
    }

    /**
     * Verifies that all changes compared to the latest GeoAPI release are compatible changes.
     * This test asserts that the list of incompatible changes is empty.
     * If not, that list will be formatted in the error message.
     *
     * @throws IOException if an error occurred while reading a JAR file.
     * @throws ClassNotFoundException if a class that existed in the previous GeoAPI release
     *         has not been found in the new release.
     * @throws NoSuchMethodException if a method that existed in the previous GeoAPI release
     *         has not been found in the new release.
     */
    @Test
    public void verifyCompatibility() throws IOException, ClassNotFoundException, NoSuchMethodException {
        final List<IncompatibleChange> incompatibleChanges = createIncompatibleChangesList();
        if (!incompatibleChanges.isEmpty()) {
            final String lineSeparator = System.lineSeparator();
            final StringBuilder buffer = new StringBuilder(240 * incompatibleChanges.size());
            for (final IncompatibleChange change : incompatibleChanges) {
                change.toString(buffer, lineSeparator);
            }
            fail(buffer.toString());
        }
    }
}
