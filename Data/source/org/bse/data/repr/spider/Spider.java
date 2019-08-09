package org.bse.data.repr.spider;

import org.bse.data.coursedata.CourseDataLocator.StagedDataPath;
import org.bse.data.repr.faculties.FacultyTreeNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

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
                try {
                    for (FacultyTreeNode.SubDirectories subDir : FacultyTreeNode.SubDirectories.values()) {
                        Files.createDirectories(absolutePathToCampuses.path.resolve(
                                faculty.getRootAnchoredPathToInfo(subDir)
                        ));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case POST_DEPLOYMENT:
                break;
        }
    }

}
