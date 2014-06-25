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
package org.jboss.gwt.circuit.test;

import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.Agreement;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.dag.BoundedQueue;
import org.jboss.gwt.circuit.dag.CycleDetected;
import org.jboss.gwt.circuit.dag.DAGDispatcher;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Harald Pehl
 * @author Heiko Braun
 */
public class DispatcherTest {

    private Dispatcher dispatcher;
    private TestDiagnostics diag;

    @Before
    public void setUp() {
        dispatcher = new DAGDispatcher();
        diag = new TestDiagnostics();
        dispatcher.addDiagnostics(diag);
    }

    @Test
    public void noStoresRegistered() {

        dispatcher.dispatch(new FooBarAction(1));
        assertFalse("Dispatcher should not remain locked if no stores are registered", diag.isLocked());
    }

    @Test
    public void dependencies() {
        FooStore foo = new FooStore(dispatcher) {
            @Override
            protected Agreement vote(Action action) {
                return new Agreement(true, BarStore.class);  // declare dependency
            }
        };

        BarStore bar = new BarStore(dispatcher);

        dispatcher.dispatch(new FooBarAction(0));

        assertTrue("Both stores should have processed the action", diag.getNumExecuted()==2);

        assertTrue("BarStore should be first", diag.getExecutionOrder().get(0) == BarStore.class);

        assertTrue("FooStore should be second", diag.getExecutionOrder().get(1) == FooStore.class);
    }

    @Test(expected = CycleDetected.class)
    public void dependencyCycles() {
        FooStore foo = new FooStore(dispatcher) {

            @Override
            protected Agreement vote(Action action) {
                return new Agreement(true, BarStore.class);
            }
        };

        BarStore bar = new BarStore(dispatcher) {
            @Override
            protected Agreement vote(Action action) {
                return new Agreement(true, FooStore.class);  // declare cyclic dependency
            }
        };

        dispatcher.dispatch(new FooBarAction(0));
    }

    @Test
    public void boundedQueue() {
        BoundedQueue<Integer> queue = new BoundedQueue<>(3);
        queue.offer(1);
        queue.offer(2);
        queue.offer(3);
        boolean enqueued = queue.offer(4);

        assertEquals(3, queue.size());
        assertFalse("fourth item should not have been enqueued", enqueued);

        assertTrue(1==queue.poll());
        assertTrue(2==queue.poll());
        assertTrue(3==queue.poll());

        assertTrue("queue should be empty", queue.isEmpty());
        assertTrue(queue.poll() == null);
    }

}