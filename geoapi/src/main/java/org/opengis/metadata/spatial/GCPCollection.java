/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2009-2011 Open Geospatial Consortium, Inc.
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
package org.opengis.metadata.spatial;

import java.util.Collection;

import org.opengis.annotation.UML;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.util.InternationalString;

import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;


/**
 * Information about a control point collection.
 *
 * @author Cédric Briançon (Geomatys)
 *
 * @since GeoAPI 2.3
 *
 * @navassoc 1 - - ReferenceSystem
 * @navassoc - - - GCP
 */
@UML(identifier="MI_GCPCollection", specification=ISO_19115_2)
public interface GCPCollection extends GeolocationInformation {
    /**
     * Identifier of the GCP collection.
     *
     * @return The identifier.
     */
    @UML(identifier="collectionIdentification", obligation=MANDATORY, specification=ISO_19115_2)
    Integer getCollectionIdentification();

    /**
     * Name of the GCP collection.
     *
     * @return Name of the GCP collection.
     */
    @UML(identifier="collectionName", obligation=MANDATORY, specification=ISO_19115_2)
    InternationalString getCollectionName();

    /**
     * Coordinate system in which the ground control points are defined.
     *
     * @return Coordinate system in which the ground control points are defined.
     */
    @UML(identifier="coordinateReferenceSystem", obligation=MANDATORY, specification=ISO_19115_2)
    ReferenceSystem getCoordinateReferenceSystem();

    /**
     * Ground control point(s) used in the collection.
     *
     * @return Ground control point(s).
     */
    @UML(identifier="gcp", obligation=MANDATORY, specification=ISO_19115_2)
    Collection<? extends GCP> getGCPs();
}
