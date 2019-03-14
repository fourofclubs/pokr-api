// Created on Sep 12, 2018
package ca.foc.pokr;

import static fj.Function.compose;
import static fj.Function.flip;
import static fj.P2.untuple;
import static fj.Show.*;
import static fj.data.Stream.range;
import static fj.function.Integers.subtract;
import fj.F;
import fj.Show;
import fj.data.List;

public final class Menu<B> {
    public final String prompt;
    private final List<String> items;
    private final F<Integer, B> select;

    private Menu(final String prompt, final List<String> itemsList, final F<Integer, B> select) {
        this.prompt = prompt;
        this.items = itemsList;
        this.select = select;
    }

    @SuppressWarnings("boxing") public static final <B, I> Menu<B> menu(final Show<I> itemShow, final String prompt,
            final List<I> options, final F<I, B> select) {
        return new Menu<B>(prompt, options.map(itemShow.showS_()), compose(select, compose(i -> options.index(i), flip(subtract)
                .f(1))));
    }

    public List<String> items() {
        return range(1).zipWith(this.items.toStream(), untuple(p2Show(intShow, stringShow, "", ":", "").showS_())).toList();
    }

    public B select(final Integer itemIndex) {
        return this.select.f(itemIndex);
    }
}
