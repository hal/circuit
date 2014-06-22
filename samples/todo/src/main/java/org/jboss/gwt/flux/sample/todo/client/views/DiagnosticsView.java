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
package org.jboss.gwt.flux.sample.todo.client.views;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.gwt.flux.Action;
import org.jboss.gwt.flux.Store;
import org.jboss.gwt.flux.dag.DAGDispatcher;

@ApplicationScoped
@SuppressWarnings("UnusedDeclaration")
@Templated("View.html#queueInfo")
public class DiagnosticsView extends Composite implements DAGDispatcher.DAGDiagnostics {

    @Inject @DataField InlineLabel info;
    private int dispatched;
    private int executed;
    private int acked;
    private int nacked;
    private boolean locked;

    public void refresh() {
        StringBuilder message = new StringBuilder().append("dispatched: ").append(dispatched).append(", executed: ")
                .append(executed).append(", acked: ").append(acked).append(", nacked: ").append(nacked);
        if (locked) {
            addStyleName("diagnostics-locked");
            removeStyleName("diagnostics");
            info.setText(message.toString());
        } else {
            removeStyleName("diagnostics-locked");
            addStyleName("diagnostics");
            info.setText(message.toString());
        }
    }

    @Override
    public void onDispatch(final Action a) {
        dispatched++;
        refresh();
    }

    @Override
    public void onLock() {
        locked = true;
        refresh();
    }

    @Override
    public void onExecute(final Class<? extends Store> s, final Action a) {
        executed++;
        refresh();
    }

    @Override
    public void onAck(final Class<? extends Store> s, final Action a) {
        acked++;
        refresh();
    }

    @Override
    public void onNack(final Class<? extends Store> s, final Action a, final Throwable t) {
        nacked++;
        refresh();
    }

    @Override
    public void onUnlock() {
        locked = false;
        refresh();
    }
}
