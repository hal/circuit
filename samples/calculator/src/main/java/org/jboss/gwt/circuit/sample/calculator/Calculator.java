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

import java.util.Random;

import org.jboss.gwt.circuit.ChangeManagement;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.sample.calculator.views.InputView;
import org.jboss.gwt.circuit.sample.calculator.views.StatsView;
import org.jboss.gwt.circuit.sample.calculator.views.TermsView;

public class Calculator {

    public static void main(String[] args) {
        int numberOfActions = 5 + new Random().nextInt(5);
        System.out.printf("~=~=~=~=~ Dispatching %d actions\n\n", numberOfActions);
        new Calculator(numberOfActions).run();
        System.out.printf("~=~=~=~=~ Finished with %d actions\n", numberOfActions);
    }

    private final int numberOfActions;
    private final ChangeManagement changeManagement;
    private final Dispatcher dispatcher;
    private final CalculatorStore store;

    public Calculator(final int numberOfActions) {
        this.numberOfActions = numberOfActions;
        this.changeManagement = new ChangeManagement();
        this.dispatcher = new SequentialDispatcher();
        this.store = new CalculatorStore(changeManagement, dispatcher);
    }

    public void run() {
        new StatsView(store, changeManagement);
        new TermsView(store, changeManagement);
        new InputView(dispatcher, numberOfActions).dispatch();
    }
}
