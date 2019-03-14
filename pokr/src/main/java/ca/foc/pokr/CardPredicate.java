// Created on Sep 6, 2018
package ca.foc.pokr;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import ca.foc.pokr.Card.Suit;
import ca.foc.pokr.Card.Value;
import fj.Equal;
import fj.F;
import fj.Show;
import fj.function.Booleans;

abstract class CardPredicate implements F<Card, Boolean> {
    public static final Equal<CardPredicate> eq = Equal.<CardPredicate> equal(p1 -> p2 -> p1.accept(new EqualsVisitor(p2)));
    public static final Show<CardPredicate> show = Show.<CardPredicate> showS(p -> p.accept(new ShowVisitor()));

    public static CardPredicate and(final CardPredicate p1, final CardPredicate p2) {
        return new And(p1, p2);
    }

    public static CardPredicate isAnything() {
        return new IsAnything();
    }

    public static CardPredicate isAtMost(final Value v) {
        return new IsAtMost(v);
    }

    public static CardPredicate isCard(final Card c) {
        return new IsCard(c);
    }

    public static CardPredicate isNotValue(final Value v) {
        return new IsNotValue(v);
    }

    public static CardPredicate isSuit(final Suit s) {
        return new IsSuit(s);
    }

    public static CardPredicate isValue(final Value v) {
        return new IsValue(v);
    }

    public static CardPredicate or(final CardPredicate p1, final CardPredicate p2) {
        return new Or(p1, p2);
    }

    @Override public String toString() {
        return show.showS(this);
    }

    abstract <A> A accept(final Visitor<A> v);

    abstract static class AbstractVisitor<A> implements Visitor<A> {
        private final A defaultValue;

        AbstractVisitor(final A defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override public A visit(final And and) {
            return this.defaultValue;
        }

        @Override public A visit(final IsAnything isAnything) {
            return this.defaultValue;
        }

        @Override public A visit(final IsAtMost isAtMost) {
            return this.defaultValue;
        }

        @Override public A visit(final IsCard isCard) {
            return this.defaultValue;
        }

        @Override public A visit(final IsNotValue isValue) {
            return this.defaultValue;
        }

        @Override public A visit(final IsSuit isSuit) {
            return this.defaultValue;
        }

        @Override public A visit(final IsValue isValue) {
            return this.defaultValue;
        }

        @Override public A visit(final Or or) {
            return this.defaultValue;
        }
    }

    interface Visitor<A> {
        A visit(final And and);

        A visit(final IsAnything isAnything);

        A visit(final IsAtMost isAtMost);

        A visit(final IsCard isCard);

        A visit(final IsNotValue isNotValue);

        A visit(final IsSuit isSuit);

        A visit(final IsValue isValue);

        A visit(final Or or);
    }

    private static final class And extends CardPredicate {
        public final CardPredicate p1;
        public final CardPredicate p2;

        And(final CardPredicate p1, final CardPredicate p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        @Override public Boolean f(final Card c) {
            return Booleans.and(this.p1, this.p2).f(c);
        }

        @Override <A> A accept(final Visitor<A> v) {
            return v.visit(this);
        }
    }

    private static final class EqualsVisitor implements Visitor<Boolean> {
        private final CardPredicate pt;

        EqualsVisitor(final CardPredicate pt) {
            this.pt = pt;
        }

        @Override public Boolean visit(final And and) {
            return this.pt.accept(new AbstractVisitor<Boolean>(FALSE) {
                @Override public Boolean visit(final And and2) {
                    return Boolean.valueOf(CardPredicate.eq.eq(and.p1, and2.p1) && CardPredicate.eq.eq(and.p2, and2.p2));
                }
            });
        }

        @Override public Boolean visit(final IsAnything isAnything) {
            return this.pt.accept(new AbstractVisitor<Boolean>(FALSE) {
                @Override public Boolean visit(final IsAnything isAnything2) {
                    return TRUE;
                }
            });
        }

        @Override public Boolean visit(final IsAtMost isAtMost) {
            return this.pt.accept(new AbstractVisitor<Boolean>(FALSE) {
                @Override public Boolean visit(final IsAtMost isAtMost2) {
                    return Boolean.valueOf(Value.eq.eq(isAtMost.v, isAtMost2.v));
                }
            });
        }

        @Override public Boolean visit(final IsCard isCard) {
            return this.pt.accept(new AbstractVisitor<Boolean>(FALSE) {
                @Override public Boolean visit(final IsCard isCard2) {
                    return Boolean.valueOf(Card.eq.eq(isCard.c, isCard2.c));
                }
            });
        }

        @Override public Boolean visit(final IsNotValue isNotValue) {
            return this.pt.accept(new AbstractVisitor<Boolean>(FALSE) {
                @Override public Boolean visit(final IsNotValue isNotValue2) {
                    return Boolean.valueOf(Value.eq.eq(isNotValue.v, isNotValue2.v));
                }
            });
        }

        @Override public Boolean visit(final IsSuit isSuit) {
            return this.pt.accept(new AbstractVisitor<Boolean>(FALSE) {
                @Override public Boolean visit(final IsSuit isSuit) {
                    return Boolean.valueOf(Suit.eq.eq(isSuit.s, isSuit.s));
                }
            });
        }

        @Override public Boolean visit(final IsValue isValue) {
            return this.pt.accept(new AbstractVisitor<Boolean>(FALSE) {
                @Override public Boolean visit(final IsValue isValue2) {
                    return Boolean.valueOf(Value.eq.eq(isValue.v, isValue2.v));
                }
            });
        }

        @Override public Boolean visit(final Or or) {
            return this.pt.accept(new AbstractVisitor<Boolean>(FALSE) {
                @Override public Boolean visit(final Or or2) {
                    return Boolean.valueOf(CardPredicate.eq.eq(or.p1, or2.p1) && CardPredicate.eq.eq(or.p2, or2.p2));
                }
            });
        }
    }

    private static final class IsAnything extends CardPredicate {
        @Override public Boolean f(final Card c) {
            return TRUE;
        }

        @Override <A> A accept(final Visitor<A> v) {
            return v.visit(this);
        }
    }

    private static final class IsAtMost extends CardPredicate {
        public final Value v;

        IsAtMost(final Value v) {
            this.v = v;
        }

        @Override public Boolean f(final Card c) {
            return Boolean.valueOf(Value.ord.isLessThanOrEqualTo(c.value, this.v));
        }

        @Override <A> A accept(final Visitor<A> v) {
            return v.visit(this);
        }
    }

    private static final class IsCard extends CardPredicate {
        public final Card c;

        IsCard(final Card c) {
            this.c = c;
        }

        @Override public Boolean f(final Card c) {
            return Boolean.valueOf(Card.eq.eq(this.c, c));
        }

        @Override <A> A accept(final Visitor<A> v) {
            return v.visit(this);
        }
    }

    private static final class IsNotValue extends CardPredicate {
        public final Value v;

        IsNotValue(final Value v) {
            this.v = v;
        }

        @Override public Boolean f(final Card c) {
            return Boolean.valueOf(Value.eq.notEq(this.v, c.value));
        }

        @Override <A> A accept(final Visitor<A> v) {
            return v.visit(this);
        }
    }

    private static final class IsSuit extends CardPredicate {
        public final Suit s;

        IsSuit(final Suit s) {
            this.s = s;
        }

        @Override public Boolean f(final Card c) {
            return Boolean.valueOf(Suit.eq.eq(this.s, c.suit));
        }

        @Override <A> A accept(final Visitor<A> v) {
            return v.visit(this);
        }
    }

    private static final class IsValue extends CardPredicate {
        public final Value v;

        IsValue(final Value v) {
            this.v = v;
        }

        @Override public Boolean f(final Card c) {
            return Boolean.valueOf(Value.eq.eq(this.v, c.value));
        }

        @Override <A> A accept(final Visitor<A> v) {
            return v.visit(this);
        }
    }

    private static final class Or extends CardPredicate {
        public final CardPredicate p1;
        public final CardPredicate p2;

        Or(final CardPredicate p1, final CardPredicate p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        @Override public Boolean f(final Card c) {
            return Booleans.or(this.p1, this.p2).f(c);
        }

        @Override <A> A accept(final Visitor<A> v) {
            return v.visit(this);
        }
    }

    private static final class ShowVisitor implements Visitor<String> {
        @Override public String visit(final And and) {
            return "(" + and.p1.accept(this) + " and " + and.p2.accept(this) + ")";
        }

        @Override public String visit(final IsAnything isAnything) {
            return "anything";
        }

        @Override public String visit(final IsAtMost isAtMost) {
            return "is at most a " + Value.show.showS(isAtMost.v);
        }

        @Override public String visit(final IsCard isCard) {
            return "is the " + Card.show.showS(isCard.c);
        }

        @Override public String visit(final IsNotValue isNotValue) {
            return "is not a " + Value.show.showS(isNotValue.v);
        }

        @Override public String visit(final IsSuit isSuit) {
            return "is " + Suit.show.showS(isSuit.s);
        }

        @Override public String visit(final IsValue isValue) {
            return "is a " + Value.show.showS(isValue.v);
        }

        @Override public String visit(final Or or) {
            return "(" + or.p1.accept(this) + " or " + or.p2.accept(this) + ")";
        }
    }
}
