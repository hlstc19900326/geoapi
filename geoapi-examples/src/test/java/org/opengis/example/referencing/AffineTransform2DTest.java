/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    This file is hereby placed into the Public Domain.
 *    This means anyone is free to do whatever they wish with this file.
 */
package org.opengis.example.referencing;

import org.junit.jupiter.api.Test;
import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.test.referencing.AffineTransformTest;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Tests {@link AffineTransform2D} using the
 * <code><a href="http://www.geoapi.org/conformance/index.html">geoapi-conformance</a></code>
 * module.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   3.1
 */
public strictfp class AffineTransform2DTest extends AffineTransformTest {
    /**
     * Creates a new test case.
     */
    public AffineTransform2DTest() {
        super(new SimpleTransformFactory());
    }

    /**
     * Invoked after every tests in order to ensure that the transform created by the factory
     * is of the expected type. This method requires that the transform class is exactly the
     * {@code AffineTransform2D} class - not a subclass.
     */
    private void ensureExpectedTransformClass() {
        assertEquals(AffineTransform2D.class, transform.getClass(), "Unexpected transform instance.");
    }

    /**
     * Runs the test, then verify that the result is an {@link AffineTransform2D} instance.
     */
    @Test
    @Override
    public void testIdentity2D() throws FactoryException, TransformException {
        super.testIdentity2D();
        ensureExpectedTransformClass();
    }

    /**
     * Runs the test, then verify that the result is an {@link AffineTransform2D} instance.
     */
    @Test
    @Override
    public void testAxisSwapping2D() throws FactoryException, TransformException {
        super.testAxisSwapping2D();
        ensureExpectedTransformClass();
    }

    /**
     * Runs the test, then verify that the result is an {@link AffineTransform2D} instance.
     */
    @Test
    @Override
    public void testSouthOrientated2D() throws FactoryException, TransformException {
        super.testSouthOrientated2D();
        ensureExpectedTransformClass();
    }

    /**
     * Runs the test, then verify that the result is an {@link AffineTransform2D} instance.
     */
    @Test
    @Override
    public void testTranslatation2D() throws FactoryException, TransformException {
        super.testTranslatation2D();
        ensureExpectedTransformClass();
    }

    /**
     * Runs the test, then verify that the result is an {@link AffineTransform2D} instance.
     */
    @Test
    @Override
    public void testUniformScale2D() throws FactoryException, TransformException {
        super.testUniformScale2D();
        ensureExpectedTransformClass();
    }

    /**
     * Runs the test, then verify that the result is an {@link AffineTransform2D} instance.
     */
    @Test
    @Override
    public void testGenericScale2D() throws FactoryException, TransformException {
        super.testGenericScale2D();
        ensureExpectedTransformClass();
    }

    /**
     * Runs the test, then verify that the result is an {@link AffineTransform2D} instance.
     */
    @Test
    @Override
    public void testRotation2D() throws FactoryException, TransformException {
        super.testRotation2D();
        ensureExpectedTransformClass();
    }

    /**
     * Runs the test, then verify that the result is an {@link AffineTransform2D} instance.
     */
    @Test
    @Override
    public void testGeneral() throws FactoryException, TransformException {
        super.testGeneral();
        ensureExpectedTransformClass();
    }

    /**
     * Declares that our implementation can not invert such transform, then runs the test.
     */
    @Test
    @Override
    public void testDimensionReduction() throws FactoryException, TransformException {
        isInverseTransformSupported = false;
        super.testDimensionReduction();
    }
}
