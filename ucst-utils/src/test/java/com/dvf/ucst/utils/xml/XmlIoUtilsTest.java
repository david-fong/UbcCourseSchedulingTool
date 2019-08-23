package com.dvf.ucst.utils.xml;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;

class XmlIoUtilsTest {

    @Test
    void test() {
        try {
            final URL url = new URL(
                    "https://courses.students.ubc.ca/cs/courseschedule"
                    + "?pname=subjarea&tname=subj-department&dept=CPEN"
            );
            XmlIoUtils.printDocument(XmlIoUtils.fetchHtmlFromUrl(url));
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
    }

}