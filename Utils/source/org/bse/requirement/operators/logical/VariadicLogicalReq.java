package org.bse.requirement.operators.logical;

import org.bse.requirement.Requirement;

import java.util.Collections;
import java.util.Set;

/**
 * A requirement that requires an item to meet a logical function
 * of a group of unordered requirements. Ex. requiring a [T] object
 * to meet one of several requirements (logical logic OR), or all
 * of several requirements (logical logic AND).
 *
 * @param <T> The type of element to passed to [requireOf] for each .
 */
abstract class VariadicLogicalReq<T> implements Requirement<T> {

    private final Set<Requirement<T>> children;

    VariadicLogicalReq(Set<? extends Requirement<T>> children) {
        this.children = Collections.unmodifiableSet(children);
    }

    final Set<Requirement<T>> getChildren() {
        return children;
    }

    @Override
    public abstract VariadicLogicalReq<T> copy();

}
