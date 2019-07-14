package org.bse.requirement;

import org.bse.requirement.RequireOpResult.RequireOpResultStatus;

/**
 * For all purposes, objects implementing this class should be used as if they
 * were immutable. Objects passed to constructors of implementations of this
 * interface should not be externally mutated after they have been passed. Neither
 * should Requirement objects acquired as the result of a requireOf operation be
 * mutated by any means of coercion. This allows any necessary constructor argument
 * to subclasses to be used directly, and only defensively copied in implementations
 * of [requireOfVerbose].
 *
 * @param <T> The type of element that will be tested against this requirement.
 */
public abstract class Requirement<T> {

    /**
     *
     * @param item An item to be checked against one or more complex requirements.
     * @return A [RequireOpResultStatus] indicating the result status of the operation.
     */
    public abstract RequireOpResultStatus requireOf(final T item);

    /**
     *
     * @param item An item to be checked against one or more complex requirements.
     * @return A RequireOpResult encapsulating a Requirement object containing all
     *     Requirement objects that [item] failed against. The behaviour of this
     *     Requirement object must follow exactly that of this requirement when
     *     used against [item].
     */
    public abstract RequireOpResult<T> requireOfVerbose(final T item);

    /**
     * TODO: decide on whether the subclasses should return types the same
     * as their own type (implementation type and not just [Requirement].
     * @return A deep copy of this [Requirement] object.
     */
    public abstract Requirement<T> copy();

}
