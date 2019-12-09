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

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.Closeable;
import java.io.File;
import java.lang.reflect.Type;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;


/**
 * Reports API changes.
 * This base class reports the changes between GeoAPI 3.1 and 4.0.
 * Subclasses change API compatibility between GeoAPI 3.1 and 3.0.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 4.0
 * @since   3.1
 */
public strictfp class APIChangeReport implements Closeable {
    /**
     * The GeoAPI version used as a reference.
     */
    private final Release oldAPI;

    /**
     * The GeoAPI version to compare against the reference one.
     */
    private final Release newAPI;

    /**
     * Classes which were intentionally deleted. Such classes should be very rare (we usually try to deprecate
     * classes instead), but may happen when it was not possible to retrofit the deprecated class as a subtype
     * of the non-deprecated class.
     */
    private final Set<String> deletedClasses;

    /**
     * Incompatible changes which are accepted for this release.
     * For minor releases we should have very few of them - only if the previous type was clearly wrong.
     * For major releases the amount of incompatible changes may be greater.
     */
    private final Set<IncompatibleChange> acceptedIncompatibleChanges;

    /**
     * Lists the new methods and the methods having an incompatible change.
     *
     * @param  ignored  command-line parameters (ignored).
     * @throws IOException if an error occurred while reading a JAR file.
     * @throws ClassNotFoundException if a class that existed in the previous GeoAPI release
     *         has not been found in the new release.
     * @throws NoSuchMethodException if a method that existed in the previous GeoAPI release
     *         has not been found in the new release.
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public static void main(String[] ignored) throws IOException, ClassNotFoundException, NoSuchMethodException {
        try (final APIChangeReport c = new APIChangeReport("3.1-SNAPSHOT", "4.0-SNAPSHOT")) {
            for (final IncompatibleChange change : c.acceptedIncompatibleChanges) {
                System.out.println(change.method + "=I");
            }
            for (final IncompatibleChange change : c.createIncompatibleChangesList()) {
                System.out.println(change.method + "=MC");
            }
            for (final String change : c.listNewMethods()) {
                System.out.println(change + "=N");
            }
        }
    }

    /**
     * Creates a new API comparator between the given versions of GeoAPI.
     *
     * @param  oldVersion  the GeoAPI version used as a reference.
     * @param  newVersion  the GeoAPI version to compare against the reference one.
     * @throws MalformedURLException if an error occurred while building the URL to the JAR files.
     */
    APIChangeReport(final String oldVersion, final String newVersion) throws MalformedURLException {
        final File mavenRepository = new File(System.getProperty("user.home"), ".m2/repository");
        final ClassLoader parent = APIChangeReport.class.getClassLoader().getParent();
        oldAPI = new Release(mavenRepository, oldVersion, parent);
        newAPI = new Release(mavenRepository, newVersion, parent);
        if (newVersion.startsWith("3.1")) {
            deletedClasses = Collections.emptySet();
            acceptedIncompatibleChanges = IncompatibleChange.for31();
        } else if (newVersion.startsWith("4.0")) {
            deletedClasses = Collections.singleton("org.opengis.metadata.Obligation");
            acceptedIncompatibleChanges = IncompatibleChange.for40();
        } else {
            throw new IllegalStateException("Unsupported version: " + newVersion);
        }
    }

    /**
     * Returns the list of new methods found in the new GeoAPI version. This method is not used by
     * {@link #verifyCompatibility()} since adding new methods are compatible changes. This method
     * is used only by {@link #listChanges()} as part of the release notes.
     *
     * @return list of new methods, or {@code null} if none.
     */
    private List<String> listNewMethods() throws IOException, ClassNotFoundException, NoSuchMethodException {
        final List<String> newMethods = new ArrayList<>();
        for (final String className : newAPI.listClasses()) {
            if (className.indexOf('$') >= 0) {
                continue;                                                           // Skip inner classes.
            }
            final Class<?> newClass = newAPI.loadClass(className);
            if (!Modifier.isPublic(newClass.getModifiers())) {
                continue;                                                           // Skip non-public classes.
            }
            Class<?> oldClass;
            try {
                oldClass = oldAPI.loadClass(className);
            } catch (ClassNotFoundException e) {
                oldClass = null;                                                    // Will mark all methods as new.
            }
            for (final Method newMethod : newClass.getDeclaredMethods()) {
                if (!Modifier.isPublic(newMethod.getModifiers())) {
                    continue;                                                       // Skip non-public methods.
                }
                if (newMethod.isSynthetic()) {
                    continue;                                                       // Skip methods added by compiler.
                }
                final String methodName = newMethod.getName();
                if (oldClass != null) try {
                    final Class<?>[] paramTypes = oldAPI.getParameterTypes(newAPI, newMethod);
                    final Method oldMethod = oldClass.getMethod(methodName, paramTypes);
                    /*
                     * At this point we found that the method existed, so we will not report it has a new method.
                     * As a paranoiac check we verify that the parameters of the new method are the same than the
                     * parameters of the old method, but this check should actually never fail (if the parameters
                     * were not the same, the method would not have been found).
                     */
                    assertArrayEquals(paramTypes, oldMethod.getParameterTypes(), methodName);
                    continue;
                } catch (ClassNotFoundException | NoSuchMethodException e) {
                    // Ignore - will execute the same code as if 'oldClass' were null.
                }
                assertTrue(newMethods.add(newClass.getCanonicalName() + '.' + methodName));
            }
        }
        return newMethods;
    }

    /**
     * Returns the list of incompatible changes found between the old and the new GeoAPI version.
     * If this method is used for a JUnit test, then the expected result is an empty list.
     *
     * @return list of incompatible changes.
     */
    final List<IncompatibleChange> createIncompatibleChangesList() throws IOException, ClassNotFoundException, NoSuchMethodException {
        final List<IncompatibleChange> incompatibleChanges = new ArrayList<>();
        for (final String className : oldAPI.listClasses()) {
            final Class<?> oldClass = oldAPI.loadClass(className);
            if (!Modifier.isPublic(oldClass.getModifiers())) {
                continue;                                                         // Skip non-public classes.
            }
            if (deletedClasses.contains(className)) {
                continue;                                              // Skip intentionally deleted classes.
            }
            final Class<?> newClass = newAPI.loadClass(className);
            assumeFalse(oldClass.equals(newClass),
                        "Old and new classes have not been loaded by different class loaders. "
                      + "This may happen with Java 9 when at least one module is modularized. "
                      + "We need to revisit, maybe with the use of ModuleLayer.");
            /*
             * At this point we have a class from the previous GeoAPI release (oldClass) and its new version.
             * Now compare all public methods. We skip protected methods for simplicity, since we perform our
             * checks using Class.getMethod (we can not use Class.getDeclaredMethod because we want to search
             * in parent classes if needed). Note that arguments and return values which are GeoAPI types are
             * represented by different Class instances between the two GeoAPI version, so we need to convert
             * them using their class name.
             */
            for (final Method oldMethod : oldClass.getDeclaredMethods()) {
                if (!Modifier.isPublic(oldMethod.getModifiers())) {
                    continue;                                                     // Skip non-public methods.
                }
                final String methodName = oldMethod.getName();
                final Class<?>[] paramTypes = newAPI.getParameterTypes(oldAPI, oldMethod);
                final Method newMethod = newClass.getMethod(methodName, paramTypes);
                assertArrayEquals(paramTypes, newMethod.getParameterTypes(), methodName);   // Paranoiac check (should never fail).
                /*
                 * Compare generic arguments (if any). We require an exact match,
                 * including for parameterized types.
                 */
                final Type[] oldGPT = oldMethod.getGenericParameterTypes();
                final Type[] newGPT = newMethod.getGenericParameterTypes();
                assertEquals(oldGPT.length, newGPT.length, methodName);         // Paranoiac check (should never fail).
                for (int i=0; i<oldGPT.length; i++) {
                    final String oldType = Release.normalize(oldGPT[i].getTypeName());
                    final String newType = Release.normalize(newGPT[i].getTypeName());
                    if (!newType.equals(oldType)) {
                        final String lineSeparator = System.lineSeparator();
                        fail("Incompatible change in argument #" + (i+1) + " of "
                                + className + '.' + methodName + ':' + lineSeparator
                                + "    (old) " + oldType + " from " + oldAPI + lineSeparator
                                + "    (new) " + newType + " from " + newAPI + lineSeparator);
                    }
                }
                /*
                 * Compare generic return type, with a tolerance for a pre-defined set of incompatible changes.
                 * Any change not listed in the collection of accepted incompatible changes will be considered
                 * as an error.
                 */
                if (!oldMethod.isSynthetic()) {
                    final String oldType = Release.normalize(oldMethod.getGenericReturnType().getTypeName());
                    final String newType = Release.normalize(newMethod.getGenericReturnType().getTypeName());
                    if (!newType.equals(oldType)) {
                        final IncompatibleChange change = new IncompatibleChange(className + '.' + methodName, oldType, newType);
                        if (!acceptedIncompatibleChanges.remove(change)) {
                            incompatibleChanges.add(change);
                        }
                    }
                }
            }
        }
        if (!acceptedIncompatibleChanges.isEmpty()) {
            final String lineSeparator = System.lineSeparator();
            fail("The collection of \"accepted incompatible changes\" has not been fully used." + lineSeparator +
                 "Is this test configured with the right collection? Remaining items are:" + lineSeparator +
                 acceptedIncompatibleChanges + lineSeparator +
                 "JAR files are:" + lineSeparator +
                 "    old API: " + oldAPI + lineSeparator +
                 "    new API: " + newAPI + lineSeparator);
        }
        return incompatibleChanges;
    }

    /**
     * Releases the resources used by this test.
     *
     * @throws IOException if an error occurred while releasing the resources.
     */
    @Override
    @AfterEach
    public void close() throws IOException {
        oldAPI.close();
        newAPI.close();
    }
}
