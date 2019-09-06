package com.dvf.ucst.utils.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Convenience methods whose their bodies generally just make a single call to an
 * external library's method for the same purpose. They are more to us to remember
 * how things are done, and to reuse background objects (saved in static constants).
 * Reduces the interface for reading and writing xml files to using [Document]s and
 * [File]s.
 */
public final class XmlIoUtils {

    public static final String XML_EXTENSION_STRING = ".xml";
    public static final DirectoryStream.Filter<Path> XML_FILE_FILTER;

    private static final DocumentBuilder DOC_BUILDER;
    private static final Transformer PLAIN_TRANSFORMER;
    private static final Transformer PRETTY_TRANSFORMER;
    static {
        XML_FILE_FILTER = (entry) -> Files.isRegularFile(entry)
                && entry.getFileName().toString().toLowerCase()
                .endsWith(XML_EXTENSION_STRING.toLowerCase());
        try {
            DOC_BUILDER = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Could not create a DocumentBuilder", e);
        }
        try {
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            PLAIN_TRANSFORMER = transformerFactory.newTransformer();
            PLAIN_TRANSFORMER.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            PRETTY_TRANSFORMER = transformerFactory.newTransformer();
            PRETTY_TRANSFORMER.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            PRETTY_TRANSFORMER.setOutputProperty(OutputKeys.INDENT, "yes");
        } catch (final TransformerConfigurationException e) {
            throw new RuntimeException("Could not create a Transformer", e);
        }
    }

    /**
     * @param file A [Path] that is probably a regular file and may have a trailing
     *     xml file extension. Must not be null.
     * @return The filename of [file] without the xml file extension, if the extension
     *     was found. This operation is not case sensitive to the file extension. If
     *     [file] does not exist or is not a regular file, returns [null].
     */
    public static String getFileNameWithoutXmlExtension(final Path file) {
        final String filename = file.getFileName().toString();
        if (!Files.isRegularFile(file)) {
            return null;
        } else {
            if (filename.toLowerCase().endsWith(XML_EXTENSION_STRING.toLowerCase())) {
                return filename.substring(0, filename.length() - XML_EXTENSION_STRING.length());
            } else {
                return filename;
            }
        }
    }



    /**
     * Reads a file that is assumed to contain valid xml, and returns a [Document]
     * view of the contents of that file, or null if the file did not contain not
     * valid XML. Does not check if the file at [filePath] exists and is a file.
     * Such checking should be performed externally.
     *
     * @param filePath The path to the file to read from. Must not be null.
     * @return [null] if the file could not be read into a [Document].
     */
    public static Document readXmlFromFile(Path filePath) throws IOException, SAXException {
        return DOC_BUILDER.parse(filePath.toFile());
    }

    /**
     * @param url A valid [URL] to a page carrying html content.
     * @return A [Document] of the fetched HTML.
     */
    public static Document fetchHtmlFromUrl(final URL url) throws IOException, SAXException {
        return DOC_BUILDER.parse(url.openStream());
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
     * @param dir The path to the directory that the file will be written to.
     * @param filename The name to give to the new file, excluding the file extension.
     * @throws TransformerException If there was an error during the transforming process.
     */
    public static void writeDocumentToFile(final Document document, final Path dir, final String filename) throws TransformerException {
        PLAIN_TRANSFORMER.transform(
                new DOMSource(document),
                new StreamResult(dir.resolve(filename + XML_EXTENSION_STRING).toFile())
        );
    }

    /**
     * A utility method for debugging and test purposes.
     * @param node A [Node] to print to the standard output.
     */
    public static void printNode(final Node node) {
        try {
            PRETTY_TRANSFORMER.transform(
                    new DOMSource(node),
                    new StreamResult(System.out)
            );
        } catch (final TransformerException e) {
            e.printStackTrace();
        }
    }

    public static String printNodeToString(final Node node) throws TransformerException {
        final StringWriter writer = new StringWriter();
        PRETTY_TRANSFORMER.transform(
                new DOMSource(node),
                new StreamResult(writer)
        );
        return writer.toString();
    }

}
