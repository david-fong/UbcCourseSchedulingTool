package org.bse.data.repr.courseutils;

import org.bse.data.repr.courseutils.CourseUtils.Semester;
import org.bse.utils.requirement.operators.matching.CreditValued;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.Set;

/**
 * A [Course] object will have three [CourseSectionCategory] fields: one each
 * for all existing lecture sections, lab sections, and tutorial sections.
 */
public class CourseSectionCategory implements CreditValued {

    private final Course parentCourse;
    private final int creditValue;
    private final Set<? extends CourseSection> childSections;

    public static CourseSectionCategory fromXml(final Element courseSectionCategoryElement) {
        return null; // TODO:
    }

    protected static CourseSection createSectionFromXml(final Element courseSectionElement) {
        return null; // TODO:
    }

    CourseSectionCategory (Course parentCourse, int creditValue, Set<? extends CourseSection> childSections) {
        this.parentCourse = parentCourse;
        this.creditValue = creditValue;
        this.childSections = Collections.unmodifiableSet(childSections);
    }

    @Override
    public final int getCreditValue() {
        return creditValue;
    }

    public Set<? extends CourseSection> getChildSections() {
        return childSections;
    }


    /**
     * Represents a section of a [Course]. On top of the properties provided by a
     * [Course], a [CourseSection] includes information on the lecturer, times and
     * places of meetings, and seating availability and restrictions.
     *
     * TODO: add representation of seating.
     */
    public class CourseSection implements CodeStringPath, CreditValued {

        protected final String sectionCode;
        protected final Semester semester;
        protected final String lecturer = ""; // TODO:
        protected final Set<CourseSectionBlock> blocks;

        /**
         * @param sectionCode Ex. "101", or "T2A", or "L1B".
         * @param semester One of four options.
         * @param blocks Should not be modified externally after being passed in.
         */
        protected CourseSection(String sectionCode, Semester semester, Set<CourseSectionBlock> blocks) {
            this.sectionCode  = String.format("%s %s", getParentCourse().getFullCodeString(), sectionCode);
            this.semester     = semester;
            this.blocks       = Collections.unmodifiableSet(blocks);
        }

        public final boolean overlapsWith(CourseSection other) {
            return semester == other.semester && blocks.stream().anyMatch(block ->
                    other.blocks.stream().anyMatch(block::overlapsWith)
            );
        }

        public final Course getParentCourse() {
            return parentCourse;
        }

        public final String getFullCodeString() {
            return parentCourse.getFullCodeString() + sectionCode;
        }

        public final Semester getSemester() {
            return semester;
        }

        public final Set<CourseSectionBlock> getBlocks() {
            return blocks;
        }

        @Override
        public final int getCreditValue() {
            return creditValue;
        }
    }

}
