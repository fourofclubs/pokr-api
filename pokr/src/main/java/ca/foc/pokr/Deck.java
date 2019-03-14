// Created on Sep 5, 2018
package ca.foc.pokr;

import static ca.foc.pokr.Card.card;
import static ca.foc.pokr.Card.Suit.*;
import static ca.foc.pokr.Card.Value.*;
import static fj.Equal.listEqual;
import static fj.Hash.listHash;
import static fj.Show.listShow;
import static fj.data.List.arrayList;
import fj.Equal;
import fj.Hash;
import fj.Show;
import fj.data.List;

public final class Deck {
    public static final Equal<Deck> eq = listEqual(Card.eq).contramap(d -> d.cards);
    public static final Hash<Deck> hash = listHash(Card.hash).contramap(d -> d.cards);
    public static final Show<Deck> show = listShow(Card.show).contramap(d -> d.cards);
    public final List<Card> cards;

    private Deck(final List<Card> cards) {
        this.cards = cards;
    }

    public static final Deck deck(final List<Card> cards) {
        return new Deck(cards);
    }

    public static final Deck micholsonStack() {
        return new Deck(arrayList(card(KING, SPADES), card(FOUR, HEARTS), card(KING, HEARTS), card(EIGHT, CLUBS), card(NINE,
                DIAMONDS), card(SIX, SPADES), card(EIGHT, DIAMONDS), card(THREE, SPADES), card(TEN, HEARTS), card(FIVE, CLUBS),
                card(KING, CLUBS), card(QUEEN, SPADES), card(FIVE, HEARTS), card(NINE, SPADES), card(SEVEN, HEARTS), card(TWO,
                        CLUBS), card(TEN, CLUBS), card(FIVE, DIAMONDS), card(TWO, SPADES), card(FOUR, DIAMONDS),
                card(TWO, HEARTS), card(QUEEN, HEARTS), card(SEVEN, CLUBS), card(JACK, DIAMONDS), card(EIGHT, SPADES), card(TEN,
                        DIAMONDS), card(FIVE, SPADES), card(NINE, HEARTS), card(FOUR, CLUBS), card(QUEEN, CLUBS), card(ACE,
                        DIAMONDS), card(THREE, HEARTS), card(JACK, SPADES), card(SIX, HEARTS), card(ACE, CLUBS),
                card(NINE, CLUBS), card(SEVEN, DIAMONDS), card(FOUR, SPADES), card(SIX, DIAMONDS), card(ACE, SPADES), card(JACK,
                        HEARTS), card(SIX, CLUBS), card(KING, DIAMONDS), card(TEN, SPADES), card(QUEEN, DIAMONDS), card(SEVEN,
                        SPADES), card(EIGHT, HEARTS), card(THREE, CLUBS), card(JACK, CLUBS), card(THREE, DIAMONDS), card(ACE,
                        HEARTS), card(TWO, DIAMONDS)));
    }

    public final Card cardAt(final int i) {
        return this.cards.index(i - 1);
    }

    public final Deck cutAt(final int n) {
        return deck(this.cards.drop(n).append(this.cards.take(n)));
    }

    public final int size() {
        return this.cards.length();
    }

    @Override public String toString() {
        return show.showS(this);
    }
}
