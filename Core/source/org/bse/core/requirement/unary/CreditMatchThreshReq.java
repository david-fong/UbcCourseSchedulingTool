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
     * @param testSubject Items to be checked against one or more complex requirements.
     * @return
     */
    @Override
    public RequireOpResultStatus requireOf(final Set<T> testSubject) {
        final int creditsOfMatching = this.getCandidates().stream()
                .filter(testSubject::contains)
                .mapToInt(CreditValued::getCreditValue)
                .sum();
        return creditsOfMatching >= this.threshold
                ? RequireOpResultStatus.PASSED_REQ
                : RequireOpResultStatus.FAILED_REQ;
    }

    // TODO:
    @Override
    public RequireOpResult<Set<T>> requireOfVerbose(final Set<T> testSubject) {
        return null;
    }

    @Override
    public Requirement<Set<T>> copy() {
        return new CreditMatchThreshReq<>(
                threshold, new HashSet<>(getCandidates())
        );
    }

    @Override
    public RequireOpResult<Set<T>> excludingPassingTermsFor(final Set<T> givens) {
        return null;
    }

}
