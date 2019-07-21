package org.bse.core.registration.scheduler;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CourseSchedule getSchedule() {
        return schedule;
    }
}
