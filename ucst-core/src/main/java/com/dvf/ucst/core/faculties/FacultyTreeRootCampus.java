package com.dvf.ucst.core.faculties;

import com.dvf.ucst.core.courseutils.Course;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

/**
 * At the top level, everything is first partitioned by campus. All faculties under
 * a common campus must have unique abbreviations.
 *
 * We will go by the hierarchy specified in the second link.
 * - https://www.ubc.ca/our-campuses/vancouver/directories/faculties-schools.html
 * - https://courses.students.ubc.ca/cs/courseschedule?pname=subjarea&tname=subj-all-departments
 */
public interface FacultyTreeRootCampus extends FacultyTreeNode {

    Map<String, Course> EMPTY_COURSE_CODE_MAP = Collections.emptyMap();

    @Override
    default String getNameWithTitle() {
        // Here, the title is a suffix instead of a prefix:
        return getNameNoTitle() + getFacultyType().title;
    }

    @Override
    default FacultyTreeNodeType getFacultyType() {
        return FacultyTreeNodeType.CAMPUS;
    }

    @Override
    UbcCampuses getRootCampus(); // reject default implementation in FacultyTreeNode.

    @Override
    default int getDepth() {
        return 0;
    }

    @Override
    default FacultyTreeNode getParentNode() {
        return null;
    }

    @Override
    default Path getCampusAnchoredPathTo(final FacultyCourseSubDir subDir) {
        return Paths.get(getAbbreviation()).resolve(subDir.getPathToken());
    }

    @Override
    String getRegistrationSiteUrl();

    /**
     * @return An unmodifiable [Map] from faculty names under [this] campus to the
     *     corresponding [FacultyTreeNode]s.
     */
    Map<String, FacultyTreeNode> getSquashedFacultyAbbrMap();

    /**
     * @return An empty [Map] because a campus does not have courses directly under it.
     */
    @Override
    default Map<String, Course> getCourseIdTokenToCourseMap() {
        return EMPTY_COURSE_CODE_MAP;
    }

}
