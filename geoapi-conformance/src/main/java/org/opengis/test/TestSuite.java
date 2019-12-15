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
package org.opengis.test;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Arrays;
import java.util.Objects;
import org.opengis.util.Factory;
import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.test.referencing.gigs.EPSG;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.RegisterExtension;


/**
 * The suite of every tests defined in the GeoAPI conformance module.
 * All tests use {@link Factory} instances which depend on the implementation being tested.
 * By default {@code TestSuite} fetches the factory implementations with {@link java.util.ServiceLoader}.
 * However implementers can override this default mechanism by overriding the methods in this class or
 * by invoking setter methods at construction time.
 *
 * <p>Implementers can have some control on the tests (features to test, tolerance thresholds)
 * by providing an {@link ImplementationDetails} implementation and declaring its name in the
 * {@code META-INF/services/} directory.</p>
 *
 * <div class="note"><b>Example:</b>
 * the test suite below declares the CRS factory to use for all tests.
 *
 * <blockquote><pre>public class AllTests extends TestSuite {
 *    public AllTest() {
 *       setFactoryCandidates(CRSFactory.class, new MyFactory());
 *    }
 *}</pre></blockquote>
 * </div>
 *
 * @see ImplementationDetails
 * @see TestCase
 * @see Factory
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   3.1
 */
@SelectClasses({
  org.opengis.test.util.NameTest.class,
  org.opengis.test.referencing.ObjectFactoryTest.class,
  org.opengis.test.referencing.AffineTransformTest.class,
  org.opengis.test.referencing.ParameterizedTransformTest.class,
  org.opengis.test.referencing.AuthorityFactoryTest.class,
  org.opengis.test.referencing.gigs.GIGS2001.class,
  org.opengis.test.referencing.gigs.GIGS2002.class,
  org.opengis.test.referencing.gigs.GIGS2003.class,
  org.opengis.test.referencing.gigs.GIGS2004.class,
  org.opengis.test.referencing.gigs.GIGS2005.class,
  org.opengis.test.referencing.gigs.GIGS2006.class,
  org.opengis.test.referencing.gigs.GIGS2007.class,
  org.opengis.test.referencing.gigs.GIGS2008.class,
  org.opengis.test.referencing.gigs.GIGS2009.class,
  org.opengis.test.referencing.gigs.GIGS3002.class,
  org.opengis.test.referencing.gigs.GIGS3003.class,
  org.opengis.test.referencing.gigs.GIGS3004.class,
  org.opengis.test.referencing.gigs.GIGS3005.class,
  org.opengis.test.wkt.CRSParserTest.class
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public strictfp class TestSuite {
    /**
     * A JUnit extension for providing values to {@link TestCase} constructors expecting a factory.
     *
     * <p>Note: this field is not {@code private} because of JUnit 5 requirement.</p>
     */
    @RegisterExtension
    final FactoryProvider implementations;

    /**
     * Creates a new test suite. Subclasses can invoke {@link #setFactoryCandidates(Class, Object...)}
     * at construction time for declaring all their factories, or override the {@link #selectFactory
     * selectFactory(…)} method, or do nothing and rely on the automatic discovery mechanism.
     */
    protected TestSuite() {
        implementations = new FactoryProvider();
    }

    /**
     * Specifies the factory implementation to use for the given factory interface.
     * If the given array is empty, no test will be performed for the given factory type.
     * If the given array contains one or more factories, the factory to use is determined
     * by {@link #selectFactory selectFactory(…)} (it may be none).
     *
     * <p>If this method is never invoked for a given factory type, then {@code TestSuite}
     * will discover factory implementations with {@link java.util.ServiceLoader}.
     * This method is useful only if there is a need to bypass the service loader mechanism.</p>
     *
     * @param  <T>      the compile-time type of the {@code type} class argument.
     * @param  type     the factory interface for which implementations are specified.
     * @param  factory  the implementations to use for the given interface.
     */
    @SafeVarargs
    public final <T extends Factory> void setFactoryCandidates(final Class<T> type, final T... factory) {
        Objects.requireNonNull(type);
        implementations.setFactoryCandidates(type, Arrays.asList(factory.clone()));
        // TODO: replace Arrays.asList by List.of when allowed to use JDK9.
    }

    /**
     * Returns the class loader to use for searching {@link Factory} instances on the classpath.
     * The default implementation returns {@code getClass().getClassLoader()}. Subclasses may
     * need to override this method if the library to test is packaged a JAR file not accessible
     * by above class loader.
     *
     * @return the class loader to use for searching {@link Factory} instances on the classpath,
     *         or {@code null} for the class loader that {@link ServiceLoader} uses by default.
     */
    public ClassLoader getFactoryClassLoader() {
        return getClass().getClassLoader();
    }

    /**
     * Invoked once before any test is executed. This method notifies {@link TestCase} about the
     * class loader to use for {@link ServiceLoader} operations. This method may be removed in a
     * future version if we can remove the {@link TestCase#classLoader} static field.
     */
    @BeforeAll
    final void initialize() {
        TestCase.setClassLoader(getFactoryClassLoader());
    }

    /**
     * Resets {@link TestCase#classLoader} static field to its initial value after all tests finished.
     * This method may be removed in a future version if we can remove the {@link TestCase#classLoader}.
     */
    @AfterAll
    static void clear() {
        TestCase.setClassLoader(null);
    }

    /**
     * Provides dependency injection for each parameter expecting a factory.
     *
     * @author  Martin Desruisseaux (Geomatys)
     * @version 3.1
     * @since   3.1
     */
    private final class FactoryProvider implements ParameterResolver {
        /**
         * The factories specified explicitly by the implementers,
         * or the {@link java.util.ServiceLoader} to use for loading those factories.
         */
        private final Map<Class<? extends Factory>, Iterable<? extends Factory>> factories;

        /**
         * Creates a new
         */
        FactoryProvider() {
            factories = new HashMap<>();
        }

        /**
         * Sets the factories to use for the given factory type.
         */
        final <T extends Factory> void setFactoryCandidates(final Class<T> type, final List<T> factory) {
            synchronized (factories) {
                factories.put(type, factory);
            }
        }

        /**
         * Returns {@code true} if this provider is applicable to the given parameter.
         */
        @Override
        public boolean supportsParameter(final ParameterContext context, final ExtensionContext ec) {
            return Factory.class.isAssignableFrom(context.getParameter().getType());
        }

        /**
         * Provides a factory implementation for the given parameter. This method gets the list
         * of all factory candidates, then delegates to {@link #selectFactory selectFactory(…)}.
         */
        @Override
        public Object resolveParameter(final ParameterContext context, final ExtensionContext ec) {
            final Class<? extends Factory> type = context.getParameter().getType().asSubclass(Factory.class);
            Iterable<? extends Factory> factory;
            synchronized (factories) {
                factory = factories.computeIfAbsent(type, TestCase::load);
            }
            return selectFactory(context, factory);
        }
    }

    /**
     * Selects a factory to test. This method is invoked at {@link TestCase} construction time
     * when the test constructor requires one or more factory instances. The {@code factories}
     * argument provides all factories found on the classpath for the parameter type.
     * The default implementation performs the following actions:
     *
     * <ul>
     *   <li>If the parameter has {@link EPSG} annotation, discards all factories that are not
     *       {@link AuthorityFactory} for EPSG authority.</li>
     *   <li>Then return the first remaining factory.</li>
     * </ul>
     *
     * Subclasses can override. It is okay to ignore the given {@code factories} and return a
     * completely different instance.
     *
     * @param  context    information about the constructor parameter for which to supply a factory.
     * @param  factories  all factories found on the classpath for the parameter type.
     * @return the factory to use, or {@code null} if none.
     */
    protected Factory selectFactory(final ParameterContext context, final Iterable<? extends Factory> factories) {
        final String[] want = context.isAnnotated(EPSG.class) ? new String[] {"EPSG"} : null;
        synchronized (factories) {
            for (final Factory factory : factories) {
                if (want == null || isAuthorityFactory(factory, want)) {
                    return factory;
                }
            }
        }
        return null;
    }

    /**
     * Returns {@code true}, unless the given factory is an instance of {@link AuthorityFactory}
     * and the authority name is not the expected one.
     */
    static boolean isAuthorityFactory(final Factory factory, final String... names) {
        if (factory instanceof AuthorityFactory) {
            final Citation authority = ((AuthorityFactory) factory).getAuthority();
            if (authority != null) {
                if (isSelected(names, authority.getTitle())) {
                    return true;
                }
                final Collection<? extends InternationalString> titles = authority.getAlternateTitles();
                if (titles != null) {
                    for (final InternationalString candidate : titles) {
                        if (isSelected(names, candidate)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if the given {@link InternationalString} is equal to at least one
     * {@code names} element. Only the US locale and the default locale strings are compared.
     */
    @SuppressWarnings("StringEquality")
    private static boolean isSelected(final String[] names, final InternationalString i18n) {
        if (i18n == null) {
            return false;
        }
        for (final String name : names) {
            final String asString = i18n.toString(Locale.US);
            if (asString.trim().equalsIgnoreCase(name)) {
                return true;
            }
            final String asLocalized = i18n.toString();
            if (asLocalized != asString && asLocalized.trim().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
