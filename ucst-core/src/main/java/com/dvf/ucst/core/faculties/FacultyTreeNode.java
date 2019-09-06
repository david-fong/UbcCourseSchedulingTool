package com.dvf.ucst.core.faculties;

import com.dvf.ucst.core.HyperlinkBookIf;
import com.dvf.ucst.core.UbcLocalFiles;
import com.dvf.ucst.core.UbcLocalFiles.UbcLocalDataCategory.CourseSubDirs;
import com.dvf.ucst.core.coursedata.CourseDataLocator;
import com.dvf.ucst.core.courseutils.Course;
import com.dvf.ucst.utils.xml.MalformedXmlDataException;
import com.dvf.ucst.utils.xml.XmlIoUtils;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A property of a [Course].
 * All implementations should call [verifyProperTree(this)] at the end of their constructors.
 */
public interface FacultyTreeNode extends HyperlinkBookIf, UbcLocalFiles {

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

    @Override
    default String getRegistrationSiteUrl() {
        return RegistrationSubjAreaQuery.getFacultyUrl(this);
    }

    @Override
    default Path getLocalDataPath() {
        return getParentNode().getLocalDataPath()
                .resolve(CourseSubDirs.SUB_FACULTIES.getSubDirName())
                .resolve(getAbbreviation());
    }

    /**
     *
     * @param courseIdToken Must not be null. Ex "101". This operation will only
     *     succeed if a course by this id token exists.
     * @return The course registered by the code [courseIdToken].
     * @throws FacultyCourseNotFoundException if a file could not be located under
     *     [this][FacultyTreeNode] following the path spec during lazy initialization
     *     of the [Course] registered by the code [courseIdToken].
     */
    default Course getCourseByCodeString(final String courseIdToken) throws FacultyCourseNotFoundException {
        if (!getCourseIdTokenToCourseMap().containsKey(courseIdToken)) {
            final String message = "code string not registered in faculty tree node";
            throw new FacultyCourseNotFoundException(message, this);
        }
        Course course = getCourseIdTokenToCourseMap().get(courseIdToken);
        if (course != null) {
            // the course has already been loaded from its xml file.
            return course;
        } else {
            // the course has not yet been loaded from its xml file.
            final Path coursePath = CourseDataLocator.StagedDataPath.POST_DEPLOYMENT.path
                    .resolve(Course.getLocalDataPath(this, courseIdToken));
            try {
                // try to reconstruct the course from xml:
                course = new Course(XmlIoUtils.readXmlFromFile(coursePath).getDocumentElement());
            } catch (SAXException | IOException e) {
                throw new RuntimeException("could not get xml from file", e);
            } catch (MalformedXmlDataException e) {
                throw new RuntimeException("malformed xml course data", e);
            }
            getCourseIdTokenToCourseMap().put(courseIdToken, course);
            return course;
        }
    }

    /**
     * @return A map from course code strings to [Course]s. Must not be [null].
     *     All existing [Course]s under the implementing [FacultyTreeNode]
     *     must have their code string as a key after an implementation's
     *     construction. Keys in the returned set must never change. Do not use
     *     this method to get a [Course], since that course may not have been
     *     loaded from its xml file yet. Instead, use [::getCourseByCodeString].
     */
    Map<String, Course> getCourseIdTokenToCourseMap();

    /**
     * Not for public use. To be called after all enum constructors. Populates map
     * with keys of file names without their xml file extensions, mapping them to
     * [null].
     * TODO: make a call for this somewhere through all campuses and forEachChild.
     */
    default void initCourseIdTokenToCourseMap() {
        final Path anchoringDirectory = Paths.get(""); // TODO: what will the anchoring directory be?
        for (final CourseSubDirs courseSubDirs : CourseSubDirs.values()) {
            try {
                Files.createDirectories(anchoringDirectory
                        .resolve(getLocalDataPath())
                        .resolve(courseSubDirs.getSubDirName())
                );
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    default String getSubTreeString() {
        final StringJoiner treeStringJoiner = new StringJoiner("\n");
        //getSubTreeString(0, treeStringJoiner);
        forEachChild((treeNode, depth) -> {
            treeStringJoiner.add(
                    new String(new char[depth]).replace("\0", "   ") // <- String repetition
                            + "- " + treeNode.getAbbreviation() + ": " + treeNode.getLocalDataPath()
            );
        });
        return treeStringJoiner.toString();
    }

    /**
     * The utmost care should be taken not to accidentally use instance methods when
     * methods applied through the first argument of [action] are meant to be used.
     *
     * @param action A [BiConsumer] of a [FacultyTreeNode] and a depth starting from
     *     zero that should be applied for each child recursively in a depth-first
     *     manner, with [this] [FacultyTreeNode] treated as the root.
     */
    default void forEachChild(final BiConsumer<FacultyTreeNode, Integer> action) {
        forEachChild(this, action, 0);
    }
    private static void forEachChild(
            final FacultyTreeNode scrub,
            final BiConsumer<FacultyTreeNode, Integer> action,
            final int depth
    ) {
        for (final FacultyTreeNode childNode : scrub.getChildren()) {
            action.accept(childNode, depth + 1);
            forEachChild(childNode, action, depth + 1);
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

}
