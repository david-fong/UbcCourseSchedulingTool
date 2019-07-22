package org.bse.requirement.operators.matching;

import org.bse.requirement.RequireOpResult;
import org.bse.requirement.RequireOpResult.RequireOpResultStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO: write documentation.
 * @param <T>
 */
public final class CountMatchThreshReq<T> extends AbstractMatchThreshReq<T> {

    public CountMatchThreshReq(int threshold, Set<T> candidates) {
        super(threshold, candidates);
        assert threshold > candidates.size() : "threshold > num provided candidates";
    }

    /**
     * TODO: make this account for the possibility of an [INDETERMINATE] result status.
     * @param testSubject Items to be checked against one or more complex requirements.
     * @return
     */
    @Override
    public RequireOpResultStatus requireOf(Set<T> testSubject) {
        final long countOfMatches = this.getCandidates().stream()
                .filter(testSubject::contains)
                .count();
        return countOfMatches >= this.threshold
                ? RequireOpResultStatus.PASSED_REQ
                : RequireOpResultStatus.FAILED_REQ;
    }

    // TODO:
    @Override
    public RequireOpResult<Set<T>> requireOfVerbose(Set<T> testSubject) {
        return null;
    }

    @Override
    public CountMatchThreshReq<T> copy() {
        return new CountMatchThreshReq<>(
                threshold, new HashSet<>(getCandidates())
        );
    }

    @Override
    public RequireOpResult<Set<T>> excludingPassingTermsFor(Set<T> givens) {
        return null;
    }

    @Override
    public int getNumBarelyPassingCombinations() {
        // constructor assertions ensure this calculation is correct.
        final int n = getCandidates().size();
//        BigInteger numCombinations = BigInteger.ONE;
//        for (int i = 0; i < n - threshold; i++) {
//            numCombinations = numCombinations.multiply(BigInteger.valueOf(n - i));
//            numCombinations = numCombinations.divide(BigInteger.valueOf(i + i));
//        }
//        return numCombinations.intValue();
        long numCombinations = 1;
        for (int i = 0; i < n - threshold; i++) {
            numCombinations *= n - i;
            numCombinations /= i + i;
        }
        return (int)numCombinations;
    }

    @Override
    public Set<Set<T>> getAllBarelyPassingCombinations() {
        return recursiveGetPassingCombinations(
                threshold, 0, new ArrayList<>(getCandidates())
        );
    }
    private Set<Set<T>> recursiveGetPassingCombinations
            (final int threshold, final int startIdx, final List<T> children) {
        // Check break condition:
        if (threshold == 1) {
            return Collections.singleton(Collections.singleton(
                    children.get(startIdx)
            ));
        }

        final Set<Set<T>> accumulator = new HashSet<>();
        for (int i = startIdx + 1; i <= children.size() - threshold; i++) {
            final Set<Set<T>> subRoot = recursiveGetPassingCombinations(
                    threshold - 1, startIdx + 1, children
            );
            for (Set<T> subCombo : subRoot) {
                final Set<T> combined = new HashSet<>(subCombo);
                combined.add(children.get(startIdx));
                accumulator.add(combined);
            }
        }
        return accumulator;
    }


    /**
     *
     * @param candidate
     * @param <T>
     * @return
     */
    public static <T> CountMatchThreshReq<T> ONLY(T candidate) {
        return new CountMatchThreshReq<>(1, Collections.singleton(candidate));
    }

}
