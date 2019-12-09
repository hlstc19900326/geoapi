/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    This file is hereby placed into the Public Domain.
 *    This means anyone is free to do whatever they wish with this file.
 */
package org.opengis.example.parameter;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.metadata.citation.Citation;
import org.opengis.example.metadata.SimpleCitation;
import tec.units.ri.unit.Units;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests {@link SimpleParameterGroup}.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   3.1
 */
public strictfp class SimpleParameterGroupTest {
    /**
     * Creates a parameter group for a Mercator projection.
     */
    @Test
    public void testMercator() {
        final Citation authority = new SimpleCitation("EPSG");
        final ParameterValueGroup group = new SimpleParameterGroup(authority, "Mercator (variant A)",
                new SimpleParameter(authority, "Latitude of natural origin",     SimpleParameter.Type.LATITUDE),
                new SimpleParameter(authority, "Longitude of natural origin",    SimpleParameter.Type.LONGITUDE),
                new SimpleParameter(authority, "Scale factor at natural origin", SimpleParameter.Type.SCALE),
                new SimpleParameter(authority, "False easting",                  SimpleParameter.Type.LINEAR),
                new SimpleParameter(authority, "False northing",                 SimpleParameter.Type.LINEAR));

        assertEquals(SimpleParameter.DEGREE, group.parameter("Longitude of natural origin").getUnit());
        assertEquals(Units.METRE,            group.parameter("False easting")              .getUnit());
        assertEquals(0.0,                    group.parameter("Latitude of natural origin") .doubleValue());

        final ParameterValueGroup clone = group.clone();
        assertNotSame(clone, group);
        assertEquals (clone, group);

        group.parameter("Latitude of natural origin").setValue(30.0);
        assertEquals(30.0, group.parameter("Latitude of natural origin") .doubleValue());
        assertNotEquals(group, clone, "Group should not anymore be equal to the clone.");
    }
}
