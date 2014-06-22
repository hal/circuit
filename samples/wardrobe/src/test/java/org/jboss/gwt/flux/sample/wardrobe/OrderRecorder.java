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
package org.jboss.gwt.flux.sample.wardrobe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.gwt.flux.Action;
import org.jboss.gwt.flux.Store;
import org.jboss.gwt.flux.dag.DAGDispatcher;

public class OrderRecorder implements DAGDispatcher.DAGDiagnostics {

    private final List<Class<? extends Store>> order;

    public OrderRecorder() {
        order = new ArrayList<>();
    }

    @Override
    public void onDispatch(final Action a) {
        // noop
    }

    @Override
    public void onLock() {
        // noop
    }

    @Override
    public void onExecute(final Class<?  extends Store> s, final Action a) {
        // noop
    }

    @Override
    public void onAck(final Class<?  extends Store> s, final Action a) {
        order.add(s);
    }

    @Override
    public void onNack(final Class<? extends Store> s, final Action a, final Throwable t) {
        // noop
    }

    @Override
    public void onUnlock() {
        // noop
    }

    public List<Class<? extends Store>> getOrder() {
        return Collections.unmodifiableList(order);
    }
}
