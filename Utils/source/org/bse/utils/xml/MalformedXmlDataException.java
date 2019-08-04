package org.bse.utils.xml;

/**
 * Throw when xml data was not formatted as expected.
 */
public class MalformedXmlDataException extends RuntimeException {

    // TODO: add utility constructors for situations like "missing tag", or
    //  "bad attr val"

    public MalformedXmlDataException(String message) {
        super(message);
    }

}
