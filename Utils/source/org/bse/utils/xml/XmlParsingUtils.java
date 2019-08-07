package org.bse.utils.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class XmlParsingUtils {

    /**
     * A convenience method for finding a mandatory [Element] in a host [Element]
     *     and throwing a [MalformedXmlDataException] if it cannot be found.
     *
     * @param host An [Element] that is expected to contain
     *     an [Element] by the name [tagName].
     * @param tagName The tag name of an [Element] expected to be found in [host].
     * @return The unique [Element] if it was found.
     * @throws MalformedXmlDataException If an [Element] with [tagName] could not be
     *     found, or if more than one was found as a direct child of [host].
     */
    public static Element getMandatoryUniqueElementByTag(Element host, XmlConstant tagName) throws MalformedXmlDataException {
        final NodeList nodeList = host.getElementsByTagName(tagName.value());
        if (nodeList.getLength() == 1 && nodeList.item(0) instanceof Element) {
            return (Element)nodeList.item(0);
        } else {
            throw MalformedXmlDataException.noSuchUniqueChildElement(host, tagName.value());
        }
    }

    /**
     * A convenience method for finding a mandatory [Attr] in a host [Element]
     *     and throwing a [MalformedXmlDataException] if it cannot be found.
     *
     * @param host An [Element] that is expected to contain
     *     an [Attr] by the name [attrName].
     * @param attrName The name of an [Attr] expected to be found in [host].
     * @return The [Attr] if it was found.
     * @throws MalformedXmlDataException If [attrName] could not be found.
     */
    public static Attr getMandatoryAttr(Element host, XmlConstant attrName) throws MalformedXmlDataException {
        Attr attr = host.getAttributeNode(attrName.value());
        if (attr == null) {
            throw MalformedXmlDataException.missingAttr(host, attrName.value());
        } else {
            return attr;
        }
    }



    public interface XmlConstant {
        String value();
    }

}
