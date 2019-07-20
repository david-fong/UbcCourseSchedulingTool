package org.bse.data.utils.spider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;

/**
 * Traverses the web gathering information on course offerings at UBC, and outputting
 * java files representing those courses.
 */
public abstract class Spider {

    protected final Document fetchDocument(URI uri) throws IOException {
        return Jsoup.connect(uri.toASCIIString()).get();
    }

}
