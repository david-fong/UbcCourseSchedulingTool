package com.dvf.ucst.core.spider;

import com.dvf.ucst.core.coursedata.CourseDataLocator.StagedDataPath;
import com.dvf.ucst.core.faculties.FacultyTreeNode;
import com.dvf.ucst.utils.xml.XmlIoUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Traverses the web gathering information on course offerings at UBC, and outputting
 * java files representing those courses.
 */
public final class Spider {

    protected static Document fetchDocument(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

}
