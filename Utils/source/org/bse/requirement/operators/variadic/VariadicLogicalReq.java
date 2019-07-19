package org.bse.requirement.operators.variadic;

import org.bse.requirement.Requirement;

import java.util.Collections;
import java.util.Set;

/**
 * A requirement that requires an item to meet a logical function
 * of a group of unordered requirements. Ex. requiring a [T] object
 * to meet one of several requirements (variadic logic OR), or all
 * of several requirements (variadic logic AND).
 *
 * @param <T> The type of element to passed to [requireOf] for each .
 */
public abstract class VariadicLogicalReq<T> extends Requirement<T> {

    private final Set<Requirement<T>> children;

    protected VariadicLogicalReq(Set<Requirement<T>> children) {
        this.children = Collections.unmodifiableSet(children);
    }

    protected Set<Requirement<T>> getChildren() {
        return children;
    }

}
