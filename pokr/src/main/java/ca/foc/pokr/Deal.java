// Created on Sep 7, 2018
package ca.foc.pokr;

import static fj.Equal.*;
import static fj.P.p;
import static fj.Show.*;
import fj.Equal;
import fj.Show;
import fj.data.List;

@SuppressWarnings("javadoc") public final class Deal {
    public static final Equal<Deal> eq = p2Equal(intEqual, listEqual(intEqual)).contramap(d -> p(d.cutAt, d.seconds));
    @SuppressWarnings("boxing") public static final Show<Deal> show = Show.<Deal> showS(d -> {
        final List<String> formattedSeconds =
                d.seconds.map(i -> intEqual.eq(i, 10) ? "A" : intEqual.eq(i, -1) ? "B" : intShow.showS(i));
        return intShow.showS(d.cutAt) + "." + streamShow(stringShow, "", "", "").showS(formattedSeconds.toStream());
    });
    public final Integer cutAt;
    public final List<Integer> seconds;

    private Deal(final Integer cutAt, final List<Integer> seconds) {
        this.cutAt = cutAt;
        this.seconds = seconds;
    }

    public static final Deal deal(final int cutAt, final List<Integer> seconds) {
        return new Deal(Integer.valueOf(cutAt), seconds);
    }

    public static final Deal deal(final Integer cutAt, final List<Integer> seconds) {
        return new Deal(cutAt, seconds);
    }

    @Override public String toString() {
        return show.showS(this);
    }
}
