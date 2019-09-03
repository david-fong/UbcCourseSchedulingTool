package com.dvf.ucst.utils.pickybuild;

import java.util.Set;

/**
 * A collection where items cannot be added if they would conflict with entries that
 * have already been added. It is up to the implementation to determine what qualifies
 * as a conflict. Implementations are allowed to contain elements upon construction
 * as long as none of those elements would conflict with each other if manually added
 * through [::addIfNoConflicts] in any sequence (since that method is order-agnostic).
 *
 * @param <E> The type of [PickyBuildElement] that can be added to the implementation.
 */
public interface PickyBuild<E extends PickyBuildElement<E>> {

    /**
     * @return A copy [PickyBuild] of an implementation instance. Cloning depth must
     *     go deep enough that adding items to the clone must not affect the state of
     *     its parent or siblings in any way, and that all following adding operations
     *     performed by [PickyBuildGenerator::generateAllFullPickyBuilds] with the same
     *     arguments and sequence on the clone and its parent must return the same value.
     *     Any other state-related information that has no bearings on these requirements
     *     is free to differ between a newborn clone and its parent.
     */
    PickyBuild<E> copy();

    /**
     * It is up to the implementation to determine what qualifies as a conflict.
     *
     * The return value for this method is allowed to evaluate upon the qualities
     * of [item], and on what [item]s have previously been added, but must not
     * depend on the order in which those previous [item]s were added. It is allowed
     * to depend on the state of previously added [item]s as long as those stateful
     * qualities are guaranteed not to change as the result of any of the operations
     * specified in this interface.
     *
     * Attempts to add an item that has already been added (according to [::equals]
     * comparison) must return [true] and make no changes to the implementation's state.
     *
     * This does not need to directly handle the addition on any [PickyBuildElement]s
     * from [item::getPickyBuildFriends] to itself. This will be managed by the
     * [PickyBuildGenerator] through more calls to this method.
     *
     * @param item An item to add if doing so would not result in any conflicts with
     *     existing items in [this] [PickyBuild].
     * @return True if [item] can be added without conflicts.
     */
    boolean addIfNoConflicts(final E item);

    /**
     * @param others A [Set] of other [PickyBuildElement]s.
     * @return Whether any contents of [others] are already in [this] [PickyBuild]
     *     (according to ::equals comparison). Implementations of this interface
     *     may choose to override this for performance gains. If they do, they must
     *     ensure that from an outside point of view, they follow the exact same
     *     behaviour as this default implementation.
     */
    default boolean containsAny(final Set<E> others) {
        return getAllContents().stream().anyMatch(others::contains);
    }

    /**
     * @return The Set of all [PickyBuildElement]s that were in [this] [PickyBuild]
     *     since its construction, or were added in any previous [::addIfNoConflicts]
     *     operations. For some implementations, this may be equivalent to saying
     *     "all elements that are considered as possible conflict sources during an
     *     [::addIfNoConflicts] operation". The returned [Set] must be unmodifiable.
     */
    Set<E> getAllContents();

}
