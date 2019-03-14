// Created on Mar 14, 2019
package ca.foc.pokr;

import static ca.foc.pokr.Card.Suit.*;
import static ca.foc.pokr.Hand.*;
import static fj.Equal.optionEqual;
import static fj.data.Option.none;
import static fj.data.Option.some;
import static fj.test.Arbitrary.arbEnumValue;
import static fj.test.Property.*;

import org.junit.runner.RunWith;

import ca.foc.pokr.Card.Value;
import fj.test.Property;
import fj.test.runner.PropertyTestRunner;

@SuppressWarnings("javadoc") @RunWith(PropertyTestRunner.class) public final class HandTest {
    static final Property readFlushClubs = property(arbEnumValue(Value.class), v -> prop(optionEqual(Hand.eq).eq(
            some(flush(CLUBS, v)), readHand(v.numeric + "^3"))));
    static final Property readFlushDiamonds = property(arbEnumValue(Value.class), v -> prop(optionEqual(Hand.eq).eq(
            some(flush(DIAMONDS, v)), readHand(v.numeric + "^4"))));
    static final Property readFlushHearts = property(arbEnumValue(Value.class), v -> prop(optionEqual(Hand.eq).eq(
            some(flush(HEARTS, v)), readHand(v.numeric + "^2"))));
    static final Property readFlushSpades = property(arbEnumValue(Value.class), v -> prop(optionEqual(Hand.eq).eq(
            some(flush(SPADES, v)), readHand(v.numeric + "^1"))));
    static final Property readFullHouse = property(arbEnumValue(Value.class), arbEnumValue(Value.class),
            v1 -> v2 -> impliesBoolean(Value.eq.notEq(v1, v2), optionEqual(Hand.eq).eq(some(fullHouse(v1, v2)),
                    readHand(v1.numeric + "/" + v2.numeric))));
    static final Property readInvalidFullHouse = property(arbEnumValue(Value.class), arbEnumValue(Value.class),
            v1 -> v2 -> impliesBoolean(Value.eq.eq(v1, v2), optionEqual(Hand.eq).eq(none(),
                    readHand(v1.numeric + "/" + v2.numeric))));
    static final Property readInvalidStraight = property(arbEnumValue(Value.class), v -> impliesBoolean(v.numeric < 5,
            optionEqual(Hand.eq).eq(none(), readHand("-" + v.numeric))));
    static final Property readInvalidStraightFlush = property(arbEnumValue(Value.class), v -> impliesBoolean(v.numeric < 5,
            optionEqual(Hand.eq).eq(none(), readHand("-" + v.numeric + "^3"))));
    static final Property readInvalidTwoPair = property(arbEnumValue(Value.class), arbEnumValue(Value.class),
            v1 -> v2 -> impliesBoolean(Value.eq.eq(v1, v2), optionEqual(Hand.eq).eq(none(),
                    readHand(v1.numeric + "+" + v2.numeric))));
    static final Property readPair = property(arbEnumValue(Value.class), v -> prop(optionEqual(Hand.eq).eq(some(pair(v)),
            readHand("2x" + v.numeric))));
    static final Property readQuads = property(arbEnumValue(Value.class), v -> prop(optionEqual(Hand.eq).eq(some(quads(v)),
            readHand("4x" + v.numeric))));
    static final Property readStraight = property(arbEnumValue(Value.class), v -> impliesBoolean(v.numeric >= 5, optionEqual(
            Hand.eq).eq(some(straight(v)), readHand("-" + v.numeric))));
    static final Property readStraightFlushClubs = property(arbEnumValue(Value.class), v -> impliesBoolean(v.numeric >= 5,
            optionEqual(Hand.eq).eq(some(straightFlush(v, CLUBS)), readHand("-" + v.numeric + "^3"))));
    static final Property readStraightFlushDiamonds = property(arbEnumValue(Value.class), v -> impliesBoolean(v.numeric >= 5,
            optionEqual(Hand.eq).eq(some(straightFlush(v, DIAMONDS)), readHand("-" + v.numeric + "^4"))));
    static final Property readStraightFlushHearts = property(arbEnumValue(Value.class), v -> impliesBoolean(v.numeric >= 5,
            optionEqual(Hand.eq).eq(some(straightFlush(v, HEARTS)), readHand("-" + v.numeric + "^2"))));
    static final Property readStraightFlushSpades = property(arbEnumValue(Value.class), v -> impliesBoolean(v.numeric >= 5,
            optionEqual(Hand.eq).eq(some(straightFlush(v, SPADES)), readHand("-" + v.numeric + "^1"))));
    static final Property readTrips = property(arbEnumValue(Value.class), v -> prop(optionEqual(Hand.eq).eq(some(trips(v)),
            readHand("3x" + v.numeric))));
    static final Property readTwoPair = property(arbEnumValue(Value.class), arbEnumValue(Value.class),
            v1 -> v2 -> impliesBoolean(Value.eq.notEq(v1, v2), optionEqual(Hand.eq).eq(some(twoPair(v1, v2)),
                    readHand(v1.numeric + "+" + v2.numeric))));
}
