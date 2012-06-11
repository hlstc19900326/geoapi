/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2012 Open Geospatial Consortium, Inc.
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
package org.opengis.test.coverage.image;

import java.util.Random;
import java.io.Closeable;
import java.io.IOException;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.BandedSampleModel;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import javax.imageio.stream.ImageInputStream;

import org.opengis.test.TestCase;


/**
 * Base class for all tests defined in the {@code org.opengis.test.coverage.image} package.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   3.1
 */
public strictfp abstract class ImageBackendTestCase extends TestCase {
    /**
     * Small value for comparison of sample values. Since scientific data often store their
     * values as {@code float} numbers, this {@code SAMPLE_TOLERANCE} value must be of the
     * order of {@code float} relative precision, not {@code double}.
     */
    static final float SAMPLE_TOLERANCE = 1E-5f;

    /**
     * Creates a new test case.
     */
    protected ImageBackendTestCase() {
    }

    /**
     * Returns the bounds of the given image.
     *
     * @param  image The image for which to get the bounds.
     * @return The image bounds.
     */
    static Rectangle getBounds(final RenderedImage image) {
        return new Rectangle(
            image.getMinX(),
            image.getMinY(),
            image.getWidth(),
            image.getHeight());
    }

    /**
     * Creates a banded raster for the given data type.
     *
     * @param  dataType The data type as one of the {@link DataBuffer} constants.
     * @param  width    The desired raster width.
     * @param  height   The desired raster height.
     * @param  numBands The desired number of bands.
     * @return A writable raster of the given type and size.
     */
    static WritableRaster createBandedRaster(final int dataType, final int width, final int height, final int numBands) {
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:
            case DataBuffer.TYPE_USHORT:
            case DataBuffer.TYPE_INT: {
                // As of JDK7, the method called below supports only the above-cited types.
                return Raster.createBandedRaster(dataType, width, height, numBands, null);
            }
            default: {
                // For all other types, we need to create the sample model ourselves.
                return Raster.createWritableRaster(new BandedSampleModel(dataType, width, height, numBands), null);
            }
        }
    }

    /**
     * Fills the given raster with random numbers.
     *
     * @param raster The raster to fill.
     * @param random The random numbers generator to use.
     */
    static void fill(final WritableRaster raster, final Random random) {
        final int xmin = raster.getMinX();
        final int ymin = raster.getMinY();
        final int xmax = raster.getWidth()  + xmin;
        final int ymax = raster.getHeight() + ymin;
        final int numBands = raster.getNumBands();
        final int dataType = raster.getSampleModel().getDataType();
        for (int y=ymin; y<ymax; y++) {
            for (int x=xmin; x<xmax; x++) {
                for (int b=0; b<numBands; b++) {
                    switch (dataType) {
                        default:                     raster.setSample(x, y, b, random.nextInt());    break;
                        case DataBuffer.TYPE_FLOAT:  raster.setSample(x, y, b, random.nextFloat());  break;
                        case DataBuffer.TYPE_DOUBLE: raster.setSample(x, y, b, random.nextDouble()); break;
                    }
                }
            }
        }
    }

    /**
     * Returns an initially empty image of the given type. For one-banded images of any type,
     * this method will returns a gray scale image. For three or four banded images, this method
     * will use an (A)RGB color model.
     *
     * @param  dataType The data type as one of the {@link DataBuffer} constants.
     * @param  width    The desired raster width.
     * @param  height   The desired raster height.
     * @param  numBands The desired number of bands.
     * @return An image of the given type and size.
     * @throws IllegalArgumentException If this method does not support the given combination
     *         of number of bands and data type.
     */
    static BufferedImage createImage(final int dataType, final int width, final int height, final int numBands) {
        final ColorModel cm;
        switch (numBands) {
            case 1: {
                cm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY), false, true, Transparency.OPAQUE, dataType);
                break;
            }
            case 3: {
                switch (dataType) {
                    case DataBuffer.TYPE_BYTE: return new BufferedImage(BufferedImage.TYPE_3BYTE_BGR, width, height);
                    default: throw new IllegalArgumentException("Unsupported data type: " + dataType);
                }
            }
            default: {
                throw new IllegalArgumentException("Unsupported number of bands: " + numBands);
            }
        }
        return new BufferedImage(cm, cm.createCompatibleWritableRaster(width, height), false, null);
    }

    /**
     * Closes the given input or output stream if it implements the {@link Closeable} interface.
     * Do nothing otherwise. Note that this method perform a special check for Image I/O streams,
     * which implement the {@code Closeable} interface only since JDK7.
     *
     * @param  stream The input or output stream to close, or {@code null} if none.
     * @throws IOException In an error occurred while closing the stream.
     */
    static void close(final Object stream) throws IOException {
        if (stream instanceof Closeable) {
            ((Closeable) stream).close();
        } else if (stream instanceof ImageInputStream) {
            /*
             * ImageInputStream extends Closeable only since JDK7. For JDK6, we need an
             * explicit check. Note that we don't need to check for ImageOutputStream
             * since it extends ImageInputStream.
             */
            ((ImageInputStream) stream).close();
        }
    }
}
