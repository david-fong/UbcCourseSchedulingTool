package com.dvf.ucst.utils.requirement.operators.matching;

import com.dvf.ucst.utils.requirement.RequireOpResult.ReqOpOutcome;
import com.dvf.ucst.utils.requirement.Requirement;
import com.dvf.ucst.utils.xml.XmlUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Requires a certain number of entries in a test subject to match
 * candidates provided in the constructor.
 * @param <T>
 */
public final class CountMatchThreshReq<T> extends AbstractMatchThreshReq<T> {

    private final long numBarelyPassingCombinations;

    public CountMatchThreshReq(int threshold, Set<T> candidates) {
        super(threshold, candidates);
        assert candidates.size() >= threshold : String.format("The provided"
                + " threshold (%s) is greater than the number of provided"
                + " candidates (%s).", threshold, candidates.size()
        );

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
        this.numBarelyPassingCombinations = numCombinations;
    }

    // TODO [xml:read]: Xml parsing constructor?

    @Override
    public ReqOpOutcome requireOf(Set<T> testSubject) {
        final long countOfMatches = this.getCandidates().stream()
                .filter(testSubject::contains)
                .count();
        return countOfMatches >= this.threshold
                ? ReqOpOutcome.PASSED_REQ
                : ReqOpOutcome.FAILED_REQ;
    }

    @Override
    public CountMatchThreshReq<T> copy() {
        return new CountMatchThreshReq<>(
                threshold, new HashSet<>(getCandidates())
        );
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
            return new CountMatchThreshReq<>(
                    threshold - partition.get(true).size(),
                    partition.get(false));
        }
    }

    @Override
    public long getNumBarelyPassingCombinations() {
        return numBarelyPassingCombinations;
    }

    @Override
    public Set<Set<T>> getAllBarelyPassingCombinations() {
        final Set<Set<T>> accumulator = new HashSet<>((int) getNumBarelyPassingCombinations());
        final List<T> children = new ArrayList<>(getCandidates());

        for (int startIdx = 0; startIdx <= children.size() - threshold; startIdx++) {
            accumulator.addAll(
                    recursiveGetPassingCombinations(threshold, startIdx, children)
            );
        }
        return accumulator;
    }
    /* A helper for [getAllBarelyPassingCombinations].
     * [children] must not be modified externally or internally.
     * Only gets combinations that include [children.get(startId)]
     *   and no children whose indices in [children] are less than
     *   [startIdx].
     */
    private Set<Set<T>> recursiveGetPassingCombinations
            (final int threshold, final int startIdx, final List<T> children) {

        // Check break condition:
        if (threshold == 1) {
            return Collections.singleton(Collections.singleton(
                    children.get(startIdx)
            ));
        }

        final Set<Set<T>> accumulator = new HashSet<>();
        for (int subStartIdx = startIdx + 1; subStartIdx <= children.size() - threshold; subStartIdx++) {
            final Set<Set<T>> subPassingCombos = recursiveGetPassingCombinations(
                    threshold - 1, subStartIdx, children
            );
            for (Set<T> subCombo : subPassingCombos) {
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
        return new CountMatchThreshReq<>(1, Collections.singleton(candidate));
    }

    public enum Xml implements XmlUtils.XmlConstant {
        COUNT_MTR_TAG ("CountMtr"),
        ;
        private final String value;

        Xml(String value) {
            this.value = value;
        }

        @Override
        public String getXmlConstantValue() {
            return value;
        }
    }

}
