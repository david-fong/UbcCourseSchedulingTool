package org.bse.data.repr;

import org.bse.data.repr.courseutils.Course;

import java.util.Collections;
import java.util.Set;

/**
 *
 */
public abstract class CourseSchedule {

    /**
     * @return An unmodifiable set of sections contained in [this] [CourseSchedule]
     */
    public abstract Set<Course.CourseSection> getCourseSections();

    /**
     * @return An immutable copy of an implementing instance, with none of its
     *     unique behaviour. This may be called, for instance, when a student
     *     has successfully registered into a [Worklist] (a subclass of
     *     [CourseSchedule]), and no longer requires any of its mutable behaviour.
     *     Since [CourseSection]s are immutable, only a shallow copy is done.
     */
    public final CourseSchedule createImmutableCopy() {
        return new ImmutableCourseSchedule(getCourseSections());
    }



    class ImmutableCourseSchedule extends CourseSchedule {

        private Set<Course.CourseSection> courseSections;

        public ImmutableCourseSchedule(Set<Course.CourseSection> courseSections) {
            this.courseSections = Collections.unmodifiableSet(courseSections);
        }

        @Override
        public Set<Course.CourseSection> getCourseSections() {
            return courseSections;
        }
    }

}
