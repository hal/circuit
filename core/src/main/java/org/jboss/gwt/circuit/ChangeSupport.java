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

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

public abstract class ChangeSupport implements PropagatesChange {

    private final static Class<Void> ANY_ACTION = Void.TYPE;

    private Multimap<Class<?>, Handler> handler = LinkedListMultimap.create();

    @Override
    public void addChangedHandler(final Handler handler) {
        this.handler.put(ANY_ACTION, handler);
    }

    @Override
    public void addChangedHandler(final Class<?> actionType, final Handler handler) {
        this.handler.put(actionType, handler);
    }

    protected void fireChange(Action action) {
        Class<? extends Action> actionType = action.getClass();
        Iterable<Handler> actionHandlers = getActionHandler(action);
        if (actionHandlers.iterator().hasNext()) {
            for (Handler actionHandler : actionHandlers) {
                actionHandler.onChanged(actionType);
            }
        } else {
            Iterable<Handler> storeHandlers = getHandler();
            for (Handler storeHandler : storeHandlers) {
                storeHandler.onChanged(actionType);
            }
        }
    }

    public Iterable<Handler> getActionHandler(Action action) {
        Class<? extends Action> actionType = action.getClass();
        return handler.get(actionType); // returns an empty list if no handler were registered for this action
    }

    public Iterable<Handler> getHandler() {
        return handler.get(ANY_ACTION);
    }
}
