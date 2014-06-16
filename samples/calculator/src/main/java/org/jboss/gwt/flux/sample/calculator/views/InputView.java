/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.gwt.flux.sample.calculator.views;

import static org.jboss.gwt.flux.sample.calculator.Term.Op;

import java.util.Random;
import java.util.stream.Stream;

import org.jboss.gwt.flux.Action;
import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.sample.calculator.NoopAction;
import org.jboss.gwt.flux.sample.calculator.Term;
import org.jboss.gwt.flux.sample.calculator.TermAction;

public class InputView implements View {

    private final int numberOfActions;
    private final Dispatcher dispatcher;
    private final Stream<Action> actionStream;

    public InputView(final Dispatcher dispatcher, int numberOfActions) {
        this.dispatcher = dispatcher;
        this.numberOfActions = numberOfActions;
        this.actionStream = Stream.generate(() -> {
            Random random = new Random();
            Op op = Op.values()[random.nextInt(Op.values().length)];
            boolean noop = 1 + random.nextInt(11) % 4 == 4;
            return noop ? new NoopAction() : new TermAction(
                    new Term(1 + random.nextInt(10), op, 1 + random.nextInt(10)));
        });
    }

    public void dispatch() {
        // Simulate user input
        actionStream.limit(numberOfActions).forEach(dispatcher::dispatch);
    }
}
