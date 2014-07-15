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
package org.jboss.gwt.circuit.meta;

import org.jboss.gwt.circuit.ChangeSupport;
import org.jboss.gwt.circuit.Dispatcher;

/**
 * Helper class to simplify sending ACKs and change events for a given store and action type.
 */
public class BackChannel {

    private final Class<?> store;
    private final Class<?> actionType;
    private final Dispatcher.Channel channel;
    private final ChangeSupport changeSupport;

    public BackChannel(final Class<?> store, final Class<?> actionType, final Dispatcher.Channel channel,
            final ChangeSupport changeSupport) {
        this.store = store;
        this.actionType = actionType;
        this.channel = channel;
        this.changeSupport = changeSupport;
    }

    public void ack() {channel.ack();}

    public void nack(final Throwable t) {channel.nack(t);}

    /**
     * Fires a change event for the store and action type.
     */
    public void fireChanged() {
        if (changeSupport != null) {
            changeSupport.fireChanged(store, actionType);
        } else {
            System.out.println("WARN: Calling fireChanged() for store " + store.getName() + " and action " + actionType
                    .getName() + ", but " + store.getName() + " does not extend " + ChangeSupport.class.getName());
        }
    }
}
