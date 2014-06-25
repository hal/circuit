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
package org.jboss.gwt.circuit.sample.calculator;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.gwt.circuit.AbstractStore;
import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.Agreement;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.Store;

public class CalculatorStore extends AbstractStore {

    private final Map<Term, Integer> results;

    public CalculatorStore(final Dispatcher dispatcher) {
        this.results = new LinkedHashMap<>();

        dispatcher.register(CalculatorStore.class, new Store.Callback() {
            @Override
            public Agreement voteFor(final Action action) {
                if (action instanceof TermAction) {
                    return new Agreement(true);
                }
                return Agreement.NONE;
            }

            @Override
            public void complete(final Action action, final Dispatcher.Channel channel) {
                Term term = (Term) action.getPayload();
                results.put(term, calculate(term));
                channel.ack();
                fireChanged(CalculatorStore.class);
            }
        });
    }

    private int calculate(final Term term) {
        switch (term.getOp()) {
            case PLUS:
                return term.getLeft() + term.getRight();
            case MINUS:
                return term.getLeft() - term.getRight();
            case MULTIPLY:
                return term.getLeft() * term.getRight();
            case DIVIDE:
                return term.getLeft() / term.getRight();
        }
        return 0;
    }

    public Map<Term, Integer> getResults() {
        return Collections.unmodifiableMap(results);
    }
}
