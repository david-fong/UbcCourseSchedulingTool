package core.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a
 */
public class CourseSection {

    private final Course parentCourse;
    private final SectionCode sectionCode;
    private final ArrayList<CourseSectionBlock> blocks;

    public CourseSection(Course parentCourse) {
        this.parentCourse = parentCourse;
        this.blocks = new ArrayList<>();
    }

    public void addBlock(CourseSectionBlock block) {
        blocks.add(block);
    }

    /**
     * Users of this method should not modify the return value.
     * @return A list of CourseSectionBlock items part of this CourseSection.
     */
    public List<CourseSectionBlock> getBlocks() {
        return blocks;
    }

}
