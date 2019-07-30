package org.bse.data.courseutils;

import org.bse.data.courseutils.CourseUtils.Semester;
import org.bse.utils.pickybuild.PickyBuild;

import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class CourseSchedule implements PickyBuild<CourseSection> {

    public static final Supplier<CourseSchedule> EMPTY_COURSE_SCHEDULE_SUPPLIER = CourseSchedule::new;

    private final HashSet<CourseSection> courseSections;
    private final EnumMap<Semester, EnumMap<DayOfWeek, Set<CourseSectionBlock>>> timetables;

    public CourseSchedule() {
        this.courseSections = new HashSet<>();
        this.timetables = new EnumMap<>(Semester.class);
        for (Semester semester : Semester.values()) {
            EnumMap<DayOfWeek, Set<CourseSectionBlock>> timetable = new EnumMap<>(DayOfWeek.class);
            for (DayOfWeek day : DayOfWeek.values()) {
                timetable.put(day, new HashSet<>());
            }
            timetables.put(semester, timetable);
        }
    }

    private CourseSchedule(CourseSchedule other) {
        this.courseSections = new HashSet<>(other.courseSections);
        this.timetables = new EnumMap<>(Semester.class);
        for (Map.Entry<Semester, EnumMap<DayOfWeek, Set<CourseSectionBlock>>>
                semester : other.timetables.entrySet()) {
            EnumMap<DayOfWeek, Set<CourseSectionBlock>> dayToBlockMap = new EnumMap<>(DayOfWeek.class);
            for (Map.Entry<DayOfWeek, Set<CourseSectionBlock>>
                    dayToBlockEntry : semester.getValue().entrySet()) {
                dayToBlockMap.put(
                        dayToBlockEntry.getKey(),
                        new HashSet<>(dayToBlockEntry.getValue())
                );
            }
            timetables.put(semester.getKey(), dayToBlockMap);
        }

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
        if (!courseSections.contains(section)) {
            EnumMap<DayOfWeek, Set<CourseSectionBlock>> timetable = timetables.get(section.getSemester());
            for (CourseSectionBlock block : section.getBlocks()) {
                if (timetable.get(block.getDayOfWeek()).stream()
                        .anyMatch(existingBlock -> existingBlock.overlapsWith(block))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
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
        if (checkForConflictsWith(section)) {
            // Success (no conflicts will result from the following operation):
            EnumMap<DayOfWeek, Set<CourseSectionBlock>> semesterTimeTable
                    = timetables.get(section.getSemester());
            courseSections.add(section);
            for (CourseSectionBlock block : section.getBlocks()) {
                semesterTimeTable.get(block.getDayOfWeek()).add(block);
            }
            return true;
        } else {
            return false;
        }
    }

}
