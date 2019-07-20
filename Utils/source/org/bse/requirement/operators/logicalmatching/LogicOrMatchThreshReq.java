package org.bse.requirement.operators.logicalmatching;

import org.bse.requirement.operators.logical.VariadicOrReq;
import org.bse.requirement.operators.matching.MatchThreshReqIf;

import java.util.Set;

public class LogicOrMatchThreshReq<T> extends VariadicOrReq<Set<T>> implements MatchThreshReqIf<T> {

    public LogicOrMatchThreshReq(Set<MatchThreshReqIf<T>> children) {
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
