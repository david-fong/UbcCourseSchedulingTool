package org.bse.data.schedule;

import org.bse.data.repr.courseutils.Course.CourseSection;
import org.bse.utils.pickybuild.PickyBuild;

import java.util.HashSet;
import java.util.Set;

/**
 * Used when generating schedules.
 */
public class ScheduleBuild extends Schedule implements PickyBuild<CourseSection> {

    // defensively copies.
    ScheduleBuild(Schedule other) {
        super(new HashSet<>(other.getCourseSections()));
    }

    @Override
    public boolean isBasedOffAnStt() {
        return false;
    }

    @Override
    public ScheduleBuild copy() {
        return new ScheduleBuild(this);
    }

    /**
     * The operation will fail with a return value of false if any scheduling conflicts
     * would arise as a result of adding [section] to this [Schedule].
     *
     * @param section A [CourseSection] to test adding to this [Schedule].
     * @return [true] if [section] can be added without conflicts.
     */
    @Override
    public boolean conflictsWith(CourseSection section) {
        return courseSections.contains(section)
                || getCourseSections().stream()
                .noneMatch(section::overlapsWith);
    }

    /**
     * The operation will fail with a return value of false if any scheduling conflicts
     * would arise as a result of adding [section] to this [Schedule].
     *
     * @param section A [CourseSection] to attempt to add to this [Schedule].
     * @return [true] if the operation was successful.
     */
    @Override
    public final boolean addIfNoConflicts(CourseSection section) {
        final boolean canAdd = conflictsWith(section);
        if (canAdd) {
            courseSections.add(section);
        }
        return canAdd;
    }



    /**
     *
     */
    public static final class SttScheduleBuild extends ScheduleBuild {

        // private final String name; // name is just deadweight when generating builds.
        private final Set<CourseSection> sttSections;

        SttScheduleBuild(SttSchedule other) {
            super(other);
            this.sttSections = other.getSttSections();
        }

        private SttScheduleBuild(SttScheduleBuild other) {
            super(other);
            this.sttSections = other.sttSections;
        }

        @Override
        public boolean isBasedOffAnStt() {
            return true;
        }

        @Override
        public SttScheduleBuild copy() {
            return new SttScheduleBuild(this);
        }

        public Set<CourseSection> getSttSections() {
            return sttSections;
        }
    }

}