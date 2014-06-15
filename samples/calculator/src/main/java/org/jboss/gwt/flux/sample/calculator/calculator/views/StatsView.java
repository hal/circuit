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
package org.jboss.gwt.flux.sample.calculator.calculator.views;

import static org.jboss.gwt.flux.sample.calculator.calculator.Term.Op;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.gwt.flux.sample.calculator.calculator.CalculatorStore;
import org.jboss.gwt.flux.sample.calculator.calculator.Term;

public class StatsView implements View {

    public StatsView(final CalculatorStore store) {
        store.addChangedHandler(event -> {
            Set<Term> terms = store.getResults().keySet();
            Map<Op, List<Term>> termsByOp = terms.stream().collect(Collectors.groupingBy(Term::getOp));
            String message = termsByOp.entrySet().stream()
                    .map(entry -> entry.getKey().name() + "(" + entry.getValue().size() + ")")
                    .collect(Collectors.joining(", "));
            System.out.printf("Operation stats:    %s\n", message);
        });
    }
}
