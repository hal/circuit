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
package org.jboss.gwt.circuit.dag;

import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.Agreement;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.Store;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * A dispatcher implementation with global locking and dependency resolution between stores based on directed acyclic
 * graphs (DAG).
 *
 * @see <a href="http://en.wikipedia.org/wiki/Directed_acyclic_graph">http://en.wikipedia.org/wiki/Directed_acyclic_graph</a>
 * @see <a href="http://en.wikipedia.org/wiki/Topological_ordering">http://en.wikipedia.org/wiki/Topological_ordering</a>
 */
public class DAGDispatcher implements Dispatcher {

    public interface Diagnostics extends Dispatcher.Diagnostics {

        void onDispatch(Action a);

        void onLock();

        void onExecute(Class<?> s, Action a);

        void onAck(Class<?> s, Action a);

        void onNack(Class<?> s, Action a, final Throwable t);

        void onUnlock();
    }

    public final static int BOUNDS_SIZE = 50;

    private boolean locked;
    private final Queue<Action> queue;
    private final Map<Class<? extends Store>, Store.Callback> callbacks;
    private final DelegatingDiag diag;

    public DAGDispatcher() {
        locked = false;
        queue = new BoundedQueue<Action>(BOUNDS_SIZE);
        callbacks = new HashMap<>();
        diag = new DelegatingDiag();
    }

    @Override
    public <S extends Store> void register(final Class<S> store, final Store.Callback callback) {
        assert callbacks.get(store) == null : "Store " + store.getName() + " already registered!";
        callbacks.put(store, callback);
    }

    @Override
    public void dispatch(final Action action) {
        diag.onDispatch(action);

        if (!locked) {
            // lock globally
            lock();

            // collect approvals
            Map<Class<? extends Store>, Agreement> approvals = prepare(action);

            // complete callbacks
            if(approvals.isEmpty()) {
                unlock();
            }
            else {
                complete(action, approvals);
            }
        } else {
            queue.offer(action);
        }
    }

    private void lock() {
        diag.onLock();
        locked = true;
    }

    private void unlock() {
        diag.onUnlock();
        locked = false;
    }

    private Map<Class<? extends Store>, Agreement> prepare(final Action action) {
        Map<Class<? extends Store>, Agreement> approvals = new HashMap<>();
        for (Map.Entry<Class<? extends Store>, Store.Callback> entry : callbacks.entrySet()) {
            Class<? extends Store> store = entry.getKey();
            Store.Callback callback = entry.getValue();

            Agreement agreement = callback.voteFor(action);
            if (agreement.isApproved()) {
                approvals.put(store, agreement);
            }
        }
        return approvals;
    }

    private void complete(final Action action, final Map<Class<? extends Store>, Agreement> approvals) {

        DirectedGraph<Class<?>, DefaultEdge> dag = createDag(approvals);
        // TODO Cache topological order
        TopologicalOrderIterator<Class<?>, DefaultEdge> iterator = new TopologicalOrderIterator<>(dag);

        executeInOrder(action, iterator);
    }

    private DirectedGraph<Class<?>, DefaultEdge> createDag(
            final Map<Class<? extends Store>, Agreement> approvals
    ) {

        DirectedGraph<Class<?>, DefaultEdge> dag = new DefaultDirectedGraph<>(new EdgeFactoryImpl());

        // Add vertices (stores)
        for (Map.Entry<Class<? extends Store>, Agreement> entry : approvals.entrySet()) {
            Class<? extends Store> store = entry.getKey();
            Agreement agreement = entry.getValue();
            dag.addVertex(store);
            for (Class<? extends Store> depStore : agreement.getDependencies()) {
                dag.addVertex(depStore);
            }
        }

        // Add edges (dependencies from one store to other stores)
        for (Map.Entry<Class<? extends Store>, Agreement> entry : approvals.entrySet()) {
            Class<? extends Store> store = entry.getKey();
            Agreement agreement = entry.getValue();
            for (Class<? extends Store> depStore : agreement.getDependencies()) {
                dag.addEdge(depStore, store);
            }
        }

        // cycle detection
        /*CycleDetector<Class<?>, DefaultEdge> cycleDetection = new CycleDetector<>(dag);
        Set<Class<?>> cycles = cycleDetection.findCycles();
        if(cycles.size()>0)
        {
            StringBuffer sb = new StringBuffer();
            int i=1;
            for(Class<?> store : cycles)
            {
                sb.append(store.getName());
                if(i<cycles.size())
                    sb.append(" > ");
                i++;
            }

            throw new CycleDetected(sb.toString());
        }         */

        return dag;
    }

    private void executeInOrder(final Action action, final TopologicalOrderIterator<Class<?>, DefaultEdge> iterator) {

        if (!iterator.hasNext()) {
            unlock();
            if (!queue.isEmpty()) {
                dispatch(queue.poll());
            }
            return;
        }

        final Class<?> store = iterator.next();
        Store.Callback callback = callbacks.get(store);
        diag.onExecute(store, action);
        callback.execute(action, new Channel() {
            @Override
            public void ack() {
                diag.onAck(store, action);
                proceed();
            }

            @Override
            public void nack(final Throwable t) {
                diag.onNack(store, action, t);
                proceed();
            }

            private void proceed() {
                executeInOrder(action, iterator);
            }
        });
    }

    @Override
    public void addDiagnostics(final Dispatcher.Diagnostics d) {
        this.diag.add(d);
    }

    @Override
    public void removeDiagnostics(Dispatcher.Diagnostics d) {
        this.diag.remove(d);
    }
}

