package org.bse.core.registration.scheduler;

import org.bse.core.registration.CourseUtils.Semester;
import org.bse.core.registration.course.CourseSection;
import org.bse.core.registration.course.CourseSectionBlock;

import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

public class CourseSchedule {

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

    /**
     * The operation will fail with a return value of false if any scheduling conflicts
     * would arise as a result of adding [section] to this [CourseSchedule].
     *
     * @param section A [CourseSection] to test adding to this [CourseSchedule].
     * @return true if the operation was successful.
     */
    public boolean canAddSection(CourseSection section) {
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
    public boolean addSection(CourseSection section) {
        if (canAddSection(section)) {
            // Success (no conflicts will result from the following operation):
            EnumMap<DayOfWeek, Set<CourseSectionBlock>> timetable = timetables.get(section.getSemester());
            courseSections.add(section);
            for (CourseSectionBlock block : section.getBlocks()) {
                timetable.get(block.getDayOfWeek()).add(block);
            }
            return true;
        } else {
            return false;
        }
    }

}
