package org.bse.data.repr.courseutils;

import java.util.Collections;
import java.util.Set;

/**
 *
 */
public final class CourseSectionLectureCategory extends CourseSectionCategory {

    private final Set<CourseLectureSection> childSections;

    private CourseSectionLectureCategory (Course parentCourse, int creditValue, Set<CourseLectureSection> childSections) {
        super(parentCourse, creditValue, childSections);
        this.childSections = Collections.unmodifiableSet(childSections);
    }

    @Override
    public Set<CourseLectureSection> getChildSections() {
        return childSections;
    }

    /**
     *
     */
    public final class CourseLectureSection extends CourseSection {

        private final Set<CourseSection> requiredLabOptions;
        private final Set<CourseSection> requiredTutorialOptions;

        protected CourseLectureSection(String sectionCode, CourseUtils.Semester semester, Set<CourseSectionBlock> blocks,
                                       Set<CourseSection> requiredLabOptions, Set<CourseSection> requiredTutorialOptions) {
            super(sectionCode, semester, blocks);
            this.requiredLabOptions = requiredLabOptions;
            this.requiredTutorialOptions = requiredTutorialOptions;
        }

        public Set<CourseSection> getRequiredLabOptions() {
            return requiredLabOptions;
        }

        public Set<CourseSection> getRequiredTutorialOptions() {
            return requiredTutorialOptions;
        }
    }

}
