/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    This file is hereby placed into the Public Domain.
 *    This means anyone is free to do whatever they wish with this file.
 */
package org.opengis.example.referencing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.test.referencing.AffineTransformTest;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests {@link ProjectiveTransform} using the
 * <code><a href="http://www.geoapi.org/conformance/index.html">geoapi-conformance</a></code>
 * module.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   3.1
 */
public strictfp class ProjectiveTransformTest extends AffineTransformTest {
    /**
     * Creates a new test case.
     */
    public ProjectiveTransformTest() {
        super(new SimpleTransformFactory() {
            @Override // Prevent SimpleTransformFactory from creating AffineTransform2D instances.
            public MathTransform createAffineTransform(final Matrix matrix) {
                return new ProjectiveTransform(getVendor(), "Projective transform", null, null,
                        (matrix instanceof SimpleMatrix) ? (SimpleMatrix) matrix : new SimpleMatrix(matrix));
            }
        });
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

    /**
     * Invoked after every tests in order to ensure that the transform created by the factory
     * is of the expected type. This method requires that the transform class is exactly the
     * {@code ProjectiveTransform} class - not a subclass.
     */
    @AfterEach
    public void ensureExpectedTransformClass() {
        assertEquals(ProjectiveTransform.class, transform.getClass(), "Unexpected transform instance.");
    }
}
