package org.bse.utils.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public final class XmlParsingUtils {

    /**
     * A convenience method for finding a mandatory [Attr] in a host [Element]
     *     and throwing a [MalformedXmlDataException] if it cannot be found.
     *
     * @param host An [Element] that is expected to contain
     *     an [Attr] by the name [attrName].
     * @param attrName The name of an [Attr] expected to be found in [host].
     * @return [attr] if it was found.
     * @throws MalformedXmlDataException If [attr] could not be found.
     */
    public static Attr getMandatoryAttr(Element host, String attrName) throws MalformedXmlDataException {
        Attr attr = host.getAttributeNode(attrName);
        if (attr == null) {
            throw MalformedXmlDataException.missingAttr(host, attrName);
        } else {
            return attr;
        }
    }

}
