package org.bse.utils.requirement;

/**
 * Must be thrown in from [Requirement] implementation constructors when the given
 * arguments would create an exception that no test subject could possibly pass
 * against, and such behaviour is NOT INTENDED for the implementation's use cases.
 * (Ie, the implementation is not designed to intentionally always fail).
 */
public final class InsatiableReqException extends Exception {

    public InsatiableReqException(String message) {
        super(message);
    }


    /**
     * This should be thrown in places where invariants guarantee that the construction
     * of a [Requirement] which declares itself to throw an [InsatiableReqException] in
     * context should never result in that exception being thrown.
     */
    public static final class UnexpectedInsatiableReqException extends RuntimeException {
        public UnexpectedInsatiableReqException(InsatiableReqException e) {
            super("Surprised by an unexpected insatiable requirement.", e);
        }
    }

}
