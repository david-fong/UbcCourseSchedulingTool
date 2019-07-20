package org.bse.requirement.operators.logicalmatching;

import org.bse.requirement.Requirement;
import org.bse.requirement.operators.logical.VariadicAndReq;
import org.bse.requirement.operators.matching.MatchThreshReqIf;

import java.util.Set;

/**
 * Requires all children requirements to pass against a test subject to return with
 * a passing status.
 *
 * @param <T>
 */
public final class LogicAndMatchThreshReq<T> extends VariadicAndReq<Set<T>> implements MatchThreshReqIf<T> {

    public LogicAndMatchThreshReq(Set<Requirement<Set<T>>> children) {
        super(children);
    }

    @Override
    public int getNumBarelyPassingCombinations() {
        return 0; // TODO:
    }

    @Override
    public Set<Set<T>> getAllBarelyPassingCombinations() {
        return null; // TODO:
    }
}
