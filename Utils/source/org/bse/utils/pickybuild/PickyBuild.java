package org.bse.utils.pickybuild;

/**
 * A collection where items cannot be added if they would conflict with entries that
 * have already been added.
 *
 * @param <T> The type of items that can be added to an implementation of [PickyBuild].
 */
public interface PickyBuild<T> {

    /**
     * A constructor alias.
     *
     * @return A copy [PickyBuild] of an implementation instance.
     */
    PickyBuild<T> copy();

    /**
     * It is up to the implementation to determine what qualifies as a conflict.
     *
     * @param item An item to check if it can be added to [this][PickyBuild] without
     *     conflicting with any existing items.
     * @return True if [item] does not conflict with any current contents.
     */
    boolean checkForConflictsWith(final T item);

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
