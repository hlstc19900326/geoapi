/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source$
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.filter.expression;


/**
 * Expression class whose value is computed by retrieving the value
 * of a {@linkplain org.opengis.feature.Feature feature}'s property.
 *
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version <A HREF="http://www.opengis.org/docs/02-059.pdf">Implementation specification 1.0</A>
 * @since 1.1
 */
public interface PropertyName extends Expression {
    /**
     * Returns the name of the property whose value will be returned by the
     * {@link #evaluate evaluate} method.
     */
    String getPropertyName();
}
