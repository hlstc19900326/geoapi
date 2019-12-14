/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2008-2019 Open Geospatial Consortium, Inc.
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

import java.util.Collection;


/**
 * Extension to JUnit assertion methods.
 *
 * @deprecated Replaced by {@link Assertions} for consistency with JUnit 5 pattern.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   2.2
 */
@Deprecated
public final strictfp class Assert extends org.junit.jupiter.api.Assertions {
    /**
     * For subclasses constructors.
     */
    protected Assert() {
    }

    /**
     * Asserts that the given value is an instance of the given class. No tests are performed if
     * the type is {@code null}. If the type is not-null but the value is null, this is considered
     * as a failure.
     *
     * @param message       header of the exception message in case of failure, or {@code null} if none.
     * @param expectedType  the expected parent class of the value, or {@code null} if unrestricted.
     * @param value         the value to test, or {@code null} (which is a failure).
     */
    public static void assertInstanceOf(final String message, final Class<?> expectedType, final Object value) {
        Assertions.assertInstanceOf(expectedType, value, message);
    }

    /**
     * Asserts that the given integer value is positive, including zero.
     *
     * @param message  header of the exception message in case of failure, or {@code null} if none.
     * @param value   The value to test.
     */
    public static void assertPositive(final String message, final int value) {
        Assertions.assertPositive(value, message);
    }

    /**
     * Asserts that the given integer value is strictly positive, excluding zero.
     *
     * @param message  header of the exception message in case of failure, or {@code null} if none.
     * @param value    the value to test.
     */
    public static void assertStrictlyPositive(final String message, final int value) {
        Assertions.assertStrictlyPositive(value, message);
    }

    /**
     * Asserts that the given minimum and maximum values make a valid range. More specifically
     * asserts that if both values are non-null, then the minimum value is not greater than the
     * maximum value.
     *
     * @param <T>      the type of values being compared.
     * @param message  header of the exception message in case of failure, or {@code null} if none.
     * @param minimum  the lower bound of the range to test, or {@code null} if unbounded.
     * @param maximum  the upper bound of the range to test, or {@code null} if unbounded.
     */
    public static <T> void assertValidRange(final String message, final Comparable<T> minimum, final Comparable<T> maximum) {
        Assertions.assertValidRange(minimum, maximum, message);
    }

    /**
     * Asserts that the given minimum is smaller or equals to the given maximum.
     *
     * @param message  header of the exception message in case of failure, or {@code null} if none.
     * @param minimum  the lower bound of the range to test.
     * @param maximum  the upper bound of the range to test.
     */
    public static void assertValidRange(final String message, final int minimum, final int maximum) {
        Assertions.assertValidRange(minimum, maximum, message);
    }

    /**
     * Asserts that the given minimum is smaller or equals to the given maximum.
     * If one bound is or both bounds are {@linkplain Double#NaN NaN}, then the test fails.
     *
     * @param message  header of the exception message in case of failure, or {@code null} if none.
     * @param minimum  the lower bound of the range to test.
     * @param maximum  the upper bound of the range to test.
     */
    public static void assertValidRange(final String message, final double minimum, final double maximum) {
        Assertions.assertValidRange(minimum, maximum, message);
    }

    /**
     * Asserts that the given value is inside the given range. This method does <strong>not</strong>
     * test the validity of the given [{@code minimum} … {@code maximum}] range.
     *
     * @param <T>      the type of values being compared.
     * @param message  header of the exception message in case of failure, or {@code null} if none.
     * @param minimum  the lower bound of the range (inclusive), or {@code null} if unbounded.
     * @param maximum  the upper bound of the range (inclusive), or {@code null} if unbounded.
     * @param value    the value to test, or {@code null} (which is a failure).
     */
    public static <T> void assertBetween(final String message, final Comparable<T> minimum, final Comparable<T> maximum, T value) {
        Assertions.assertBetween(minimum, maximum, value, message);
    }

    /**
     * Asserts that the given value is inside the given range. This method does <strong>not</strong>
     * test the validity of the given [{@code minimum} … {@code maximum}] range.
     *
     * @param message  header of the exception message in case of failure, or {@code null} if none.
     * @param minimum  the lower bound of the range, inclusive.
     * @param maximum  the upper bound of the range, inclusive.
     * @param value    the value to test.
     */
    public static void assertBetween(final String message, final int minimum, final int maximum, final int value) {
        Assertions.assertBetween(minimum, maximum, value, message);
    }

    /**
     * Asserts that the given value is inside the given range. If the given {@code value} is
     * {@linkplain Double#NaN NaN}, then this test passes silently. This method does <strong>not</strong>
     * test the validity of the given [{@code minimum} … {@code maximum}] range.
     *
     * @param message  header of the exception message in case of failure, or {@code null} if none.
     * @param minimum  the lower bound of the range, inclusive.
     * @param maximum  the upper bound of the range, inclusive.
     * @param value    the value to test.
     */
    public static void assertBetween(final String message, final double minimum, final double maximum, final double value) {
        Assertions.assertBetween(minimum, maximum, value, message);
    }

    /**
     * Asserts that the given value is contained in the given collection. If the given collection
     * is null, then this test passes silently (a null collection is considered as "unknown", not
     * empty). If the given value is null, then the test passes only if the given collection
     * contains the null element.
     *
     * @param message     header of the exception message in case of failure, or {@code null} if none.
     * @param collection  the collection where to look for inclusion, or {@code null} if unrestricted.
     * @param value       the value to test for inclusion.
     */
    public static void assertContains(final String message, final Collection<?> collection, final Object value) {
        Assertions.assertContains(collection, value, message);
    }

    /**
     * @deprecated Renamed {@link Assertions#assertUnicodeIdentifierEquals(CharSequence, CharSequence, boolean, String)}
     * for avoiding confusion with the {@code Identifier} interface.
     *
     * @param message   header of the exception message in case of failure, or {@code null} if none.
     * @param expected  the expected character sequence.
     * @param value     the character sequence to compare.
     */
    @Deprecated
    public static void assertIdentifierEquals(final String message, final CharSequence expected, final CharSequence value) {
        Assertions.assertUnicodeIdentifierEquals(expected, value, true, message);
    }
}
