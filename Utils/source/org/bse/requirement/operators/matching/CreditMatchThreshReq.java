package org.bse.requirement.operators.matching;

import org.bse.requirement.RequireOpResult;
import org.bse.requirement.RequireOpResult.RequireOpResultStatus;
import org.bse.requirement.Requirement;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Requires the sum of the credit values of items matching against certain
 * candidates to meet a credit threshold.
 * @param <T>
 */
public final class CreditMatchThreshReq<T extends CreditValued> extends AbstractMatchThreshReq<T> {

    private final Integer[] candidateCreditValues; // Do not modify or reassign entries.

    public CreditMatchThreshReq(int threshold, Set<T> candidates) {
        super(threshold, candidates);
        Stream<Integer> creditValueStream = Stream.of(getCandidates())
                .map(candidate -> ((CreditValued)candidate).getCreditValue());

        assert creditValueStream.reduce(0, (x, y) -> x + y) > threshold
                : "threshold > sum of all credits of provided candidates";
        candidateCreditValues = creditValueStream.toArray(Integer[]::new);
        Arrays.sort(candidateCreditValues, Collections.reverseOrder());
    }

    /**
     * TODO: make this account for possibility of an [INDETERMINATE] result status.
     * @param testSubject Items to be checked against one or more complex requirements.
     * @return
     */
    @Override
    public RequireOpResultStatus requireOf(final Set<T> testSubject) {
        final int creditsOfMatching = this.getCandidates().stream()
                .filter(testSubject::contains)
                .mapToInt(CreditValued::getCreditValue)
                .sum();
        return creditsOfMatching >= this.threshold
                ? RequireOpResultStatus.PASSED_REQ
                : RequireOpResultStatus.FAILED_REQ;
    }

    // TODO:
    @Override
    public RequireOpResult<Set<T>> requireOfVerbose(final Set<T> testSubject) {
        return null;
    }

    @Override
    public CreditMatchThreshReq<T> copy() {
        return new CreditMatchThreshReq<>(
                threshold, new HashSet<>(getCandidates())
        );
    }

    @Override
    public Requirement<Set<T>> excludingPassingTermsFor(final Set<T> givens) {
        return null; // TODO:
    }

    @Override
    public int getNumBarelyPassingCombinations() {
        return recursiveCountCombos(this.threshold, 0);
    }
    // TODO: test.
    private int recursiveCountCombos(final int threshold, final int startIdx) {
        int numPassing = 0;
        // TODO: think about how to add another conditional to further avoid unnecessary work.
        if (threshold <= 0) {
            numPassing++;
        } else {
            for (int next = startIdx + 1; next < candidateCreditValues.length; next++) {
                numPassing += recursiveCountCombos(
                        threshold - candidateCreditValues[startIdx], next
                );
            }
        }
        return numPassing;

    }

    @Override
    public Set<Set<T>> getAllBarelyPassingCombinations() {
        return null;
    }

}
