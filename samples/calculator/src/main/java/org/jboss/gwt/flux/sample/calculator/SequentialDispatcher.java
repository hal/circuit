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
package org.jboss.gwt.flux.sample.calculator;

import java.util.HashMap;
import java.util.Map;

import org.jboss.gwt.flux.Action;
import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.Store;
import org.jboss.gwt.flux.impl.NoopContext;

public class SequentialDispatcher implements Dispatcher {

    private final Map<Enum, Store.Callback> callbacks;

    public SequentialDispatcher() {callbacks = new HashMap<>();}

    @Override
    @SafeVarargs
    public final <A extends Action, T extends Enum<T>> void register(final Store.Callback<A> callback, final T type,
            final T... types) {
        callbacks.put(type, callback);
        if (types != null && types.length != 0) {
            for (T t : types) {
                callbacks.put(t, callback);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Action> void dispatch(final A action) {
        System.out.printf("~-~-~-~-~ Processing %s with payload '%s'\n", action.getClass().getSimpleName(),
                action.getPayload());

        Store.Callback callback = callbacks.get(action.getType());
        if (callback != null) {
            callback.execute(action, NoopContext.INSTANCE);
        } else {
            System.out.printf("No callback found for action type %s\n", action.getType());
        }

        System.out.printf("~-~-~-~-~ Finished\n\n");
    }
}
