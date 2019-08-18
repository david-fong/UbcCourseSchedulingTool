package com.dvf.ucst.utils.requirement.operators.matching;

import org.w3c.dom.Element;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

/**
 * Threshold is an inclusive lower bound greater than zero. Ie. if a test subject's
 * matches map to a sum greater than or equal to [threshold], then it is considered
 * to pass [this][Requirement].
 *
 * IMPORTANT: Matching is done through the [equals] method.
 * @param <T>
 */
public abstract class AbstractMatchThreshReq<T> implements MatchingRequirementIf<T> {

    protected final int threshold;
    private final Set<T> candidates;

    public final int getThreshold() {
        return threshold;
    }

    public AbstractMatchThreshReq(int threshold, Set<T> candidates) {
        assert threshold > 0 : "threshold must be greater than zero";
        this.threshold  = threshold;
        this.candidates = Collections.unmodifiableSet(candidates);
    }

    // TODO [xml:read]:
    public AbstractMatchThreshReq(final Element mtrElement, final Function<Element, T> candidateParser) {
        this.threshold = -1;
        this.candidates = null;
    }

    protected final Set<T> getCandidates() {
        return candidates;
    }

}
