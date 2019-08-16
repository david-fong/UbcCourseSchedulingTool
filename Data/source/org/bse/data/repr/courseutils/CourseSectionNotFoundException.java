package org.bse.data.repr.courseutils;

/**
 *
 */
public class CourseSectionNotFoundException extends Exception {

    public CourseSectionNotFoundException(final Course parentCourse, final String searchString) {
        super(String.format("A \"%s\" by the name \"%s\" could not be found under"
                + " the \"%s\" \"%s\'", Course.CourseSection.class.getName(), searchString,
                Course.class.getName(), parentCourse.toString()
        ));
    }

}
