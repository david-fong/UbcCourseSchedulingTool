package com.dvf.ucst.utils.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * Throw when xml data was not formatted as expected.
 */
public class MalformedXmlDataException extends Exception {

    public MalformedXmlDataException(String message) {
        super(message);
    }

    public MalformedXmlDataException(Exception rootCause) {
        super(rootCause);
    }

    public static MalformedXmlDataException noSuchUniqueChildElement(final Element host, final String tagName) {
        return new MalformedXmlDataException(String.format("A unique %s by the tag name"
                + " \"%s\" could not be found as a direct child of the host %s",
                Element.class, tagName, host.getTagName()
        ));
    }

    public static MalformedXmlDataException missingAttr(final Element host, final String attrName) {
        return new MalformedXmlDataException(String.format("A %s by the name \"%s\""
                + " could not be found for the %s %s",
                Attr.class, attrName, Element.class, host.getTagName()
        ));
    }

    public static MalformedXmlDataException invalidAttrVal(final Attr attr) {
        Element host = attr.getOwnerElement();
        return new MalformedXmlDataException(String.format(""
                + "Encountered unexpected value \"%s\" for"
                + " attribute \"%s\" from element \"%s\"",
                attr.getValue(), attr.getName(), host == null ? "null" : host.getTagName()
        ));
    }

}
