// Created on Sep 5, 2018
package ca.foc.pokr;

import static ca.foc.pokr.Deck.micholsonStack;
import static fj.test.Gen.*;
import static fj.test.Property.prop;
import static fj.test.Property.property;

import org.junit.runner.RunWith;

import fj.test.Gen;
import fj.test.Property;
import fj.test.runner.PropertyTestRunner;

@SuppressWarnings("javadoc") public final class DeckTest {
    static final Gen<Card> arbCard = pickOne(Card.all);
    static final Gen<Deck> arbDeck = someSelectionOf(Card.all.length(), Card.all).map(Deck::deck);
    @SuppressWarnings("boxing") static final Gen<Deck> arbNonEmptyDeck = arbDeck.filter(d -> d.size() > 1);

    static final Property areEqual(final Deck d1, final Deck d2) {
        return prop(Deck.eq.eq(d1, d2));
    }

    static final Property sizesAreEqual(final Deck d1, final Deck d2) {
        return prop(d1.size() == d2.size());
    }

    @RunWith(PropertyTestRunner.class) public static class TestCutAt {
        public static final Property lengthIsConstant = property(arbDeck, d -> property(choose(0, d.size()), n -> sizesAreEqual(d
                .cutAt(n.intValue()), d)));
        public static final Property zeroCutIsNull = property(arbDeck, d -> areEqual(d.cutAt(0), d));
        public static final Property maxCutIsNull = property(arbDeck, d -> areEqual(d.cutAt(d.size()), d));
        public static final Property cut1TopToBottom = property(arbNonEmptyDeck, d -> areEqual(d.cutAt(1), Deck.deck(d.cards
                .tail().snoc(d.cards.head()))));
        public static final Property cutCardToBottom = property(arbNonEmptyDeck, d -> property(choose(1, d.size()),
                n -> prop(Card.eq.eq(d.cutAt(n.intValue()).cardAt(d.size()), d.cardAt(n.intValue())))));
        public static final Property cardMovesN = property(arbNonEmptyDeck, d -> property(choose(0, d.size() - 1), n -> property(
                choose(n.intValue() + 1, d.size()), i -> prop(Card.eq.eq(d.cardAt(i.intValue()), d.cutAt(n.intValue()).cardAt(
                        i.intValue() - n.intValue()))))));
        public static final Property cardMovesN2 = property(arbNonEmptyDeck, d -> property(choose(1, d.size()), n -> property(
                choose(1, n.intValue()), i -> prop(Card.eq.eq(d.cardAt(i.intValue()), d.cutAt(n.intValue()).cardAt(
                        i.intValue() + (d.size() - n.intValue())))))));
    }

    @RunWith(PropertyTestRunner.class) public static class TestMicholsonStack {
        public static final Property isFullDeck = prop(micholsonStack().size() == 52).and(
                prop(micholsonStack().cards.nub(Card.eq).length() == 52));
    }
}
