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
public final class VariadicAndReq<T> extends VariadicLogicalReq<T> {

    public VariadicAndReq(Set<Requirement<T>> children) {
        super(children);
    }

    // TODO: make this account for the possibility of an [INDETERMINATE] result status.
    @Override
    public RequireOpResultStatus requireOf(T item) {
        boolean success = this.children.stream()
                .allMatch(childReq -> childReq.requireOf(item) == PASSED_REQ);
        return success ? PASSED_REQ : FAILED_REQ;
    }

    // TODO:
    @Override
    public RequireOpResult<T> requireOfVerbose(T item) {
        return null;
    }

    @Override
    public Requirement<T> copy() {
        return new VariadicAndReq<>(new HashSet<>(children));
    }
}
