package org.bse.core.registration;

import org.bse.core.registration.course.CourseSectionBlock;

import java.util.HashSet;

public class CourseSchedule {

    private final HashSet<CourseSectionBlock> courses;

    public CourseSchedule() {
        this.courses = new HashSet<>();
    }

}
