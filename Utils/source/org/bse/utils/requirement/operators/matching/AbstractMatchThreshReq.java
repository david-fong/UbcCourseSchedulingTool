package org.bse.utils.requirement.operators.matching;

import java.util.Collections;
import java.util.Set;

/**
 * Threshold is an inclusive lower bound greater than zero.
 * Matching is done through the [equals] method.
 * @param <T>
 */
public abstract class AbstractMatchThreshReq<T> implements MatchingRequirementIf<T> {

    protected final int threshold;
    private final Set<T> candidates;

    public final int getThreshold() {
        return threshold;
    }

    public AbstractMatchThreshReq(int threshold, Set<T> candidates) {
        this.threshold  = threshold;
        this.candidates = Collections.unmodifiableSet(candidates);
        assert threshold > 0 : "threshold must be greater than zero";
    }

    protected final Set<T> getCandidates() {
        return candidates;
    }

}
