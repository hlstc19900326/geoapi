/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2009-2019 Open Geospatial Consortium, Inc.
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
package org.opengis.metadata;

import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests the content of {@code charset-codes.properties} file.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   3.1
 */
public final strictfp class LegacyCodesTest {
    /**
     * Tests the content of {@code "2003/charset-codes.properties"}.
     *
     * @throws IOException if the properties file can not be read.
     */
    @Test
    public void testCharsetCodes() throws IOException {
        final Properties codes = new Properties();
        try (InputStream in = Metadata.class.getResourceAsStream("2003/charset-codes.properties")) {
            codes.load(in);
        }
        assertEquals("UCS-2",       codes.get("ucs2"),       "ucs2");
        assertEquals("UCS-4",       codes.get("ucs4"),       "ucs4");
        assertEquals("UTF-7",       codes.get("utf7"),       "utf7");
        assertEquals("UTF-8",       codes.get("utf8"),       "utf8");
        assertEquals("UTF-16",      codes.get("utf16"),      "utf16");
        assertEquals("ISO-8859-1",  codes.get("8859part1"),  "8859part1");
        assertEquals("ISO-8859-2",  codes.get("8859part2"),  "8859part2");
        assertEquals("ISO-8859-3",  codes.get("8859part3"),  "8859part3");
        assertEquals("ISO-8859-4",  codes.get("8859part4"),  "8859part4");
        assertEquals("ISO-8859-5",  codes.get("8859part5"),  "8859part5");
        assertEquals("ISO-8859-6",  codes.get("8859part6"),  "8859part6");
        assertEquals("ISO-8859-7",  codes.get("8859part7"),  "8859part7");
        assertEquals("ISO-8859-8",  codes.get("8859part8"),  "8859part8");
        assertEquals("ISO-8859-9",  codes.get("8859part9"),  "8859part9");
        assertEquals("ISO-8859-10", codes.get("8859part10"), "8859part10");
        assertEquals("ISO-8859-11", codes.get("8859part11"), "8859part11");
        assertEquals("ISO-8859-12", codes.get("8859part12"), "8859part12");
        assertEquals("ISO-8859-13", codes.get("8859part13"), "8859part13");
        assertEquals("ISO-8859-14", codes.get("8859part14"), "8859part14");
        assertEquals("ISO-8859-15", codes.get("8859part15"), "8859part15");
        assertEquals("ISO-8859-16", codes.get("8859part16"), "8859part16");
        assertEquals("JIS_X0201",   codes.get("jis"),        "jis");
        assertEquals("Shift_JIS",   codes.get("shiftJIS"),   "shiftJIS");
        assertEquals("EUC-JP",      codes.get("eucJP"),      "eucJP");
        assertEquals("US-ASCII",    codes.get("usAscii"),    "usAscii");
        assertEquals("EBCDIC",      codes.get("ebcdic"),     "ebcdic");
        assertEquals("EUC-KR",      codes.get("eucKR"),      "eucKR");
        assertEquals("Big5",        codes.get("big5"),       "big5");
        assertEquals("GB2312",      codes.get("GB2312"),     "GB2312");
    }
}
