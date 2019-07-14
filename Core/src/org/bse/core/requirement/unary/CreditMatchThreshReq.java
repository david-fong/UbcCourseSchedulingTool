package org.bse.core.requirement.unary;

import org.bse.core.registration.CreditValued;
import org.bse.requirement.RequireOpResult;
import org.bse.requirement.RequireOpResult.RequireOpResultStatus;
import org.bse.requirement.Requirement;
import org.bse.requirement.operators.unary.MatchThreshReq;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO: write documentation.
 * @param <T>
 */
public final class CreditMatchThreshReq<T extends CreditValued> extends MatchThreshReq<T> {

    public CreditMatchThreshReq(int threshold, Set<T> candidates) {
        super(threshold, candidates);
    }

    /**
     * TODO: make this account for possibility of an [INDETERMINATE] result status.
     * @param items Items to be checked against one or more complex requirements.
     * @return
     */
    @Override
    public RequireOpResultStatus requireOf(Set<T> items) {
        final int creditsOfMatching = this.candidates.stream()
                .filter(items::contains)
                .mapToInt(CreditValued::getCreditValue)
                .sum();
        return creditsOfMatching >= this.threshold
                ? RequireOpResultStatus.PASSED_REQ
                : RequireOpResultStatus.FAILED_REQ;
    }

    // TODO:
    @Override
    public RequireOpResult<Set<T>> requireOfVerbose(Set<T> item) {
        return null;
    }

    @Override
    public Requirement<Set<T>> copy() {
        return new CreditMatchThreshReq<>(threshold, new HashSet<>(candidates));
    }

}
