// Created on Sep 7, 2018
package ca.foc.pokr;

import static fj.Equal.listEqual;
import static fj.Function.compose;
import static fj.data.List.range;
import fj.Equal;
import fj.F;
import fj.P2;
import fj.data.List;
import fj.data.NonEmptyList;
import fj.data.Option;

final class Utils {
    static final <A> List<Integer> findIndices(final F<A, Boolean> p, final List<A> as) {
        return as.zipIndex().filter(compose(p, P2.<A, Integer> __1())).map(P2.<A, Integer> __2());
    }

    static final <A> Option<Integer> findLastIndex(final F<A, Boolean> p, final List<A> as) {
        return NonEmptyList.fromList(findIndices(p, as)).map(is -> is.toList().last());
    }

    static final <A> List<List<A>> permutations(final List<A> as, final Equal<A> eq) {
        if ( as.isEmpty() ) {
            return List.single(List.nil());
        }
        return range(0, as.length()).bind(i -> {
            final P2<List<A>, List<A>> p = as.splitAt(i.intValue());
            return permutations(p._1().append(p._2().tail()), eq).map(List.cons_(p._2().head()));
        }).nub(listEqual(eq));
    }
}
