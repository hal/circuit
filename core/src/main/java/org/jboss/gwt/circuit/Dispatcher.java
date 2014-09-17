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

/**
 * The dispatcher manages store callbacks and dispatches actions.
 */
public interface Dispatcher {

    /**
     * Contract between the dispatcher and the store to manage ordered processing of callbacks.
     */
    public interface Channel {

        /**
         * Must be called by stores to signal the successful processing of a callback.
         */
        void ack();

        void ack(boolean emitChange);

        /**
         * Can be called by stores to signal that an action was not processed for some reason. Choose this
         * method if the reason is not related to an error, but was due to some other reason.
         */
        void nack(String reason);

        /**
         * Must be called by stores to signal an error during the processing of a callback.
         */
        void nack(Throwable t);
    }


    /**
     * Interface for accessing workflow data from a dispatcher.
     */
    interface Diagnostics {
    }

    /**
     * Registers a store callback.
     */
    <S> void register(Class<S> store, StoreCallback callback);

    /**
     * Dispatches the actions to all registered stores, which voted with an approved agreement. The stores are called
     * according to the dependencies specified in the agreement for the given action. No change events will be fired by
     * this method, that is this method is the same as calling <code>dispatch(action, false)</code>.
     *
     * @param action the action to dispatch
     */
    void dispatch(Action action);

    /**
     * Registers a diagnostics instance.
     */
    void addDiagnostics(Diagnostics diagnostics);

    /**
     * Removes a diagnostics instance.
     */
    void removeDiagnostics(Diagnostics diagnostics);
}
