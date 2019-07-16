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
     * @param items Items to be checked against one or more complex requirements.
     * @return
     */
    @Override
    public RequireOpResultStatus requireOf(Set<T> items) {
        final long countOfMatches = this.candidates.stream()
                .filter(items::contains)
                .count();
        return countOfMatches >= this.threshold
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
        return new CountMatchThreshReq<>(threshold, new HashSet<>(candidates));
    }



    public static <T> CountMatchThreshReq<T> ONLY(T candidate) {
        return new CountMatchThreshReq<T>(1, Collections.singleton(candidate));
    }

}
