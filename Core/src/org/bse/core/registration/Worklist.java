package org.bse.core.registration;

/**
 * Wrapper for a [CourseSchedule] object.
 */
public final class Worklist {

    private String name;
    private final CourseSchedule schedule;

    public Worklist(String name) {
        this.name = name;
        this.schedule = new CourseSchedule();
    }

}
