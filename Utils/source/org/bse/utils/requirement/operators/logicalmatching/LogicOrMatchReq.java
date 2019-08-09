package org.bse.utils.requirement.operators.logicalmatching;

import org.bse.utils.requirement.operators.logical.VariadicOrReq;
import org.bse.utils.requirement.operators.matching.MatchingRequirementIf;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Requires only one child requirement to pass against a test subject in
 * order to return with a passing status. You can also think of this as a
 * very special instance of a [CountMatchThreshReq] where all the children
 * are [MatchingRequirementIf]s, and the threshold is one.
 *
 * @param <T>
 */
public final class LogicOrMatchReq<T> extends VariadicOrReq<Set<T>> implements MatchingRequirementIf<T> {

    private final Set<MatchingRequirementIf<T>> children;
    private final long numBarelyPassingCombinations;

    public LogicOrMatchReq(Set<MatchingRequirementIf<T>> children) {
        super(children);
        this.children = Collections.unmodifiableSet(children);
        this.numBarelyPassingCombinations = children.stream()
                .mapToLong(MatchingRequirementIf::getNumBarelyPassingCombinations)
                .sum();
    }

    @Override
    public long getNumBarelyPassingCombinations() {
        return numBarelyPassingCombinations;
    }

    @Override
    public Set<Set<T>> getAllBarelyPassingCombinations() {
        return children.stream()
                .map(MatchingRequirementIf::getAllBarelyPassingCombinations)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public LogicOrMatchReq<T> copy() {
        return new LogicOrMatchReq<>(children.stream()
                .map(MatchingRequirementIf::copy)
                .collect(Collectors.toSet())
        );
    }

}
