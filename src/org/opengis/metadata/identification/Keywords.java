/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source$
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.metadata.identification;

// J2SE direct dependencies
import java.util.Collection;

// OpenGIS direct dependencies
import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;

// Annotations
import org.opengis.annotation.UML;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;


/**
 * Keywords, their type and reference source.
 *
 * @author ISO 19115
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version <A HREF="http://www.opengis.org/docs/01-111.pdf">Abstract specification 5.0</A>
 * @since 1.1
 */
@UML (identifier="MD_Keywords", specification=ISO_19115)
public interface Keywords {
    /**
     * Commonly used word(s) or formalised word(s) or phrase(s) used to describe the subject.
     */
    @UML (identifier="keyword", obligation=MANDATORY, specification=ISO_19115)
    Collection<InternationalString> getKeywords();

    /**
     * Subject matter used to group similar keywords.
     */
    @UML (identifier="type", obligation=OPTIONAL, specification=ISO_19115)
    KeywordType getType();

    /**
     * Name of the formally registered thesaurus or a similar authoritative source of keywords.
     */
    @UML (identifier="thesaurusName", obligation=OPTIONAL, specification=ISO_19115)
    Citation getThesaurusName();
}
