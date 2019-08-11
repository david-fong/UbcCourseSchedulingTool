package org.bse.data.repr;

import org.bse.data.repr.courseutils.Course;
import org.bse.data.repr.courseutils.CourseUtils.YearOfStudy;
import org.bse.data.repr.faculties.FacultyTreeRootCampus;
import org.bse.data.schedule.Schedule;
import org.bse.data.schedule.WorklistGroup;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * TODO [doc]: write documentation.
 */
public final class Student {

    private final String name;
    private YearOfStudy currentYear;
    private FacultyTreeRootCampus.UbcCampuses campus;

    private final Map<YearOfStudy, Schedule> previousSchedules;
    private final Map<YearOfStudy, WorklistGroup> worklistGroups; // Worklists must not have the same name.

    public Student(String name, YearOfStudy yearOfStudy, FacultyTreeRootCampus.UbcCampuses campus) {
        this.name = name;
        this.currentYear = yearOfStudy;
        this.campus = campus;
        this.previousSchedules = new EnumMap<>(YearOfStudy.class);
        this.worklistGroups = new EnumMap<>(YearOfStudy.class);
    }

    public String getName() {
        return name;
    }

    public YearOfStudy getCurrentYear() {
        return currentYear;
    }

    public FacultyTreeRootCampus.UbcCampuses getCampus() {
        return campus;
    }

    public Map<YearOfStudy, Schedule> getPreviousSchedules() {
        return previousSchedules;
    }

    public Map<YearOfStudy, WorklistGroup> getWorklistGroups() {
        return worklistGroups;
    }

    public void setCurrentYear(YearOfStudy yearOfStudy) {
        this.currentYear = yearOfStudy;
    }

    public void setCampus(FacultyTreeRootCampus.UbcCampuses campus) {
        this.campus = campus;
    }

    public Set<Course> getCompletedCourses() {
        return null; // TODO:
    }

}
