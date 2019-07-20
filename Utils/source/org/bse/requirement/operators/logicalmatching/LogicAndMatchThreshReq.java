package org.bse.requirement.operators.logicalmatching;

import org.bse.requirement.operators.logical.VariadicAndReq;
import org.bse.requirement.operators.matching.MatchThreshReqIf;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Requires all candidate requirements to pass against a test subject to return
 * with a passing status. You can also think of this as a very special instance
 * of a [CountMatchThreshReq] where all the candidates are [MatchThreshReqIf]s, and
 * the threshold is the number of [candidates].
 *
 * @param <T>
 */
public final class LogicAndMatchThreshReq<T> extends VariadicAndReq<Set<T>> implements MatchThreshReqIf<T> {

    private final Set<MatchThreshReqIf<T>> candidates;

    public LogicAndMatchThreshReq(Set<MatchThreshReqIf<T>> candidates) {
        super(candidates);
        this.candidates = Collections.unmodifiableSet(candidates);
    }

    @Override
    public int getNumBarelyPassingCombinations() {
        return candidates.stream()
                .mapToInt(MatchThreshReqIf::getNumBarelyPassingCombinations)
                .reduce(1, Math::multiplyExact);
    }

    @Override
    public Set<Set<T>> getAllBarelyPassingCombinations() {
        return null; // TODO:
    }

    @Override
    public LogicAndMatchThreshReq<T> copy() {
        return new LogicAndMatchThreshReq<>(candidates.stream()
                .map(MatchThreshReqIf::copy)
                .collect(Collectors.toSet())
        );
    }

}
