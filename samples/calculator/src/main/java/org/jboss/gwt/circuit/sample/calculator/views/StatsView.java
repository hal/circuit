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
package org.jboss.gwt.circuit.sample.calculator.views;

import org.jboss.gwt.circuit.PropagatesChange;
import org.jboss.gwt.circuit.sample.calculator.CalculatorStore;
import org.jboss.gwt.circuit.sample.calculator.Term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.jboss.gwt.circuit.sample.calculator.Term.Op;

public class StatsView implements View {

    public StatsView(final CalculatorStore store) {
        store.addChangeHandler(new PropagatesChange.Handler() {
            @Override
            public void onChange(Class<?> source) {
                Map<Op, List<Term>> termsByOp = new HashMap<>();
                Set<Term> terms = store.getResults().keySet();
                for (Term term : terms) {
                    List<Term> termsOfOp = termsByOp.get(term.getOp());
                    if (termsOfOp == null) {
                        termsOfOp = new ArrayList<>();
                        termsByOp.put(term.getOp(), termsOfOp);
                    }
                    termsOfOp.add(term);
                }

                StringBuilder message = new StringBuilder();
                for (Iterator<Map.Entry<Op, List<Term>>> iterator = termsByOp.entrySet().iterator();
                     iterator.hasNext(); ) {
                    Map.Entry<Op, List<Term>> entry = iterator.next();
                    message.append(entry.getKey().name()).append("(").append(entry.getValue().size()).append(")");
                    if (iterator.hasNext()) {
                        message.append(", ");
                    }
                }
                System.out.printf("Operation stats:    %s\n", message);
            }
        });
    }
}
