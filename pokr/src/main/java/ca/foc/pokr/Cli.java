// Created on Sep 6, 2018
package ca.foc.pokr;

import static ca.foc.pokr.Deals.playerDeals;
import static ca.foc.pokr.Menu.menu;
import static fj.F1Functions.mapOption;
import static fj.Show.intShow;
import static fj.Show.p2MapShow;
import static fj.data.Either.either_;
import static fj.data.IOFunctions.*;
import static fj.data.IOW.lift;
import static fj.data.Option.option_;
import static fj.data.Option.parseInt;

import java.io.IOException;

import fj.P;
import fj.Unit;
import fj.data.IO;
import fj.data.IOFunctions;
import fj.data.IOW;
import fj.data.Option;
import fj.data.TreeMap;

public final class Cli {
    public static void main(final String[] args) throws IOException {
        final Deck d = Deck.micholsonStack();

        //@formatter:off
        IOW.lift(buildHand(HandBuilder.initialBuilder()))
            .bind(h ->
                selectItem(playersMenu(playerDeals(h, d)), stdinReadInt())
                    .map(mapOption(P.<Hand, Deal>p2().f(h)))
                    .map(option_("X", p2MapShow(Hand.show, Deal.show).showS_())))
            .bind(IOFunctions::stdoutPrintln).run();
        //@formatter:on
    }

    private static final IO<Hand> buildHand(final HandBuilder builder) {
        return bind(selectItem(builder.menu, stdinReadInt()), either_(Cli::buildHand, IOFunctions::<Hand> unit));
    }

    private static final Menu<Option<Deal>> playersMenu(final TreeMap<Integer, Deal> deals) {
        return menu(intShow, "Number of players?", deals.keys(), deals.get());
    }

    private static final <A> IOW<A> selectItem(final Menu<A> m, final IO<Integer> readInt) {
        final IOW<Unit> printPrompt = lift(stdoutPrintln(m.prompt));
        final IO<Unit> printOptions = voided(m.items().traverseIO(IOFunctions::stdoutPrintln));
        final IO<A> select = map(readInt, m::select);
        return printPrompt.append(printOptions).append(select);
    }

    private static final IO<Integer> stdinReadInt() {
        return bind(stdinReadLine(), s -> parseInt.f(s).map(IOFunctions::unit).orSome(
                () -> append(stdoutPrintln("Enter a number."), stdinReadInt())));
    }
}
