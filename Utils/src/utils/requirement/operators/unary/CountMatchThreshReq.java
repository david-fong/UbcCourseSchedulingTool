package utils.requirement.operators.unary;

import utils.requirement.RequireOpResult;
import utils.requirement.RequireOpResult.RequireOpResultStatus;

import java.util.Collection;

/**
 * TODO: write documentation.
 * @param <T>
 */
public class CountMatchThreshReq<T> extends MatchThreshReq<T> {

    private final int threshold;

    public CountMatchThreshReq(int threshold, Collection<T> candidates) {
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
