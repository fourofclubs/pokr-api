// Created on Sep 6, 2018
package ca.foc.pokr;

import static ca.foc.pokr.Card.card;
import static ca.foc.pokr.CardPredicate.*;
import static ca.foc.pokr.Utils.permutations;
import static fj.Equal.p2Equal;
import static fj.Equal.v2Equal;
import static fj.P.p;
import static fj.data.List.range;
import static fj.data.List.replicate;
import static fj.data.Option.sequence;
import static fj.data.vector.V.v;
import static fj.function.Booleans.or;
import static java.lang.Boolean.FALSE;
import ca.foc.pokr.Card.Suit;
import ca.foc.pokr.Card.Value;
import fj.Equal;
import fj.Show;
import fj.data.List;
import fj.function.Booleans;

public abstract class Hand {
    public static final Equal<Hand> eq = Equal.<Hand> equal(h1 -> h2 -> h1.accept(new EqualsVisitor(h2)));
    public static final Show<Hand> show = Show.showS(h -> h.accept(new ShowVisitor()));

    public static Hand flush(final Suit s, final Value highCard) {
        return new Flush(highCard, s);
    }

    public static Hand fullHouse(final Value v1, final Value v2) {
        return new FullHouse(v1, v2);
    }

    public static Hand pair(final Value v) {
        return new Pair(v);
    }

    public static Hand quads(final Value v) {
        return new Quads(v);
    }

    public static Hand straight(final Value v) {
        return new Straight(v);
    }

    public static Hand straightFlush(final Value highCard, final Suit s) {
        return new StraightFlush(highCard, s);
    }

    public static Hand trips(final Value v) {
        return new Trips(v);
    }

    public static Hand twoPair(final Value v1, final Value v2) {
        return new TwoPair(v1, v2);
    }

    private static final List<Value> straightValues(final Value highCard) {
        return sequence(range(highCard.numeric - 4, highCard.numeric + 1).map(Value::fromNumeric)).orSome(List.<Value> nil());
    }

    public final List<CardPredicate> cards() {
        return this.accept(new Visitor<List<CardPredicate>>() {
            @Override public List<CardPredicate> visit(final Flush flush) {
                return replicate(4, CardPredicate.and(isAtMost(flush.highCard), isSuit(flush.s))).cons(
                        isCard(card(flush.highCard, flush.s)));
            }

            @Override public List<CardPredicate> visit(final FullHouse fullHouse) {
                return replicate(3, isValue(fullHouse.v1)).append(replicate(2, isValue(fullHouse.v2)));
            }

            @Override public List<CardPredicate> visit(final Pair pair) {
                return replicate(2, isValue(pair.v)).append(replicate(3, isNotValue(pair.v)));
            }

            @Override public List<CardPredicate> visit(final Quads quads) {
                return replicate(4, isValue(quads.v)).cons(isAnything());
            }

            @Override public List<CardPredicate> visit(final Straight straight) {
                return straightValues(straight.v).map(v -> isValue(v));
            }

            @Override public List<CardPredicate> visit(final StraightFlush straightFlush) {
                return straightValues(straightFlush.highCard).map(v -> isCard(card(v, straightFlush.s)));
            }

            @Override public List<CardPredicate> visit(final Trips trips) {
                return replicate(3, isValue(trips.v)).append(replicate(2, isNotValue(trips.v)));
            }

            @Override public List<CardPredicate> visit(final TwoPair twoPair) {
                return replicate(2, isValue(twoPair.v1)).append(replicate(2, isValue(twoPair.v2))).cons(
                        CardPredicate.and(isNotValue(twoPair.v1), isNotValue(twoPair.v2)));
            }
        });
    }

    @SuppressWarnings("boxing") public boolean isHand(final List<Card> cards) {
        final List<List<CardPredicate>> predicatePermutations = permutations(this.cards(), CardPredicate.eq);
        final List<Boolean> matchesPermutations =
                predicatePermutations.map(ps -> Booleans.and(ps.zipWith(cards, p -> c -> p.f(c))));
        final Boolean matchesAnyPermutation = or(matchesPermutations);
        return matchesAnyPermutation.booleanValue() && cards.length() >= this.cards().length();
    }

    abstract <A> A accept(final Visitor<A> v);

    public static abstract class AbstractVisitor<A> implements Visitor<A> {
        private final A defaultValue;

        public AbstractVisitor(final A defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override public A visit(final Flush flush) {
            return this.defaultValue;
        }

        @Override public A visit(final FullHouse fullHouse) {
            return this.defaultValue;
        }

        @Override public A visit(final Pair pair) {
            return this.defaultValue;
        }

        @Override public A visit(final Quads quads) {
            return this.defaultValue;
        }

        @Override public A visit(final Straight straight) {
            return this.defaultValue;
        }

        @Override public A visit(final StraightFlush straightFlush) {
            return this.defaultValue;
        }

        @Override public A visit(final Trips trips) {
            return this.defaultValue;
        }

        @Override public A visit(final TwoPair twoPair) {
            return this.defaultValue;
        }
    }

    public interface Visitor<A> {
        A visit(final Flush flush);

        A visit(final FullHouse fullHouse);

        A visit(final Pair pair);

        A visit(final Quads quads);

        A visit(final Straight straight);

        A visit(final StraightFlush straightFlush);

        A visit(final Trips trips);

        A visit(final TwoPair twoPair);
    }

    private static final class EqualsVisitor implements Visitor<Boolean> {
        private final Hand h;

        EqualsVisitor(final Hand h) {
            this.h = h;
        }

        @Override public Boolean visit(final Flush flush) {
            return this.h.accept(new AbstractVisitor<Boolean>(FALSE) {
                @Override public Boolean visit(final Flush flush2) {
                    return Boolean.valueOf(p2Equal(Value.eq, Suit.eq)
                            .eq(p(flush.highCard, flush.s), p(flush2.highCard, flush2.s)));
                }
            });
        }

        @Override public Boolean visit(final FullHouse fullHouse) {
            return this.h.accept(new AbstractVisitor<Boolean>(FALSE) {
                @Override public Boolean visit(final FullHouse fullHouse2) {
                    return Boolean.valueOf(v2Equal(Value.eq).eq(v(fullHouse.v1, fullHouse.v2), v(fullHouse2.v1, fullHouse2.v2)));
                }
            });
        }

        @Override public Boolean visit(final Pair pair) {
            return this.h.accept(new AbstractVisitor<Boolean>(FALSE) {
                @Override public Boolean visit(final Pair pair2) {
                    return Boolean.valueOf(Value.eq.eq(pair.v, pair2.v));
                }
            });
        }

        @Override public Boolean visit(final Quads quads) {
            return this.h.accept(new AbstractVisitor<Boolean>(FALSE) {
                @Override public Boolean visit(final Quads quads2) {
                    return Boolean.valueOf(Value.eq.eq(quads.v, quads2.v));
                }
            });
        }

        @Override public Boolean visit(final Straight straight) {
            return this.h.accept(new AbstractVisitor<Boolean>(FALSE) {
                @Override public Boolean visit(final Straight straight2) {
                    return Boolean.valueOf(Value.eq.eq(straight.v, straight2.v));
                }
            });
        }

        @Override public Boolean visit(final StraightFlush straightFlush) {
            return this.h.accept(new AbstractVisitor<Boolean>(FALSE) {
                @Override public Boolean visit(final StraightFlush straightFlush2) {
                    return Boolean.valueOf(p2Equal(Value.eq, Suit.eq).eq(p(straightFlush.highCard, straightFlush.s),
                            p(straightFlush2.highCard, straightFlush2.s)));
                }
            });
        }

        @Override public Boolean visit(final Trips trips) {
            return this.h.accept(new AbstractVisitor<Boolean>(FALSE) {
                @Override public Boolean visit(final Trips trips2) {
                    return Boolean.valueOf(Value.eq.eq(trips.v, trips2.v));
                }
            });
        }

        @Override public Boolean visit(final TwoPair twoPair) {
            return this.h.accept(new AbstractVisitor<Boolean>(FALSE) {
                @Override public Boolean visit(final TwoPair twoPair2) {
                    return Boolean.valueOf(v2Equal(Value.eq).eq(v(twoPair.v1, twoPair.v2), v(twoPair2.v1, twoPair2.v2)));
                }
            });
        }
    }

    private static final class Flush extends Hand {
        public final Value highCard;
        public final Suit s;

        Flush(final Value highCard, final Suit s) {
            this.highCard = highCard;
            this.s = s;
        }

        @Override <A> A accept(final Visitor<A> v) {
            return v.visit(this);
        }
    }

    private static final class FullHouse extends Hand {
        public final Value v1;
        public final Value v2;

        FullHouse(final Value v1, final Value v2) {
            this.v1 = v1;
            this.v2 = v2;
        }

        @Override <A> A accept(final Visitor<A> v) {
            return v.visit(this);
        }
    }

    private static final class Pair extends Hand {
        public final Value v;

        Pair(final Value v) {
            this.v = v;
        }

        @Override <A> A accept(final Visitor<A> v) {
            return v.visit(this);
        }
    }

    private static final class Quads extends Hand {
        public final Value v;

        Quads(final Value v) {
            this.v = v;
        }

        @Override <A> A accept(final Visitor<A> v) {
            return v.visit(this);
        }
    }

    private static final class ShowVisitor implements Visitor<String> {
        @Override public String visit(final Flush flush) {
            return "Flush in " + flush.s + " to the " + flush.highCard;
        }

        @Override public String visit(final FullHouse fullHouse) {
            return "Full house, " + fullHouse.v1 + "s over " + fullHouse.v2 + "s";
        }

        @Override public String visit(final Pair pair) {
            return "Pair of " + pair.v + "s";
        }

        @Override public String visit(final Quads quads) {
            return "Four " + quads.v + "s";
        }

        @Override public String visit(final Straight straight) {
            return "Straight to the " + straight.v;
        }

        @Override public String visit(final StraightFlush straightFlush) {
            return "Straight flush in " + straightFlush.s + " to the " + straightFlush.highCard;
        }

        @Override public String visit(final Trips trips) {
            return "Three " + trips.v + "s";
        }

        @Override public String visit(final TwoPair twoPair) {
            return "Two pair, " + twoPair.v1 + "s and " + twoPair.v2 + "s";
        }
    }

    private static final class Straight extends Hand {
        public final Value v;

        Straight(final Value v) {
            this.v = v;
        }

        @Override <A> A accept(final Visitor<A> v) {
            return v.visit(this);
        }
    }

    private static final class StraightFlush extends Hand {
        public final Value highCard;
        public final Suit s;

        StraightFlush(final Value highCard, final Suit s) {
            this.highCard = highCard;
            this.s = s;
        }

        @Override <A> A accept(final Visitor<A> v) {
            return v.visit(this);
        }
    }

    private static final class Trips extends Hand {
        public final Value v;

        Trips(final Value v) {
            this.v = v;
        }

        @Override <A> A accept(final Visitor<A> v) {
            return v.visit(this);
        }
    }

    private static final class TwoPair extends Hand {
        public final Value v1;
        public final Value v2;

        TwoPair(final Value v1, final Value v2) {
            this.v1 = v1;
            this.v2 = v2;
        }

        @Override <A> A accept(final Visitor<A> v) {
            return v.visit(this);
        }
    }
}
