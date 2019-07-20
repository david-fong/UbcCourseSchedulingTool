package org.bse.requirement.operators.logicalmatching;

import org.bse.requirement.operators.logical.VariadicOrReq;
import org.bse.requirement.operators.matching.MatchThreshReqIf;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Requires only one candidate requirement to pass against a test subject in
 * order to return with a passing status. You can also think of this as a very
 * special instance of a [CountMatchThreshReq] where all the candidates are
 * [MatchThreshReqIf]s, and the threshold is one.
 *
 * @param <T>
 */
public final class LogicOrMatchThreshReq<T> extends VariadicOrReq<Set<T>> implements MatchThreshReqIf<T> {

    private final Set<MatchThreshReqIf<T>> candidates;

    public LogicOrMatchThreshReq(Set<MatchThreshReqIf<T>> candidates) {
        super(candidates);
        this.candidates = Collections.unmodifiableSet(candidates);
    }

    @Override
    public int getNumBarelyPassingCombinations() {
        return candidates.stream()
                .mapToInt(MatchThreshReqIf::getNumBarelyPassingCombinations)
                .sum();
    }

    @Override
    public Set<Set<T>> getAllBarelyPassingCombinations() {
        return null; // TODO:
    }

    @Override
    public LogicOrMatchThreshReq<T> copy() {
        return new LogicOrMatchThreshReq<>(candidates.stream()
                .map(MatchThreshReqIf::copy)
                .collect(Collectors.toSet())
        );
    }

}
