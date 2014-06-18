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
package org.jboss.gwt.flux.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.jboss.gwt.flux.Action;
import org.jboss.gwt.flux.Agreement;
import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.Store;

/**
 * @author Harald Pehl
 */
public class DefaulDispatcherImpl implements Dispatcher {

    private boolean locked;
    private final Stack<Action> queue;
    private final Map<Class<? extends Store>, Store.Callback> callbacks;

    public DefaulDispatcherImpl() {
        locked = false;
        queue = new Stack<>();
        callbacks = new HashMap<>();
    }

    @Override
    public <S extends Store> void register(final Class<S> store, final Store.Callback callback) {
        assert callbacks.get(store) == null : "Store " + store.getName() + " already registered!";
        callbacks.put(store, callback);
    }

    @Override
    public void dispatch(final Action action) {
        // 1. collect agreements
        Map<Class<? extends Store>, Agreement> agreements = prepare(action);

        // 2. complete callbacks
        complete(action, agreements);
    }

    private Map<Class<? extends Store>, Agreement> prepare(final Action action) {
        Map<Class<? extends Store>, Agreement> approvals = new HashMap<>();
        for (Map.Entry<Class<? extends Store>, Store.Callback> entry : callbacks.entrySet()) {
            Class<? extends Store> storeLiteral = entry.getKey();
            Store.Callback callback = entry.getValue();

            Agreement agreement = callback.voteFor(action);
            if (agreement.isApproved()) {
                approvals.put(storeLiteral, agreement);
            }
        }
        return approvals;
    }

    private void complete(final Action action, final Map<Class<? extends Store>, Agreement> approvals) {
        if (locked) {
            queue.add(action);
        } else {
            locked = true;
            for (Class<? extends Store> store : approvals.keySet()) {
                Store.Callback callback = callbacks.get(store);
                callback.execute(action, new Channel() {
                    @Override
                    public void ack() {
                        proceed();
                    }

                    @Override
                    public void nack(final Throwable t) {
                        proceed();
                    }

                    private void proceed() {
                        locked = false;
                        if (queue.size() > 0) {
                            dispatch(queue.pop());
                        }
                    }
                });
            }
        }
    }
}
