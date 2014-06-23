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

import static org.jboss.gwt.circuit.StoreChangedEvent.StoreChangedHandler;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * Abstract store using a {@link com.google.web.bindery.event.shared.SimpleEventBus} to register {@link
 * org.jboss.gwt.circuit.StoreChangedEvent.StoreChangedHandler}s and fire {@link org.jboss.gwt.circuit.StoreChangedEvent}s.
 */
public abstract class AbstractStore implements Store {

    private final EventBus eventBus;

    protected AbstractStore() {
        this.eventBus = new SimpleEventBus();
    }

    @Override
    @SuppressWarnings("unchecked")
    public HandlerRegistration addChangedHandler(final StoreChangedHandler handler) {
        return eventBus.addHandler(StoreChangedEvent.TYPE, handler);
    }

    protected void fireChanged() {
        eventBus.fireEvent(new StoreChangedEvent());
    }
}
