package org.bse.utils.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

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
     * Same as [getMandatoryUniqueElementByTag], but returns null instead of throwing
     * a [MalformedXmlDataException] if zero [Element]s with [tagName] are found.
     */
    public static Element getOptionalUniqueElementByTag(Element host, XmlConstant tagName) throws MalformedXmlDataException {
        final NodeList nodeList = host.getElementsByTagName(tagName.value());
        if (nodeList.getLength() == 1 && nodeList.item(0) instanceof Element) {
            return (Element)nodeList.item(0);
        } else if (nodeList.getLength() == 0) {
            return null;
        } else {
            throw MalformedXmlDataException.noSuchUniqueChildElement(host, tagName.value());
        }
    }

    /**
     * @param host An [Element] that is expected to contain [Element]s by the
     *     given tag name.
     * @param tagName The tag name of child [Element]s to search for.
     * @return A [List] of all [Elements] directly under [host] with a tag name
     *     equal to [tagName].
     */
    public static List<Element> getElementsByTagName(Element host, XmlConstant tagName) {
        final NodeList nodeList = host.getElementsByTagName(tagName.value());
        final List<Element> elements = new ArrayList<>(nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); i++) {
            elements.add((Element)nodeList.item(i));
        }
        return elements;
    }

    /**
     * A convenience method for finding a mandatory [Attr] in a host [Element]
     *     and throwing a [MalformedXmlDataException] if it cannot be found.
     *
     * @param host An [Element] that is expected to contain an [Attr] by the name
     *     [attrName].
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

    /**
     * @param host An [Element] that may contain an [Attr] by the name [attrName].
     *     May be null.
     * @param attrName The name of an [Attr] that may be in [host].
     * @param defaultValue The value to use if an attribute by [attrName] is not found.
     * @return The attribute's value if found, or [defaultValue] otherwise. Never null.
     */
    public static String getOptionalAttr(Element host, XmlConstant attrName, String defaultValue) {
        if (host == null) {
            return defaultValue;
        } else {
            Attr attr = host.getAttributeNode(attrName.value());
            return (attr == null) ? defaultValue : attr.getValue();
        }
    }



    public interface XmlConstant {
        String value();
    }

}
