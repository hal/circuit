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
package org.jboss.gwt.flux;

import static org.jboss.gwt.flux.StoreChangedEvent.StoreChangedHandler;

import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * The store holds state and uses the dispatcher to register callbacks. Views can register change handlers to be
 * informed upon changes of the internal state.
 * <p>
 * Dependencies between stores must result in a directed acyclic graph (DAG).
 *
 * @see http://en.wikipedia.org/wiki/Directed_acyclic_graph
 */
public interface Store {

    /**
     * A callback used by the {@link org.jboss.gwt.flux.Dispatcher} to pass an {@link org.jboss.gwt.flux.Action} to
     * the store. The passed {@code context} must be used by the store to signal processing of the callback.
     */
    public interface Callback {

        Agreement voteFor(Action action);

        void execute(Action action, Dispatcher.Channel channel);
    }

    /**
     * Registers a {@link org.jboss.gwt.flux.StoreChangedEvent.StoreChangedHandler}.
     *
     * @return Use this instance to remove the handler.
     */
    HandlerRegistration addChangedHandler(StoreChangedHandler handler);
}
