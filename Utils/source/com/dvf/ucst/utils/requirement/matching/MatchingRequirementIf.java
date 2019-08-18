package com.dvf.ucst.utils.requirement.matching;

import com.dvf.ucst.utils.requirement.Requirement;

import java.util.HashSet;
import java.util.Set;

/**
 * An interface for requirements that take a set of test subjects to check that
 * the sum of some mapping of them to values meets a certain threshold.
 *
 * Checks should be done in constructors to ensure that the [MatchingRequirementIf]
 * is not impossible to satisfy from the get-go, and throw an exception if that is
 * the case.
 */
public interface MatchingRequirementIf<T> extends Requirement<Set<T>> {

    /**
     * @return The size of the collection that should be returned
     *     from the [getAllBarelyPassingCombinations] method.
     */
    long getNumBarelyPassingCombinations();

    /**
     * This method has nothing to do with whether a test subject can pass
     * this [MatchingRequirementIf]'s requirement. Rather, it should generate:
     *
     * @return A collection of all combinations of items that can satisfy the passing
     *     conditions of the implementing [MatchingRequirementIf]. No combinations
     *     should be included that include any more match-items than are absolutely
     *     necessary to satisfy the implementing [MatchingRequirementIf]. This number
     *     can be cached in implementations since [Requirement]s must be immutable.
     */
    Set<Set<T>> getAllBarelyPassingCombinations();

    @Override
    MatchingRequirementIf<T> copy();


    /**
     * A utility class for a [MatchingRequirementIf]
     * that always returns with a passing status.
     */
    final class StrictlyPassingMatchThreshReq<T>
            extends StrictlyPassingReq<Set<T>>
            implements MatchingRequirementIf<T> {

        @Override
        public long getNumBarelyPassingCombinations() {
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

        @Override
        public MatchingRequirementIf<T> excludingPassingTermsFor(final Set<T> givens) {
            return null;
        }
    }

    /**
     * A utility class for a [MatchingRequirementIf]
     * that always returns with a failing status.
     */
    final class StrictlyFailingMatchThreshReq<T>
            extends StrictlyFailingReq<Set<T>>
            implements MatchingRequirementIf<T> {

        @Override
        public long getNumBarelyPassingCombinations() {
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

        @Override
        public MatchingRequirementIf<T> excludingPassingTermsFor(final Set<T> givens) {
            return this;
        }
    }
}
