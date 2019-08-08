package org.bse.data.repr.faculties;

import org.bse.data.coursedata.CourseDataLocator;
import org.bse.data.repr.courseutils.Course;
import org.bse.utils.xml.MalformedXmlDataException;
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
     * @return An array of [FacultyTreeNode]s whose
     *     [getParentNode] methods return [this]
     */
    FacultyTreeNode[] getChildren();

    /**
     * implementation note: [FacultyTreeRootCampus]s must break the upward recursion.
     * @param infoType The class of information being looked for.
     * @return The path to the contained data specified by [infoType].
     */
    default Path getRootAnchoredPathToInfo(SubDirectories infoType) {
        return getParentNode().getRootAnchoredPathToInfo(SubDirectories.CHILD_FACULTY_NODES)
                .resolve(getAbbreviation().toLowerCase())
                .resolve(infoType.subDirName);
    }

    default String getRegistrationSiteUrl() {
        return getRootCampus().getRegistrationSiteUrl() + "&dept=" + getAbbreviation();
    }

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
            try {
                course = new Course(XmlFileUtils.readXmlFromFile(coursePath));
            } catch (MalformedXmlDataException e) {
                throw new RuntimeException("corrupted xml course data", e);
            }
            getCodeStringToCourseMap().put(codeString, course);
            return course;
        }
    }
    default Path getRuntimeFullPathToData() {
        return CourseDataLocator.RUNTIME_CAMPUS_DIR.resolve(
                getRootAnchoredPathToInfo(SubDirectories.COURSE_XML_DATA)
        );
    }

    /**
     * @return A map from course code strings to [Course]s. Must not be null.
     *     All existing [Course]s under the implementing [FacultyTreeNode]
     *     must have their code string as a key after an implementation's
     *     construction. Keys in the returned set must never change.
     */
    Map<String, Course> getCodeStringToCourseMap();

    /**
     * TODO [impl]: change this to be a recursive init method outside this
     *  interface in [DataMain]/[Core], each with different behaviour.
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
    DirectoryStream.Filter<Path> XML_FILE_FILTER = entry ->
            Files.isDirectory(entry) && entry.getFileName()
                    .toString().endsWith(XmlFileUtils.XML_EXTENSION_STRING);



    /**
     * I've never understood what the deal was with
     * "school", "institute", and "centre". Sheesh.
     */
    enum FacultyTreeNodeType {
        CAMPUS     (" Campus"), // reserved for [FacultyTreeRootCampus]
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

    // TODO: make sure usages of [Path]s are following this spec after it is written.
    enum SubDirectories {
        CHILD_FACULTY_NODES ("childnodes"),
        COURSE_XML_DATA ("coursedata"),
        STANDARD_TIMETABLES ("stts"), // an xml file for each year of study.
        PROGRAM_SPECS ("programspecs"), // a file for each year of study. include elective reqs.
        // TODO[spec]: ^design how reqs that need to finished before a certain year are represented.
        //  also, reqs will need to be able to refer to common reqs like the engineering "impact of tech
        //  on society" candidates, and the arts elective candidates. How to decide where to put and how
        //  to refer to them in a way that specifies that?
        ;
        public final String subDirName;

        SubDirectories(String subDirName) {
            this.subDirName = subDirName;
        }
    }

}

