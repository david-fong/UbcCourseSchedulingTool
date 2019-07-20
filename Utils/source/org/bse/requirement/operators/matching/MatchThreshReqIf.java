package org.bse.requirement.operators.matching;

import org.bse.requirement.Requirement;

import java.util.Set;

/**
 * TODO:
 */
public interface MatchThreshReqIf<T> extends Requirement<Set<T>> {

    /**
     * @return The expected size of the collection that should be returned from the
     *     [getAllBarelyPassingCombinations] method.
     */
    int getNumBarelyPassingCombinations();

    /**
     * @return A collection of all combinations of items that can satisfy the passing
     *     conditions of the implementing [MatchThreshReqIf]. No combinations should be
     *     included that include any more match-items than are absolutely necessary to
     *     satisfy the implementing [MatchThreshReqIf].
     */
    Set<Set<T>> getAllBarelyPassingCombinations();

    @Override
    MatchThreshReqIf<T> copy();

}
