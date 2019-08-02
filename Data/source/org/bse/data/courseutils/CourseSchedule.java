package org.bse.data.courseutils;

import org.bse.data.courseutils.CourseSectionCategory.CourseSection;
import org.bse.utils.pickybuild.PickyBuild;

import java.util.HashSet;
import java.util.function.Supplier;

/**
 *
 */
public final class CourseSchedule implements PickyBuild<CourseSection> {

    public static final Supplier<CourseSchedule> EMPTY_COURSE_SCHEDULE_SUPPLIER = CourseSchedule::new;

    private final HashSet<CourseSection> courseSections;

    public CourseSchedule() {
        this.courseSections = new HashSet<>();
    }

    private CourseSchedule(CourseSchedule other) {
        this.courseSections = new HashSet<>(other.courseSections);
    }

    @Override
    public CourseSchedule copy() {
        return new CourseSchedule(this);
    }

    /**
     * The operation will fail with a return value of false if any scheduling conflicts
     * would arise as a result of adding [section] to this [CourseSchedule].
     *
     * @param section A [CourseSection] to test adding to this [CourseSchedule].
     * @return true if the operation was successful.
     */
    @Override
    public boolean checkForConflictsWith(CourseSection section) {
        return courseSections.stream().noneMatch(section::overlapsWith);
    }

    /**
     * The operation will fail with a return value of false if any scheduling conflicts
     * would arise as a result of adding [section] to this [CourseSchedule].
     *
     * @param section A [CourseSection] to attempt to add to this [CourseSchedule].
     * @return true if the operation was successful.
     */
    @Override
    public boolean addIfNoConflicts(CourseSection section) {
        final boolean canAdd = checkForConflictsWith(section);
        if (canAdd) courseSections.add(section);
        return canAdd;
    }

}
