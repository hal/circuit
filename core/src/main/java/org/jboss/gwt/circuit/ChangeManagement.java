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

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * Singleton which manages {@link org.jboss.gwt.circuit.ChangeManagement.Handler} and takes care of emitting {@link
 * org.jboss.gwt.circuit.ChangedEvent}s.
 */
@ApplicationScoped
public class ChangeManagement {

    public interface Handler {

        void onChange(ChangedEvent event);
    }


    private Multimap<StoreActionTuple, Handler> handlers = LinkedListMultimap.create();

    public void fireChanged(final Class<?> store) {
        Collection<Handler> storeHandlers = handlers.get(new StoreActionTuple(store));
        for (Handler handler : storeHandlers) { handler.onChange(new ChangedEvent(store)); }
    }

    public void fireChanged(final Class<?> store, final Class<?> action) {
        fireChanged(store);
        Collection<Handler> actionHandlers = handlers.get(new StoreActionTuple(store, action));
        for (Handler handler : actionHandlers) { handler.onChange(new ChangedEvent(store, action)); }
    }

    public void addHandler(final Class<?> store, final Handler handler) {
        handlers.put(new StoreActionTuple(store), handler);
    }

    public void addHandler(final Class<?> store, final Class<?> actionType, final Handler handler) {
        handlers.put(new StoreActionTuple(store, actionType), handler);
    }
}
