package org.bse.utils.requirement.operators.matching;

import org.bse.utils.requirement.InsatiableReqException;

import java.util.Collections;
import java.util.Set;

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

    public AbstractMatchThreshReq(int threshold, Set<T> candidates) throws InsatiableReqException {
        this.threshold  = threshold;
        this.candidates = Collections.unmodifiableSet(candidates);

        if (threshold <= 0) {
            throw new InsatiableReqException("threshold must be greater than zero");
        }
    }

    protected final Set<T> getCandidates() {
        return candidates;
    }

}
