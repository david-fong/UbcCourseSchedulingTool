package org.bse.requirement.operators.logicalmatching;

import org.bse.requirement.operators.matching.MatchThreshReqIf;

import java.util.Collections;
import java.util.Set;

/**
 * A logical requirement of a variadic number of operators as children that also
 * implement the [MatchThreshReqIf] interface. Provides a getter to implementers
 * of this class to access [children], which is wrapped by an immutable [Set] in
 * the constructor to prevent any changes.
 *
 * @param <T>
 */
public abstract class LogicalMatchThreshReq<T> implements MatchThreshReqIf<T> {

    private Set<MatchThreshReqIf<T>> children;

    public LogicalMatchThreshReq(Set<MatchThreshReqIf<T>> children) {
        this.children = Collections.unmodifiableSet(children);
    }

    protected final Set<MatchThreshReqIf<T>> getChildren() {
        return children;
    }

}
