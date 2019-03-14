// Created on Sep 6, 2018
package ca.foc.pokr;

import static ca.foc.pokr.Deals.playerDeals;
import static fj.Function.compose;
import static fj.Show.intShow;
import static fj.data.IOFunctions.*;

import java.io.IOException;

import fj.F;
import fj.Show;
import fj.Unit;
import fj.data.*;

@SuppressWarnings("javadoc") public final class Cli2 {
    public static void main(final String[] args) throws IOException {
        final Deck d = Deck.micholsonStack();
        stdinReadHand().map(h -> playerDeals(h, d)).bind(m -> selectFrom(m, intShow, "Players?", Option.parseInt)).map(
                Deal.show.showS_()).bind(IOFunctions::stdoutPrintln).run();
    }

    private static final <A, B> IOW<B> selectFrom(final TreeMap<A, B> m, final Show<A> keyShow, final String prompt,
            final F<String, Option<A>> readA) {
        final F<String, Option<B>> readB = compose(Option.<A, B> bind().f(m.get()), readA);
        final IO<Unit> printOptions = voided(m.keys().traverseIO(compose(IOFunctions::stdoutPrintln, keyShow.showS_())));
        return IOW.lift(printOptions).append(stdinReadA(prompt, "Invalid option.", readB));
    }

    private static final <A> IOW<A> stdinReadA(final String prompt, final String error, final F<String, Option<A>> readA) {
        return IOW.lift(stdoutPrintln(prompt)).append(stdinReadLine()).map(readA).bind(
                aOpt -> aOpt.map(IOFunctions::<A> unit).orSome(
                        append(stdoutPrintln("Invalid hand."), stdinReadA(prompt, error, readA))));
    }

    private static final IOW<Hand> stdinReadHand() {
        return stdinReadA("Hand?", "Could not parse hand.", Hand::readHand);
    }
}
