package org.bse.requirement.operators.matching;

import org.bse.requirement.Requirement;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO:
 *
 * Checks should be done in constructors to ensure that the [MatchThreshReqIf] is
 * not impossible to satisfy from the get-go, and throw an exception if that is the
 * case.
 */
public interface MatchThreshReqIf<T> extends Requirement<Set<T>> {

    /**
     * @return The expected size of the collection that should be returned from the
     *     [getAllBarelyPassingCombinations] method.
     */
    int getNumBarelyPassingCombinations();

    /**
     * This method has nothing to do with whether a test subject can pass
     * this [MatchThreshReqIf]'s requirement. Rather, it should generate:
     *
     * @return A collection of all combinations of items that can satisfy the passing
     *     conditions of the implementing [MatchThreshReqIf]. No combinations should be
     *     included that include any more match-items than are absolutely necessary to
     *     satisfy the implementing [MatchThreshReqIf].
     */
    Set<Set<T>> getAllBarelyPassingCombinations();

    @Override
    MatchThreshReqIf<T> copy();


    /**
     * TODO:
     */
    final class StrictlyPassingMatchThreshReq<T>
            extends StrictlyPassingReq<Set<T>>
            implements MatchThreshReqIf<T> {

        @Override
        public int getNumBarelyPassingCombinations() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Set<Set<T>> getAllBarelyPassingCombinations() {
            return null; // TODO: not sure what to do here...
        }

        @Override
        public StrictlyPassingMatchThreshReq<T> copy() {
            return new StrictlyPassingMatchThreshReq<>();
        }
    }

    /**
     * TODO:
     */
    final class StrictlyFailingMatchThreshReq<T>
            extends StrictlyFailingReq<Set<T>>
            implements MatchThreshReqIf<T> {

        @Override
        public int getNumBarelyPassingCombinations() {
            return 0;
        }

        @Override
        public Set<Set<T>> getAllBarelyPassingCombinations() {
            return new HashSet<>(new HashSet<>());
        }

        @Override
        public StrictlyFailingMatchThreshReq<T> copy() {
            return new StrictlyFailingMatchThreshReq<>();
        }
    }
}
