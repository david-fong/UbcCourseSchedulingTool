package org.bse.requirement.operators.matching;

import java.util.Set;

/**
 * TODO:
 */
public interface MatchThreshReqIf<T> {

    /**
     * @return The expected size of the collection that should be returned from the
     *     [getAllBarelyPassingCombinations] method.
     */
    int getNumBarelyPassingCombinations();

    /**
     * @return A collection of all combinations of items that can satisfy the passing
     *     conditions of the implementing [MatchThreshReq] or [LogicalMatchThreshReq].
     *     No combinations are included that include any more match-items than are
     *     absolutely necessary to satisfy the implementing [Requirement].
     */
    Set<Set<T>> getAllBarelyPassingCombinations();

}
