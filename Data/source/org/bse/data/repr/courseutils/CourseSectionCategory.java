package org.bse.data.repr.courseutils;

import org.bse.data.repr.Professor;
import org.bse.data.repr.courseutils.CourseUtils.Semester;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A [Course] object will have three [CourseSectionCategory] fields: one each
 * for all existing lecture sections, lab sections, and tutorial sections.
 */
public class CourseSectionCategory {

    private final Course parentCourse;
    private final Set<? extends CourseSection> childSections;

    public static CourseSectionCategory fromXml(final Element courseSectionCategoryElement) {
        return null; // TODO:
    }

    protected static CourseSection createSectionFromXml(final Element courseSectionElement) {
        return null; // TODO:
    }

    CourseSectionCategory (Course parentCourse, Set<? extends CourseSection> childSections) {
        this.parentCourse = parentCourse;
        this.childSections = Collections.unmodifiableSet(childSections);
    }

    public Set<? extends CourseSection> getChildSections() {
        return childSections;
    }


    /**
     * Represents a section of a [Course]. On top of the properties provided by a
     * [Course], a [CourseSection] includes information on the lecturer, times and
     * places of meetings, and seating availability and restrictions.
     *
     * All code tokens ([CodeStringPath]) for instances under a common [Course]
     * should be unique.
     *
     * TODO: add representation of seating.
     */
    public class CourseSection implements CodeStringPath {

        private final String sectionCode;
        private final Semester semester;
        private final Professor professor;
        private final Set<CourseSectionBlock> blocks;

        CourseSection(Element sectionElement) {
            sectionCode = null;
            semester = null;
            professor = null; // See [Professor.fromXml]
            blocks = new HashSet<>();
            // TODO
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

        public final Professor getProfessor() {
            return professor;
        }

        public final Set<CourseSectionBlock> getBlocks() {
            return blocks;
        }
    }



    public enum Xml {

    }

}
