// Created on Sep 5, 2018
package ca.foc.pokr;

import static fj.Equal.p2Equal;
import static fj.Hash.p2Hash;
import static fj.Ord.intOrd;
import static fj.P.p;
import static fj.Show.show;
import static fj.Show.stringShow;
import static fj.data.List.arrayList;
import fj.*;
import fj.data.List;
import fj.data.Option;
import fj.data.TreeMap;

@SuppressWarnings("javadoc") public final class Card {
    public static final List<Card> all = arrayList(Suit.values()).bind(arrayList(Value.values()), s -> v -> card(v, s));
    public static final Equal<Card> eq;
    public static final Hash<Card> hash;
    public static final Show<Card> show = show(c -> Show.<Value> anyShow().show(c.value).append(stringShow.show(" of ")).append(
            Show.<Suit> anyShow().show(c.suit)));
    static {
        final F<Card, P2<Value, Suit>> asTuple = c -> p(c.value, c.suit);
        eq = p2Equal(Equal.<Value> anyEqual(), Equal.<Suit> anyEqual()).contramap(asTuple);
        hash = p2Hash(Hash.<Value> anyHash(), Hash.<Suit> anyHash()).contramap(asTuple);
    }
    public final Suit suit;
    public final Value value;

    private Card(final Value value, final Suit suit) {
        this.value = value;
        this.suit = suit;
    }

    public static final Card card(final Value v, final Suit s) {
        return new Card(v, s);
    }

    @Override public String toString() {
        return show.showS(this);
    }
    enum Suit {
        CLUBS,
        DIAMONDS,
        HEARTS,
        SPADES;

        static final List<Suit> allSuits = arrayList(Suit.values());
        static final Equal<Suit> eq = Equal.<Suit> anyEqual();
        static final Show<Suit> show = Show.<Suit> anyShow();
    }

    enum Value {
        ACE(14),
        EIGHT(8),
        FIVE(5),
        FOUR(4),
        JACK(11),
        KING(13),
        NINE(9),
        QUEEN(12),
        SEVEN(7),
        SIX(6),
        TEN(10),
        THREE(3),
        TWO(2);

        static final Equal<Value> eq = Equal.<Value> anyEqual();
        static final Ord<Value> ord = intOrd.contramap(v -> Integer.valueOf(v.numeric));
        static final Show<Value> show = Show.<Value> anyShow();
        static final List<Value> allValues = arrayList(Value.values()).sort(ord);
        private static final TreeMap<Integer, Value> index = TreeMap.iterableTreeMap(intOrd, allValues.map(
                v -> p(Integer.valueOf(v.numeric), v)).cons(p(Integer.valueOf(1), ACE)));

        public final int numeric;

        private Value(final int numeric) {
            this.numeric = numeric;
        }

        public static final Option<Value> fromNumeric(final int numeric) {
            return index.get(Integer.valueOf(numeric));
        }
    }
}
