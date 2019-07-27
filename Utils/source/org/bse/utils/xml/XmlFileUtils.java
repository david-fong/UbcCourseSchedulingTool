package org.bse.utils.xml;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Convenience methods whose their bodies generally just make a single call to an
 * external library's method for the same purpose. They are more to us to remember
 * how things are done, and to reuse background objects (saved in static constants).
 * Reduces the interface for reading and writing xml files to using [Document]s and
 * [File]s.
 */
public final class XmlFileUtils {

    private static final DocumentBuilder DOC_BUILDER;
    private static final Transformer TRANSFORMER;
    static {
        try {
            DOC_BUILDER = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Could not create a DocumentBuilder", e);
        }
        try {
            TRANSFORMER = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException("Could not create a Transformer", e);
        }
    }

    /**
     * Reads a file that is assumed to contain valid xml, and
     * returns a [Document] view of the contents of that file,
     * or null if the file did not exist or was not valid XML.
     *
     * @param filePath The path to the file to read from.
     * @return [null] if the file could not be read into a [Document].
     * @throws FileNotFoundException If [filePath] did not lead to an existing file.
     */
    public static Document readXmlFromFile(File filePath) throws FileNotFoundException {
        // TODO: perform some checks on file? (exists, isFile, ...)
        if (!filePath.exists()) {
            throw new FileNotFoundException();
        }
        try {
            final Document document = DOC_BUILDER.parse(filePath);
            // TODO: throw an exception if the document was not valid XML.
            return document;
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This does not create an actual file - only
     * an object that represents an XML document.
     * For file writing, see [writeDocumentToFile].
     *
     * @return An [org.w3c.dom.Document].
     */
    public static Document createNewXmlDocument() {
        return DOC_BUILDER.newDocument();
    }

    /**
     * Transforms the contents of [document] to text
     * and writes it to the file at the path specified
     * by [filePath].
     *
     * @param document An object representing an XML document.
     * @param filePath A descriptor of the path of the file to write to.
     * @throws TransformerException If there was an error during the transforming process.
     */
    public static void writeDocumentToFile(Document document, File filePath) throws TransformerException {
        TRANSFORMER.transform(new DOMSource(document), new StreamResult(filePath));
    }


}
