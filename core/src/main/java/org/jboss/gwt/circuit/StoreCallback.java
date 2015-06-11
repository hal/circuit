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
 * Callbacks are the contract between the {@link org.jboss.gwt.circuit.Dispatcher} and a store. This contract consists
 * of:
 * <ol>
 * <li>Vote for an action</li>
 * <li>Pass the action to the store</li>
 * <li>Notify handlers that the store has changed or that the action processing failed.</li>
 * </ol>
 */
public interface StoreCallback {

    /**
     * Before actually processing an action, each store can vote on specific action types
     * and declare dependencies on other stores. Disagreement will prevent that the store will
     * be included in the completion phase.
     */
    Agreement voteFor(Action action);

    /**
     * After a successful vote, the dispatcher hands the action to the store for completion.
     * It's the stores responsibility to ack / nack the action.
     */
    void complete(Action action, Dispatcher.Channel channel);

    /**
     * After the action was acknowledged by the store, a change event is sent to all
     * registered {@link PropagatesChange.Handler}s.
     */
    void signalChange(Action action);
}
