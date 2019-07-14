package org.bse.core.requirement.unary;

import org.bse.core.registration.CreditValued;
import org.bse.requirement.RequireOpResult;
import org.bse.requirement.RequireOpResult.RequireOpResultStatus;
import org.bse.requirement.operators.unary.MatchThreshReq;

import java.util.Collection;

/**
 * TODO: write documentation.
 * @param <T>
 */
public class CreditMatchThreshReq<T extends CreditValued> extends MatchThreshReq<T> {

    private final int threshold;

    public CreditMatchThreshReq(int threshold, Collection<T> candidates) {
        super(candidates);
        this.threshold = threshold;
    }

    @Override
    public RequireOpResultStatus requireOf(T item) {
        return null; // TODO
    }

    @Override
    public RequireOpResult<T> requireOfVerbose(T item) {
        return null; // TODO
    }

}
