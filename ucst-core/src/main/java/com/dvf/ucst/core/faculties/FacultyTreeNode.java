package com.dvf.ucst.core.faculties;

import com.dvf.ucst.core.HyperlinkBookIf;
import com.dvf.ucst.core.coursedata.CourseDataLocator;
import com.dvf.ucst.core.courseutils.Course;
import com.dvf.ucst.utils.xml.MalformedXmlDataException;
import com.dvf.ucst.utils.xml.XmlIoUtils;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.StringJoiner;

/**
 * A property of a [Course].
 */
public interface FacultyTreeNode extends HyperlinkBookIf {

    String getNameNoTitle();

    default String getNameWithTitle() {
        return getFacultyType().title + getNameNoTitle();
    }

    String getAbbreviation();

    FacultyTreeNodeType getFacultyType();

    default FacultyTreeRootCampus getRootCampus() {
        return getParentNode().getRootCampus();
    }

    // note: calling this in constructors will cause
    // compilation to fail due to circular references.
    default int getDepth() {
        return getParentNode().getDepth() + 1;
    }

    /**
     * @return The [FacultyTreeNode] containing [this] in the collection returned by
     *     its [getChildren] method. Instances of [FacultyTreeRootCampus] must return
     *     [null]. Any other implementation must not return [null].
     */
    FacultyTreeNode getParentNode();

    /**
     * @return An array of [FacultyTreeNode]s whose [getParentNode] methods return
     *     [this]. Must never return [null]. The returned array must never differ
     *     for repeated calls.
     */
    FacultyTreeNode[] getChildren();

    /**
     * implementation note: [FacultyTreeRootCampus]s must break the upward recursion.
     * @param subDir The class of information being looked for. Must not be [null].
     * @return The path to the contained data specified by [subDir].
     */
    default Path getRootAnchoredPathToInfo(SubDirectories subDir) {
        return getParentNode().getRootAnchoredPathToInfo(SubDirectories.CHILD_FACULTY_NODES)
                .resolve(getAbbreviation().toLowerCase())
                .resolve(subDir.subDirectory);
    }

    @Override
    default String getRegistrationSiteUrl() {
        return RegistrationSubjAreaQuery.getFacultyUrl(this);
    }

    /**
     *
     * @param codeString must not be null. Ex "101". This operation will only succeed if
     * @return The course registered by the code [codeString].
     * @throws FacultyCourseNotFoundException If a file could not be located under
     *     [this][FacultyTreeNode] following the path spec during lazy initialization
     *     of the [Course] registered by the code [codeString].
     */
    default Course getCourseByCodeString(final String codeString) throws FacultyCourseNotFoundException {
        if (!getCourseIdTokenToCourseMap().containsKey(codeString)) {
            final String message = "code string not registered in faculty tree node";
            throw new FacultyCourseNotFoundException(message, this);
        }
        Course course = getCourseIdTokenToCourseMap().get(codeString);
        if (course != null) {
            return course;
        } else {
            final Path coursePath = CourseDataLocator.StagedDataPath.POST_DEPLOYMENT.path.resolve(
                    getRootAnchoredPathToInfo(SubDirectories.COURSE_XML_DATA)
            ).resolve(codeString + XmlIoUtils.XML_EXTENSION_STRING);
            try {
                course = new Course(XmlIoUtils.readXmlFromFile(coursePath).getDocumentElement());
            } catch (SAXException | IOException e) {
                throw new RuntimeException("could not get xml from file", e);
            } catch (MalformedXmlDataException e) {
                throw new RuntimeException("malformed xml course data", e);
            }
            getCourseIdTokenToCourseMap().put(codeString, course);
            return course;
        }
    }

    /**
     * @return A map from course code strings to [Course]s. Must not be [null].
     *     All existing [Course]s under the implementing [FacultyTreeNode]
     *     must have their code string as a key after an implementation's
     *     construction. Keys in the returned set must never change.
     */
    Map<String, Course> getCourseIdTokenToCourseMap();

    static String getSubTreeString(final FacultyTreeNode scrub) {
        final StringJoiner treeStringJoiner = new StringJoiner("\n");
        getSubTreeString(scrub, 0, treeStringJoiner);
        return treeStringJoiner.toString();
    }
    static void getSubTreeString(final FacultyTreeNode scrub, final int scrubTabLevel, final StringJoiner stringJoiner) {
        stringJoiner.add(
                new String(new char[scrubTabLevel]).replace("\0", "   ") // <- String repetition
                        + "- " + scrub.getAbbreviation()
        );
        for (final FacultyTreeNode child : scrub.getChildren()) {
            getSubTreeString(child, scrubTabLevel + 1, stringJoiner);
        }
    }



    /**
     *
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

    /**
     * Course data saved locally is organized into the following subdirectories:
     */
    enum SubDirectories {
        THIS (""),
        CHILD_FACULTY_NODES ("childnodes"),
        COURSE_XML_DATA ("coursedata"),
        STANDARD_TIMETABLES ("stts"), // an xml file for each year of study.
        PROGRAM_SPECS ("programspecs"), // a file for each year of study. include elective reqs.
        // TODO[spec]: ^design how reqs that need to finished before a certain year are represented.
        //  also, reqs will need to be able to refer to common reqs like the engineering "impact of tech
        //  on society" candidates, and the arts elective candidates. How to decide where to put and how
        //  to refer to them in a way that specifies that?
        ;
        private final Path subDirectory;

        SubDirectories(final String subDirectory) {
            this.subDirectory = Paths.get(subDirectory);
        }

        public Path getSubDirectory() {
            return subDirectory;
        }
    }

}
