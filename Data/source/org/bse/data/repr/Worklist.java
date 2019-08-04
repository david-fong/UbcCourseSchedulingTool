package org.bse.data.repr;

/**
 * A mutable wrapper for a [CourseSchedule] object.
 */
public final class Worklist extends CourseScheduleBuild {

    private String name;

    public Worklist(String name) {
        this.name = name;
    }

    private Worklist(Worklist other) {
        super(other);
        name = other.name + "~";
    }

    @Override
    public Worklist copy() {
        return new Worklist(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
