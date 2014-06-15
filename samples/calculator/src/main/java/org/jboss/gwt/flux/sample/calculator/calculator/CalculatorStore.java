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
package org.jboss.gwt.flux.sample.calculator.calculator;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.gwt.flux.AbstractStore;
import org.jboss.gwt.flux.Action;
import org.jboss.gwt.flux.Dispatcher;

public class CalculatorStore extends AbstractStore {

    private final Map<Term, Integer> results;

    public CalculatorStore(final Dispatcher dispatcher) {
        this.results = new LinkedHashMap<>();

        dispatcher.register((Action<Term> action) -> {
            if (canProcess(action)) {
                // TODO How to handle (arithmetic) exceptions like 1 / 0
                results.put(action.getPayload(), calculate(action.getPayload()));
                fireChanged();
                return true;
            }
            return false;
        });
    }

    @Override
    public <T> boolean canProcess(final Action<T> action) {
        return action instanceof TermAction;
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
