package org.bse.core.registration;

import org.bse.core.registration.CourseUtils.YearOfStudy;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;

/**
 * TODO: write documentation.
 */
public final class Student {

    private final String name;
    private final YearOfStudy currentYear;

    // TODO: discuss alternative of a set, and giving the CourseSchedule class a YearOfStudy field.
    private final EnumMap<YearOfStudy, CourseSchedule> previousSchedules;

    private final HashSet<Worklist> worklists;

    public Student(String name, YearOfStudy yearOfStudy) {
        this.name = name;
        this.currentYear = yearOfStudy;
        this.previousSchedules = new EnumMap<YearOfStudy, CourseSchedule>(YearOfStudy.class);
        this.worklists = new HashSet<>();
    }

    public String getName() {
        return name;
    }
    public YearOfStudy getCurrentYear() {
        return currentYear;
    }
    public Map<YearOfStudy, CourseSchedule> getPreviousSchedules() {
        return previousSchedules;
    }

}
