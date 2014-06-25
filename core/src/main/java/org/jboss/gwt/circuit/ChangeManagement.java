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
 * Central instance to handle {@link org.jboss.gwt.circuit.StoreChangedEvent}s.
 */
public class ChangeManagement {

    private final EventBus eventBus;

    /**
     * Create a new instance using {@link com.google.web.bindery.event.shared.SimpleEventBus}
     */
    public ChangeManagement() {
        this(new SimpleEventBus());
    }

    public ChangeManagement(final EventBus eventBus) {this.eventBus = eventBus;}

    public <S> HandlerRegistration addStoreChangedHandler(Class<S> store, StoreChangedHandler handler) {
        return eventBus.addHandlerToSource(StoreChangedEvent.TYPE, store, handler);
    }

    public <S> void fireChanged(Class<S> store) {
        eventBus.fireEventFromSource(new StoreChangedEvent(), store);
    }
}
