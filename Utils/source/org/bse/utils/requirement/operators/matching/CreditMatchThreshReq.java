package org.bse.utils.requirement.operators.matching;

import org.bse.utils.requirement.RequireOpResult;
import org.bse.utils.requirement.Requirement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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

    public CreditMatchThreshReq(int threshold, Set<T> candidates) {
        super(threshold, candidates);
        final int creditTotal = getCandidates().stream()
                .mapToInt(CreditValued::getCreditValue)
                .sum();
        assert creditTotal >= threshold : String.format("The provided threshold"
                + " (%s) is greater than the sum of all the credit values of"
                + " the provided candidates (%d).", threshold, creditTotal
        );

        candidateCreditValues = getCandidates().stream()
                .mapToInt(CreditValued::getCreditValue)
                .toArray();
        Arrays.sort(candidateCreditValues); // ascending order

    }

    @Override
    public RequireOpResult.ReqOpOutcome requireOf(final Set<T> testSubject) {
        final int creditsOfMatching = getCandidates().stream()
                .filter(testSubject::contains)
                .mapToInt(CreditValued::getCreditValue)
                .sum();
        return creditsOfMatching >= this.threshold
                ? RequireOpResult.ReqOpOutcome.PASSED_REQ
                : RequireOpResult.ReqOpOutcome.FAILED_REQ;
    }

    @Override
    public CreditMatchThreshReq<T> copy() {
        return new CreditMatchThreshReq<>(
                threshold, new HashSet<>(getCandidates())
        );
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
            return new CountMatchThreshReq<>(
                    threshold - matchedValue,
                    partition.get(false));
        }
    }

    @Override
    public long estimateNumBarelyPassingCombinations() {
        // TODO: change this to use math with averages.
        return recursiveCountCombos(this.threshold, 0);
    }
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
        final Set<Set<T>> accumulator = new HashSet<>();
        final List<T> children = new ArrayList<>(getCandidates());

        for (int startIdx = 0; startIdx < children.size(); startIdx++) {
            final Set<Set<T>> subAccumulator = recursiveGetPassingCombinations(
                    threshold, startIdx, children
            );
            if (subAccumulator != null) {
                accumulator.addAll(subAccumulator);
            }
        }
        return accumulator;
    }
    /* Returns null if no passing combinations can be built using candidates from
     * [children] including [candidates.get(startIdx] and any candidates from
     * [children] after the index [startIdx].
     */
    private Set<Set<T>> recursiveGetPassingCombinations
            (final int threshold, final int startIdx, final List<T> children) {
        if (startIdx >= children.size()) return null;

        final int remainingCreditRequired = threshold - children.get(startIdx).getCreditValue();
        if (remainingCreditRequired <= 0) {
            return Collections.singleton(Collections.singleton(
                    children.get(startIdx)
            ));
        }

        final Set<Set<T>> accumulator = new HashSet<>();
        for (int subStartIdx = startIdx + 1; subStartIdx < children.size(); subStartIdx++) {
            final Set<Set<T>> subPassingCombos = recursiveGetPassingCombinations(
                    remainingCreditRequired, subStartIdx, children
            );
            if (subPassingCombos != null && !subPassingCombos.isEmpty()) {
                for (Set<T> subCombo : subPassingCombos) {
                    final Set<T> combined = new HashSet<>(subCombo);
                    combined.add(children.get(startIdx));
                    accumulator.add(combined);
                }
            }
        }
        return accumulator.isEmpty() ? null : accumulator;
    }

}
