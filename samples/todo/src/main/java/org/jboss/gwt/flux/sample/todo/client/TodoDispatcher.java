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

import org.jboss.gwt.flux.Action;
import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.Store;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

public class TodoDispatcher implements Dispatcher {

    private final Map<Enum[],Store.Callback> callbacks;
    private Stack<Action> queue;

    private boolean locked;

    public TodoDispatcher() {
        this.callbacks = new HashMap<>();
        this.queue = new Stack<>();
    }

    @Override
    public <P> void register(Store.Callback callback) {

        callbacks.put(callback.getTypes(), callback);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P> void dispatch(final Action action) {

        System.out.println(">> Queue size "+ queue.size());

        if(locked)
        {
            queue.add(action);
            return;
        }

        Iterator<Enum[]> it = callbacks.keySet().iterator();
        boolean matched = false;

        while(it.hasNext())
        {
            Enum[] actionTypes = it.next();
            for(Enum e : actionTypes)
            {
                if(action.getType().equals(e))
                {
                    locked = true;

                    callbacks.get(actionTypes).execute(action, new Context() {
                        @Override
                        public void yield() {
                            locked = false;
                            if(queue.size()>0)
                            {
                                dispatch(queue.pop());
                            }
                        }
                    });
                    matched = true;
                    break;
                }
            }

            if(matched) break;
        }
    }
}
