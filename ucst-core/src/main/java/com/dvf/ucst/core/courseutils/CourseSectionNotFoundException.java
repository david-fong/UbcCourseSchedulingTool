package com.dvf.ucst.core.courseutils;

/**
 *
 */
public class CourseSectionNotFoundException extends Exception {

    public CourseSectionNotFoundException(final Course parentCourse, final String searchString) {
        super(String.format("A \"%s\" by the name \"%s\" could not be found under"
                + " the \"%s\" \"%s\'",
                Course.CourseSection.class,
                searchString,
                Course.class,
                parentCourse.getSystemFullSectionIdString()
        ));
    }

}
