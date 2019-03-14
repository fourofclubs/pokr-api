// Created on Sep 6, 2018
package ca.foc.pokr;

import static ca.foc.pokr.Card.Suit.allSuits;
import static ca.foc.pokr.Card.Value.*;
import static ca.foc.pokr.Hand.*;
import static ca.foc.pokr.Menu.menu;
import static fj.Ord.intOrd;
import static fj.data.Either.left;
import static fj.data.Either.right;
import static fj.data.List.arrayList;
import ca.foc.pokr.Card.Suit;
import ca.foc.pokr.Card.Value;
import fj.F;
import fj.Ord;
import fj.Show;
import fj.data.Either;
import fj.data.List;

public final class HandBuilder {
    private static final HandBuilder buildPair = handBuilder(Value.show, "Pair of ..?", allValues, v -> right(pair(v)));
    private static final HandBuilder buildTwoPair = handBuilder(Value.show, "First value?", allValues, v1 -> left(handBuilder(
            Value.show, "Second value?", allValues.delete(v1, Value.eq), v2 -> right(twoPair(v1, v2)))));
    private static final HandBuilder buildTrips = handBuilder(Value.show, "Three what?", allValues, v -> right(trips(v)));
    private static final HandBuilder buildStraight = handBuilder(Value.show, "Straight to the ..?", allValues.filter(Value.ord
            .isGreaterThan(FOUR)), v -> right(straight(v)));
    private static final HandBuilder buildFlush = handBuilder(Suit.show, "In?", allSuits, s -> left(handBuilder(Value.show,
            "High card?", allValues.filter(Value.ord.isGreaterThan(FIVE)), v -> right(flush(s, v)))));
    private static final HandBuilder buildFullHouse = handBuilder(Value.show, "First value?", allValues, v1 -> left(handBuilder(
            Value.show, "Over?", allValues.delete(v1, Value.eq), v2 -> right(fullHouse(v1, v2)))));
    private static final HandBuilder buildQuads = handBuilder(Value.show, "Four what?", allValues, v -> right(quads(v)));
    private static final HandBuilder buildStraightFlush = handBuilder(Suit.show, "In?", allSuits, s -> left(handBuilder(
            Value.show, "High card?", allValues.filter(Value.ord.isGreaterThan(FOUR)), v -> right(straightFlush(v, s)))));

    public final Menu<Either<HandBuilder, Hand>> menu;

    private HandBuilder(final Menu<Either<HandBuilder, Hand>> menu) {
        this.menu = menu;
    }

    public static final HandBuilder initialBuilder() {
        return handBuilder(HandType.show, "Choose a hand...", HandType.allHandTypes, h -> left(h.builder));
    }

    static final <S> HandBuilder handBuilder(final Show<S> show, final String prompt, final List<S> options,
            final F<S, Either<HandBuilder, Hand>> select) {
        return new HandBuilder(menu(show, prompt, options, select));
    }

    private enum HandType {
        PAIR(1, buildPair),
        TWO_PAIR(2, buildTwoPair),
        TRIPS(3, buildTrips),
        STRAIGHT(4, buildStraight),
        FLUSH(5, buildFlush),
        FULL_HOUSE(6, buildFullHouse),
        QUADS(7, buildQuads),
        STRAIGHT_FLUSH(8, buildStraightFlush);

        public static final Show<HandType> show = Show.<HandType> anyShow();
        public static final Ord<HandType> ord = intOrd.contramap(h -> Integer.valueOf(h.rank));
        public static final List<HandType> allHandTypes = arrayList(HandType.values()).sort(ord);

        public final int rank;
        public final HandBuilder builder;

        private HandType(final int rank, final HandBuilder builder) {
            this.rank = rank;
            this.builder = builder;
        }
    }
}
