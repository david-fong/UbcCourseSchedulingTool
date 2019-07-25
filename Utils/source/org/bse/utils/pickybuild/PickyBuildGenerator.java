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
    private final List<Set<T>> conjunctiveNormalFormed;

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
            Supplier<PickyBuild<T>> emptyBuildSupplier,
            Set<Set<T>> conjunctiveNormalFormed) {
        this.emptyBuildSupplier = emptyBuildSupplier;
        //this.conjunctiveNormalFormed = conjunctiveNormalFormed;
        this.conjunctiveNormalFormed = conjunctiveNormalFormed.stream()
                .map(Collections::unmodifiableSet)
                .collect(Collectors.toList());
        this.conjunctiveNormalFormed.sort(Comparator.comparingInt(Set::size));
    }

    public Set<PickyBuild<T>> generateAllFullPickyBuilds() {
        return null; // TODO:
    }

}
