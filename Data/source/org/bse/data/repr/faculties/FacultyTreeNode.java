package org.bse.data.repr.faculties;

import org.bse.data.coursedata.CourseDataLocator;
import org.bse.data.repr.HyperlinkBookIf;
import org.bse.data.repr.courseutils.Course;
import org.bse.utils.xml.MalformedXmlDataException;
import org.bse.utils.xml.XmlFileUtils;

import java.nio.file.Path;
import java.util.Map;

/**
 * A property of a [Course].
 */
public interface FacultyTreeNode extends HyperlinkBookIf {

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
     * @param subDir The class of information being looked for. Must not be null.
     * @return The path to the contained data specified by [subDir].
     */
    default Path getRootAnchoredPathToInfo(SubDirectories subDir) {
        return getParentNode().getRootAnchoredPathToInfo(SubDirectories.CHILD_FACULTY_NODES)
                .resolve(getAbbreviation().toLowerCase())
                .resolve(subDir.subDirName);
    }

    @Override
    default String getRegistrationSiteUrl() {
        return getRootCampus().getRegistrationSiteUrl()
                .replace(QuerySpecifierTokens.CAMPUS.tnameQueryVal,
                        QuerySpecifierTokens.FACULTY.tnameQueryVal)
                + QuerySpecifierTokens.FACULTY.tokenStub
                + getAbbreviation();
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
            ).resolve(codeString + XmlFileUtils.XML_EXTENSION_STRING);
            try {
                course = new Course(XmlFileUtils.readXmlFromFile(coursePath));
            } catch (MalformedXmlDataException e) {
                throw new RuntimeException("corrupted xml course data", e);
            }
            getCourseIdTokenToCourseMap().put(codeString, course);
            return course;
        }
    }

    /**
     * @return A map from course code strings to [Course]s. Must not be null.
     *     All existing [Course]s under the implementing [FacultyTreeNode]
     *     must have their code string as a key after an implementation's
     *     construction. Keys in the returned set must never change.
     */
    Map<String, Course> getCourseIdTokenToCourseMap();



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
        public final String subDirName;

        SubDirectories(String subDirName) {
            this.subDirName = subDirName;
        }
    }

}
