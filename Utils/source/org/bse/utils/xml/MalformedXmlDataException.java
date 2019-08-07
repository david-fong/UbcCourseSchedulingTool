package org.bse.utils.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * Throw when xml data was not formatted as expected.
 */
public class MalformedXmlDataException extends Exception {

    private MalformedXmlDataException(String message) {
        super(message);
    }

    public static MalformedXmlDataException noSuchUniqueChildElement(Element host, String tagName) {
        return new MalformedXmlDataException(""); // TODO [impl][noSuchUniqueChildElement]:
    }

    public static MalformedXmlDataException missingAttr(Element host, String attrName) {
        return new MalformedXmlDataException(""); // TODO [impl][missingAttr]:
    }

    public static MalformedXmlDataException invalidAttrVal(Attr attr) {
        Element host = attr.getOwnerElement();
        return new MalformedXmlDataException(String.format(""
                + "Encountered unexpected value \"%s\" for"
                + " attribute \"%s\" from element \"%s\"",
                attr.getValue(), attr.getName(), host == null ? "null" : host.getTagName()
        ));
    }

}
