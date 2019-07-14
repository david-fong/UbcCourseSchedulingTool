package org.bse.requirement.operators.unary;

import org.bse.requirement.Requirement;

import java.util.Set;

/**
 * Threshold is an inclusive lower bound.
 * @param <T>
 */
public abstract class MatchThreshReq<T> extends Requirement<Set<T>> {

    protected final int threshold;
    protected final Set<T> candidates;

    public MatchThreshReq(int threshold, Set<T> candidates) {
        this.threshold  = threshold;
        this.candidates = candidates;
    }

}
