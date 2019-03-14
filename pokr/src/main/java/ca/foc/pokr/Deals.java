// Created on Sep 10, 2018
package ca.foc.pokr;

import static ca.foc.pokr.Deal.deal;
import static ca.foc.pokr.Deck.deck;
import static ca.foc.pokr.Utils.findLastIndex;
import static ca.foc.pokr.Utils.permutations;
import static fj.Function.compose;
import static fj.Ord.intOrd;
import static fj.P.p;
import static fj.data.List.*;
import static fj.data.Option.*;
import static fj.function.Integers.sum;
import fj.F;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Option;
import fj.data.TreeMap;

public final class Deals {
    public static final TreeMap<Integer, Deal> playerDeals(final Hand hand, final Deck d) {
        return playerDeals(hand, d, players -> deal -> evaluateDeal(players.intValue(), deal));
    }

    static final Option<P2<Integer, Deck>> deal1(final int players, final F<Card, Boolean> isCard, final Deck d) {
        final P2<List<Card>, List<Card>> split = d.cards.splitAt(players);
        return findLastIndex(isCard, split._1()).map(
                compose(i -> p(i, deck(split._2())), i -> Integer.valueOf((i.intValue() + 1) % players)));
    }

    static final Option<P2<Integer, Deck>> deal2(final int players, final F<Card, Boolean> isCard, final Deck d) {
        final P2<List<Card>, List<Card>> split = d.cards.splitAt(players);
        return split._2().headOption().filter(isCard).map(
                c -> p(Integer.valueOf(players), deck(split._2().tail().cons(split._1().last()))));
    }

    static final Option<P2<Integer, Deck>> deal3(final int players, final F<Card, Boolean> isCard, final Deck d) {
        final P2<List<Card>, List<Card>> split = d.cards.splitAt(players - 1);
        return iif(!split._2().isEmpty(), () -> split._2().last()).filter(isCard).map(
                c -> p(Integer.valueOf(-1), deck(split._2().init())));
    }

    static final Option<P2<Integer, Deck>> dealOne(final int players, final F<Card, Boolean> isCard, final Deck d) {
        return dealOne(players, isCard, d, i -> Integer.valueOf(evaluateSingle(players, i.intValue())));
    }

    static final Option<P2<Integer, Deck>> dealOne(final int players, final F<Card, Boolean> isCard, final Deck d,
            final F<Integer, Integer> evaluate) {
        if ( d.size() < players ) {
            return none();
        }
        final List<P2<Integer, Deck>> deals =
                somes(arrayList(deal1(players, isCard, d), deal2(players, isCard, d), deal3(players, isCard, d)));
        return deals.maximumOption(intOrd.contramap(evaluate).contramap(P2.<Integer, Deck> __1()));
    }

    static final Integer evaluateDeal(final int players, final Deal d) {
        return Integer.valueOf(sum(d.seconds.map(i -> Integer.valueOf(evaluateSingle(players, i.intValue())))));
    }

    static final int evaluateSingle(final int players, final int deal) {
        final int seconds = players == deal ? 1 : deal == 0 ? 0 : players - deal;
        return players - seconds;
    }

    static final TreeMap<Integer, Deal>
            playerDeals(final Hand hand, final Deck d, final F<Integer, F<Deal, Integer>> evaluateDeal) {
        return TreeMap.iterableTreeMap(intOrd, somes(range(3, 10).map(
                p -> search(p.intValue(), hand, d, evaluateDeal.f(p)).map(P.<Integer, Deal> p2().f(p)))));
    }

    static final Option<Deal> search(final int players, final Hand hand, final Deck d) {
        return search(players, hand, d, deal -> evaluateDeal(players, deal));
    }

    static final Option<Deal> search(final int players, final Hand hand, final Deck d, final F<Deal, Integer> evaluateDeal) {
        final List<Deal> deals = permutations(hand.cards(), CardPredicate.eq).bind(ps -> search(players, ps, d));
        return deals.maximumOption(intOrd.contramap(evaluateDeal));
    }

    static final List<Deal> search(final int players, final List<CardPredicate> ps, final Deck d) {
        return somes(range(1, d.size()).map(n -> searchFrom(n.intValue(), players, ps, d)));
    }

    static final Option<Deal> searchFrom(final int n, final int players, final List<CardPredicate> ps, final Deck d) {
        return searchFromTop(players, ps, d.cutAt(n)).map(seconds -> deal(n, seconds));
    }

    static final Option<List<Integer>> searchFromTop(final int players, final List<CardPredicate> ps, final Deck d) {
        return ps.isEmpty() ? some(nil()) : dealOne(players, ps.head(), d).bind(
                p -> searchFromTop(players, ps.tail(), p._2()).map(List.cons_(p._1())));
    }
}
