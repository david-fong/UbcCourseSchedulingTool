package org.bse.utils.requirement.operators.logical;

import org.bse.utils.requirement.RequireOpResult.ReqOpOutcome;
import org.bse.utils.requirement.Requirement;

import java.util.Set;
import java.util.stream.Collectors;

import static org.bse.utils.requirement.RequireOpResult.ReqOpOutcome.FAILED_REQ;
import static org.bse.utils.requirement.RequireOpResult.ReqOpOutcome.PASSED_REQ;

/**
 * Requires only one candidate requirement to pass against a test subject in
 * order to return with a passing status.
 *
 * @param <T>
 */
public class VariadicOrReq<T> extends VariadicLogicalReq<T> {

    public VariadicOrReq(Set<? extends Requirement<T>> children) {
        super(children);
    }

    // TODO: make this account for the possibility of an [INDETERMINATE] result status.
    @Override
    public final ReqOpOutcome requireOf(final T testSubject) {
        boolean success = this.getChildren().stream()
                .anyMatch(childReq -> childReq.requireOf(testSubject) == PASSED_REQ);
        return success ? PASSED_REQ : FAILED_REQ;
    }

    @Override
    public VariadicOrReq<T> copy() {
        return new VariadicOrReq<>(getChildren().stream()
                .map(Requirement::copy)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public final Requirement<T> excludingPassingTermsFor(final T givens) {
        final boolean anyMatch = getChildren().stream()
                .anyMatch(childReq -> childReq.excludingPassingTermsFor(givens) == null);
        return anyMatch ? null : this;
    }

}
