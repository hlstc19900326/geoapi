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

import java.util.Arrays;
import java.util.Objects;
import java.util.ServiceLoader;
import org.opengis.util.Factory;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.jupiter.api.extension.RegisterExtension;


/**
 * Base class of all GeoAPI tests. All concrete subclasses need at least one {@linkplain Factory factory}
 * for instantiating the objects to test. The factories must be specified at construction time either
 * directly by a implementation-specific subclass, or indirectly by JUnit 5 dependency injection.
 * Dependency injections can be done by {@link org.junit.jupiter.api.extension.ParameterResolver}.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   2.2
 *
 * @see TestSuite
 */
public strictfp abstract class TestCase {
    /**
     * The service loader to use for loading {@link ImplementationDetails}.
     * Accesses to this field must be synchronized on itself.
     */
    private static ServiceLoader<ImplementationDetails> implementationDetails;

    /**
     * The class loader to use for searching implementations, or {@code null} for the default.
     *
     * @todo Need to find a way to remove this field, or at least to make it non-static.
     *       A possible way could be to create an JUnit extension with a method expecting
     *       a {@link org.junit.jupiter.api.TestInfo} argument,
     *       then search for a {@link TestSuite} instance in the chain of parents.
     */
    private static ClassLoader classLoader;

    /**
     * Sets the class loader to use for loading implementations. A {@code null} value restores
     * the default {@linkplain Thread#getContextClassLoader() context class loader}.
     *
     * @param loader  the class loader to use, or {@code null} for the default.
     */
    static synchronized void setClassLoader(final ClassLoader loader) {
        if (loader != classLoader) {
            classLoader = loader;
            implementationDetails = null;
        }
    }

    /**
     * Creates a service loader for the given type.
     */
    static synchronized <T> ServiceLoader<T> load(final Class<T> service) {
        return (classLoader == null)
               ? ServiceLoader.load(service)
               : ServiceLoader.load(service, classLoader);
    }

    /**
     * Returns the current {@link #implementationDetails} instance, creating a new one if needed.
     */
    static synchronized ServiceLoader<ImplementationDetails> getImplementationDetails() {
        if (implementationDetails == null) {
            implementationDetails = load(ImplementationDetails.class);
        }
        return implementationDetails;
    }

    /**
     * A JUnit extension watching test results and logging configuration tip when a test fails.
     * If the failure occurred in an optional part of the test, then this class logs an
     * information message for helping the developer to disable that test if (s)he wish.
     *
     * <p>Note: this field is not {@code private} because of JUnit 5 requirement.</p>
     *
     * @since 3.1
     */
    @RegisterExtension
    static final TestWatcher WATCHER = new TipLogger();

    /**
     * The factories used by the test case to execute, or an empty array if none.
     * This array is given at construction time and is not cloned.
     */
    private final Factory[] factories;

    /**
     * Provider of units of measurement (degree, metre, second, <i>etc</i>), never {@code null}.
     * The {@link Units#degree()}, {@link Units#metre() metre()} and other methods shall return
     * {@link javax.measure.Unit} instances compatible with the units created by the {@link Factory}
     * instances to be tested. Those {@code Unit<?>} instances depend on the Unit of Measurement (JSR-373)
     * implementation used by the factories.
     * If no units were {@linkplain org.opengis.test.Configuration.Key#units explicitly specified},
     * then the {@linkplain Units#getDefault() default units} are used.
     *
     * @since 3.1
     */
    protected final Units units;

    /**
     * The set of {@link Validator} instances to use for verifying objects conformance (never {@code null}).
     * If no validators were {@linkplain org.opengis.test.Configuration.Key#validators explicitly specified},
     * then the {@linkplain Validators#DEFAULT default validators} are used.
     *
     * @since 3.1
     */
    protected final ValidatorContainer validators;

    /**
     * A tip set by subclasses during the execution of some optional tests.
     * In case of optional test failure, if this field is non-null, then a message will be logged at the
     * {@link java.util.logging.Level#INFO} for giving some tips to the developer about how he can disable the test.
     *
     * <p><b>Example</b></p>
     * <blockquote><pre>&#64;Test
     *public void myTest() {
     *    if (isDerivativeSupported) {
     *        configurationTip = Configuration.Key.isDerivativeSupported;
     *        // Do some tests the require support of math transform derivatives.
     *    }
     *    configurationTip = null;
     *}</pre></blockquote>
     *
     * @since 3.1
     */
    protected transient Configuration.Key<Boolean> configurationTip;

    /**
     * Creates a new test which will use the given factories to execute.
     * The given factories are forwarded to {@link ImplementationDetails#configuration(Factory[])}
     * in order to decide which {@linkplain #validators} to use.
     *
     * @param factories  the factories to be used by the test. This array is not cloned.
     *
     * @since 3.1
     */
    @SuppressWarnings("LocalVariableHidesMemberVariable")       // Before field final values.
    protected TestCase(final Factory... factories) {
        Objects.requireNonNull(factories, "Given `factories` array can not be null.");
        this.factories = factories;
        Units units = null;
        ValidatorContainer validators = null;
        final ServiceLoader<ImplementationDetails> services = getImplementationDetails();
        synchronized (services) {
            for (final ImplementationDetails impl : services) {
                final Configuration config = impl.configuration(factories);
                if (config != null) {
                    if (units == null) {
                        units = config.get(Configuration.Key.units);
                    }
                    if (validators == null) {
                        validators = config.get(Configuration.Key.validators);
                    }
                    if (units != null && validators != null) {
                        break;          // We got all information will we looking for, no need to continue.
                    }
                }
            }
        }
        if (units == null) {
            units = Units.getDefault();
        }
        if (validators == null) {
            Objects.requireNonNull(validators = Validators.DEFAULT, "Validators.DEFAULT shall not be null.");
        }
        this.units = units;
        this.validators = validators;
    }

    /**
     * Returns booleans indicating whether the given operations are enabled. By default, every
     * operations are enabled. However if any {@link ImplementationDetails} instance found on the
     * classpath returns a {@linkplain ImplementationDetails#configuration configuration} map
     * having the value {@link Boolean#FALSE} for a given key, then the boolean value corresponding
     * to that key is set to {@code false}.
     *
     * @param  properties  the key for which the flags are wanted.
     * @return an array of the same length than {@code properties} in which each element at index
     *         <var>i</var> indicates whether the {@code properties[i]} test should be enabled.
     *
     * @since 3.1
     */
    @SafeVarargs
    protected final boolean[] getEnabledFlags(final Configuration.Key<Boolean>... properties) {
        final boolean[] isEnabled = new boolean[properties.length];
        Arrays.fill(isEnabled, true);
        final ServiceLoader<ImplementationDetails> services = getImplementationDetails();
        synchronized (services) {
            for (final ImplementationDetails impl : services) {
                final Configuration config = impl.configuration(factories);
                if (config != null) {
                    boolean atLeastOneTestIsEnabled = false;
                    for (int i=0; i<properties.length; i++) {
                        if (isEnabled[i]) {
                            final Boolean value = config.get(properties[i]);
                            if (value != null && !(isEnabled[i] = value)) {
                                continue;                       // Leave 'atLeastOneTestIsEnabled' unchanged.
                            }
                            atLeastOneTestIsEnabled = true;
                        }
                    }
                    if (!atLeastOneTestIsEnabled) {
                        break;                                  // No need to continue scanning the classpath.
                    }
                }
            }
        }
        return isEnabled;
    }

    /**
     * Returns information about the configuration of the test which has been run.
     * The content of this map depends on the {@code TestCase} subclass and on the
     * values returned by the {@link ImplementationDetails#configuration(Factory[])}
     * method for the factories being tested. For a description of the map content,
     * see any of the following subclasses:
     *
     * <ul>
     *   <li>{@link org.opengis.test.referencing.AffineTransformTest#configuration()}</li>
     *   <li>{@link org.opengis.test.referencing.ParameterizedTransformTest#configuration()}</li>
     *   <li>{@link org.opengis.test.referencing.AuthorityFactoryTest#configuration()}</li>
     *   <li>{@link org.opengis.test.referencing.gigs.AuthorityFactoryTestCase#configuration()}</li>
     * </ul>
     *
     * @return the configuration of the test being run, or an empty map if none.
     *         This method returns a modifiable map in order to allow subclasses to modify it.
     *
     * @see ImplementationDetails#configuration(Factory[])
     *
     * @since 3.1
     */
    public Configuration configuration() {
        final Configuration configuration = new Configuration();
        configuration.put(Configuration.Key.units,      units);
        configuration.put(Configuration.Key.validators, validators);
        return configuration;
    }
}
