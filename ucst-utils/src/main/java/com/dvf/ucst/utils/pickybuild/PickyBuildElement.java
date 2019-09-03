package com.dvf.ucst.utils.pickybuild;

import java.util.Set;

/**
 * An element of a [PickyBuild]. May have friends that it wants to pull in too.
 * @param <I> The implementation type.
 */
public interface PickyBuildElement<I extends PickyBuildElement<I>> {

    /**
     * @return A conjunctive-normal-formed collection of other [PickyBuildElement]s
     *     that this [PickyBuildElement] requires a [PickyBuild] to also accommodate
     *     for the build to be considered complete. Must never return [null]. May
     *     return an empty [Set] to signal that it has no friends (meIrl (jk jk)).
     *     Follows same requirements and handling as the conjunctive-normal-formed
     *     constructor argument for [PickyBuildGenerator].
     */
    Set<Set<I>> getPickyBuildFriends();

}
