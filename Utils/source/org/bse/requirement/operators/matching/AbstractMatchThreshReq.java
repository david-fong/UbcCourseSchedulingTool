package org.bse.requirement.operators.matching;

import java.util.Collections;
import java.util.Set;

/**
 * Threshold is an inclusive lower bound.
 * Matching is done through the [equals] method.
 * @param <T>
 */
public abstract class AbstractMatchThreshReq<T> implements MatchThreshReqIf<T> {

    protected final int threshold;
    private final Set<T> candidates;

    public AbstractMatchThreshReq(int threshold, Set<T> candidates) {
        this.threshold  = threshold;
        this.candidates = Collections.unmodifiableSet(candidates);
    }

    protected final Set<T> getCandidates() {
        return candidates;
    }

}
