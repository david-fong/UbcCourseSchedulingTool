package org.bse.data.repr.spider;

import org.bse.data.coursedata.CourseDataLocator.StagedDataPath;
import org.bse.data.repr.faculties.FacultyTreeNode;
import org.bse.utils.xml.XmlFileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

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
     * @param faculty The faculty to fetch data for. Should not be an instance of
     *     [FacultyTreeRootCampus].
     * @param absolutePathToCampuses Specifies pre/post-deployment path to campuses
     *     directory.
     */
    public final void fetchDataFromWebAndCache(FacultyTreeNode faculty, StagedDataPath absolutePathToCampuses) {
        switch (absolutePathToCampuses) {
            case PRE_DEPLOYMENT:
                try {
                    for (FacultyTreeNode.SubDirectories subDir : FacultyTreeNode.SubDirectories.values()) {
                        final Path createdDir = Files.createDirectories(absolutePathToCampuses.path.resolve(
                                faculty.getRootAnchoredPathToInfo(subDir)
                        ));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case POST_DEPLOYMENT:
                // Check that path to data exists:
                for (FacultyTreeNode.SubDirectories subDir : FacultyTreeNode.SubDirectories.values()) {
                    if (!Files.isDirectory(absolutePathToCampuses.path.resolve(
                            faculty.getRootAnchoredPathToInfo(subDir)
                    ))) {
                        throw new RuntimeException(String.format("%s \"%s\" under the campus \"%s\""
                                + " is missing the subdirectory \"%s\".",
                                FacultyTreeNode.class.getName(), faculty.getNameWithTitle(),
                                faculty.getRootCampus().getNameWithTitle(), subDir.subDirName
                        ));
                    }
                }
                // Init keys of [getCodeStringToCourseMap] with names of files under the faculty folder:
                final Path courseXmlPath = StagedDataPath.POST_DEPLOYMENT.path.resolve(
                        faculty.getRootAnchoredPathToInfo(FacultyTreeNode.SubDirectories.COURSE_XML_DATA)
                );
                try (final DirectoryStream<Path> fileStream = Files.newDirectoryStream(courseXmlPath, XML_FILE_FILTER)) {
                    fileStream.forEach(file -> {
                        String fileName = file.getFileName().toString();
                        fileName = fileName.substring(0, fileName.length() - XmlFileUtils.XML_EXTENSION_STRING.length());
                        faculty.getCourseIdTokenToCourseMap().putIfAbsent(fileName, null);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private static final DirectoryStream.Filter<Path> XML_FILE_FILTER = entry ->
            Files.isDirectory(entry) && entry.getFileName()
                    .toString().endsWith(XmlFileUtils.XML_EXTENSION_STRING);

}
