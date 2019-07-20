package org.bse.requirement;

import org.bse.requirement.RequireOpResult.RequireOpResultStatus;

/**
 * Implementations of this class should be immutable, and provide no access to any
 * of their fields. Objects passed to their constructors should not be externally
 * mutated after being passed to the constructor.
 *
 * @param <T> The type of element that will be tested against this requirement.
 */
public interface Requirement<T> {

    /**
     *
     * @param testSubject An item to be checked against complex requirements.
     * @return A [RequireOpResultStatus] indicating the result status of the operation.
     */
    RequireOpResultStatus requireOf(final T testSubject);

    /**
     *
     * @param testSubject An item to be checked against complex requirements.
     * @return A RequireOpResult encapsulating a Requirement object containing all
     *     Requirement objects that [testSubject] failed against. The behaviour of
     *     this [Requirement] object must follow exactly that of this requirement
     *     when used against [testSubject].
     */
    RequireOpResult<T> requireOfVerbose(final T testSubject);

    /**
     * @return A deep copy of this [Requirement] object.
     */
    Requirement<T> copy();

    /**
     * Returns a Requirement that has the exact same behaviour as [this] one for the
     * [requireOf] and [requireOfVerbose] methods when reused with [givens] as the
     * test subject. Any terms that currently pass for [givens] can be excluded from
     * the returned [Requirement].
     *
     * Using this method can improve performance for repeated tests against an object
     * that may change in its ability to meet this requirement, but is guaranteed not
     * to change in its ability to meet terms of [this][Requirement] that it is already
     * meeting.
     *
     * @param givens A specific object to test against a requirement.
     * @return A [RequireOpResult] holding a [scope][Requirement] with identical
     *     behaviour as [this] one when specifically used against the parameter
     *     [givens]. Must not be null.
     */
    RequireOpResult<T> excludingPassingTermsFor(T givens);



    /**
     * A Class for a [Requirement] that always returns with a passing status.
     * @param <T>
     */
    final class StrictlyPassingReq<T> implements Requirement<T> {

        @Override
        public RequireOpResultStatus requireOf(T testSubject) {
            return RequireOpResultStatus.PASSED_REQ;
        }

        @Override
        public RequireOpResult<T> requireOfVerbose(T testSubject) {
            return new RequireOpResult<>(
                    null,
                    1.0,
                    RequireOpResultStatus.PASSED_REQ
            );
        }

        @Override
        public Requirement<T> copy() {
            return new StrictlyPassingReq<>();
        }

        @Override
        public RequireOpResult<T> excludingPassingTermsFor(T givens) {
            return new RequireOpResult<>(
                    copy(),
                    1.0,
                    RequireOpResultStatus.PASSED_REQ
            );
        }
    }

    /**
     * A Class for a [Requirement] that always returns with a failing status.
     */
    final class StrictlyFailingReq<T> implements Requirement<T> {

        @Override
        public RequireOpResultStatus requireOf(T testSubject) {
            return RequireOpResultStatus.FAILED_REQ;
        }

        @Override
        public RequireOpResult<T> requireOfVerbose(T testSubject) {
            return new RequireOpResult<>(
                    copy(),
                    1.0,
                    RequireOpResultStatus.FAILED_REQ
            );
        }

        @Override
        public Requirement<T> copy() {
            return new StrictlyFailingReq<>();
        }

        @Override
        public RequireOpResult<T> excludingPassingTermsFor(T givens) {
            return new RequireOpResult<>(
                    copy(),
                    1.0,
                    RequireOpResultStatus.FAILED_REQ
            );
        }
    }

}
