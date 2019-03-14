// Created on Sep 10, 2018
package ca.foc.pokr;

import static ca.foc.pokr.Utils.findIndices;
import static ca.foc.pokr.Utils.findLastIndex;
import static fj.Equal.*;
import static fj.Function.constant;
import static fj.Ord.intOrd;
import static fj.data.List.range;
import static fj.data.Option.some;
import static fj.test.Arbitrary.*;
import static fj.test.Cogen.cogenInteger;
import static fj.test.Gen.choose;
import static fj.test.Property.prop;
import static fj.test.Property.property;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ca.foc.pokr.UtilsTest.FindIndicesTest;
import ca.foc.pokr.UtilsTest.FindLastIndexTest;
import fj.F;
import fj.data.List;
import fj.data.NonEmptyList;
import fj.test.Property;
import fj.test.runner.PropertyTestRunner;

@RunWith(Suite.class) @SuiteClasses({ FindIndicesTest.class, FindLastIndexTest.class }) public final class UtilsTest {
    @RunWith(PropertyTestRunner.class) static final class FindIndicesTest {
        static final Property alwaysFalseIsEmpty = property(arbList(arbInteger), as -> prop(checkAlwaysFalseIsEmpty(as)));
        static final Property alwaysTrue = property(arbList(arbInteger), as -> prop(checkAlwaysTrue(as)));
        static final Property boundedLength = property(arbList(arbInteger), arbF(cogenInteger, arbBoolean),
                as -> f -> prop(checkBoundedLength(as, f)));
        static final Property foundIndex = property(arbNonEmptyList(arbInteger), arbF(cogenInteger, arbBoolean),
                as -> f -> property(choose(0, as.length() - 1), i -> prop(checkFoundIndex(as, f, i))));
        static final Property isOrdered = property(arbList(arbInteger), arbF(cogenInteger, arbBoolean),
                as -> f -> prop(checkIsOrdered(as, f)));

        private static <A> boolean checkAlwaysFalseIsEmpty(final List<A> as) {
            return findIndices(constant(FALSE), as).isEmpty();
        }

        private static <A> boolean checkAlwaysTrue(final List<A> as) {
            return listEqual(intEqual).eq(range(0, as.length()), findIndices(constant(TRUE), as));
        }

        private static <A> boolean checkBoundedLength(final List<A> as, final F<A, Boolean> f) {
            return findIndices(f, as).length() <= as.length();
        }

        private static <A> boolean checkFoundIndex(final NonEmptyList<A> as, final F<A, Boolean> f, final Integer i) {
            final boolean shouldFind = f.f(as.toList().index(i.intValue())).booleanValue();
            final boolean found = findIndices(f, as.toList()).exists(intEqual.eq(i));
            return shouldFind == found;
        }

        private static <A> boolean checkIsOrdered(final List<A> as, final F<A, Boolean> f) {
            final List<Integer> indices = findIndices(f, as);
            return listEqual(intEqual).eq(indices, indices.sort(intOrd));
        }
    }

    @RunWith(PropertyTestRunner.class) static final class FindLastIndexTest {
        static final Property alwaysFalseIsNone = property(arbList(arbInteger), as -> prop(checkAlwaysFalseIsNone(as)));
        static final Property alwaysTrueIsMax = property(arbNonEmptyList(arbInteger), as -> prop(checkAlwaysTrueIsMax(as)));
        static final Property emptyListIsNone = property(arbF(cogenInteger, arbBoolean), f -> prop(checkEmptyListIsNone(f)));
        static final Property existsIsSome = property(arbList(arbInteger), arbF(cogenInteger, arbBoolean),
                as -> f -> prop(checkExistsIsSome(as, f)));
        static final Property index = property(arbNonEmptyList(arbInteger), arbF(cogenInteger, arbBoolean), as -> f -> property(
                choose(0, as.length() - 1), i -> prop(checkIndex(as, f, i))));
        static final Property max = property(arbList(arbInteger), arbF(cogenInteger, arbBoolean),
                as -> f -> prop(checkMax(as, f)));

        private static <A> boolean checkAlwaysFalseIsNone(final List<A> as) {
            return findLastIndex(constant(FALSE), as).isNone();
        }

        @SuppressWarnings("boxing") private static <A> boolean checkAlwaysTrueIsMax(final NonEmptyList<A> as) {
            return optionEqual(intEqual).eq(findLastIndex(constant(TRUE), as.toList()), some(as.length() - 1));
        }

        private static <A> boolean checkEmptyListIsNone(final F<A, Boolean> f) {
            return findLastIndex(f, List.nil()).isNone();
        }

        private static <A> boolean checkExistsIsSome(final List<A> as, final F<A, Boolean> f) {
            return as.exists(f) == findLastIndex(f, as).isSome();
        }

        @SuppressWarnings("boxing") private static <A> boolean checkIndex(final NonEmptyList<A> as, final F<A, Boolean> f,
                final Integer i) {
            final boolean b = f.f(as.toList().index(i)).booleanValue();
            return !b || findLastIndex(f, as.toList()).orSome(-1) >= i;
        }

        @SuppressWarnings("boxing") private static <A> boolean checkMax(final List<A> as, final F<A, Boolean> f) {
            return findLastIndex(f, as).orSome(-1) < as.length();
        }
    }
}
