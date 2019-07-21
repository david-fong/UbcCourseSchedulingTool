package org.bse.requirement.operators.logicalmatching;

import org.bse.requirement.operators.logical.VariadicAndReq;
import org.bse.requirement.operators.matching.MatchThreshReqIf;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Requires all child requirements to pass against a test subject in order to
 * return with a passing status. You can also think of this as a very special
 * instance of a [CountMatchThreshReq] where all the children are [MatchThreshReqIf]s,
 * and the threshold is the number of [children].
 *
 * @param <T>
 */
public final class LogicAndMatchThreshReq<T> extends VariadicAndReq<Set<T>> implements MatchThreshReqIf<T> {

    private final Set<MatchThreshReqIf<T>> children;

    public LogicAndMatchThreshReq(Set<MatchThreshReqIf<T>> children) {
        super(children);
        this.children = Collections.unmodifiableSet(children);
    }

    @Override
    public int getNumBarelyPassingCombinations() {
        return children.stream()
                .mapToInt(MatchThreshReqIf::getNumBarelyPassingCombinations)
                .reduce(1, Math::multiplyExact);
    }

    @Override
    public Set<Set<T>> getAllBarelyPassingCombinations() {
        List<Set<Set<T>>> childCombos = children.stream()
                .map(MatchThreshReqIf::getAllBarelyPassingCombinations)
                .collect(Collectors.toList());
        return recursiveGenerateBarelyPassingCombinations(0, childCombos);
        // TODO: create all combinations of size [children.size()]
    }
    private Set<Set<T>> recursiveGenerateBarelyPassingCombinations
            (final int startIdx, final List<Set<Set<T>>> childCombos) {
        return null; // TODO:
    }

    @Override
    public LogicAndMatchThreshReq<T> copy() {
        return new LogicAndMatchThreshReq<>(children.stream()
                .map(MatchThreshReqIf::copy)
                .collect(Collectors.toSet())
        );
    }

}
