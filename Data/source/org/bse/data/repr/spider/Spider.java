package org.bse.data.repr.spider;

import org.bse.data.coursedata.CourseDataLocator.StagedDataPath;
import org.bse.data.repr.faculties.FacultyTreeNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;

/**
 * Traverses the web gathering information on course offerings at UBC, and outputting
 * java files representing those courses.
 */
public final class Spider {

    protected static Document fetchDocument(URI uri) throws IOException {
        return Jsoup.connect(uri.toASCIIString()).get();
    }

    /**
     *
     * @param faculty The faculty to fetch data for.
     * @param absolutePathToCampuses Specifies pre/post-deployment path to campuses directory.
     */
    public final void fetchDataFromWebAndCache(FacultyTreeNode faculty, StagedDataPath absolutePathToCampuses) {
        switch (absolutePathToCampuses) {
            case PRE_DEPLOYMENT:
                break;
            case POST_DEPLOYMENT:
                break;
        }
    }

}
