package org.bse.data.repr.courseutils;

import org.w3c.dom.Element;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public final class CourseSectionLectureCategory extends CourseSectionCategory {

    private final Set<CourseLectureSection> childSections;

    private CourseSectionLectureCategory (Course parentCourse, Set<CourseLectureSection> childSections) {
        super(parentCourse, childSections);
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

        CourseLectureSection(Element lectureElement) {
            super(lectureElement);

            // TODO:
            this.requiredLabOptions = new HashSet<>();
            this.requiredTutorialOptions = new HashSet<>();
        }

        /**
         * @return A non-empty set of [CourseSection]s for labs from which a student
         *     must register for ONE to be considered taking the [Course] returned by
         *     [getParentCourse], or null if that [Course] has no labs.
         */
        public Set<CourseSection> getRequiredLabOptions() {
            return requiredLabOptions;
        }

        /**
         * @return A non-empty set of [CourseSection]s for tutorials from which a student
         *     must register for ONE to be considered taking the [Course] returned by
         *     [getParentCourse], or null if that [Course] has no tutorials.
         */
        public Set<CourseSection> getRequiredTutorialOptions() {
            return requiredTutorialOptions;
        }
    }

}
