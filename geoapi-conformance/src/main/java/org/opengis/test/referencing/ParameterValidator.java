/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2008-2019 Open Geospatial Consortium, Inc.
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
package org.opengis.test.referencing;

import java.util.Set;
import java.util.List;
import org.opengis.parameter.*;
import org.opengis.test.ValidatorContainer;

import static org.opengis.test.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Validates {@link ParameterValue} and related objects from the {@code org.opengis.parameter}
 * package.
 *
 * <p>This class is provided for users wanting to override the validation methods. When the default
 * behavior is sufficient, the {@link org.opengis.test.Validators} static methods provide a more
 * convenient way to validate various kinds of objects.</p>
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   2.2
 */
public strictfp class ParameterValidator extends ReferencingValidator {
    /**
     * Creates a new validator instance.
     *
     * @param container  the set of validators to use for validating other kinds of objects
     *                   (see {@linkplain #container field javadoc}).
     */
    public ParameterValidator(final ValidatorContainer container) {
        super(container, "org.opengis.parameter");
    }

    /**
     * For each interface implemented by the given object, invokes the corresponding
     * {@code validate(…)} method defined in this class (if any).
     *
     * @param  object  the object to dispatch to {@code validate(…)} methods, or {@code null}.
     * @return number of {@code validate(…)} methods invoked in this class for the given object.
     */
    public int dispatch(final GeneralParameterDescriptor object) {
        int n = 0;
        if (object != null) {
            if (object instanceof ParameterDescriptor<?>)   {validate((ParameterDescriptor<?>)   object); n++;}
            if (object instanceof ParameterDescriptorGroup) {validate((ParameterDescriptorGroup) object); n++;}
            if (n == 0) {
                validateIdentifiedObject(object);
            }
        }
        return n;
    }

    /**
     * For each interface implemented by the given object, invokes the corresponding
     * {@code validate(…)} method defined in this class (if any).
     *
     * @param  object  the object to dispatch to {@code validate(…)} methods, or {@code null}.
     * @return number of {@code validate(…)} methods invoked in this class for the given object.
     */
    public int dispatch(final GeneralParameterValue object) {
        int n = 0;
        if (object != null) {
            if (object instanceof ParameterValue<?>)   {validate((ParameterValue<?>)   object); n++;}
            if (object instanceof ParameterValueGroup) {validate((ParameterValueGroup) object); n++;}
            if (n == 0) {
                dispatch(object.getDescriptor());
            }
        }
        return n;
    }

    /**
     * Validates the given descriptor.
     *
     * @param  <T>     the class of parameter values.
     * @param  object  the object to validate, or {@code null}.
     */
    public <T> void validate(final ParameterDescriptor<T> object) {
        if (object == null) {
            return;
        }
        validateIdentifiedObject(object);
        final Class<T> valueClass = object.getValueClass();
        mandatory("ParameterDescriptor: getValueClass() can not return null.", valueClass);
        Set<T> validValues = object.getValidValues();
        if (validValues != null) {
            validate(validValues);
            for (final T value : validValues) {
                if (value != null) {
                    assertInstanceOf(valueClass, value, "ParameterDescriptor: getValidValues() has unexpected element.");
                }
            }
        }
        final Comparable<T> min = object.getMinimumValue();
        if (min != null) {
            assertInstanceOf(valueClass, min, "ParameterDescriptor: getMinimumValue() returns unexpected value.");
        }
        final Comparable<T> max = object.getMaximumValue();
        if (max != null) {
            assertInstanceOf(valueClass, max, "ParameterDescriptor: getMaximumValue() returns unexpected value.");
        }
        assertValidRange(min, max, "ParameterDescriptor: inconsistent minimum and maximum values.");
        final T def = object.getDefaultValue();
        if (def != null) {
            assertInstanceOf(valueClass, def, "ParameterDescriptor: getDefaultValue() returns unexpected value.");
            assertBetween(min, max, def, "ParameterDescriptor: getDefaultValue() out of range.");
        }
        assertBetween(0, 1, object.getMinimumOccurs(),
                "ParameterDescriptor: getMinimumOccurs() shall returns 0 or 1.");
        assertEquals(1, object.getMaximumOccurs(),
                "ParameterDescriptor: getMaximumOccurs() shall returns exactly 1.");
    }

    /**
     * Validates the given descriptor.
     *
     * @param  object  the object to validate, or {@code null}.
     */
    public void validate(final ParameterDescriptorGroup object) {
        if (object == null) {
            return;
        }
        validateIdentifiedObject(object);
        final List<GeneralParameterDescriptor> descriptors = object.descriptors();
        if (requireMandatoryAttributes) {
            // Do not invoke mandatory(…) because we allow empty collections.
            assertNotNull(descriptors, "ParameterDescriptorGroup: descriptors() should not return null.");
        }
        if (descriptors != null) {
            validate(descriptors);
            for (final GeneralParameterDescriptor descriptor : descriptors) {
                assertNotNull(descriptor, "ParameterDescriptorGroup: descriptors() can not contain null element.");
                dispatch(descriptor);
                final GeneralParameterDescriptor byName = object.descriptor(descriptor.getName().getCode());
                mandatory("ParameterDescriptorGroup: descriptor(String) should returns a value.", byName);
                if (byName != null) {
                    assertEquals(descriptor, byName,
                            "ParameterDescriptorGroup: descriptor(String) inconsistent with descriptors().");
                }
            }
        }
        final int minOccurs = object.getMinimumOccurs();
        assertPositive(minOccurs, "ParameterDescriptor: getMinimumOccurs() can not be negative.");
        assertValidRange(minOccurs, object.getMaximumOccurs(),
                "ParameterDescriptor: getMaximumOccurs() gives inconsistent range.");
    }

    /**
     * Validates the given parameter value.
     *
     * @param  <T>     the class of parameter values.
     * @param  object  the object to validate, or {@code null}.
     */
    public <T> void validate(final ParameterValue<T> object) {
        if (object == null) {
            return;
        }
        final ParameterDescriptor<T> descriptor = object.getDescriptor();
        mandatory("ParameterValue: shall have a descriptor.", descriptor);
        validate(descriptor);
        final T value = object.getValue();
        if (value != null) {
            if (descriptor != null) {
                final Class<T> valueClass = descriptor.getValueClass();
                assertInstanceOf(valueClass, value, "ParameterValue: getValue() returns unexpected value.");
                final Set<T> validValues = descriptor.getValidValues();
                if (validValues != null) {
                    validate(validValues);
                    assertContains(validValues, value,
                            "ParameterValue: getValue() not a member of getValidValues() set.");
                }
                assertBetween(descriptor.getMinimumValue(), descriptor.getMaximumValue(), value,
                        "ParameterValue: getValue() is out of bounds.");
            }
        }
    }

    /**
     * Validates the given coordinate system.
     *
     * @param  object  the object to validate, or {@code null}.
     */
    public void validate(final ParameterValueGroup object) {
        if (object == null) {
            return;
        }
        final ParameterDescriptorGroup descriptors = object.getDescriptor();
        mandatory("ParameterValueGroup: shall have a descriptor.", descriptors);
        validate(descriptors);
        final List<GeneralParameterValue> values = object.values();
        if (requireMandatoryAttributes) {
            // Do not invoke mandatory(…) because we allow empty collections.
            assertNotNull(values, "ParameterValueGroup: values() should not return null.");
        }
        if (values == null) {
            return;
        }
        validate(values);
        for (final GeneralParameterValue value : values) {
            assertNotNull(value, "ParameterValueGroup: values() can not contain null element.");
            dispatch(value);
            final GeneralParameterDescriptor descriptor = value.getDescriptor();
            mandatory("GeneralParameterValue: expected a descriptor.", descriptor);
            if (descriptor == null) {
                continue;
            }
            final String name = descriptor.getName().getCode();
            mandatory("GeneralParameterDescriptor: expected a name.", name);
            if (name == null) {
                continue;
            }
            if (descriptors != null) {
                final GeneralParameterDescriptor byName = descriptors.descriptor(name);
                mandatory("ParameterDescriptorGroup: should never return null.", byName);
                if (byName != null) {
                    assertEquals(descriptor, byName,
                            "ParameterValueGroup: descriptor(String) inconsistent with value.getDescriptor().");
                }
            }
            if (value instanceof ParameterValue<?>) {
                final ParameterValue<?> byName = object.parameter(name);
                mandatory("ParameterValueGroup: parameter(String) should returns a value.", byName);
                if (byName != null) {
                    assertEquals(value, byName, "ParameterValueGroup: value(String) inconsistent with values().");
                }
            }
        }
    }
}
