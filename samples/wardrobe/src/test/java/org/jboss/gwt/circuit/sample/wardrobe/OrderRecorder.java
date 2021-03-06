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
package org.jboss.gwt.circuit.sample.wardrobe;

import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.dag.DAGDispatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderRecorder implements DAGDispatcher.Diagnostics {

    private final List<Class<?>> order;

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
    public void onExecute(final Class<?> s, final Action a) {
        // noop
    }

    @Override
    public void onAck(final Class<?> s, final Action a) {
        order.add(s);
    }

    @Override
    public void onNack(Class<?> store, Action action, String reason) {
        // noop
    }

    @Override
    public void onNack(final Class<?> s, final Action a, final Throwable t) {
        // noop
    }

    @Override
    public void onUnlock() {
        // noop
    }

    public List<Class<?>> getOrder() {
        return Collections.unmodifiableList(order);
    }
}
