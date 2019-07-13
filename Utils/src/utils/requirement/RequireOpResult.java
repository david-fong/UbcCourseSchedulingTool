package utils.requirement;

/**
 * The return value from a Requirement.requireOfVerbose(...) operation.
 */
public class RequireOpResult<T> {

    private final Requirement<T> scope;
    private final double percentPassed;
    private final RequireOpResultStatus status;

    /**
     *
     * @param scope May be null if status is RequireOpResultStatus.PASSED_REQ.
     * @param percentPassed A double value between zero and one approximating
     *     how close the requireOfVerbose operation that created this object was
     *     to being fulfilled.
     * @param status A RequireOpResultStatus enum indicating the result status.
     */
    public RequireOpResult(Requirement<T> scope, double percentPassed, RequireOpResultStatus status) {
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

    public final RequireOpResultStatus getStatus() {
        return status;
    }



    /**
     * TODO: design how information in the 'item' parameter given to
     *   Requirement.requireOf() can be specified as requiring future
     *   information to be judged fully.
     */
    public enum RequireOpResultStatus {
        FAILED_REQ,
        INDETERMINATE,
        PASSED_REQ,
        ;
    }

}
