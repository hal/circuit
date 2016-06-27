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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.UmbrellaException;

public abstract class ChangeSupport implements PropagatesChange, PropagatesError {

    private static class ChangeHandlerRef {

        private final String id;
        final ChangeHandler changeHandler;

        ChangeHandlerRef(final ChangeHandler handler) {
            this.id = UUID.uuid();
            this.changeHandler = handler;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) { return true; }
            if (!(o instanceof ChangeHandlerRef)) { return false; }

            ChangeHandlerRef handlerRef = (ChangeHandlerRef) o;
            return id.equals(handlerRef.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }


    public class ChangeHandlerRegistration implements HandlerRegistration {

        private final Class<? extends Action> actionType;
        private final Action action;
        private final ChangeHandlerRef handlerRef;

        public ChangeHandlerRegistration(final ChangeHandlerRef handlerRef) {
            this(null, null, handlerRef);
        }

        public ChangeHandlerRegistration(final Class<? extends Action> actionType, final ChangeHandlerRef handlerRef) {
            this(actionType, null, handlerRef);
        }

        public ChangeHandlerRegistration(final Action action, final ChangeHandlerRef handlerRef) {
            this(null, action, handlerRef);
        }

        private ChangeHandlerRegistration(final Class<? extends Action> actionType, final Action action,
                final ChangeHandlerRef handlerRef) {
            this.actionType = actionType;
            this.action = action;
            this.handlerRef = handlerRef;
        }

        @Override
        public void removeHandler() {
            //noinspection Duplicates
            if (action == null && actionType == null) {
                changeHandler.remove(handlerRef);
            } else if (actionType != null) {
                changeHandlerByType.remove(actionType, handlerRef);
            } else {
                changeHandlerByInstance.remove(action, handlerRef);
            }
        }
    }
    
    
    private static class ErrorHandlerRef {

        private final String id;
        final ErrorHandler errorHandler;

        ErrorHandlerRef(final ErrorHandler handler) {
            this.id = UUID.uuid();
            this.errorHandler = handler;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) { return true; }
            if (!(o instanceof ErrorHandlerRef)) { return false; }

            ErrorHandlerRef handlerRef = (ErrorHandlerRef) o;
            return id.equals(handlerRef.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }


    public class ErrorHandlerRegistration implements HandlerRegistration {

        private final Class<? extends Action> actionType;
        private final Action action;
        private final ErrorHandlerRef handlerRef;

        public ErrorHandlerRegistration(final ErrorHandlerRef handlerRef) {
            this(null, null, handlerRef);
        }

        public ErrorHandlerRegistration(final Class<? extends Action> actionType, final ErrorHandlerRef handlerRef) {
            this(actionType, null, handlerRef);
        }

        public ErrorHandlerRegistration(final Action action, final ErrorHandlerRef handlerRef) {
            this(null, action, handlerRef);
        }

        private ErrorHandlerRegistration(final Class<? extends Action> actionType, final Action action,
                final ErrorHandlerRef handlerRef) {
            this.actionType = actionType;
            this.action = action;
            this.handlerRef = handlerRef;
        }

        @Override
        public void removeHandler() {
            //noinspection Duplicates
            if (action == null && actionType == null) {
                changeHandler.remove(handlerRef);
            } else if (actionType != null) {
                changeHandlerByType.remove(actionType, handlerRef);
            } else {
                changeHandlerByInstance.remove(action, handlerRef);
            }
        }
    }

    
    private Set<ChangeHandlerRef> changeHandler = new LinkedHashSet<>();
    private Multimap<Class<? extends Action>, ChangeHandlerRef> changeHandlerByType = LinkedListMultimap.create();
    private Multimap<Action, ChangeHandlerRef> changeHandlerByInstance = LinkedListMultimap.create();

    private Set<ErrorHandlerRef> errorHandler = new LinkedHashSet<>();
    private Multimap<Class<? extends Action>, ErrorHandlerRef> errorHandlerByType = LinkedListMultimap.create();
    private Multimap<Action, ErrorHandlerRef> errorHandlerByInstance = LinkedListMultimap.create();


    // ------------------------------------------------------ change handler
    
    @Override
    public HandlerRegistration addChangeHandler(final ChangeHandler handler) {
        ChangeHandlerRef handlerRef = new ChangeHandlerRef(handler);
        this.changeHandler.add(handlerRef);
        return new ChangeHandlerRegistration(handlerRef);
    }

    @Override
    public HandlerRegistration addChangeHandler(final Class<? extends Action> actionType, final ChangeHandler handler) {
        ChangeHandlerRef handlerRef = new ChangeHandlerRef(handler);
        this.changeHandlerByType.put(actionType, handlerRef);
        return new ChangeHandlerRegistration(actionType, handlerRef);
    }

    @Override
    public HandlerRegistration addChangeHandler(Action action, ChangeHandler handler) {
        ChangeHandlerRef handlerRef = new ChangeHandlerRef(handler);
        this.changeHandlerByInstance.put(action, handlerRef);
        return new ChangeHandlerRegistration(action, handlerRef);
    }

    public Iterable<ChangeHandler> getChangeHandler() {
        return extractChangeHandler(changeHandler);
    }

    public Iterable<ChangeHandler> getChangeHandler(final Class<? extends Action> actionType) {
        Collection<ChangeHandlerRef> handlerRefs = changeHandlerByType
                .get(actionType); // returns an empty list if nothing was found
        return extractChangeHandler(handlerRefs);
    }

    public Iterable<ChangeHandler> getChangeHandler(final Action action) {
        Collection<ChangeHandlerRef> handlerRefs = changeHandlerByInstance
                .get(action); // returns an empty list if nothing was found
        return extractChangeHandler(handlerRefs);
    }

    private List<ChangeHandler> extractChangeHandler(final Collection<ChangeHandlerRef> handlerRefs) {
        List<ChangeHandler> handler = new ArrayList<>();
        for (ChangeHandlerRef id : handlerRefs) {
            handler.add(id.changeHandler);
        }
        return handler;
    }

    protected void fireChange(Action action) {
        List<ChangeHandlerRef> allHandler = new ArrayList<>();
        allHandler.addAll(changeHandler);
        allHandler.addAll(changeHandlerByType.get(action.getClass()));
        allHandler.addAll(changeHandlerByInstance.get(action));

        Set<Throwable> causes = null;
        for (ChangeHandlerRef handlerRef : allHandler) {
            try {
                handlerRef.changeHandler.onChange(action);
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

    // ------------------------------------------------------ error handler

    @Override
    public HandlerRegistration addErrorHandler(final ErrorHandler handler) {
        ErrorHandlerRef handlerRef = new ErrorHandlerRef(handler);
        this.errorHandler.add(handlerRef);
        return new ErrorHandlerRegistration(handlerRef);
    }

    @Override
    public HandlerRegistration addErrorHandler(final Class<? extends Action> actionType, final ErrorHandler handler) {
        ErrorHandlerRef handlerRef = new ErrorHandlerRef(handler);
        this.errorHandlerByType.put(actionType, handlerRef);
        return new ErrorHandlerRegistration(actionType, handlerRef);
    }

    @Override
    public HandlerRegistration addErrorHandler(Action action, ErrorHandler handler) {
        ErrorHandlerRef handlerRef = new ErrorHandlerRef(handler);
        this.errorHandlerByInstance.put(action, handlerRef);
        return new ErrorHandlerRegistration(action, handlerRef);
    }

    public Iterable<ErrorHandler> getErrorHandler() {
        return extractErrorHandler(errorHandler);
    }

    public Iterable<ErrorHandler> getErrorHandler(final Class<? extends Action> actionType) {
        Collection<ErrorHandlerRef> handlerRefs = errorHandlerByType
                .get(actionType); // returns an empty list if nothing was found
        return extractErrorHandler(handlerRefs);
    }

    public Iterable<ErrorHandler> getErrorHandler(final Action action) {
        Collection<ErrorHandlerRef> handlerRefs = errorHandlerByInstance
                .get(action); // returns an empty list if nothing was found
        return extractErrorHandler(handlerRefs);
    }

    private List<ErrorHandler> extractErrorHandler(final Collection<ErrorHandlerRef> handlerRefs) {
        List<ErrorHandler> handler = new ArrayList<>();
        for (ErrorHandlerRef id : handlerRefs) {
            handler.add(id.errorHandler);
        }
        return handler;
    }

    protected void fireError(Action action, Throwable throwable) {
        List<ErrorHandlerRef> allHandler = new ArrayList<>();
        allHandler.addAll(errorHandler);
        allHandler.addAll(errorHandlerByType.get(action.getClass()));
        allHandler.addAll(errorHandlerByInstance.get(action));

        Set<Throwable> causes = null;
        for (ErrorHandlerRef handlerRef : allHandler) {
            try {
                handlerRef.errorHandler.onError(action, throwable);
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
