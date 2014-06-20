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
package org.jboss.gwt.flux.dag;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.jboss.gwt.flux.Action;
import org.jboss.gwt.flux.Agreement;
import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.Store;
import org.jboss.gwt.flux.impl.NoopLifecycle;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

/**
 * A dispatcher implementation with global locking and dependency resolution between stores based on directed acyclic
 * graphs (DAG).
 *
 * @see <a href="http://en.wikipedia.org/wiki/Directed_acyclic_graph">http://en.wikipedia.org/wiki/Directed_acyclic_graph</a>
 * @see <a href="http://en.wikipedia.org/wiki/Topological_ordering">http://en.wikipedia.org/wiki/Topological_ordering</a>
 */
public class DAGDispatcher implements Dispatcher {

    private boolean locked;
    private final Stack<Action> queue;
    private final Map<Class<? extends Store>, Store.Callback> callbacks;
    private Lifecycle lifecycle;

    public DAGDispatcher() {
        locked = false;
        queue = new Stack<>();
        callbacks = new HashMap<>();
        lifecycle = new NoopLifecycle();
    }

    @Override
    public <S extends Store> void register(final Class<S> store, final Store.Callback callback) {
        assert callbacks.get(store) == null : "Store " + store.getName() + " already registered!";
        callbacks.put(store, callback);
    }

    @Override
    public void dispatch(final Action action) {
        lifecycle.onDispatch(action);

        if (!locked) {
            // lock globally
            locked = true;

            // collect agreements
            Map<Class<? extends Store>, Agreement> agreements = prepare(action);

            // complete callbacks
            complete(action, agreements);
        } else {
            queue.push(action);
        }
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
        DirectedGraph<Class<? extends Store>, DefaultEdge> dag = createDag(approvals);
        TopologicalOrderIterator<Class<? extends Store>, DefaultEdge> iterator = new TopologicalOrderIterator<>(
                dag);
        executeInOrder(action, iterator);
    }

    private DirectedGraph<Class<? extends Store>, DefaultEdge> createDag(
            final Map<Class<? extends Store>, Agreement> approvals) {

        DirectedGraph<Class<? extends Store>, DefaultEdge> dag = new DefaultDirectedGraph<>(DefaultEdge.class);

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
        return dag;
    }

    private void executeInOrder(final Action action,
            final TopologicalOrderIterator<Class<? extends Store>, DefaultEdge> iterator) {

        if (!iterator.hasNext()) {
            locked = false;
            if (!queue.isEmpty()) {
                dispatch(queue.pop());
            }
            return;
        }

        final Class<? extends Store> store = iterator.next();
        Store.Callback callback = callbacks.get(store);
        lifecycle.onExecute(store, action);
        callback.execute(action, new Channel() {
            @Override
            public void ack() {
                lifecycle.onAck(store, action);
                proceed();
            }

            @Override
            public void nack(final Throwable t) {
                lifecycle.onNack(store, action, t);
                proceed();
            }

            private void proceed() {
                executeInOrder(action, iterator);
            }
        });
    }

    @Override
    public void registerLifecycle(final Lifecycle lifecycle) {
        assert lifecycle != null : "Lifecylce must not be null!";
        this.lifecycle = lifecycle;
    }
}

