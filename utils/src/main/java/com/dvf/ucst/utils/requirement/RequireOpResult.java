package com.dvf.ucst.utils.requirement;

/**
 * The return value from a Requirement.requireOfVerbose(...) operation.
 */
public class RequireOpResult<T> {

    private final Requirement<T> scope;
    private final double percentPassed;
    private final ReqOpOutcome status;

    /**
     *
     * @param scope May be null if status is ReqOpOutcome.PASSED_REQ.
     *     Otherwise, it should contain a view of the [Requirement] that created
     *     this [RequireOfResult] that excludes all terms that were not necessary
     *     not make [testSubject] fail against it.
     * @param percentPassed A double value between zero and one approximating
     *     how close the requireOfVerbose operation that created this object was
     *     to being fulfilled.
     * @param status A ReqOpOutcome enum indicating the result status.
     */
    public RequireOpResult(Requirement<T> scope, double percentPassed, ReqOpOutcome status) {
        this.scope  = scope;
        this.percentPassed = percentPassed;
        this.status = status;
    }

    public final Requirement<T> getScope() {
        return scope;
    }

    public final double getPercentPassed() {
        return percentPassed;
    }

    public final ReqOpOutcome getStatus() {
        return status;
    }



    /**
     */
    public enum ReqOpOutcome {
        FAILED_REQ,
        //INDETERMINATE,
        PASSED_REQ,
        ;
    }

}
