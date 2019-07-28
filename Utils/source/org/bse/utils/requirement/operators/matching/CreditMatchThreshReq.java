package org.bse.utils.requirement.operators.matching;

import org.bse.utils.requirement.InsatiableReqException;
import org.bse.utils.requirement.InsatiableReqException.UnexpectedInsatiableReqException;
import org.bse.utils.requirement.RequireOpResult;
import org.bse.utils.requirement.RequireOpResult.RequireOpResultStatus;
import org.bse.utils.requirement.Requirement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Requires the sum of the credit values of items matching against certain
 * candidates to meet a credit threshold.
 * @param <T>
 */
public final class CreditMatchThreshReq<T extends CreditValued> extends AbstractMatchThreshReq<T> {

    private final int[] candidateCreditValues; // Do not modify or reassign entries.

    public CreditMatchThreshReq(int threshold, Set<T> candidates) throws InsatiableReqException {
        super(threshold, candidates);

        candidateCreditValues = getCandidates().stream()
                .mapToInt(CreditValued::getCreditValue)
                .toArray();
        Arrays.sort(candidateCreditValues); // ascending order

        // Check validity of the arguments:
        final int creditTotal = getCandidates().stream()
                .mapToInt(CreditValued::getCreditValue)
                .sum();
        if (creditTotal < threshold) {
            throw new InsatiableReqException(String.format("The provided threshold"
                    + " (%s) is greater than the sum of all the credit values of"
                    + " the provided candidates (%d).", threshold, creditTotal
            ));
        }
    }

    /**
     * TODO: make this account for possibility of an [INDETERMINATE] result status.
     * @param testSubject Items to be checked against one or more complex requirements.
     * @return
     */
    @Override
    public RequireOpResultStatus requireOf(final Set<T> testSubject) {
        final int creditsOfMatching = getCandidates().stream()
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
        try {
            return new CreditMatchThreshReq<>(
                    threshold, new HashSet<>(getCandidates())
            );
        } catch (InsatiableReqException e) {
            // Should never reach here: All [Requirement] implementations must be
            // immutable (See [Requirement] spec), and validity is enforced in the
            // constructor.
            throw new UnexpectedInsatiableReqException(e);
        }
    }

    @Override
    public Requirement<Set<T>> excludingPassingTermsFor(final Set<T> givens) {
        Set<T> candidates = getCandidates();
        Map<Boolean, Set<T>> partition = givens.stream()
                .collect(Collectors.partitioningBy(
                        candidates::contains, Collectors.toSet()
                ));
        final int matchedValue = partition.get(true).stream()
                .mapToInt(CreditValued::getCreditValue).sum();
        final int unMatchedValue = partition.get(false).stream()
                .mapToInt(CreditValued::getCreditValue).sum();

        if (matchedValue >= threshold) {
            // Enough terms matched to pass completely.
            return null;

        } else if (unMatchedValue == threshold) {
            // No terms matched at all.
            return this;

        } else {
            // Some terms matched, but not enough.
            try {
                return new CountMatchThreshReq<>(
                        threshold - matchedValue,
                        partition.get(false));
            } catch (InsatiableReqException e) {
                throw new UnexpectedInsatiableReqException(e);
                // return this;
            }
        }
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
                        threshold - candidateCreditValues[startIdx],
                        next
                );
            }
        }
        return numPassing;

    }

    @Override
    public Set<Set<T>> getAllBarelyPassingCombinations() {
        return null; // TODO:
    }

}
