/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    This file is hereby placed into the Public Domain.
 *    This means anyone is free to do whatever they wish with this file.
 */
package org.opengis.example.parameter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests {@link SimpleParameter}.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   3.1
 */
public strictfp class SimpleParameterTest {
    /**
     * Small tolerance factor for comparison of floating point numbers.
     */
    private static final double EPS = 1E-12;

    /**
     * Tests a latitude parameter.
     */
    @Test
    public void testLatitude() {
        final SimpleParameter param = new SimpleParameter(null, "latitude", SimpleParameter.Type.LATITUDE);
        assertEquals(Double.valueOf(-90),     param.getMinimumValue(), "Minimum");
        assertEquals(Double.valueOf(+90),     param.getMaximumValue(), "Maximum");
        assertEquals(0.0,                     param.doubleValue(),     "Initial value");
        assertEquals(0,                       param.intValue(),        "Lenient cast");
        assertEquals(false,                   param.booleanValue(),    "Lenient behavior");
        assertArrayEquals(new double[] {0.0}, param.doubleValueList(), "Lenient behavior");
        assertArrayEquals(new int[]    {0},   param.intValueList(),    "Lenient behavior");
        assertEquals     (              0.0,  param.doubleValue(SimpleParameter.GRAD),     "Unit conversion");
        assertArrayEquals(new double[] {0.0}, param.doubleValueList(SimpleParameter.GRAD), "Unit conversion");

        param.setValue(18);
        assertEquals(                   18.0,  param.doubleValue(),     "value");
        assertEquals(                   18,    param.intValue(),        "Lenient cast");
        assertArrayEquals(new double[] {18.0}, param.doubleValueList(), "Lenient behavior");
        assertArrayEquals(new int[]    {18  }, param.intValueList(),    "Lenient behavior");
        assertEquals     (              20.0,  param.doubleValue(SimpleParameter.GRAD), EPS, "Unit conversion");
        assertArrayEquals(new double[] {20.0}, param.doubleValueList(SimpleParameter.GRAD), EPS, "Unit conversion");
    }
}
