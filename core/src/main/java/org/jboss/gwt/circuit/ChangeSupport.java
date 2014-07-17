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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.web.bindery.event.shared.HandlerRegistration;

public abstract class ChangeSupport implements PropagatesChange {

    static class HandlerRef {

        private final String id;
        final Handler handler;

        HandlerRef(final Handler handler) {
            this.id = UUID.uuid();
            this.handler = handler;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) { return true; }
            if (!(o instanceof HandlerRef)) { return false; }

            HandlerRef handlerRef = (HandlerRef) o;

            if (!id.equals(handlerRef.id)) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }


    public class ChangeHandlerRegistration implements HandlerRegistration {

        private final HandlerRef handlerRef;
        private final Class<?> actionType;

        public ChangeHandlerRegistration(final Class<?> actionType, final HandlerRef handlerRef) {
            this.actionType = actionType;
            this.handlerRef = handlerRef;
        }

        @Override
        public void removeHandler() {
            handler.remove(actionType, handlerRef);
        }
    }


    private final static Class<Void> ANY_ACTION = Void.TYPE;
    private Multimap<Class<?>, HandlerRef> handler = LinkedListMultimap.create();

    @Override
    public HandlerRegistration addChangeHandler(final Handler handler) {
        HandlerRef handlerRef = new HandlerRef(handler);
        this.handler.put(ANY_ACTION, handlerRef);
        return new ChangeHandlerRegistration(ANY_ACTION, handlerRef);
    }

    @Override
    public HandlerRegistration addChangeHandler(final Class<?> actionType, final Handler handler) {
        HandlerRef handlerRef = new HandlerRef(handler);
        this.handler.put(actionType, handlerRef);
        return new ChangeHandlerRegistration(actionType, handlerRef);
    }

    public Iterable<Handler> getActionHandler(final Class<?> actionType) {
        Collection<HandlerRef> handlerRefs = handler.get(actionType); // returns an empty list if nothing was found
        return extractHandler(handlerRefs);
    }

    public Iterable<Handler> getHandler() {
        return extractHandler(handler.get(ANY_ACTION));
    }

    private List<Handler> extractHandler(final Collection<HandlerRef> handlerRefs) {
        List<Handler> handler = new ArrayList<>();
        for (HandlerRef id : handlerRefs) {
            handler.add(id.handler);
        }
        return handler;
    }

    protected void fireChange(Action action) {
        // TODO Exception handling / umbrella exception
        Class<? extends Action> actionType = action.getClass();
        Iterable<Handler> actionHandlers = getActionHandler(actionType);
        for (Handler actionHandler : actionHandlers) {
            actionHandler.onChanged(actionType);
        }
        Iterable<Handler> storeHandlers = getHandler();
        for (Handler storeHandler : storeHandlers) {
            storeHandler.onChanged(actionType);
        }
    }
}
