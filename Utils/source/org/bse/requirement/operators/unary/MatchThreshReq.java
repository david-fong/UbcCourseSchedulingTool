package org.bse.requirement.operators.unary;

import org.bse.requirement.Requirement;

import java.util.Collections;
import java.util.Set;

/**
 * Threshold is an inclusive lower bound.
 * Matching is done through the [equals] method.
 * @param <T>
 */
public abstract class MatchThreshReq<T> extends Requirement<Set<T>> {

    protected final int threshold;
    private final Set<T> candidates;

    public MatchThreshReq(int threshold, Set<T> candidates) {
        this.threshold  = threshold;
        this.candidates = Collections.unmodifiableSet(candidates);
    }

    protected Set<T> getCandidates() {
        return candidates;
    }

}
