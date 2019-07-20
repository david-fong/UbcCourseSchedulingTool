package org.bse.data.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;

/**
 * Traverses the web gathering information on course offerings at UBC, and outputting
 * java files representing those courses.
 */
public class UbcCourseSpider {

    public UbcCourseSpider(URI uri) {
        try {
            Document document = Jsoup.connect(uri.toASCIIString()).get();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
