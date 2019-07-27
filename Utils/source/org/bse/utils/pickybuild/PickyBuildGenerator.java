package org.bse.utils.pickybuild;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Used to generate all possible [PickyBuild]s (which inherently have no conflicts.
 *
 * @param <T> The type of items contained in generated [PickyBuild]s.
 */
public final class PickyBuildGenerator<T> {

    private final Supplier<PickyBuild<T>> emptyBuildSupplier;
    private final List<Set<T>> clauses;
    private final int numClauses;

    /**
     *
     * @param emptyBuildSupplier A supplier of empty [PickyBuild]s as templates for
     *     [generateAllFullPickyBuilds].
     * @param conjunctiveNormalFormed An AND of OR's. Each OR clause represents items
     *     of which one and only one must be included in each [PickyBuild] generated
     *     by [generateAllFullPickyBuilds]. This field initializer is not defensively
     *     copied, so this collection, its clauses, and any enclosed [T] items must not
     *     be modified after being passed to this constructor. The amount of unnecessary
     *     computation in [generateAllFullPickyBuilds] may be reduced if more restrictive
     */
    public PickyBuildGenerator(
            final Supplier<PickyBuild<T>> emptyBuildSupplier,
            final Set<Set<T>> conjunctiveNormalFormed) {
        this.emptyBuildSupplier = emptyBuildSupplier;
        //this.clauses = clauses;
        this.clauses = conjunctiveNormalFormed.stream()
                .map(Collections::unmodifiableSet)
                .collect(Collectors.toList());
        this.clauses.sort(Comparator.comparingInt(Set::size));
        this.numClauses = clauses.size();
    }

    public Set<PickyBuild<T>> generateAllFullPickyBuilds() {
        Set<PickyBuild<T>> soFar = new HashSet<>();
        for (T option : clauses.get(0)) {
            PickyBuild<T> buildSeedOption = emptyBuildSupplier.get();
            if (buildSeedOption.addIfNoConflicts(option)) {
                soFar.add(buildSeedOption);
            }
        }
        return recursiveGenerateBuilds(0, soFar);
    }

    // TODO: test.
    private Set<PickyBuild<T>> recursiveGenerateBuilds
            (final int clauseIdx, final Set<PickyBuild<T>> soFar) {
        if (soFar.size() == 0 || clauseIdx == numClauses) {
            return soFar;
        }
        final Set<PickyBuild<T>> newSoFar = new HashSet<>();
        for (PickyBuild<T> buildInProgress : soFar) {
            Set<PickyBuild<T>> subSoFar = new HashSet<>();
            for (T nextClauseOption : clauses.get(clauseIdx + 1)) {
                PickyBuild<T> optionCopy = buildInProgress.copy();
                if (optionCopy.addIfNoConflicts(nextClauseOption)) {
                    subSoFar.add(optionCopy);
                }
            }
            newSoFar.addAll(subSoFar);
        }
        return recursiveGenerateBuilds(clauseIdx + 1, newSoFar);
    }

    /**
     * Helper for [generateAllFullPickyBuilds]
     * to setup the result collection's initial capacity.
     */
    private int guessNumResults() {
        final int percentReciprocal = 4;
        return clauses.stream()
                .mapToInt(Set::size)
                .reduce(1, (a, b) -> a * b)
                / percentReciprocal
                ;
    }

}
