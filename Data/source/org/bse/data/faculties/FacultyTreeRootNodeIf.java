package org.bse.data.faculties;

import org.bse.data.courseutils.Course;

import java.util.Map;

/**
 * At the top level, everything is first partitioned by campus.
 */
public interface FacultyTreeRootNodeIf extends FacultyTreeNodeIf {

    Map<String, Course> EMPTY_COURSE_CODE_MAP = Map.of();

    String getCampusFolderName();

    @Override
    default FacultyTreeNodeIf getParentNode() {
        return null;
    }

    @Override
    default Map<String, Course> getCodeStringToCourseMap() {
        return EMPTY_COURSE_CODE_MAP;
    }

}
