package org.bse.data.repr;

import org.bse.data.repr.courseutils.Course;
import org.bse.utils.pickybuild.PickyBuild;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A mutable container for [CourseSection]s.
 */
class CourseScheduleBuild extends CourseSchedule implements PickyBuild<Course.CourseSection> {

    public static final Supplier<CourseScheduleBuild> EMPTY_COURSE_SCHEDULE_SUPPLIER = CourseScheduleBuild::new;

    private final HashSet<Course.CourseSection> courseSections;

    CourseScheduleBuild() {
        this.courseSections = new HashSet<>();
    }

    CourseScheduleBuild(CourseSchedule other) {
        this.courseSections = new HashSet<>(other.getCourseSections());
    }

    @Override
    public Set<Course.CourseSection> getCourseSections() {
        return Collections.unmodifiableSet(courseSections);
    }

    @Override
    public CourseScheduleBuild copy() {
        return new CourseScheduleBuild(this);
    }

    /**
     * The operation will fail with a return value of false if any scheduling conflicts
     * would arise as a result of adding [section] to this [CourseSchedule].
     *
     * @param section A [CourseSection] to test adding to this [CourseSchedule].
     * @return [true] if [section] can be added without conflicts.
     */
    @Override
    public boolean conflictsWith(Course.CourseSection section) {
        return getCourseSections().stream().noneMatch(section::overlapsWith);
    }

    /**
     * The operation will fail with a return value of false if any scheduling conflicts
     * would arise as a result of adding [section] to this [CourseSchedule].
     *
     * @param section A [CourseSection] to attempt to add to this [CourseSchedule].
     * @return [true] if the operation was successful.
     */
    @Override
    public boolean addIfNoConflicts(Course.CourseSection section) {
        final boolean canAdd = conflictsWith(section);
        if (canAdd) {
            courseSections.add(section);
        }
        return canAdd;
    }

    protected boolean removeSection(Course.CourseSection section) {
        return courseSections.remove(section);
    }

}
