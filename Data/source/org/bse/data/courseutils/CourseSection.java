package org.bse.data.courseutils;

import org.bse.data.courseutils.CourseUtils.CourseSectionType;
import org.bse.data.courseutils.CourseUtils.Semester;

import java.util.Collections;
import java.util.Set;

/**
 * Represents a section of a [Course]. On top of the properties provided by a
 * [Course], a [CourseSection] includes information on the lecturer, times and
 * places of meetings, and seating availability and restrictions.
 *
 * TODO: add representation of seating.
 */
public class CourseSection implements CodeStringPath {

    private final CourseSectionType sectionType;
    private final Course parentCourse;
    private final String sectionCode;

    private final Semester semester;
    private final Set<CourseSectionBlock> blocks;

    /**
     *
     * @param sectionType One of {LECTURE, LAB, or TUTORIAL}.
     * @param parentCourse Ex. "APSC 160", or "ENGL 112".
     * @param sectionCode Ex. "101", or "T2A", or "L1B".
     * @param semester One of four options.
     * @param blocks Should not be modified externally after being passed in.
     */
    public CourseSection(CourseSectionType sectionType, Course parentCourse, String sectionCode,
                         Semester semester, Set<CourseSectionBlock> blocks) {
        this.sectionType  = sectionType;
        this.parentCourse = parentCourse;
        this.sectionCode  = String.format("%s %s", parentCourse.getFullCodeString(), sectionCode);
        this.semester     = semester;
        this.blocks       = Collections.unmodifiableSet(blocks);
    }

    public CourseSectionType getSectionType() {
        return sectionType;
    }
    public Course getParentCourse() {
        return parentCourse;
    }
    public String getFullCodeString() {
        return sectionCode;
    }
    public Semester getSemester() {
        return semester;
    }
    public Set<CourseSectionBlock> getBlocks() {
        return blocks;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof CourseSection) &&
                getFullCodeString().equals(((CourseSection) other).getFullCodeString());
    }

}
