/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2004-2011 Open Geospatial Consortium, Inc.
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

/**
 * {@linkplain org.opengis.metadata.lineage.Lineage} information. The following is adapted from
 * <A HREF="http://www.opengis.org/docs/01-111.pdf">OpenGIS&reg; Metadata (Topic 11)</A> specification.
 *
 * <P ALIGN="justify">This package also contains information about the sources and production processes
 * used in producing a dataset. The {@linkplain org.opengis.metadata.lineage.Lineage lineage} entity is
 * optional and contains a statement about the lineage. It is an aggregate of
 * {@linkplain org.opengis.metadata.lineage.ProcessStep process step} and
 * {@linkplain org.opengis.metadata.lineage.Source source}. The
 * {@linkplain org.opengis.metadata.lineage.Lineage#getStatement statement} element is mandatory if
 * <CODE>{@linkplain org.opengis.metadata.quality.DataQuality#getScope DataQuality.scope}.{@linkplain
 * org.opengis.metadata.quality.Scope#getLevel level}</CODE> has a value of
 * "{@linkplain org.opengis.metadata.maintenance.ScopeCode#DATASET dataset}" or
 * "{@linkplain org.opengis.metadata.maintenance.ScopeCode#SERIES series}" and the
 * {@linkplain org.opengis.metadata.lineage.Source source} and
 * {@linkplain org.opengis.metadata.lineage.ProcessStep process step} are not set.</P>
 *
 * @version <A HREF="http://www.opengeospatial.org/standards/as#01-111">ISO 19115</A>
 * @since   GeoAPI 2.0
 */
package org.opengis.metadata.lineage;
