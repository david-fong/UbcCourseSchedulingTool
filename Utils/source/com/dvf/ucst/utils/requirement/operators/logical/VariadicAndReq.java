package com.dvf.ucst.utils.requirement.operators.logical;

import com.dvf.ucst.utils.requirement.RequireOpResult.ReqOpOutcome;
import com.dvf.ucst.utils.requirement.Requirement;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dvf.ucst.utils.requirement.RequireOpResult.ReqOpOutcome.FAILED_REQ;
import static com.dvf.ucst.utils.requirement.RequireOpResult.ReqOpOutcome.PASSED_REQ;

/**
 * Requires all candidate requirements to pass against a test subject in order to
 * return with a passing status.
 *
 * @param <T>
 */
public class VariadicAndReq<T> extends VariadicLogicalReq<T> {

    public VariadicAndReq(Set<? extends Requirement<T>> children) {
        super(children);
    }

    // TODO: make this account for the possibility of an [INDETERMINATE] result status.
    @Override
    public final ReqOpOutcome requireOf(final T testSubject) {
        boolean success = this.getChildren().stream()
                .allMatch(childReq -> childReq.requireOf(testSubject) == PASSED_REQ);
        return success ? PASSED_REQ : FAILED_REQ;
    }

    @Override
    public VariadicAndReq<T> copy() {
        return new VariadicAndReq<>(getChildren().stream()
                .map(Requirement::copy)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public final Requirement<T> excludingPassingTermsFor(final T givens) {
        Set<Requirement<T>> nonPassingTerms = getChildren().stream()
                .map(term -> term.excludingPassingTermsFor(givens))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return nonPassingTerms.isEmpty() ? null : new VariadicAndReq<>(nonPassingTerms);
    }

}
