package org.bse.core.registration;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a section of a [Course].
 */
public class CourseSection {

    private final Course parentCourse;
    private final SectionCode sectionCode;
    private final HashSet<CourseSectionBlock> blocks;

    public CourseSection(Course parentCourse, SectionCode sectionCode, Collection<CourseSectionBlock> blocks) {
        this.parentCourse = parentCourse;
        this.sectionCode = sectionCode;
        this.blocks = new HashSet<>(blocks);
    }

    public Course getParentCourse() {
        return parentCourse;
    }

    public SectionCode getSectionCode() {
        return sectionCode;
    }

    /**
     * Users of this method should not modify the return value.
     * @return A list of CourseSectionBlock items part of this CourseSection.
     */
    public Set<CourseSectionBlock> getOfferedBlocks() {
        return blocks;
    }



    // TODO:
    public static final class SectionCode {

        private final CourseUtils.Term term = null; // TODO
        private final String stringVal = null; // TODO

    }

}
