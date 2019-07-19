package org.bse.requirement.operators.variadic;

import org.bse.requirement.RequireOpResult;
import org.bse.requirement.RequireOpResult.RequireOpResultStatus;
import org.bse.requirement.Requirement;

import java.util.HashSet;
import java.util.Set;

import static org.bse.requirement.RequireOpResult.RequireOpResultStatus.FAILED_REQ;
import static org.bse.requirement.RequireOpResult.RequireOpResultStatus.PASSED_REQ;

/**
 * TODO
 * @param <T>
 */
public final class VariadicOrReq<T> extends VariadicLogicalReq<T> {

    public VariadicOrReq(Set<Requirement<T>> children) {
        super(children);
    }

    // TODO: make this account for the possibility of an [INDETERMINATE] result status.
    @Override
    public RequireOpResultStatus requireOf(final T testSubject) {
        boolean success = this.getChildren().stream()
                .anyMatch(childReq -> childReq.requireOf(testSubject) == PASSED_REQ);
        return success ? PASSED_REQ : FAILED_REQ;
    }

    // TODO:
    @Override
    public RequireOpResult<T> requireOfVerbose(final T testSubject) {
        return null;
    }

    @Override
    public Requirement<T> copy() {
        return new VariadicOrReq<>(
                new HashSet<>(getChildren())
        );
    }

    @Override
    public RequireOpResult<T> excludingPassingTermsFor(final T givens) {
        return null;
    }

}
