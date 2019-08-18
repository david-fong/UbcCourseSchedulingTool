package com.dvf.ucst.utils.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class XmlUtils {

    /**
     * A convenience method for finding a single mandatory [Element] directly under a
     * host [Element], and throwing a [MalformedXmlDataException] if it cannot be found.
     *
     * @param host An [Element] that is expected to contain
     *     a single child [Element] by the name [tagName].
     * @param tagName The tag name of an [Element] expected to be found in [host].
     * @return The unique [Element] if it was found.
     * @throws MalformedXmlDataException If an [Element] with [tagName] could not be
     *     found, or if more than one was found as a direct child of [host].
     */
    public static Element getMandatoryUniqueChildByTag(Element host, XmlConstant tagName) throws MalformedXmlDataException {
        final Element element = getOptionalUniqueChildByTag(host, tagName);
        if (element != null) {
            return element;
        } else {
            throw MalformedXmlDataException.noSuchUniqueChildElement(host, tagName.getXmlConstantValue());
        }
    }

    /**
     * Same as [getMandatoryUniqueElementByTag], but returns [null] instead of throwing
     * a [MalformedXmlDataException] if zero [Element]s with [tagName] are found.
     */
    public static Element getOptionalUniqueChildByTag(Element host, XmlConstant tagName) throws MalformedXmlDataException {
        final List<Element> elements = getChildElementsByTagName(host, tagName);
        if (elements.size() == 1) {
            return elements.get(0);
        } else if (elements.isEmpty()) {
            return null;
        } else {
            throw MalformedXmlDataException.noSuchUniqueChildElement(host, tagName.getXmlConstantValue());
        }
    }

    /**
     * @param host An [Element] that is expected to contain direct child [Element]s
     *     by the given tag name.
     * @param tagName The tag name of child [Element]s to search for.
     * @return A [List] of all [Elements] directly under [host] with a tag name
     *     equal to [tagName]. Never [null].
     */
    public static List<Element> getChildElementsByTagName(Element host, XmlConstant tagName) {
        final String tagNameString = tagName.getXmlConstantValue();
        return getChildElementsOf(host).stream()
                .filter((element) -> element.getTagName().equals(tagNameString))
                .collect(Collectors.toList());
    }

    /**
     *
     * @param host An [Element]
     * @return All [Element]s that are direct children of [host].
     */
    public static List<Element> getChildElementsOf(Element host) {
        final NodeList nodeList = host.getChildNodes();
        final List<Element> elements = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);
            if (node instanceof Element) {
                elements.add((Element)node);
            }
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
        Attr attr = host.getAttributeNode(attrName.getXmlConstantValue());
        if (attr == null) {
            throw MalformedXmlDataException.missingAttr(host, attrName.getXmlConstantValue());
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
            Attr attr = host.getAttributeNode(attrName.getXmlConstantValue());
            return (attr == null) ? defaultValue : attr.getValue();
        }
    }



    public interface XmlConstant {
        String getXmlConstantValue();
    }

    public interface UserDataXml {
        /**
         * @param document Must not be modified. Only used to create an [Element]
         *     with an implementation-specific tag name.
         * @return An [Element] representing a user data object that can later be
         *     reconstructed.
         */
        Element toXml(Document document);
    }

}
