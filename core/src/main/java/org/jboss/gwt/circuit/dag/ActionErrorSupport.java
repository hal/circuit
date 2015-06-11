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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.jboss.gwt.circuit.Action;

import java.util.HashSet;
import java.util.Set;

/**
 * Convenience diagnostics implementation which can be used to handle nacked actions.
 *
 * @author Harald Pehl
 */
public class ActionErrorSupport implements DAGDispatcher.Diagnostics {

    public interface ErrorHandler {

        void onError(Action action, Throwable error);
    }


    private final Set<ErrorHandler> handlers;
    private final Multimap<Class<? extends Action>, ErrorHandler> handlersByActionType;

    public ActionErrorSupport() {
        handlers = new HashSet<>();
        handlersByActionType = HashMultimap.create();
    }


    @Override
    public void onDispatch(final Action action) {
        // nop
    }

    @Override
    public void onLock() {

    }

    @Override
    public void onExecute(final Class<?> store, final Action action) {

    }

    @Override
    public void onAck(final Class<?> store, final Action action) {

    }

    @Override
    public void onNack(final Class<?> store, final Action action, final String reason) {
        onNack(store, action, new RuntimeException(reason));
    }

    @Override
    public void onNack(final Class<?> store, final Action action, final Throwable throwable) {
        for (ErrorHandler handler : handlers) {
            handler.onError(action, throwable);
        }
        for (ErrorHandler handler : handlersByActionType.get(action.getClass())) {
            handler.onError(action, throwable);
        }
    }

    @Override
    public void onUnlock() {

    }

    public void onError(ErrorHandler errorHandler) {
        handlers.add(errorHandler);
    }

    public void onError(Class<? extends Action> actionType, ErrorHandler errorHandler) {
        handlersByActionType.put(actionType, errorHandler);
    }
}
