package org.bse.core.registration;

import org.bse.core.registration.CourseUtils.UbcCampus;
import org.bse.core.registration.CourseUtils.YearOfStudy;
import org.bse.core.registration.scheduler.CourseSchedule;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;

/**
 * TODO: write documentation.
 */
public final class Student {

    private final String name;
    private YearOfStudy currentYear;
    private UbcCampus campus;

    // TODO: discuss alternative of a set, and giving the CourseSchedule class a YearOfStudy field.
    private final EnumMap<YearOfStudy, CourseSchedule> previousSchedules;
    private final HashSet<Worklist> worklists; // Worklists must not have the same name.

    public Student(String name, YearOfStudy yearOfStudy, UbcCampus campus) {
        this.name = name;
        this.currentYear = yearOfStudy;
        this.campus = campus;
        this.previousSchedules = new EnumMap<>(YearOfStudy.class);
        this.worklists = new HashSet<>();
    }

    public String getName() {
        return name;
    }
    public YearOfStudy getCurrentYear() {
        return currentYear;
    }
    public UbcCampus getCampus() {
        return campus;
    }
    public Map<YearOfStudy, CourseSchedule> getPreviousSchedules() {
        return previousSchedules;
    }
    public HashSet<Worklist> getWorklists() {
        return worklists;
    }

}