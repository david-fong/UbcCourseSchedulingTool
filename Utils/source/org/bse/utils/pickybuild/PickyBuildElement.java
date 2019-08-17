package org.bse.utils.pickybuild;

import java.util.Set;

/**
 * An element of a [PickyBuild]. May have friends that it wants to pull in too.
 * @param <I> The implementation type.
 */
public interface PickyBuildElement<I extends PickyBuildElement> {

    /**
     * @return A conjunctive-normal-formed collection of other [PickyBuildElement]s
     *     that this [PickyBuildElement] requires a [PickyBuild] to also accommodate
     *     for the build to be considered complete. Must never return [null]. May
     *     return an empty [Set] to signal that it has no friends (meIrl (jk jk)).
     */
    Set<Set<I>> getPickyBuildFriends();

}
