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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

/**
 * A property of a [Course].
 * All implementations should call [verifyProperTree(this)] at the end of their constructors.
 */
public interface FacultyTreeNode extends HyperlinkBookIf {

    String getNameNoTitle();

    default String getNameWithTitle() {
        return getFacultyType().title + getNameNoTitle();
    }

    String getAbbreviation();

    FacultyTreeNodeType getFacultyType();

    // implementation note: UbcCampuses must break recursion by returning "this" (ie. themselves).
    default UbcCampuses getRootCampus() {
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
     *     [null]. Any other implementation must not return [null]. Chained calls to
     *     this method should end with a [NullPointerException] caused by a call from
     *     a [FacultyTreeRootCampus]. There should be no loops.
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
    default Path getCampusAnchoredPathTo(final FacultyCourseSubDir subDir) {
        return getParentNode().getCampusAnchoredPathTo(FacultyCourseSubDir.CHILD_FACULTY_NODES)
                .resolve(getAbbreviation())
                .resolve(subDir.getPathToken());
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
                    getCampusAnchoredPathTo(FacultyCourseSubDir.COURSE_XML_DATA)
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

    default String getSubTreeString() {
        final StringJoiner treeStringJoiner = new StringJoiner("\n");
        getSubTreeString(0, treeStringJoiner);
        return treeStringJoiner.toString();
    }
    private void getSubTreeString(final int scrubTabLevel, final StringJoiner stringJoiner) {
        stringJoiner.add(
                new String(new char[scrubTabLevel]).replace("\0", "   ") // <- String repetition
                        + "- " + getAbbreviation()
        );
        for (final FacultyTreeNode child : getChildren()) {
            child.getSubTreeString(scrubTabLevel + 1, stringJoiner);
        }
    }

    /**
     * Should only be used for assertions for each FacultyTreeNode implementation
     * after their construction (so their parent can statically load). Verifies
     * that the upward path contains absolutely no cycles, and that it ends with an
     * instance of [UbcCampuses] at the root of the tree. Throws a [RuntimeException]
     * if those conditions are not found to be met.
     *
     * @param nodeUnderTest The node to search upwards and verify a valid path for.
     */
    static void verifyProperTree(final FacultyTreeNode nodeUnderTest) {
        System.out.println(nodeUnderTest.getAbbreviation());
        final Set<FacultyTreeNode> encounteredNodes = new LinkedHashSet<>();
        FacultyTreeNode scrub = nodeUnderTest;
        do {
            if (!encounteredNodes.add(scrub)) {
                // if the set of encountered nodes already contained the scrub node,
                // that means there is a loop. throw a RuntimeException.
                throw new RuntimeException(String.format("The faculty tree must not"
                        + " contain any cycles, but one was found with the elements"
                        + " %s", encounteredNodes

                ));
            } else {
                // verify that the spec for getParentNode is being followed.
                // ie. that returning null is equivalent to being an instance
                // of [FacultyTreeRootCampus].
                if (scrub.getParentNode() == null && !(scrub instanceof FacultyTreeRootCampus)) {
                    System.out.println(encounteredNodes);
                    throw new RuntimeException(String.format("[%s]s with a %s parent must be"
                            + " instances of [%s], but this was not true for the node %s",
                            FacultyTreeNode.class,
                            null,
                            FacultyTreeRootCampus.class,
                            nodeUnderTest.getAbbreviation()
                    ));
                }
            }
            scrub = scrub.getParentNode();
        } while(scrub != null); // unnecessary check.
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
    enum FacultyCourseSubDir {
        THIS (""),
        CHILD_FACULTY_NODES ("childnodes"),
        COURSE_XML_DATA ("coursedata"),
        STANDARD_TIMETABLES ("stts"), // an xml file for each year of study.
        ;
        private final Path pathToken;

        FacultyCourseSubDir(final String pathTokenString) {
            this.pathToken = Paths.get(pathTokenString);
        }

        public Path getPathToken() {
            return pathToken;
        }
    }

}
