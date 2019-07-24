package org.bse.requirement.operators.logical;

import org.bse.requirement.RequireOpResult;
import org.bse.requirement.RequireOpResult.RequireOpResultStatus;
import org.bse.requirement.Requirement;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.bse.requirement.RequireOpResult.RequireOpResultStatus.FAILED_REQ;
import static org.bse.requirement.RequireOpResult.RequireOpResultStatus.PASSED_REQ;

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
    public final RequireOpResultStatus requireOf(final T testSubject) {
        boolean success = this.getChildren().stream()
                .allMatch(childReq -> childReq.requireOf(testSubject) == PASSED_REQ);
        return success ? PASSED_REQ : FAILED_REQ;
    }

    // TODO:
    @Override
    public final RequireOpResult<T> requireOfVerbose(final T testSubject) {
        return null;
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
