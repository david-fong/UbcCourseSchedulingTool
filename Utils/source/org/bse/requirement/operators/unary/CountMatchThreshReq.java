package org.bse.requirement.operators.unary;

import org.bse.requirement.RequireOpResult;
import org.bse.requirement.RequireOpResult.RequireOpResultStatus;
import org.bse.requirement.Requirement;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO: write documentation.
 * @param <T>
 */
public final class CountMatchThreshReq<T> extends MatchThreshReq<T> {

    public CountMatchThreshReq(int threshold, Set<T> candidates) {
        super(threshold, candidates);
    }

    /**
     * TODO: make this account for the possibility of an [INDETERMINATE] result status.
     * @param testSubject Items to be checked against one or more complex requirements.
     * @return
     */
    @Override
    public RequireOpResultStatus requireOf(Set<T> testSubject) {
        final long countOfMatches = this.getCandidates().stream()
                .filter(testSubject::contains)
                .count();
        return countOfMatches >= this.threshold
                ? RequireOpResultStatus.PASSED_REQ
                : RequireOpResultStatus.FAILED_REQ;
    }

    // TODO:
    @Override
    public RequireOpResult<Set<T>> requireOfVerbose(Set<T> testSubject) {
        return null;
    }

    @Override
    public Requirement<Set<T>> copy() {
        return new CountMatchThreshReq<>(
                threshold, new HashSet<>(getCandidates())
        );
    }

    @Override
    public RequireOpResult<Set<T>> excludingPassingTermsFor(Set<T> givens) {
        return null;
    }


    public static <T> CountMatchThreshReq<T> ONLY(T candidate) {
        return new CountMatchThreshReq<>(1, Collections.singleton(candidate));
    }

}
