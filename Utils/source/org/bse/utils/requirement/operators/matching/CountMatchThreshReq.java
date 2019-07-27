package org.bse.utils.requirement.operators.matching;

import org.bse.utils.requirement.InsatiableReqException;
import org.bse.utils.requirement.RequireOpResult;
import org.bse.utils.requirement.RequireOpResult.RequireOpResultStatus;
import org.bse.utils.requirement.Requirement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO: write documentation.
 * @param <T>
 */
public final class CountMatchThreshReq<T> extends AbstractMatchThreshReq<T> {

    public CountMatchThreshReq(int threshold, Set<T> candidates) throws InsatiableReqException {
        super(threshold, candidates);

        // Check validity of the arguments:
        if (candidates.size() < threshold) {
            throw new InsatiableReqException(String.format("The provided threshold"
                    + " (%s) is greater than the number of provided candidates (%s).",
                    threshold, candidates.size()
            ));
        }
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
        try {
            return new CountMatchThreshReq<>(
                    threshold, new HashSet<>(getCandidates())
            );
        } catch (InsatiableReqException e) {
            // Should never reach here: All [Requirement] implementations must be
            // immutable (See [Requirement] spec), and validity is enforced in the
            // constructor.
            throw new InsatiableReqException.UnexpectedInsatiableReqException(e);
        }
    }

    @Override
    public Requirement<Set<T>> excludingPassingTermsFor(Set<T> givens) {
        Set<T> candidates = getCandidates();
        Map<Boolean, Set<T>> partition = givens.stream()
                .collect(Collectors.partitioningBy(
                        candidates::contains, Collectors.toSet()
                ));
        if (partition.get(true).size() >= threshold) {
            // Enough terms matched to pass completely.
            return null;

        } else if (partition.get(false).size() == threshold) {
            // No terms matched at all.
            return this;

        } else {
            // Some terms matched, but not enough.
            try {
                return new CountMatchThreshReq<>(
                        threshold - partition.get(true).size(),
                        partition.get(false));
            } catch (InsatiableReqException e) {
                throw new InsatiableReqException.UnexpectedInsatiableReqException(e);
                // return this;
            }
        }
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
        // I am unreasonably proud of this.
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
     * @param candidate An object that, if found in a test subject, will cause this
     *     [Requirement] to return with a passing status.
     * @param <T> The type of items contained in a test subject collection.
     * @return A [MatchingReqIf] that requires a test subject to contain [candidate].
     */
    public static <T> CountMatchThreshReq<T> ONLY(T candidate) {
        try {
            return new CountMatchThreshReq<>(1, Collections.singleton(candidate));
        } catch (InsatiableReqException e) {
            // Should never reach here: the primitive value 1 is always less than or equal to
            // the size of a singleton collection (Ie. 1).
            throw new InsatiableReqException.UnexpectedInsatiableReqException(e);
        }
    }

}
