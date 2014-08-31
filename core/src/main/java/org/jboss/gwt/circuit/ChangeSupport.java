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
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.UmbrellaException;

import java.util.*;

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

        private final Class<? extends Action> actionType;
        private final Action action;
        private final HandlerRef handlerRef;

        public ChangeHandlerRegistration(final HandlerRef handlerRef) {
            this(null, null, handlerRef);
        }

        public ChangeHandlerRegistration(final Class<? extends Action> actionType, final HandlerRef handlerRef) {
            this(actionType, null, handlerRef);
        }

        public ChangeHandlerRegistration(final Action action, final HandlerRef handlerRef) {
            this(null, action, handlerRef);
        }

        private ChangeHandlerRegistration(final Class<? extends Action> actionType, final Action action, final HandlerRef handlerRef) {
            this.actionType = actionType;
            this.action = action;
            this.handlerRef = handlerRef;
        }

        @Override
        public void removeHandler() {
            if (action == null && actionType == null) {
                handler.remove(handlerRef);
            } else if (actionType != null) {
                handlerByType.remove(actionType, handlerRef);
            } else {
                handlerByInstance.remove(action, handlerRef);
            }
        }
    }


    private Set<HandlerRef> handler = new LinkedHashSet<>();
    private Multimap<Class<? extends Action>, HandlerRef> handlerByType = LinkedListMultimap.create();
    private Multimap<Action, HandlerRef> handlerByInstance = LinkedListMultimap.create();

    @Override
    public HandlerRegistration addChangeHandler(final Handler handler) {
        HandlerRef handlerRef = new HandlerRef(handler);
        this.handler.add(handlerRef);
        return new ChangeHandlerRegistration(handlerRef);
    }

    @Override
    public HandlerRegistration addChangeHandler(final Class<? extends Action> actionType, final Handler handler) {
        HandlerRef handlerRef = new HandlerRef(handler);
        this.handlerByType.put(actionType, handlerRef);
        return new ChangeHandlerRegistration(actionType, handlerRef);
    }

    @Override
    public HandlerRegistration addChangeHandler(Action action, Handler handler) {
        HandlerRef handlerRef = new HandlerRef(handler);
        this.handlerByInstance.put(action, handlerRef);
        return new ChangeHandlerRegistration(action, handlerRef);
    }

    public Iterable<Handler> getActionHandler() {
        return extractHandler(handler);
    }

    public Iterable<Handler> getActionHandler(final Class<? extends Action> actionType) {
        Collection<HandlerRef> handlerRefs = handlerByType.get(actionType); // returns an empty list if nothing was found
        return extractHandler(handlerRefs);
    }

    public Iterable<Handler> getActionHandler(final Action action) {
        Collection<HandlerRef> handlerRefs = handlerByInstance.get(action); // returns an empty list if nothing was found
        return extractHandler(handlerRefs);
    }

    private List<Handler> extractHandler(final Collection<HandlerRef> handlerRefs) {
        List<Handler> handler = new ArrayList<>();
        for (HandlerRef id : handlerRefs) {
            handler.add(id.handler);
        }
        return handler;
    }

    protected void fireChange(Action action) {
        List<HandlerRef> allHandler = new ArrayList<>();
        allHandler.addAll(handler);
        allHandler.addAll(handlerByType.get(action.getClass()));
        allHandler.addAll(handlerByInstance.get(action));

        Set<Throwable> causes = null;
        for (HandlerRef handlerRef : allHandler) {
            try {
                handlerRef.handler.onChange(action);
            } catch (Throwable e) {
                if (causes == null) {
                    causes = new HashSet<>();
                }
                causes.add(e);
            }
        }
        if (causes != null) {
            throw new UmbrellaException(causes);
        }
    }
}
