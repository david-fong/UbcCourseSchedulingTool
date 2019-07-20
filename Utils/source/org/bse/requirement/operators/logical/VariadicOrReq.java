package org.bse.requirement.operators.logical;

import org.bse.requirement.RequireOpResult;
import org.bse.requirement.RequireOpResult.RequireOpResultStatus;
import org.bse.requirement.Requirement;

import java.util.Set;
import java.util.stream.Collectors;

import static org.bse.requirement.RequireOpResult.RequireOpResultStatus.FAILED_REQ;
import static org.bse.requirement.RequireOpResult.RequireOpResultStatus.PASSED_REQ;

/**
 * TODO
 * @param <T>
 */
public class VariadicOrReq<T> extends VariadicLogicalReq<T> {

    public VariadicOrReq(Set<? extends Requirement<T>> children) {
        super(children);
    }

    // TODO: make this account for the possibility of an [INDETERMINATE] result status.
    @Override
    public final RequireOpResultStatus requireOf(final T testSubject) {
        boolean success = this.getChildren().stream()
                .anyMatch(childReq -> childReq.requireOf(testSubject) == PASSED_REQ);
        return success ? PASSED_REQ : FAILED_REQ;
    }

    // TODO:
    @Override
    public final RequireOpResult<T> requireOfVerbose(final T testSubject) {
        return null;
    }

    @Override
    public VariadicOrReq<T> copy() {
        return new VariadicOrReq<>(getChildren().stream()
                .map(Requirement::copy)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public final RequireOpResult<T> excludingPassingTermsFor(final T givens) {
        return null; // TODO
    }

}
