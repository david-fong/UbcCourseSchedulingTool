package org.bse.utils.pickybuild;

/**
 * A collection where items cannot be added if they would conflict with entries that
 * have already been added. It is up to the implementation to determine what qualifies
 * as a conflict.
 *
 * @param <T> The type of item that can be added to a [PickyBuild] implementation.
 */
public interface PickyBuild<T extends PickyBuildElement<T>> {

    /**
     * @return A copy [PickyBuild] of an implementation instance. Cloning depth must
     *     go deep enough that adding items to the clone must not affect the state of
     *     its parent or siblings in any way, and that between the point of the
     *     cloning event and any following adding operations, the clone and its parent
     *     must return the same value for any item to their [checkForConflictsWith]
     *     methods. Any other state-related information that has no bearings on these
     *     requirements is free to differ between a newborn clone and its parent.
     *     Implementations MUST return an instance of their own type so build-generation
     *     operations are ensured to return builds that are cast-safe to the type
     *     provided to the [PickyBuildGenerator] constructor as a template.
     */
    PickyBuild<T> copy();

    /**
     * It is up to the implementation to determine what qualifies as a conflict.
     * The return value for this method is allowed to evaluate upon the qualities
     * of [item], and on what [item]s have previously been added, but must not
     * depend on the order in which those previous [item]s were added. It is allowed
     * to depend on the state of previously added [item]s as long as those stateful
     * qualities are guaranteed not to change as the result of any of the operations
     * specified in this interface. Attempts to add an item that has already been
     * added should return [true] without any changes to the implementation's state.
     *
     * @param item An item to add if doing so would not result in any conflicts with
     *     existing items in [this][PickyBuild].
     * @return True if [item] can be added without conflicts.
     */
    boolean addIfNoConflicts(final T item);

}
