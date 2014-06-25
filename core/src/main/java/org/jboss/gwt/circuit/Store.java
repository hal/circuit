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
 * A store holds application state and manages segments of the domain model used by an application.
 * In to process actions store register callbacks with a {@link Dispatcher}.<p/>
 *
 * When actions are dispatched <u>stores run through a voting and a completion phase</u>.
 * Voting allows to reject actions or declare dependencies on other stores. The actual processing of the action
 * (and all corresponding state changes) is done in the completion phase.<p/>
 *
 * <u>It is mandatory to acknowledge each action through the {@link org.jboss.gwt.circuit.Dispatcher.Channel} after completion</u>.
 */
public interface Store {

    /**
     * Callbacks are registered with a {@link org.jboss.gwt.circuit.Dispatcher} to pass an {@link org.jboss.gwt.circuit.Action} to
     * a store.
     */
    public interface Callback {

        /**
         * Before actually processing an action, each store can vote on specific action types
         * and declare dependencies on other stores. Disagreement will prevent that the store will
         * be included in the completion phase.
         *
         */
        Agreement voteFor(Action action);

        /**
         * After a successful vote, the dispatcher hands the action to the store for completion.
         * It's the stores responsibility to acknowledge the action and notify it's change handlers.
         */
        void complete(Action action, Dispatcher.Channel channel);
    }
}
