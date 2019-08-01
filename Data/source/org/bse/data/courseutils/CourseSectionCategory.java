package org.bse.data.courseutils;

import org.bse.data.courseutils.CourseUtils.Semester;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public final class CourseSectionCategory {

    private final Course parentCourse;
    private final int creditValue;
    private final Set<CourseSection> childSections;

    public CourseSectionCategory (Course parentCourse, int creditValue) {
        this.parentCourse = parentCourse;
        this.creditValue = creditValue;
        this.childSections = new HashSet<>(); // TODO:
    }

    public Course getParentCourse() {
        return parentCourse;
    }
    public int getCreditValue() {
        return creditValue;
    }



    /**
     * Represents a section of a [Course]. On top of the properties provided by a
     * [Course], a [CourseSection] includes information on the lecturer, times and
     * places of meetings, and seating availability and restrictions.
     *
     * TODO: add representation of seating.
     */
    public class CourseSection implements CodeStringPath {

        private final String sectionCode;
        private final Semester semester;
        private final String lecturer = ""; // TODO:
        private final Set<CourseSectionBlock> blocks;

        /**
         *
         * @param sectionCode Ex. "101", or "T2A", or "L1B".
         * @param semester One of four options.
         * @param blocks Should not be modified externally after being passed in.
         */
        public CourseSection(String sectionCode,
                             Semester semester, Set<CourseSectionBlock> blocks) {
            this.sectionCode  = String.format("%s %s", getParentCourse().getFullCodeString(), sectionCode);
            this.semester     = semester;
            this.blocks       = Collections.unmodifiableSet(blocks);
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

    }

}
