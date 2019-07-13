package utils.requirement.operators.unary;

import utils.requirement.Requirement;

import java.util.Collection;
import java.util.HashSet;

/**
 * TODO: write documentation.
 * @param <T>
 */
public abstract class MatchThreshReq<T> implements Requirement<T> {

    protected HashSet<T> candidates;

    public MatchThreshReq(Collection<T> candidates) {
        this.candidates = new HashSet<T>(candidates);
    }

}
