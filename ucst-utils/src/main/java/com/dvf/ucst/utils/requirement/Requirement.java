package com.dvf.ucst.utils.requirement;

/**
 * All implementations of this class MUST BE IMMUTABLE. They must not provide any
 * public access to any of their fields. Objects passed to their constructors should
 * not be externally mutated after being passed to the constructor (allows constructor
 * arguments that are collections to be wrapped with an immutable collection view, as
 * opposed to being required to defensively copy the contents of the passed collection).
 *
 * @param <T> The type of element that will be tested against this requirement.
 */
public interface Requirement<T> {

    /**
     *
     * @param testSubject An item to be checked against complex requirements.
     * @return A [ReqOpOutcome] indicating the result status of the operation.
     */
    RequireOpResult.ReqOpOutcome requireOf(final T testSubject);

    /**
     * @return A deep copy of this [Requirement] object.
     */
    Requirement<T> copy();

    /**
     * Returns a Requirement that has the exact same behaviour as [this] one for the
     * [requireOf] and methods when reused with [givens] as the test subject. Any terms
     * that currently pass for [givens] can be excluded from the returned [Requirement].
     * Passing a [given] that is known to fail [this] [Requirement] is allowed.
     *
     * Using this method can improve performance for repeated tests against an object
     * that may change in its ability to meet this requirement, but is guaranteed not
     * to change in its ability to meet terms of [this][Requirement] that it is already
     * meeting.
     *
     * @param givens A specific object to test against a requirement.
     * @return A [Requirement] with identical behaviour as [this] one when specifically
     *     used against the parameter [givens]. Returns [NULL] if enough terms pass for
     *     [this] whole [Requirement] to pass. Since [Requirement] implementations must
     *     be immutable (see spec), this method is allowed to return [this] (itself).
     */
    Requirement<T> excludingPassingTermsFor(T givens);



    /**
     * A utility class for a [Requirement] that
     * always returns with a passing status.
     * @param <T>
     */
    class StrictlyPassingReq<T> implements Requirement<T> {

        @Override
        public final RequireOpResult.ReqOpOutcome requireOf(T testSubject) {
            return RequireOpResult.ReqOpOutcome.PASSED_REQ;
        }

        @Override
        public Requirement<T> copy() {
            return new StrictlyPassingReq<>();
        }

        @Override
        public Requirement<T> excludingPassingTermsFor(final T givens) {
            return null;
        }
    }

    /**
     * A utility class for a [Requirement] that
     * always returns with a failing status.
     */
    class StrictlyFailingReq<T> implements Requirement<T> {

        @Override
        public final RequireOpResult.ReqOpOutcome requireOf(T testSubject) {
            return RequireOpResult.ReqOpOutcome.FAILED_REQ;
        }

        @Override
        public Requirement<T> copy() {
            return new StrictlyFailingReq<>();
        }

        @Override
        public Requirement<T> excludingPassingTermsFor(T givens) {
            return this;
        }
    }

}
