package org.bse.requirement.operators.variadic;

import org.bse.requirement.RequireOpResult;
import org.bse.requirement.RequireOpResult.RequireOpResultStatus;
import org.bse.requirement.Requirement;

/**
 * TODO
 * @param <T>
 */
public final class VariadicOR<T> implements Requirement<T> {

    private final Requirement<T>[] operands;

    public VariadicOR(Requirement<T>[] operands) {
        this.operands = operands;
    }

    @Override
    public RequireOpResultStatus requireOf(T item) {
        return null; // TODO
        // collect all false items to a new array,
        // and use that to create a RequireOpResult.
    }

    @Override
    public RequireOpResult<T> requireOfVerbose(T item) {
        return null;
    }

}
