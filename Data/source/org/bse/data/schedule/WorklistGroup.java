package org.bse.data.schedule;

import org.bse.data.schedule.Schedule.SttSchedule;
import org.bse.data.schedule.ScheduleBuild.SttScheduleBuild;
import org.bse.data.schedule.Worklist.SttWorklist;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO [doc]:
 */
public final class WorklistGroup {

    private static final String PLACEHOLDER_NAME = "unnamed";
    private static final String NAME_OF_COPY_SUFFIX = "~";

    private final Map<String, Worklist> worklists;
    private final Set<String> backingNameSet;

    public WorklistGroup() {
        this.worklists = new ConcurrentHashMap<>();
        this.backingNameSet = Collections.unmodifiableSet(worklists.keySet());
    }

    /**
     * @param other A [Schedule] to add a new [Worklist] based on. Must not be null.
     * @param name A name for the new [Worklist]. Operation fails if another
     *     [Worklist] by the same name already exists in this [WorklistGroup].
     *     If null, will attempt to create a name from [other] if it has one, and
     *     otherwise use a generic placeholder name.
     * @return [true] if the operation was successful.
     */
    public boolean addNewBasedOn(Schedule other, String name) {
        final Worklist copy;

        if (other instanceof SttSchedule) {
            if (name == null) name = safeNameCopy(((SttSchedule) other).getName());
            copy = new SttWorklist((SttSchedule) other, name);

        } else if (other instanceof SttScheduleBuild) {
            if (name == null) name = safeNameCopy(PLACEHOLDER_NAME);
            copy = new SttWorklist((SttScheduleBuild) other, name);

        } else if (other instanceof SttWorklist) {
            if (name == null) name = safeNameCopy(((SttWorklist) other).getName());
            copy = new SttWorklist((SttWorklist) other);

        } else if (other instanceof Worklist) {
            if (name == null) name = safeNameCopy(((Worklist) other).getName());
            copy = new Worklist(other, name);

        } else {
            if (name == null) name = safeNameCopy(PLACEHOLDER_NAME);
            copy = new Worklist(other, name);
        }

        return worklists.putIfAbsent(name, copy) == null;
    }

    /**
     * @param other Another [WorklistGroup]. This operation will fail if [other]
     *     is not from this [WorklistGroup].
     * @return A copy of [other], which is added to this [WorklistGroup]. The copy's
     *     name will indicate that it is a copy, and is guaranteed not to be the
     *     same as that of any other [Worklist] already in this [WorklistGroup].
     */
    public boolean createCopyOf(Worklist other) {
        if (isFromThis(other)) {
            return addNewBasedOn(other, null);
        } else {
            return false;
        }
    }

    /**
     * @param worklist A [Worklist]. This operation will fail if [worklist] is
     *     not from this [WorklistGroup].
     * @param newName A [String] to set as the new name for [worklist]. This
     *     operation will fail if another [Worklist] in this [WorklistGroup]
     *     already goes by the name [name].
     * @return [true] if [worklist] is from [this] [WorklistGroup] and [newName]
     *     is not already the name of another [Worklist] in this [WorklistGroup].
     */
    public boolean rename(Worklist worklist, String newName) {
        if (isFromThis(worklist) && !worklist.isLocked() && !worklists.containsKey(newName)) {
            worklists.remove(worklist.getName(), worklist);
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
        if (isFromThis(worklist) && !worklist.isLocked()) {
            return worklists.remove(worklist.getName(), worklist);
        } else {
            return false;
        }
    }

    /**
     * @param name A string to get a safe version of to use as a name.
     * @return A [String] to be used as a [Worklist] [name] that has as many of
     *     the [NAME_OF_COPY_SUFFIX] appended to it as are needed so there are
     *     no conflicts with [Worklist]s already in this [WorklistGroup].
     */
    private String safeNameCopy(String name) {
        String safeName = name;
        while (worklists.containsKey(safeName)) {
            safeName += NAME_OF_COPY_SUFFIX;
        }
        return safeName;
    }

    private boolean isFromThis(Worklist worklist) {
        // return worklist.getContext() == this;
        return worklists.get(worklist.getName()) == worklist;
    }

    /**
     * @return An unmodifiable view of the [Set] of all [Worklist] names in [this]
     *     [WorklistGroup].
     */
    public Set<String> getNames() {
        return backingNameSet;
    }

}
