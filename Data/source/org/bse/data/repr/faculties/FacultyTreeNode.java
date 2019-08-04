package org.bse.data.repr.faculties;

import org.bse.data.coursedata.CourseDataLocator;
import org.bse.data.repr.courseutils.Course;
import org.bse.utils.xml.XmlFileUtils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * A property of a [Course].
 */
public interface FacultyTreeNode {

    String getNameNoTitle();

    default String getNameWithTitle() {
        return getType().title + getNameNoTitle();
    }

    String getAbbreviation();

    FacultyTreeNodeType getType();

    default FacultyTreeRootCampus getRootCampus() {
        return getParentNode().getRootCampus();
    }

    /**
     * @return The [FacultyTreeNode] containing [this] in the
     *     collection returned by its [getChildren] method. Must
     *     not return null if [this] is an instance of [FacultyTreeRootCampus].
     */
    FacultyTreeNode getParentNode();

    /**
     * @return A [Path] to the directory containing xml files representing course data
     *     for courses under [this] faculty, and sub-directories whose [Path]s are for
     *     [FacultyTreeNode]s under this faculty. [FacultyTreeRootCampus]s must return
     *     a single-token [Path].
     */
    default Path getPathToData() {
        return getParentNode().getPathToData().resolve(getAbbreviation().toLowerCase());
    }

    /**
     * @return An array of [FacultyTreeNode]s whose
     *     [getParentNode] methods return [this]
     */
    FacultyTreeNode[] getChildren();

    /**
     *
     * @param codeString must not be null. Ex "101". This operation will only succeed if
     * @return The course registered by the code [codeString].
     * @throws FacultyCourseNotFoundException If a file could not be located under
     *     [this][FacultyTreeNode] following the path spec during lazy initialization
     *     of the [Course] registered by the code [codeString].
     */
    default Course getCourseByCodeString(String codeString) throws FacultyCourseNotFoundException {
        if (!getCodeStringToCourseMap().containsKey(codeString)) {
            final String message = "code string not registered in faculty tree node";
            throw new FacultyCourseNotFoundException(message, this);
        }
        Course course = getCodeStringToCourseMap().get(codeString);
        if (course != null) {
            return course;
        } else {
            final Path coursePath = getRuntimeFullPathToData()
                    .resolve(codeString + XmlFileUtils.XML_EXTENSION_STRING);
            course = Course.fromXml(XmlFileUtils.readXmlFromFile(coursePath));
            getCodeStringToCourseMap().put(codeString, course);
            return course;
        }
    }
    default Path getRuntimeFullPathToData() {
        return CourseDataLocator.RUNTIME_CAMPUS_DIR.resolve(getPathToData());
    }

    /**
     * @return A map from course code strings to [Course]s. Must not be null.
     *     All existing [Course]s under the implementing [FacultyTreeNode]
     *     must have their code string as a key after an implementation's
     *     construction. Keys in the returned set must never change.
     */
    Map<String, Course> getCodeStringToCourseMap();

    /**
     * TODO: Implementations must call this in their constructors.
     * Also checks if [getRuntimeFullPath] returns an existing directory.
     */
    default void initCodeStringToCourseMapKeys() {
        // Check that path to data exists:
        final Path dataPath = getRuntimeFullPathToData();
        if (!Files.isDirectory(dataPath)) {
            final String messageFmt = "the expected path to the contents of course"
                    + " data for the faculty \"%s\" did not exist at %s";
            throw new RuntimeException(String.format(messageFmt,
                    getClass().getName(),
                    dataPath.toString()
                    ));
        }
        // Init keys of [getCodeStringToCourseMap] with names of files under the faculty folder:
        try (final DirectoryStream<Path> fileStream = Files.newDirectoryStream(
                getRuntimeFullPathToData(), XML_FILE_FILTER)
        ) {
            fileStream.forEach(file -> {
                String fileName = file.getFileName().toString();
                fileName = fileName.substring(0, fileName.length() - XmlFileUtils.XML_EXTENSION_STRING.length());
                getCodeStringToCourseMap().putIfAbsent(fileName, null);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    DirectoryStream.Filter<Path> XML_FILE_FILTER = entry -> {
        return Files.isDirectory(entry) && entry.getFileName()
                .toString().endsWith(XmlFileUtils.XML_EXTENSION_STRING);
    };



    /**
     * I've never understood what the deal was with
     * "school", "institute", and "centre". Sheesh.
     */
    enum FacultyTreeNodeType {
        CAMPUS     ("Campus - "), // reserved for [FacultyTreeRootCampus]
        FACULTY    ("Faculty of "),
        SCHOOL     ("School of "),
        // INSTITUTE,
        // CENTRE,
        DEPARTMENT ("Department of "),
        ;
        public final String title;

        FacultyTreeNodeType(String title) {
            this.title = title;
        }
    }

}

