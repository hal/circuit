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

import java.util.LinkedList;
import java.util.List;

import org.jboss.gwt.flux.Action;
import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.Store;

public class SequentialDispatcher implements Dispatcher {

    private final List<Store.Callback> callbacks;

    public SequentialDispatcher() {callbacks = new LinkedList<>();}

    @Override
    public <P> void register(Store.Callback<P> callback, Class<?>... type) {
        callbacks.add(callback);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P> void dispatch(final Action<P> action) {
        System.out.printf("~-~-~-~-~ Processing %s with payload '%s'\n", action.getClass().getSimpleName(),
                action.getPayload());
        callbacks.forEach(callback -> callback.execute(action, new Context() {
            @Override
            public void yield() {
                // TODO
            }
        }));
        System.out.printf("~-~-~-~-~ Finished\n\n");
    }
}
