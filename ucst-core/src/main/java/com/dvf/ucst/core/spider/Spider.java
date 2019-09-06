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

    /**
     *
     * @param faculty The faculty to fetch data for. Should not be an instance of
     *     [FacultyTreeRootCampus].
     * @param absolutePathToCampuses Specifies pre/post-deployment path to campuses
     *     directory.
     */
    public final void fetchDataFromWebAndCache(
            final FacultyTreeNode faculty,
            final StagedDataPath absolutePathToCampuses
    ) {
        switch (absolutePathToCampuses) {
            case PRE_DEPLOYMENT:
                try {
                    for (FacultyTreeNode.FacultyCourseSubDir subDir : FacultyTreeNode.FacultyCourseSubDir.values()) {
                        final Path createdDir = Files.createDirectories(absolutePathToCampuses.path.resolve(
                                faculty.getCampusAnchoredPathTo(subDir)
                        ));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case POST_DEPLOYMENT:
                // Check that path to data exists:
                for (final FacultyTreeNode.FacultyCourseSubDir subDir : FacultyTreeNode.FacultyCourseSubDir.values()) {
                    if (!Files.isDirectory(absolutePathToCampuses.path
                            .resolve(faculty.getCampusAnchoredPathTo(subDir))
                    )) {
                        throw new RuntimeException(String.format("%s \"%s\" under the campus \"%s\""
                                + " is missing the subdirectory \"%s\".",
                                FacultyTreeNode.class, faculty.getNameWithTitle(),
                                faculty.getRootCampus().getNameWithTitle(), subDir.getPathToken()
                        ));
                    }
                }
                // Init keys of [getCodeStringToCourseMap] with names of files under the faculty folder:
                final Path courseXmlPath = StagedDataPath.POST_DEPLOYMENT.path
                        .resolve(faculty.getCampusAnchoredPathTo(FacultyTreeNode.FacultyCourseSubDir.COURSE_XML_DATA));
                try (final DirectoryStream<Path> fileStream = Files.newDirectoryStream(courseXmlPath, XmlIoUtils.XML_FILE_FILTER)) {
                    fileStream.forEach(file -> faculty.getCourseIdTokenToCourseMap().putIfAbsent(
                            XmlIoUtils.getFileNameWithoutXmlExtension(file),
                            null
                    ));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

}
