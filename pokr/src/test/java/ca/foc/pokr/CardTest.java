// Created on Sep 5, 2018
package ca.foc.pokr;

import static fj.test.Property.prop;

import org.junit.runner.RunWith;

import fj.test.Property;
import fj.test.runner.PropertyTestRunner;

@RunWith(PropertyTestRunner.class) public final class CardTest {
    public static final Property p1 = prop(Card.all.length() == 52);
}
