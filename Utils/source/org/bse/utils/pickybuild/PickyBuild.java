package org.bse.utils.pickybuild;

/**
 * A collection where items cannot be added if they would conflict with entries that
 * have already been added. It is up to the implementation to determine what qualifies
 * as a conflict.
 *
 * @param <T> The type of item that can be added to a [PickyBuild] implementation.
 */
public interface PickyBuild<T> {

    /**
     * @return A copy [PickyBuild] of an implementation instance. Cloning depth must
     *     go deep enough that adding items to the clone must not affect the state of
     *     its parent or siblings in any way, and that between the point of the
     *     cloning event and any following adding operations, the clone and its parent
     *     must return the same value for any item to their [checkForConflictsWith]
     *     methods. Any other state-related information that has no bearings on these
     *     requirements is free to differ between a newborn clone and its parent.
     */
    PickyBuild<T> copy();

    /**
     * It is up to the implementation to determine what qualifies as a conflict.
     *
     * @param item An item to check if it can be added to [this][PickyBuild] without
     *     conflicting with any existing items.
     * @return True if [item] does not conflict with any current contents.
     */
    boolean conflictsWith(final T item);

    /**
     * It is up to the implementation to determine what qualifies as a conflict.
     * Return value must be consistent with that of [checkForConflictsWith].
     *
     * @param item An item to add if doing so would not result in any conflicts with
     *     existing items in [this][PickyBuild].
     * @return True if [item] can be added without conflicts.
     */
    boolean addIfNoConflicts(final T item);

}
