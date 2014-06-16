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
package org.jboss.gwt.flux.sample.todo.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.gwt.flux.Action;
import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.Store;
import org.jboss.gwt.flux.sample.todo.client.views.QueueInfoView;

@ApplicationScoped
@SuppressWarnings("UnusedDeclaration")
public class TodoDispatcher implements Dispatcher {

    private boolean locked;
    private final Stack<Action> queue;
    private final Map<Enum, Store.Callback> callbacks;

    @Inject private QueueInfoView queueInfoView;

    public TodoDispatcher() {
        locked = false;
        queue = new Stack<>();
        callbacks = new HashMap<>();
    }

    @Override
    public <A extends Action, T extends Enum<T>> void register(final Store.Callback<A> callback, final T type,
            final T... types) {
        callbacks.put(type, callback);
        if (types != null && types.length != 0) {
            for (T t : types) {
                callbacks.put(t, callback);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Action> void dispatch(final A action) {
        System.out.println("~-~-~-~-~ Processing " + action);
        queueInfoView.refresh(queue.size());

        System.out.println("Queue size: " + queue.size());
        if (locked) {
            System.out.println("Dispatcher locked - push action to queue.");
            queue.add(action);
            queueInfoView.refresh(queue.size());
        } else {
            Store.Callback callback = callbacks.get(action.getType());
            if (callback != null) {
                locked = true;
                System.out.println("Dispatch action");
                callback.execute(action, new Context() {
                    @Override
                    public void yield() {
                        locked = false;
                        if (queue.size() > 0) {
                            dispatch(queue.pop());
                        }
                    }
                });
            } else {
                System.out.println("No callback found for action type " + action.getType());
            }
        }

        System.out.println("~-~-~-~-~ Exit\n");
    }
}
