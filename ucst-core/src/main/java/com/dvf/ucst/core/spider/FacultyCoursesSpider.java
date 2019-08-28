package com.dvf.ucst.core.spider;

import com.dvf.ucst.core.faculties.FacultyTreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class FacultyCoursesSpider {

    /**
     * does not write any xml.
     */
    public static List<CourseWip> fetchCourses(final FacultyTreeNode facultyNode) {
        final List<CourseWip> courseWips = new ArrayList<>();

        // fetch courses offered under [facultyNode]

        return courseWips;
    }

    // A list of single-token Strings such as "211", "221", or "311".
    private static List<String> getCodesOfCoursesUnderFaculty(final FacultyTreeNode facultyNode) {
        final List<String> courseCodeTokens = new ArrayList<>();

        // fetch html from [facultyNode]'s url.

        return courseCodeTokens;
    }

}
