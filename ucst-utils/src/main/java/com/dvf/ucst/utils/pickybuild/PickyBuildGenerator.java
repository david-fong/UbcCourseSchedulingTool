package com.dvf.ucst.utils.pickybuild;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Used to generate all possible [PickyBuild]s (which inherently have no conflicts).
 *
 * @param <T> The type of items contained in generated [PickyBuild]s.
 */
public final class PickyBuildGenerator<T extends PickyBuildElement<T>> {

    private final PickyBuild<T> templateBuild;
    private final List<Set<T>> clauses; // unmodifiable
    private final int numClauses;

    /**
     * @param templateBuild All [PickyBuild]s in the collection returned from the
     *     [generateAllFullPickyBuilds] operation will be supersets of [templateBuild]
     *     that are safely type-cast-able to the runtime type of [templateBuild] as
     *     is guaranteed by the [PickyBuild.copy] method spec.
     * @param conjunctiveNormalFormed A set of clauses representing option groups
     *      where each clause is composed of items representing options, and the group
     *      of clauses represents a requirement for an inclusion of at least one option
     *      from each clause. Like a logical AND of logical OR's.
     *   - It is allowed for clauses to have shared items (using ::equals comparison).
     *       See spec of [PickyBuild::addIfNoConflicts] for handling of such situations.
     *   - Empty clauses are allowed and will be ignored.
     *   - [conjunctiveNormalFormed] must not be [null]. If empty, then generating
     *       builds will return [templateBuild] directly.
     */
    public PickyBuildGenerator(
            final PickyBuild<T> templateBuild,
            final Set<Set<T>> conjunctiveNormalFormed) {
        this.templateBuild = templateBuild;
        this.clauses = Collections.unmodifiableList(conjunctiveNormalFormed.stream()
                .filter(clause -> !clause.isEmpty()) // ignore empty clauses.
                .filter(clause -> !templateBuild.containsAny(clause)) // filter out satisfied clauses to prevent infinite loops.
                .map(Collections::unmodifiableSet) // don't trust anybody- not even yourself.
                .sorted(Comparator.comparingInt(Set::size)) // order more-restrictive clauses first.
                .collect(Collectors.toList())
        );
        this.numClauses = clauses.size();
    }

    /**
     * @return An unmodifiable [Set]. Never [null]. See constructor spec.
     */
    public Set<PickyBuild<T>> generateAllFullPickyBuilds() {
        return (clauses.isEmpty())
                ? Collections.singleton(templateBuild)
                : Collections.unmodifiableSet(recursiveGenerateBuilds(0, Set.of(templateBuild)))
                ;
    }

    /**
     * @param conjunctiveNormalFormed See the similar constructor argument for this
     *     class.
     * @param <T> The type of elements to be added to a picky build using
     *     [conjunctiveNormalFormed].
     * @return A modified version of [conjunctiveNormalFormed] where the union of all
     *     its clauses has not changed, but is different in that any clauses sharing
     *     an option item will have been merged into a single clause. This means that
     *     if the provided clauses were like {{A, B, C}, {B, D}, {E, F}}, then the
     *     returned value would be {{A, B, C, D}, {E, F}}. This causes a subtle, but
     *     important difference in behaviour when generating [PickyBuild]s: Without
     *     first passing [conjunctiveNormalFormed] through this function, the set of
     *     full [PickyBuild]s will include the builds {(A|C),(B|D),(E|F)}, {B,(E|F)},
     *     {B,(D)?,(E|F)}. But, if it is first passed through this method, it will
     *     only include the builds {(A|B|C|D),(E|F)}. Options are considered to be
     *     shared between provided clauses through the use of their ::equals method,
     *     in the same way as is done in [PickyBuild::addIfNoConflicts].
     */
    public static <T extends PickyBuildElement<T>> Set<Set<T>> mergeSharedClauseItems
    (final Set<Set<T>> conjunctiveNormalFormed) {
        final Set<Set<T>> mergedClauses = new HashSet<>();
        for (final Set<T> unMergedClause : conjunctiveNormalFormed) {
            boolean didNotMerge = true;
            for (final Set<T> mergedClause : mergedClauses) {
                if (unMergedClause.stream().anyMatch(mergedClause::contains)) {
                    mergedClause.addAll(unMergedClause);
                    didNotMerge = false;
                    break;
                }
            }
            if (didNotMerge) {
                mergedClauses.add(unMergedClause);
            }
        }
        return mergedClauses;
    }

    /**
     * @param clauseIdx The index of the clause in [.clauses] to add options from to
     *     all [PickyBuild]s collected so far in [soFar].
     * @param soFar All [PickyBuild]s containing one option from each clause in
     *     [clauses] from index zero to index [clauseIdx] EXCLUSIVE. Is not modified
     *     by this operation in any way, and is no longer needed after this operation.
     * @return All [PickyBuild]s containing one option from each clause in [clauses]
     *     from index zero to index [clauseIdx] INCLUSIVE. Important conceptual note:
     *     the size of [PickyBuild]s in the returned collection will not necessarily
     *     have incremented from the [PickyBuild] they were copied from. See the note
     *     for [PickyBuild::addIfNoConflicts] on adding duplicates.
     */
    private Set<PickyBuild<T>> recursiveGenerateBuilds(final int clauseIdx, final Set<PickyBuild<T>> soFar) {
        if (soFar.size() == 0 || clauseIdx == numClauses) {
            return soFar;
        }
        final Set<PickyBuild<T>> newSoFar = new HashSet<>();

        // for each build from the previous recursive operation that might
        // not yet have an element from the clause for this recursive step,
        for (PickyBuild<T> buildInProgress : soFar) {

            // with each element from the clause for this recursive step,
            for (T optionFromNextClause : clauses.get(clauseIdx)) {
                final PickyBuild<T> optionCopy = buildInProgress.copy();

                // try adding that element to that build,
                if (optionCopy.addIfNoConflicts(optionFromNextClause)) {

                    // and if that build accepts that element, hold
                    // on to that build for the next recursive step.
                    newSoFar.addAll(new PickyBuildGenerator<>(
                            optionCopy, // <- the build with that element
                            optionFromNextClause.getPickyBuildFriends() // <- make sure it has its friends.
                    ).generateAllFullPickyBuilds());
                }
            }
        }
        return recursiveGenerateBuilds(clauseIdx + 1, newSoFar);
    }



    /**
     * package-private class intended for testing purposes only.
     * @param <T>
     */
    static final class BuildEquivalenceComparison<T extends PickyBuildElement<T>> {

        private final Set<Set<T>> unmatchedExpectedBuildContents;
        private final Set<Set<T>> unmatchedActualBuildContents;

        BuildEquivalenceComparison(
                final Set<Set<T>> expectedBuildContents, // <- may be unmodifiable (same for contents). must assume so.
                final Set<PickyBuild<T>> actualBuilds
        ) {
            final Set<Set<T>> actualBuildContents = actualBuilds.stream()
                    .map(PickyBuild::getAllContents)
                    .collect(Collectors.toSet());
            final Set<Set<T>> unmatchedExpectedBuildContents = new HashSet<>(expectedBuildContents);
            final Set<Set<T>> unmatchedActualBuildContents = new HashSet<>(actualBuildContents);

            for (final Set<T> buildContent : expectedBuildContents) {

            }

            this.unmatchedExpectedBuildContents = Collections.unmodifiableSet(unmatchedExpectedBuildContents);
            this.unmatchedActualBuildContents = Collections.unmodifiableSet(unmatchedActualBuildContents);
        }

        public boolean expectedAndActualMatch() {
            return getUnmatchedExpectedBuildContents().isEmpty() && getUnmatchedActualBuildContents().isEmpty();
        }

        public Set<Set<T>> getUnmatchedExpectedBuildContents() {
            return unmatchedExpectedBuildContents;
        }

        public Set<Set<T>> getUnmatchedActualBuildContents() {
            return unmatchedActualBuildContents;
        }
    }

}
