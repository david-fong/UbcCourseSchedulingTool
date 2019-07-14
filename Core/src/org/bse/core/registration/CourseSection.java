package org.bse.core.registration;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a section of a [CourseIf].
 */
public class CourseSection {

    private final CourseIf parentCourse;
    private final SectionCode sectionCode;
    private final HashSet<CourseSectionBlock> blocks;

    public CourseSection(CourseIf parentCourse, SectionCode sectionCode, Collection<CourseSectionBlock> blocks) {
        this.parentCourse = parentCourse;
        this.sectionCode = sectionCode;
        this.blocks = new HashSet<>(blocks);
    }

    /**
     * Users of this method should not modify the return value.
     * @return A list of CourseSectionBlock items part of this CourseSection.
     */
    public Set<CourseSectionBlock> getOfferedBlocks() {
        return blocks;
    }

    public CourseIf getParentCourse() {
        return parentCourse;
    }



    // TODO:
    public static final class SectionCode {

        private CourseUtils.Term term;
        private final String stringVal;

    }

}
