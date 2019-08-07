package org.bse.data.repr;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * TODO [doc]:
 */
public final class WorklistGroup {

    private static final String NAME_OF_COPY_SUFFIX = "~";

    private final ConcurrentMap<String, Worklist> worklists;

    public WorklistGroup() {
        this.worklists = new ConcurrentHashMap<>();
    }

    /**
     * @param source Another [Worklist] that does not necessarily need to be in
     *     this [WorklistGroup].
     * @return A [String] to be used as a [Worklist] [name] that has at least one
     *     [NAME_OF_COPY_SUFFIX] appended to it, and up to as many as are needed
     *     so there are no conflicts with [Worklist]s already in this [WorklistGroup].
     */
    private String safeNameCopy(Worklist source) {
        String safeName = source.name + NAME_OF_COPY_SUFFIX;
        while (worklists.containsKey(safeName)) {
            safeName += NAME_OF_COPY_SUFFIX;
        }
        return safeName;
    }

    /**
     * @param other A [CourseSchedule] to add a new [Worklist] based on.
     * @param name A name for the new [Worklist]. Operation fails if another
     *     [Worklist] by the same name is already part of this [WorklistGroup].
     * @return [true] if the operation was successful.
     */
    public boolean addNewBasedOn(CourseSchedule other, String name) {
        return worklists.putIfAbsent(name, new Worklist(name, other)) == null;
    }

    /**
     * @param other Another [WorklistGroup]. This operation will fail if [other]
     *     is not from this [WorklistGroup].
     * @return A copy of [other], which is added to this [WorklistGroup].
     */
    public boolean createCopyOf(Worklist other) {
        if (isFromThis(other)) {
            return addNewBasedOn(other, safeNameCopy(other));
        } else {
            return false;
        }
    }

    /**
     *
     * @param worklist A [Worklist]. This operation will fail if [worklist] is
     *     not from this [WorklistGroup].
     * @param newName A [String] to set as the new name for
     * @return [true] if [worklist] is from [this] [WorklistGroup] and [newName]
     *     is not already the name of another [Worklist] in this [WorklistGroup].
     */
    public boolean rename(Worklist worklist, String newName) {
        if (isFromThis(worklist)) {
            worklists.remove(worklist.name);
            return addNewBasedOn(worklist, newName);
        } else {
            return false;
        }
    }

    /**
     * @param worklist A [Worklist] to remove. This operation will fail if [worklist]
     *     is not from this [WorklistGroup].
     * @return [true] if [worklist] is in this [WorklistGroup] and was removed.
     */
    public boolean remove(Worklist worklist) {
        if (isFromThis(worklist)) {
            return worklists.remove(worklist.name) != null;
        } else {
            return false;
        }
    }

    private boolean isFromThis(Worklist worklist) {
        return worklist.getContext() == this;
        // return worklists.get(worklist.name) == worklist;
    }



    /**
     * A mutable wrapper for a [CourseSchedule] object. [name] is immutable.
     * Implementation is final with no public constructors.
     */
    public final class Worklist extends CourseScheduleBuild {

        private final String name;

        private Worklist(String name) {
            this.name = name;
        }

        private Worklist(String name, CourseSchedule other) {
            super(other);
            this.name = name;

            // TODO [impl:cond]: If [Worklist]s later get any other
            //  properties like tags or a favorite flag, copy those here:
//            if (other instanceof Worklist) {
//                ;
//            }
        }

        private WorklistGroup getContext() {
            return WorklistGroup.this;
        }

        @Override
        public Worklist copy() {
            Worklist copy = new Worklist(safeNameCopy(this), this);
            worklists.put(copy.name, copy);
            return copy;
        }

        public String getName() {
            return name;
        }
    }

}
