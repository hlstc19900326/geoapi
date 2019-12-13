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
package org.opengis.geoapi;

import java.util.Collection;
import java.lang.reflect.Type;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Member;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.WildcardType;
import java.lang.reflect.Modifier;
import org.opengis.annotation.UML;
import org.opengis.util.CodeList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Verifies method signatures and annotations of GeoAPI interfaces.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   3.1
 */
public final strictfp class MethodSignatureTest extends SourceGenerator {
    /**
     * Returns {@code true} if the given field or method is public from a GeoAPI point of view.
     */
    private static boolean isPublic(final Member method) {
        return (method.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0;
    }

    /**
     * Verifies the values of all UML annotations.
     */
    @Test
    public void verifyUML() {
        for (final Class<?> c : Content.ALL.types()) {
            verifyUML(c);
            for (final Field m : c.getDeclaredFields()) {
                if (isPublic(m)) {
                    verifyUML(m);
                }
            }
            for (final Method m : c.getDeclaredMethods()) {
                if (isPublic(m)) {
                    verifyUML(m);
                }
            }
        }
    }

    /**
     * Verifies the UML annotation of the given class or method.
     */
    private static void verifyUML(final AnnotatedElement c) {
        final UML uml = c.getAnnotation(UML.class);
        if (uml != null) {
            final String identifier = uml.identifier().trim();
            if (identifier.isEmpty()) {
                fail("UML identifier is empty in " + c);
            }
            /*
             * As a policy, we do not declare version numbers which are equal to the default version.
             * This make easier for users to identify methods derived from older standards.
             * We make an exception for deprecated interfaces, when the version number is sometime
             * added in anticipation to a future upgrade.
             */
            final short version = uml.version();
            final short defaultVersion = uml.specification().defaultVersion();
            if (!c.isAnnotationPresent(Deprecated.class)) {
                if (version == defaultVersion) {
                    fail(c + ": " + identifier + " does not need explicit version number.");
                }
            } else if (!identifier.equals("MD_CharacterSetCode")) {
                /*
                 * We expect deprecated methods to be legacy from older standards.
                 * Consequently their version number shall not be the default one
                 * (except if we have only one version, as in old OGC documents).
                 *
                 * We skip MD_CharacterSetCode because that code list has not been
                 * removed by ISO 19115:2014 but GeoAPI nevertheless replaced it by
                 * java.nio.charset.Charset.
                 */
                if (defaultVersion != 1) {
                    if (version == 0) {
                        fail(c + ": " + identifier + " should have a version number.");
                    }
                }
            }
        }
    }

    /**
     * Verifies that code lists are final.
     */
    @Test
    public void verifyCodeLists() {
        for (final Class<?> c : Content.ALL.types()) {
            if (CodeList.class.isAssignableFrom(c) && c != CodeList.class) {
                assertTrue(Modifier.isFinal(c.getModifiers()), c.getName());
            }
        }
    }

    /**
     * Verifies that collections have parameterized type.
     * The upper bounds depend on whether the collection element type are final classes or not.
     */
    @Test
    public void verifyReturnTypes() {
        for (final Class<?> c : Content.ALL.types()) {
            final String pkg = c.getPackage().getName();                    // TODO: replace by getPackageName() in JDK9.
            if (pkg.startsWith("org.opengis.util"))        continue;        // Skipped for now.
            if (pkg.startsWith("org.opengis.referencing")) continue;        // Skipped for now.
            if (pkg.startsWith("org.opengis.parameter"))   continue;        // Skipped for now.
            if (pkg.startsWith("org.opengis.geometry"))    continue;        // Skipped for now.
            if (pkg.startsWith("org.opengis.feature"))     continue;        // Skipped for now.
            for (final Method m : c.getDeclaredMethods()) {
                if (isPublic(m) && Collection.class.isAssignableFrom(m.getReturnType())) {
                    final String description = m.toString();
                    Type type = m.getGenericReturnType();
                    /*
                     * Require all collections to be parameterized with exactly one parameter.
                     */
                    assertTrue(type instanceof ParameterizedType, description);
                    Type[] p = ((ParameterizedType) type).getActualTypeArguments();
                    assertEquals(1, p.length, description);
                    type = p[0];
                    /*
                     * Whether we allow covariant element type, i.e. <? extends T> instead of <T>.
                     * If T is an actual class or interface like org.opengis.metadata.Identifier,
                     * we verify its bound. Otherwise we skip the check.
                     */
                    final boolean isCovariant = (type instanceof WildcardType);
                    if (isCovariant) {
                        p = ((WildcardType) type).getUpperBounds();
                        assertEquals(1, p.length, description);
                        type = p[0];
                    }
                    if (type instanceof Class<?>) {
                        final Class<?> cl = (Class<?>) type;
                        boolean isFinal = Modifier.isFinal(cl.getModifiers());
                        if (!isFinal && !cl.isInterface()) {
                            // If no public constructor, consider as final.
                            isFinal = cl.getConstructors().length == 0;
                        }
                        if (isFinal == isCovariant) {
                            fail(description + ": " + (isFinal ? "does not need" : "should allow")
                                    + " covariant element type. The element type is " + type + '.');
                        }
                    }
                }
            }
        }
    }

    /**
     * Verifies that all optional methods have default implementation, and all mandatory methods are abstract.
     */
    @Test
    public void verifyDefaultMethods() {
        for (final Class<?> c : Content.ALL.types()) {
            final String pkg = c.getPackage().getName();                // TODO: replace by getPackageName() in JDK9.
            if (pkg.startsWith("org.opengis.util"))      continue;      // Skipped for now.
            if (pkg.startsWith("org.opengis.parameter")) continue;      // Skipped for now.
            if (pkg.startsWith("org.opengis.temporal"))  continue;      // Skipped for now.
            if (pkg.startsWith("org.opengis.geometry"))  continue;      // Skipped for now.
            if (c.isInterface() && !c.isAnnotationPresent(Deprecated.class)) {
                for (final Method m : c.getDeclaredMethods()) {
                    final UML uml;
                    if (isPublic(m) && !m.isBridge() && (uml = m.getAnnotation(UML.class)) != null) {
                        boolean isOptional;
                        if (m.isAnnotationPresent(Deprecated.class)) {
                            isOptional = true;
                        } else {
                            switch (uml.obligation()) {
                                case CONDITIONAL: continue;                     // Choice on a case-by-case basis.
                                case MANDATORY: isOptional = false; break;
                                case FORBIDDEN:
                                case OPTIONAL:  isOptional = true;  break;
                                default: throw new AssertionError(uml);
                            }
                            /*
                             * Special case for methods withoud default despite declared optional.
                             */
                            if (c == org.opengis.referencing.operation.Conversion.class) {
                                switch (m.getName()) {
                                    case "getSourceCRS":
                                    case "getTargetCRS": isOptional = false;
                                }
                            }
                            if (c == org.opengis.feature.IdentifiedType.class) {
                                switch (m.getName()) {
                                    case "getName": isOptional = false;
                                }
                            }
                            if (c == org.opengis.feature.FeatureType.class) {
                                switch (m.getName()) {
                                    case "getSuperTypes":
                                    case "getProperties": isOptional = false;
                                }
                            }
                        }
                        if (m.isDefault() != isOptional) {
                            fail(c.getSimpleName() + '.' + m.getName() + ": " + (isOptional
                                    ? "expected a default method."
                                    : "should not have default method."));
                        }
                    }
                }
            }
        }
    }
}
