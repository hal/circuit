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

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.PropagatesChange;
import org.jboss.gwt.circuit.sample.calculator.CalculatorStore;
import org.jboss.gwt.circuit.sample.calculator.Term;

import java.util.Iterator;
import java.util.Set;

import static org.jboss.gwt.circuit.sample.calculator.Term.Op;

public class StatsView implements View {

    public StatsView(final CalculatorStore store) {
        store.addChangeHandler(new PropagatesChange.Handler() {
            @Override
            public void onChange(final Action action) {
                Multimap<Op, Term> termsByOp = LinkedListMultimap.create();
                Set<Term> terms = store.getResults().keySet();
                for (Term term : terms) {
                    termsByOp.put(term.getOp(), term);
                }

                StringBuilder message = new StringBuilder();
                Set<Op> keys = termsByOp.keySet();
                for (Iterator<Op> iterator = keys.iterator(); iterator.hasNext(); ) {
                    Op key = iterator.next();
                    message.append(key.name()).append("(").append(termsByOp.get(key).size()).append(")");
                    if (iterator.hasNext()) {
                        message.append(", ");
                    }
                }
                System.out.printf("Operation stats:    %s\n", message);
            }
        });
    }
}
