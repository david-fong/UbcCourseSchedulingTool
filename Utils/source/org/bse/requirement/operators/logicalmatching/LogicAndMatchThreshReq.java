package org.bse.requirement.operators.logicalmatching;

import org.bse.requirement.operators.logical.VariadicAndReq;
import org.bse.requirement.operators.matching.MatchThreshReqIf;

import java.util.Collections;
import java.util.HashSet;
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
        childCombos = Collections.unmodifiableList(childCombos);
        return recursiveGetPassingCombinations(0, childCombos);
    }
    private Set<Set<T>> recursiveGetPassingCombinations
            (final int startIdx, final List<Set<Set<T>>> childCombos) {
        // Check break condition:
        if (startIdx == childCombos.size() - 1) {
            return childCombos.get(startIdx);
        }

        final Set<Set<T>> accumulator = new HashSet<>();
        final Set<Set<T>> subRoot = recursiveGetPassingCombinations(
                startIdx + 1, childCombos
        );
        for (Set<T> rootCombo : childCombos.get(startIdx)) {
            for (Set<T> subCombo : subRoot) {
                final Set<T> combined = new HashSet<>(rootCombo);
                combined.addAll(subCombo);
                accumulator.add(combined);
            }
        }
        return accumulator;
    }

    @Override
    public LogicAndMatchThreshReq<T> copy() {
        return new LogicAndMatchThreshReq<>(children.stream()
                .map(MatchThreshReqIf::copy)
                .collect(Collectors.toSet())
        );
    }

}
