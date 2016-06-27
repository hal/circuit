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
package org.jboss.gwt.circuit;

import org.jboss.gwt.circuit.dag.CycleDetected;
import org.jboss.gwt.circuit.dag.DAGDispatcher;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Harald Pehl
 * @author Heiko Braun
 */
public class DispatcherTest {

    private Dispatcher dispatcher;
    private TestDiagnostics diagnostics;

    @Before
    public void setUp() {
        dispatcher = new DAGDispatcher();
        diagnostics = new TestDiagnostics();
        dispatcher.addDiagnostics(diagnostics);
    }

    @Test
    public void noStoresRegistered() {
        dispatcher.dispatch(new FooBarAction(1));
        assertFalse("Dispatcher should not remain locked if no stores are registered", diagnostics.isLocked());
    }

    @Test
    public void dependencies() {
        new FooStore(dispatcher) {
            @Override
            protected Agreement vote(Action action) {
                return new Agreement(true, BarStore.class);  // declare dependency
            }
        };
        new BarStore(dispatcher);
        dispatcher.dispatch(new FooBarAction(0));

        assertTrue("Both stores should have processed the action", diagnostics.getNumExecuted() == 2);
        assertTrue("BarStore should be first", diagnostics.getExecutionOrder().get(0) == BarStore.class);
        assertTrue("FooStore should be second", diagnostics.getExecutionOrder().get(1) == FooStore.class);
    }

    @Test(expected = CycleDetected.class)
    public void dependencyCycles() {
        new FooStore(dispatcher) {
            @Override
            protected Agreement vote(Action action) {
                return new Agreement(true, BarStore.class);
            }
        };
        new BarStore(dispatcher) {
            @Override
            protected Agreement vote(Action action) {
                return new Agreement(true, FooStore.class);  // declare cyclic dependency
            }
        };
        dispatcher.dispatch(new FooBarAction(0));
    }

    @Test
    public void changedEvents() {
        final List<Class<?>> stores = new ArrayList<>();

        FooStore fooStore = new FooStore(dispatcher);
        fooStore.addChangeHandler(new PropagatesChange.ChangeHandler() {
            @Override
            public void onChange(final Action action) {
                stores.add(FooStore.class);
            }
        });
        BarStore barStore = new BarStore(dispatcher) {
            @Override
            protected Agreement vote(Action action) {
                return new Agreement(true, FooStore.class);
            }
        };
        barStore.addChangeHandler(new PropagatesChange.ChangeHandler() {
            @Override
            public void onChange(final Action action) {
                stores.add(BarStore.class);
            }
        });
        dispatcher.dispatch(new FooBarAction(0));

        assertEquals(2, stores.size());
        assertEquals(FooStore.class, stores.get(0));
        assertEquals(BarStore.class, stores.get(1));
    }

    @Test
    public void actionChangedEvents() throws InterruptedException {
        final List<Class<?>> stores = new ArrayList<>();
        final List<Class<? extends Action>> actions = new ArrayList<>();

        FooStore fooStore = new FooStore(dispatcher);
        fooStore.addChangeHandler(new PropagatesChange.ChangeHandler() {
            @Override
            public void onChange(final Action action) {
                stores.add(FooStore.class);
            }
        });
        BarStore barStore = new BarStore(dispatcher);
        barStore.addChangeHandler(FooBarAction.class, new PropagatesChange.ChangeHandler() {
            @Override
            public void onChange(final Action action) {
                stores.add(BarStore.class);
                actions.add(action.getClass());
            }
        });
        dispatcher.dispatch(new FooBarAction(0));

        // FooBarAction is processed both by the FooStore and BarStore
        assertEquals(2, stores.size());
        assertTrue(stores.contains(BarStore.class));
        assertTrue(stores.contains(FooStore.class));

        // The change errorHandler for FooBarAction must be called exactly once
        assertEquals(1, actions.size());
        assertEquals(FooBarAction.class, actions.get(0));
    }
}