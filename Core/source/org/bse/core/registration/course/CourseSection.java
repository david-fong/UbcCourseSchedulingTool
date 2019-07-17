package org.bse.core.registration.course;


import org.bse.core.registration.CourseUtils.Semester;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a section of a [Course]. On top of the properties provided by a
 * [Course], a [CourseSection] includes information on the lecturer, times and
 * places of meetings, and seating availability and restrictions.
 *
 * TODO: add representation of seating.
 */
public class CourseSection implements CodeStringRegistered {

    private final Course parentCourse;
    private final String sectionCode;
    private final Semester semester;
    private final HashSet<CourseSectionBlock> blocks;

    public CourseSection(Course parentCourse, String sectionCode,
                         Semester semester, Collection<CourseSectionBlock> blocks) {
        this.parentCourse = parentCourse;
        this.sectionCode = String.format("%s %s", parentCourse.getCodeString(), sectionCode);
        this.semester = semester;
        this.blocks = new HashSet<>(blocks);
    }

    public Course getParentCourse() {
        return parentCourse;
    }
    public String getCodeString() {
        return sectionCode;
    }
    public Semester getSemester() {
        return semester;
    }

    /**
     * Users of this method should not modify the return value.
     * @return A list of CourseSectionBlock items part of this CourseSection.
     */
    public Set<CourseSectionBlock> getBlocks() {
        return blocks;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof CourseSection) &&
                getCodeString().equals(((CourseSection) other).getCodeString());
    }

}
