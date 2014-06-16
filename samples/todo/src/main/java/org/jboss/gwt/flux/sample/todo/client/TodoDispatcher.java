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
package org.jboss.gwt.flux.sample.todo.client;

import org.jboss.gwt.flux.Action;
import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.Store;
import org.jboss.gwt.flux.impl.NoopContext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TodoDispatcher implements Dispatcher {

    private final Map<Enum[],Store.Callback> callbacks;

    public TodoDispatcher() {
        this.callbacks = new HashMap<>();
    }

    @Override
    public <P> void register(Store.Callback callback) {

        callbacks.put(callback.getTypes(), callback);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P> void dispatch(final Action action) {


        Iterator<Enum[]> it = callbacks.keySet().iterator();
        boolean matched = false;

        while(it.hasNext())
        {
            Enum[] actionTypes = it.next();
            for(Enum e : actionTypes)
            {
                if(action.getType().equals(e))
                {
                    callbacks.get(actionTypes).execute(action, NoopContext.INSTANCE);
                    matched = true;
                    break;
                }
            }

            if(matched) break;
        }
    }
}
