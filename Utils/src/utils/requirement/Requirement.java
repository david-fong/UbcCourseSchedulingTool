package utils.requirement;

import utils.requirement.RequireOpResult.RequireOpResultStatus;

/**
 * For all purposes, objects implementing this class should be used as if they were
 * immutable. Objects passed to constructors of implementations of this interface should
 * not be externally mutated after they have been passed. Neither should Requirement
 * objects acquired as the result of a requireOf operation be mutated by any means
 * of coercion, or be mutated in implementations of requireOf.
 *
 * @param <T> The type of element that will be tested against this requirement
 *     in the requireOf method.
 */
public interface Requirement<T> {

    /**
     *
     * @param item An item to be checked against one or more complex requirements.
     * @return
     */
    RequireOpResultStatus requireOf(final T item);

    /**
     *
     * @param item An item to be checked against one or more complex requirements.
     * @return A RequireOpResult encapsulating a Requirement object containing all
     *     unary Requirement objects that [item] failed against. The behaviour of
     *     this Requirement object must follow exactly that of this requirement when
     *     used against [item]. Tighter bounds may improve performance where this
     *     object is
     */
    RequireOpResult<T> requireOfVerbose(final T item);

}
